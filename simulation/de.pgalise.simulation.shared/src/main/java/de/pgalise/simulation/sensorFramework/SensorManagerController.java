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
package de.pgalise.simulation.sensorFramework;

import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.exception.SensorException;
import java.util.Set;

/**
 * Sensor manager controller interface for all sensor controllers. This
 * represents a base class to distungish {@link SensorManagerController} which
 * are usually EJBs from {@link Tr
 *
 * @param <E>
 * @param <S>
 * @param <I> @author Timo
 * @param <Y>
 */
public interface SensorManagerController<E extends Event, S extends StartParameter, I extends InitParameter, Y extends Sensor>
  extends Controller<E, S, I> {

  /**
   * Creates a new sensor.
   *
   * @param sensor
   * @throws SensorException
   */
  public void createSensor(Y sensor);

  /**
   * Creates a collection of new sensors.
   *
   * @param sensors
   * @throws SensorException
   */
  public void createSensors(Set<Y> sensors);

  /**
   * Deletes a sensor.
   *
   * @param sensor
   * @throws SensorException
   */
  public void deleteSensor(Y sensor);

  /**
   * Removes a collection of sensors.
   *
   * @param sensors
   * @throws SensorException
   */
  public void deleteSensors(Set<Y> sensors);

  /**
   * Is the sensor activated or not
   *
   * @param sensor
   * @return
   * @throws SensorException
   */
  public boolean isActivated(Y sensor);

  public Set<Y> getAllManagedSensors();
}
