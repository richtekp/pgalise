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
 
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.City;
import java.sql.Date;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * Weather forecast data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
//@Table(name = "PGALISE.DEFAULT_SERVICE_DATA_FORECAST")
@NamedQuery(name = "DefaultServiceDataForecast.findByDate", query = "SELECT i FROM DefaultServiceDataForecast i WHERE i.measureDate = :date AND i.city = :city")
public class DefaultServiceDataForecast extends AbstractServiceData implements ServiceDataForecast, Comparable<ServiceDataForecast> {
	private static final long serialVersionUID = 1L;

	/**
	 * Temperature (high)
	 */
	@Column(name = "TEMPERATURE_HIGH")
	private Measure<Float, Temperature> temperatureHigh;

	/**
	 * Temperature (low)
	 */
	@Column(name = "TEMPERATURE_LOW")
	private Measure<Float, Temperature> temperatureLow;

	protected DefaultServiceDataForecast() {
	}

	public DefaultServiceDataForecast(
		Date measureDate, 
		Time measureTime,
		City city, 
		Measure<Float, Temperature> temperatureHigh, 
		Measure<Float, Temperature> temperatureLow, 
		Float relativHumidity, 
		Float windDirection, 
		Float windVelocity, 
		WeatherCondition condition
	) {
		super(measureDate, measureTime, city, relativHumidity, windDirection, windVelocity, condition);
		this.temperatureHigh = temperatureHigh;
		this.temperatureLow = temperatureLow;
	}

	@Override
	public Measure<Float, Temperature> getTemperatureHigh() {
		return this.temperatureHigh;
	}

	@Override
	public Measure<Float, Temperature> getTemperatureLow() {
		return this.temperatureLow;
	}

	public void setTemperatureHigh(Measure<Float, Temperature> temperature) {
		this.temperatureHigh = temperature;
	}

	public void setTemperatureLow(Measure<Float, Temperature> temperature) {
		this.temperatureLow = temperature;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServiceData [id=" + this.getId() + ", city=" + this.getCity() + ", measureDate=" + this.getMeasureDate()
				+ ", temperatureLow=" + this.temperatureLow + ", temperatureHigh=" + this.temperatureHigh + "]";
	}

	@Override
	public int compareTo(ServiceDataForecast o) {
		return this.getMeasureTime().compareTo(o.getMeasureTime());
	}

}
