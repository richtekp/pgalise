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

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import de.pgalise.simulation.weather.service.WeatherInitParameter;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link DefaultSimulationController}<br />
 * <br />
 * Tests if the states, create, delete sensors and the updates will be passed
 * down to the other controllers correctly.
 *
 * @author Timo
 */
@ManagedBean
@LocalClient
@LocalBean
public class DefaultSimulationControllerTest {

  private static final long START_TIMESTAMP = 0;
  private static final long END_TIMESTAMP = 100000;
  private static final long INTERVAL = 1000;
  private static final long CLOCK_GENERATOR_INTERVAL = 1000;
  @EJB
  private DefaultSimulationController instance;
  private TrafficInitParameter initParameter;
  private TrafficStartParameter startParameter;
  @EJB
  private TrafficController<?> trafficController;
  @EJB
  private EnergyController energyController;
  @EJB
  private WeatherControllerLocal weatherController;
  private TrafficCity city;
  @EJB
  private IdGenerator idGenerator;
  private Output output = EasyMock.createNiceMock(Output.class);

  public DefaultSimulationControllerTest() {
  }

  @Before
  public void setUp() throws NamingException, MalformedURLException {
    TestUtils.getContext().bind("inject",
      this);

    /* Mock all controllers: */
    trafficController = EasyMock.createNiceMock(TrafficController.class);
    energyController = EasyMock.createNiceMock(EnergyController.class);
    weatherController = EasyMock.createNiceMock(WeatherControllerLocal.class);

    /* Mock all other services: */
    EventInitiator eventInitiator = EasyMock.
      createNiceMock(EventInitiator.class);
    EasyMock.expect(eventInitiator.getStatus()).andReturn(
      ControllerStatusEnum.STARTED);
    city = TrafficTestUtils.createDefaultTestCityInstance(idGenerator);
    /* Prepare service dictionary: */
    Collection<Service> controllerCollection = new LinkedList<>();
    controllerCollection.add(energyController);
    controllerCollection.add(trafficController);
    controllerCollection.add(weatherController);
    controllerCollection.add(instance);

    instance.setEventInitiator(eventInitiator);

    initParameter = new TrafficInitParameter(city,
      START_TIMESTAMP,
      END_TIMESTAMP,
      INTERVAL,
      CLOCK_GENERATOR_INTERVAL,
      new URL("http://localhost:8080/operationCenter"),
      new URL("http://localhost:8080/controlCenter"),
      new TrafficFuzzyData(0,
        0.9,
        1),
      2,
      output);
  }

