/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import java.sql.Date;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;

/**
 *
 * @author richter
 */
public interface MutableStationData extends StationData {
	
	void setMeasureDate(Date measureDate);
	
	void setMeasureTime(Time measureTime);

	void setRelativHumidity(Float relativeHumidity); 

	void setWindDirection(Float windDirection) ;

	void setWindVelocity(Float windVelocity);

	void setAirPressure(Integer airPressure);

	void setLightIntensity(Integer lightIntensity);

	void setPerceivedTemperature(Float perceivedTemperature) ;

	void setPrecipitationAmount(Float precipitationAmount) ;

	void setRadiation(Integer radiation) ;

	void setTemperature(Measure<Float, Temperature> temperature) ;
	
}
