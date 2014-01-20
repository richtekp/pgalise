/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weathercollector.weatherservice;

import de.pgalise.simulation.weathercollector.util.DatabaseManager;

/**
 *
 * @author richter
 */
public interface WeatherServiceManager {

  public void saveInformations();

  /**
   * @return the saver
   */
  public DatabaseManager getSaver();

}
