/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.controlCenter.InitDialogCtrlInitialTypeEnum;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsNoInterferer;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.InformationBasedVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.swing.JOptionPane;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.wizard.JWizard;
import org.opengis.filter.Filter;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
/*
 - use IdGenerator of MainCtrl
 - interact with CCSimulationParamter singleton
 */
public class InitDialogCtrl implements Serializable {

	private static final long serialVersionUID = 1L;

//	private String chosenInitialType;
	/**
	 * maps pathes to deserialized instances of {@link ControlCenterStartParameter}
	 */
	private Map<String, ControlCenterStartParameter> recentScenarioMap = new HashMap<>();
	private ChosenStartParameterPair chosenRecentScenario;
	private String importedXML;
	private InformationBasedVehicleFactory vehicleFactory;
//	@ManagedProperty(value = "#{mainCtrl}")
//	private MainCtrl mainCtrl;
	@EJB
	private TcpIpOutput output;
	private Queue<VehicleData> uiVehicles;

	private InitDialogCtrlInitialTypeEnum chosenInitialType;
	@EJB
	private IdGenerator idGenerator;

	public InitDialogCtrl() {
	}

	public InitDialogCtrl(
		String chosenInitialType,
		ChosenStartParameterPair chosenRecentScenario,
		String importedXML,
		InformationBasedVehicleFactory vehicleFactory,
		TcpIpOutput output) {
//		this.chosenInitialType = chosenInitialType;
		this.chosenRecentScenario = chosenRecentScenario;
		this.importedXML = importedXML;
		this.vehicleFactory = vehicleFactory;
		this.output = output;
	}

	/**
	 * @return the chosenInitialType
	 */
//	public String getChosenInitialType() {
//		return chosenInitialType;
//	}
	public void setChosenInitialType(InitDialogCtrlInitialTypeEnum initialTypeEnum) {
		this.chosenInitialType = initialTypeEnum;
	}

	public InitDialogCtrlInitialTypeEnum getChosenInitialType() {
		return chosenInitialType;
	}

	/**
	 * @param chosenInitialType the chosenInitialType to set
	 */
//	public void setChosenInitialType(
//		String chosenInitialType) {
//		this.chosenInitialType = chosenInitialType;
//	}
	/**
	 * @return the importedXML
	 */
	public String getImportedXML() {
		return importedXML;
	}

	/**
	 * @param importedXML the importedXML to set
	 */
	public void setImportedXML(String importedXML) {
		this.importedXML = importedXML;
	}

	/**
	 * @return the vehicleFactory
	 */
	public InformationBasedVehicleFactory getVehicleFactory() {
		return vehicleFactory;
	}

	/**
	 * @param vehicleFactory the vehicleFactory to set
	 */
	public void setVehicleFactory(
		InformationBasedVehicleFactory vehicleFactory) {
		this.vehicleFactory = vehicleFactory;
	}

	/**
	 * @return the output
	 */
	public TcpIpOutput getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(
		TcpIpOutput output) {
		this.output = output;
	}

	private void connect() {
		DataStoreFactorySpi format = new PostgisNGDataStoreFactory();
		JDataStoreWizard wizard = new JDataStoreWizard(format);
		int result = wizard.showModalDialog();
		if (result == JWizard.FINISH) {
			try {
				Map<String, Object> connectionParameters = wizard.
					getConnectionParameters();

				DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);
				if (dataStore == null) {
					JOptionPane.showMessageDialog(null,
						"Could not connect - check parameters");
				}
				String typeName = dataStore.getTypeNames()[0]; //investigate
				SimpleFeatureSource source = dataStore.getFeatureSource(typeName);

				String filterString = "";
				Filter filter = CQL.toFilter(filterString);
				SimpleFeatureCollection features = source.getFeatures(filter);
			} catch (IOException | CQLException ex) {
				Logger.getLogger(InitDialogCtrl.class.getName()).log(Level.SEVERE,
					null,
					ex);
			}
		}
	}

	public List<String> retrieveOSMAutocomplete() {
		connect();
		return null;
	}

	@PostConstruct
	public void init() {
		uiVehicles = new LinkedList<VehicleData>(Arrays.asList(new CarData(
			Color.yellow,
			2000,
			500,
			3500,
			4000,
			2000,
			1000,
			1300,
			100,
			200,
			2,
			"name",
			new GpsSensor(idGenerator.getNextId(),
				output,
				null,
				new GpsNoInterferer()),
			VehicleTypeEnum.CAR)));
	}

	public String getNextGpsSensor() {
		Coordinate peekCoordinate = uiVehicles.peek().getGpsSensor().getSensorData().
			getPosition();
		peekCoordinate = new Coordinate(45,
			55);
		return String.format("[%f,%f]",
			peekCoordinate.getX(),
			peekCoordinate.getY());
	}

