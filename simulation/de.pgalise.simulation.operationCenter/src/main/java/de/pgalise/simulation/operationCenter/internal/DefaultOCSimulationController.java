/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.operationCenter.internal;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.pgalise.simulation.operationCenter.internal.hqf.OCHQFDataStreamController;
import de.pgalise.simulation.operationCenter.internal.message.ErrorMessage;
import de.pgalise.simulation.operationCenter.internal.message.GPSSensorTimeoutMessage;
import de.pgalise.simulation.operationCenter.internal.message.GateMessage;
import de.pgalise.simulation.operationCenter.internal.message.NewSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.NewVehiclesMessage;
import de.pgalise.simulation.operationCenter.internal.message.RemoveSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.RemoveVehiclesMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationInitMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationStartMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationStoppedMessage;
import de.pgalise.simulation.operationCenter.internal.model.OCSimulationInitParameter;
import de.pgalise.simulation.operationCenter.internal.model.OCSimulationStartParameter;
import de.pgalise.simulation.operationCenter.internal.model.SensorHelperTypeWrapper;
import de.pgalise.simulation.operationCenter.internal.model.VehicleData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.GPSSensorData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSGateStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSSensorTimeoutStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.SendSensorDataStrategy;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.traffic.event.DeleteVehiclesEvent;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventTypeEnum;

/**
 * The default implementation of {@link OCSimulationController}.
 * It sends messages to all users in {@link OCWebSocketService} with
 * information about simulation init, start, stop, new sensors,
 * new sensor values and gps senors with timeout.
 * This implementation uses given implementations of {@link GPSSensorTimeoutStrategy},
 * {@link OCWebSocketService}, {@link OCSensorStreamController}, {@link OCHQFDataStreamController}
 * and {@link SendSensorDataStrategy} for its purposes.
 * All the settings can be done in 'properties.props'.
 * 
 * @author Timo
 */
