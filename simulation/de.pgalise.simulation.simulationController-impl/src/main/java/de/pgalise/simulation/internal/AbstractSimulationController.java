/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.internal;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.SimulationControllerLocal;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.NoValidControllerForSensorException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.visualizationcontroller.ServerSideControlCenterController;
import de.pgalise.simulation.visualizationcontroller.ServerSideOperationCenterController;
import de.pgalise.util.generic.MutableBoolean;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public abstract class AbstractSimulationController extends AbstractController<Event, InfrastructureStartParameter, TrafficInitParameter>
	implements SimulationControllerLocal {

	private static final String NAME = "SimulationController";
	private static final long serialVersionUID = 1L;
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		AbstractSimulationController.class);

	/**
	 * EventInitiator
	 */
	@EJB
	private EventInitiator eventInitiator;
	private EntityManager sensorPersistenceService;
	@EJB
	private ServerSideOperationCenterController operationCenterController;
	@EJB
	private ServerSideControlCenterController controlCenterController;
	@EJB
	private ServiceDictionary serviceDictionary;
	@EJB
	private ServerConfigurationReader serverConfigReader;
	@EJB
	private ConfigReader configReader;
	/**
	 * Start parameter
	 */
	private StartParameter startParameter;
	private List<Controller<?, ?, ?>> frontControllerList;
	private long elapsedTime = 0;
	private long lastElapsedTimeCheckTimestamp;

	/**
	 * Default constructor
	 */
	public AbstractSimulationController() {
		this.frontControllerList = new LinkedList<>();
	}

	/**
	 * Automatically called on post construct
	 */
	@PostConstruct
	public void onPostContruct() {
	}

	@Override
	public void createSensor(Sensor<?, ?> sensor) throws SensorException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"sensor"));
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof SensorManagerController && !(c instanceof SimulationController)) {
				try {
					((SensorManagerController) c).createSensor(sensor);
					this.operationCenterController.createSensor(sensor);
					return;
				} catch (SensorException e) {
					log.error(e.getLocalizedMessage(),
						e);
				}
			}
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof Controller) {
				((Controller) c).reset();
			}
		}

		throw new NoValidControllerForSensorException(String.format(
			"Can't create sensor for sensor id: %d sensortype: %s because no suitable controller was found!",
			sensor.getId(),
			sensor.getSensorType().toString()));
	}

	@Override
	public void deleteSensor(Sensor sensor) throws SensorException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"sensor"));
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof SensorManagerController && !(c instanceof SimulationController)) {
				try {
					((SensorManagerController) c).deleteSensor(sensor);
					this.operationCenterController.deleteSensor(sensor);
					return;
				} catch (SensorException e) {
					log.error(e.getLocalizedMessage(),
						e);
				}
			}
		}

		throw new NoValidControllerForSensorException(String.format(
			"Can't delete sensor for sensor id: %d sensortype: %s because no suitable controller was found!",
			sensor.getId(),
			sensor.getSensorType().toString()));
	}

	public ServerSideOperationCenterController getOperationCenterController() {
		return this.operationCenterController;
	}

	/**
	 * Determine the status of a sensor
	 *
	 * @param sensor sensor to get information from
	 * @return status of the sensor (false if there is no sensor or controller)
	 * @throws SensorException
	 */
	@Override
	public boolean statusOfSensor(Sensor sensor) throws SensorException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"sensor"));
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof SensorManagerController && !(c instanceof SimulationController)) {
				try {
					return ((SensorManagerController) c).statusOfSensor(sensor);
				} catch (Exception e) {
					log.error(e.getLocalizedMessage(),
						e);
				}
			}
		}

		throw new NoValidControllerForSensorException(
			String.format(
				"Can't find status of sensor for sensor id: %d sensortype: %s because no suitable controller was found!",
				sensor.getId(),
				sensor.getSensorType().toString()));
	}

	@Override
	protected void onInit(final TrafficInitParameter param) throws InitializationException {
		// init the operation center
		this.operationCenterController.init(param);

		// init the control center
		this.controlCenterController.init(param);

		// Delete all Sensors from database
		this.sensorPersistenceService.clear();

		final MutableBoolean exception = new MutableBoolean();
		exception.setValue(false);

		serverConfigReader.read(
			param.getServerConfiguration(),
			new ServiceHandler<Controller<Event, StartParameter, InitParameter>>() {
				@Override
				public String getName() {
					return ServiceDictionary.FRONT_CONTROLLER;
				}

				@Override
				public void handle(String server,
					Controller service) {
					log.info(String.format("Using %s on server %s",
							getName(),
							server));
					if (!(service instanceof SimulationController)) {
						try {
							if (service.getStatus() != StatusEnum.INIT) {
								service.reset();
							}
							service.init(param);
						} catch (IllegalStateException e) {
							exception.setValue(false);
							log.error("Could not inititialize FrontController",
								e);
						}
					}
					frontControllerList.add(service);
				}
			});

		if (exception.getValue()) {
			throw new InitializationException(
				"An error occured during the initialization of the front controllers");
		}

		Collection<Service> controllers = this.serviceDictionary.getControllers();
		log.debug(
			controllers.size() + " Controller referenced by the ServiceDictionary");
		// init controller
		for (Service c : controllers) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller) c).init(param);
				}
			}
		}

		// init the event initiator
		this.eventInitiator.setFrontController(frontControllerList);
		this.eventInitiator.init(param);
	}

	@Override
	protected void onReset() {
		this.frontControllerList.clear();

		// reset controller
		for (Service c : this.serviceDictionary.getControllers()) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller) c).reset();
				}
			}
		}

		for (Controller<?, ?, ?> c : frontControllerList) {
			if (!(c instanceof SimulationController)) {
				c.reset();
			}
		}

		// reset the event initiator
		this.eventInitiator.reset();

		// reset the operation center
		this.operationCenterController.reset();
		this.controlCenterController.reset();
	}

	@Override
	protected void onStart(InfrastructureStartParameter param) {
		this.startParameter = param;

		// start the controllers
		for (Service c : this.serviceDictionary.getControllers()) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller<?, StartParameter, ?>) c).start(param);
				}
			}

		}

		for (Controller<?, ?, ?> c : frontControllerList) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller<?, StartParameter, ?>) c).start(param);
				}
			}
		}

		// start the operation center
		this.operationCenterController.start(param);
		this.controlCenterController.start(param);

		// start the event initiator
		this.eventInitiator.start(param);
	}

	@Override
	protected void onStop() {
		// stop the event initiator
		if (this.eventInitiator.getStatus() == StatusEnum.STARTED) {
			this.eventInitiator.stop();
		}

		// stop the operation center
		this.operationCenterController.stop();

		// stop the control center
		this.controlCenterController.stop();

		// stop all controllers (without this controller type)
		for (Service c : this.serviceDictionary.getControllers()) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller) c).stop();
				}
			}
		}

		for (Controller<?, ?, ?> c : frontControllerList) {
			if (!(c instanceof SimulationController)) {
				c.stop();
			}
		}
	}

	@Override
	protected void onResume() {
		// start the controllers
		for (Service c : this.serviceDictionary.getControllers()) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller) c).start(this.startParameter);
				}
			}
		}

		for (Controller<?, ?, ?> c : frontControllerList) {
			if (!(c instanceof SimulationController)) {
				if (c instanceof Controller) {
					((Controller) c).start(this.startParameter);
				}
			}
		}

		// start the event initiator
		this.eventInitiator.start(this.startParameter);

		// start the operation center
		this.operationCenterController.start(this.startParameter);
	}

	@Override
	public long getElapsedTime() {
		long timestamp = System.currentTimeMillis();
		elapsedTime += timestamp - lastElapsedTimeCheckTimestamp;
		lastElapsedTimeCheckTimestamp = timestamp;
		return elapsedTime;
	}

	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {
		this.eventInitiator.addSimulationEventList(simulationEventList);
	}

	@Override
	public void addSimulationEventList(EventList<?> simulationEventList) {
		this.eventInitiator.addSimulationEventList(simulationEventList);
	}

	@Override
	public long getSimulationTimestamp() {
		return this.eventInitiator.getCurrentTimestamp();
	}

	protected void setOperationCenterController(
		ServerSideOperationCenterController operationCenterController) {
		this.operationCenterController = operationCenterController;
	}

	@Override
	public EventInitiator getEventInitiator() {
		return this.eventInitiator;
	}

	@Override
	public void createSensors(Collection<Sensor<?, ?>> sensors) throws SensorException {
		for (Sensor<?, ?> sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensors(Collection<Sensor<?, ?>> sensors) throws SensorException {
		for (Sensor<?, ?> sensor : sensors) {
			this.deleteSensor(sensor);
		}
	}

	/**
	 * Only for J-Unit tests.
	 *
	 * @param serviceDictionary
	 */
	protected void setServiceDictionary(ServiceDictionary serviceDictionary) {
		this.serviceDictionary = serviceDictionary;
	}

	/**
	 * Only for J-Unit tests.
	 *
	 * @param eventInitiator
	 */
	protected void setEventInitiator(EventInitiator eventInitiator) {
		this.eventInitiator = eventInitiator;
	}

	/**
	 * Only for J-Unit tests.
	 *
	 * @param sensorPersistenceService
	 */
	protected void setSensorPersistenceService(
		EntityManager sensorPersistenceService) {
		this.sensorPersistenceService = sensorPersistenceService;
	}

	/**
	 * Only for J-Unit tests.
	 *
	 * @param serverConfigurationReader
	 */
	protected void setServerConfigurationReader(
		ServerConfigurationReader serverConfigurationReader) {
		this.serverConfigReader = serverConfigurationReader;
	}

	protected void setControlCenterController(
		ServerSideControlCenterController controlCenterController) {
		this.controlCenterController = controlCenterController;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
