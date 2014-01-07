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

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.internal.event.DefaultEventInitiator;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.visualizationcontroller.ServerSideControlCenterController;
import de.pgalise.simulation.visualizationcontroller.ServerSideOperationCenterController;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import javax.ejb.EJB;

/**
 * J-Unit test for {@link DefaultEventInitiator}, which will test if every
 * controller gets it's updates and if the event initiator will start and
 * finish.
 *
 * @author Timo
 */
public class DefaultEventInitiatorTest {

	private static final long INTERVAL = 1000;
	private static final long CLOCK_GENERATOR_INTERVAL = 0;
	@EJB
	private DefaultEventInitiator eventInitiator;
	private static EnergyControllerMock energyController;
	private static WeatherControllerMock weatherController;
	private static TrafficControllerMock trafficController;
	private static ServiceDictionary serviceDictionary;
	private static OperationCenterControllerMock operationCenterController;
	private static long startTimestamp, endTimestamp;
	private static InitParameter initParameter;
	private static StartParameter startParameter;
	private static SimulationController simulationController;
	private static ControlCenterControllerMock controlCenterController;

	@BeforeClass
	public void setUp() {
		Calendar cal = new GregorianCalendar();
		cal.set(2011,
			0,
			0,
			0,
			0,
			0);
		startTimestamp = cal.getTimeInMillis();

		cal.set(2011,
			0,
			0,
			1,
			0,
			0);
		endTimestamp = cal.getTimeInMillis();

		initParameter = new InitParameter(null,
			startTimestamp,
			endTimestamp,
			INTERVAL,
			CLOCK_GENERATOR_INTERVAL,
			"",
			"",
			null,
			null);

		startParameter = new StartParameter(true,
			null);

		energyController = new EnergyControllerMock();
		weatherController = new WeatherControllerMock();
		trafficController = new TrafficControllerMock();
		operationCenterController = new OperationCenterControllerMock();
		simulationController = new SimulationControllerMock();
		controlCenterController = new ControlCenterControllerMock();

		serviceDictionary = EasyMock.createNiceMock(ServiceDictionary.class);
		EasyMock.expect(serviceDictionary.getController(EnergyController.class)).
			andStubReturn(energyController);
		EasyMock.expect(serviceDictionary.getController(WeatherController.class)).
			andStubReturn(weatherController);
		EasyMock.expect(serviceDictionary.getController(TrafficController.class)).
			andStubReturn(trafficController);
		EasyMock.expect(serviceDictionary.getController(SimulationController.class))
			.andStubReturn(simulationController);
		EasyMock.replay(serviceDictionary);
		IdGenerator idGenerator = EasyMock.createNiceMock(IdGenerator.class);

		eventInitiator = new DefaultEventInitiator(idGenerator,
			serviceDictionary,
			operationCenterController,
			controlCenterController,
			null);
		eventInitiator.setOperationCenterController(operationCenterController);
		List<Controller<?, ?, ?>> controllerCollection = new LinkedList<>();
		eventInitiator.setFrontController(controllerCollection);
		eventInitiator.setControlCenterController(controlCenterController);
	}

	@Test
	public void test() throws IllegalStateException, InitializationException, InterruptedException {
		long updateIntervals = ((endTimestamp - startTimestamp) / INTERVAL) + 1;

		// Status test
		assertEquals(StatusEnum.INIT,
			eventInitiator.getStatus());

		eventInitiator.init(initParameter);

		// Status test
		assertEquals(StatusEnum.INITIALIZED,
			eventInitiator.getStatus());

		eventInitiator.start(startParameter);

		// Status test
		assertEquals(StatusEnum.STARTED,
			eventInitiator.getStatus());

		eventInitiator.getEventThread().join();

		// Test all counters
		assertEquals(updateIntervals,
			energyController.getUpdateCounter());
		assertEquals(updateIntervals,
			weatherController.getUpdateCounter());
		assertEquals(updateIntervals,
			trafficController.getUpdateCounter());
		assertEquals(updateIntervals,
			operationCenterController.getUpdateCounter());
		assertEquals(updateIntervals,
			controlCenterController.getUpdateCount());

		eventInitiator.stop();

		// Status test
		assertEquals(StatusEnum.STOPPED,
			eventInitiator.getStatus());

		eventInitiator.reset();

		// Status test
		assertEquals(StatusEnum.INIT,
			eventInitiator.getStatus());
	}

