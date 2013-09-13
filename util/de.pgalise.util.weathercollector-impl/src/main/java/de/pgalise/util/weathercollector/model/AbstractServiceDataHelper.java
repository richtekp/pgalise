/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.weathercollector.model.ExtendedServiceDataCurrent;
import de.pgalise.weathercollector.model.ExtendedServiceDataForecast;
import de.pgalise.weathercollector.model.ServiceDataCompleter;
import de.pgalise.weathercollector.model.ServiceDataHelper;
import java.sql.Date;
import java.sql.Time;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author richter
 */
public abstract class AbstractServiceDataHelper<T> implements ServiceDataHelper {
	
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
	public static ServiceDataHelper getForecastFromDate(Set<ServiceDataHelper> forecasts, Date date) {
		for (ServiceDataHelper forecastCondition : forecasts) {
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
	private ServiceDataHelper currentCondition;

	/**
	 * Set with forecasts
	 */
	private Set<ServiceDataHelper> forecastConditions;

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
	 * @param apiSource the name of the weather API used to retrieve the date 
	 * (e.g. Yahoo)
	 */
	public AbstractServiceDataHelper(City city, String apiSource) {
		this.source = apiSource;
		this.city = city;
		this.apicity = "";
		this.forecastConditions = new TreeSet<ServiceDataHelper>();
	}

	@Override
	public void complete(ServiceDataHelper obj) {
		ServiceDataHelper newObj = obj;

		if ((this.city == null) || this.city.getName().equals("")) {
			this.city = newObj.getCity();
		}

		if (this.measureTime == null) {
			this.measureTime = newObj.getMeasureTime();
		}

		if (this.currentCondition == null) {
			this.currentCondition = newObj.getCurrentCondition();
		} else {
			this.currentCondition.complete(newObj.getCurrentCondition());
		}

		if ((this.forecastConditions == null) || (this.forecastConditions.size() == 0)) {
			this.forecastConditions = newObj.getForecastConditions();
		} else {
			for (ServiceDataHelper condition : this.forecastConditions) {
				ServiceDataHelper newCondition = getForecastFromDate(newObj.getForecastConditions(),
						condition.getMeasureDate());
				if (newCondition != null) {
					condition.complete(newCondition);
				}
			}

			for (ServiceDataHelper newCondition : newObj.getForecastConditions()) {

				ServiceDataHelper oldCondition = getForecastFromDate(this.forecastConditions, newCondition.getMeasureDate());
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
	public ServiceDataHelper getCurrentCondition() {
		return this.currentCondition;
	}

	@Override
	public Set<ServiceDataHelper> getForecastConditions() {
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

	public void setApicity(String apicity) {
		this.apicity = apicity;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public void setCurrentCondition(ServiceDataHelper currentCondition) {
		this.currentCondition = currentCondition;
	}

	public void setForecastConditions(
		Set<ServiceDataHelper> forecastConditions) {
		this.forecastConditions = forecastConditions;
	}

	public void setMeasureTime(Time measureTime) {
		this.measureTime = measureTime;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}

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
		if (this.measureTime != null) {
			builder.append("measureTimestamp=");
			builder.append(this.measureTime);
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
			for (ServiceDataHelper condition : this.forecastConditions) {
				builder.append(condition.toString());
			}
			builder.append("]");

		}
		builder.append("\n]");
		return builder.toString();
	}
}
