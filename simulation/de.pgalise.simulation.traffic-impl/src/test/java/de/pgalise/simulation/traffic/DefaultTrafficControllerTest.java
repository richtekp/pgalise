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

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.util.LinkedList;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the DefaultTrafficController
 *
 * @author Mustafa
 * @version 1.0 (Feb 15, 2013)
 */
@LocalClient
@ManagedBean
public class DefaultTrafficControllerTest {

  @EJB
  private IdGenerator idGenerator;
  private final Output tcpIpOutput = EasyMock.createNiceMock(Output.class);

  public DefaultTrafficControllerTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  /**
   * Test for initialization and start
   *
   * @throws IllegalStateException
   * @throws InitializationException
   * @throws javax.naming.NamingException
   */
  @Test
  public void initAndStartTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<?> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.init(initParam);
    ctrl.start(startParam);
  }

  @Test
  /**
   * Test for stop and resume
   *
   * @throws IllegalStateException
   * @throws InitializationException
   */
  public void stopAndResumeTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<?> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");

    ctrl.init(
      initParam);
    ctrl.start(startParam);
    ctrl.stop();
    ctrl.start(null);
  }

  @Test
  public void resetTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<?> ctrl = (TrafficControllerLocal) TestUtils.
      getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.
      init(initParam);
    ctrl.start(startParam);
    ctrl.stop();
    ctrl.reset();
  }

  @Test
  public void updateTest() throws IllegalStateException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficControllerLocal<VehicleEvent> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.
      init(initParam);
    ctrl.start(startParam);
    EventList<VehicleEvent> eventList = new EventList<>(idGenerator.getNextId(),
      new LinkedList<VehicleEvent>(),
      System.currentTimeMillis());
    ctrl.update(eventList);
    ctrl.update(eventList);
    ctrl.processMovedVehicles();
  }

  @Test
  /**
   * Test for creating, deleting and asking for status of sensors
   *
   * @throws IllegalStateException
   * @throws SensorException
   * @throws InitializationException
   */
  public void sensorTests() throws IllegalStateException, SensorException, InitializationException, NamingException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(1,
      1.0,
      1);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      trafficFuzzyData);
    TrafficStartParameter startParam = createNiceMock(
      TrafficStartParameter.class);

    // create sensors
    TrafficNode someNode = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(1,
        2));
    TrafficSensor<?> sensor = new InductionLoopSensor(idGenerator.getNextId(),
      tcpIpOutput,
      someNode,
      new InductionLoopNoInterferer());
    sensor.setActivated(true);
    TrafficSensor<?> sensor2 = new InductionLoopSensor(idGenerator.getNextId(),
      tcpIpOutput,
      someNode,
      new InductionLoopNoInterferer());
    sensor2.setActivated(false);

    TrafficControllerLocal<?> ctrl
      = (TrafficControllerLocal) TestUtils.getContext().lookup(
        "java:global/classpath.ear/de.pgalise.simulation.traffic-impl/DefaultTrafficController!de.pgalise.simulation.traffic.TrafficControllerLocal");
    ctrl.init(initParam);
    ctrl.start(startParam);
    ctrl.createSensor(sensor);
    assertTrue(ctrl.isActivated(sensor));
    assertFalse(ctrl.isActivated(sensor2));
    ctrl.deleteSensor(sensor);
  }
}
