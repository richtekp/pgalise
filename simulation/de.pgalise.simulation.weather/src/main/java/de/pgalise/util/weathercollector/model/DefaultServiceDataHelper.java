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
import de.pgalise.simulation.weather.model.AbstractTimeSensitive;
import de.pgalise.simulation.weather.model.WeatherCondition;
import java.sql.Date;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
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
public class DefaultServiceDataHelper extends AbstractTimeSensitive implements ServiceDataHelper<MyExtendedServiceDataCurrent, MyExtendedServiceDataForecast> {
	private static final long serialVersionUID = 1L;

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
	public static <T extends MyExtendedServiceDataForecast> T getForecastFromDate(Set<T> forecasts, Date date) {
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
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private City city;

	/**
	 * Current condition
	 */
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private MyExtendedServiceDataCurrent currentCondition;

	/**
	 * Set with forecasts
	 */
	@OneToMany(cascade = {CascadeType.ALL})
	private Set<MyExtendedServiceDataForecast> forecastConditions;

	/**
	 * Source
	 */
	private String source;

	protected DefaultServiceDataHelper() {
	}

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
	public void complete(ServiceDataHelper<MyExtendedServiceDataCurrent, MyExtendedServiceDataForecast> newObj) {

		if ((this.city == null) || this.city.getName().isEmpty()) {
			this.city = newObj.getCity();
		}

		if (this.getMeasureTime() == null) {
			this.setMeasureTime(newObj.getMeasureTime());
		}
		if(this.getMeasureDate() == null) {
			this.setMeasureDate(newObj.getMeasureDate());
		}

		if (this.currentCondition == null) {
			this.currentCondition = newObj.getCurrentCondition();
		} else {
			this.currentCondition.complete(newObj.getCurrentCondition());
		}

		if ((this.forecastConditions == null) || (this.forecastConditions.isEmpty())) {
			this.forecastConditions = newObj.getForecastConditions();
		} else {
			for (MyExtendedServiceDataForecast condition : this.forecastConditions) {
				MyExtendedServiceDataForecast newCondition = getForecastFromDate(newObj.getForecastConditions(),
						condition.getMeasureDate());
				if (newCondition != null) {
					condition.complete(newCondition);
				}
			}

			for (MyExtendedServiceDataForecast newCondition : newObj.getForecastConditions()) {

				MyExtendedServiceDataForecast oldCondition = getForecastFromDate(this.forecastConditions, newCondition.getMeasureDate());
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
	public MyExtendedServiceDataCurrent getCurrentCondition() {
		return this.currentCondition;
	}

	@Override
	public Set<MyExtendedServiceDataForecast> getForecastConditions() {
		return this.forecastConditions;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	public void setApicity(String apicity) {
		this.apicity = apicity;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public void setCurrentCondition(MyExtendedServiceDataCurrent currentCondition) {
		this.currentCondition = currentCondition;
	}

	public void setForecastConditions(
		Set<MyExtendedServiceDataForecast> forecastConditions) {
		this.forecastConditions = forecastConditions;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
