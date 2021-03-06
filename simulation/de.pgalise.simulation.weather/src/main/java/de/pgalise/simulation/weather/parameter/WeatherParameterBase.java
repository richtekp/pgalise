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
package de.pgalise.simulation.weather.parameter;

import java.sql.Date;

import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.unit.SI;

/**
 * Abstract super class for weather parameter
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (07.07.2012)
 */
public abstract class WeatherParameterBase implements WeatherParameter {

  /**
   * WeatherService
   */
  private WeatherService weatherService;

  /**
   * Default constructor
   */
  protected WeatherParameterBase() {
  }

  /**
   * Constructor
   *
   * @param weatherService WeatherService
   */
  public WeatherParameterBase(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  /**
   * Set the WeatherService
   *
   * @param base WeatherService
   */
  @Override
  public void setWeatherService(WeatherService base) {
    this.weatherService = base;
  }

  private AbstractStationData a
    = new StationDataNormal(2000000L,
      new Date(System.currentTimeMillis()),
      new Time(System.currentTimeMillis()),
      1,
      2,
      4.0f,
      Measure.valueOf(1.0f,
        SI.CELSIUS),
      1.0f,
      3,
      Float.NaN,
      4.0f,
      5.0f
    );

  /**
   * Returns the weather object to the given time. If there is no weather object
   * for that time, than it will return the next value for the next reference
   * time.
   *
   * @param time Timestamp
   * @return weather object to the given time
   * @throws NoWeatherDataFoundException There is no value for the given time.
   */
  protected AbstractStationData getWeather(long time) {
    // Check if it is the right date
    if ((this.getWeatherService().getLoadedTimestamp() < 1)
      || !DateConverter.isTheSameDay(this.weatherService.getLoadedTimestamp(),
        time)) {
      throw new IllegalArgumentException(
        "Wrong date: " + new Date(time) + "! Date for the simulation is:  "
        + new Date(this.weatherService.getLoadedTimestamp()));
    }

    return a;

    //@Todo:
    // Get weather
//    AbstractStationData weather = this.weatherService.getReferenceValues().
//      get(time);
//    if (weather == null) {
//
//      // Calculate next minute
//      long newTime = time + (WeatherMap.INTERPOLATE_INTERVAL - (time % WeatherMap.INTERPOLATE_INTERVAL));
//      weather = this.weatherService.getReferenceValues().get(newTime);
//
//      // Get weather
//      if (weather == null) {
//
//        // Look for the last 5 Minutes
//        for (int i = 0; i < 5; i++) {
//          newTime -= WeatherMap.INTERPOLATE_INTERVAL;
//          weather = this.weatherService.getReferenceValues().get(newTime);
//          if (weather != null) {
//            break;
//          }
//        }
//      }
//    }
//
//    // Returns
//    return weather;
  }

  /**
   * @return the weatherService
   */
  public WeatherService getWeatherService() {
    return weatherService;
  }
}
