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
 
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.easymock.IAnswer;
import org.junit.Test;

import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.internal.DefaultTrafficController;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import java.util.LinkedList;
import javax.ejb.EJB;

/**
 * Tests the DefaultTrafficController
 * 
 * @author Mustafa
 * @version 1.0 (Feb 15, 2013)
 */
public class TrafficControllerTest {
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
	@EJB
	private IdGenerator idGenerator;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	/**
	 * Test for initialization and start
	 * @throws IllegalStateException
	 * @throws InitializationException
	 */
	public void initAndStartTest() throws IllegalStateException, InitializationException {
		TrafficInitParameter initParam = createNiceMock(TrafficInitParameter.class);
		InfrastructureStartParameter startParam = createNiceMock(InfrastructureStartParameter.class);

		TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

		s1.init(initParam);
		s1.setCityZone(anyObject(Geometry.class));
		expectLastCall().andAnswer(new IAnswer() {

			@Override
			public Object answer() throws Throwable {
				Geometry g = (Geometry) getCurrentArguments()[0];

				assertTrue(g.getEnvelopeInternal().getMinX() == 0 && g.getEnvelopeInternal().getMinY() == 0 && g.getEnvelopeInternal().getWidth() == 800 && g.getEnvelopeInternal().getHeight() == 500);
				return null;
			}

		});
		s1.start(startParam);
		replay(s1);

		TrafficServerLocal<TrafficEvent> s2 = createMock(TrafficServerLocal.class);
		s2.init(initParam);
		s2.setCityZone(anyObject(Geometry.class));
		expectLastCall().andAnswer(new IAnswer() {

			@Override
			public Object answer() throws Throwable {
				Geometry g = (Geometry) getCurrentArguments()[0];
				assertTrue(g.getEnvelopeInternal().getMinX() == 0 && g.getEnvelopeInternal().getMinY() == 500 && g.getEnvelopeInternal().getWidth() == 800 && g.getEnvelopeInternal().getHeight() == 500);
				return null;
			}

		});
		s2.start(startParam);
		replay(s2);

		TrafficControllerLocal<?> ctrl = new DefaultTrafficController(GEOMETRY_FACTORY.createPolygon(new JaxRSCoordinate[] {}),new LinkedList<>(Arrays.asList(s1, s2)));

		ctrl.init(initParam);
		ctrl.start(startParam);

		verify(s1);
		verify(s2);
	}

	@Test
	/**
	 * Test for stop and resume
	 * @throws IllegalStateException
	 * @throws InitializationException
	 */
	public void stopAndResumeTest() throws IllegalStateException, InitializationException {
		TrafficInitParameter initParam = createNiceMock(TrafficInitParameter.class);
		InfrastructureStartParameter startParam = createNiceMock(InfrastructureStartParameter.class);

		TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

		s1.init(initParam);
		s1.setCityZone(anyObject(Geometry.class));
		s1.start(startParam);
		s1.stop();
		s1.start(null);
		replay(s1);

		TrafficServerLocal<TrafficEvent> s2 = createMock(TrafficServerLocal.class);
		s2.init(initParam);
		s2.setCityZone(anyObject(Geometry.class));
		s2.start(startParam);
		s2.stop();
		s2.start(null);
		replay(s2);

		TrafficControllerLocal ctrl = new DefaultTrafficController(GEOMETRY_FACTORY.createPolygon(new JaxRSCoordinate[] {}), Arrays.asList(s1, s2));

		ctrl.init(initParam);
		ctrl.start(startParam);
		ctrl.stop();
		ctrl.start(null);

		verify(s1);
		verify(s2);
	}

