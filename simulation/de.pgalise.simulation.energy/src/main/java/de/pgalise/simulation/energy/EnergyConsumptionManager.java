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
package de.pgalise.simulation.energy;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;

/**
 * The energy consumption manager returns the energy consumption for a given
 * energy profile on a given position and timestamp.
 *
 * @author Andreas Rehfeldt
 * @author Timo
 * @version 1.0 (Oct 27, 2012)
 */
public interface EnergyConsumptionManager {

  /**
   * Returns the energy consumption in kwh for a given timestamp
   *
   * @param timestamp Timestamp
   * @param energyProfile Energy profile
   * @param position position can be useful to consider weather data.
   * @return value of the energy consumption
   */
  public double getEnergyConsumptionInKWh(long timestamp, EnergyProfileEnum energyProfile, BaseCoordinate position);

  /**
   * Inits the energy consumption manager.
   *
   * @param start start timestamp of the simulation
   * @param end end timestamp of the simulation
   * @param weatherController weather controller, if the weather is needed for
   * calculating the energy consumption
   */
  public void init(long start, long end, WeatherControllerLocal weatherController);
}
