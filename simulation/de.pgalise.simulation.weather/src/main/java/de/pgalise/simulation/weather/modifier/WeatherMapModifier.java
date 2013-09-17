/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.modifier;

import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.WeatherCondition;

/**
 *
 * @param <C> 
 * @author richter
 */
public interface WeatherMapModifier<C extends WeatherCondition> extends WeatherStrategy {
	WeatherMap getMap();


	long getSimulationTimestamp();

	WeatherLoader<C> getWeatherLoader();
}
