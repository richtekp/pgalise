/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.persistence.Identifiable;
import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author richter
 */
public interface TimeSensitive extends Identifiable {
	
	Date getMeasureDate();
	
	Time getMeasureTime();
}
