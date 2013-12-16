/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.internal;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.controlCenter.model.CCSimulationStartParameter;
import de.pgalise.simulation.controlCenter.model.OSMAndBusstopFileData;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsNoInterferer;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.InformationBasedVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class InitDialogBean {

	@EJB
	private IdGenerator idGenerator;
	private InitDialogEnum chosenInitialType;
	private String chosenRecentScenarioPath;
	private String importedXML;
	private OSMParsedStateEnum oSMParsedStateEnum;
	private OSMAndBusstopFileData oSMAndBusstopFileData;
	private CCSimulationStartParameter importedStartParameter;
	private InformationBasedVehicleFactory vehicleFactory;
	private Output output = new Output() {

		@Override
		public void beginTransmit() throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void endTransmit() throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public OutputStateEnum getState() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitBoolean(boolean value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitByte(byte value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitShort(short value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitInt(int value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitLong(long value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitFloat(float value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitDouble(double value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitString(String value) throws IllegalArgumentException, IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void transmitByteArray(byte[] value) throws IllegalStateException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	};
	private Queue<VehicleData> uiVehicles = new LinkedList<VehicleData>(Arrays.asList(new CarData(Color.yellow,
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
				new GpsSensor(output,
					null,
					new GpsNoInterferer()),
				VehicleTypeEnum.CAR)));

	public InitDialogBean() {
	}

	public InitDialogBean(IdGenerator idGenerator,
		InitDialogEnum chosenInitialType,
		String chosenRecentScenarioPath,
		String importedXML,
		OSMParsedStateEnum oSMParsedStateEnum,
		OSMAndBusstopFileData oSMAndBusstopFileData,
		CCSimulationStartParameter importedStartParameter,
		InformationBasedVehicleFactory vehicleFactory,
		Output output) {
		this.idGenerator = idGenerator;
		this.chosenInitialType = chosenInitialType;
		this.chosenRecentScenarioPath = chosenRecentScenarioPath;
		this.importedXML = importedXML;
		this.oSMParsedStateEnum = oSMParsedStateEnum;
		this.oSMAndBusstopFileData = oSMAndBusstopFileData;
		this.importedStartParameter = importedStartParameter;
		this.vehicleFactory = vehicleFactory;
		this.output = output;
	}

	/**
	 * @return the idGenerator
	 */
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	/**
	 * @param idGenerator the idGenerator to set
	 */
	public void setIdGenerator(
		IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/**
	 * @return the chosenInitialType
	 */
	public InitDialogEnum getChosenInitialType() {
		return chosenInitialType;
	}

	/**
	 * @param chosenInitialType the chosenInitialType to set
	 */
	public void setChosenInitialType(
		InitDialogEnum chosenInitialType) {
		this.chosenInitialType = chosenInitialType;
	}

	/**
	 * @return the chosenRecentScenarioPath
	 */
	public String getChosenRecentScenarioPath() {
		return chosenRecentScenarioPath;
	}

	/**
	 * @param chosenRecentScenarioPath the chosenRecentScenarioPath to set
	 */
	public void setChosenRecentScenarioPath(
		String chosenRecentScenarioPath) {
		this.chosenRecentScenarioPath = chosenRecentScenarioPath;
	}

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
	 * @return the oSMParsedStateEnum
	 */
	public OSMParsedStateEnum getoSMParsedStateEnum() {
		return oSMParsedStateEnum;
	}

	/**
	 * @param oSMParsedStateEnum the oSMParsedStateEnum to set
	 */
	public void setoSMParsedStateEnum(
		OSMParsedStateEnum oSMParsedStateEnum) {
		this.oSMParsedStateEnum = oSMParsedStateEnum;
	}

	/**
	 * @return the oSMAndBusstopFileData
	 */
	public OSMAndBusstopFileData getoSMAndBusstopFileData() {
		return oSMAndBusstopFileData;
	}

	/**
	 * @param oSMAndBusstopFileData the oSMAndBusstopFileData to set
	 */
	public void setoSMAndBusstopFileData(
		OSMAndBusstopFileData oSMAndBusstopFileData) {
		this.oSMAndBusstopFileData = oSMAndBusstopFileData;
	}

	/**
	 * @return the importedStartParameter
	 */
	public CCSimulationStartParameter getImportedStartParameter() {
		return importedStartParameter;
	}

	/**
	 * @param importedStartParameter the importedStartParameter to set
	 */
	public void setImportedStartParameter(
		CCSimulationStartParameter importedStartParameter) {
		this.importedStartParameter = importedStartParameter;
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
	public Output getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(
		Output output) {
		this.output = output;
	}
	
	public String getNextGpsSensor() {
		Coordinate peekCoordinate = uiVehicles.peek().getGpsSensor().getSensorData().getPosition();
		peekCoordinate = new Coordinate(45,
			55);
		return String.format("[%f,%f]", peekCoordinate.x, peekCoordinate.y);
	}

//	public void confirmInitialDialog(StartParameterOriginEnum chosenInitialType) {
//		switch (chosenInitialType) {
//			/*
//			 * New Scenario is to be created
//			 */
//			case CREATED:
//				FacesContext context = FacesContext.getCurrentInstance();
//				CCSimulationStartParameter startParameter = (CCSimulationStartParameter) context.
//					getELContext().getContext(CCSimulationStartParameter.class);
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
//				CCSimulationStartParameter startParameter = (CCSimulationStartParameter) context.
//					getELContext().getContext(CCSimulationStartParameter.class);
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
//		CCSimulationStartParameter cCSimulationStartParameter) {
//		throw new UnsupportedOperationException();
//	}
//
//	public void performStartParameter2UI(CCSimulationStartParameter startParameter) {
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
//				CCSimulationStartParameter startParameter = (CCSimulationStartParameter) context.
//					getELContext().getContext(CCSimulationStartParameter.class);
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
	
	public String test() {
		return "lslslsl";
	}
}
