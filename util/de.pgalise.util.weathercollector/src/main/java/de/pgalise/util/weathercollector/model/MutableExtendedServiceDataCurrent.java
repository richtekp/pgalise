/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.weather.model.MutableServiceDataCurrent;
import de.pgalise.simulation.weather.model.MutableServiceDataForecast;
import java.sql.Time;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public interface MutableExtendedServiceDataCurrent extends ExtendedServiceDataCurrent, MutableServiceDataCurrent {
	
	void setSunrise(Time sunrise);

	void setSunset(Time sunset);

	void setVisibility(Float visibility);
}
