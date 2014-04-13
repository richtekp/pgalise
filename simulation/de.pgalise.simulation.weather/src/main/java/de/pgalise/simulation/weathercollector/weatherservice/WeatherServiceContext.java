/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weathercollector.weatherservice;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import java.io.Serializable;

/**
 *
 * @author richter
 */
public interface WeatherServiceContext extends Serializable {

  /**
   * Returns the best ServiceData
   *
   * @param city City
   * @param databaseManager
   * @return Best ServiceData
   */
  public ServiceDataHelper getBestWeather(City city,
    DatabaseManager databaseManager);

  /**
   * Returns the weather informations with the help of a random strategy for the
   * given city
   *
   * @param city City
   * @param databaseManager
   * @return ServiceData object
   * @throws ReadServiceDataException Data can not be read by strategy
   */
  public ServiceDataHelper getSingleWeather(City city,
    DatabaseManager databaseManager) throws ReadServiceDataException;

}
