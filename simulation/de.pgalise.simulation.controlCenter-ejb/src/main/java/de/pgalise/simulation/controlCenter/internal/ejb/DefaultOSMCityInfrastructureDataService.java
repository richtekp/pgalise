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
 
package de.pgalise.simulation.controlCenter.internal.ejb;

import de.pgalise.simulation.controlCenter.internal.util.service.MapCityInfrastructureDataService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.simulation.traffic.OSMCityInfrastructureData;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficInfrastructureData;

/**
 * The default implementation of {@link MapCityInfrastructureDataService}.
 * 
 * @author Timo
 */
public class DefaultOSMCityInfrastructureDataService implements MapCityInfrastructureDataService {
	/**
	 * Pattern to get filenames without postfix.
	 */
	private static final Pattern fileNamePattern = Pattern.compile("[^\\.]+");
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(DefaultOSMCityInfrastructureDataService.class);

	/**
	 * Default
	 */
	public DefaultOSMCityInfrastructureDataService() {
	}

	/**
	 * Returns an instance of @CityInfrastructureData. If the file is already parsed and persistent, it can be loaded,
	 * otherwise it will be parsed.
	 * 
	 * @param osm
	 * @param busstops
	 * @param buildingEnergyProfileStrategy
	 * @return
	 * @throws IOException
	 */
	@Override
	public TrafficInfrastructureData createCityInfrastructureData(File osm, File busstops,
			BuildingEnergyProfileStrategy buildingEnergyProfileStrategy, TrafficGraph graph) throws IOException {

		Matcher osmNameMatcher = fileNamePattern.matcher(osm.getName());
		osmNameMatcher.find();
		Matcher busstopNameMatcher = fileNamePattern.matcher(busstops.getName());
		busstopNameMatcher.find();
		File cityInfrastructuraDataFile = new File(osm.getParent() + "/" + osmNameMatcher.group() + "_"
				+ busstopNameMatcher.group() + ".bin");

		/* Already parsed, correct osm/busstop and correct class version: */
		if (cityInfrastructuraDataFile.exists() && osm.lastModified() < cityInfrastructuraDataFile.lastModified()
				&& busstops.lastModified() < cityInfrastructuraDataFile.lastModified()) {

			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(cityInfrastructuraDataFile.getAbsolutePath());
				ois = new ObjectInputStream(fis);
				return (TrafficInfrastructureData) ois.readObject();
			} catch (Exception e) {
				log.error("Exception", e);
			} finally {
				try {
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/* Files have to be parsed and saved: */

		if (cityInfrastructuraDataFile.exists()) {
			cityInfrastructuraDataFile.delete();
		}
		TrafficInfrastructureData cityInfrastructureData = new OSMCityInfrastructureData(
			new FileInputStream(osm),
			new FileInputStream(busstops), 
			buildingEnergyProfileStrategy,
			graph
		);

		FileOutputStream fis = null;
		ObjectOutputStream oos = null;
		try {
			fis = new FileOutputStream(cityInfrastructuraDataFile.getAbsolutePath());
			oos = new ObjectOutputStream(fis);
			oos.writeObject(cityInfrastructureData);
		} catch (Exception e) {
			log.error("Exception", e);
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}

		return cityInfrastructureData;
	}
}
