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

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergySensorController;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.internal.event.DefaultEventInitiator;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorController;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherInitParameter;
import de.pgalise.testutils.TestUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * J-Unit test for {@link DefaultEventInitiator}, which will test if every
 * controller gets it's updates and if the event initiator will start and
 * finish.
 *
 * @author Timo
 */
@LocalBean
@ManagedBean
public class DefaultEventInitiatorTest {

  private static final long INTERVAL = 1000;
  private static final long CLOCK_GENERATOR_INTERVAL = 0;
  @EJB
  private DefaultEventInitiator eventInitiator;
  private static EnergyControllerMock energyController;
  private static WeatherControllerMock weatherController;
  private static TrafficControllerMock trafficController;
  private static long startTimestamp, endTimestamp;
  private static WeatherInitParameter initParameter;
  private static StartParameter startParameter;
  private static SimulationController simulationController;
  @EJB
  private IdGenerator idGenerator;

  public DefaultEventInitiatorTest() {
  }

  @Before
  public void setUp() throws NamingException, MalformedURLException {
    TestUtils.getContainer().bind("inject",
      this);

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

    initParameter = new WeatherInitParameter<>(
      TestUtils.createDefaultTestCityInstance(idGenerator),
      startTimestamp,
      endTimestamp,
      INTERVAL,
      CLOCK_GENERATOR_INTERVAL,
      new URL("http://localhost:8080/operationCenter"),
      new URL("http://localhost:8080/controlCenter"),
      null);

    startParameter = new StartParameter<>(true,
      null,
      null);

    energyController = new EnergyControllerMock();
    weatherController = new WeatherControllerMock();
    trafficController = new TrafficControllerMock();
    simulationController = new SimulationControllerMock();

    IdGenerator idGenerator = EasyMock.createNiceMock(IdGenerator.class);

    eventInitiator = new DefaultEventInitiator(idGenerator,
      null);
    List<Controller<?, ?, ?>> controllerCollection = new LinkedList<>();
    eventInitiator.setFrontController(controllerCollection);
  }

  @Test
  public void test() throws IllegalStateException, InitializationException, InterruptedException {
    long updateIntervals = ((endTimestamp - startTimestamp) / INTERVAL) + 1;

    // Status test
    assertEquals(ControllerStatusEnum.INIT,
      eventInitiator.getStatus());

    eventInitiator.init(initParameter);

    // Status test
    assertEquals(ControllerStatusEnum.INITIALIZED,
      eventInitiator.getStatus());

    eventInitiator.start(startParameter);

    // Status test
    assertEquals(ControllerStatusEnum.STARTED,
      eventInitiator.getStatus());

    eventInitiator.getEventThread().join();

    // Test all counters
    assertEquals(updateIntervals,
      energyController.getUpdateCounter());
    assertEquals(updateIntervals,
      weatherController.getUpdateCounter());
    assertEquals(updateIntervals,
      trafficController.getUpdateCounter());
    eventInitiator.stop();

    // Status test
    assertEquals(ControllerStatusEnum.STOPPED,
      eventInitiator.getStatus());

    eventInitiator.reset();

    // Status test
    assertEquals(ControllerStatusEnum.INIT,
      eventInitiator.getStatus());
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
    public void createSensor(TrafficSensor sensor) {
    }

    @Override
    public void createSensors(Set<TrafficSensor> sensors) {
    }

    @Override
    public void deleteSensor(TrafficSensor sensor) {
    }

    @Override
    public void deleteSensors(Set<TrafficSensor> sensors) {
    }

    @Override
    public boolean isActivated(TrafficSensor sensor) {
      return false;
    }

    @Override
    public void init(TrafficInitParameter param) throws IllegalStateException {
    }

    @Override
    public void reset() throws IllegalStateException {
    }

    @Override
    public void start(TrafficStartParameter param) throws IllegalStateException {
    }

    @Override
    public void stop() throws IllegalStateException {
    }

    @Override
    public void update(EventList<TrafficEvent> simulationEventList) throws IllegalStateException {
      this.updateCounter++;
    }

    @Override
    public ControllerStatusEnum getStatus() {
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
    public Set<TrafficSensor> getAllManagedSensors() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void init(WeatherInitParameter param) throws IllegalStateException {
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
    public void update(EventList<WeatherEvent> simulationEventList) throws IllegalStateException {
      this.updateCounter++;
    }

    @Override
    public ControllerStatusEnum getStatus() {
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

    @Override
    public Long getId() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void update(EventList<EnergyEvent> simulationEventList) throws IllegalStateException {
      this.updateCounter++;
    }

    @Override
    public ControllerStatusEnum getStatus() {
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

    @Override
    public Long getId() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void createSensor(Sensor sensor) {
    }

    @Override
    public void createSensors(Set<Sensor<?, ?>> sensors) {
    }

    @Override
    public void deleteSensor(Sensor sensor) {
    }

    @Override
    public void deleteSensors(Set<Sensor<?, ?>> sensors) {
    }

    @Override
    public boolean isActivated(Sensor sensor) {
      return false;
    }

    @Override
    public void init(TrafficInitParameter param) throws IllegalStateException {
    }

    @Override
    public void reset() throws IllegalStateException {
    }

    @Override
    public void start(TrafficStartParameter param) throws IllegalStateException {
    }

    @Override
    public void stop() throws IllegalStateException {

    }

    @Override
    public void update(EventList<Event> simulationEventList) throws IllegalStateException {
    }

    @Override
    public ControllerStatusEnum getStatus() {
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
    public long getElapsedTime() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Sensor<?, ?>> getAllManagedSensors() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WeatherSensorController getWeatherSensorController() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EnergySensorController getEnergySensorController() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TrafficSensorController getTrafficSensorController() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long getId() {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  }
}
