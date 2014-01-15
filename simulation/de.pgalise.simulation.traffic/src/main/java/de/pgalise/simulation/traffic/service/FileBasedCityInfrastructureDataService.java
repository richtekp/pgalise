/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author richter
 */
public interface FileBasedCityInfrastructureDataService extends CityInfrastructureDataService {
	
	void parse(File osmFile, File busStopFile) throws IOException;

	void parse(InputStream osmIN,
		InputStream busStopIN) throws IOException;
	
}
