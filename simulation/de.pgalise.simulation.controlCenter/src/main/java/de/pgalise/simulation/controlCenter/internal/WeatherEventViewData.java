/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import java.util.List;

/**
 *
 * @author richter
 */
/*
decorator pattern
*/
public class WeatherEventViewData {
	private long datetime;
	private long duration;
	private WeatherEventViewData delegator; 
	private Object value;
	private List<WeatherEvent> events;

	public WeatherEventViewData(long datetime,
		long duration,
		WeatherEventViewData delegator,
		Object value,
		List<WeatherEvent> events) {
		this.datetime = datetime;
		this.duration = duration;
		this.delegator = delegator;
		this.value = value;
		this.events = events;
	}

	public WeatherEventViewData() {
	}

	/**
	 * @return the datetime
	 */
	public long getDatetime() {
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * @return the delegator
	 */
	public WeatherEventViewData getDelegator() {
		return delegator;
	}

	/**
	 * @param delegator the delegator to set
	 */
	public void setDelegator(
		WeatherEventViewData delegator) {
		this.delegator = delegator;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the events
	 */
	public List<WeatherEvent> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(
		List<WeatherEvent> events) {
		this.events = events;
	}
}
