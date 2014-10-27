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
package de.pgalise.simulation.weather.positionconverter;

import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;

/**
 * The {@link WeatherService} provides only weather information for one
 * reference point of the simulation city. Therefore, the interface
 * {@link WeatherPostionConverter} makes methods available that convert the
 * reference value to a modified value on another position of the used graph.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 12, 2012)
 */
public interface WeatherPositionConverter {

  /**
   * Returns the modified reference value for the given position
   *
   * @param <T>
   * @param key WeatherParameterEnum
   * @param time Timestamp
   * @param position Position
   * @param refValue Reference value
   * @param grid
   * @throws IllegalStateException if the grid hasn't been specified before
   * @return Modified value
   */
  public <T extends Number> T getValue(WeatherParameterEnum key,
    long time,
    BaseCoordinate position,
    T refValue,
    Polygon grid) throws IllegalStateException;

  void init(WeatherPositionInitParameter initParameter);
  
  void setGrid(Polygon grid);
}
