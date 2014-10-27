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
package de.pgalise.simulation.traffic.internal.server.sensor;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.entity.BusData;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.sensor.AbstractStaticTrafficSensor;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ...
 *
 * @author Lena
 * @version 1.0
 */
@Stateful
public class DefaultTrafficSensorController extends AbstractController<TrafficEvent<?>, TrafficStartParameter, TrafficInitParameter>
  implements TrafficSensorController<TrafficEvent<?>> {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultTrafficSensorController.class);
  private static final String NAME = "SensorController";
  private static final long serialVersionUID = 1L;

  /**
   * Sensor domain
   */
  private Set<TrafficSensor> sensorRegistry = new HashSet<>();

  /**
   * {@link TrafficGraphExtensions}
   */
  @EJB
  private TrafficGraphExtensions trafficGraphExtensions;

  public DefaultTrafficSensorController() {
  }

  public DefaultTrafficSensorController(
    TrafficControllerLocal<VehicleEvent> server,
    SensorFactory sensorFactory,
    TrafficGraphExtensions trafficGraphExtensions) {
    this.sensorRegistry = sensorRegistry;
    this.trafficGraphExtensions = trafficGraphExtensions;
  }

  @Override
  public void createSensor(TrafficSensor sensor) {
    if (sensor != null) {
      if (sensor instanceof InductionLoopSensor) {
        sensor.setActivated(true);
      }
			// else if(newSensor instanceof GpsSensor) {
      // return newSensor.getSensorId();
      // }
      // else if (newSensor instanceof InfraredSensor) {
      //
      // }

      // log.debug("Add sensor to Registry " + sensorRegistry.hashCode());
      sensorRegistry.add(sensor);
      if (sensor instanceof AbstractStaticTrafficSensor) {
        trafficGraphExtensions.addSensor(null,
          (AbstractStaticTrafficSensor) sensor);
        log.info("Sensor " + sensor + " added to node " + sensor);
      }
    }
  }

  @Override
  public void deleteSensor(TrafficSensor sensor) {
    // Check sensor
    if (sensor != null) {
      if (sensor instanceof GpsSensor) {
        ((GpsSensor) sensor).setActivated(false);
      }
      sensorRegistry.remove(sensor);
    }
  }

  @Override
  public boolean isActivated(TrafficSensor sensor) {
    boolean state = false;
    // Check sensor
    if (sensor != null) {
      state = sensor.isActivated();
    }
    return state;
  }

  @Override
  protected void onInit(TrafficInitParameter param) throws InitializationException {
    /* Nothing to do here: */
  }

  @Override
  protected void onReset() {
    // Remove all sensors
    sensorRegistry.clear();
  }

  @Override
  protected void onStart(TrafficStartParameter param) {
    /* Nothing to do here: */
  }

  @Override
  protected void onStop() {
    // Deactivate sensors
    for (Sensor<?, ?> sensor : sensorRegistry) {
      sensor.setActivated(false);
    }
  }

  @Override
  protected void onResume() {
    // Activate sensors
    for (Sensor<?, ?> sensor : sensorRegistry) {
      sensor.setActivated(true);
    }
  }

  @Override
  protected void onUpdate(EventList<TrafficEvent<?>> simulationEventList) {
    /* Nothing to do here: */
  }

  @Override
  public void onUpdate(Vehicle<?> vehicle,
    EventList<TrafficEvent<?>> eventList) {
  }

  public void prepareUpdate(Vehicle<?> vehicle) {
    VehicleData data = vehicle.getData();

    // Prepare GPS sensor
    Sensor<?, ?> sensor = data.getGpsSensor();
    if (sensor == null) {
      GpsSensor newSensor;
      newSensor = data.getGpsSensor();
      newSensor.setVehicle(vehicle);
      vehicle.getData().setGpsSensor(newSensor);
      sensorRegistry.add(newSensor);
    }
		// log.debug("sending gps sensor data for vehicle " + vehicle.getId());
    // sensor.update(eventList); //auskommentiert lassen, sonst werden die Werte doppelt gesendet

    // Prepare infrared sensor (only for busses)
    if (data instanceof BusData) {
      BusData busdata = (BusData) data;
      if (busdata.getInfraredSensor() != null) {
        sensor = busdata.getInfraredSensor();
        if (sensor == null) {
          InfraredSensor newSensor;
          newSensor = busdata.getInfraredSensor();
          newSensor.setVehicle((Vehicle<BusData>) vehicle);
          ((Vehicle<BusData>) vehicle).getData().setInfraredSensor(newSensor);
          log.debug(
            "InfraredSensor " + newSensor.getId() + " registered for vehicle "
            + vehicle.getName());
          sensorRegistry.add(newSensor);
        } else {
          // workaround (warum auch immer ist in der registry sonst ein falsches fahrzeug gespeichert)
          // evtl. sensorregistry beim start clearen?
          ((InfraredSensor) sensor).setVehicle((Vehicle<BusData>) vehicle);
        }

        // log.debug("sending infrared sensor data for vehicle " + vehicle.getId());
        // sensor.update(eventList); //auskommentiert lassen, sonst werden die Werte doppelt gesendet
      }
    }
  }

  @Override
  public void onRemove(Vehicle<?> vehicle) {
    // Check sensor
    Sensor<?, ?> newSensor = vehicle.getData().getGpsSensor();
    if (newSensor != null) {
      vehicle.getData().getGpsSensor().setActivated(false);
      sensorRegistry.remove(newSensor);
    }
  }

  @Override
  public void onSchedule(Vehicle<?> vehicle) {
    prepareUpdate(vehicle);
  }

  @Override
  public void createSensors(Set<TrafficSensor> sensors) {
    for (TrafficSensor sensor : sensors) {
      this.createSensor(sensor);
    }
  }

  @Override
  public void deleteSensors(Set<TrafficSensor> sensors) {
    for (TrafficSensor sensor : sensors) {
      this.deleteSensor(sensor);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Set<TrafficSensor> getAllManagedSensors() {
    return sensorRegistry;
  }
}
