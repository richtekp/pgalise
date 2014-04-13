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
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.pool.sizeof.SizeOf;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

/**
 * A helper/util class for static and "stateless" constants, services, etc.
 *
 * <h1>Caching</h1>
 * <p>
 * The application manages uploaded files with a LRU cache. The file cache uses
 * ehcache framework with a customized {@link SizeOf} implementation which
 * simulates the file in memory. Initial files (shipped with the application are
 * not cached, because they can't be evicted; their parsing results are)
 *
 * The parsing results (of both uploaded and initial files) are cached
 * seperately in another LRU cache manager. It manages the parsing results in a
 * {@link FutureTask} which make the results retrievable in the most flexible
 * way (one might wait for the completion, check whether the task is completed
 * and retrieve the result multiple times). Parsing results are stored in a
 * memory cache which overflows to disk.
 *
 * Parsing results of evicted files might still be in the cache if the uploaded
 * file has been evicted (this is not forcibly good, but so far it's implemented
 * like this).
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

  /**
   * names of files shipped with the application (as long as multiple files are
   * not supported, the set can only contain one name)
   */
  public final static Set<String> INITIAL_BUS_STOP_FILE_NAMES;
  /**
   * names of files shipped with the application (as long as multiple files are
   * not supported, the set can only contain one name)
   */
  public final static Set<String> INITIAL_OSM_FILE_NAMES;

  static {
    INITIAL_OSM_FILE_NAMES = Collections.
      unmodifiableSet(new HashSet<>(
          Arrays.asList("oldenburg_pg.osm",
            "dbis_institute_berlin.osm")));
    INITIAL_BUS_STOP_FILE_NAMES = Collections.
      unmodifiableSet(new HashSet<>(
          Arrays.
          asList("stops.gtfs")));
  }

  //////////////////////////////
  // Caching
  //////////////////////////////
  /**
   * The base directory for directories for file and parsing cache
   */
  public final static File PGALISE_DIR = new File(System.getProperty(
    "user.home"),
    ".pgalise");
  public final static File PARSING_CACHE_DIR = new File(PGALISE_DIR,
    "cache");
  public final static File OSM_FILE_CACHE_DIR = new File(PGALISE_DIR,
    "cache-data-osm");
  public final static File BUS_STOP_FILE_CACHE_DIR = new File(PGALISE_DIR,
    "cache-data-bus-stop");

  /*
   * creates directories if they don't exist
   */
  static {
    if (!PGALISE_DIR.exists()) {
      if (!PGALISE_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          PGALISE_DIR));

      }
    }
    if (!PARSING_CACHE_DIR.exists()) {
      if (!PARSING_CACHE_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          PARSING_CACHE_DIR));

      }
    }
    if (!OSM_FILE_CACHE_DIR.exists()) {
      if (!OSM_FILE_CACHE_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          OSM_FILE_CACHE_DIR));

      }
    }
    if (!BUS_STOP_FILE_CACHE_DIR.exists()) {
      if (!BUS_STOP_FILE_CACHE_DIR.mkdir()) {
        throw new RuntimeException(String.format(
          "directory %s could not be created",
          BUS_STOP_FILE_CACHE_DIR));

      }
    }
  }
  public final static StreamedFileCache OSM_FILE_CACHE;
  public final static StreamedFileCache BUS_STOP_FILE_CACHE;
  public final static Cache OSM_PARSING_CACHE;
  public final static Cache BUS_STOP_PARSING_CACHE;

  /*
   initializes and configures caches
   */
  static {
    //file caches
    String osmFileCacheName = "osmFileCache";
    String busStopFileCacheName = "busStopFileCache";

    OSM_FILE_CACHE = new StreamedFileCache(OSM_FILE_CACHE_DIR,
      osmFileCacheName);

    BUS_STOP_FILE_CACHE = new StreamedFileCache(BUS_STOP_FILE_CACHE_DIR,
      busStopFileCacheName);

    //parsing caches    
    String osmParsingCacheName = "osmParsingCache";
    String busStopParsingCacheName = "busStopParsingCache";

    CacheManager parsingCacheManager = CacheManager.create();
    Cache osmParsingCache = new Cache(
      new CacheConfiguration(osmParsingCacheName,
        0 //maxElementsInMemory (0 == no limit)
      )
      .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(true)
      .diskExpiryThreadIntervalSeconds(120)
      .maxBytesLocalDisk(1,
        MemoryUnit.GIGABYTES)
      .persistence(new PersistenceConfiguration().strategy(
          Strategy.LOCALTEMPSWAP)));
    parsingCacheManager.addCache(osmParsingCache);

    Cache busStopParsingCache = new Cache(
      new CacheConfiguration(busStopParsingCacheName,
        0 //maxElementsInMemory (0 == no limit)
      )
      .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
      .eternal(true)
      .diskExpiryThreadIntervalSeconds(120)
      .maxBytesLocalDisk(5,
        MemoryUnit.GIGABYTES)
      .persistence(new PersistenceConfiguration().strategy(
          Strategy.LOCALTEMPSWAP)));
    parsingCacheManager.addCache(busStopParsingCache);

    OSM_PARSING_CACHE = parsingCacheManager.getCache(osmParsingCacheName);
    BUS_STOP_PARSING_CACHE = parsingCacheManager.getCache(
      busStopParsingCacheName);
  }

  public final static Output OUTPUT = new DefaultTcpIpOutput("localhost",
    6666,
    TcpIpForceCloseStrategy.getInstance());

  public final static Executor EXECUTOR = new ScheduledThreadPoolExecutor(
    2 //one for osm and one for bus stops though there're no separate executors 
  //for the two 
  );

  //JCS's auxillary cache doesn't provide possibility to pass and get data as 
  //streams
