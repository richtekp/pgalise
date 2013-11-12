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
 
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.BusAgency;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Contains information about a bus route.
 * The ID, long name, short, name, the route type
 * and a flag, if it's used or not.
 * @author Lena
 */
@Entity
public class BusRoute extends BusLineInformation {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3115299698580903433L;

	/**
	 * Short name
	 */
	private String routeShortName;

	/**
	 * Long name
	 */
	private String routeLongName;

	/**
	 * Route type
	 */
	private int routeType;
	
	/**
	 * Used in simulation
	 */
	private boolean used;	
	
	@Column(name = "AGENCY_ID")
	private BusAgency agency;
	
	@Column(name = "ROUTE_DESC")
	private String routeDesc;
	
	@Column(name = "ROUTE_URL")
	private String routeUrl;
	
	@Column(name = "ROUTE_COLOR")
	private String routeColor;
	
	@Column(name = "ROUTE_TEXT_COLOR")
	private String routeTextColor;
	
	private BusData bus;

	/**
	 * Default constructor
	 */
	protected BusRoute() {
	}

	public BusRoute(String routeShortName,
		String routeLongName,
		int routeType,
		boolean used,
		BusAgency agency,
		String routeDesc,
		String routeUrl,
		String routeColor,
		String routeTextColor) {
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.routeType = routeType;
		this.used = used;
		this.agency = agency;
		this.routeDesc = routeDesc;
		this.routeUrl = routeUrl;
		this.routeColor = routeColor;
		this.routeTextColor = routeTextColor;
	}
	
	
	public BusRoute(String routeShortName, String routeLongName, int routeType) {
		this(routeShortName,
			routeLongName,
			routeType,
			true);
	}

	/**
	 * Constructor
	 * 
	 * @param routeShortName
	 *            Short name
	 * @param routeLongName
	 *            Long name
	 * @param routeType
	 *            Route type
	 */
	public BusRoute(String routeShortName, String routeLongName, int routeType, boolean used) {
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.routeType = routeType;
		this.used = used;
	}

	/**
	 * @return the routeShortName
	 */
	public String getRouteShortName() {
		return routeShortName;
	}

	/**
	 * @param routeShortName
	 *            the routeShortName to set
	 */
	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	/**
	 * @return the routeLongName
	 */
	public String getRouteLongName() {
		return routeLongName;
	}

	/**
	 * @param routeLongName
	 *            the routeLongName to set
	 */
	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	/**
	 * @return the routeType
	 */
	public int getRouteType() {
		return routeType;
	}

	/**
	 * @param routeType
	 *            the routeType to set
	 */
	public void setRouteType(int routeType) {
		this.routeType = routeType;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public BusAgency getAgency() {
		return agency;
	}

	public void setAgency(BusAgency agencyId) {
		this.agency = agencyId;
	}

	public String getRouteDesc() {
		return routeDesc;
	}

	public void setRouteDesc(String routeDesc) {
		this.routeDesc = routeDesc;
	}

	public String getRouteUrl() {
		return routeUrl;
	}

	public void setRouteUrl(String routeUrl) {
		this.routeUrl = routeUrl;
	}

	public String getRouteColor() {
		return routeColor;
	}

	public void setRouteColor(String routeColor) {
		this.routeColor = routeColor;
	}

	public String getRouteTextColor() {
		return routeTextColor;
	}

	public void setRouteTextColor(String routeTextColor) {
		this.routeTextColor = routeTextColor;
	}
}
