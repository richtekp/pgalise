/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.io.File;
import java.io.IOException;

/**
 * Can be used to parse {@link CityInfCityTrafficDataa file to which a
 * java serialization stream has been written to before.
 *
 * @author richter
 */
public interface SerializationBasedCityDataService extends
  CityDataService {

  /**
   *
   * @param osm
   * @param busstops
   * @param buildingEnergyProfileStrategy
   * @throws IOException
   */
  void parse(
    File osm,
    File busstops,
    BuildingEnergyProfileStrategy buildingEnergyProfileStrategy) throws IOException;
}
