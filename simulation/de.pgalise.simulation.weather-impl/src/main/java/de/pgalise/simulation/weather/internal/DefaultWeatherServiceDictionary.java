/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather.internal;

import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.internal.AbstractServiceDictionary;
import de.pgalise.simulation.weather.WeatherServiceDictionary;
import de.pgalise.simulation.service.manager.ServiceHandler;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author richter
 */
//@Singleton(name = "de.pgalise.simulation.staticsensor.WeatherServiceDictionary")
public class DefaultWeatherServiceDictionary extends AbstractServiceDictionary implements WeatherServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(WeatherServiceDictionary.class);

	@Override
	protected void initBeforeRead(List<ServiceHandler<Service>> list) {
		
		 list.add(new ServiceHandler<Service>() {

			@Override
			public String getName() {
				return WEATHER_CONTROLLER;
			}

			@Override
			public void handle(String server,
				Service service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				getServices().put(getName(), service);
			}
		});
	}
	
	
	
}
