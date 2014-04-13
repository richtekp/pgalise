/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.operation.BoundaryOp;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.service.FileBasedCityDataService;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import net.sf.ehcache.Element;
import org.apache.commons.dbcp.SQLNestedException;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.postgresql.util.PSQLException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class CityCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private final static Logger LOGGER = LoggerFactory.getLogger(CityCtrl.class);
  /*
   should be initialized with a default value in order to avoid nececity to 
   check if simulation is started immediately (without reviewing settings) (will 
   be passed to weather collector which needs city.name
   */
  private String chosenName = "Berlin";
  private int chosenPopulation = 1;
  private int chosenAltitude = 4;
  private boolean nearSea;
  private boolean nearRiver;
  private MapModel mapModel = new DefaultMapModel();
  private String mapCenter = "52.50304053365354, 13.617159360663575";
  private City chosenCity = null;
  private boolean useFileBoundaries = true;
  private List<BaseCoordinate> customFileBoundaries = new LinkedList<>();
  private String dbHost = "127.0.0.1";
  private int dbPort = 5204;
  private String dbDatabase = "postgis1";
  private String dbUser = "pgalise";
  private String dbPassword = "somepw";
  private List<String> osmFileNamesParsing = new LinkedList<>();
  private List<String> osmFileNamesParsed = new LinkedList<>();
  /**
   * selected OSM files (out of parsed OSM files)
   */
