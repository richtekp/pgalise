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
 
package de.pgalise.simulation.weather.internal.dataloader.entity;

import de.pgalise.simulation.shared.city.City;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Weather current data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
//@Table(name = "PGALISE.WEATHER_SERVICE_CURRENT")
@NamedQuery(name = "ServiceDataCurrent.findByDate", query = "SELECT i FROM ServiceDataCurrent i WHERE i.measureDate = :date AND i.city = :city")
public class ServiceDataCurrent extends AbstractServiceData {
	private static final long serialVersionUID = 1L;

	/**
	 * Temperature
	 */
	@Column(name = "TEMPERATURE")
	private Float temperature;

	protected ServiceDataCurrent() {
	}

	public ServiceDataCurrent(Float relativHumidity, Float temperature, Float windDirection, Float windVelocity, City city, Date measureDate) {
		super(relativHumidity, windDirection, windVelocity, city, measureDate);
		this.temperature = temperature;
	}

	public Float getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

}
