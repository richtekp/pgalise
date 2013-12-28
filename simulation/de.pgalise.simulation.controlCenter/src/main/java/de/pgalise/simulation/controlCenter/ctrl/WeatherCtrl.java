/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
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
}
