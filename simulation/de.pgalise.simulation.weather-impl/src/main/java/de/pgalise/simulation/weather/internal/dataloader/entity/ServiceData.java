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
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Abstract super class for weather data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@MappedSuperclass
public abstract class ServiceData extends AbstractIdentifiable implements Comparable<ServiceData> {

	/**
	 * City
	 */
	@ManyToOne
	@JoinColumn(name="CITY")
	private City city;

	/**
	 * Timestamp
	 */
//	@Column(name = "MEASURE_DATE")
	private Date measureDate;

	protected ServiceData() {
		super();
	}

	public ServiceData(City city, Date measureDate) {
		this();
		this.city = city;
		this.measureDate = measureDate;
	}

	@Override
	public int compareTo(ServiceData data) {
		long thisTime = this.getMeasureDate().getTime();
		long anotherTime = data.getMeasureDate().getTime();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	public City getCity() {
		return this.city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(City city) {
		this.city = city;
	}

	/**
	 * @return the measureDate
	 */
	public Date getMeasureDate() {
		return measureDate;
	}

	/**
	 * @param measureDate the measureDate to set
	 */
	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}
}
