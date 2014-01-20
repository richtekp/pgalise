/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weathercollector.util;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.WeatherCondition;
import de.pgalise.simulation.weathercollector.exceptions.SaveStationDataException;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import java.util.List;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface DatabaseManager {

	/**
	 * Returns a list of reference cities from the database
	 *
	 * @return list of cities
	 */
	public List<City> getReferenceCities();

	/**
	 * Saves all informations from the weather services
	 *
	 * @param weather ServiceData instance
	 */
	public void saveServiceData(ServiceDataHelper weather);

	/**
	 * Saves a list of station data objects
	 *
	 * @param list List of station data objects
	 * @return true if all the list could be saved
	 * @throws SaveStationDataException Will be thrown if the list can not be
	 * saved
	 */
	public boolean saveStationDataSet(Set<AbstractStationData> list) throws SaveStationDataException;

	WeatherCondition getCondition(String condition);
}
