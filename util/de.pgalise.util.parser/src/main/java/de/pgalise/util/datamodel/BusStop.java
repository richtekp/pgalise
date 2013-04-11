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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author marcus
 *
 */
@Entity
@Table(name = "PGALISE.BUS_STOPS")
public final class BusStop {
	
	@Column(name = "STOP_NAME")
	private String stopName;
	
	@Id
	@Column(name = "STOP_ID")
	private String stopId;
	
	@Column(name = "STOP_LAT")
	private double stopLat;
	
	@Column(name = "STOP_LON")
	private double stopLon;
	
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
	
	public BusStop() {}

	public BusStop(String stopName, String stopId, double stopLat,
			double stopLon, String stopCode, String zoneId, String stopUrl,
			String locationType, String parentStation, String stopTimezone,
			String wheelchairBoarding) {
		super();
		this.stopName = stopName;
		this.stopId = stopId;
		this.stopLat = stopLat;
		this.stopLon = stopLon;
		this.stopCode = stopCode;
		this.zoneId = zoneId;
		this.stopUrl = stopUrl;
		this.locationType = locationType;
		this.parentStation = parentStation;
		this.stopTimezone = stopTimezone;
		this.wheelchairBoarding = wheelchairBoarding;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getStopId() {
		return stopId;
	}

	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	public double getStopLat() {
		return stopLat;
	}

	public void setStopLat(double stopLat) {
		this.stopLat = stopLat;
	}

	public double getStopLon() {
		return stopLon;
	}

	public void setStopLon(double stopLon) {
		this.stopLon = stopLon;
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
