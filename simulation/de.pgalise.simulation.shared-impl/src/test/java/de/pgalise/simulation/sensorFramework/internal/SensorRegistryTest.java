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
 
package de.pgalise.simulation.sensorFramework.internal;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.GPSSensorData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SimpleSensorData;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
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
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.AbstractTcpIpOutput;
import de.pgalise.simulation.sensorFramework.output.tcpip.DefaultTcpIpOutput;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.StaticSensor;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import org.easymock.EasyMock;

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
	private EntityManager persistence;

	/**
	 * Test class
	 */
	private DefaultSensorRegistry sensorRegistry;
	@EJB
	private IdGenerator IdGenerator;
	
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
		this.persistence = EasyMock.createMock(EntityManager.class);
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
			StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
			EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(i, i));
			this.sensorRegistry.addSensor(sensor);
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
			StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
			EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(i, i));
			this.sensorRegistry.addSensor(sensor);
		}
		StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
		EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(887, 14));
		this.sensorRegistry.addSensor(sensor);
		Assert.assertEquals(this.sensorRegistry.getSensor(sensor), sensor);
	}

	/**
	 * This method tests whether the number of sensors is set correctly;
	 */
	@Test
	public void testNumberOfSensors() {
		final ArrayList<Sensor> sensors = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
			EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(i, i));
			sensors.add(this.sensorRegistry.addSensor(sensor));
		}
		for (int i = 0; i < (sensors.size() - 2); i++) {
			this.sensorRegistry.removeSensor(sensors.get(i));
		}
		StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
		EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(12, 343));
		this.sensorRegistry.addSensor(sensor);
		this.sensorRegistry.removeSensor(sensor);
		Assert.assertEquals(2, this.sensorRegistry.numberOfSensors());
	}

	@Test
	public void testRemoveSensor() {
		// Remove
		for (int i = 0; i < 10; i++) {
			StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
			EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(i, i));
			this.sensorRegistry.addSensor(sensor);
		}
		StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
		EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(23, 8438));
		this.sensorRegistry.addSensor(sensor);
		this.sensorRegistry.removeSensor(sensor);

		// All removed?
		Assert.assertEquals(10, this.sensorRegistry.numberOfSensors());
	}

	@Test
	public void testSetSensorsActivated() {
		// add ten sensors to the SensorDomain
		for (int i = 0; i < 10; i++) {
			StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
			EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(i, i));
			this.sensorRegistry.addSensor(sensor);
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
			StaticSensor sensor = EasyMock.createNiceMock(StaticSensor.class);
			EasyMock.expect(sensor.getPosition()).andReturn(new JaxRSCoordinate(i, i));
			this.sensorRegistry.addSensor(sensor);
		}
		this.sensorRegistry.setSensorsActivated(false);
		for (int i = 0; i < 100; i++) {
			this.sensorRegistry.update(new EventList(33L, new ArrayList<>(), 1));
		}

		for (final Sensor sensor : this.sensorRegistry) {
			Assert.assertEquals(0, sensor.getMeasuredValues());
		}

		this.sensorRegistry.setSensorsActivated(true);
		for (int i = 0; i < 100; i++) {
			this.sensorRegistry.update(new EventList(66L, new ArrayList<AbstractEvent>(), 1));
		}

		for (final Sensor sensor : this.sensorRegistry) {
			Assert.assertEquals(100, sensor.getMeasuredValues());
		}
	}
}
