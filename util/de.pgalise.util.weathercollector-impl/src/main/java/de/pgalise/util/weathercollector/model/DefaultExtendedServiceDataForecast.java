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

import de.pgalise.weathercollector.model.ExtendedServiceDataForecastCompleter;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.Condition;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import de.pgalise.weathercollector.model.ExtendedServiceDataForecast;
import de.pgalise.weathercollector.model.MutableExtendedServiceDataForecast;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;

/**
 * Weather forecast data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
//@Table(name = "PGALISE.EXTENDED_SERVICE_DATA_FORECAST")
@NamedQuery(name = "DefaultExtendedServiceDataForecast.findByCityAndDate", query = "SELECT i FROM DefaultExtendedServiceDataForecast i WHERE i.measureDate = :date AND i.city = :city")
public class DefaultExtendedServiceDataForecast extends DefaultServiceDataForecast implements ExtendedServiceDataForecastCompleter, ExtendedServiceDataForecast, MutableExtendedServiceDataForecast {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	protected DefaultExtendedServiceDataForecast() {
	}

	public DefaultExtendedServiceDataForecast(
		Date measureDate,
		Time measureTime, 
		City city,
		Measure<Float, Temperature> temperatureHigh,
		Measure<Float, Temperature> temperatureLow,
		Float relativHumidity,
		Float windDirection,
		Float windVelocity,
		Condition condition) {
		super(
			measureDate,
			measureTime,
			city,
			temperatureHigh,
			temperatureLow,
			relativHumidity,
			windDirection,
			windVelocity,
			condition);
	}

	@Override
	public void complete(ExtendedServiceDataForecast newObj) {
		// Date
		if (this.getMeasureDate() == null) {
			this.setMeasureDate(newObj.getMeasureDate());
		}

		if (this.getTemperatureLow() == null) {
			this.setTemperatureLow(newObj.getTemperatureLow());
		}

		if (this.getTemperatureHigh() == null) {
			this.setTemperatureHigh(newObj.getTemperatureHigh());
		}

		if (this.getCondition() == Condition.UNKNOWN_CONDITION) {
			this.setCondition(newObj.getCondition());
		}
	}
}
