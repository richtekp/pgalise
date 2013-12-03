/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.controller;

import de.pgalise.simulation.shared.event.weather.WeatherEventHelper;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author richter
 */
public interface StartParameter extends Serializable {

	List<WeatherEventHelper> getWeatherEventHelperList();
	
	void setWeatherEventHelperList(List<WeatherEventHelper> eventHelpers);

	boolean isAggregatedWeatherDataEnabled();
	
	void setAggregatedWeatherDataEnabled(boolean b);
	
}
