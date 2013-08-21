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
 
package de.pgalise.ssf.test;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.internal.DefaultSensorRegistry;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.sensorFramework.persistence.SensorPersistenceService;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.sensor.SensorType;

/**
 * Tests the sensor domain
 * 
 * @author Marcus Behrendt
 * @version 1.0 (Oct 1, 2012)
 */
@Ignore
public class SensorRegistryTest {

	private boolean beforeDone = false;
	private boolean afterDone = false;
	
	/**
	 * Test class
	 */
	private SensorPersistenceService persistence;

	/**
	 * Test class
	 */
	private DefaultSensorRegistry sensorRegistry;
	
	@After
	public void tearDown() {
		this.persistence.clear();
		this.afterDone = false;
	}

	@Before
	public void setUp() throws Exception {
		if(this.beforeDone) {
			return;
		}
		System.setProperty("simulation.configuration.filepath", "src/test/resources/simulation.conf");
		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);
		Context ctx = container.getContext();
		this.persistence = (SensorPersistenceService) ctx
				.lookup("java:global/de.pgalise.simulation.sensorFramework-impl/de.pgalise.sensorFramework.persistence.SensorPersistenceService");
		this.persistence.clear();
		this.sensorRegistry = new DefaultSensorRegistry(this.persistence);
		this.beforeDone = true;
	}

	/**
	 * This method tests whether a sensor can be added correctly to the SensorDomain.
	 */
	@Test
	public void testAddSensor() {
		// add ten sensors to the SensorDomain
		final int expected = 10;
		for (int i = 0; i < expected; i++) {
			this.sensorRegistry.addSensor(new TestSensor(i + 1, new Coordinate(i, i)));
		}
		// assert
		Assert.assertEquals(expected, this.sensorRegistry.numberOfSensors());
	}

	/**
	 * This method tests whether a sensor can be extracted correctly from the SensorDomain.
	 */
	@Test
	public void testGetSensor() {
		// add ten sensors to the SensorDomain
		for (int i = 0; i < 10; i++) {
			this.sensorRegistry.addSensor(new TestSensor(i + 1, new Coordinate(i, i)));
		}
		final Sensor sensor = new TestSensor(11, new Coordinate(887, 14));
		this.sensorRegistry.addSensor(sensor);
		Assert.assertEquals(this.sensorRegistry.getSensor(sensor.getSensorId()), sensor);
	}

	/**
	 * This method tests whether the number of sensors is set correctly;
	 */
	@Test
	public void testNumberOfSensors() {
		final ArrayList<Sensor> sensors = new ArrayList<Sensor>();
		for (int i = 0; i < 20; i++) {
			sensors.add(this.sensorRegistry.addSensor(new TestSensor(i + 1, new Coordinate(i, i))));
		}
		for (int i = 0; i < (sensors.size() - 2); i++) {
			this.sensorRegistry.removeSensor(sensors.get(i));
		}
		final Sensor sensor = new TestSensor(3, new Coordinate(12, 343));
		this.sensorRegistry.addSensor(sensor);
		this.sensorRegistry.removeSensor(sensor);
		Assert.assertEquals(2, this.sensorRegistry.numberOfSensors());
	}

	@Test
	public void testRemoveSensor() {
		// Remove
		for (int i = 0; i < 10; i++) {
			this.sensorRegistry.addSensor(new TestSensor(i + 1, new Coordinate(i, i)));
		}
		final Sensor sensor = new TestSensor(11, new Coordinate(23, 8438));
		this.sensorRegistry.addSensor(sensor);
		this.sensorRegistry.removeSensor(sensor);

		// All removed?
		Assert.assertEquals(10, this.sensorRegistry.numberOfSensors());
	}

	@Test
	public void testSetSensorsActivated() {
		// add ten sensors to the SensorDomain
		for (int i = 0; i < 10; i++) {
			this.sensorRegistry.addSensor(new TestSensor(i + 1, new Coordinate(i, i)));
		}
		this.sensorRegistry.setSensorsActivated(true);
		for (final Sensor s : this.sensorRegistry) {
			Assert.assertEquals(true, s.isActivated());
		}

		this.sensorRegistry.setSensorsActivated(false);
		for (final Sensor s : this.sensorRegistry) {
			Assert.assertEquals(false, s.isActivated());
		}
	}

	@Test
	public void testUpdateSensors() {
		// add ten sensors to the SensorDomain
		for (int i = 0; i < 10; i++) {
			this.sensorRegistry.addSensor(new TestSensor(i + 1, new Coordinate(i, i)));
		}
		this.sensorRegistry.setSensorsActivated(false);
		for (int i = 0; i < 100; i++) {
			this.sensorRegistry.update(new SimulationEventList(new ArrayList<SimulationEvent>(), 1, UUID.randomUUID()));
		}

		for (final Sensor sensor : this.sensorRegistry) {
			Assert.assertEquals(0, sensor.getMeasuredValues());
		}

		this.sensorRegistry.setSensorsActivated(true);
		for (int i = 0; i < 100; i++) {
			this.sensorRegistry.update(new SimulationEventList(new ArrayList<SimulationEvent>(), 1, UUID.randomUUID()));
		}

		for (final Sensor sensor : this.sensorRegistry) {
			Assert.assertEquals(100, sensor.getMeasuredValues());
		}
	}
}

/**
 * Mock of the SensorPersistenceService
 * 
 * @author Marcus
 * @version 1.0 (Mar 20, 2013)
 */
class TestSensorpersistence implements SensorPersistenceService {

	@Override
	public void clear() {

	}

	@Override
	public void deleteSensor(int sensorId) {

	}

	@Override
	public void deleteSensor(Sensor sensor) throws IllegalArgumentException, UnsupportedOperationException {

	}

	@Override
	public int nextAvailableSensorId() {
		return 0;
	}

	@Override
	public int numberOfSensors() {
		return 0;
	}

	@Override
	public void saveSensor(Sensor sensor) throws UnsupportedOperationException {

	}

}

/**
 * Test class for sensors
 * 
 * @author Marcus
 * @version 1.0 (Mar 20, 2013)
 */
class TestSensor extends Sensor {

	/**
	 * Sensor output
	 */
	private static Output SENSOR_OUTPUT = new TcpIpOutput("127.0.0.1", 6666, new TcpIpKeepOpenStrategy());

	/**
	 * Constructor
	 * 
	 * @param sensorId
	 *            ID of sensor
	 * @param position
	 *            Position as Coordinate
	 * @throws IllegalArgumentException
	 */
	protected TestSensor(Object sensorId, Coordinate position) throws IllegalArgumentException {
		super(TestSensor.SENSOR_OUTPUT, sensorId, position);
	}

	@Override
	public SensorType getSensorType() {
		return SensorType.GPS_BUS;
	}

	@Override
	public void transmitUsageData(SimulationEventList eventList) {
	}

}
