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
 
package de.pgalise.weathercollector.model;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.pgalise.weathercollector.weatherservice.ServiceStrategyLib;

/**
 * Weather current data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
@Table(name = "PGALISE.WEATHER_SERVICE_CURRENT")
@NamedQuery(name = "ServiceDataCurrent.findByCityAndDate", query = "SELECT i FROM ServiceDataCurrent i WHERE i.measureDate = :date AND i.city = :city")
public final class ServiceDataCurrent extends ServiceData {

	/**
	 * Time
	 */
	@Column(name = "MEASURE_TIME")
	protected Time measureTime;

	/**
	 * Relativ humidity
	 */
	@Column(name = "RELATIV_HUMIDITY")
	private Float relativHumidity;

	/**
	 * Sunrise
	 */
	@Column(name = "SUNRISE")
	private Time sunrise;

	/**
	 * Sunset
	 */
	@Column(name = "SUNSET")
	private Time sunset;

	/**
	 * Temperature
	 */
	@Column(name = "TEMPERATURE")
	private Float temperature;

	/**
	 * Visibility
	 */
	@Column(name = "VISIBILITY")
	private Float visibility;

	/**
	 * Wind direction
	 */
	@Column(name = "WIND_DIRECTION")
	private Integer windDirection;

	/**
	 * Wind velocity
	 */
	@Column(name = "WIND_VELOCITY")
	private Float windVelocity;

	/**
	 * Default constructor
	 */
	public ServiceDataCurrent() {

	}

	/**
	 * Constructor
	 * 
	 * @param Date
	 *            Date
	 * @param time
	 *            Time
	 */
	public ServiceDataCurrent(Date date, Time time) {
		super(date);
		this.measureTime = time;
		this.relativHumidity = null;
		this.sunrise = null;
		this.sunset = null;
		this.temperature = null;
		this.visibility = null;
		this.windDirection = null;
		this.windVelocity = null;
	}

	@Override
	public void complete(ServiceDataCompleter obj) {
		if (!(obj instanceof ServiceDataCurrent)) {
			return;
		}
		ServiceDataCurrent newObj = (ServiceDataCurrent) obj;

		// Date
		if (this.measureDate == null) {
			this.measureDate = newObj.getDate();
		}

		// Time
		if (this.measureTime == null) {
			this.measureTime = newObj.getTime();
		}

		if (this.temperature == null) {
			this.temperature = newObj.getTemperature();
			this.unitTemperature = newObj.getUnitTemperature();
		}

		if (this.condition == ServiceStrategyLib.UNKNOWN_CONDITION) {
			this.condition = newObj.getCondition();
		}

		if (this.relativHumidity == null) {
			this.relativHumidity = newObj.getRelativHumidity();
		}

		if (this.visibility == null) {
			this.visibility = newObj.getVisibility();
		}

		if (this.sunrise == null) {
			this.sunrise = newObj.getSunrise();
		}

		if (this.sunset == null) {
			this.sunset = newObj.getSunset();
		}

		if (this.windVelocity == null) {
			this.windVelocity = newObj.getWindVelocity();
		}

		if (this.windDirection == null) {
			this.windDirection = newObj.getWindDirection();
		}
	}

	public Float getRelativHumidity() {
		return this.relativHumidity;
	}

	public Time getSunrise() {
		return this.sunrise;
	}

	public Time getSunset() {
		return this.sunset;
	}

	public Float getTemperature() {
		return this.temperature;
	}

	public Time getTime() {
		return this.measureTime;
	}

	public Float getVisibility() {
		return this.visibility;
	}

	public Integer getWindDirection() {
		return this.windDirection;
	}

	public Float getWindVelocity() {
		return this.windVelocity;
	}

	public void setRelativHumidity(Float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	public void setSunrise(Time sunrise) {
		this.sunrise = sunrise;
	}

	public void setSunset(Time sunset) {
		this.sunset = sunset;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public void setTime(Time measureTime) {
		this.measureTime = measureTime;
	}

	public void setVisibility(Float visibility) {
		this.visibility = visibility;
	}

	public void setWindDirection(Integer windDirection) {
		this.windDirection = windDirection;
	}

	public void setWindVelocity(Float windVelocity) {
		this.windVelocity = windVelocity;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CurrentCondition [");
		if (this.measureDate != null) {
			builder.append("measureDate=");
			builder.append(this.measureDate);
			builder.append(", ");
		}
		if (this.measureTime != null) {
			builder.append("measureTime=");
			builder.append(this.measureTime);
			builder.append(", ");
		}
		builder.append("temperature=");
		builder.append(this.temperature);
		builder.append(", condition=");
		builder.append(this.condition);
		builder.append(", relativHumidity=");
		builder.append(this.relativHumidity);
		builder.append(", visibility=");
		builder.append(this.visibility);
		builder.append(", ");
		if (this.sunset != null) {
			builder.append("sunset=");
			builder.append(this.sunset);
			builder.append(", ");
		}
		if (this.sunrise != null) {
			builder.append("sunrise=");
			builder.append(this.sunrise);
			builder.append(", ");
		}
		if (this.unitTemperature != null) {
			builder.append("unitTemperature=");
			builder.append(this.unitTemperature);
			builder.append(", ");
		}
		builder.append("windVelocity=");
		builder.append(this.windVelocity);
		builder.append(", windDirection=");
		builder.append(this.windDirection);
		builder.append("]");
		return builder.toString();
	}

}
