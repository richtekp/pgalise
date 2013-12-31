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
 
package de.pgalise.simulation.controlCenter.internal.util.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficInfrastructureData;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;

/**
 * Interface for all services to create city infrastructure data
 * with open street map files.
 * @author Timo
 */
public interface MapCityInfrastructureDataService {
	/**
	 * Returns an instance of @CityInfrastructureData.
	 * @param osm
	 * 			the used openstreetmap file
	 * @param busstops
	 * 			the used bus stop file
	 * @param buildingEnergyProfileStrategy
	 * 			the used building energy profile strategy
	 * @return
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public TrafficInfrastructureData createCityInfrastructureData(File osm, File busstops, 
			BuildingEnergyProfileStrategy buildingEnergyProfileStrategy, TrafficGraph graph) throws FileNotFoundException, IOException;
}
