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

import de.pgalise.simulation.shared.geolocation.GeoLocation;

/**
 * A building is a {@link Boundary} with several openstreetmap tags.
 * Every building has a center point and area is given in m².
 * @author Timo
 */
public class Building extends Boundary {
	private static final long serialVersionUID = 5874106158590082263L;
	private int squareMeter;
	private GeoLocation centerPoint;
	private String tourism, sport, service, school, repair, amenity, attraction, shop, emergencyService, office,
	craft, leisure, military, publicTransport, gambling, landUseArea;	

	public Building(GeoLocation northEast, GeoLocation southWest, GeoLocation centerPoint) {
		super(northEast, southWest);
		this.centerPoint = centerPoint;
	}
	
	/**
	 * Constructor
	 * @param northEast the northeast point of the building
	 * @param southWest the southwest point of the building
	 * @param centerPoint the center of the building
	 * @param squareMeter the area of the building
	 * @param landUseArea
	 * @param tourism
	 * @param sport
	 * @param service
	 * @param school
	 * @param repair
	 * @param amenity
	 * @param attraction
	 * @param shop
	 * @param emergencyService
	 * @param office
	 * @param craft
	 * @param leisure
	 * @param military
	 * @param publicTransport
	 * @param gambling
	 */
	public Building(GeoLocation northEast, GeoLocation southWest, GeoLocation centerPoint,
			int squareMeter, String landUseArea, String tourism,
			String sport, String service, String school,
			String repair, String amenity, String attraction, String shop,
			String emergencyService, String office, String craft,
			String leisure, String military, String publicTransport,
			String gambling) {
		super(northEast, southWest);
		this.centerPoint = centerPoint;
		this.squareMeter = squareMeter;
		this.landUseArea = landUseArea;
		this.tourism = tourism;
		this.sport = sport;
		this.service = service;
		this.school = school;
		this.repair = repair;
		this.amenity = amenity;
		this.attraction = attraction;
		this.shop = shop;
		this.emergencyService = emergencyService;
		this.office = office;
		this.craft = craft;
		this.leisure = leisure;
		this.military = military;
		this.publicTransport = publicTransport;
		this.gambling = gambling;
	}



	public int getSquareMeter() {
		return squareMeter;
	}

	public void setSquareMeter(int squareMeter) {
		this.squareMeter = squareMeter;
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

	public String getLandUseArea() {
		return landUseArea;
	}

	public void setLandUseArea(String landUseArea) {
		this.landUseArea = landUseArea;
	}

	public GeoLocation getCenterPoint() {
		return centerPoint;
	}

	public void setCenterPoint(GeoLocation centerPoint) {
		this.centerPoint = centerPoint;
	}
}
