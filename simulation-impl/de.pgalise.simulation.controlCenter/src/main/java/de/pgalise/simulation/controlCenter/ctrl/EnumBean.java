/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@ApplicationScoped
public class EnumBean {

	/**
	 * Creates a new instance of enumBean
	 */
	public EnumBean() {
	}
	
	public WeatherEventTypeEnum[] getWeatherEventTypes() {
	 return WeatherEventTypeEnum.values();
	}
}
