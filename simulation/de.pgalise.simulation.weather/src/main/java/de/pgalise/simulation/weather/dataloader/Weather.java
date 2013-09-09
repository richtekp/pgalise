///* 
// * Copyright 2013 PG Alise (http://www.pg-alise.de/)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License. 
// */
// 
//package de.pgalise.simulation.weather.dataloader;
//
//import de.pgalise.simulation.weather.modifier.WeatherStrategy;
//
///**
// * The class {@link Weather} represents every single weather information entry to a particular timestamp. These values
// * can be modified by {@link WeatherStrategy}.
// * 
// * @author Andreas Rehfeldt
// * @version 1.0 (03.07.2012)
// */
//public class Weather implements Comparable<Weather> {
//
//	/**
//	 * Interpolate interval
//	 */
//	public static final int INTERPOLATE_INTERVAL = 60000;
//
//	/**
//	 * air pressure
//	 */
//	private int airPressure;
//
//	/**
//	 * light intensity
//	 */
//	private int lightIntensity;
//
//	/**
//	 * Timestamp
//	 */
//	private long timestamp;
//
//	/**
//	 * perceived temperature
//	 */
//	private float perceivedTemperature;
//
//	/**
//	 * precipitation amount
//	 */
//	private float precipitationAmount;
//
//	/**
//	 * radiation
//	 */
//	private int radiation;
//
//	/**
//	 * relativ humidity
//	 */
//	private float relativHumidity;
//
//	/**
//	 * temperature
//	 */
//	private float temperature;
//
//	/**
//	 * wind direction
//	 */
//	private int windDirection;
//
//	/**
//	 * wind velocity
//	 */
//	private float windVelocity;
//
//	/**
//	 * Constructor
//	 */
//	protected Weather() {
//	}
//
//	public Weather(long timestamp, int airPressure, int lightIntensity, float perceivedTemperature, float precipitationAmount, int radiation, float relativHumidity, float temperature, int windDirection, float windVelocity) {
//		this.airPressure = airPressure;
//		this.lightIntensity = lightIntensity;
//		this.timestamp = timestamp;
//		this.perceivedTemperature = perceivedTemperature;
//		this.precipitationAmount = precipitationAmount;
//		this.radiation = radiation;
//		this.relativHumidity = relativHumidity;
//		this.temperature = temperature;
//		this.windDirection = windDirection;
//		this.windVelocity = windVelocity;
//	}
//
//	@Override
//	public int compareTo(Weather weather) {
//		long thisTime = this.getTimestamp();
//		long anotherTime = weather.getTimestamp();
//		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
//	}
//
//	public int getAirPressure() {
//		return this.airPressure;
//	}
//
//	public int getLightIntensity() {
//		return this.lightIntensity;
//	}
//
//	public float getPerceivedTemperature() {
//		return this.perceivedTemperature;
//	}
//
//	public float getPrecipitationAmount() {
//		return this.precipitationAmount;
//	}
//
//	public int getRadiation() {
//		return this.radiation;
//	}
//
//	public float getRelativHumidity() {
//		return this.relativHumidity;
//	}
//
//	public float getTemperature() {
//		return this.temperature;
//	}
//
//	public long getTimestamp() {
//		return this.timestamp;
//	}
//
//	public int getWindDirection() {
//		return this.windDirection;
//	}
//
//	public float getWindVelocity() {
//		return this.windVelocity;
//	}
//
//	public void setAirPressure(int airPressure) {
//		this.airPressure = airPressure;
//	}
//
//	public void setLightIntensity(int lightIntensity) {
//		this.lightIntensity = lightIntensity;
//	}
//
//	public void setPerceivedTemperature(float perceivedTemperature) {
//		this.perceivedTemperature = perceivedTemperature;
//	}
//
//	public void setPrecipitationAmount(float precipitationAmount) {
//		this.precipitationAmount = precipitationAmount;
//	}
//
//	public void setRadiation(int radiation) {
//		this.radiation = radiation;
//	}
//
//	public void setRelativHumidity(float relativHumidity) {
//		this.relativHumidity = relativHumidity;
//	}
//
//	public void setTemperature(float temperature) {
//		this.temperature = temperature;
//	}
//
//	public void setTimestamp(long timestamp) {
//		this.timestamp = timestamp;
//	}
//
//	public void setWindDirection(int windDirection) {
//		this.windDirection = windDirection;
//	}
//
//	public void setWindVelocity(float windVelocity) {
//		this.windVelocity = windVelocity;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (obj == null) {
//			return false;
//		}
//		if (getClass() != obj.getClass()) {
//			return false;
//		}
//		final Weather other = (Weather) obj;
//		if (this.timestamp != other.timestamp) {
//			return false;
//		}
//		return equalsIgnoreTimestamp(other);
//	}
//
//	public boolean equalsIgnoreTimestamp(Weather other) {
//		if (this.airPressure != other.airPressure) {
//			return false;
//		}
//		if (this.lightIntensity != other.lightIntensity) {
//			return false;
//		}
//		if (Float.floatToIntBits(this.perceivedTemperature) != Float.floatToIntBits(other.perceivedTemperature)) {
//			return false;
//		}
//		if (Float.floatToIntBits(this.precipitationAmount) != Float.floatToIntBits(other.precipitationAmount)) {
//			return false;
//		}
//		if (this.radiation != other.radiation) {
//			return false;
//		}
//		if (Float.floatToIntBits(this.relativHumidity) != Float.floatToIntBits(other.relativHumidity)) {
//			return false;
//		}
//		if (Float.floatToIntBits(this.temperature) != Float.floatToIntBits(other.temperature)) {
//			return false;
//		}
//		if (this.windDirection != other.windDirection) {
//			return false;
//		}
//		if (Float.floatToIntBits(this.windVelocity) != Float.floatToIntBits(other.windVelocity)) {
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	public int hashCode() {
//		int hash = 7;
//		hash = 97 * hash + this.airPressure;
//		hash = 97 * hash + this.lightIntensity;
//		hash = 97 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
//		hash = 97 * hash + Float.floatToIntBits(this.perceivedTemperature);
//		hash = 97 * hash + Float.floatToIntBits(this.precipitationAmount);
//		hash = 97 * hash + this.radiation;
//		hash = 97 * hash + Float.floatToIntBits(this.relativHumidity);
//		hash = 97 * hash + Float.floatToIntBits(this.temperature);
//		hash = 97 * hash + this.windDirection;
//		hash = 97 * hash + Float.floatToIntBits(this.windVelocity);
//		return hash;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		return "Weather [timestamp=" + this.getTimestamp() + ", windVelocity=" + this.getWindVelocity() + ", temperature="
//				+ this.getTemperature() + ", perceivedTemperature=" + this.getPerceivedTemperature() + ", lightIntensity="
//				+ this.getLightIntensity() + ", relativHumidity=" + this.getRelativHumidity() + ", windDirection="
//				+ this.getWindDirection() + ", radiation=" + this.getRadiation() + ", precipitationAmount="
//				+ this.getPrecipitationAmount() + ", airPressure=" + this.getAirPressure() + "]";
//	}
//
//}
