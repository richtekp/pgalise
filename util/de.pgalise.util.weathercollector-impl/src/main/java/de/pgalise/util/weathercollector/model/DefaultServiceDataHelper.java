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

import de.pgalise.weathercollector.model.ServiceDataHelperCompleter;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.weathercollector.model.ExtendedServiceDataCurrent;
import de.pgalise.weathercollector.model.ExtendedServiceDataForecast;
import de.pgalise.weathercollector.model.MutableExtendedServiceDataCurrent;
import de.pgalise.weathercollector.model.MutableExtendedServiceDataForecast;
import de.pgalise.weathercollector.model.MutableServiceDataHelper;
import de.pgalise.weathercollector.model.ServiceDataHelper;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;

/**
 * Is a helper for weather service data
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public class DefaultServiceDataHelper implements MutableServiceDataHelper {

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
	private City city;

	/**
	 * Current condition
	 */
	private MutableExtendedServiceDataCurrent currentCondition;

	/**
	 * Set with forecasts
	 */
	private Set<MutableExtendedServiceDataForecast> forecastConditions;

	/**
	 * Timestamp of the data
	 */
	private Timestamp measureTimestamp;

	/**
	 * Source
	 */
	private String source;

	/**
	 * Constructor
	 * 
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
	public void complete(ServiceDataHelper<MutableExtendedServiceDataCurrent, MutableExtendedServiceDataForecast> newObj) {

		if ((this.city == null) || this.city.getName().isEmpty()) {
			this.city = newObj.getCity();
		}

		if (this.measureTimestamp == null) {
			this.measureTimestamp = newObj.getMeasureTimestamp();
		}

		if (this.currentCondition == null) {
			this.currentCondition = newObj.getCurrentCondition();
		} else {
			this.currentCondition.complete(newObj.getCurrentCondition());
		}

		if ((this.forecastConditions == null) || (this.forecastConditions.size() == 0)) {
			this.forecastConditions = newObj.getForecastConditions();
		} else {
			for (ExtendedServiceDataForecast condition : this.forecastConditions) {
				ExtendedServiceDataForecast newCondition = getForecastFromDate(newObj.getForecastConditions(),
						condition.getMeasureDate());
				if (newCondition != null) {
					condition.complete(newCondition);
				}
			}

			for (MutableExtendedServiceDataForecast newCondition : newObj.getForecastConditions()) {

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
	public MutableExtendedServiceDataCurrent getCurrentCondition() {
		return this.currentCondition;
	}

	@Override
	public Set<MutableExtendedServiceDataForecast> getForecastConditions() {
		return this.forecastConditions;
	}

	@Override
	public Timestamp getMeasureTimestamp() {
		return this.measureTimestamp;
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
	public void setCurrentCondition(MutableExtendedServiceDataCurrent currentCondition) {
		this.currentCondition = currentCondition;
	}

	@Override
	public void setMeasureTimestamp(Timestamp timestamp) {
		this.measureTimestamp = timestamp;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Liefert die Vorhersage, die zum Datum passt.
	 * 
	 * @param conditions
	 *            Set mit Vorhersagen
	 * @param date
	 *            Datum
	 * @return Vorhersage zum Datum, ansonsten null
	 */

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Weather [");
		if (this.city != null) {
			builder.append("city=");
			builder.append(this.city);
			builder.append(", ");
		}
		if (this.apicity != null) {
			builder.append("apicity=");
			builder.append(this.apicity);
			builder.append(", ");
		}
		if (this.measureTimestamp != null) {
			builder.append("measureTimestamp=");
			builder.append(this.measureTimestamp);
			builder.append(", ");
		}
		if (this.source != null) {
			builder.append("source=");
			builder.append(this.source);
			builder.append(", ");
		}
		if (this.currentCondition != null) {
			builder.append("\n- currentCondition=");
			builder.append(this.currentCondition.toString());
			builder.append(", ");
		}
		if ((this.forecastConditions != null) && !this.forecastConditions.isEmpty()) {
			builder.append("\n- forecastCondition=[");
			for (ExtendedServiceDataForecast condition : this.forecastConditions) {
				builder.append(condition.toString());
			}
			builder.append("]");

		}
		builder.append("\n]");
		return builder.toString();
	}
}
