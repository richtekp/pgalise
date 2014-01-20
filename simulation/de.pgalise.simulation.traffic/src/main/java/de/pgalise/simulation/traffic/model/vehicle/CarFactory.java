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
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import java.util.Set;

/**
 * Interface to provide methods to create different types of {@link Car}.
 *
 * @author Andreas
 * @version 1.0
 */
public interface CarFactory extends BaseVehicleFactory {

  /**
   * creates a {@link Car} without position information, i.e. all position
   * related properties are <code>null</code>.
   *
   * @return
   */
  public Car createRandomCar(Output output);

  /**
   * the same as {@link #createRandomCar() }
   *
   * @return
   */
  public Car createCar(Output output);

  /**
   * Method to create a {@link Car} with a random position on a random edge of
   * <tt>edges</tt>.
   *
   * @TODO: so far this does the same as {@link #createRandomCar(java.util.Set)
   * }, but there should be more options to customize the creation apart from a
   * complete random process like in createRandomCar.
   *
   * @return created {@link Car}
   */
  public Car createCar(Set<TrafficEdge> edges,
    Output output);

  /**
   * Method to create a random {@link Car} with a random position on a random
   * edge of
   * <tt>edges</tt>.
   *
   * @return created {@link Car}
   */
  Car createRandomCar(Set<TrafficEdge> edges,
    Output output);
}
