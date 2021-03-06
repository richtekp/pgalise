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
package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import java.util.Objects;
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
public class ServiceDataHelper extends AbstractTimeSensitive {

  private static final long serialVersionUID = 1L;

  /**
   * Checks data if the date is the same day
   *
   * @param date1 Date
   * @param date2 Date
   * @return true if the date is the same day
   */
  @SuppressWarnings("deprecation")
  public static boolean checkDate(java.util.Date date1,
    java.util.Date date2) {
    if ((date1.getDay() == date2.getDay()) && (date1.getMonth() == date2.
      getMonth())
      && (date1.getYear() == date2.getYear())) {
      return true;
    }

    return false;
  }

  /**
   * Returns the forecasts of the day
   *
   * @param <T>
   * @param forecasts Forecasts
   * @param date Date
   * @return Forecast of the day or null
   */
  public static <T extends ExtendedServiceDataForecast> T getForecastFromDate(
    Set<T> forecasts,
    java.util.Date date) {
    for (T forecastCondition : forecasts) {
      if (checkDate(forecastCondition.getMeasureDate(),
        date)) {
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
  private ExtendedServiceDataCurrent currentCondition;

  /**
   * Set with forecasts
   */
  @OneToMany(cascade = {CascadeType.ALL})
  private Set<ExtendedServiceDataForecast> forecastConditions;

  /**
   * Source
   */
  private String source;

  protected ServiceDataHelper() {
  }

  /**
   * Constructor
   *
   * @param city
   * @param apiSource the name of the weather API used to retrieve the date
   * (e.g. Yahoo)
   */
  public ServiceDataHelper(City city,
    String apiSource) {
    this.source = apiSource;
    this.city = city;
    this.apicity = "";
    this.forecastConditions = new TreeSet<>();
  }

  public void complete(ServiceDataHelper newObj,
    IdGenerator idGenerator) {

    if ((this.city == null) || this.city.getName().isEmpty()) {
      this.city = newObj.getCity();
    }

    if (this.getMeasureTime() == null) {
      this.setMeasureTime(newObj.getMeasureTime());
    }
    if (this.getMeasureDate() == null) {
      this.setMeasureDate(newObj.getMeasureDate());
    }

    if (this.currentCondition == null) {
      this.currentCondition = newObj.getCurrentCondition();
    } else {
      this.currentCondition.complete(newObj.getCurrentCondition(),
        idGenerator);
    }

    if ((this.forecastConditions == null) || (this.forecastConditions.isEmpty())) {
      this.forecastConditions = newObj.getForecastConditions();
    } else {
      for (ExtendedServiceDataForecast condition : this.forecastConditions) {
        ExtendedServiceDataForecast newCondition = getForecastFromDate(newObj.
          getForecastConditions(),
          condition.getMeasureDate());
        if (newCondition != null) {
          condition.complete(newCondition,
            idGenerator);
        }
      }

      for (ExtendedServiceDataForecast newCondition : newObj.
        getForecastConditions()) {

        ExtendedServiceDataForecast oldCondition = getForecastFromDate(
          this.forecastConditions,
          newCondition.getMeasureDate());
        if (oldCondition == null) {
          this.forecastConditions.add(newCondition);
        }

      }
    }
  }

  public String getApicity() {
    return this.apicity;
  }

  public City getCity() {
    return this.city;
  }

  public ExtendedServiceDataCurrent getCurrentCondition() {
    return this.currentCondition;
  }

  public Set<ExtendedServiceDataForecast> getForecastConditions() {
    return this.forecastConditions;
  }

  public String getSource() {
    return this.source;
  }

  public void setApicity(String apicity) {
    this.apicity = apicity;
  }

  public void setCity(City city) {
    this.city = city;
  }

  public void setCurrentCondition(ExtendedServiceDataCurrent currentCondition) {
    this.currentCondition = currentCondition;
  }

  public void setForecastConditions(
    Set<ExtendedServiceDataForecast> forecastConditions) {
    this.forecastConditions = forecastConditions;
  }

  public void setSource(String source) {
    this.source = source;
  }

	@Override
	public int hashCode() {
		int hash = 89 * super.hashCode();
		hash = 89 * hash + Objects.hashCode(this.apicity);
		hash = 89 * hash + Objects.hashCode(this.city);
		hash = 89 * hash + Objects.hashCode(this.currentCondition);
		hash = 89 * hash + Objects.hashCode(this.forecastConditions);
		hash = 89 * hash + Objects.hashCode(this.source);
		return hash;
	}
	
	protected boolean equalsTransitive(ServiceDataHelper other) {
		if(!super.equalsTransitive(other)) {
			return false;
		}
		if (!Objects.equals(this.apicity,
			other.apicity)) {
			return false;
		}
		if (!Objects.equals(this.city,
			other.city)) {
			return false;
		}
		if (!Objects.equals(this.currentCondition,
			other.currentCondition)) {
			return false;
		}
		if (!Objects.equals(this.forecastConditions,
			other.forecastConditions)) {
			return false;
		}
		if (!Objects.equals(this.source,
			other.source)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ServiceDataHelper other = (ServiceDataHelper) obj;
		return equalsTransitive(other);
	}
}
