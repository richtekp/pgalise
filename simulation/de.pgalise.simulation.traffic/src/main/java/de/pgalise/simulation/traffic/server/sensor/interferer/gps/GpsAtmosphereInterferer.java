/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.sensor.interferer.gps;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;

/**
 *
 * @author richter
 */
public interface GpsAtmosphereInterferer extends GpsInterferer {
  /**
   * File path for property file
   */
  String PROPERTIES_FILE_PATH = "/interferer_gps_atmosphere.properties";

  BaseCoordinate interfere(final BaseCoordinate mutablePosition,
    final BaseCoordinate realPosition,
    final long simTime);
  
}
