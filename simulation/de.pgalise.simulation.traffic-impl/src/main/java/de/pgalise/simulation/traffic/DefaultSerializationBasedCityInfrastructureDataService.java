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
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.shared.city.CityInfrastructureDataService;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.city.Building;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.CityInfrastructureDataService;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.SerializationBasedCityInfrastructureDataService;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Stateful;

import org.slf4j.Logger;

/**
 * Service to create OSM city infrastructure data.
 *
 * @author Timo
 * @author Mustafa
 */
@Stateful
public class DefaultSerializationBasedCityInfrastructureDataService implements SerializationBasedCityInfrastructureDataService {

	/**
	 * Pattern to get filenames without postfix.
	 */
	private static final Pattern fileNamePattern = Pattern.compile("[^\\.]+");

	/**
	 * Logger
	 */
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(
		DefaultSerializationBasedCityInfrastructureDataService.class);

	private CityInfrastructureData cityInfrastructureData;

	/**
	 * Default
	 */
	public DefaultSerializationBasedCityInfrastructureDataService() {
	}

	/**
	 * Returns an instance of {@link CityInfrastructureDataService}. If the file is
	 * already parsed and persistent, it can be loaded, otherwise it will be
	 * parsed.
	 *
	 * @param osm OSM file
	 * @param busstops bus stops file
	 * @param buildingEnergyProfileStrategy BuildingEnergyProfileStrategy
	 * @return CityInfrastructureDataService
	 * @throws IOException
	 */
	@Override
	public void parse(File osm,
		File busstops,
		BuildingEnergyProfileStrategy buildingEnergyProfileStrategy) throws IOException {

		Matcher osmNameMatcher = fileNamePattern.matcher(osm.getName());
		osmNameMatcher.find();
		Matcher busstopNameMatcher = fileNamePattern.matcher(busstops.getName());
		busstopNameMatcher.find();
		File cityInfrastructuraDataFile = new File(
			osm.getParent() + "/" + osmNameMatcher.group() + "_"
			+ busstopNameMatcher.group() + ".bin");

		/* Already parsed, correct osm/busstop and correct class version: */
		if (cityInfrastructuraDataFile.exists() && osm.lastModified() < cityInfrastructuraDataFile.
			lastModified()
			&& busstops.lastModified() < cityInfrastructuraDataFile.lastModified()) {

			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(cityInfrastructuraDataFile.getAbsolutePath());
				ois = new ObjectInputStream(fis);
				this.cityInfrastructureData= (CityInfrastructureData) ois.readObject();
			} catch (Exception e) {
				log.warn(e.getLocalizedMessage());
			} finally {
				try {
					ois.close();
				} catch (Exception e) {
				}
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}

		/* Files have to be parsed and saved: */
		if (cityInfrastructuraDataFile.exists()) {
			cityInfrastructuraDataFile.delete();
		}
		
		FileOutputStream fis = null;
		ObjectOutputStream oos = null;
		try {
			fis = new FileOutputStream(cityInfrastructuraDataFile.getAbsolutePath());
			oos = new ObjectOutputStream(fis);
			oos.writeObject(cityInfrastructureData);
		} catch (Exception e) {
			log.warn(e.getLocalizedMessage());
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
	}

	@Override
	public Envelope getBoundary() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Map<EnergyProfileEnum, List<Building>> getBuildings(
		JaxRSCoordinate geolocation,
		int radiusInMeter) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Building> getBuildingsInRadius(JaxRSCoordinate centerPoint,
		int radiusInMeter) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public CityInfrastructureData createCityInfrastructureData()  {
		return this.cityInfrastructureData;
	}

	@Override
	public NavigationNode getNearestNode(double latitude,
		double longitude) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public NavigationNode getNearestStreetNode(double latitude,
		double longitude) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public NavigationNode getNearestJunctionNode(double latitude,
		double longitude) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<NavigationNode> getNodesInBoundary(Envelope boundary) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
