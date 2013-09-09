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
 
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultCondition;
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.Condition;
import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.pgalise.util.weathercollector.weatherservice.ServiceStrategyLib;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;

/**
 * Weather current data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
@Table(name = "PGALISE.EXTENDED_SERVICE_DATA_CURRENT")
@NamedQuery(name = "ExtendedServiceDataCurrent.findByCityAndDate", query = "SELECT i FROM ExtendedServiceDataCurrent i WHERE i.measureDate = :date AND i.city = :city")
public class ExtendedServiceDataCurrent extends DefaultServiceDataCurrent implements ServiceDataCompleter {
	private static final long serialVersionUID = 1L;

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
	 * Visibility
	 */
	@Column(name = "VISIBILITY")
	private Float visibility;

	/**
	 * Default constructor
	 */
	protected ExtendedServiceDataCurrent() {
	}

	public ExtendedServiceDataCurrent(
		Date measureDate, Time measureTime, City city, Measure<Float, Temperature> temperature, Float relativHumidity, Float visibility, Float windDirection, Float windVelocity, DefaultCondition condition, Time sunrise, Time sunset) {
		super(
			measureDate,
			measureTime, 
			city,
			relativHumidity,
			temperature,
			windDirection,
			windVelocity,
			condition);
		this.sunrise = sunrise;
		this.sunset = sunset;
		this.visibility = visibility;
	}

	@Override
	public void complete(ServiceDataCompleter obj) {
		if (!(obj instanceof ExtendedServiceDataCurrent)) {
			return;
		}
		ExtendedServiceDataCurrent newObj = (ExtendedServiceDataCurrent) obj;

		// Date
		if (this.getMeasureDate() == null) {
			this.setMeasureDate(newObj.getMeasureDate());
		}

		// Time
		if (this.getMeasureTime() == null) {
			this.setMeasureTime(newObj.getMeasureTime());
		}

		if (this.getTemperature() == null) {
			this.setTemperature(newObj.getTemperature());
		}

		if (this.getCondition() == DefaultCondition.UNKNOWN_CONDITION) {
			this.setCondition(newObj.getCondition());
		}

		if (this.getRelativHumidity() == null) {
			this.setRelativHumidity(newObj.getRelativHumidity());
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

		if (this.getWindVelocity() == null) {
			this.setWindVelocity(newObj.getWindVelocity());
		}

		if (this.getWindDirection() == null) {
			this.setWindDirection(newObj.getWindDirection());
		}
	}

	public Time getSunrise() {
		return this.sunrise;
	}

	public Time getSunset() {
		return this.sunset;
	}

	public Float getVisibility() {
		return this.visibility;
	}

	public void setSunrise(Time sunrise) {
		this.sunrise = sunrise;
	}

	public void setSunset(Time sunset) {
		this.sunset = sunset;
	}

	public void setVisibility(Float visibility) {
		this.visibility = visibility;
	}
}
