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
 
package de.pgalise.util.datamodel;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 
 * @author marcus
 *
 */
@Entity
//@Table(name = "PGALISE.BUS_ROUTES")
public class BusRoute extends AbstractIdentifiable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "ROUTE_SHORT_NAME")
	private String routeShortName;
	
	@Column(name = "ROUTE_LONG_NAME")
	private String routeLongName;
	
	@Column(name = "ROUTE_TYPE")
	private short routeType;
	
	@Column(name = "AGENCY_ID")
	private String agencyId;
	
	@Column(name = "ROUTE_DESC")
	private String routeDesc;
	
	@Column(name = "ROUTE_URL")
	private String routeUrl;
	
	@Column(name = "ROUTE_COLOR")
	private String routeColor;
	
	@Column(name = "ROUTE_TEXT_COLOR")
	private String routeTextColor;
	
	public BusRoute() {}

	public BusRoute(String routeShortName,
			String routeLongName, short routeType, String agencyId,
			String routeDesc, String routeUrl, String routeColor,
			String routeTextColor) {
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.routeType = routeType;
		this.agencyId = agencyId;
		this.routeDesc = routeDesc;
		this.routeUrl = routeUrl;
		this.routeColor = routeColor;
		this.routeTextColor = routeTextColor;
	}

	public String getRouteShortName() {
		return routeShortName;
	}

	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	public String getRouteLongName() {
		return routeLongName;
	}

	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	public short getRouteType() {
		return routeType;
	}

	public void setRouteType(short routeType) {
		this.routeType = routeType;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
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