  /**
   * Sets state behavior for the mocks
   *
   * @throws InitializationException
   * @throws IllegalStateException
   */
  private void initControllerMockStateBehavior() throws IllegalStateException, InitializationException {
    trafficController.init(initParameter);
    trafficController.start(startParameter);
    trafficController.stop();
    trafficController.reset();
    EasyMock.replay(trafficController);

    weatherController.init(new WeatherInitParameter(initParameter.getCity(),
      initParameter.getStartTimestamp().getTime(),
      initParameter.getEndTimestamp().getTime(),
      initParameter.getInterval(),
      initParameter.getClockGeneratorInterval(),
      initParameter.getControlCenterURL(),
      initParameter.getOperationCenterURL(),
      initParameter.getCityBoundary(),
      output));
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
  private void resetControllerMockBehavior() {
    EasyMock.reset(trafficController);
    EasyMock.reset(weatherController);
    EasyMock.reset(energyController);
  }

  @Test
  public void testStates() throws IllegalStateException, InitializationException {
    resetControllerMockBehavior();

    /* First start: */
    initControllerMockStateBehavior();
    instance.init(initParameter);
    instance.start(startParameter);
    instance.stop();
    instance.reset();

    /* Verify all controllers: */
    EasyMock.verify(trafficController);
    EasyMock.verify(weatherController);
    EasyMock.verify(energyController);

    /* Second start: */
    resetControllerMockBehavior();
    initControllerMockStateBehavior();
    instance.init(initParameter);
    instance.start(startParameter);
    instance.stop();
    instance.reset();

    /* Verify all controllers: */
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
    TrafficSensor sensorHelperStaticSensor = new TrafficLightSensor(idGenerator.
      getNextId(),
      output,
      null,
      null);
    TrafficSensor sensorHelperTrafficSensor = new InductionLoopSensor(
      idGenerator.getNextId(),
      output,
      null,
      null);

    /* The operation center will receive both sensors: */

    /* The static sensor controller will receive only the static sensor: */
    EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();

    /* The traffic controller will receive only the traffic sensor: */
    trafficController.createSensor(sensorHelperTrafficSensor);
    trafficController.createSensor(sensorHelperStaticSensor);
    EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
    EasyMock.replay(trafficController);

    instance.init(initParameter);
    instance.start(startParameter);
    instance.createSensor(sensorHelperStaticSensor);
    instance.createSensor(sensorHelperTrafficSensor);

    EasyMock.verify(trafficController);

    instance.stop();
    instance.reset();
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
    TrafficSensor sensorHelperStaticSensor = new TrafficLightSensor(idGenerator.
      getNextId(),
      output,
      null,
      null);
    TrafficSensor sensorHelperTrafficSensor = new InductionLoopSensor(
      idGenerator.getNextId(),
      output,
      null,
      null);
    Set<Sensor<?, ?>> sensorHelperList = new HashSet<>();
    sensorHelperList.add(sensorHelperStaticSensor);
    sensorHelperList.add(sensorHelperTrafficSensor);

    /* The operation center will receive both sensors: */

    /* The static sensor controller will receive only the static sensor: */

    /* The traffic controller will receive only the traffic sensor: */
    trafficController.createSensor(sensorHelperTrafficSensor);
    trafficController.createSensor(sensorHelperStaticSensor);
    EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
    EasyMock.replay(trafficController);

    instance.init(initParameter);
    instance.start(startParameter);
    instance.createSensors(sensorHelperList);

    EasyMock.verify(trafficController);

    instance.stop();
    instance.reset();
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
    TrafficSensor sensorHelperStaticSensor = new TrafficLightSensor(idGenerator.
      getNextId(),
      output,
      null,
      null);
    TrafficSensor sensorHelperTrafficSensor = new InductionLoopSensor(
      idGenerator.getNextId(),
      output,
      null,
      null);

    /* The operation center will receive both sensors: */

    /* The static sensor controller will receive only the static sensor: */

    /* The traffic controller will receive only the traffic sensor: */
    trafficController.deleteSensor(sensorHelperTrafficSensor);
    trafficController.deleteSensor(sensorHelperStaticSensor);
    EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
    EasyMock.replay(trafficController);

    instance.init(initParameter);
    instance.start(startParameter);
    instance.deleteSensor(sensorHelperStaticSensor);
    instance.deleteSensor(sensorHelperTrafficSensor);

    EasyMock.verify(trafficController);

    instance.stop();
    instance.reset();
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
    TrafficSensor sensorHelperStaticSensor = new TrafficLightSensor(idGenerator.
      getNextId(),
      output,
      null,
      null);
    TrafficSensor sensorHelperTrafficSensor = new InductionLoopSensor(
      idGenerator.getNextId(),
      output,
      null,
      null);
    Set<Sensor<?, ?>> sensorHelperList = new HashSet<>();
    sensorHelperList.add(sensorHelperStaticSensor);
    sensorHelperList.add(sensorHelperTrafficSensor);

    /* The traffic controller will receive only the traffic sensor: */
    trafficController.deleteSensor(sensorHelperTrafficSensor);
    trafficController.deleteSensor(sensorHelperStaticSensor);
    EasyMock.expectLastCall().andThrow(new SensorException()).anyTimes();
    EasyMock.replay(trafficController);

    instance.init(initParameter);
    instance.start(startParameter);
    instance.deleteSensors(sensorHelperList);

    EasyMock.verify(trafficController);

    instance.stop();
    instance.reset();
  }

  /**
   * Tests the update method.
   *
   * @throws InitializationException
   * @throws IllegalStateException
   */
  @Test
  public void testUpdate() throws IllegalStateException, InitializationException {
    instance.init(initParameter);
    instance.start(startParameter);

    EventInitiator eventInitiatorMock = EasyMock.createNiceMock(
      EventInitiator.class);
    EventList<Event> testSimulationEventList = new EventList<>(idGenerator.
      getNextId(),
      new LinkedList<Event>(),
      0);

    instance.update(testSimulationEventList);

    assertEquals(testSimulationEventList,
      eventInitiatorMock.getEventList());

    instance.stop();
    instance.reset();
  }
}
