/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.modifier;

import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.entity.WeatherCondition;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface WeatherMapModifier extends WeatherStrategy {
	WeatherMap getMap();


	long getSimulationTimestamp();

	WeatherLoader getWeatherLoader();
}
