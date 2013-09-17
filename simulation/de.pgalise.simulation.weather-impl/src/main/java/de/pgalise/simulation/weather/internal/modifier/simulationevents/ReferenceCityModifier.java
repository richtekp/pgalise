/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.weather.internal.modifier.simulationevents;

import java.util.Properties;

import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.util.comparator.RelativHumidityComparator;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;
import de.pgalise.simulation.weather.internal.util.comparator.WindComparator;
import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.model.ServiceDataForecast;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.modifier.WeatherSimulationEventModifier;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import javax.measure.Measure;
import javax.measure.unit.SI;

/**
 * Changes the values to reference cities ({@link WeatherMapModifier} and {@link WeatherStrategy}).<br />
 * <br />
 * The file weather_decorators.properties describes the default properties for the implemented modifier. If no
 * parameters are given in the constructor of an implemented modifier, the standard properties of the file will be used.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (02.07.2012)
 */
public class ReferenceCityModifier extends WeatherSimulationEventModifier {

	/**
	 * Event type
	 */
	public static final WeatherEventEnum TYPE = WeatherEventEnum.REFERENCECITY;

	/**
	 * Order id
	 */
	public static final int ORDER_ID = 1;

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -2545514269078129147L;

	/**
	 * Constructor
	 * 
	 * @param props
	 *            Properties
	 * @param seed
	 *            Seed for random generators
	 */
	public ReferenceCityModifier(long seed, Properties props, WeatherLoader weatherLoader) {
		super(seed, props, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 */
	public ReferenceCityModifier(long seed, WeatherLoader weatherLoader) {
		super(seed, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 *            WeatherMap
	 * @param seed
	 *            Seed for random generators
	 */
	public ReferenceCityModifier(WeatherMap map, long seed, WeatherLoader weatherLoader) {
		super(map, seed, weatherLoader);
	}

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.database.Weather#deployChanges()
	 */
	@Override
	public void deployChanges() {
		if (this.getCity() != null) {
			
			// Get values from weather services
			ServiceDataForecast serviceForecast = this.getWeatherLoader().loadForecastServiceWeatherData(
					this.getSimulationTimestamp(), this.getCity());
			ServiceDataForecast serviceCurrent = this.getWeatherLoader().loadCurrentServiceWeatherData(
					this.getSimulationTimestamp(), this.getCity());

			// Temperature
			this.changeTemperature(serviceForecast.getTemperatureLow().floatValue(SI.CELSIUS), serviceForecast.getTemperatureHigh().floatValue(SI.CELSIUS));

			// Relativ humidity
			this.changeRelativHumidity(serviceCurrent.getRelativHumidity());

			// wind velocity
			this.changeWindVelocity(serviceCurrent.getWindVelocity());
		}

		// Super class
		super.deployChanges();
	}

	@Override
	public WeatherEventEnum getType() {
		return ReferenceCityModifier.TYPE;
	}

	/**
	 * Changes the relativ humidity
	 * 
	 * @param relativHumidity
	 *            relativ humidity of the weather service
	 */
	private void changeRelativHumidity(Float relativHumidity) {
		StationData max = this.getMaxValue(new RelativHumidityComparator());

		// Values exists?
		if ((max == null) || (relativHumidity == null)) {
			return;
		}

		// Calculate difference between service and reference
		float difference = relativHumidity - max.getRelativHumidity();

		// If there is a difference
		if (difference != 0) {
			// Changes all data
			float value;
			for (MutableStationData weather : this.getMap().values()) {
				value = weather.getRelativHumidity() + difference;
				if (value < 0) {
					weather.setRelativHumidity(0.0f);
				} else if (value > 100) {
					weather.setRelativHumidity(100.0f);
				} else {
					weather.setRelativHumidity(value);
				}
			}
		}
	}

	/**
	 * Changes the temperature
	 * 
	 * @param temperatureLow
	 *            Temperature (low) of the weather service
	 * @param temperatureHigh
	 *            Temperature (high) of the weather service
	 */
	private void changeTemperature(Float temperatureLow, Float temperatureHigh) {
		// Temperature values exists?
		if ((temperatureLow == null) || (temperatureHigh == null)) {
			return;
		}

		// Get limits
		StationData min = this.getMinValue(new TemperatureComparator());
		StationData max = this.getMaxValue(new TemperatureComparator());

		// Calculate difference between service and reference
		float differenceMin = temperatureLow - min.getTemperature().floatValue(SI.CELSIUS);
		// Calculate difference between service and reference
		float differenceMax = temperatureHigh - max.getTemperature().floatValue(SI.CELSIUS);

		// If there is a difference
		if ((differenceMin != 0) || (differenceMax != 0)) {

			// Changes all data
			float value1, value2;
			long timeMin, timeMax;
			for (MutableStationData weather : this.getMap().values()) {
				timeMin = Math.abs(weather.getMeasureTime().getTime()- min.getMeasureTime().getTime());
				timeMax = Math.abs(weather.getMeasureTime().getTime()- max.getMeasureTime().getTime());

				// Calculate value
				if (timeMax > timeMin) {
					value1 = weather.getTemperature().floatValue(SI.CELSIUS) + differenceMin;
					value2 = weather.getPerceivedTemperature() + differenceMin;
				} else {
					value1 = weather.getTemperature().floatValue(SI.CELSIUS) + differenceMax;
					value2 = weather.getPerceivedTemperature() + differenceMax;
				}

				// Set value
				weather.setTemperature(Measure.valueOf(value1, SI.CELSIUS));
				weather.setPerceivedTemperature(value2);
			}
		}

	}

	/**
	 * Changes the wind velocity
	 * 
	 * @param windVelocity
	 *            Wind velocity of the weather service
	 */
	private void changeWindVelocity(Float windVelocity) {
		// Get limit
		StationData max = this.getMaxValue(new WindComparator());

		// Wind velocity values exists?
		if ((max == null) || (windVelocity == null)) {
			return;
		}

		// Calculate difference between service and reference
		float difference = windVelocity - max.getWindVelocity();

		// If there is a difference
		if (difference != 0) {
			// Changes all data
			float value;
			for (MutableStationData weather : this.getMap().values()) {
				value = (float) ((weather.getWindVelocity() + difference) / 3.6);
				if (value < 0) {
					weather.setWindVelocity(0.0f);
				} else {
					weather.setWindVelocity(value);
				}
			}
		}
	}

	@Override
	protected void initDecorator() {
		if (this.getProps() != null) {
			this.setOrderID(Integer.parseInt(this.getProps().
				getProperty("referencecity_order_id")));
		} else {
			this.setOrderID(ReferenceCityModifier.ORDER_ID);
		}
	}
}