	/**
	 * Operation center controller mock which can count the update steps.
	 *
	 * @author Timo
	 */
	private static class OperationCenterControllerMock implements
		ServerSideOperationCenterController {

		private int updateCounter;

		OperationCenterControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void createSensor(Sensor sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<Sensor<?, ?>> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(Sensor sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<Sensor<?, ?>> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(Sensor sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(InitParameter param) throws IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(StartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {
		}

		@Override
		public void update(EventList<Event> simulationEventList) throws IllegalStateException {
			this.updateCounter++;
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public void displayException(Exception exception) throws IllegalStateException {
		}

		public int getUpdateCounter() {
			return updateCounter;
		}

		@Override
		public String getName() {
			return "OperationCenterController";
		}

		@Override
		public Long getId() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}

	/**
	 * TrafficController mock which can count the update steps.
	 *
	 * @author Timo
	 */
	private static class TrafficControllerMock implements
		TrafficController<TrafficEvent> {

		private int updateCounter;

		TrafficControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void createSensor(StaticSensor sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<StaticSensor> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(StaticSensor sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<StaticSensor> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(StaticSensor sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(TrafficInitParameter param) throws IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(InfrastructureStartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {
		}

		@Override
		public void update(EventList<TrafficEvent> simulationEventList) throws IllegalStateException {
			this.updateCounter++;
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		public int getUpdateCounter() {
			return updateCounter;
		}

		@Override
		public String getName() {
			return "TrafficController";
		}

		@Override
		public Long getId() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}

	/**
	 * WeatherController mock which can count the update steps.
	 *
	 * @author Timo
	 */
	private static class WeatherControllerMock implements WeatherController {

		private int updateCounter;

		WeatherControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void init(InitParameter param) throws IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(InfrastructureStartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {
		}

		@Override
		public void update(EventList<WeatherEvent> simulationEventList) throws IllegalStateException {
			this.updateCounter++;
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public void checkDate(long timestamp) throws IllegalArgumentException {
		}

		@Override
		public JaxRSCoordinate getReferencePosition() {
			return null;
		}

		@Override
		public Number getValue(WeatherParameterEnum key,
			long timestamp,
			JaxRSCoordinate position) {
			return null;
		}

		public int getUpdateCounter() {
			return updateCounter;
		}

		@Override
		public String getName() {
			return "EventInitiator";
		}
	}

	/**
	 * EnergyController mock which can count the update steps.
	 *
	 * @author Timo
	 */
	private static class EnergyControllerMock implements EnergyController {

		private int updateCounter;

		EnergyControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void init(TrafficInitParameter param) throws IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(InfrastructureStartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {
		}

		@Override
		public void update(EventList<EnergyEvent> simulationEventList) throws IllegalStateException {
			this.updateCounter++;
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public double getEnergyConsumptionInKWh(long timestamp,
			JaxRSCoordinate position,
			int measureRadiusInMeter) {
			return 0;
		}

		public int getUpdateCounter() {
			return updateCounter;
		}

		@Override
		public String getName() {
			return "EnergyController";
		}
	}

	/**
	 * Mock for testing.
	 *
	 * @author Timo
	 */
	private static class SimulationControllerMock implements SimulationController {

		SimulationControllerMock() {

		}

		@Override
		public void createSensor(Sensor sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<Sensor<?, ?>> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(Sensor sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<Sensor<?, ?>> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(Sensor sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(TrafficInitParameter param) throws IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(InfrastructureStartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {

		}

		@Override
		public void update(EventList<Event> simulationEventList) throws IllegalStateException {
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public void addSimulationEventList(EventList<?> simulationEventList) {
		}

		@Override
		public long getSimulationTimestamp() {
			return 0;
		}

		@Override
		public EventInitiator getEventInitiator() {
			return null;
		}

		@Override
		public String getName() {
			return "SimulationController";
		}

		@Override
		public Long getId() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public long getElapsedTime() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}

	/**
	 * Mock for the controll center controller.
	 *
	 * @author Timo
	 */
	private static class ControlCenterControllerMock implements
		ServerSideControlCenterController {

		private int updateCount;

		/**
		 * Constructor
		 */
		ControlCenterControllerMock() {
			this.updateCount = 0;
		}

		@Override
		public void init(InitParameter param) throws
			IllegalStateException {
		}

		@Override
		public void reset() throws IllegalStateException {
		}

		@Override
		public void start(StartParameter param) throws IllegalStateException {
		}

		@Override
		public void stop() throws IllegalStateException {
		}

		@Override
		public void update(EventList<Event> simulationEventList)
			throws IllegalStateException {
			this.updateCount++;
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public void displayException(Exception exception)
			throws IllegalStateException {
		}

		public int getUpdateCount() {
			return updateCount;
		}
	}
}
