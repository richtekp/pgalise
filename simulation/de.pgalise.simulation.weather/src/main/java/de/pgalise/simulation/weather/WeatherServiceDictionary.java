/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.weather.service.WeatherController;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface WeatherServiceDictionary extends ServiceDictionary {
	
	public static final String WEATHER_CONTROLLER = WeatherController.class.getName();
	public final static Set<String> WEATHER_SERVICES = new HashSet<String>(
		ServiceDictionary.SERVICES){{
			add(WEATHER_CONTROLLER);
		}};
}
