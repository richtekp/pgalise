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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents the city from the simulation
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
@Entity
@Table(name = "PGALISE.WEATHER_CITY")
@NamedQuery(name = "City.getAll", query = "SELECT i FROM City i")
public class City implements Serializable, Comparable<City> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3576972145732461552L;

	/**
	 * Altitude (in m over normal null)
	 */
	@Column(name = "ALTITUDE")
	private int altitude;

	/**
	 * ID
	 */
	@Id
	@Column(name = "ID")
	private int id;

	/**
	 * Name
	 */
	@Column(name = "NAME")
	private String name;

	/**
	 * Option that the city is near a river
	 */
	@Column(name = "NEAR_RIVER")
	private boolean nearRiver;

	/**
	 * Option that the city is near the sea
	 */
	@Column(name = "NEAR_SEA")
	private boolean nearSea;

	/**
	 * Population
	 */
	@Column(name = "POPULATION")
	private int population;

	/**
	 * Default contructor
	 */
	public City() {
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name
	 * @param population
	 *            Population
	 * @param altitude
	 *            Altitude
	 * @param nearRiver
	 *            Option that the city is near a river
	 * @param nearSea
	 *            Option that the city is near the sea
	 */
	public City(String name, int population, int altitude, boolean nearRiver, boolean nearSea) {
		super();
		this.name = name;
		this.population = population;
		this.nearRiver = nearRiver;
		this.nearSea = nearSea;
		this.altitude = altitude;
	}

	@Override
	public int compareTo(City anothercity) {
		return this.getName().compareToIgnoreCase(anothercity.getName());
	}

	public int getAltitude() {
		return this.altitude;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getPopulation() {
		return this.population;
	}

	public boolean isNearRiver() {
		return this.nearRiver;
	}

	public boolean isNearSea() {
		return this.nearSea;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNearRiver(boolean nearRiver) {
		this.nearRiver = nearRiver;
	}

	public void setNearSea(boolean nearSea) {
		this.nearSea = nearSea;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

}
