/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventType;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author richter
 */
/*
decorator pattern
*/
@ManagedBean
@SessionScoped
public class WeatherEventViewData implements Serializable {
	private static final long serialVersionUID = 1L;
	private long timestamp;
	private long duration;
	private WeatherEventViewData delegator; 
	private Float value;
	private List<WeatherEvent> events;
	private WeatherEventType eventType;

	public WeatherEventViewData(long timestamp,
		long duration,
		WeatherEventViewData delegator,
		Float value,
		List<WeatherEvent> events) {
		this.timestamp = timestamp;
		this.duration = duration;
		this.delegator = delegator;
		this.value = value;
		this.events = events;
	}

	public WeatherEventViewData() {
	}

	public void setEventType(WeatherEventType eventType) {
		this.eventType = eventType;
	}

	public WeatherEventType getEventType() {
		return eventType;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param datetime the timestamp to set
	 */
	public void setDatetime(long datetime) {
		this.timestamp = datetime;
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
	public Float getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Float value) {
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
