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

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Contains all information for a bus stop.
 * The id, name and the graph {@link Node}.
 * @author Lena
 */
@Entity
public class BusStop extends TrafficNode {
	private static final long serialVersionUID = 1L;
	private String stopName;
	@OneToOne(mappedBy = "busStop")
	private BusStopInformation busStopInformation;
	
	@Column(name = "STOP_CODE")
	private String stopCode;
	
	@Column(name = "ZONE_ID")
	private String zoneId;
	
	@Column(name = "STOP_URL")
	private String stopUrl;
	
	@Column(name = "LOCATION_TYPE")
	private String locationType;
	
	@Column(name = "PARENT_STATION")
	private String parentStation;
	
	@Column(name = "STOP_TIMEZONE")
	private String stopTimezone;
	
	@Column(name = "WHEELCHAIR_BOARDING")
	private String wheelchairBoarding;

	protected BusStop() {
	}
	
	public BusStop(String name, BusStopInformation busStopInformation, JaxRSCoordinate geoLocation) {
		super(geoLocation);
		this.stopName = name;
		this.busStopInformation = busStopInformation;
	}

	/**
	 * @return the stopName
	 */
	public String getStopName() {
		return stopName;
	}

	/**
	 * @param stopName the stopName to set
	 */
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getStopCode() {
		return stopCode;
	}

	public void setStopCode(String stopCode) {
		this.stopCode = stopCode;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getStopUrl() {
		return stopUrl;
	}

	public void setStopUrl(String stopUrl) {
		this.stopUrl = stopUrl;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getParentStation() {
		return parentStation;
	}

	public void setParentStation(String parentStation) {
		this.parentStation = parentStation;
	}

	public String getStopTimezone() {
		return stopTimezone;
	}

	public void setStopTimezone(String stopTimezone) {
		this.stopTimezone = stopTimezone;
	}

	public String getWheelchairBoarding() {
		return wheelchairBoarding;
	}

	public void setWheelchairBoarding(String wheelchairBoarding) {
		this.wheelchairBoarding = wheelchairBoarding;
	}
	
}