public class DefaultOCSimulationController extends AbstractController<Event> implements
		OCSimulationController {
	private static final Logger log = LoggerFactory
			.getLogger(DefaultOCSimulationController.class);
	private GPSSensorTimeoutStrategy gpsTimeoutStrategy;
	private OCWebSocketService ocWebSocketService;
	private OCSensorStreamController ocSensorStreamController;
	private OCHQFDataStreamController ocHqfDataStreamController;
	private SendSensorDataStrategy sendSensorDataStrategy;
	private InitParameter initParameter;
	private StartParameter startParameter;
	private long currentTimestamp;
	private Set<SensorHelper> currentGPSSensorHelpers;
	/**
	 * Holds all current sensor ids.
	 * <Integer = sensor type, Set<Integer = sensor id>>
	 */
	private Map<Integer, Set<Integer>> sensorTypeIDMap;
	private Properties properties;
	private GPSGateStrategy gateMessageStrategy;

	/**
	 * A map with marker as entry and sensor id as key.
	 */
	private Map<Integer, SensorHelperTypeWrapper> sensorMap;
	/**
	 * Map<UUID = vehicle id, 
	 */
	private Map<Vehicle<?>, VehicleData> vehicleDataMap;

	/**
	 * Contructor
	 * @param ocWebSocketService
	 * 			this instance will receive messages for init, start, stop, new sensors and new sensor data.
	 * @param ocSensorStreamController
	 * 			this controller must inform the default oc simulation controller about sensor updates
	 * @param gpsTimeoutStrategy
	 * 			this strategy has to find gps time outs
	 * @param sendSensorDataStrategy
	 * 			this strategy defines when the sensor data will be sent to the user
	 * @param ocHqfDataStreamController
	 * 			this controller will save the received sensor data into a database
	 * @param gateMessageStrategy
	 * 			this steategy handles the gate messages for IBM InfoSphere
	 */
	@Inject
	public DefaultOCSimulationController(OCWebSocketService ocWebSocketService,
			OCSensorStreamController ocSensorStreamController,
			GPSSensorTimeoutStrategy gpsTimeoutStrategy,
			SendSensorDataStrategy sendSensorDataStrategy,
			OCHQFDataStreamController ocHqfDataStreamController,
			GPSGateStrategy gateMessageStrategy) {
		this.ocWebSocketService = ocWebSocketService;
		this.ocSensorStreamController = ocSensorStreamController;
		this.ocHqfDataStreamController = ocHqfDataStreamController;
		this.gateMessageStrategy = gateMessageStrategy;
		this.gpsTimeoutStrategy = gpsTimeoutStrategy;
		this.sendSensorDataStrategy = sendSensorDataStrategy;
		this.sensorMap = new HashMap<>();
		this.currentGPSSensorHelpers = new HashSet<>();
		this.vehicleDataMap = new HashMap<>();
		this.properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("/properties.props"));
		} catch (Exception e) {
			try {
				properties.load(DefaultOCSimulationController.class
						.getResourceAsStream("/properties.props"));
			} catch (IOException e1) {
				throw new RuntimeException("Can not load properties!");
			}
		}
		this.sensorTypeIDMap = new HashMap<>();
		this.ocWebSocketService.registerNewUserEventListener(this);
	}

	@Override
	public void displayException(Exception exeption) {
		try {
			this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(
					exeption.getMessage()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public OCSensorStreamController getOcSensorStreamController() {
		return ocSensorStreamController;
	}

	public void setOcSensorStreamController(
			OCSensorStreamController ocSensorStreamController) {
		this.ocSensorStreamController = ocSensorStreamController;
	}

	public OCWebSocketService getOcWebSocketService() {
		return ocWebSocketService;
	}

	public void setOcWebSocketService(OCWebSocketService ocWebSocketService) {
		this.ocWebSocketService = ocWebSocketService;
	}

	public OCHQFDataStreamController getOcHqfDataStreamController() {
		return ocHqfDataStreamController;
	}

	public void setOcHqfDataStreamController(
			OCHQFDataStreamController ocHqfDataStreamController) {
		this.ocHqfDataStreamController = ocHqfDataStreamController;
	}

	@Override
	public void createSensor(SensorHelper sensor) throws SensorException,
			IllegalStateException {
		List<SensorHelper> sensorHelperList = new LinkedList<>();
		sensorHelperList.add(sensor);
		this.createSensors(sensorHelperList);
		this.gateMessageStrategy.createSensor(sensor);
	}

	@Override
	public void deleteSensor(SensorHelper sensor) throws SensorException,
			IllegalStateException {
		List<SensorHelper> sensorHelperList = new LinkedList<>();
		sensorHelperList.add(sensor);
		this.deleteSensors(sensorHelperList);
		this.gateMessageStrategy.deleteSensor(sensor);
	}

	public Map<Integer, SensorHelperTypeWrapper> getSensorMap() {
		return sensorMap;
	}

	public void setSensorMap(Map<Integer, SensorHelperTypeWrapper> sensorMap) {
		this.sensorMap = sensorMap;
	}

	@Override
	public void update(long timestamp, SensorData sensorData)
			throws IllegalStateException {
		log.debug("Updating: " + new Date(timestamp) + " sensorid: "
				+ sensorData.getId() + " sensortype: " + sensorData.getType());

		if (timestamp > this.currentTimestamp) {
			
			log.debug("Look for timeouts. Current gps sensors: " +this.currentGPSSensorHelpers.size());
			
			this.currentTimestamp = timestamp;
			Collection<SensorData> gpsSensorsWithTimeout = this.gpsTimeoutStrategy
					.processUpdateStep(timestamp, this.currentGPSSensorHelpers);
			this.currentGPSSensorHelpers = new HashSet<>();
						
			if (gpsSensorsWithTimeout != null
					&& !gpsSensorsWithTimeout.isEmpty()) {
				log.debug("Found: " +gpsSensorsWithTimeout.size() +" timeouts. ");
				try {
					this.ocWebSocketService
							.sendMessageToAllUsers(new GPSSensorTimeoutMessage(
									gpsSensorsWithTimeout));
				} catch (IOException e) {
					log.error("Exception", e);
				}
			}else {
				log.debug("Found: no timeouts. ");
			}
		}

		SensorHelperTypeWrapper sensorHelperTypeWrapper = this.sensorMap
				.get(sensorData.getId());
		if (sensorHelperTypeWrapper != null) {
			try {
				this.sendSensorDataStrategy
						.addSensorData(timestamp, sensorData);
			} catch (IOException e) {
				log.error("Exception", e);
			}

			if (sensorData instanceof GPSSensorData) {
				log.debug("Add sensor: " +sensorData.getId() +" to current gps sensors");
				this.currentGPSSensorHelpers.add(sensorHelperTypeWrapper
						.getSensorHelper());
			}
		} else {
			log.warn("Sensorid: " +sensorData.getId() +" unknown!");
		}
	}

	/**
	 * Resets the sensor map.
	 */
	public void resetSensorMap() {
		this.sensorMap = new HashMap<>();
	}

	public InitParameter getInitParameter() {
		return initParameter;
	}

	public void setInitParameter(InitParameter initParameter) {
		this.initParameter = initParameter;
	}

	public StartParameter getStartParameter() {
		return startParameter;
	}

	public void setStartParameter(StartParameter startParameter) {
		this.startParameter = startParameter;
	}

	/**
	 * @return returns always false.
	 */
	@Override
	public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
		/* Nothing to do */
		return false;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		this.currentTimestamp = param.getStartTimestamp();
		this.setInitParameter(param);
		try {
			this.gpsTimeoutStrategy.init(param.getInterval(), Integer
					.valueOf(this.properties
							.getProperty("MissedGPSUpdateStepsBeforeTimeout")));
			this.ocWebSocketService
					.sendMessageToAllUsers(new SimulationInitMessage(
							new OCSimulationInitParameter(param
									.getStartTimestamp(), param
									.getEndTimestamp(), param.getInterval(),
									param.getClockGeneratorInterval(), param
											.getCityBoundary())));
		} catch (IOException e) {
			log.error("Exception", e);
		}
		this.gateMessageStrategy.init(param);
	}

	@Override
	protected void onReset() {
		this.currentTimestamp = this.getInitParameter().getStartTimestamp();
		this.gateMessageStrategy.reset();
	}

	@Override
	protected void onStart(StartParameter param) {
		this.setStartParameter(param);
		try {
			
			this.ocWebSocketService.sendMessageToAllUsers(new SimulationStartMessage(new OCSimulationStartParameter(
					param.getCity().getName(), param.getCity().getPopulation())));			
			this.ocSensorStreamController.listenStream(this, 
					this.properties.getProperty("InfoSphereIPStaticSensors"),  
						Integer.valueOf(this.properties.getProperty("InfoSpherePortStaticSensor")), 
					this.properties.getProperty("InfoSphereIPDynamicSensors"), 
						Integer.valueOf(this.properties.getProperty("InfoSpherePortDynamicSensor")), 
					this.properties.getProperty("InfoSphereIPTopoRadarSensors"), 
						Integer.valueOf(this.properties.getProperty("InfoSpherePortTopoRadarSensor")),
					this.properties.getProperty("InfoSphereIPTrafficLightSensors"),
						Integer.valueOf(this.properties.getProperty("InfoSpherePortTrafficLightSensor")));
					this.ocHqfDataStreamController.listenStream(this.properties.getProperty("InfoSphereIPStaticSensors"), 
						Integer.valueOf(this.properties.getProperty("InfoSpherePortHQFData")));
		} catch (IOException e) {
			log.error("Exception", e);
			try {
				this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(e.getLocalizedMessage()));
			} catch (IOException e1) {
				log.error("Exception", e1);
			}
		}
		this.gateMessageStrategy.start(param);
	}

	@Override
	protected void onStop() {
		try {
			this.getOcWebSocketService().sendMessageToAllUsers(
					new SimulationStoppedMessage());
			this.ocSensorStreamController.unlistenStream();
			this.ocHqfDataStreamController.unlistenStream();
		} catch (IOException e) {
			log.error("Exception", e);
			try {
				this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(
						e.getLocalizedMessage()));
			} catch (IOException e1) {
				log.error("Exception", e1);
			}
		}
		this.gateMessageStrategy.stop();
	}

	@Override
	protected void onResume() {
		try {
			this.init(this.getInitParameter());
		} catch (IllegalStateException e) {
			log.warn("see nested exception", e);
			try {
				this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(
						e.getLocalizedMessage()));
			} catch (IOException e1) {
				log.error("Exception", e1);
			}
		}
		this.start(startParameter);
	}

	/**
	 * Creates a vehicle data from a create random vehicle data.
	 * @param data
	 * @return
	 */
	private VehicleData createRandomVehicleDataToVehicleData(CreateRandomVehicleData data) {
		Collection<SensorHelperTypeWrapper> sensors = new LinkedList<>();
		for(SensorHelper sensor : data.getSensorHelpers()) {
			sensors.add(new SensorHelperTypeWrapper(sensor));
		}
		return new VehicleData(sensors,  null,
				data.getVehicleInformation().getVehicleType().getVehicleTypeId(), 
				data.getVehicleInformation().getVehicleModel().getVehicleModelId());
	}
	
	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {
		/* Add new events here: */
		for (Event event : simulationEventList.getEventList()) {
			try {
				if(event.getType().equals(TrafficEventTypeEnum.CREATE_VEHICLES_EVENT)) {
					Collection<VehicleData> vehicleDataCollection = new LinkedList<>();
					log.warn("Create new vehicles");
					for (CreateRandomVehicleData data : ((CreateVehiclesEvent<?>) event)
							.getVehicles()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				}

				else if(event.getType().equals(TrafficEventTypeEnum. CREATE_RANDOM_VEHICLES_EVENT)) {
					log.warn("Create new random vehicles: " +((CreateRandomVehiclesEvent) event)
							.getCreateRandomVehicleDataList().size());
					Collection<VehicleData> vehicleDataCollection = new LinkedList<>();
					for (CreateRandomVehicleData data : ((CreateRandomVehiclesEvent<?>) event)
							.getCreateRandomVehicleDataList()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				}

				else if(event.getType().equals(TrafficEventTypeEnum. CREATE_BUSSES_EVENT)) {
					log.warn("Create new random vehicles: " +((CreateBussesEvent) event)
							.getCreateRandomVehicleDataList().size());
					Collection<VehicleData> vehicleDataCollection = new LinkedList<>();
					for (CreateRandomVehicleData data : ((CreateBussesEvent) event)
							.getCreateRandomVehicleDataList()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				}
				else if(event.getType().equals(TrafficEventTypeEnum. DELETE_VEHICLES_EVENT)) {
					Collection<VehicleData> vehicleDataCollection = new LinkedList<>();
					vehicleDataCollection.add(this.vehicleDataMap.get(((DeleteVehiclesEvent<?>)event).getVehicle()));
					this.removeVehicleSensors(vehicleDataCollection);
				}
				else if(event.getType().equals(TrafficEventTypeEnum. ATTRACTION_TRAFFIC_EVENT)) {
					log.warn("Create new random vehicles: " +((AttractionTrafficEvent) event)
							.getCreateRandomVehicleDataList().size());
					Collection<VehicleData> vehicleDataCollection = new LinkedList<>();
					for (CreateRandomVehicleData data : ((AttractionTrafficEvent<?>) event)
							.getCreateRandomVehicleDataList()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				}
				else {
					log.warn(event.getType().toString());
				}
			} catch (Exception e) {
				log.error("Exception", e);
				try {
					this.ocWebSocketService
					.sendMessageToAllUsers(new ErrorMessage(e
							.getLocalizedMessage()));
				} catch (IOException e1) {
				}
			}
		}
		this.gateMessageStrategy.update(simulationEventList);
	}

	@Override
	public void createSensors(Collection<SensorHelper> sensors)
			throws SensorException {

		List<SensorHelperTypeWrapper> sensorHelperTypeWrapperList = new LinkedList<>();
		for (SensorHelper sensor : sensors) {
			SensorHelperTypeWrapper tmpSensorHelperTypeWrapper = new SensorHelperTypeWrapper(
					sensor);
			this.sensorMap.put(sensor.getSensorID(), tmpSensorHelperTypeWrapper);
			sensorHelperTypeWrapperList.add(tmpSensorHelperTypeWrapper);
			Set<Integer> sensorIDSet = this.sensorTypeIDMap.get(sensor.getSensorType().getSensorTypeId());
			if(sensorIDSet == null) {
				sensorIDSet = new HashSet<>();
				this.sensorTypeIDMap.put(sensor.getSensorType().getSensorTypeId(), sensorIDSet);
			}
			sensorIDSet.add(sensor.getSensorID());
			log.debug("create sensor: " + sensor.getSensorType() + " with id: "
					+ sensor.getSensorID());
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(
					new NewSensorsMessage(sensorHelperTypeWrapperList));
		} catch (IOException e) {
			log.error("Exception", e);
		}
		
		this.gateMessageStrategy.createSensors(sensors);

	}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors)
			throws SensorException {
		List<Integer> sensorIDList = new LinkedList<>();
		for (SensorHelper sensor : sensors) {
			sensorIDList.add(sensor.getSensorID());
			sensorMap.remove(sensor.getSensorID());
			sensorTypeIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getSensorID());
			log.debug("remove sensor: " + sensor.getSensorType() + " with id: "
					+ sensor.getSensorID());
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(
					new RemoveSensorsMessage(sensorIDList));
		} catch (IOException e) {
			log.error("Exception", e);
		}
		this.gateMessageStrategy.deleteSensors(sensors);
	}

	@Override
	public void handleGateMessage(GateMessage gateMessage) throws UnknownHostException, IOException {
		this.gateMessageStrategy.handleGateMessage(gateMessage.getContent());
	}

	@Override
	public void handleNewUserEvent(OCWebSocketUser user) throws IOException {
		log.debug("handle new user event. Current state is: " +this.getStatus());
		
		switch (this.getStatus()) {
		case INITIALIZED:
			/* Send init: */
			user.sendMessage(new SimulationInitMessage(
					new OCSimulationInitParameter(this.initParameter.getStartTimestamp(), 
							this.initParameter.getEndTimestamp(),
							this.initParameter.getInterval(),
							this.initParameter.getClockGeneratorInterval(), 
							this.initParameter.getCityBoundary())));
			/* Send sensors: */
			user.sendMessage(new NewSensorsMessage(sensorMap.values()));
			break;
		case STARTED:
			/* Send init: */
			user.sendMessage(new SimulationInitMessage(
					new OCSimulationInitParameter(this.initParameter.getStartTimestamp(), 
							this.initParameter.getEndTimestamp(),
							this.initParameter.getInterval(),
							this.initParameter.getClockGeneratorInterval(), 
							this.initParameter.getCityBoundary())));
			/* Send sensors */
			user.sendMessage(new NewSensorsMessage(sensorMap.values()));
			
			/* Send vehicles */
			user.sendMessage(new NewVehiclesMessage(vehicleDataMap.values()));
			
			/* Send start */
			user.sendMessage(new SimulationStartMessage(new OCSimulationStartParameter(
					this.startParameter.getCity().getName(), 
					this.startParameter.getCity().getPopulation())));
			break;
		default:
			break;
		}
	}
	
	private void removeVehicleSensors(Collection<VehicleData> vehicleDataCollection) {
		Collection<SensorHelper> sensorHelperCollection = new LinkedList<>();
		
		for(VehicleData vehicleData : vehicleDataCollection) {
			for(SensorHelperTypeWrapper sensor : vehicleData.getSensors()) {
				this.sensorMap.put(sensor.getSensorHelper().getSensorID(), sensor);
				Set<Integer> sensorIDSet = this.sensorTypeIDMap.get(sensor.getSensorHelper().getSensorType().getSensorTypeId());
				if(sensorIDSet != null) {
					sensorIDSet.remove(sensor.getSensorHelper().getSensorID());
				}
				sensorHelperCollection.add(sensor.getSensorHelper());
				log.debug("Remove sensor: " + sensor.getSensorType() + " with id: "
						+ sensor.getSensorHelper().getSensorID());
			}
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(new RemoveVehiclesMessage(vehicleDataCollection));
		} catch (IOException e) {
			log.error("Exception", e);
		}
		
		try {
			this.gateMessageStrategy.deleteSensors(sensorHelperCollection);
		} catch (SensorException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates the sensors for vehicles and sends them to the user.
	 * @param vehicleDataCollection
	 */
	private void createsVehicleSensors(Collection<VehicleData> vehicleDataCollection) {
		Collection<SensorHelper> sensorHelperCollection = new LinkedList<>();
		
		for(VehicleData vehicleData : vehicleDataCollection) {
			for(SensorHelperTypeWrapper sensor : vehicleData.getSensors()) {
				this.sensorMap.put(sensor.getSensorHelper().getSensorID(), sensor);
				Set<Integer> sensorIDSet = this.sensorTypeIDMap.get(sensor.getSensorHelper().getSensorType().getSensorTypeId());
				if(sensorIDSet == null) {
					sensorIDSet = new HashSet<>();
					this.sensorTypeIDMap.put(sensor.getSensorHelper().getSensorType().getSensorTypeId(), sensorIDSet);
				}
				sensorIDSet.add(sensor.getSensorHelper().getSensorID());
				sensorHelperCollection.add(sensor.getSensorHelper());
				log.debug("create sensor: " + sensor.getSensorType() + " with id: "
						+ sensor.getSensorHelper().getSensorID());
			}
			
			this.vehicleDataMap.put(vehicleData.getVehicleID(), vehicleData);
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(new NewVehiclesMessage(vehicleDataCollection));
		} catch (IOException e) {
			log.error("Exception", e);
		}
		
		try {
			this.gateMessageStrategy.createSensors(sensorHelperCollection);
		} catch (SensorException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return DefaultOCSimulationController.class.getName();
	}
}
