/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.event.weather;

import de.pgalise.simulation.shared.event.EventType;

/**
 *
 * @author richter
 */
public interface WeatherEventType extends EventType {
	int getMinValue();
	
	int getMaxValue();
}