//	public void confirmInitialDialog(StartParameterOriginEnum chosenInitialType) {
//		switch (chosenInitialType) {
//			/*
//			 * New Scenario is to be created
//			 */
//			case CREATED:
//				FacesContext context = FacesContext.getCurrentInstance();
//				ControlCenterStartParameter startParameter = (ControlCenterStartParameter) context.
//					getELContext().getContext(ControlCenterStartParameter.class);
//				if (startParameter.getoSMAndBusstopFileData() == null) {
//					return;
//				}
//				sendOsmAndBusstop(startParameter.getoSMAndBusstopFileData());
//				break;
//			/*
//			 * Recently started scenario is to be loaded
//			 */
//			case RECENTLY_STARTED:
//				Long id = idGenerator.getNextId();
//				sendMessage(
//					new LoadSimulationStartParameterMessage(
//						id,
//						chosenRecentScenarioPath));
//				importCallback();
//				break;
//			/*
//			 * XML is to be imported
//			 */
//			case IMPORT:
//				if (importedXML == null) {
//					return;
//				}
//				Long id = idGenerator.getNextId();
//				sendMessage(
//					new ImportXMLStartParameterMessage(
//						id,
//						importedXML));
//				importCallback();
//				break;
//		}
//	}
//
//	public void sendOSMAndBusstop() {
//		oSMParsedStateEnum = OSMParsedStateEnum.IN_PROGRESS;
//		Long messageID = idGenerator.getNextId();
//		sendMessage(
//			new OSMAndBusstopFileMessage(
//				messageID,
//				oSMAndBusstopFileData));
//		function(callbackMessage) {
//			// Check if callback is appropriate
//			if (callbackMessage.messageType != = model.MessageType.OSM_PARSED) {
//				// Callback is error
//				if (callbackMessage.messageType == = model.MessageType.ERROR) {
//					PopupService.openMessageDialog('Error while parsing OSM and BusStops on server.'
//					);
//							console.log(callbackMessage.content);
//					return;
//				}
//				console.log(
//				 'Unrecognizapublic void confirmInitialDialog(StartParameterOriginEnum chosenInitialType) {
//		switch (chosenInitialType) {
//			/*
//			 * New Scenario is to be created
//			 */
//			case CREATED:
//				FacesContext context = FacesContext.getCurrentInstance();
//				ControlCenterStartParameter startParameter = (ControlCenterStartParameter) context.
//					getELContext().getContext(ControlCenterStartParameter.class);
//				if (startParameter.getoSMAndBusstopFileData() == null) {
//					return;
//				}
//				sendOsmAndBusstop(startParameter.getoSMAndBusstopFileData());
//				break;
//			/*
//			 * Recently started scenario is to be loaded
//			 */
//			case RECENTLY_STARTED:
//				Long id = idGenerator.getNextId();
//				sendMessage(
//					new LoadSimulationStartParameterMessage(
//						id,
//						chosenRecentScenarioPath));
//				importCallback();
//				break;
//			/*
//			 * XML is to be imported
//			 */
//			case IMPORT:
//				if (importedXML == null) {
//					return;
//				}
//				Long id = idGenerator.getNextId();
//				sendMessage(
//					new ImportXMLStartParameterMessage(
//						id,
//						importedXML));
//				importCallback();
//				break;
//		}
//	}
//
//	public void sendOSMAndBusstop() {
//		oSMParsedStateEnum = OSMParsedStateEnum.IN_PROGRESS;
//		Long messageID = idGenerator.getNextId();
//		sendMessage(
//			new OSMAndBusstopFileMessage(
//				messageID,
//				oSMAndBusstopFileData));
//		function(callbackMessage) {
//			// Check if callback is appropriate
//			if (callbackMessage.messageType != = model.MessageType.OSM_PARSED) {
//				// Callback is error
//				if (callbackMessage.messageType == = model.MessageType.ERROR) {
//					PopupService.openMessageDialog('Error while parsing OSM and BusStops on server.'
//					);
//							console.log(callbackMessage.content);
//					return;
//				}
//				console.log(
//				 'Unrecognizable callback called ... dafuq?'
//				);
//						return;
//			}
//
//			// Set bounds to mapService, set ready and initialize
//			MapService.setReady(callbackMessage.content);
//			if (typeof  {
//				_this.$scope.contentURL == = 
//			}'undefined'
//			
//				) {
//						MapService.init();
//			}
//
//			// Set status
//			_this.$scope.osm.osmParsedState = 'done'
//		;
//				});
//
//	public void sendOsmAndBusstop(OSMBusstopFileData ) {
//
//	}
//
//	public void sendMessage() {
//
//	}
//
//	public void importCallback(CCWebSocketMessage callbackMessage) {
//		// Check appropriate callback
//		if (!callbackMessage.getMessageType().equals(
//			MessageTypeEnum.SIMULATION_START_PARAMETER)) {
//			// Callback is error
//			if (callbackMessage.getMessageType().equals(MessageTypeEnum.ERROR)) {
//				FacesMessage msg = new FacesMessage(
//					"Error while parsing OSM and BusStops on server.");
//				FacesContext.getCurrentInstance().addMessage(null,
//					msg);
//				return;
//			}
//			FacesMessage msg = new FacesMessage("Unrecognizable callback");
//			FacesContext.getCurrentInstance().addMessage(null,
//				msg);
//			return;
//		}
//
//		// Temporarily save start paraneters
//		if (callbackMessage instanceof SimulationStartParameterMessage) {
//			importedStartParameter = ((SimulationStartParameterMessage) callbackMessage).
//				getContent();
//		}
//
//		// Request OSM and BusStop parsing
//		parseOsmAndBusstop(importedStartParameter.getoSMAndBusstopFileData(),
//			importedStartParameter);
//
//		// Import into UI
//		performStartParameter2UI(importedStartParameter);
//
//		//call Close Dialog on client
//		//PopupService.close('initial');
//}
//
//
//	public void setChosenInitialType(InitDialogEnum chosenInitialType) {
//		this.chosenInitialType = chosenInitialType;
//	}
//
//	public InitDialogEnum getChosenInitialType() {
//		return chosenInitialType;
//	}
//
//	private List<BusRoute> allBusRoutes;
//	private WeatherEventViewData currentWeatherEventViewData;
//	private Set<Vehicle<?>> uiVehicles = new HashSet<>();
//
//	/**
//	 *
//	 * @param oSMAndBusstopFileData
//	 * @param cCSimulationStartParameter the parsed information will be passed to
//	 * this start parameter
//	 */
//	public void parseOsmAndBusstop(OSMAndBusstopFileData oSMAndBusstopFileData,
//		ControlCenterStartParameter cCSimulationStartParameter) {
//		throw new UnsupportedOperationException();
//	}
//
//	public void performStartParameter2UI(ControlCenterStartParameter startParameter) {
//		// workaround
////		if (startParameter.specificUpdateSteps[model.SensorType.INDUCTIONLOOP] === 'undefined') {
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.INDUCTIONLOOP] = {name: 'Induction Loop', value: 10};
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.SMARTMETER] = {name: 'Smart Meter', value: 1};
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.WINDPOWERSENSOR] = {name: 'Wind Power Sensor', value: 1};
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.TOPORADAR] = {name: 'Topo Radar', value: 1};
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.PHOTOVOLTAIK] = {name: 'Photovoltaik', value: 1};
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.WEATHER_STATION] = {name: 'Weather Station', value: 1};
////			_this.$scope.startParameter.specificUpdateSteps[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = {name: 'Traffic Light Intersection', value: 1};
////		}
//
//		allBusRoutes = startParameter.getBusRoutes();
//
//		/*
//		 * WEATHER
//		 */
//		List<WeatherEvent> tmp = new LinkedList<>();
//		for (WeatherEvent w : startParameter.getWeatherEventList()) {
//			if (!w.getEvent().equWeatherEventTypeEnumEnum.CITYCLIMATE) {
//				 && !w.getEvent()
//			}WeatherEventTypeEnumventEnum.REFERENCECITY
//			
//				)) {
//				tmp.add(w);
//			}
//		}
//		currentWeatherEventViewData.setEvents(tmp);
//
//
//		/*
//		 * SensorObjects
//		 */
//		List<Sensor<?,?>> mapService = new LinkedList<>();
//		for (Sensor<?,?> s : startParameter.getSensorHelperList()) {
//			if (s.getSensorType().equals(SensorTypeEnum.ANEMOMETER)
//				|| s.getSensorType().equals(SensorTypeEnum.BAROMETER)
//				|| s.getSensorType().equals(SensorTypeEnum.HYGROMETER)
//				|| s.getSensorType().equals(SensorTypeEnum.LUXMETER)
//				|| s.getSensorType().equals(SensorTypeEnum.PYRANOMETER)
//				|| s.getSensorType().equals(SensorTypeEnum.RAIN)
//				|| s.getSensorType().equals(SensorTypeEnum.THERMOMETER)
//				|| s.getSensorType().equals(SensorTypeEnum.WINDFLAG)) {
//				throw new UnsupportedOperationException(
//					"implement uncheck all weather stations");
//			}
//		}
//
//		/*
//		 * Simulation events
//		 */
//		for (EventList<?> e : startParameter.getSimulationEventLists()) {
//			for (Event ev : e.getEventList()) {
//				if (ev instanceof CreateRandomVehiclesEvent) {
//					for (CreateRandomVehicleData c : ((CreateRandomVehiclesEvent<?>) ev).
//						getCreateRandomVehicleDataList()) {
//						VehicleInformation vehicleInformation = c.getVehicleInformation();
//						Vehicle<?> v = vehicleFactory.createVehicle(vehicleInformation,
//							output);
//						vehicles.add(v);
//					}
//				}else if()
//			}
//		}
//
//		angular.forEach(startParameter.simulationEventLists,
//			function(simulationEventList) {
//			angular.forEach(simulationEventList,
//				function(simulationEvent) {
//				switch (simulationEvent.eventType) {
//					/* PATH */
//					case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CREATE_RANDOM_VEHICLES_EVENT]:
//						var vehicles = {};
//						var oneVehicle = undefined;
//						angular.forEach(simulationEvent.vehicles,
//							function(vehicleInformation)  {
//							vehicles[vehicleInformation.vehicleID] = new model.Vehicle(
//								vehicleInformation.vehicleID,
//								vehicleInformation.vehicleTypeEnum,
//								vehicleInformation.vehicleModelEnum,
//								vehicleInformation.name,
//			public void confirmInitialDialog(StartParameterOriginEnum chosenInitialType) {
//		switch (chosenInitialType) {
//			/*
//			 * New Scenario is to be created
//			 */
//			case CREATED:
//				FacesContext context = FacesContext.getCurrentInstance();
//				ControlCenterStartParameter startParameter = (ControlCenterStartParameter) context.
//					getELContext().getContext(ControlCenterStartParameter.class);
//				if (startParameter.getoSMAndBusstopFileData() == null) {
//					return;
//				}
//				sendOsmAndBusstop(startParameter.getoSMAndBusstopFileData());
//				break;
//			/*
//			 * Recently started scenario is to be loaded
//			 */
//			case RECENTLY_STARTED:
//				Long id = idGenerator.getNextId();
//				sendMessage(
//					new LoadSimulationStartParameterMessage(
//						id,
//						chosenRecentScenarioPath));
//				importCallback();
//				break;
//			/*
//			 * XML is to be imported
//			 */
//			case IMPORT:
//				if (importedXML == null) {
//					return;
//				}
//				Long id = idGenerator.getNextId();
//				sendMessage(
//					new ImportXMLStartParameterMessage(
//						id,
//						importedXML));
//				importCallback();
//				break;
//		}
//	}
//
//	public void sendOSMAndBusstop() {
//		oSMParsedStateEnum = OSMParsedStateEnum.IN_PROGRESS;
//		Long messageID = idGenerator.getNextId();
//		sendMessage(
//			new OSMAndBusstopFileMessage(
//				messageID,
//				oSMAndBusstopFileData));
//		function(callbackMessage) {
//			// Check if callback is appropriate
//			if (callbackMessage.messageType != = model.MessageType.OSM_PARSED) {
//				// Callback is error
//				if (callbackMessage.messageType == = model.MessageType.ERROR) {
//					PopupService.openMessageDialog('Error while parsing OSM and BusStops on server.'
//					);
//							console.log(callbackMessage.content);
//					return;
//				}
//				console.log(
//				 'Unrecogniza					vehicleInformation.gpsActivated,
//								vehicleInformation.trip.startTime);
//							if (typeof  {
//								oneVehicle == = 
//							}'undefined'
//							)
//								oneVehicle = vehicles[vehicleInformation.vehicleID];
//						}
//						);
//
//						
//						break;
//					/* STREET BLOCK */
//					case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.ROAD_BARRIER_TRAFFIC_EVENT]:
//						var objectId = simulationEvent.id;
//						markerName =  
//						'Street block' + objectId;
//						markerObject = new model.StreetBlock(objectId,
//							roadBarrierPoint.latitude.degree,
//							roadBarrierPoint.longitude.degree,
//							markerName);
//						markerObject.startTimestamp = simulationEvent.roadBarrierStartTimestamp;
//						markerObject.durationMillis = simulationEvent.roadBarrierEndTimestamp - simulationEvent.roadBarrierStartTimestamp;
//						markerObject.nodeId = simulationEvent.nodeID;
//						MapService.addEvent(markerObject);
//						break;
//					/* ENERGY EVENT */
//					case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT]:
//						var objectId = simulationEvent.id;
//						markerName =  
//						'Energy event' + objectId;
//						markerObject = new model.EnergyEvent(objectId,
//							roadBarrierPoint.latitude.degree,
//							roadBarrierPoint.longitude.degree,
//							markerName);
//						markerObject.startTimestamp = simulationEvent.startTimestamp;
//						markerObject.durationMillis = simulationEvent.endTimestamp - simulationEvent.startTimestamp;
//						markerObject.value = simulationEvent.percentage * 100;
//						markerObject.ratio = simulationEvent.measureRadiusInMeter;
//						MapService.drawRadius(markerObject);
//						MapService.addEvent(markerObject);
//						break;
//				}
//			});
//		});
//
//		/*
//		 * Attraction
//		 */
//		angular.forEach(startParameter.attractionCollection,
//			function(attraction) {
//			var objectId = attraction.id;
//			markerName = 'Attraction'+ objectId;
//			markerObject = new model.Attraction(objectId,
//				attraction.attractionPoint.latitude.degree,
//				attraction.attractionPoint.longitude.degree,
//				markerName);
//		});
//	}
//;
	public ChosenStartParameterPair getChosenRecentScenario() {
		return chosenRecentScenario;
	}

	public void setChosenRecentScenario(
		ChosenStartParameterPair chosenRecentScenario) {
		this.chosenRecentScenario = chosenRecentScenario;
	}

	public Map<String, ControlCenterStartParameter> getRecentScenarios() {
		return recentScenarioMap;
	}

	public void setRecentScenarios(
		Map<String, ControlCenterStartParameter> recentScenarios) {
		this.recentScenarioMap = recentScenarios;
	}
}
