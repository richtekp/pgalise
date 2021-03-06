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
import java.awt.Color;

/**
 * Interface to provide methods to create different types of {@link Truck}.
 *
 * @author Andreas
 * @version 1.0
 */
public interface TruckFactory extends VehicleFactory<Truck> {

  /**
   * Method to create a {@link Truck} with the given typeId.All position related
   * data is <code>null</code>
   *
   * @param color Color of the {@link Truck}
   * @param trailercount Number of trailers
   * @param output
   * @return created {@link Truck}
   */
  public Truck createTruck(Color color,
    int trailercount,
    Output output
  );

}
