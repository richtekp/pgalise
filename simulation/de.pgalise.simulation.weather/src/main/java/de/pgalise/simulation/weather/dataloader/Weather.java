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

import de.pgalise.simulation.weather.modifier.WeatherStrategy;

/**
 * The class {@link Weather} represents every single weather information entry to a particular timestamp. These values
 * can be modified by {@link WeatherStrategy}.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public class Weather implements Comparable<Weather> {

	/**
	 * Interpolate interval
	 */
	public static final int INTERPOLATE_INTERVAL = 60000;

	/**
	 * air pressure
	 */
	protected int airPressure;

	/**
	 * light intensity
	 */
	protected int lightIntensity;

	/**
	 * Timestamp
	 */
	protected long timestamp;

	/**
	 * perceived temperature
	 */
	protected float perceivedTemperature;

	/**
	 * precipitation amount
	 */
	protected float precipitationAmount;

	/**
	 * radiation
	 */
	protected int radiation;

	/**
	 * relativ humidity
	 */
	protected float relativHumidity;

	/**
	 * temperature
	 */
	protected float temperature;

	/**
	 * wind direction
	 */
	protected int windDirection;

	/**
	 * wind velocity
	 */
	protected float windVelocity;

	/**
	 * Constructor
	 */
	public Weather() {
	}

	/**
	 * Constructor
	 * 
	 * @param timestamp
	 *            Timestamp
	 */
	public Weather(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(Weather weather) {
		long thisTime = this.timestamp;
		long anotherTime = weather.getTimestamp();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	public int getAirPressure() {
		return this.airPressure;
	}

	public int getLightIntensity() {
		return this.lightIntensity;
	}

	public float getPerceivedTemperature() {
		return this.perceivedTemperature;
	}

	public float getPrecipitationAmount() {
		return this.precipitationAmount;
	}

	public int getRadiation() {
		return this.radiation;
	}

	public float getRelativHumidity() {
		return this.relativHumidity;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public int getWindDirection() {
		return this.windDirection;
	}

	public float getWindVelocity() {
		return this.windVelocity;
	}

	public void setAirPressure(int airPressure) {
		this.airPressure = airPressure;
	}

	public void setLightIntensity(int lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	public void setPerceivedTemperature(float perceivedTemperature) {
		this.perceivedTemperature = perceivedTemperature;
	}

	public void setPrecipitationAmount(float precipitationAmount) {
		this.precipitationAmount = precipitationAmount;
	}

	public void setRadiation(int radiation) {
		this.radiation = radiation;
	}

	public void setRelativHumidity(float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setWindDirection(int windDirection) {
		this.windDirection = windDirection;
	}

	public void setWindVelocity(float windVelocity) {
		this.windVelocity = windVelocity;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Weather [timestamp=" + this.timestamp + ", windVelocity=" + this.windVelocity + ", temperature="
				+ this.temperature + ", perceivedTemperature=" + this.perceivedTemperature + ", lightIntensity="
				+ this.lightIntensity + ", relativHumidity=" + this.relativHumidity + ", windDirection="
				+ this.windDirection + ", radiation=" + this.radiation + ", precipitationAmount="
				+ this.precipitationAmount + ", airPressure=" + this.airPressure + "]";
	}

}
