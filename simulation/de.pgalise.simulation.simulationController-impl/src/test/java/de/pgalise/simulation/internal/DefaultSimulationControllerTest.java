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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.InfrastructureInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficInfrastructureData;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.Anemometer;
import javax.persistence.EntityManager;
import org.junit.Ignore;

/**
 * JUnit tests for {@link DefaultSimulationController}<br />
 * <br />
 * Tests if the states, create, delete sensors and the updates will be passed
 * down to the other controllers correctly.
 *
 * @author Timo
 */
@Ignore //@EasyMock setup stimmt noch nicht
public class DefaultSimulationControllerTest {

	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
	private static final long START_TIMESTAMP = 0;
	private static final long END_TIMESTAMP = 100000;
	private static final long INTERVAL = 1000;
	private static final long CLOCK_GENERATOR_INTERVAL = 1000;
	private static DefaultSimulationController testClass;
	private static InfrastructureInitParameter initParameter;
	private static InfrastructureStartParameter startParameter;
//	private static EventInitiatorMock eventInitiator;
	private static ServiceDictionary serviceDictionary;
	private static OperationCenterController operationCenterController;
	private static ControlCenterController controlCenterController;
	private static TrafficController<?> trafficController;
	private static EnergyController energyController;
	private static WeatherController weatherController;
	private static EntityManager sensorPersistenceService;
	private static ServerConfigurationReader serverConfigurationReader;
	private static TrafficInfrastructureData cityInfrastructureData;
	private static ServerConfiguration serverConfiguration;

	@BeforeClass
	public static void setUp() {
		testClass = new DefaultSimulationController();

		/* Mock all controllers: */
		operationCenterController = EasyMock.createNiceMock(
			OperationCenterController.class);
		controlCenterController = EasyMock.createNiceMock(
			ControlCenterController.class);
		trafficController = EasyMock.createNiceMock(TrafficController.class);
		energyController = EasyMock.createNiceMock(EnergyController.class);
		weatherController = EasyMock.createNiceMock(WeatherController.class);

		/* Mock all other services: */
		sensorPersistenceService = EasyMock.createNiceMock(EntityManager.class);
		serverConfigurationReader = EasyMock.createNiceMock(
			ServerConfigurationReader.class);
		EventInitiator eventInitiator = EasyMock.
			createNiceMock(EventInitiator.class);
		cityInfrastructureData = EasyMock.createNiceMock(
			TrafficInfrastructureData.class);

		/* Prepare service dictionary: */
		serviceDictionary = EasyMock.createNiceMock(ServiceDictionary.class);
		Collection<Service> controllerCollection = new LinkedList<>();
		controllerCollection.add(energyController);
		controllerCollection.add(trafficController);
		controllerCollection.add(weatherController);
		controllerCollection.add(testClass);
		EasyMock.expect(serviceDictionary.getControllers()).andStubReturn(
			controllerCollection);
		EasyMock.expect(serviceDictionary.getController(TrafficController.class)).
			andStubReturn(trafficController);
		EasyMock.expect(serviceDictionary.getController(EnergyController.class)).
			andStubReturn(energyController);
		EasyMock.expect(serviceDictionary.getController(WeatherController.class)).
			andStubReturn(weatherController);
		EasyMock.expect(serviceDictionary.getController(SimulationController.class)).
			andStubReturn(testClass);
		EasyMock.replay(serviceDictionary);

		testClass._setEventInitiator(eventInitiator);
		testClass._setServiceDictionary(serviceDictionary);
		testClass.setOperationCenterController(operationCenterController);
		testClass._setSensorPersistenceService(sensorPersistenceService);
		testClass._setServerConfigurationReader(serverConfigurationReader);
		testClass.setControlCenterController(controlCenterController);

		serverConfiguration = new ServerConfiguration();

		initParameter = new InfrastructureInitParameter(cityInfrastructureData,
			serverConfiguration,
			START_TIMESTAMP,
			END_TIMESTAMP,
			INTERVAL,
			CLOCK_GENERATOR_INTERVAL,
			"",
			"",
			new TrafficFuzzyData(0,
			0.9,
			1),
			new Boundary(
			new Coordinate(),
			new Coordinate()));
	}

	/**
	 * Sets state behavior for the mocks
	 *
	 * @throws InitializationException
	 * @throws IllegalStateException
	 */
	private static void initControllerMockStateBehavior() throws IllegalStateException, InitializationException {
		operationCenterController.init(initParameter);
		operationCenterController.start(startParameter);
		operationCenterController.stop();
		operationCenterController.reset();
		EasyMock.replay(operationCenterController);

		trafficController.init(initParameter);
		trafficController.start(startParameter);
		trafficController.stop();
		trafficController.reset();
		EasyMock.replay(trafficController);

		weatherController.init(initParameter);
		weatherController.start(startParameter);
		weatherController.stop();
		weatherController.reset();
		EasyMock.replay(weatherController);

		energyController.init(initParameter);
		energyController.start(startParameter);
		energyController.stop();
		energyController.reset();
		EasyMock.replay(energyController);
	}