//  private Set<String> osmFileNames = new HashSet<>();
  private String osmFileName = null;
  @EJB
  private FileBasedCityDataService fileBasedCityDataService;
  @EJB
  private IdGenerator idGenerator;
  private TrafficCity city;
  /*
   the map model for the preview of boundaries of OSM file
   */
  private MapModel previewMapModel = new DefaultMapModel();
  /**
   * progress of the parsing process for the p:progressBar item on XHTML page
   * (in [0;100]), handled autonomously by {@link FutureTask} produced by 
   * {@link #generateOsmParseTask(java.lang.String) }
   */
  private Integer osmParsingProgress = 0;
  /*
   necessary because osmParsingProgress can't be final
   */
  private final Object osmParsingProgressLock = new Object();
  private String cityNameAutocompleteWarning = "";
  
  private DataStore currentDataStore;
  private Map<String,Object> currentDataStoreConnectionParameters;

  /**
   * Creates a new instance of CityCtrl
   */
  public CityCtrl() {
  }

  public CityCtrl(boolean nearSea,
    boolean nearRiver,
    FileBasedCityDataService cityInfrastructureManager,
    IdGenerator idGenerator) {
    this();
    this.nearSea = nearSea;
    this.nearRiver = nearRiver;
    this.fileBasedCityDataService = cityInfrastructureManager;
    this.idGenerator = idGenerator;
  }

  @PostConstruct
  public void initialize() {
    this.city = new TrafficCity(idGenerator.getNextId(),
      null);
    this.initializeOsmParsing();
    try {
      this.currentDataStore = retrieveDataStore();
    } catch (PSQLException ex) {
      //skip because connection parameter are just not right (will be fixed 
      //by user when input fields are available
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  @PreDestroy
  public void destroy() {
    if(currentDataStore != null) {
      currentDataStore.dispose();
    }
  }

  public List<String> retrieveOsmFileCacheKeys() {
    return MainCtrlUtils.OSM_FILE_CACHE.
      getKeys();
  }

  public List<String> retrieveBusStopFileCacheKeys() {
    return MainCtrlUtils.BUS_STOP_FILE_CACHE.getKeys();
  }

  //////
  // event handling 
  //////////
  /**
   * Adds file to available files, i.e. cache, not to selected files. Files are
   * overwritten in the server side cache no matter what.
   *
   * @param event
   */
  public void onOSMFileUpload(FileUploadEvent event) {
    UploadedFile uploadedFile = event.getFile();
    try {
      MainCtrlUtils.OSM_FILE_CACHE.put(event.getFile().
        getFileName(),
        uploadedFile.getInputstream());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    FutureTask<TrafficCity> osmParseTask = generateOsmParseTask(
      uploadedFile.getFileName());
    MainCtrlUtils.EXECUTOR.execute(osmParseTask);
    MainCtrlUtils.OSM_PARSING_CACHE.put(new Element(uploadedFile.getFileName(),
      osmParseTask));
  }

  /**
   * used to handle changes in OSM file preview map when file choice changes
   * @param e
   * @throws InterruptedException
   * @throws ExecutionException 
   */
  public void onOsmFileSelectionChanged(ValueChangeEvent e) throws InterruptedException, ExecutionException {
    String selectedOsmFileName = (String) e.getNewValue();
    Element cachedParsing = MainCtrlUtils.OSM_PARSING_CACHE.get(
      selectedOsmFileName);
    FutureTask<TrafficCity> futureTask = (FutureTask<TrafficCity>) cachedParsing.
      getObjectValue();
    TrafficCity trafficCity = futureTask.get();      
    List<LatLng> cityInfrastructureDataBounds = new LinkedList<>();
//    List<Coordinate> boundaryMultiPointCoords = new LinkedList<>(); //there's 
    //no implementation of CoordinateSequence which is a linked list
    for (Coordinate n : trafficCity.getGeoInfo().retrieveBoundary().getCoordinates()) {
      cityInfrastructureDataBounds.add(new LatLng(n.x,
        n.y));
    }
    Polygon polygon = new Polygon(cityInfrastructureDataBounds);
    polygon.setStrokeColor("#FF9900");  
    polygon.setFillColor("#FF9900");  
    polygon.setStrokeOpacity(0.7);  
    polygon.setFillOpacity(0.7);  
    previewMapModel
      .addOverlay(polygon);
  }

  /**
   * iterates over all entries of {@link MainCtrlUtils#OSM_PARSING_CACHE} and
   * returns <code>true</code> if at least one is completed
   *
   * @return
   */
  public boolean isOneMapParsed() {
    return osmFileNamesParsed.size() > 0;
  }
  
  public void onCityNameValueChange(ValueChangeEvent valueChangeEvent) {
    TrafficCity newCity = (TrafficCity) valueChangeEvent.getNewValue();
    this.city.setName(newCity.getName());
    this.city.setGeoInfo(newCity.getGeoInfo());
  }

  //////
  // PostGIS auto completion
  //////////
  /**
   * generates a list of autocompletion propositions for the city name input. In
   * order to provide information about ambiguous city names, the coordinates of
   * a reference point are appended in brackets. The coordinates can be used to
   * listen to input changes for the city name, e.g. for moving the bounds of a
   * preview map of city boundaries.
   *
   * @param input
   * @return a list of autocompletion propositions from the database 
   */
  /*
  PSQLException is not thrown in retrieveDataStore if an invalid and/or 
  inexistant database name is in dbName property -> find out where 
  (PSQLException is catched anyway)
  */
  public List<TrafficCity> cityNameAutocomplete(String input) throws CQLException {
    try {
      String typeName = "planet_osm_polygon";
      SimpleFeatureSource featureSource = retrieveDataStore().
        getFeatureSource(typeName);
      FeatureType schema = featureSource.getSchema();
      String geometryPropertyName = schema.getGeometryDescriptor().
        getLocalName();
      FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
      Filter cityFilter
        = CQL.toFilter(
          "boundary = 'administrative' AND name LIKE '%" + input + "%'");
      Filter filter
        = cityFilter;
      Query query = new Query(typeName,
        filter);
      query.setMaxFeatures(20);
      SimpleFeatureCollection features = featureSource.getFeatures(query);
      List<TrafficCity> retValue = new LinkedList<>();
      try (SimpleFeatureIterator simpleFeatureIterator = features.features()) {
        while (simpleFeatureIterator.hasNext()) {
          SimpleFeature nextFeature = simpleFeatureIterator.next();
          String name = (String) nextFeature.getAttribute("name");
          com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) nextFeature.
            getAttribute("way");
          TrafficCity autocompletionValue = new TrafficCity(idGenerator.getNextId(),
            name,
            -1,
            -1,
            false,
            false,
            new BaseBoundary(idGenerator.getNextId(),
              polygon),
            null);
          retValue.add(autocompletionValue);
        }
      }
      cityNameAutocompleteWarning = "";
      return retValue;
    }catch(PSQLException | RuntimeException ex) {
      cityNameAutocompleteWarning = String.format("Postgis database returns errornous message or warning %s",
        ex.getMessage());
      return null;
    } catch (IOException ex) {
      throw new RuntimeException (ex);
    }
  }

  /**
   * used in PostGIS city name autocomplete input to change preview overlay on 
   * preview map. Changes to city name and properties occur in {@link #onCityNameValueChange(javax.faces.event.ValueChangeEvent) }.
   * @param event 
   */
  public void onCityNameSelectionChange(SelectEvent event) {
    City selectedCity = (City) event.getObject();
    List<LatLng> citySelectionBounds = new LinkedList<>();
    Polygon citySelectionBoundsPolygon = new Polygon();
    for (com.vividsolutions.jts.geom.Coordinate coordinate : selectedCity.
      getGeoInfo().retrieveBoundary().getCoordinates()) {
      citySelectionBoundsPolygon.getPaths().add(new LatLng(coordinate.y,
        coordinate.x));
    }
    mapCenter = selectedCity.getGeoInfo().retrieveCenterPoint().getY() + ", " + selectedCity.
      getGeoInfo().retrieveCenterPoint().getX();
    mapModel.getPolygons().clear();
    citySelectionBoundsPolygon.setStrokeColor("#FF9900");
    citySelectionBoundsPolygon.setFillColor("#FF9900");
    citySelectionBoundsPolygon.setStrokeOpacity(0.7);
    citySelectionBoundsPolygon.setFillOpacity(0.7);
    mapModel.addOverlay(citySelectionBoundsPolygon);
  }

  /**
   * creates a {@link DataStore} if currentDataStore is <code>null</code> or 
   * if one connection parameter has changed (to test, the connection parameter 
   * map of the last connection which has been used for currentDataStore is 
   * compared to a map with current parameters.
   * @return 
   */
  /*
  if an invalid database name is passed via database property no excpetion is 
  thrown in DataStoreFinder.getDataStore
  */
  public DataStore retrieveDataStore() throws PSQLException,IOException {
    Map<String, Object> connectionParameters = new HashMap<>(); //needs to 
      //be created every time because currentDataStore is either null (then 
      //currentDataStoreConnectionParameters is also null) or current 
      //parameters need to be compared 
      connectionParameters.put("port",
        dbPort);
      connectionParameters.put("Connection timeout",
        null);
      connectionParameters.put("passwd",
        dbPassword);
      connectionParameters.put("dbtype",
        "postgis");
      connectionParameters.put("host",
        dbHost);
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
        dbDatabase);
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
        dbUser);
      connectionParameters.put("min connections",
        null);
    if(currentDataStore == null) {
      currentDataStore = DataStoreFinder.getDataStore(connectionParameters);
      currentDataStoreConnectionParameters = connectionParameters;
    }else if(!currentDataStoreConnectionParameters.equals(connectionParameters)){
      currentDataStore = DataStoreFinder.getDataStore(connectionParameters);
      currentDataStoreConnectionParameters = connectionParameters;
    }
    return currentDataStore;
  }

  /**
   *
   * @return
   */
  /*
   OSM shouldn't be parsed again
   */
  public Envelope retrieveEnvelope() {
    if (useFileBoundaries) {
      return fileBasedCityDataService.createCity().getGeoInfo().retrieveBoundary().getEnvelopeInternal();
    } else {
      return GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
        customFileBoundaries.toArray(new BaseCoordinate[customFileBoundaries.
          size()])).getEnvelopeInternal();
    }
  }

  //////////////////////////////////////////////////
  // Parsing
  //////////////////////////////////////////////////
  /**
   * Scans classpath for resource names specified in
   * {@link MainCtrlUtils#INITIAL_OSM_FILE_NAMES}
   *
   * lazily initializes the map parsing when the application starts (can be
   * invoked multiple times)
   */
  private void initializeOsmParsing() {
    final TrafficCity city = new TrafficCity(idGenerator.getNextId(),
      getChosenName(),
      getChosenPopulation(),
      getChosenAltitude(),
      getNearRiver(),
      getNearSea(),
      null, //geoInformation (set later)
      null, //cityInfrastructureData (set later)
      null //need to set referencePoint explicitly in order to avoid NullPointerException
    );
    for (String initialOsmFileName : MainCtrlUtils.INITIAL_OSM_FILE_NAMES) {
      Element initialOsmFileElement = MainCtrlUtils.OSM_PARSING_CACHE.get(
        initialOsmFileName);
      if (initialOsmFileElement == null) {
        MainCtrlUtils.OSM_FILE_CACHE.put(
          initialOsmFileName,
          Thread.currentThread().getContextClassLoader().getResourceAsStream(
            initialOsmFileName)
        ); //should occur before generateOsmParseTask
        FutureTask<TrafficCity> osmParseTask = generateOsmParseTask(
          initialOsmFileName);
        MainCtrlUtils.EXECUTOR.execute(osmParseTask);
        initialOsmFileElement = new Element(initialOsmFileName,
          osmParseTask);
        MainCtrlUtils.OSM_PARSING_CACHE.put(initialOsmFileElement);
//          ICacheElement osmFileCacheElement = MainCtrlUtils.OSM_FILE_CACHE.get(initialOsmFileName);
//          if(osmFileCacheElement == null) {
//            osmFileCacheElement = new CacheElement(MainCtrlUtils.OSM_FILE_CACHE_NAME, initialOsmFileName, new )
//          }
//          
//          
//          IElementAttributes elementAttributes = new ElementAttributes();
//          elementAttributes.
//          osmFileCacheElement.setElementAttributes(null);

        this.osmFileNamesParsing.add(initialOsmFileName);
        LOGGER.info("stared OSM parse thread");
      } else {
        if (((FutureTask<?>) initialOsmFileElement.getObjectValue()).isDone()) {
          LOGGER.debug("OSM parse task for %s already finished");
        } else {
          LOGGER.debug("OSM parse task for %s already running");
        }
      }
    }

    LOGGER.info("started weather collector thread");
  }

  /**
   * generates a {@link FutureTask} which can be submitted to an
   * {@link Executor}. Handles the move from {@link #osmFileNamesParsing} to
   * {@link #osmFileNamesParsed} internally.
   *
   * @param osmFileName
   * @return
   */
  @SuppressWarnings("FinalMethod")
  public final FutureTask<TrafficCity> generateOsmParseTask(
    final String osmFileName) {
    FutureTask<TrafficCity> retValue = new FutureTask<>(
      new Callable<TrafficCity>() {
        @Override
        public TrafficCity call() throws Exception {
          InputStream osmFileBytes = MainCtrlUtils.OSM_FILE_CACHE.get(
            osmFileName);
          BuildingEnergyProfileStrategy buildingEnergyProfileStrategy = new DefaultBuildingEnergyProfileStrategy();

          fileBasedCityDataService.parseStream(
            osmFileBytes
          );
          TrafficCity cityInfrastructureData = fileBasedCityDataService.
          createCity();
          osmFileNamesParsing.remove(osmFileName);
          osmFileNamesParsed.add(osmFileName);
          synchronized (osmParsingProgressLock) {
            osmParsingProgress = (osmFileNamesParsed.size() / (osmFileNamesParsed.
            size() + osmFileNamesParsing.
            size())) * 100;
          }
          return cityInfrastructureData;
        }
      });
    return retValue;
  }

  //////
  // getter and setter
  //////////
  /**
   * @return the dbHost
   */
  public String getDbHost() {
    return dbHost;
  }

  /**
   * @param dbHost the dbHost to set
   */
  public void setDbHost(String dbHost) {
    this.dbHost = dbHost;
  }

  /**
   * @return the dbPort
   */
  public int getDbPort() {
    return dbPort;
  }

  /**
   * @param dbPort the dbPort to set
   */
  public void setDbPort(int dbPort) {
    this.dbPort = dbPort;
  }

  /**
   * @return the dbUser
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * @param dbUser the dbUser to set
   */
  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
  }

  /**
   * @return the dbPassword
   */
  public String getDbPassword() {
    return dbPassword;
  }

  /**
   * @param dbPassword the dbPassword to set
   */
  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public void setOsmFileNamesParsing(
    List<String> osmFileNames) {
    this.osmFileNamesParsing = osmFileNames;
  }

  public List<String> getOsmFileNamesParsing() {
    return osmFileNamesParsing;
  }

  public List<String> getOsmFileNamesParsed() {
    return osmFileNamesParsed;
  }

  public void setOsmFileNamesParsed(List<String> osmFileNamesParsed) {
    this.osmFileNamesParsed = osmFileNamesParsed;
  }

  public City getChosenCity() {
    return chosenCity;
  }

  public void setChosenCity(City chosenCity) {
    this.chosenCity = chosenCity;
  }

  public MapModel getMapModel() {
    return mapModel;
  }

  public void setMapModel(MapModel mapModel) {
    this.mapModel = mapModel;
  }

  public void setChosenName(String chosenName) {
    this.chosenName = chosenName;
  }

  public String getChosenName() {
    return chosenName;
  }

  public void setChosenPopulation(int chosenPopulation) {
    this.chosenPopulation = chosenPopulation;
  }

  public int getChosenPopulation() {
    return chosenPopulation;
  }

  public void setChosenAltitude(int chosenAltitude) {
    this.chosenAltitude = chosenAltitude;
  }

  public int getChosenAltitude() {
    return chosenAltitude;
  }

  public void setNearSea(boolean nearSea) {
    this.nearSea = nearSea;
  }

  public void setNearRiver(boolean nearRiver) {
    this.nearRiver = nearRiver;
  }

  public boolean getNearSea() {
    return nearSea;
  }

  public boolean getNearRiver() {
    return nearRiver;
  }

  public void setMapCenter(String mapCenter) {
    this.mapCenter = mapCenter;
  }

  public String getMapCenter() {
    return mapCenter;
  }

  public void setPreviewMapModel(MapModel previewMapModel) {
    this.previewMapModel = previewMapModel;
  }

  public MapModel getPreviewMapModel() {
    return previewMapModel;
  }

  public TrafficCity getCity() {
    return city;
  }

  public void setDbDatabase(String dbDatabase) {
    this.dbDatabase = dbDatabase;
  }

  public String getDbDatabase() {
    return dbDatabase;
  }

  public void setUseFileBoundaries(boolean useFileBoundaries) {
    this.useFileBoundaries = useFileBoundaries;
  }

  public boolean getUseFileBoundaries() {
    return useFileBoundaries;
  }

  public void setOsmFileName(String osmFileName) {
    this.osmFileName = osmFileName;
  }

  public String getOsmFileName() {
    return osmFileName;
  }

//  public void setOsmFileNames(Set<String> osmFileNames) {
//    this.osmFileNames = osmFileNames;
//  }
//
//  public Set<String> getOsmFileNames() {
//    return osmFileNames;
//  }
  public Integer getOsmParsingProgress() {
    return osmParsingProgress;
  }

  protected void setOsmParsingProgress(Integer osmParsingProgress) {
    this.osmParsingProgress = osmParsingProgress;
  }

  public void setCityNameAutocompleteWarning(String cityNameAutocompleteWarning) {
    this.cityNameAutocompleteWarning = cityNameAutocompleteWarning;
  }

  public String getCityNameAutocompleteWarning() {
    return cityNameAutocompleteWarning;
  }

}
