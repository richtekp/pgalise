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
 
package de.pgalise.simulation.weather.util;

import de.pgalise.simulation.weather.modifier.WeatherStrategy;

/**
 * Helper for weather strategies
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 11, 2012)
 */
public class WeatherStrategyHelper {

	/**
	 * Strategy
	 */
	private WeatherStrategy strategy;

	/**
	 * Timestamp
	 */
	private long timestamp;

	/**
	 * Constructor
	 * 
	 * @param strategy
	 *            Strategy
	 * @param timestamp
	 *            Timestamp of the strategy
	 */
	public WeatherStrategyHelper(WeatherStrategy strategy, long timestamp) {
		super();
		this.strategy = strategy;
		this.timestamp = timestamp;
	}

	public int getOrderID() {
		return this.strategy.getOrderID();
	}

	public WeatherStrategy getStrategy() {
		return this.strategy;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setStrategy(WeatherStrategy strategy) {
		this.strategy = strategy;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
