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
 
package de.pgalise.weathercollector.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import de.pgalise.weathercollector.weatherservice.ServiceStrategyLib;

/**
 * Abstract super class for weather data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@MappedSuperclass
public abstract class ServiceData implements Comparable<ServiceData>, ServiceDataCompleter {

	/**
	 * City
	 */
	@Column(name = "CITY")
	protected Integer city;

	/**
	 * Condition
	 */
	@Column(name = "CONDITIONCODE")
	protected Integer condition;

	/**
	 * Database id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Integer id;

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

	public Integer getCity() {
		return this.city;
	}

	public Integer getCondition() {
		return this.condition;
	}

	public Date getDate() {
		return this.measureDate;
	}

	public Integer getId() {
		return this.id;
	}

	public String getUnitTemperature() {
		return this.unitTemperature;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	public void setCondition(Integer condition) {
		this.condition = condition;
	}

	public void setDate(Date measureDate) {
		this.measureDate = measureDate;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUnitTemperature(String unitTemperature) {
		this.unitTemperature = unitTemperature;
	}

}
