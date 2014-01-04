/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import com.vividsolutions.jts.io.WKTReader;
import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.city.City;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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

	/**
	 * Creates a new instance of CityCtrl
	 */
	public CityCtrl() {
		mapModel = new DefaultMapModel();
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
			SimpleFeatureSource featureSource = MainCtrlUtils.POSTGIS_OSM_DATA_STORE.
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
				WKTReader parser = new WKTReader(); //new GeometryBuilder(GeoToolsBootstrapping.COORDINATE_REFERENCE_SYSTEM));
				com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) nextFeature.
					getAttribute("way");
				City autocompletionValue = new City(name,
					-1,
					-1,
					false,
					false,
					new BaseGeoInfo(polygon));
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
}
