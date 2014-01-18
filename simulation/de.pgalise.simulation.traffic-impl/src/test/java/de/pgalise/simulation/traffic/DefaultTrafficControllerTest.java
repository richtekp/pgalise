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
 *//* 
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

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.internal.DefaultTrafficController;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.util.Arrays;
import java.util.LinkedList;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;

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
  @EJB
  private TcpIpOutput tcpIpOutput;

  public DefaultTrafficControllerTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  /**
   * Test for initialization and start
   *
   * @throws IllegalStateException
   * @throws InitializationException
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void initAndStartTest() throws IllegalStateException, InitializationException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      null);
    TrafficStartParameter startParam = new TrafficStartParameter();

    TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

    s1.init(initParam);
    s1.setCityZone(anyObject(Geometry.class));
    expectLastCall().andAnswer(new IAnswer() {

      @Override
      public Object answer() throws Throwable {
        Geometry g = (Geometry) getCurrentArguments()[0];

        assertTrue(g.getEnvelopeInternal().getMinX() == 0 && g.
          getEnvelopeInternal().getMinY() == 0 && g.getEnvelopeInternal().
          getWidth() == 800 && g.getEnvelopeInternal().getHeight() == 500);
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
        assertTrue(g.getEnvelopeInternal().getMinX() == 0 && g.
          getEnvelopeInternal().getMinY() == 500 && g.getEnvelopeInternal().
          getWidth() == 800 && g.getEnvelopeInternal().getHeight() == 500);
        return null;
      }

    });
    s2.start(startParam);
    replay(s2);

    TrafficControllerLocal<?> ctrl = new DefaultTrafficController(
      GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
        new JaxRSCoordinate[]{}),
      new LinkedList<>(Arrays.asList(s1,
          s2)));

    ctrl.init(initParam);
    ctrl.start(startParam);

    verify(s1);
    verify(s2);
  }

  @Test
  /**
   * Test for stop and resume
   *
   * @throws IllegalStateException
   * @throws InitializationException
   */
  public void stopAndResumeTest() throws IllegalStateException, InitializationException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      null);
    TrafficStartParameter startParam = new TrafficStartParameter();

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

    TrafficControllerLocal ctrl = new DefaultTrafficController(
      GeoToolsBootstrapping.getGEOMETRY_FACTORY().
      createPolygon(new JaxRSCoordinate[]{}),
      Arrays.asList(s1,
        s2));

    ctrl.init(initParam);
    ctrl.start(startParam);
    ctrl.stop();
    ctrl.start(null);

    verify(s1);
    verify(s2);
  }

  @Test
  public void resetTest() throws IllegalStateException, InitializationException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      null);
    TrafficStartParameter startParam = createNiceMock(
      TrafficStartParameter.class);

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

    TrafficControllerLocal ctrl = new DefaultTrafficController(
      GeoToolsBootstrapping.getGEOMETRY_FACTORY().
      createPolygon(new JaxRSCoordinate[]{}),
      Arrays.asList(s1,
        s2));

    ctrl.init(initParam);
    ctrl.start(startParam);
    ctrl.stop();
    ctrl.reset();

    verify(s1);
    verify(s2);
  }

  @Test
  public void updateTest() throws IllegalStateException, InitializationException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      null);
    TrafficStartParameter startParam = new TrafficStartParameter();

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

    TrafficControllerLocal<?> ctrl = new DefaultTrafficController(
      GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
        new JaxRSCoordinate[]{}),
      Arrays.asList(s1,
        s2));

    ctrl.init(initParam);
    ctrl.start(startParam);
    ctrl.update(createNiceMock(EventList.class));

    verify(s1);
    verify(s2);
  }

  @Test
  /**
   * Test for creating, deleting and asking for status of sensors
   *
   * @throws IllegalStateException
   * @throws SensorException
   * @throws InitializationException
   */
  public void sensorTests() throws IllegalStateException, SensorException, InitializationException {
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    TrafficInitParameter initParam = new TrafficInitParameter(city,
      null);
    TrafficStartParameter startParam = createNiceMock(
      TrafficStartParameter.class);

    // create sensors
    TrafficSensor sensor = new InductionLoopSensor(idGenerator.getNextId(),
      tcpIpOutput,
      null,
      new InductionLoopNoInterferer());
    TrafficSensor sensor2 = new InductionLoopSensor(idGenerator.getNextId(),
      tcpIpOutput,
      null,
      new InductionLoopNoInterferer());

    TrafficServerLocal<TrafficEvent> s1 = createMock(TrafficServerLocal.class);

    s1.init(initParam);
    s1.setCityZone(anyObject(Geometry.class));
    s1.start(startParam);
    s1.createSensor(sensor);
    expect(s1.isActivated(sensor)).andReturn(true);
    s1.deleteSensor(sensor);
    replay(s1);

    TrafficServerLocal<TrafficEvent> s2 = createMock(TrafficServerLocal.class);
    s2.init(initParam);
    s2.setCityZone(anyObject(Geometry.class));
    s2.start(startParam);
    s2.createSensor(sensor);
    expect(s2.isActivated(sensor2)).andReturn(false);
    s2.deleteSensor(sensor);
    replay(s2);

    TrafficControllerLocal<?> ctrl = new DefaultTrafficController(
      GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
        new JaxRSCoordinate[]{}),
      Arrays.asList(s1,
        s2));

    ctrl.init(initParam);
    ctrl.start(startParam);
    ctrl.createSensor(sensor);
    assertTrue(ctrl.isActivated(sensor));
    assertFalse(ctrl.isActivated(sensor2));
    ctrl.deleteSensor(sensor);

    verify(s1);
    verify(s2);
  }
}
