package de.pgalise.simulation.controlCenter.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

/**
 * A StreamedFileCache manages files by their names and allows file content 
 * to be put and retrieved as streams
 * @author richter
 */
public class StreamedFileCache {

  private File fileDataDir;
  /**
   * the internal cache is used to be able to use EHCache's eviction 
   * implementation. In order to use the <tt>FileSizeSizeOf</tt> policy it 
   * manages values of type File.
   */
  private final Cache cache;

  private static class FileSizeSizeOf extends SizeOf {

    FileSizeSizeOf(SizeOfFilter fieldFilter,
      boolean caching) {
      super(fieldFilter,
        caching);
    }

    @Override
    public long sizeOf(Object obj) {
      if (!(obj instanceof File)) {
        throw new IllegalArgumentException(String.format(
          "object %s is not an instance of  %s",
          obj,
          File.class));
      }
      File file = (File) obj;
      return file.length();
    }
  }
  private final CacheEventListener CACHE_EVENT_LISTENER = new StreamCacheEventListener(fileDataDir);
  private final static CacheManager fileCacheManager = CacheManager.create();

  /**
   * 
   * @param fileDataDir has to be a directory in which the cache can store its 
   * data
   * @param cacheName 
   * @throws IllegalArgumentException if <tt>fileDataDir</tt> isn't a directory
   */
  public StreamedFileCache(File fileDataDir,
    String cacheName) {
    if(fileDataDir == null) {
      throw new IllegalArgumentException("fileDataDir mustn't be null");
    }
    if(!fileDataDir.isDirectory()) {
      throw new IllegalArgumentException("fileDataDir mustn't be a directory");
    }
    if(cacheName == null) {
      throw new IllegalArgumentException("cacheName mustn't be null");
    }
    this.cache = new Cache(new CacheConfiguration(cacheName,
      0 //maxElementsInMemory (0 == no limit)
    )
      .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(true)
      .diskExpiryThreadIntervalSeconds(120)
      .maxBytesLocalDisk(5,
        MemoryUnit.GIGABYTES)
      .persistence(new PersistenceConfiguration().strategy(
          PersistenceConfiguration.Strategy.LOCALTEMPSWAP)).cacheLoaderFactory(
        new CacheConfiguration.CacheLoaderFactoryConfiguration().className(
          StreamCacheLoaderFactory.class.getName()).properties(String.format("fileDataDir=%s", fileDataDir))));
    this.cache.getCacheEventNotificationService().registerListener(
      CACHE_EVENT_LISTENER);
    this.fileDataDir = fileDataDir;
    System.setProperty(String.format("net.sf.ehcache.sizeofengine.default.%s",
      cacheName),
      FileSizeSizeOf.class.getName());
    fileCacheManager.addCache(cache);
  }

  /**
   * Returns a stream of the managed value identified by its key. 
   * <code>null</code> can't be managed.
   * @param key
   * @return 
   */
  public InputStream get(String key) {
    if (key == null) {
      throw new IllegalArgumentException("key mustn't be null");
    }
    Element cacheElement = cache.get(key);
    if (cacheElement == null || cacheElement.getObjectValue() == null) {
      return null;
    }
    File cacheFile = generateCacheFile(key,
      true);
    FileInputStream fileInputStream;
    try {
      fileInputStream = new FileInputStream(cacheFile);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    return fileInputStream;
  }

  /**
   * Creates a file in the cache with the content of <tt>dataStream</tt>.
   * 
   * Cache entries can be deleted by passing <code>null</code> as 
   * <tt>dataStream</tt> with the key of the entry which ought to be deleted. 
   * This also deletes the file on disk.
   * @param key
   * @param dataStream 
   * @throws IllegalArgumentException if <tt>key</tt> is <code>null</code>
   */
  public void put(String key,
    InputStream dataStream) {
    if (key == null) {
      throw new IllegalArgumentException("key mustn't be null");
    }
    if(dataStream == null) {
      cache.put(new Element(key, null));
      return;
    }
    File file = generateCacheFile(key,
    true);
    cache.put(new Element(key,
      file));
    try {
      file.createNewFile(); //returns false if the file already exists
      Streams.copy(dataStream,
        new FileOutputStream(file),
        true);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<String> getKeys() {
    return cache.getKeys();
  }

  public int getSize() {
    return cache.getSize();
  }

  private File generateCacheFile(String fileName,
    boolean createLazily) {
    File cacheFile = new File(fileDataDir,
      fileName);
    if (!cacheFile.exists()) {
      try {
        if (!cacheFile.createNewFile()) {
          throw new RuntimeException(String.format(
            "cache file %s could not be created",
            cacheFile));
        }
      } catch (IOException ex) {
        throw new RuntimeException(String.format(
          "cache file %s could not be created",
          cacheFile),
          ex);
      }
    }
    return cacheFile;
  }

}
