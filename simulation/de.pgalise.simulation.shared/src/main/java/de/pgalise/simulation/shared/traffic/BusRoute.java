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
 
package de.pgalise.simulation.shared.traffic;

import java.io.Serializable;

/**
 * Contains information about a bus route.
 * The ID, long name, short, name, the route type
 * and a flag, if it's used or not.
 * @author Lena
 */
public class BusRoute implements Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3115299698580903433L;

	/**
	 * ID of the route
	 */
	private String routeId;

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

	/**
	 * Default constructor
	 */
	public BusRoute() {

	}
	
	
	public BusRoute(String routeId, String routeShortName, String routeLongName, int routeType) {
		this.routeId = routeId;
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.routeType = routeType;
		this.used = true;
	}

	/**
	 * Constructor
	 * 
	 * @param routeId
	 *            ID of the route
	 * @param routeShortName
	 *            Short name
	 * @param routeLongName
	 *            Long name
	 * @param routeType
	 *            Route type
	 */
	public BusRoute(String routeId, String routeShortName, String routeLongName, int routeType, boolean used) {
		this.routeId = routeId;
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.routeType = routeType;
		this.used = used;
	}

	/**
	 * @return the routeId
	 */
	public String getRouteId() {
		return routeId;
	}

	/**
	 * @param routeId
	 *            the routeId to set
	 */
	public void setRouteId(String routeId) {
		this.routeId = routeId;
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
}
