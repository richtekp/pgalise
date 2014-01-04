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
 
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.persistence.Identifiable;
import javax.faces.bean.ManagedBean;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * Represents the city from the simulation
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
@Entity
@NamedQuery(name = "City.getAll", query = "SELECT i FROM City i")
/*
 * has identical properties boundaries and centerPoint and Building, but sharing 
 * code is not possible in this inheritance hierarchy (all NavigationNode would have to be AbstractGeometricObjects
 */
public class City extends AbstractIdentifiable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3576972145732461552L;
	
	/**
	 * Altitude (in m over normal null)
	 */
	@Column(name = "ALTITUDE")
	private int altitude = 4;

	/**
	 * Name
	 */
	@Column(name = "NAME")
	private String name = "Oldenburg (Oldb)";

	/**
	 * Option that the city is near a river
	 */
	@Column(name = "NEAR_RIVER")
	private boolean nearRiver = false;

	/**
	 * Option that the city is near the sea
	 */
	@Column(name = "NEAR_SEA")
	private boolean nearSea = false;

	/**
	 * Population
	 */
	@Column(name = "POPULATION")
	private int population = 162481;

	/**
	 * Rate for reference evaluation
	 */
	@Transient
	private int rate = 0;
	@OneToOne
	private BaseGeoInfo position = new BaseGeoInfo(GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(new JaxRSCoordinate[] {new JaxRSCoordinate(1,
				1), new JaxRSCoordinate(1,
				2), new JaxRSCoordinate(2,
				2), new JaxRSCoordinate(2,
				1), new JaxRSCoordinate(1,
				1)})); //@TODO: improve with correct geo information
	/**
	 * a point which is considered the most important in the geometry which is not forcibly always the geographical center of the referenced area
	 */
	private JaxRSCoordinate referencePoint;

	/**
	 * Default constructor
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
	 * @param position  
	 */
	public City(String name, int population, int altitude, boolean nearRiver, boolean nearSea, BaseGeoInfo position) {
		this.position = position;
		this.name = name;
		this.population = population;
		this.altitude = altitude;
		this.nearRiver = nearRiver;
		this.nearSea = nearSea;
	}

	public int getAltitude() {
		return this.altitude;
	}

	public String getName() {
		return this.name;
	}

	public int getPopulation() {
		return this.population;
	}

	public int getRate() {
		return this.rate;
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
	
	public void setRate(int rate) {
		this.rate = rate;
	}

	public void setPosition(BaseGeoInfo position) {
		this.position = position;
	}

	public BaseGeoInfo getPosition() {
		return position;
	}

	public void setReferencePoint(JaxRSCoordinate referencePoint) {
		this.referencePoint = referencePoint;
	}

	public JaxRSCoordinate getReferencePoint() {
		return referencePoint;
	}
}
