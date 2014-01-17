/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weathercollector;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.simulation.weathercollector.weatherstation.StationStrategy;
import java.util.Set;

/**
 * This program gets various informations of weather stations and weather
 * services and saves these information to the given database. It only retrieves
 * current weather data and short term forecast data, it can not be used to
 * setup historical weather data!
 *
 * @author richter
 */
public interface WeatherCollector {

  void collectServiceData(
    Set<ServiceStrategy> serviceStrategys);

  /**
   * Collect weather informations of the weather services
   *
   */
  public void collectServiceData();

  ServiceDataHelper retrieveServiceDataCurrent(
    ServiceStrategy serviceStrategy,
    City city);

  public void collectStationData(
    Set<StationStrategy> stationStrategys);

  /**
   * Collect weather informations of the weather stations
   *
   */
  public void collectStationData();

}
