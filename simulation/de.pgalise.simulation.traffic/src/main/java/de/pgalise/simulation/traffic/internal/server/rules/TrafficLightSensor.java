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
package de.pgalise.simulation.traffic.internal.server.rules;

import de.pgalise.simulation.traffic.entity.TrafficLightSensorData;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.sensor.AbstractStaticTrafficSensor;

/**
 * Sensor for the traffic lights
 *
 * @author Marcus
 * @param <D>
 */
public class TrafficLightSensor<D extends VehicleData> extends AbstractStaticTrafficSensor<TrafficLightSensorData> {

  private static final long serialVersionUID = 1L;

  /**
   * Traffic light
   */
  private final TrafficLight trafficLight;

  /**
   * Constructor
   *
   * @param id
   * @param output Sensor output
   * @param node
   * @param trafficLight Traffic light
   * @throws IllegalArgumentException Thrown if no traffic light is null
   */
  public TrafficLightSensor(Long id,
    Output output,
    TrafficNode node,
    TrafficLight trafficLight)
    throws IllegalArgumentException {
    super(id,
      output,
      node,
      new TrafficLightSensorData());
    if (trafficLight == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "trafficLight"));
    }
    this.trafficLight = trafficLight;
  }

  @Override
  public void transmitUsageData(EventList<TrafficEvent<?>> eventList) {
    // // Send data
    this.getOutput().transmitDouble(this.trafficLight.getAngle1());
    this.getOutput().transmitDouble(this.trafficLight.getAngle2());
    this.getOutput().transmitByte((byte) this.trafficLight.getState().
      getStateId()); // stateId
    this.getOutput().transmitLong(this.trafficLight.getId());
    this.getOutput().transmitShort((short) 0);
    this.getOutput().transmitShort((short) 0);
  }

  @Override
  public void vehicleOnNodeRegistered(Vehicle<?> vehicle) {
    // nothing to do here
  }

  @Override
  public SensorType getSensorType() {
    return TrafficSensorTypeEnum.TRAFFICLIGHT_SENSOR;
  }
}
