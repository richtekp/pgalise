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

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import de.pgalise.util.weathercollector.weatherservice.ServiceStrategyLib;
import javax.persistence.ManyToOne;

/**
 * Abstract super class for weather data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@MappedSuperclass
public abstract class ServiceData extends AbstractIdentifiable implements Comparable<ServiceData>, ServiceDataCompleter {

	/**
	 * City
	 */
	@ManyToOne
	protected City city;

	/**
	 * Condition
	 */
	@Column(name = "CONDITIONCODE")
	protected Integer condition;

	/**
	 * Date
	 */
	@Column(name = "MEASURE_DATE")
	protected Date measureDate;

	/**
	 * Temperature unit
	 */
	@Transient
	protected String unitTemperature;

	/**
	 * Default constructor
	 */
	public ServiceData() {
	}

	/**
	 * Constructor
	 * 
	 * @param date
	 *            Date
	 */
	public ServiceData(Date date) {
		this.city = null;
		this.condition = ServiceStrategyLib.UNKNOWN_CONDITION;
		this.measureDate = date;
		this.unitTemperature = "C";
	}

	@Override
	public int compareTo(ServiceData data) {
		long thisTime = this.getDate().getTime();
		long anotherTime = data.getDate().getTime();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	public City getCity() {
		return this.city;
	}

	public Integer getCondition() {
		return this.condition;
	}

	public Date getDate() {
		return this.measureDate;
	}

	public String getUnitTemperature() {
		return this.unitTemperature;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public void setCondition(Integer condition) {
		this.condition = condition;
	}

	public void setDate(Date measureDate) {
		this.measureDate = measureDate;
	}

	public void setUnitTemperature(String unitTemperature) {
		this.unitTemperature = unitTemperature;
	}

}
