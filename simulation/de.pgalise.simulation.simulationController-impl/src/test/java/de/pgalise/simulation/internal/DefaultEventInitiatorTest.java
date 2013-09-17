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
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.staticsensor.StaticSensorController;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * J-Unit test for {@link DefaultEventInitiator}, which will test if every controller gets it's updates and if the event
 * initiator will start and finish.
 * 
 * @author Timo
 */
public class DefaultEventInitiatorTest {
	private static final long INTERVAL = 1000;
	private static final long CLOCK_GENERATOR_INTERVAL = 0;
	private static DefaultEventInitiator testClass;
	private static EnergyControllerMock energyController;
	private static WeatherControllerMock weatherController;
	private static StaticSensorControllerMock staticSensorController;
	private static TrafficControllerMock trafficController;
	private static ServiceDictionary serviceDictionary;
	private static OperationCenterControllerMock operationCenterController;
	private static long startTimestamp, endTimestamp;
	private static InitParameter initParameter;
	private static StartParameter startParameter;
	private static SimulationController simulationController;
	private static ControlCenterControllerMock controlCenterController;

	@BeforeClass
	public static void setUp() {
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 0, 0, 0, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		cal.set(2011, 0, 0, 1, 0, 0);
		endTimestamp = cal.getTimeInMillis();

		initParameter = new InitParameter(null, null, startTimestamp, endTimestamp, INTERVAL, CLOCK_GENERATOR_INTERVAL,
				"", "", null, null);

		startParameter = new StartParameter(null, true, null,
				null);

		energyController = new EnergyControllerMock();
		weatherController = new WeatherControllerMock();
		staticSensorController = new StaticSensorControllerMock();
		trafficController = new TrafficControllerMock();
		operationCenterController = new OperationCenterControllerMock();
		simulationController = new SimulationControllerMock();
		controlCenterController = new ControlCenterControllerMock();

		serviceDictionary = EasyMock.createNiceMock(ServiceDictionary.class);
		EasyMock.expect(serviceDictionary.getController(EnergyController.class)).andStubReturn(energyController);
		EasyMock.expect(serviceDictionary.getController(WeatherController.class)).andStubReturn(weatherController);
		EasyMock.expect(serviceDictionary.getController(StaticSensorController.class)).andStubReturn(
				staticSensorController);
		EasyMock.expect(serviceDictionary.getController(TrafficController.class)).andStubReturn(trafficController);
		EasyMock.expect(serviceDictionary.getController(SimulationController.class))
				.andStubReturn(simulationController);
		EasyMock.replay(serviceDictionary);

		testClass = new DefaultEventInitiator();
		testClass._setServiceDictionary(serviceDictionary);
		testClass.setOperationCenterController(operationCenterController);
		List<Controller> controllerCollection = new LinkedList<>();
		controllerCollection.add(staticSensorController);
		testClass.setFrontController(controllerCollection);
		testClass.setControlCenterController(controlCenterController);
	}

	@Test
	public void test() throws IllegalStateException, InitializationException, InterruptedException {
		long updateIntervals = ((endTimestamp - startTimestamp) / INTERVAL) + 1;

		// Status test
		assertEquals(StatusEnum.INIT, testClass.getStatus());

		testClass.init(initParameter);

		// Status test
		assertEquals(StatusEnum.INITIALIZED, testClass.getStatus());

		testClass.start(startParameter);

		// Status test
		assertEquals(StatusEnum.STARTED, testClass.getStatus());

		testClass.getEventThread().join();

		// Test all counters
		assertEquals(updateIntervals, energyController.getUpdateCounter());
		assertEquals(updateIntervals, weatherController.getUpdateCounter());
		assertEquals(updateIntervals, trafficController.getUpdateCounter());
		assertEquals(updateIntervals, staticSensorController.getUpdateCounter());
		assertEquals(updateIntervals, operationCenterController.getUpdateCounter());
		assertEquals(updateIntervals, controlCenterController.getUpdateCount());
		
		testClass.stop();

		// Status test
		assertEquals(StatusEnum.STOPPED, testClass.getStatus());

		testClass.reset();

		// Status test
		assertEquals(StatusEnum.INIT, testClass.getStatus());
	}

	/**
	 * Operation center controller mock which can count the update steps.
	 * 
	 * @author Timo
	 */
	private static class OperationCenterControllerMock implements OperationCenterController {

		private int updateCounter;

		public OperationCenterControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void createSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
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
	}

	/**
	 * TrafficController mock which can count the update steps.
	 * 
	 * @author Timo
	 */
	private static class TrafficControllerMock implements TrafficController {
		private int updateCounter;

		public TrafficControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void createSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
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
	}

	/**
	 * StaticSensorController mock which can count the update steps.
	 * 
	 * @author Timo
	 */
	private static class StaticSensorControllerMock implements StaticSensorController {

		private int updateCounter;

		public StaticSensorControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void createSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
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
			return "StaticSensorController";
		}
	}

	/**
	 * WeatherController mock which can count the update steps.
	 * 
	 * @author Timo
	 */
	private static class WeatherControllerMock implements WeatherController {

		private int updateCounter;

		public WeatherControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
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
		public Coordinate getReferencePosition() {
			return null;
		}

		@Override
		public Number getValue(WeatherParameterEnum key, long timestamp, Coordinate position) {
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

		public EnergyControllerMock() {
			this.updateCounter = 0;
		}

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
			this.updateCounter++;
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public double getEnergyConsumptionInKWh(long timestamp, Coordinate position, int measureRadiusInMeter) {
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

		public SimulationControllerMock() {

		}

		@Override
		public void createSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public void deleteSensor(SensorHelper sensor) throws SensorException {
		}

		@Override
		public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		}

		@Override
		public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
			return false;
		}

		@Override
		public void init(InitParameter param) throws InitializationException, IllegalStateException {
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
		public void update(SimulationEventList simulationEventList) throws IllegalStateException {
		}

		@Override
		public StatusEnum getStatus() {
			return null;
		}

		@Override
		public void addSimulationEventList(SimulationEventList simulationEventList) {
		}

		@Override
		public long getSimulationTimestamp() {
			return 0;
		}

		@Override
		public void _setOperationCenterController(OperationCenterController operationCenterController) {
		}

		@Override
		public EventInitiator _getEventInitiator() {
			return null;
		}

		@Override
		public void _setControlCenterController(ControlCenterController controlCenterController) {
		}

		@Override
		public String getName() {
			return "SimulationController";
		}
	}
	
	/**
	 * Mock for the controll center controller.
	 * @author Timo
	 */
	private static class ControlCenterControllerMock implements ControlCenterController {

		private int updateCount;
		
		/**
		 * Constructor
		 */
		public ControlCenterControllerMock() {
			this.updateCount = 0;
		}
		
		@Override
		public void init(InitParameter param) throws InitializationException,
				IllegalStateException {}

		@Override
		public void reset() throws IllegalStateException {}

		@Override
		public void start(StartParameter param) throws IllegalStateException {}

		@Override
		public void stop() throws IllegalStateException {}

		@Override
		public void update(SimulationEventList simulationEventList)
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
				throws IllegalStateException {}

		public int getUpdateCount() {
			return updateCount;
		}
	}
}
