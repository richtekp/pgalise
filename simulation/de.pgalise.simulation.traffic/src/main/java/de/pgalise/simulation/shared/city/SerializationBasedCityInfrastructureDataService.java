/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author richter
 */
public interface SerializationBasedCityInfrastructureDataService extends CityInfrastructureDataService {
	
	void parse(
		File osm,
		File busstops,
		BuildingEnergyProfileStrategy buildingEnergyProfileStrategy) throws IOException;
}
