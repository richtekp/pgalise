/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.ServiceDataForecast;
import de.pgalise.util.weathercollector.WeatherCollector;
import de.pgalise.util.weathercollector.app.DefaultWeatherCollector;

/**
 *
 * @author richter
 */
public class LazyDatabaseWeatherLoader implements WeatherLoader{
	private WeatherCollector weatherLoader = new DefaultWeatherCollector();

	@Override
	public boolean checkStationDataForDay(long timestamp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ServiceDataForecast loadCurrentServiceWeatherData(long timestamp,
		City city) throws NoWeatherDataFoundException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ServiceDataForecast loadForecastServiceWeatherData(long timestamp,
		City city) throws NoWeatherDataFoundException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public WeatherMap loadStationData(long timestamp) throws NoWeatherDataFoundException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setLoadOption(boolean takeNormalData) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
