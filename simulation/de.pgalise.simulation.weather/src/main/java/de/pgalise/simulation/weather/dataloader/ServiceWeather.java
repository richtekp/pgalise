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
 
package de.pgalise.simulation.weather.dataloader;

/**
 * Weather informations from weather services.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public class ServiceWeather implements Comparable<ServiceWeather> {

	/**
	 * Timestamp of the date
	 */
	private long timestamp;

	/**
	 * ID of the city
	 */
	private int cityId;

	/**
	 * relativ humidity
	 */
	private Float relativHumidity;

	/**
	 * temperature
	 */
	protected Float temperatureLow;

	/**
	 * temperature
	 */
	protected Float temperatureHigh;

	/**
	 * wind direction
	 */
	protected Float windDirection;

	/**
	 * wind velocity
	 */
	protected Float windVelocity;

	/**
	 * Default constructor
	 */
	public ServiceWeather() {
	}

	@Override
	public int compareTo(ServiceWeather weather) {
		long thisTime = this.timestamp;
		long anotherTime = weather.getTimestamp();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	public int getCityId() {
		return this.cityId;
	}

	public Float getRelativHumidity() {
		return this.relativHumidity;
	}

	public Float getTemperatureHigh() {
		return this.temperatureHigh;
	}

	public Float getTemperatureLow() {
		return this.temperatureLow;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public Float getWindDirection() {
		return this.windDirection;
	}

	public Float getWindVelocity() {
		return this.windVelocity;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setRelativHumidity(Float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	public void setTemperatureHigh(Float temperatureHigh) {
		this.temperatureHigh = temperatureHigh;
	}

	public void setTemperatureLow(Float temperatureLow) {
		this.temperatureLow = temperatureLow;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setWindDirection(Float windDirection) {
		this.windDirection = windDirection;
	}

	public void setWindVelocity(Float windVelocity) {
		this.windVelocity = windVelocity;
	}

}
