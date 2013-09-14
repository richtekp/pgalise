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
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.sql.Date;
import java.sql.Time;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Is a helper for weather service data
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
@Entity
public class DefaultServiceDataHelper extends AbstractIdentifiable implements MutableServiceDataHelper<DefaultExtendedServiceDataCurrent, DefaultExtendedServiceDataForecast> {
	private static final long serialVersionUID = 1L;

	protected DefaultServiceDataHelper() {
	}

	/**
	 * Checks data if the date is the same day
	 * 
	 * @param date1
	 *            Date
	 * @param date2
	 *            Date
	 * @return true if the date is the same day
	 */
	@SuppressWarnings("deprecation")
	public static boolean checkDate(Date date1, Date date2) {
		if ((date1.getDay() == date2.getDay()) && (date1.getMonth() == date2.getMonth())
				&& (date1.getYear() == date2.getYear())) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the forecasts of the day
	 * 
	 * @param <T> 
	 * @param forecasts
	 *            Forecasts
	 * @param date
	 *            Date
	 * @return Forecast of the day or null
	 */
	public static <T extends ExtendedServiceDataForecast> T getForecastFromDate(Set<T> forecasts, Date date) {
		for (T forecastCondition : forecasts) {
			if (checkDate(forecastCondition.getMeasureDate(), date)) {
				return forecastCondition;
			}
		}

		return null;
	}

	/**
	 * City of the API
	 */
	private String apicity;

	/**
	 * City
	 */
	@OneToOne
	private City city;

	/**
	 * Current condition
	 */
	@OneToOne
	private DefaultExtendedServiceDataCurrent currentCondition;

	/**
	 * Set with forecasts
	 */
	@OneToMany
	private Set<DefaultExtendedServiceDataForecast> forecastConditions;

	/**
	 * Timestamp of the data
	 */
	private Time measureTime;
	
	private Date measureDate;

	/**
	 * Source
	 */
	private String source;

	/**
	 * Constructor
	 * 
	 * @param city 
	 * @param apiSource the name of the weather API used to retrieve the date 
	 * (e.g. Yahoo)
	 */
	public DefaultServiceDataHelper(City city, String apiSource) {
		this.source = apiSource;
		this.city = city;
		this.apicity = "";
		this.forecastConditions = new TreeSet<>();
	}

	@Override
	public void complete(ServiceDataHelper<DefaultExtendedServiceDataCurrent, DefaultExtendedServiceDataForecast> newObj) {

		if ((this.city == null) || this.city.getName().isEmpty()) {
			this.city = newObj.getCity();
		}

		if (this.measureTime == null) {
			this.measureTime = newObj.getMeasureTime();
		}
		if(this.measureDate == null) {
			this.measureDate = newObj.getMeasureDate();
		}

		if (this.currentCondition == null) {
			this.currentCondition = newObj.getCurrentCondition();
		} else {
			this.currentCondition.complete(newObj.getCurrentCondition());
		}

		if ((this.forecastConditions == null) || (this.forecastConditions.isEmpty())) {
			this.forecastConditions = newObj.getForecastConditions();
		} else {
			for (ExtendedServiceDataForecast condition : this.forecastConditions) {
				ExtendedServiceDataForecast newCondition = getForecastFromDate(newObj.getForecastConditions(),
						condition.getMeasureDate());
				if (newCondition != null) {
					condition.complete(newCondition);
				}
			}

			for (DefaultExtendedServiceDataForecast newCondition : newObj.getForecastConditions()) {

				ExtendedServiceDataForecast oldCondition = getForecastFromDate(this.forecastConditions, newCondition.getMeasureDate());
				if (oldCondition == null) {
					this.forecastConditions.add(newCondition);
				}

			}
		}
	}

	@Override
	public String getApicity() {
		return this.apicity;
	}

	@Override
	public City getCity() {
		return this.city;
	}

	@Override
	public DefaultExtendedServiceDataCurrent getCurrentCondition() {
		return this.currentCondition;
	}

	@Override
	public Set<DefaultExtendedServiceDataForecast> getForecastConditions() {
		return this.forecastConditions;
	}

	@Override
	public Time getMeasureTime() {
		return this.measureTime;
	}

	@Override
	public Date getMeasureDate() {
		return measureDate;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public void setApicity(String apicity) {
		this.apicity = apicity;
	}

	@Override
	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public void setCurrentCondition(DefaultExtendedServiceDataCurrent currentCondition) {
		this.currentCondition = currentCondition;
	}

	@Override
	public void setForecastConditions(
		Set<DefaultExtendedServiceDataForecast> forecastConditions) {
		this.forecastConditions = forecastConditions;
	}

	@Override
	public void setMeasureTime(Time timestamp) {
		this.measureTime = timestamp;
	}

	@Override
	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}
}
