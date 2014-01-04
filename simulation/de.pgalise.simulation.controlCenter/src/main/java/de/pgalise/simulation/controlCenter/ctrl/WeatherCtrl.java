/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.ValueWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventType;
import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class WeatherCtrl {
	@EJB
	private IdGenerator idGenerator;
	private boolean aggregatedWeatherDataEnabled = true;
	private Date chosenTimestamp = GregorianCalendar.getInstance().getTime();
	private int chosenDurationMinutes = 180;
	private WeatherEventTypeEnum chosenWeatherEventType = WeatherEventTypeEnum.COLDDAY;
	private float chosenValue = 1.0f;
	private List<ChangeWeatherEvent> weatherEvents = new LinkedList<>();

	/**
	 * Creates a new instance of WeatherCtrl
	 */
	public WeatherCtrl() {
	}

	public void setAggregatedWeatherDataEnabled(
		boolean aggregatedWeatherDataEnabled) {
		this.aggregatedWeatherDataEnabled = aggregatedWeatherDataEnabled;
	}

	public boolean getAggregatedWeatherDataEnabled() {
		return aggregatedWeatherDataEnabled;
	}

	public void addWeatherEvent() {
		weatherEvents.add(
			new ChangeWeatherEvent(
				idGenerator.getNextId(),
				chosenWeatherEventType,
				chosenValue,
				chosenTimestamp.getTime(),
				chosenDurationMinutes));
	}
	
	public void deleteWeatherEvent(){
		throw new UnsupportedOperationException("collection can be accessed directly on client");
	}

	public void setChosenTimestamp(Date chosenTimestamp) {
		this.chosenTimestamp = chosenTimestamp;
	}

	public Date getChosenTimestamp() {
		return chosenTimestamp;
	}

	public void setChosenDurationMinutes(int chosenDurationMinutes) {
		this.chosenDurationMinutes = chosenDurationMinutes;
	}

	public int getChosenDurationMinutes() {
		return chosenDurationMinutes;
	}

	public void setChosenWeatherEventType(WeatherEventTypeEnum chosenWeatherEventType) {
		this.chosenWeatherEventType = chosenWeatherEventType;
	}

	public WeatherEventTypeEnum getChosenWeatherEventType() {
		return chosenWeatherEventType;
	}

	public void setChosenValue(float chosenValue) {
		this.chosenValue = chosenValue;
	}

	public float getChosenValue() {
		return chosenValue;
	}

	public void setWeatherEvents(List<ChangeWeatherEvent> weatherEvents) {
		this.weatherEvents = weatherEvents;
	}

	public List<ChangeWeatherEvent> getWeatherEvents() {
		return weatherEvents;
	}
	
	public void deleteWeatherEvent(ValueWeatherEvent event) {
		weatherEvents.remove(event);
	}
}