	/**
	 * Resets the behavior for the controller mocks.
	 */
	private static void resetControllerMockBehavior() {
		EasyMock.reset(operationCenterController);
		EasyMock.reset(trafficController);
		EasyMock.reset(weatherController);
		EasyMock.reset(energyController);
	}

	@Test
	public void testStates() throws IllegalStateException, InitializationException {
		resetControllerMockBehavior();

		/* First start: */
		initControllerMockStateBehavior();
		testClass.init(initParameter);
		testClass.start(startParameter);
		testClass.stop();
		testClass.reset();

		/* Verify all controllers: */
		EasyMock.verify(operationCenterController);
		EasyMock.verify(trafficController);
		EasyMock.verify(weatherController);
		EasyMock.verify(energyController);

		/* Second start: */
		resetControllerMockBehavior();
		initControllerMockStateBehavior();
		testClass.init(initParameter);
		testClass.start(startParameter);
		testClass.stop();
		testClass.reset();

		/* Verify all controllers: */
		EasyMock.verify(operationCenterController);
		EasyMock.verify(trafficController);
		EasyMock.verify(weatherController);
		EasyMock.verify(energyController);
	}

	/**
	 * Tests the create sensor method.
	 *
	 * @throws SensorException
	 * @throws InitializationException
	 * @throws IllegalStateException
	 */
	@Test
	public void testCreateSensor() throws SensorException, IllegalStateException, InitializationException {
		resetControllerMockBehavior();
		Sensor<?, ?> sensor = null;
		StaticSensor sensorHelperStaticSensor = new Anemometer(null,
			null,
			weatherController,
			null);
		StaticSensor sensorHelperTrafficSensor = new InductionLoopSensor(
			null,
			null,
			null);

		/* The operation center will receive both sensors: */
		operationCenterController.createSensor(sensorHelperStaticSensor);
		operationCenterController.createSensor(sensorHelperTrafficSensor);
		EasyMock.replay(operationCenterController);

		/* The static sensor controller will receive only the static sensor: */
		EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();

		/* The traffic controller will receive only the traffic sensor: */
		trafficController.createSensor(sensorHelperTrafficSensor);
		trafficController.createSensor(sensorHelperStaticSensor);
		EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
		EasyMock.replay(trafficController);

		testClass.init(initParameter);
		testClass.start(startParameter);
		testClass.createSensor(sensorHelperStaticSensor);
		testClass.createSensor(sensorHelperTrafficSensor);

		EasyMock.verify(operationCenterController);
		EasyMock.verify(trafficController);

		testClass.stop();
		testClass.reset();
	}

	/**
	 * Tests the create sensors method.
	 *
	 * @throws SensorException
	 * @throws InitializationException
	 * @throws IllegalStateException
	 */
	@Test
	public void testCreateSensors() throws SensorException, IllegalStateException, InitializationException {
		resetControllerMockBehavior();
		Sensor<?, ?> sensor = null;
		StaticSensor sensorHelperStaticSensor = new Anemometer(null,
			null,
			weatherController,
			null);
		StaticSensor sensorHelperTrafficSensor = new InductionLoopSensor(null,
			null,
			null);
		List<Sensor<?, ?>> sensorHelperList = new LinkedList<>();
		sensorHelperList.add(sensorHelperStaticSensor);
		sensorHelperList.add(sensorHelperTrafficSensor);

		/* The operation center will receive both sensors: */
		operationCenterController.createSensor(sensorHelperStaticSensor);
		operationCenterController.createSensor(sensorHelperTrafficSensor);
		EasyMock.replay(operationCenterController);

		/* The static sensor controller will receive only the static sensor: */

		/* The traffic controller will receive only the traffic sensor: */
		trafficController.createSensor(sensorHelperTrafficSensor);
		trafficController.createSensor(sensorHelperStaticSensor);
		EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
		EasyMock.replay(trafficController);

		testClass.init(initParameter);
		testClass.start(startParameter);
		testClass.createSensors(sensorHelperList);

		EasyMock.verify(operationCenterController);
		EasyMock.verify(trafficController);

		testClass.stop();
		testClass.reset();
	}

