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
import java.sql.Date;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * Weather current data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
//@Table(name = "PGALISE.DEFAULT_SERVICE_DATA_CURRENT")
@NamedQuery(name = "DefaultServiceDataCurrent.findByDate", query = "SELECT i FROM DefaultServiceDataCurrent i WHERE i.measureDate = :date AND i.city = :city")
public class DefaultServiceDataCurrent extends AbstractBaseServiceData implements ServiceDataCurrent {
	private static final long serialVersionUID = 1L;

	/**
	 * Temperature
	 */
	@Column(name = "TEMPERATURE")
	private Measure<Float, Temperature> temperature;

	protected DefaultServiceDataCurrent() {
	}

	public DefaultServiceDataCurrent(
		Date measureDate, 
		Time measureTime, 
		City city, 
		Float relativHumidity, 
		Measure<Float, Temperature> temperature, Float windDirection, Float windVelocity, WeatherCondition condition) {
		super(measureDate, measureTime, city, relativHumidity, windDirection, windVelocity, condition);
		this.temperature = temperature;
	}

	@Override
	public Measure<Float, Temperature> getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Measure<Float, Temperature> temperature) {
		this.temperature = temperature;
	}

}
