/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.internal;

import de.pgalise.simulation.SimulationControllerLocal;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.energy.EnergySensorController;
import de.pgalise.simulation.energy.EnergySensorControllerLocal;
import de.pgalise.simulation.energy.sensor.EnergySensor;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorController;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherInitParameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public abstract class AbstractSimulationController extends AbstractController<Event, TrafficStartParameter, TrafficInitParameter>
  implements SimulationControllerLocal {

  private static final String NAME = "SimulationController";
  private static final long serialVersionUID = 1L;
  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    AbstractSimulationController.class);

  /**
   * EventInitiator
   */
  @EJB
  private EventInitiator eventInitiator;
  @EJB
  private ConfigReader configReader;
  /**
   * Start parameter
   */
  private List<Controller<?, ?, ?>> frontControllerList;
  private long elapsedTime = 0;
  private long lastElapsedTimeCheckTimestamp;
  @EJB
  private WeatherController weatherController;
  @EJB
  private EnergyControllerLocal energyController;
  @EJB
  private TrafficControllerLocal trafficController;
  @EJB
  private TrafficSensorController<TrafficEvent<?>> trafficSensorController;
  @EJB
  private EnergySensorControllerLocal energySensorController;
  @EJB
  private WeatherSensorController weatherSensorController;
  @EJB
  private TrafficServerLocal<?> trafficServer;
  /**
   * copy of the start parameter to be able to resume controllers after being
   * paused
   */
  private TrafficStartParameter startParameter;

  /**
   * Default constructor
   */
  public AbstractSimulationController() {
    this.frontControllerList = new LinkedList<>();
  }

  /**
   * handles delegation of sensors to controllers which are managed by
   * {@link SensorManagerController}s, others sensors are ignored
   *
   * @param sensor
   * @
   */
  @Override
  public void createSensor(Sensor<?, ?> sensor) {
    if (sensor == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "sensor"));
    }
    if (sensor instanceof EnergySensor) {
      energySensorController.createSensor((EnergySensor) sensor);
    } else if (sensor instanceof TrafficSensor) {
      trafficServer.createSensor((TrafficSensor) sensor);
    } else if (sensor instanceof GpsSensor) {
      trafficSensorController.createSensor((GpsSensor) sensor);
    } else {
      throw new IllegalArgumentException(String.format(
        "Can't create sensor %s because no suitable controller was found!",
        sensor));
    }
  }

  @Override
  public void deleteSensor(Sensor sensor) {
    if (sensor == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "sensor"));
    }
    if (sensor instanceof EnergySensor) {
      energySensorController.deleteSensor((EnergySensor) sensor);
    } else if (sensor instanceof TrafficSensor) {
      trafficServer.deleteSensor((TrafficSensor) sensor);
    } else if (sensor instanceof GpsSensor) {
      trafficSensorController.deleteSensor((GpsSensor) sensor);
    } else {
      throw new IllegalArgumentException(String.format(
        "Can't delete sensor %s because no suitable controller was found!",
        sensor));
    }
  }

  /**
   * Determine the status of a sensor
   *
   * @param sensor sensor to get information from
   * @return status of the sensor (false if there is no sensor or controller)
   * @throws SensorException
   */
  @Override
  public boolean isActivated(Sensor<?, ?> sensor) {
    if (sensor == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "sensor"));
    }
    if (sensor instanceof EnergySensor) {
      return energySensorController.isActivated((EnergySensor) sensor);
    } else if (sensor instanceof TrafficSensor) {
      return trafficServer.isActivated((TrafficSensor) sensor);
    } else if (sensor instanceof GpsSensor) {
      return trafficSensorController.isActivated((GpsSensor) sensor);
    } else {
      throw new IllegalArgumentException(String.format(
        "Can't delete sensor %s because no suitable controller was found!",
        sensor));
    }
  }

  @Override
  protected void onInit(final TrafficInitParameter param) throws InitializationException {
    weatherController.init(new WeatherInitParameter<>(param.getCity(),
      param.getStartTimestamp().getTime(),
      param.getEndTimestamp().getTime(),
      param.getInterval(),
      param.getClockGeneratorInterval(),
      param.getOperationCenterURL(),
      param.getControlCenterURL(),
      param.getCity().getGeoInfo().getBoundaries().getEnvelopeInternal(),
      param.getOutput()));
    energyController.init(param);
    trafficController.init(param);
    weatherSensorController.init(param);

    // init the event initiator
    this.eventInitiator.setFrontController(frontControllerList);
    this.eventInitiator.init(param);
    trafficSensorController.init(param);
    trafficServer.init(param);
    energySensorController.init(param);
  }

  @Override
  protected void onReset() {
    this.frontControllerList.clear();

    weatherController.reset();
    trafficController.reset();
    trafficSensorController.reset();
    trafficServer.reset();
    energyController.reset();
    energySensorController.reset();
    weatherSensorController.reset();

    // reset the event initiator
    this.eventInitiator.reset();
  }

  @Override
  protected void onStart(TrafficStartParameter param) {
    this.startParameter = param;
    weatherController.start(param);
    trafficController.start(param);
    trafficSensorController.start(param);
    trafficServer.start(param);
    energyController.start(param);
    energySensorController.start(param);
    weatherSensorController.start(param);

    // start the event initiator
    this.eventInitiator.start(param);
  }

  @Override
  protected void onStop() {
    // stop the event initiator
    if (this.eventInitiator.getStatus() == ControllerStatusEnum.STARTED) {
      this.eventInitiator.stop();
    }

    weatherController.stop();
    trafficController.stop();
    trafficSensorController.stop();
    trafficServer.stop();
    energyController.stop();
    energySensorController.stop();
    weatherSensorController.stop();
  }

  @Override
  protected void onResume() {
    // start the controllers
    weatherController.start(startParameter);
    trafficController.start(startParameter);
    trafficSensorController.start(startParameter);
    trafficServer.start(startParameter);
    energyController.start(startParameter);
    energySensorController.start(startParameter);
    weatherSensorController.start(startParameter);

    // start the event initiator
    this.eventInitiator.start(this.startParameter);
  }

  @Override
  public long getElapsedTime() {
    long timestamp = System.currentTimeMillis();
    elapsedTime += timestamp - lastElapsedTimeCheckTimestamp;
    lastElapsedTimeCheckTimestamp = timestamp;
    return elapsedTime;
  }

  @Override
  protected void onUpdate(EventList<Event> simulationEventList) {
    this.eventInitiator.addSimulationEventList(simulationEventList);
  }

  @Override
  public void addSimulationEventList(EventList<?> simulationEventList) {
    this.eventInitiator.addSimulationEventList(simulationEventList);
  }

  @Override
  public long getSimulationTimestamp() {
    return this.eventInitiator.getCurrentTimestamp();
  }

  @Override
  public EventInitiator getEventInitiator() {
    return this.eventInitiator;
  }

  @Override
  public void createSensors(Set<Sensor<?, ?>> sensors) {
    for (Sensor<?, ?> sensor : sensors) {
      this.createSensor(sensor);
    }
  }

  @Override
  public void deleteSensors(Set<Sensor<?, ?>> sensors) {
    for (Sensor<?, ?> sensor : sensors) {
      this.deleteSensor(sensor);
    }
  }

  /**
   * Only for J-Unit tests.
   *
   * @param eventInitiator
   */
  protected void setEventInitiator(EventInitiator eventInitiator) {
    this.eventInitiator = eventInitiator;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Set<Sensor<?, ?>> getAllManagedSensors() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public EnergySensorController getEnergySensorController() {
    return energySensorController;
  }

  @Override
  public WeatherSensorController getWeatherSensorController() {
    return weatherSensorController;
  }

  @Override
  public TrafficSensorController getTrafficSensorController() {
    return trafficSensorController;
  }
}
