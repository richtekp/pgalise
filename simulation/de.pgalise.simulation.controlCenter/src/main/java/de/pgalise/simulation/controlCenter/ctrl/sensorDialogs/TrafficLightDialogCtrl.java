/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl.sensorDialogs;

import de.pgalise.simulation.controlCenter.ctrl.InitDialogCtrl;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLight;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightSensor;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.swing.JOptionPane;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.wizard.JWizard;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.BoundingBox3D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class TrafficLightDialogCtrl extends BaseSensorDialogCtrl {

	private static final long serialVersionUID = 1L;
	@EJB
	private TrafficGraph trafficGraph;

	/**
	 * Creates a new instance of TrafficLightDialogCtrl
	 */
	public TrafficLightDialogCtrl() {
	}

	public void onStateChange(StateChangeEvent event) {
		LatLngBounds bounds = event.getBounds();
		List<SimpleFeature> trafficLightFeatures = getBoundingBoxFeatures(
			bounds.getNorthEast().getLat(),
			bounds.getSouthWest().getLat(),
			bounds.getNorthEast().getLng(),
			bounds.getNorthEast().getLng());
		for(SimpleFeature feature : trafficLightFeatures) {
//			Marker marker = new Marker(new LatLng(feature.getAttribute("way"), ), title);  
//        emptyModel.addOverlay(marker);  
//			
//			getMapModel().
		}
	}

	public void saveSensor() throws SensorException, InterruptedException, ExecutionException {
		TrafficNode node = trafficGraph.getNodeClosestTo(getCoordinate());
		if(node.getTrafficRule() == null || !(node.getTrafficRule() instanceof TrafficLight)) {
			throw new IllegalStateException("Wrong parsing of traffic node (no traffic light specified");
		}
		getSensorManagerController().createSensor(new TrafficLightSensor(
			getIdGenerator().getNextId(),
			getOutput(),
			node, (TrafficLight) node.getTrafficRule()));
	}

	private static List<SimpleFeature> getBoundingBoxFeatures(double x1, double x2, double y1,double y2) {
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
		try {
			DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);
			if (dataStore == null) {
				throw new RuntimeException("Could not connect - check parameters");
			}

			String typeName = "planet_osm_point";
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			FeatureType schema = featureSource.getSchema();

			String geometryPropertyName = schema.getGeometryDescriptor().
				getLocalName();
			CoordinateReferenceSystem targetCRS = schema.getGeometryDescriptor()
				.getCoordinateReferenceSystem();

			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

			BoundingBox3D boundingBox = new ReferencedEnvelope3D(
				x1,
				x2,
				y1,
				y2,
				-100,
				10000,
				targetCRS);
			Filter wayFilter = ff.
				bbox("way",
				boundingBox);
			Filter trafficLightFilter = ff.like(ff.property("highway"),
				"traffic_signals");
			Filter filter = ff.and(wayFilter,
				trafficLightFilter);
			Query query = new Query(typeName,
				wayFilter);
			query.setMaxFeatures(100);
			SimpleFeatureCollection features = featureSource.getFeatures(query);
			List<SimpleFeature> retValue = new LinkedList<>();
			SimpleFeatureIterator simpleFeatureIterator = features.features();
			while (simpleFeatureIterator.hasNext()) {
				retValue.add(simpleFeatureIterator.next());
			}
			return retValue;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
