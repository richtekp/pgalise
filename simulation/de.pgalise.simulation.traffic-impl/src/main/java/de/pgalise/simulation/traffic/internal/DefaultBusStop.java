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
 
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.CityNodeTag;
import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Type;

/**
 * Contains all information for a bus stop.
 * The id, name and the graph {@link Node}.
 * @author Lena
 */
@Entity
public class DefaultBusStop extends DefaultTrafficNode<BusData> implements BusStop<DefaultTrafficNode<BusData>> {
	private static final long serialVersionUID = 1L;
	private String stopName;
	@OneToOne(mappedBy = "busStop")
	private DefaultBusStopInformation busStopInformation;
	@Type(type="org.hibernate.spatial.GeometryType")
	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
	private Coordinate location;
	
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

	protected DefaultBusStop() {
	}
	
	public DefaultBusStop(String name, DefaultBusStopInformation busStopInformation) {
		
		this.stopName = name;
		this.busStopInformation = busStopInformation;
	}

	public DefaultBusStop(String stopName,
		DefaultBusStopInformation busStopInformation,
		Coordinate location) {
		this.stopName = stopName;
		this.busStopInformation = busStopInformation;
		this.location = location;
	}

	@Override
	public Coordinate getLocation() {
		return location;
	}

	@Override
	public void setLocation(Coordinate location) {
		this.location = location;
	}

	/**
	 * @return the stopName
	 */
	@Override
	public String getStopName() {
		return stopName;
	}

	/**
	 * @param stopName the stopName to set
	 */
	@Override
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	@Override
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

	@Override
	public void setWheelchairBoarding(String wheelchairBoarding) {
		this.wheelchairBoarding = wheelchairBoarding;
	}
	
}