//  public final static String CACHE_DB_FILE_NAME = "db_file";
//  public final static AuxiliaryCache OSM_FILE_CACHE;
//  public final static String OSM_FILE_CACHE_NAME = "file-cache-osm";
//  
//  static {
//    try {
//      Class.forName("org.hsqldb.jdbcDriver");
//      Connection conn = DriverManager.getConnection("jdbc:hsqldb:"
//        + CACHE_DB_FILE_NAME,    // filenames
//        "sa",                     // username
//        "");
//    } catch (ClassNotFoundException | SQLException ex) {
//      throw new ExceptionInInitializerError(ex);
//    }
//  }
//  
//  static  {
//    JDBCDiskCacheFactory a = new JDBCDiskCacheFactory();
//    AuxiliaryCacheAttributes auxiliaryCacheAttributes = new JDBCDiskCacheAttributes();
//    auxiliaryCacheAttributes.setCacheName("pgalise-cache-osm");
//    auxiliaryCacheAttributes.setName("pgalise-cache-osm");
//    ICompositeCacheManager compositeCacheManager = CompositeCacheManager.getUnconfiguredInstance();
//    compositeCacheManager.getConfigurationProperties().setProperty("userName", "sa");
//    compositeCacheManager.getConfigurationProperties().setProperty("password", "");
//    compositeCacheManager.getConfigurationProperties().setProperty("url", String.format("jdbc:hsqldb:%s", CACHE_DB_FILE_NAME));
//    compositeCacheManager.getConfigurationProperties().setProperty("driverClassName", "org.hsqldb.jdbcDriver");
//      
////jcs.auxiliary.MYSQL.attributes.userName=myUsername
////jcs.auxiliary.MYSQL.attributes.password=myPassword
////jcs.auxiliary.MYSQL.attributes.url=${MYSQL}
////jcs.auxiliary.MYSQL.attributes.driverClassName=org.gjt.mm.mysql.Driver
////jcs.auxiliary.MYSQL.attributes.tableName=JCS_STORE
////jcs.auxiliary.MYSQL.attributes.testBeforeInsert=false
////jcs.auxiliary.MYSQL.attributes.maxActive=100
////jcs.auxiliary.MYSQL.attributes.MaxPurgatorySize=10000000
////jcs.auxiliary.MYSQL.attributes.UseDiskShrinker=true
////jcs.auxiliary.MYSQL.attributes.ShrinkerIntervalSeconds=1800
////jcs.auxiliary.MYSQL.attributes.allowRemoveAll=false
////jcs.auxiliary.MYSQL.attributes.EventQueueType=POOLED
////jcs.auxiliary.MYSQL.attributes.EventQueuePoolName=disk_cache_event_queue
//    
//    ICacheEventLogger cacheEventLogger = new CacheEventLoggerDebugLogger();
//    IElementSerializer elementSerializer = new StandardSerializer();
//      
//    a.
//    
//    OSM_FILE_CACHE = a.createCache(
//      auxiliaryCacheAttributes, 
//      compositeCacheManager, 
//      cacheEventLogger, 
//      elementSerializer);
//  }
  private MainCtrlUtils() {
  }

}
