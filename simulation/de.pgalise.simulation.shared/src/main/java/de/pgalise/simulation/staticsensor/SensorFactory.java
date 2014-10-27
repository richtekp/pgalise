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
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Factory to create sensors
 *
 * @author marcus
 */
/*
 doesn't make sense to provide a method to create sensors from SensorType with specification of position because not all sensors can have positions without constraints and the constraints are different
 */
public interface SensorFactory {

  /**
   * Creates a sensor of a specific type with random values (use other methods
   * of this class to create sensors with values of specific bounds)
   *
   * @param sensorType
   * @param sensorInterfererTypes
   * @param output
   * @return sensor Returns the created sensor
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public Sensor<?, ?> createSensor(SensorType sensorType,
    List<SensorInterfererType> sensorInterfererTypes,
    Output output)
    throws InterruptedException, ExecutionException;

  /**
   *
   * @param position
   * @param sensorInterfererTypes
   * @param output
   * @return
   */
  public BaseCoordinate createEnergySensor(BaseCoordinate position,
    List<SensorInterfererType> sensorInterfererTypes,
    Output output);
}
