/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.event.weather;

import de.pgalise.simulation.shared.event.EventType;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author richter
 */
public abstract class ValueWeatherEvent extends WeatherEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * Specified value @TODO: move downward in hierarchy because not needed in
	 * NewDayEvent
	 */
	private Float value;

	public ValueWeatherEvent() {
	}

	public ValueWeatherEvent(Long id,
		long timestamp,
		long duration,Float value) {
		super(id,
			timestamp,
			duration);
		this.value = value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Float getValue() {
		return value;
	}
	
}
