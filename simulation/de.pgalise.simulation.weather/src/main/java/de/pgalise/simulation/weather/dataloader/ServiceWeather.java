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

import de.pgalise.simulation.shared.city.City;
import java.util.Objects;

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
	private City city;

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
	protected ServiceWeather() {
	}

	public ServiceWeather(long timestamp, City city, Float relativHumidity, Float temperatureLow, Float temperatureHigh, Float windDirection, Float windVelocity) {
		this.timestamp = timestamp;
		this.city = city;
		this.relativHumidity = relativHumidity;
		this.temperatureLow = temperatureLow;
		this.temperatureHigh = temperatureHigh;
		this.windDirection = windDirection;
		this.windVelocity = windVelocity;
	}

	@Override
	public int compareTo(ServiceWeather weather) {
		long thisTime = this.timestamp;
		long anotherTime = weather.getTimestamp();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	public City getCity() {
		return this.city;
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

	public void setCity(City city) {
		this.city = city;
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

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
		hash = 59 * hash + Objects.hashCode(this.city);
		hash = 59 * hash + Objects.hashCode(this.relativHumidity);
		hash = 59 * hash + Objects.hashCode(this.temperatureLow);
		hash = 59 * hash + Objects.hashCode(this.temperatureHigh);
		hash = 59 * hash + Objects.hashCode(this.windDirection);
		hash = 59 * hash + Objects.hashCode(this.windVelocity);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ServiceWeather other = (ServiceWeather) obj;
		if (this.timestamp != other.timestamp) {
			return false;
		}
		if (!Objects.equals(this.city, other.city)) {
			return false;
		}
		if (!Objects.equals(this.relativHumidity, other.relativHumidity)) {
			return false;
		}
		if (!Objects.equals(this.temperatureLow, other.temperatureLow)) {
			return false;
		}
		if (!Objects.equals(this.temperatureHigh, other.temperatureHigh)) {
			return false;
		}
		if (!Objects.equals(this.windDirection, other.windDirection)) {
			return false;
		}
		if (!Objects.equals(this.windVelocity, other.windVelocity)) {
			return false;
		}
		return true;
	}

}
