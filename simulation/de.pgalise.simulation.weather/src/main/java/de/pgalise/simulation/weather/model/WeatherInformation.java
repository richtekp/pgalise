/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

/**
 *
 * @author richter
 */
public interface WeatherInformation extends TimeSensitive {

	Float getRelativHumidity(); 
	
	Float getWindDirection() ;

	Float getWindVelocity();
}
