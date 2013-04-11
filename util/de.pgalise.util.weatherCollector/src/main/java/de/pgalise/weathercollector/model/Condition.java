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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents the weather conditions
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
@Entity
@Table(name = "PGALISE.WEATHER_CONDITION")
@NamedQuery(name = "Condition.getConditionByString", query = "SELECT i FROM Condition i WHERE i.condition = :condition")
public class Condition implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1236972145732461552L;

	/**
	 * Code
	 */
	@Column(name = "CODE")
	private int code;

	/**
	 * Condition
	 */
	@Column(name = "CONDITION")
	private String condition;

	/**
	 * ID
	 */
	@Id
	@Column(name = "ID")
	private int id;

	/**
	 * Default constructor
	 */
	public Condition() {
	}

	public int getCode() {
		return this.code;
	}

	public String getCondition() {
		return this.condition;
	}

	public int getId() {
		return this.id;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setId(int id) {
		this.id = id;
	}

}
