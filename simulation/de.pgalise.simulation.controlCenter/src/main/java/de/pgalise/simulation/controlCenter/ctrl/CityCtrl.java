/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.CityInfrastructureDataService;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.TrafficCity;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Size;
import net.sf.ehcache.Element;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
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
	private String chosenName;
	private int chosenPopulation = 1;
	private int chosenAltitude = 4;
	private boolean nearSea;
	private boolean nearRiver;
	private MapModel mapModel;
	private String mapCenter = "52.50304053365354, 13.617159360663575";
	private City chosenCity = null;
	private boolean useFileBoundaries = true;
	private List<JaxRSCoordinate> customFileBoundaries = new LinkedList<>();
	private String dbHost = "127.0.0.1";
	private int dbPort = 5204;
	private String dbDatabase = "postgis1";
	private String dbUser = "pgalise";
	private String dbPassword = "somepw";
	@Size(min = 1)
	private List<String> osmFileNames = new LinkedList<>(
		MainCtrlUtils.INITIAL_OSM_FILE_PATHS);
	@Size(min = 1)
	private List<String> busStopFileNames = new LinkedList<>(
		MainCtrlUtils.INITIAL_BUS_STOP_FILE_PATHS);
	@EJB
	private CityInfrastructureDataService cityInfrastructureManager;

	/**
	 * Creates a new instance of CityCtrl
	 */
	public CityCtrl() {
		mapModel = new DefaultMapModel();
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

	public List<String> retrieveOsmFileCacheKeys() {
		return MainCtrlUtils.OSM_FILE_CACHE.
			getKeys();
	}

	public List<String> retrieveBusStopFileCacheKeys() {
		return MainCtrlUtils.BUS_STOP_FILE_CACHE.getKeys();
	}

	public void setOsmFileNames(
		List<String> osmFileNames) {
		this.osmFileNames = osmFileNames;
	}

	public List<String> getOsmFileNames() {
		return osmFileNames;
	}

	public void setBusStopFileNames(
		List<String> busStopFileNames) {
		this.busStopFileNames = busStopFileNames;
	}

	public List<String> getBusStopFileNames() {
		return busStopFileNames;
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
	public List<City> cityNameAutocomplete(String input) {
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
			List<City> retValue = new LinkedList<>();
			SimpleFeatureIterator simpleFeatureIterator = features.features();
			while (simpleFeatureIterator.hasNext()) {
				SimpleFeature nextFeature = simpleFeatureIterator.next();
				String name = (String) nextFeature.getAttribute("name");
				com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) nextFeature.
					getAttribute("way");
				City autocompletionValue = new TrafficCity(name,
					-1,
					-1,
					false,
					false,
					new BaseGeoInfo(polygon),
					null);
				retValue.add(autocompletionValue);
			}
			simpleFeatureIterator.close();
			return retValue;
		} catch (IOException | CQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void onCityNameSelectionChange(SelectEvent event) {
		City selectedValue = (City) event.getObject();
		List<LatLng> citySelectionBounds = new LinkedList<>();
		Polygon citySelectionBoundsPolygon = new Polygon();
		for (com.vividsolutions.jts.geom.Coordinate coordinate : selectedValue.
			getPosition().getBoundaries().getCoordinates()) {
			citySelectionBoundsPolygon.getPaths().add(new LatLng(coordinate.y,
				coordinate.x));
		}
		mapCenter = selectedValue.getPosition().getCenterPoint().getY() + ", " + selectedValue.
			getPosition().getCenterPoint().getX();
		mapModel.getPolygons().clear();
		citySelectionBoundsPolygon.setStrokeColor("#FF9900");
		citySelectionBoundsPolygon.setFillColor("#FF9900");
		citySelectionBoundsPolygon.setStrokeOpacity(0.7);
		citySelectionBoundsPolygon.setFillOpacity(0.7);
		mapModel.addOverlay(citySelectionBoundsPolygon);
	}

	public DataStore retrieveDataStore() {
		try {
			Map<String, Object> connectionParameters = new HashMap<>();
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
			DataStore dataStore = DataStoreFinder.
				getDataStore(connectionParameters);
			if (dataStore == null) {
				throw new RuntimeException("Could not connect - check parameters");
			}
			return dataStore;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Adds file to available files, i.e. cache, not to selected files. Files are
	 * overwritten in the server side cache no matter what.
	 *
	 * @param event
	 */
	/*
	 @TODO: implement check for name with p:remoteCommand and onStart of p:fileUpload
	 */
	public void onGTFSFileUpload(FileUploadEvent event) {
		MainCtrlUtils.BUS_STOP_FILE_CACHE.put(new Element(event.getFile().
			getFileName(),
			event.getFile().getContents()));
	}

	/**
	 * Adds file to available files, i.e. cache, not to selected files. Files are
	 * overwritten in the server side cache no matter what.
	 *
	 * @param event
	 */
	public void onOSMFileUpload(FileUploadEvent event) {
		MainCtrlUtils.OSM_FILE_CACHE.put(new Element(event.getFile().
			getFileName(),
			event.getFile().getContents()));
	}

	/**
	 *
	 * @param trafficInfrastructureData
	 * @return
	 */
	/*
	 OSM shouldn't be parsed again
	 */
	public Envelope retrieveBoundaries(
		) {
		if (useFileBoundaries) {
			return cityInfrastructureManager.getBoundary();
		} else {
			return GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
				customFileBoundaries.toArray(new JaxRSCoordinate[customFileBoundaries.
					size()])).getEnvelopeInternal();
		}
	}

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
}
