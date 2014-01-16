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

import de.pgalise.simulation.shared.entity.City;
import java.util.Objects;
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
@NamedQuery(name = "ServiceDataForecast.findByDate",
  query = "SELECT i FROM ServiceDataForecast i WHERE i.measureDate = :date AND i.city = :city")
public class ServiceDataForecast extends AbstractServiceData implements
  Comparable<ServiceDataForecast> {

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

  protected ServiceDataForecast() {
  }

  public ServiceDataForecast(Long id,
    java.util.Date measureDate,
    java.sql.Time measureTime,
    City city,
    Measure<Float, Temperature> temperatureHigh,
    Measure<Float, Temperature> temperatureLow,
    Float relativHumidity,
    Float windDirection,
    Float windVelocity,
    WeatherCondition condition
  ) {
    super(id,
      measureDate,
      measureTime,
      city,
      relativHumidity,
      windDirection,
      windVelocity,
      condition);
    this.temperatureHigh = temperatureHigh;
    this.temperatureLow = temperatureLow;
  }

  public Measure<Float, Temperature> getTemperatureHigh() {
    return this.temperatureHigh;
  }

  public Measure<Float, Temperature> getTemperatureLow() {
    return this.temperatureLow;
  }

  public void setTemperatureHigh(Measure<Float, Temperature> temperature) {
    this.temperatureHigh = temperature;
  }

  public void setTemperatureLow(Measure<Float, Temperature> temperature) {
    this.temperatureLow = temperature;
  }

  @Override
  public int compareTo(ServiceDataForecast o) {
    return this.getMeasureTime().compareTo(o.getMeasureTime());
  }
	
	protected boolean equalsTransitive(ServiceDataForecast other) {
		if(!super.equalsTransitive(other)) {
			return false;
		}
		if (!Objects.equals(this.temperatureHigh,
			other.temperatureHigh)) {
			return false;
		}
		if (!Objects.equals(this.temperatureLow,
			other.temperatureLow)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 23 * super.hashCode();
		hash = 23 * hash + Objects.hashCode(this.temperatureHigh);
		hash = 23 * hash + Objects.hashCode(this.temperatureLow);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ServiceDataForecast other = (ServiceDataForecast) obj;
		return equalsTransitive(other);
	}

}
