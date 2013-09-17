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

import java.io.Serializable;

import org.graphstream.graph.Graph;

/**
 * A node is just a point with latitude and longitude. It has an ID (e.g. from openstreetmap) and several tags about what it is.
 * A node can be part of several ways, junctions or buildings. It is also possible that a node is a roundabout. 
 * 
 * Nodes for a way. Nodes can be round abouts.
 * 
 * @author Timo
 */
public class Node implements Serializable {
	private static final long serialVersionUID = 4282516662264224410L;
	private String id, landuse, tourism, sport, service, school, repair, amenity, attraction, shop, emergencyService, office,
	craft, leisure, military, publicTransport, gambling;
	private boolean isRoundabout;
	private double latitude, longitude;
	

	/**
	 * Default
	 */
	public Node() {
	}

	/**
	 * Default
	 * @param id
	 * 			the node ID will be used by the {@link Graph}
	 * @param latitude
	 * @param longitude
	 */
	public Node(String id, double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public String getLanduse() {
		return this.landuse;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public boolean isRoundabout() {
		return this.isRoundabout;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLanduse(String landuse) {
		this.landuse = landuse;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setRoundabout(boolean isRoundabout) {
		this.isRoundabout = isRoundabout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isRoundabout ? 1231 : 1237);
		result = prime * result + ((landuse == null) ? 0 : landuse.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (isRoundabout != other.isRoundabout) {
			return false;
		}
		if (landuse == null) {
			if (other.landuse != null) {
				return false;
			}
		} else if (!landuse.equals(other.landuse)) {
			return false;
		}
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude)) {
			return false;
		}
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude)) {
			return false;
		}
		return true;
	}

	public String getTourism() {
		return tourism;
	}

	public void setTourism(String tourism) {
		this.tourism = tourism;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getRepair() {
		return repair;
	}

	public void setRepair(String repair) {
		this.repair = repair;
	}

	public String getAmenity() {
		return amenity;
	}

	public void setAmenity(String amenity) {
		this.amenity = amenity;
	}

	public String getAttraction() {
		return attraction;
	}

	public void setAttraction(String attraction) {
		this.attraction = attraction;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public String getEmergencyService() {
		return emergencyService;
	}

	public void setEmergencyService(String emergencyService) {
		this.emergencyService = emergencyService;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getCraft() {
		return craft;
	}

	public void setCraft(String craft) {
		this.craft = craft;
	}

	public String getLeisure() {
		return leisure;
	}

	public void setLeisure(String leisure) {
		this.leisure = leisure;
	}

	public String getMilitary() {
		return military;
	}

	public void setMilitary(String military) {
		this.military = military;
	}

	public String getPublicTransport() {
		return publicTransport;
	}

	public void setPublicTransport(String publicTransport) {
		this.publicTransport = publicTransport;
	}

	public String getGambling() {
		return gambling;
	}

	public void setGambling(String gambling) {
		this.gambling = gambling;
	}
}
