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
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.ReferenceCityEvaluator;
import de.pgalise.simulation.weather.internal.util.comparator.CityComparator;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateful;

/**
 * Evaluator for reference cities.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 17, 2012)
 */
@Stateful
public class PointsReferenceCityEvaluator implements ReferenceCityEvaluator {

  @Override
  public City evaluate(List<City> list,
    City simCity) {
    // Set Rate for Cities
    for (City city : list) {
      this.setRate(city,
        simCity);
    }

    // Get max
    City resultCity = Collections.max(list,
      new CityComparator());

    // Return best city
    return (resultCity.getRate() <= 0) ? null : resultCity;
  }

  /**
   * Rate the city against the simulation city
   *
   * @param city City from database
   * @param simCity City of the simulation
   */
  public void setRate(City city,
    City simCity) {
    int points = 0;

    // Population
    int difference = Math.abs(simCity.getPopulation() - city.getPopulation());
    if (city.getPopulation() > 1500000) {
      if (difference < 1500000) {
        points++;
      }
    } else {
      if (difference < 150000) {
        points++;
      }
    }

    // Altitude
    difference = Math.abs(simCity.getAltitude() - city.getAltitude());
    if (difference < 30) {
      points++;
    }

    // River
    if (city.isNearRiver() == simCity.isNearRiver()) {
      points++;
    }

    // Sea
    if (city.isNearSea() == simCity.isNearSea()) {
      points++;
    }

    // Set Rate
    city.setRate(points);
  }

}
