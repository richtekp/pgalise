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
 * Weather current data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
@Table(name = "PGALISE.WEATHER_SERVICE_CURRENT")
@NamedQuery(name = "ServiceDataCurrent.findByDate", query = "SELECT i FROM ServiceDataCurrent i WHERE i.measureDate = :date AND i.city = :city")
public final class ServiceDataCurrent extends ServiceData {

	/**
	 * Relativ humidity
	 */
	@Column(name = "RELATIV_HUMIDITY")
	private Float relativHumidity;

	/**
	 * Temperature
	 */
	@Column(name = "TEMPERATURE")
	private Float temperature;

	/**
	 * wind direction
	 */
	@Column(name = "WIND_DIRECTION")
	private Float windDirection;

	/**
	 * wind velocity
	 */
	@Column(name = "WIND_VELOCITY")
	private Float windVelocity;

	public Float getRelativHumidity() {
		return this.relativHumidity;
	}

	public Float getTemperature() {
		return this.temperature;
	}

	public Float getWindDirection() {
		return this.windDirection;
	}

	public Float getWindVelocity() {
		return this.windVelocity;
	}

	public void setRelativHumidity(Float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public void setWindDirection(Float windDirection) {
		this.windDirection = windDirection;
	}

	public void setWindVelocity(Float windVelocity) {
		this.windVelocity = windVelocity;
	}

}
