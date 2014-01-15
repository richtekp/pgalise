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

import de.pgalise.simulation.energy.EnergySensorController;
import de.pgalise.simulation.event.EventInitiator;
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
import de.pgalise.simulation.operationCenter.internal.strategy.GPSGateStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSSensorTimeoutStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.SendSensorDataStrategy;
import de.pgalise.simulation.operationcenter.OCWebSocketUser;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.entity.GPSSensorData;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorController;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.traffic.event.DeleteVehiclesEvent;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.InformationBasedVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
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
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link OCSimulationController}. It sends
 * messages to all users in {@link OCWebSocketService} with information about
 * simulation init, start, stop, new sensors, new sensor values and gps senors
 * with timeout. This implementation uses given implementations of {@link GPSSensorTimeoutStrategy},
 * {@link OCWebSocketService}, {@link OCSensorStreamController}, {@link OCHQFDataStreamController}
 * and {@link SendSensorDataStrategy} for its purposes. All the settings can be
 * done in 'properties.props'.
 *
 * @author Timo
 */
@Singleton
public class DefaultOCSimulationController extends AbstractController<Event, TrafficStartParameter, TrafficInitParameter>
	implements
	OCSimulationController {

	private static final Logger log = LoggerFactory
		.getLogger(DefaultOCSimulationController.class);
	private static final long serialVersionUID = 1L;
	private GPSSensorTimeoutStrategy gpsTimeoutStrategy;
	private OCWebSocketService ocWebSocketService;
	private OCSensorStreamController ocSensorStreamController;
	private OCHQFDataStreamController ocHqfDataStreamController;
	private SendSensorDataStrategy sendSensorDataStrategy;
	private TrafficInitParameter initParameter;
	private TrafficStartParameter startParameter;
	private long currentTimestamp;
	private Set<GpsSensor> currentGPSSensors;
	/**
	 * Holds all current sensor ids. <Integer = sensor type, Set<Integer = sensor
	 * id>>
	 */
	private final Map<Class<? extends Sensor>, Set<Sensor<?, ?>>> sensorTypeIDMap = new HashMap<>();
	private Properties properties;
	private GPSGateStrategy gateMessageStrategy;

	/**
	 * A map with marker as entry and sensor id as key.
	 */
	private final Set<Sensor<?, ?>> sensors = new HashSet<>();
	/**
	 * Map<UUID = vehicle id,
	 */
	private Set<Vehicle<?>> vehicleDataMap;

	@EJB
	private InformationBasedVehicleFactory vehicleFactory;

	private Output output;

	public DefaultOCSimulationController() {
	}

	/**
	 * Contructor
	 *
	 * @param ocWebSocketService this instance will receive messages for init,
	 * start, stop, new sensors and new sensor data.
	 * @param ocSensorStreamController this controller must inform the default oc
	 * simulation controller about sensor updates
	 * @param gpsTimeoutStrategy this strategy has to find gps time outs
	 * @param sendSensorDataStrategy this strategy defines when the sensor data
	 * will be sent to the user
	 * @param ocHqfDataStreamController this controller will save the received
	 * sensor data into a database
	 * @param gateMessageStrategy this steategy handles the gate messages for IBM
	 * InfoSphere
	 */
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
		this.currentGPSSensors = new HashSet<>();
		this.vehicleDataMap = new HashSet<>();
		this.properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("properties.props"));
		} catch (IOException e) {
			try {
				properties.load(DefaultOCSimulationController.class
					.getResourceAsStream("properties.props"));
			} catch (IOException e1) {
				throw new RuntimeException("Can not load properties!");
			}
		}
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
	public void createSensor(Sensor<?, ?> sensor)  {
		Set<Sensor<?, ?>> sensorHelperList = new HashSet<>();
		sensorHelperList.add(sensor);
		this.createSensors(sensorHelperList);
		if(sensor instanceof GpsSensor) {
			this.gateMessageStrategy.createSensor((GpsSensor)sensor);
		}
	}

	@Override
	public void deleteSensor(Sensor sensor)  {
		Set<Sensor<?, ?>> sensorHelperList = new HashSet<>();
		sensorHelperList.add(sensor);
		this.deleteSensors(sensorHelperList);
		if(sensor instanceof GpsSensor) {
			this.gateMessageStrategy.deleteSensor((GpsSensor)sensor);
		}
	}

	@Override
	public void update(long timestamp,
		Sensor sensor)
		throws IllegalStateException {
		log.debug("Updating: " + new Date(timestamp) + " sensorid: "
			+ sensor + " sensortype: " + sensor.
			getSensorType());

		if (timestamp > this.currentTimestamp) {

			log.debug("Look for timeouts. Current gps sensors: "
				+ this.currentGPSSensors.size());

			this.currentTimestamp = timestamp;
			Set<GpsSensor> gpsSensorsWithTimeout = this.gpsTimeoutStrategy
				.processUpdateStep(timestamp,
					this.currentGPSSensors);
			this.currentGPSSensors = new HashSet<>();

			if (gpsSensorsWithTimeout != null
				&& !gpsSensorsWithTimeout.isEmpty()) {
				log.debug("Found: " + gpsSensorsWithTimeout.size() + " timeouts. ");
				try {
					this.ocWebSocketService
						.sendMessageToAllUsers(new GPSSensorTimeoutMessage(
								gpsSensorsWithTimeout));
				} catch (IOException e) {
					log.error("Exception",
						e);
				}
			} else {
				log.debug("Found: no timeouts. ");
			}
		}

		if (sensor != null) {
			try {
				this.sendSensorDataStrategy
					.addSensorData(timestamp,
						sensor);
			} catch (IOException e) {
				log.error("Exception",
					e);
			}

			if (sensor instanceof GpsSensor) {
				log.debug("Add sensor: " + sensor
					+ " to current gps sensors");
				this.currentGPSSensors.add((GpsSensor) sensor);
			}
		} else {
			log.warn("Sensorid: " + sensor + " unknown!");
		}
	}

	public TrafficInitParameter getInitParameter() {
		return initParameter;
	}

	public void setInitParameter(TrafficInitParameter initParameter) {
		this.initParameter = initParameter;
	}

	public StartParameter getStartParameter() {
		return startParameter;
	}

	public void setStartParameter(TrafficStartParameter startParameter) {
		this.startParameter = startParameter;
	}

	/**
	 * @return returns always false.
	 * @throws de.pgalise.simulation.shared.exception.SensorException
	 */
	@Override
	public boolean isActivated(Sensor<?, ?> sensor) {
		/* Nothing to do */
		return false;
	}

	@Override
	protected void onInit(TrafficInitParameter param) throws
		InitializationException {
		this.currentTimestamp = param.getStartTimestamp().getTime();
		this.setInitParameter(param);
		try {
			this.gpsTimeoutStrategy.init(param.getInterval(),
				Integer
				.valueOf(this.properties
					.getProperty("MissedGPSUpdateStepsBeforeTimeout")));
			this.ocWebSocketService
				.sendMessageToAllUsers(new SimulationInitMessage(
						new OCSimulationInitParameter(param
							.getStartTimestamp().getTime(),
							param
							.getEndTimestamp().getTime(),
							param.getInterval(),
							param.getClockGeneratorInterval(),
							param
							.getCityBoundary())));
		} catch (IOException e) {
			log.error("Exception",
				e);
		}
		this.gateMessageStrategy.init(param);
	}

	@Override
	protected void onReset() {
		this.currentTimestamp = this.getInitParameter().getStartTimestamp().
			getTime();
		this.gateMessageStrategy.reset();
	}

	@Override
	protected void onStart(TrafficStartParameter param) {
		this.setStartParameter(param);
		try {

			this.ocWebSocketService.sendMessageToAllUsers(new SimulationStartMessage(
				new OCSimulationStartParameter(
					param.getCity().getName(),
					param.getCity().getPopulation())));
			this.ocSensorStreamController.listenStream(this,
				this.properties.getProperty("InfoSphereIPStaticSensors"),
				Integer.valueOf(this.properties.getProperty(
						"InfoSpherePortStaticSensor")),
				this.properties.getProperty("InfoSphereIPDynamicSensors"),
				Integer.valueOf(this.properties.getProperty(
						"InfoSpherePortDynamicSensor")),
				this.properties.getProperty("InfoSphereIPTopoRadarSensors"),
				Integer.valueOf(this.properties.getProperty(
						"InfoSpherePortTopoRadarSensor")),
				this.properties.getProperty("InfoSphereIPTrafficLightSensors"),
				Integer.valueOf(this.properties.getProperty(
						"InfoSpherePortTrafficLightSensor")));
			this.ocHqfDataStreamController.listenStream(this.properties.getProperty(
				"InfoSphereIPStaticSensors"),
				Integer.valueOf(this.properties.getProperty(
						"InfoSpherePortHQFData")));
		} catch (IOException e) {
			log.error("Exception",
				e);
			try {
				this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(e.
					getLocalizedMessage()));
			} catch (IOException e1) {
				log.error("Exception",
					e1);
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
			log.error("Exception",
				e);
			try {
				this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(
					e.getLocalizedMessage()));
			} catch (IOException e1) {
				log.error("Exception",
					e1);
			}
		}
		this.gateMessageStrategy.stop();
	}

	@Override
	protected void onResume() {
		try {
			this.init(this.getInitParameter());
		} catch (IllegalStateException e) {
			log.warn("see nested exception",
				e);
			try {
				this.ocWebSocketService.sendMessageToAllUsers(new ErrorMessage(
					e.getLocalizedMessage()));
			} catch (IOException e1) {
				log.error("Exception",
					e1);
			}
		}
		this.start(startParameter);
	}

	/**
	 * Creates a vehicle data from a create random vehicle data.
	 *
	 * @param data
	 * @return
	 */
	private Vehicle<?> createRandomVehicleDataToVehicleData(
		CreateRandomVehicleData data) {
		return vehicleFactory.createVehicle(data.getVehicleInformation(),
			output);
	}

	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {
		/* Add new events here: */
		for (Event event : simulationEventList.getEventList()) {
			try {
				if (event.getType().equals(TrafficEventTypeEnum.CREATE_VEHICLES_EVENT)) {
					Collection<Vehicle<?>> vehicleDataCollection = new LinkedList<>();
					log.warn("Create new vehicles");
					for (CreateRandomVehicleData data : ((CreateVehiclesEvent<?>) event)
						.getVehicles()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(
							data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				} else if (event.getType().equals(
					TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT)) {
					log.warn("Create new random vehicles: "
						+ ((CreateRandomVehiclesEvent) event)
						.getCreateRandomVehicleDataList().size());
					Collection<Vehicle<?>> vehicleDataCollection = new LinkedList<>();
					for (CreateRandomVehicleData data
						: ((CreateRandomVehiclesEvent<?>) event)
						.getCreateRandomVehicleDataList()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(
							data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				} else if (event.getType().equals(
					TrafficEventTypeEnum.CREATE_BUSSES_EVENT)) {
					log.warn("Create new random vehicles: " + ((CreateBussesEvent) event)
						.getCreateRandomVehicleDataList().size());
					Collection<Vehicle<?>> vehicleDataCollection = new LinkedList<>();
					for (CreateRandomVehicleData data : ((CreateBussesEvent<?>) event)
						.getCreateRandomVehicleDataList()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(
							data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				} else if (event.getType().equals(
					TrafficEventTypeEnum.DELETE_VEHICLES_EVENT)) {
					Collection<Vehicle<?>> vehicleDataCollection = new LinkedList<>();
					vehicleDataCollection.add(
						((DeleteVehiclesEvent<?>) event).getVehicle());
					this.removeVehicleSensors(vehicleDataCollection);
				} else if (event.getType().equals(
					TrafficEventTypeEnum.ATTRACTION_TRAFFIC_EVENT)) {
					log.warn("Create new random vehicles: "
						+ ((AttractionTrafficEvent) event)
						.getCreateRandomVehicleDataList().size());
					Collection<Vehicle<?>> vehicleDataCollection = new LinkedList<>();
					for (CreateRandomVehicleData data
						: ((AttractionTrafficEvent<?>) event)
						.getCreateRandomVehicleDataList()) {
						vehicleDataCollection.add(this.createRandomVehicleDataToVehicleData(
							data));
					}
					this.createsVehicleSensors(vehicleDataCollection);

				} else {
					log.warn(event.getType().toString());
				}
			} catch (Exception e) {
				log.error("Exception",
					e);
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
	public void createSensors(Set<Sensor<?, ?>> sensors)
		 {
		List<Sensor<?, ?>> newSensors = new LinkedList<>();
		for (Sensor<?, ?> sensor : sensors) {
			this.sensors.add(sensor);
			newSensors.add(sensor);
			Set<Sensor<?, ?>> sensorIDSet = this.sensorTypeIDMap.
				get(sensor.getClass());
			if (sensorIDSet == null) {
				sensorIDSet = new HashSet<>();
				this.sensorTypeIDMap.put(sensor.getClass(),
					sensorIDSet);
			}
			sensorIDSet.add(sensor);
			log.debug("create sensor: " + sensor.getSensorType() + " with id: "
				+ sensor);
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(
				new NewSensorsMessage(newSensors));
		} catch (IOException e) {
			log.error("Exception",
				e);
		}

		Set<GpsSensor> gpsSensors = new HashSet<>();
		for(Sensor sensor : sensors) {
			if(sensor instanceof GpsSensor) {
				gpsSensors.add((GpsSensor)sensor);
			}
		}
		this.gateMessageStrategy.createSensors(gpsSensors);
	}

	@Override
	public void deleteSensors(Set<Sensor<?, ?>> sensors)
		 {
		List<Sensor<?, ?>> sensorIDList = new LinkedList<>();
		for (Sensor<?, ?> sensor : sensors) {
			sensorIDList.add(sensor);
			this.sensors.remove(sensor);
			sensorTypeIDMap.get(sensor.getClass()).remove(
				sensor);
			log.debug("remove sensor: " + sensor.getSensorType() + " with id: "
				+ sensor);
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(
				new RemoveSensorsMessage(sensorIDList));
		} catch (IOException e) {
			log.error("Exception",
				e);
		}
		
		Set<GpsSensor> gpsSensors = new HashSet<>();
		for(Sensor sensor : sensors) {
			if(sensor instanceof GpsSensor) {
				gpsSensors.add((GpsSensor)sensor);
			}
		}
		this.gateMessageStrategy.deleteSensors(gpsSensors);
	}

	@Override
	public void handleGateMessage(GateMessage gateMessage) throws
		UnknownHostException, IOException {
		this.gateMessageStrategy.handleGateMessage(gateMessage.getContent());
	}

	@Override
	public void handleNewUserEvent(OCWebSocketUser user) throws IOException {
		log.debug("handle new user event. Current state is: " + this.getStatus());

		switch (this.getStatus()) {
			case INITIALIZED:
				/* Send init: */
				user.sendMessage(new SimulationInitMessage(
					new OCSimulationInitParameter(this.initParameter.
						getStartTimestamp().getTime(),
						this.initParameter.getEndTimestamp().getTime(),
						this.initParameter.getInterval(),
						this.initParameter.getClockGeneratorInterval(),
						this.initParameter.getCityBoundary())));
				/* Send sensors: */
				user.sendMessage(new NewSensorsMessage(sensors));
				break;
			case STARTED:
				/* Send init: */
				user.sendMessage(new SimulationInitMessage(
					new OCSimulationInitParameter(this.initParameter.
						getStartTimestamp().getTime(),
						this.initParameter.getEndTimestamp().getTime(),
						this.initParameter.getInterval(),
						this.initParameter.getClockGeneratorInterval(),
						this.initParameter.getCityBoundary())));
				/* Send sensors */
				user.sendMessage(new NewSensorsMessage(sensors));

				/* Send vehicles */
				user.sendMessage(new NewVehiclesMessage(vehicleDataMap));

				/* Send start */
				user.sendMessage(new SimulationStartMessage(
					new OCSimulationStartParameter(
						this.startParameter.getCity().getName(),
						this.startParameter.getCity().getPopulation())));
				break;
			default:
				break;
		}
	}

	private void removeVehicleSensors(
		Collection<Vehicle<?>> vehicles) {
		Collection<Sensor<?, ?>> sensorHelperCollection = new LinkedList<>();

		for (Vehicle<?> vehicle : vehicles) {
			Sensor<?, ?> sensor = vehicle.getGpsSensor();
			sensors.add(sensor);
			Set<Sensor<?, ?>> sensorIDSet = this.sensorTypeIDMap.
				get(sensor.getClass());
			if (sensorIDSet != null) {
				sensorIDSet.remove(sensor);
			}
			sensorHelperCollection.add(sensor);
			log.debug("Remove sensor: " + sensor.getSensorType() + " with id: "
				+ sensor);
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(
				new RemoveVehiclesMessage(vehicles));
		} catch (IOException e) {
			log.error("Exception",
				e);
		}

		Set<GpsSensor> gpsSensors = new HashSet<>();
		for(Sensor sensor : sensors) {
			if(sensor instanceof GpsSensor) {
				gpsSensors.add((GpsSensor)sensor);
			}
		}
		this.gateMessageStrategy.deleteSensors(gpsSensors);
	}

	/**
	 * Creates the sensors for vehicles and sends them to the user.
	 *
	 * @param vehicleDataCollection
	 */
	private void createsVehicleSensors(
		Collection<Vehicle<?>> vehicleDataCollection) {
		Collection<Sensor<?, ?>> sensorHelperCollection = new LinkedList<>();

		for (Vehicle<?> vehicleData : vehicleDataCollection) {
			Sensor sensor = vehicleData.getGpsSensor();
			this.sensors.add(sensor);
			Set<Sensor<?, ?>> sensorIDSet = this.sensorTypeIDMap.
				get(sensor.getClass());
			if (sensorIDSet == null) {
				sensorIDSet = new HashSet<>();
				this.sensorTypeIDMap.put(sensor.getClass(),
					sensorIDSet);
			}
			sensorIDSet.add(sensor);
			sensorHelperCollection.add(sensor);
			log.debug("create sensor: " + sensor.getSensorType() + " with id: "
				+ sensor);
			this.vehicleDataMap.add(vehicleData);
		}

		try {
			this.getOcWebSocketService().sendMessageToAllUsers(new NewVehiclesMessage(
				vehicleDataCollection));
		} catch (IOException e) {
			log.error("Exception",
				e);
		}

		Set<GpsSensor> gpsSensors = new HashSet<>();
		for(Sensor sensor : sensors) {
			if(sensor instanceof GpsSensor) {
				gpsSensors.add((GpsSensor)sensor);
			}
		}
		this.gateMessageStrategy.createSensors(gpsSensors);
	}

	@Override
	public String getName() {
		return DefaultOCSimulationController.class.getName();
	}

	@Override
	public void addSimulationEventList(
		EventList<?> simulationEventList) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public long getSimulationTimestamp() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public long getElapsedTime() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public EventInitiator getEventInitiator() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Set<Sensor<?, ?>> getAllManagedSensors() {
		return this.sensors;
	}

	@Override
	public WeatherSensorController getWeatherSensorController() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public EnergySensorController getEnergySensorController() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public TrafficSensorController getTrafficSensorController() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
