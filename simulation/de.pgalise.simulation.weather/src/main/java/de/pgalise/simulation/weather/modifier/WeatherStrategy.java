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
package de.pgalise.simulation.weather.modifier;

import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import de.pgalise.simulation.weather.dataloader.WeatherMap;

/**
 * Interface for weather strategies
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public interface WeatherStrategy {

  /**
   * Deploy changes to the map (Decorator-Pattern)
   */
  public void deployChanges();

  /**
   * Returns the order id
   *
   * @return order ID
   */
  public int getOrderID();

  /**
   * Returns the type of the weather event
   *
   * @return WeatherEventTypeEnum
   */
  public WeatherEventTypeEnum getType();

  /**
   * Set the map
   *
   * @param map WeatherMap
   */
  public void setMap(WeatherMap map);

  /**
   * Set the simulation timestamp
   *
   * @param timestamp Current simulation timestamp
   */
  public void setSimulationTimestamp(long timestamp);

}
