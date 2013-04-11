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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Weather forecast data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
@Table(name = "PGALISE.WEATHER_SERVICE_FORECAST")
@NamedQuery(name = "ServiceDataForecast.findByDate", query = "SELECT i FROM ServiceDataForecast i WHERE i.measureDate = :date AND i.city = :city")
public final class ServiceDataForecast extends ServiceData {

	/**
	 * Temperature (high)
	 */
	@Column(name = "TEMPERATURE_HIGH")
	private Float temperatureHigh;

	/**
	 * Temperature (low)
	 */
	@Column(name = "TEMPERATURE_LOW")
	private Float temperatureLow;

	public Float getTemperatureHigh() {
		return this.temperatureHigh;
	}

	public Float getTemperatureLow() {
		return this.temperatureLow;
	}

	public void setTemperatureHigh(Float temperature) {
		this.temperatureHigh = temperature;
	}

	public void setTemperatureLow(Float temperature) {
		this.temperatureLow = temperature;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceData [id=" + this.id + ", city=" + this.city + ", measureDate=" + this.measureDate
				+ ", temperatureLow=" + this.temperatureLow + ", temperatureHigh=" + this.temperatureHigh + "]";
	}

}