	@Test
	public void resetTest() throws IllegalStateException, InitializationException {
		TrafficInitParameter initParam = createNiceMock(TrafficInitParameter.class);
		InfrastructureStartParameter startParam = createNiceMock(InfrastructureStartParameter.class);

		TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

		s1.init(initParam);
		s1.setCityZone(anyObject(Geometry.class));
		s1.start(startParam);
		s1.stop();
		s1.reset();
		replay(s1);

		TrafficServerLocal<TrafficEvent> s2 = createMock(TrafficServerLocal.class);
		s2.init(initParam);
		s2.setCityZone(anyObject(Geometry.class));
		s2.start(startParam);
		s2.stop();
		s2.reset();
		replay(s2);

		TrafficControllerLocal ctrl = new DefaultTrafficController(GEOMETRY_FACTORY.createPolygon(new JaxRSCoordinate[] {}), Arrays.asList(s1, s2));

		ctrl.init(initParam);
		ctrl.start(startParam);
		ctrl.stop();
		ctrl.reset();

		verify(s1);
		verify(s2);
	}

	@Test
	public void updateTest() throws IllegalStateException, InitializationException {
		TrafficInitParameter initParam = createNiceMock(TrafficInitParameter.class);
		InfrastructureStartParameter startParam = createNiceMock(InfrastructureStartParameter.class);

		TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

		s1.init(initParam);
		s1.setCityZone(anyObject(Geometry.class));
		s1.start(startParam);
		s1.update(anyObject(EventList.class));
		s1.processMovedVehicles();
		replay(s1);

		TrafficServerLocal<TrafficEvent> s2 = createMock(TrafficServerLocal.class);
		s2.init(initParam);
		s2.setCityZone(anyObject(Geometry.class));
		s2.start(startParam);
		s2.update(anyObject(EventList.class));
		s2.processMovedVehicles();
		replay(s2);

		TrafficControllerLocal<?> ctrl = new DefaultTrafficController(GEOMETRY_FACTORY.createPolygon(new JaxRSCoordinate[] {}), Arrays.asList(s1, s2));

		ctrl.init(initParam);
		ctrl.start(startParam);
		ctrl.update(createNiceMock(EventList.class));

		verify(s1);
		verify(s2);
	}

	@Test
	/**
	 * Test for creating, deleting and asking for status of sensors
	 * @throws IllegalStateException
	 * @throws SensorException
	 * @throws InitializationException
	 */
	public void sensorTests() throws IllegalStateException, SensorException, InitializationException {
		TrafficInitParameter initParam = createNiceMock(TrafficInitParameter.class);
		InfrastructureStartParameter startParam = createNiceMock(InfrastructureStartParameter.class);

		// create sensors
		StaticSensor sensor = new InductionLoopSensor(idGenerator.getNextId(),null,null,
			null);	
		StaticSensor sensor2 = new InductionLoopSensor(idGenerator.getNextId(),null,null,
			null);

		TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

		s1.init(initParam);
		s1.setCityZone(anyObject(Geometry.class));
		s1.start(startParam);
		s1.createSensor(sensor);
		expect(s1.statusOfSensor(sensor)).andReturn(true);
		s1.deleteSensor(sensor);
		replay(s1);

		TrafficServerLocal<TrafficEvent> s2 = createMock(TrafficServerLocal.class);
		s2.init(initParam);
		s2.setCityZone(anyObject(Geometry.class));
		s2.start(startParam);
		s2.createSensor(sensor);
		expect(s2.statusOfSensor(sensor2)).andReturn(false);
		s2.deleteSensor(sensor);
		replay(s2);

		TrafficControllerLocal<?> ctrl = new DefaultTrafficController(GEOMETRY_FACTORY.createPolygon(new JaxRSCoordinate[] {}), Arrays.asList(s1, s2));

		ctrl.init(initParam);
		ctrl.start(startParam);
		ctrl.createSensor(sensor);
		assertTrue(ctrl.statusOfSensor(sensor));
		assertFalse(ctrl.statusOfSensor(sensor2));
		ctrl.deleteSensor(sensor);

		verify(s1);
		verify(s2);
	}
}
