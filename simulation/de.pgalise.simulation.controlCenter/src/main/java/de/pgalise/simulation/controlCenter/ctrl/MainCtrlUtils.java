/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.DefaultTcpIpOutput;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpForceCloseStrategy;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

/**
 *
 * @author richter
 */
public class MainCtrlUtils {

  public final static DataStore POSTGIS_OSM_DATA_STORE;

  static {
    try {
      Map<String, Object> connectionParameters = new HashMap<>();
      connectionParameters.put("port",
        5204);
      connectionParameters.put("Connection timeout",
        null);
      connectionParameters.put("passwd",
        "somepw");
      connectionParameters.put("dbtype",
        "postgis");
      connectionParameters.put("host",
        "localhost");
      connectionParameters.put("Session close-up SQL",
        null);
      connectionParameters.put("encode functions",
        null);
      connectionParameters.put("validate connections",
        null);
      connectionParameters.put("max connections",
        null);
      connectionParameters.put("Primary key metadata table",
        null);
      connectionParameters.put("database",
        "postgis1");
      connectionParameters.put("namespace",
        null);
      connectionParameters.put("schema",
        null);
      connectionParameters.put("Loose bbox",
        null);
      connectionParameters.put("Expose primary keys",
        null);
      connectionParameters.put("Session startup SQL",
        null);
      connectionParameters.put("fetch size",
        null);
      connectionParameters.put("Max open prepared statements",
        null);
      connectionParameters.put("preparedStatements",
        null);
      connectionParameters.put("Estimated extends",
        null);
      connectionParameters.put("user",
        "pgalise");
      connectionParameters.put("min connections",
        null);
      POSTGIS_OSM_DATA_STORE = DataStoreFinder.
        getDataStore(connectionParameters);
      if (POSTGIS_OSM_DATA_STORE == null) {
        throw new RuntimeException("Could not connect - check parameters");
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public final static File PGALISE_DIR = new File(System.getProperty(
    "user.home"),
    ".pgalise");
  public final static File CACHE_DIR = new File(PGALISE_DIR,
    "cache");
  public final static File CACHE_DATA_DIR = new File(PGALISE_DIR,
    "cache-data");

  static {
    if (!PGALISE_DIR.exists()) {
      if (!PGALISE_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          PGALISE_DIR));

      }
    }
    if (!CACHE_DIR.exists()) {
      if (!CACHE_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          CACHE_DIR));

      }
    }
    if (!CACHE_DATA_DIR.exists()) {
      if (!CACHE_DATA_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          CACHE_DATA_DIR));

      }
    }
  }

  public final static Set<String> INITIAL_BUS_STOP_FILE_PATHS;
  public final static Set<String> INITIAL_OSM_FILE_PATHS;

  static {
    INITIAL_OSM_FILE_PATHS = Collections.
      unmodifiableSet(new HashSet<>(
          Arrays.asList("oldenburg_pg.osm")));
    INITIAL_BUS_STOP_FILE_PATHS = Collections.
      unmodifiableSet(new HashSet<>(
          Arrays.
          asList("stops.gtfs")));
  }

  public final static Cache OSM_FILE_CACHE;
  public final static Cache BUS_STOP_FILE_CACHE;

  private static class FileSizeSizeOf extends SizeOf {

    FileSizeSizeOf(SizeOfFilter fieldFilter,
      boolean caching) {
      super(fieldFilter,
        caching);
    }

    @Override
    public long sizeOf(Object obj) {
      if (!(obj instanceof File)) {
        throw new IllegalArgumentException(String.format("object is not a %s",
          File.class));
      }
      File file = (File) obj;
      return file.length();
    }
  }
  private final static CacheEventListener CACHE_EVENT_LISTENER = new StreamCacheEventListener();

  static {
    String osmFileCacheName = "osmFileCache";
    String busStopFileCacheName = "busStopFileCache";

    System.setProperty(String.format("net.sf.ehcache.sizeofengine.default.%s",
      busStopFileCacheName),
      FileSizeSizeOf.class.getName());
    System.setProperty(String.format("net.sf.ehcache.sizeofengine.default.%s",
      osmFileCacheName),
      FileSizeSizeOf.class.getName());
    CacheManager singletonManager = CacheManager.create();
    Cache osmFileCache = new Cache(
      new CacheConfiguration(osmFileCacheName,
        0 //maxElementsInMemory
      )
      .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(true)
      .diskExpiryThreadIntervalSeconds(120)
      .maxBytesLocalDisk(5,
        MemoryUnit.GIGABYTES)
      .persistence(new PersistenceConfiguration().strategy(
          Strategy.LOCALTEMPSWAP)).cacheLoaderFactory(
        new CacheConfiguration.CacheLoaderFactoryConfiguration().className(
          StreamCacheLoaderFactory.class.getName())));
    osmFileCache.getCacheEventNotificationService().registerListener(
      CACHE_EVENT_LISTENER);
    singletonManager.addCache(osmFileCache);

    Cache busStopFileCache = new Cache(
      new CacheConfiguration(busStopFileCacheName,
        0 //maxElementsInMemory
      )
      .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(true)
      .diskExpiryThreadIntervalSeconds(120)
      .maxBytesLocalDisk(5,
        MemoryUnit.GIGABYTES)
      .persistence(new PersistenceConfiguration().strategy(
          Strategy.LOCALTEMPSWAP)).cacheLoaderFactory(
        new CacheConfiguration.CacheLoaderFactoryConfiguration().className(
          StreamCacheLoaderFactory.class.getName())));
    busStopFileCache.getCacheEventNotificationService().
      registerListener(CACHE_EVENT_LISTENER);
    singletonManager.addCache(busStopFileCache);

    OSM_FILE_CACHE = singletonManager.
      getCache(osmFileCacheName);
    BUS_STOP_FILE_CACHE = singletonManager.getCache(busStopFileCacheName);

    //copy initially cached files to cache directory
    for (String initialOsmFile : INITIAL_OSM_FILE_PATHS) {
      //register file element in cache
      OSM_FILE_CACHE.put(new Element(new File(initialOsmFile).getName(),
        Thread.currentThread().getContextClassLoader().getResourceAsStream(
          initialOsmFile)));

      //copy actual file in managed file directory
//			File targetFile = new File(CACHE_DIR,
//				new File(initialOsmFile).getName());
//			try {
//				if (!targetFile.exists()) {
//					if (!targetFile.createNewFile()) {
//						throw new RuntimeException(String.format(
//							"file %s could not be created",
//							targetFile.getAbsoluteFile()));
//					}
//				}
//				FileUtils.copyFile(new File(initialOsmFile),
//					targetFile);
//			} catch (IOException ex) {
//				throw new RuntimeException(ex);
//			}
    }

    for (String initialBusStopFile : INITIAL_BUS_STOP_FILE_PATHS) {
      //register file element in cache
      BUS_STOP_FILE_CACHE.put(new Element(new File(initialBusStopFile).
        getName(),
        Thread.currentThread().getContextClassLoader().getResourceAsStream(
          initialBusStopFile)));

//			//copy actual file in managed file directory
//			File targetFile = new File(CACHE_DIR,
//				new File(initialBusStopFile).getName());
//			try {
//				if (!targetFile.exists()) {
//					if (!targetFile.createNewFile()) {
//						throw new RuntimeException(String.format(
//							"file %s could not be created",
//							targetFile.getAbsoluteFile()));
//					}
//				}
//				FileUtils.copyFile(new File(initialBusStopFile),
//					targetFile);
//			} catch (IOException ex) {
//				throw new RuntimeException(ex);
//			}
    }
  }

  public final static Output OUTPUT = new DefaultTcpIpOutput("localhost",
    6666,
    TcpIpForceCloseStrategy.getInstance());

  private MainCtrlUtils() {
  }

}
