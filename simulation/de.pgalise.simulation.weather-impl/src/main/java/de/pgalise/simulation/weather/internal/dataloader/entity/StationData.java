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
 
package de.pgalise.simulation.weather.internal.dataloader.entity;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract super class for weather data of weather stations. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@MappedSuperclass
public abstract class StationData implements Comparable<StationData> {

	/**
	 * air pressure
	 */
	@Column(name = "AIR_PRESSURE")
	private Integer airPressure;

	/**
	 * Database id
	 */
	@Id
	@Column(name = "ID")
	private Integer id;

	/**
	 * light intensity
	 */
	@Column(name = "LIGHT_INTENSITY")
	private Integer lightIntensity;

	/**
	 * Date
	 */
	@Column(name = "MEASURE_DATE")
	private Date measureDate;

	/**
	 * Time
	 */
	@Column(name = "MEASURE_TIME")
	private Time measureTime;

	/**
	 * perceived temperature
	 */
	@Column(name = "PERCEIVED_TEMPERATURE")
	private Float perceivedTemperature;

	/**
	 * precipitation amount
	 */
	@Column(name = "PRECIPITATION_AMOUNT")
	private Float precipitationAmount;

	/**
	 * radiation
	 */
	@Column(name = "RADIATION")
	private Integer radiation;

	/**
	 * relativ humidity
	 */
	@Column(name = "RELATIV_HUMIDITY")
	private Float relativHumidity;

	/**
	 * temperature
	 */
	@Column(name = "TEMPERATURE")
	private Float temperature;

	/**
	 * wind direction
	 */
	@Column(name = "WIND_DIRECTION")
	private Integer windDirection;

	/**
	 * wind velocity
	 */
	@Column(name = "WIND_VELOCITY")
	private Float windVelocity;

	/**
	 * Default constructor
	 */
	public StationData() {

	}

	/**
	 * Constructor
	 * 
	 * @param date
	 *            Date
	 * @param time
	 *            Time
	 * @param airPressure
	 *            air pressure
	 * @param lightIntensity
	 *            light intensity
	 * @param perceivedTemperature
	 *            perceived temperature
	 * @param temperature
	 *            temperature
	 * @param precipitationAmount
	 *            precipitation amount
	 * @param radiation
	 *            radiation
	 * @param relativHumidity
	 *            relativ humidity
	 * @param windDirection
	 *            wind direction
	 * @param windVelocity
	 *            wind velocity
	 */
	public StationData(Date date, Time time, Integer airPressure, Integer lightIntensity, Float perceivedTemperature,
			Float temperature, Float precipitationAmount, Integer radiation, Float relativHumidity,
			Integer windDirection, Float windVelocity) {
		this.measureDate = date;
		this.measureTime = time;
		this.airPressure = airPressure;
		this.lightIntensity = lightIntensity;
		this.perceivedTemperature = perceivedTemperature;
		this.temperature = temperature;
		this.precipitationAmount = precipitationAmount;
		this.radiation = radiation;
		this.relativHumidity = relativHumidity;
		this.windDirection = windDirection;
		this.windVelocity = windVelocity;
	}

	@Override
	public int compareTo(StationData data) {
		long thisTime = this.getDate().getTime() + this.getTime().getTime();
		long anotherTime = data.getDate().getTime() + data.getTime().getTime();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	public Integer getAirPressure() {
		return this.airPressure;
	}

	public Date getDate() {
		return this.measureDate;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getLightIntensity() {
		return this.lightIntensity;
	}

	public Float getPerceivedTemperature() {
		return this.perceivedTemperature;
	}

	public Float getPrecipitationAmount() {
		return this.precipitationAmount;
	}

	public Integer getRadiation() {
		return this.radiation;
	}

	public Float getRelativHumidity() {
		return this.relativHumidity;
	}

	public Float getTemperature() {
		return this.temperature;
	}

	public Time getTime() {
		return this.measureTime;
	}

	public Integer getWindDirection() {
		return this.windDirection;
	}

	public Float getWindVelocity() {
		return this.windVelocity;
	}

	public void setAirPressure(Integer airPressure) {
		this.airPressure = airPressure;
	}

	public void setDate(Date date) {
		this.measureDate = date;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLightIntensity(Integer lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	public void setPerceivedTemperature(Float perceivedTemperature) {
		this.perceivedTemperature = perceivedTemperature;
	}

	public void setPrecipitationAmount(Float precipitationAmount) {
		this.precipitationAmount = precipitationAmount;
	}

	public void setRadiation(Integer radiation) {
		this.radiation = radiation;
	}

	public void setRelativHumidity(Float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public void setTime(Time time) {
		this.measureTime = time;
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
		return "Weather [" + (this.measureDate != null ? "date=" + this.measureDate + ", " : "")
				+ (this.measureTime != null ? "time=" + this.measureTime + ", " : "") + "windVelocity="
				+ this.windVelocity + ", temperature=" + this.temperature + ", perceivedTemperature="
				+ this.perceivedTemperature + ", lightIntensity=" + this.lightIntensity + ", relativHumidity="
				+ this.relativHumidity + ", windDirection=" + this.windDirection + ", radiation=" + this.radiation
				+ ", precipitationAmount=" + this.precipitationAmount + ", airPressure=" + this.airPressure + "]";
	}

}
