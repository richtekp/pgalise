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
 
package de.pgalise.simulation.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.SimulationControllerLocal;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.service.SensorManagerController;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.NoValidControllerForSensorException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.ControlCenterControllerLoader;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterControllerLoader;
import de.pgalise.util.generic.MutableBoolean;
import javax.persistence.EntityManager;

/**
 * The default implementation of the simulation controller inits, starts, stops
 * and resets all the other {@link Controller}. The {@link SensorManagerController#createSensor(SensorHelper)} methods
 * are called for every known {@link SensorManagerController} until one of them does not response with an {@link SensorException}.
 * To update the simulation, it uses the set {@link EventInitiator}. New events can be added via {@link SimulationController#addSimulationEventList(SimulationEventList)}
 * and will be handled by the {@link EventInitiator}.
 * @author Jens
 * @author Kamil
 * @author Timo
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.SimulationController")
@Remote(SimulationController.class)
@Local(SimulationControllerLocal.class)
public class DefaultSimulationController extends AbstractController<Event> implements SimulationControllerLocal {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultSimulationController.class);
	private static final String NAME = "SimulationController";

	/**
	 * EventInitiator
	 */
	@EJB
	private EventInitiator eventInitiator;

	private EntityManager sensorPersistenceService;

	@EJB
	private ControlCenterControllerLoader controlCenterControllerLoader;
	
	@EJB
	private OperationCenterControllerLoader operationCenterControllerLoader;
	
	private OperationCenterController operationCenterController;

	private ControlCenterController controlCenterController;
	
	@EJB
	private ServiceDictionary serviceDictionary;

	@EJB
	private ServerConfigurationReader<Controller<?>> serverConfigReader;
	
	@EJB
	private ConfigReader configReader;

	/**
	 * Start parameter
	 */
	private StartParameter startParameter;
	
	private List<Controller<?>> frontControllerList;

	/**
	 * Default constructor
	 */
	public DefaultSimulationController() {
		this.frontControllerList = new LinkedList<>();
	}
	
	/**
	 * Automatically called on post construct
	 */
	@PostConstruct
	public void onPostContruct() {
		this.controlCenterController = this.controlCenterControllerLoader.loadControlCenterController();
		this.operationCenterController = this.operationCenterControllerLoader.loadOperationCenterController();
	}

	@Override
	public void createSensor(SensorHelper sensor) throws SensorException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof SensorManagerController && !(c instanceof SimulationController)) {
				try {
					((SensorManagerController) c).createSensor(sensor);
					this.operationCenterController.createSensor(sensor);
					return;
				} catch (SensorException e) {
					log.error(e.getLocalizedMessage(),e );
				}
			}
		}
		
		for(Service c : this.serviceDictionary.getControllers()) {
			if(c instanceof Controller) {
				((Controller)c).reset();
			}
		}
		
		throw new NoValidControllerForSensorException(String.format(
				"Can't create sensor for sensor id: %d sensortype: %s because no suitable controller was found!",
				sensor.getSensorID(), sensor.getSensorType().toString()));
	}

	@Override
	public void deleteSensor(SensorHelper sensor) throws SensorException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof SensorManagerController && !(c instanceof SimulationController)) {
				try {
					((SensorManagerController) c).deleteSensor(sensor);
					this.operationCenterController.deleteSensor(sensor);
					return;
				} catch (SensorException e) {
					log.error(e.getLocalizedMessage(), e);
				}
			}
		}		
		
		throw new NoValidControllerForSensorException(String.format(
				"Can't delete sensor for sensor id: %d sensortype: %s because no suitable controller was found!",
				sensor.getSensorID(), sensor.getSensorType().toString()));
	}

	public OperationCenterController getOperationCenterController() {
		return this.operationCenterController;
	}

	/**
	 * Determine the status of a sensor
	 * 
	 * @param sensor
	 *            sensor to get information from
	 * @return status of the sensor (false if there is no sensor or controller)
	 * @throws SensorException
	 */
	@Override
	public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
		if (sensor == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensor"));
		}

		for (Service c : this.serviceDictionary.getControllers()) {
			if (c instanceof SensorManagerController && !(c instanceof SimulationController)) {
				try {
					return ((SensorManagerController) c).statusOfSensor(sensor);
				} catch (Exception e) {
					log.error(e.getLocalizedMessage(), e);
				}
			}
		}
		
		throw new NoValidControllerForSensorException(
				String.format(
						"Can't find status of sensor for sensor id: %d sensortype: %s because no suitable controller was found!",
						sensor.getSensorID(), sensor.getSensorType().toString()));
	}

	@Override
	protected void onInit(final InitParameter param) throws InitializationException {
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
				new ServiceHandler<Controller<?>>() {
					@Override
					public String getName() {
						return ServiceDictionary.FRONT_CONTROLLER;
					}
		
					@Override
					public void handle(String server, Controller service) {
						log.info(String.format("Using %s on server %s", getName(), server));
						if (!(service instanceof SimulationController)) {
							try {
								if (service.getStatus() != StatusEnum.INIT) {
									service.reset();
								}
									service.init(param);
							}
							catch (IllegalStateException | InitializationException e) {
								exception.setValue(false);
								log.error("Could not inititialize FrontController", e);
							}
						}
						frontControllerList.add(service);
					}
				});
		
		if (exception.getValue()) {
			throw new InitializationException("An error occured during the initialization of the front controllers");
		}

		Collection<Service> controllers = this.serviceDictionary.getControllers();
		log.debug(controllers.size() +" Controller referenced by the ServiceDictionary");
		// init controller
		for (Service c : controllers) {
			if (!(c instanceof SimulationController)) {
				if(c instanceof Controller)  {
					((Controller)c).init(param);
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
				if(c instanceof Controller)  {
					((Controller)c).reset();
				}
			}
		}
		
		for(Controller<?> c: frontControllerList) {
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
	protected void onStart(StartParameter param) {
		this.startParameter = param;

		// start the controllers
		for (Service c : this.serviceDictionary.getControllers()) {
			if (!(c instanceof SimulationController)) {
				if(c instanceof Controller)  {
					((Controller)c).start(param);
				}
			}

		}

		for(Controller<?> c: frontControllerList) {
			if (!(c instanceof SimulationController)) {
				c.start(param);
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
				if(c instanceof Controller)  {
					((Controller)c).stop();
				}
			}
		}
		
		for(Controller<?> c: frontControllerList) {
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
				if(c instanceof Controller) {
					((Controller)c).start(this.startParameter);
				}
			}
		}
		
		for(Controller<?> c: frontControllerList) {
			if (!(c instanceof SimulationController)) {
				c.start(this.startParameter);
			}
		}


		// start the event initiator
		this.eventInitiator.start(this.startParameter);

		// start the operation center
		this.operationCenterController.start(this.startParameter);
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

	@Override
	public void setOperationCenterController(OperationCenterController operationCenterController) {
		this.operationCenterController = operationCenterController;
	}

	@Override
	public EventInitiator getEventInitiator() {
		return this.eventInitiator;
	}

	@Override
	public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.deleteSensor(sensor);
		}
	}

	/**
	 * Only for J-Unit tests.
	 * 
	 * @param serviceDictionary
	 */
	public void _setServiceDictionary(ServiceDictionary serviceDictionary) {
		this.serviceDictionary = serviceDictionary;
	}

	/**
	 * Only for J-Unit tests.
	 * 
	 * @param eventInitiator
	 */
	public void _setEventInitiator(EventInitiator eventInitiator) {
		this.eventInitiator = eventInitiator;
	}

	/**
	 * Only for J-Unit tests.
	 * 
	 * @param sensorPersistenceService
	 */
	public void _setSensorPersistenceService(EntityManager sensorPersistenceService) {
		this.sensorPersistenceService = sensorPersistenceService;
	}

	/**
	 * Only for J-Unit tests.
	 * 
	 * @param serverConfigurationReader
	 */
	public void _setServerConfigurationReader(ServerConfigurationReader<Controller<?>> serverConfigurationReader) {
		this.serverConfigReader = serverConfigurationReader;
	}

	@Override
	public void setControlCenterController(
			ControlCenterController controlCenterController) {
		this.controlCenterController = controlCenterController;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
