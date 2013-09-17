/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.persistence.Identifiable;

/**
 *
 * @author richter
 */
public interface WeatherCondition extends Identifiable {
	/**
	 * Condition code for unknown weather condition
	 */
	public static final int UNKNOWN_CONDITION_CODE = 3200;

	public String getStringRepresentation() ;

	public int getCode() ;
}
