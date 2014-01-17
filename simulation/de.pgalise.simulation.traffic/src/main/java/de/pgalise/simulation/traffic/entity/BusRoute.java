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
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.traffic.entity.TransportLineInformation;
import de.pgalise.simulation.shared.entity.BusAgency;
import de.pgalise.simulation.traffic.TransportLineTypeEnum;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Contains information about a bus route. The ID, long name, short, name, the
 * route type and a flag, if it's used or not.
 *
 * @author Lena
 */
@Entity
@ManagedBean
@SessionScoped
public class BusRoute extends TransportLineInformation
{

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
	 * Used in simulation
	 */
	private Boolean used;

	@ManyToOne
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

	public BusRoute(Long id,
					String routeShortName,
					String routeLongName,
					boolean used,
					BusAgency agency,
					String routeDesc,
					String routeUrl,
					String routeColor,
					String routeTextColor) {
		super(id,
						TransportLineTypeEnum.BUS);
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.used = used;
		this.agency = agency;
		this.routeDesc = routeDesc;
		this.routeUrl = routeUrl;
		this.routeColor = routeColor;
		this.routeTextColor = routeTextColor;
	}

	public BusRoute(Long id,
					String routeShortName,
					String routeLongName) {
		this(id,
						routeShortName,
						routeLongName,
						true);
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param routeShortName Short name
	 * @param routeLongName  Long name
	 * @param used
	 */
	public BusRoute(Long id,
					String routeShortName,
					String routeLongName,
					boolean used) {
		super(id,
						TransportLineTypeEnum.BUS);
		this.routeShortName = routeShortName;
		this.routeLongName = routeLongName;
		this.used = used;
	}

	/**
	 * @return the routeShortName
	 */
	public String getRouteShortName() {
		return routeShortName;
	}

	/**
	 * @param routeShortName the routeShortName to set
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
	 * @param routeLongName the routeLongName to set
	 */
	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
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
