/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author richter
 */
public interface MutableBaseWeatherInformation extends WeatherInformation {

	void setMeasureDate(Date measureDate);

	void setMeasureTime(Time measureTime) ;


	void setRelativHumidity(Float relativHumidity) ;

	void setWindDirection(Float windDirection);

	void setWindVelocity(Float windVelocity);
}