	/**
	 * Tests the delete sensor method.
	 *
	 * @throws SensorException
	 * @throws InitializationException
	 * @throws IllegalStateException
	 */
	@Test
	public void testDeleteSensor() throws SensorException, IllegalStateException, InitializationException {
		resetControllerMockBehavior();
		Sensor<?, ?> sensor1 = null, sensor2 = null;
		StaticSensor sensorHelperStaticSensor = new Anemometer(null,
			null,
			weatherController,
			null);
		StaticSensor sensorHelperTrafficSensor = new InductionLoopSensor(null,
			null,
			null);

		/* The operation center will receive both sensors: */
		operationCenterController.deleteSensor(sensorHelperStaticSensor);
		operationCenterController.deleteSensor(sensorHelperTrafficSensor);
		EasyMock.replay(operationCenterController);

		/* The static sensor controller will receive only the static sensor: */

		/* The traffic controller will receive only the traffic sensor: */
		trafficController.deleteSensor(sensorHelperTrafficSensor);
		trafficController.deleteSensor(sensorHelperStaticSensor);
		EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
		EasyMock.replay(trafficController);

		testClass.init(initParameter);
		testClass.start(startParameter);
		testClass.deleteSensor(sensorHelperStaticSensor);
		testClass.deleteSensor(sensorHelperTrafficSensor);

		EasyMock.verify(operationCenterController);
		EasyMock.verify(trafficController);

		testClass.stop();
		testClass.reset();
	}

	/**
	 * Tests the delete sensors method.
	 *
	 * @throws SensorException
	 * @throws InitializationException
	 * @throws IllegalStateException
	 */
	@Test
	public void testDeleteSensors() throws SensorException, IllegalStateException, InitializationException {
		resetControllerMockBehavior();
		Sensor<?, ?> sensor1 = null, sensor2 = null;
		StaticSensor sensorHelperStaticSensor = new Anemometer(null,
			null,
			weatherController,
			null);
		StaticSensor sensorHelperTrafficSensor = new InductionLoopSensor(null,
			null,
			null);
		List<Sensor<?, ?>> sensorHelperList = new LinkedList<>();
		sensorHelperList.add(sensorHelperStaticSensor);
		sensorHelperList.add(sensorHelperTrafficSensor);

		/* The operation center will receive both sensors: */
		operationCenterController.deleteSensor(sensorHelperStaticSensor);
		operationCenterController.deleteSensor(sensorHelperTrafficSensor);
		EasyMock.replay(operationCenterController);

		/* The static sensor controller will receive only the static sensor: */

		/* The traffic controller will receive only the traffic sensor: */
		trafficController.deleteSensor(sensorHelperTrafficSensor);
		trafficController.deleteSensor(sensorHelperStaticSensor);
		EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
		EasyMock.replay(trafficController);

		testClass.init(initParameter);
		testClass.start(startParameter);
		testClass.deleteSensors(sensorHelperList);

		EasyMock.verify(operationCenterController);
		EasyMock.verify(trafficController);

		testClass.stop();
		testClass.reset();
	}

	/**
	 * Tests the update method.
	 *
	 * @throws InitializationException
	 * @throws IllegalStateException
	 */
	@Test
	public void testUpdate() throws IllegalStateException, InitializationException {
		testClass.init(initParameter);
		testClass.start(startParameter);

		EventInitiator eventInitiatorMock = EasyMock.createNiceMock(
			EventInitiator.class);
		EventList<Event> testSimulationEventList = new EventList<>(
			new LinkedList<Event>(),
			0,
			UUID.randomUUID());

		testClass.update(testSimulationEventList);

		assertEquals(testSimulationEventList,
			eventInitiatorMock.getEventList());

		testClass.stop();
		testClass.reset();
	}
//	/**
//	 * Event initiator mock for this test. It will save the last received simulationeventlist in simulationEventList.
//	 * 
//	 * @author Timo
//	 */
//	private static class EventInitiatorMock extends AbstractController implements EventInitiator {
//
//		private EventList simulationEventList;
//
//		@Override
//		public Thread getEventThread() {
//			return null;
//		}
//
//		@Override
//		public void addSimulationEventList(EventList simulationEventList) {
//			this.simulationEventList = simulationEventList;
//		}
//
//		@Override
//		public long getCurrentTimestamp() {
//			return 0;
//		}
//
//		@Override
//		public void setOperationCenterController(OperationCenterController operationCenterController) {
//		}
//
//		@Override
//		protected void onInit(InitParameter param) throws InitializationException {
//		}
//
//		@Override
//		protected void onReset() {
//		}
//
//		@Override
//		protected void onStart(StartParameter param) {
//		}
//
//		@Override
//		protected void onStop() {
//		}
//
//		@Override
//		protected void onResume() {
//		}
//
//		@Override
//		protected void onUpdate(EventList simulationEventList) {
//			this.simulationEventList = simulationEventList;
//		}
//
//		public EventList getSimulationEventList() {
//			return simulationEventList;
//		}
//
//		@Override
//		public void setControlCenterController(ControlCenterController controlCenterController) {
//		}
//
//		@Override
//		public String getName() {
//			return "EventInitiator";
//		}
//
//		@Override
//		public void setFrontController(List<Controller> controller) {
//
//		}
//
//	}
}
