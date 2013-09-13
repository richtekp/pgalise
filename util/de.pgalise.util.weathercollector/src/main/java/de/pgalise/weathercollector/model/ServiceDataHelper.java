/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.model;

import de.pgalise.simulation.shared.city.City;
import java.sql.Timestamp;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface ServiceDataHelper<S extends ExtendedServiceDataCurrent, T extends ExtendedServiceDataForecast> extends ServiceDataHelperCompleter<S,T> {
	
	public String getApicity() ;

	public City getCity() ;

	public S getCurrentCondition() ;

	public Set<T> getForecastConditions() ;

	public Timestamp getMeasureTimestamp() ;

	public String getSource();

}
