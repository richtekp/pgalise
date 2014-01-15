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
 
package de.pgalise.simulation.weather.internal.modifier;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.service.WeatherService;

/**
 * Strategy context for the weather modifier (Strategy pattern). This class is used in the {@link WeatherService}.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public class WeatherStrategyContext {

	/**
	 * Strategy
	 */
	private WeatherStrategy strategy = null;

	/**
	 * Default constructor
	 */
	public WeatherStrategyContext() {
	}

	/**
	 * Constructor
	 * 
	 * @param strategy
	 *            Strategy
	 */
	public WeatherStrategyContext(WeatherStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Execute the current strategy
	 * 
	 * @param map
	 *            WeatherMap
	 * @param city
	 *            City
	 * @param timestamp
	 *            Timestamp
	 */
	public void execute(WeatherMap map, City city, long timestamp) {
		if (this.strategy != null) {
			this.strategy.setMap(map);
			this.strategy.setCity(city);
			this.strategy.setSimulationTimestamp(timestamp);

			// Deploy changes
			this.strategy.deployChanges();
		}
	}

	public WeatherStrategy getStrategy() {
		return this.strategy;
	}

	public void setStrategy(WeatherStrategy strategy) {
		this.strategy = strategy;
	}

}
