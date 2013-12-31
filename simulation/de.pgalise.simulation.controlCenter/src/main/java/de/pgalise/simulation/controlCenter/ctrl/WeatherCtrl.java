/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.ValueWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
public class WeatherCtrl {

	private WeatherEventViewData currentWeatherEventViewData = new WeatherEventViewData();
	@EJB
	private IdGenerator idGenerator;
	private boolean aggregatedWeatherDataEnabled;
	private Date chosenDate;
	private int chosenDurationMinutes;
	private WeatherEventType chosenWeatherEventType;
	private float chosenValue;
	private List<ValueWeatherEvent> weatherEvents = new LinkedList<>();

	/**
	 * Creates a new instance of WeatherCtrl
	 */
	public WeatherCtrl() {
	}

	public void setCurrentWeatherEventViewData(
		WeatherEventViewData currentWeatherEventViewData) {
		this.currentWeatherEventViewData = currentWeatherEventViewData;
	}

	public WeatherEventViewData getCurrentWeatherEventViewData() {
		return currentWeatherEventViewData;
	}

	public void setAggregatedWeatherDataEnabled(
		boolean aggregatedWeatherDataEnabled) {
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}

	public boolean getAggregatedWeatherDataEnabled() {
		return aggregatedWeatherDataEnabled;
	}

	public void addWeatherEvent() {
		throw new UnsupportedOperationException("collection can be accessed directly on client");
//		currentWeatherEventViewData.getEvents().add(
//			new ChangeWeatherEvent(
//				idGenerator.getNextId(),
//				currentWeatherEventViewData.getEventType(),
//				currentWeatherEventViewData.getValue(),
//				currentWeatherEventViewData.getTimestamp(),
//				currentWeatherEventViewData.getDuration()));
	}
	
	public void deleteWeatherEvent(){
		throw new UnsupportedOperationException("collection can be accessed directly on client");
	}

	public void setChosenDate(Date chosenDate) {
		this.chosenDate = chosenDate;
	}

	public Date getChosenDate() {
		return chosenDate;
	}

	public void setChosenDurationMinutes(int chosenDurationMinutes) {
		this.chosenDurationMinutes = chosenDurationMinutes;
	}

	public int getChosenDurationMinutes() {
		return chosenDurationMinutes;
	}

	public void setChosenWeatherEventType(WeatherEventType chosenWeatherEventType) {
		this.chosenWeatherEventType = chosenWeatherEventType;
	}

	public WeatherEventType getChosenWeatherEventType() {
		return chosenWeatherEventType;
	}

	public void setChosenValue(float chosenValue) {
		this.chosenValue = chosenValue;
	}

	public float getChosenValue() {
		return chosenValue;
	}

	public void setWeatherEvents(List<ValueWeatherEvent> weatherEvents) {
		this.weatherEvents = weatherEvents;
	}

	public List<ValueWeatherEvent> getWeatherEvents() {
		return weatherEvents;
	}
	
	public void deleteWeatherEvent(ValueWeatherEvent event) {
		weatherEvents.remove(event);
	}
}
