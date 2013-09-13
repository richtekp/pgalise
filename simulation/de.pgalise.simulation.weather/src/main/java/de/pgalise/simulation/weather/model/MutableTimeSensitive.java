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
public interface MutableTimeSensitive extends BaseTimeSensitive {
	

	public void setMeasureTime(Time measureTime) ;
	
	public void setMeasureDate(Date measureDate);
}
