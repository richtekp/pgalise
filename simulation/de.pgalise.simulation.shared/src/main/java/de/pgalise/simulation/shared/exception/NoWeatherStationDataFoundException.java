/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.exception;

/**
 *
 * @author richter
 */
public class NoWeatherStationDataFoundException extends NoWeatherDataFoundException {
	private static final long serialVersionUID = 1L;

	public NoWeatherStationDataFoundException(long timestamp) {
		super(timestamp);
	}
	
}
