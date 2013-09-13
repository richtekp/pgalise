/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.model;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.model.MutableTimeSensitive;
import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author richter
 */
public interface MutableServiceDataHelper extends ServiceDataHelper<MutableExtendedServiceDataCurrent, MutableExtendedServiceDataForecast>, MutableTimeSensitive {
	

	public void setApicity(String apicity) ;

	public void setCity(City city) ;

	public void setCurrentCondition(MutableExtendedServiceDataCurrent currentCondition) ;

	public void setSource(String source) ;
}
