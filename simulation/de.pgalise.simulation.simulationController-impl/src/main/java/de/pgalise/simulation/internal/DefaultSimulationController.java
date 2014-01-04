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
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.SimulationControllerLocal;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
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
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.visualizationcontroller.ServerSideControlCenterController;
import de.pgalise.simulation.visualizationcontroller.ServerSideOperationCenterController;
import de.pgalise.util.generic.MutableBoolean;
import javax.persistence.EntityManager;

/**
 * The default implementation of the simulation controller inits, starts, stops
 * and resets all the other {@link Controller}. The
 * {@link SensorManagerController#createSensor(Sensor)} methods are called for
 * every known {@link SensorManagerController} until one of them does not
 * response with an {@link SensorException}. To update the simulation, it uses
 * the set {@link EventInitiator}. New events can be added via
 * {@link SimulationController#addSimulationEventList(SimulationEventList)} and
 * will be handled by the {@link EventInitiator}.
 *
 * @author Jens
 * @author Kamil
 * @author Timo
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.SimulationController")
@Local(SimulationControllerLocal.class)
public class DefaultSimulationController extends AbstractSimulationController
	implements SimulationControllerLocal {
	private static final long serialVersionUID = 1L;

	public DefaultSimulationController() {
	}
}
