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
import java.util.List;
import java.util.Map;

/**
 * A way consist of several instance of {@link Node}. Ways can be streets, oneway streets, highways, landuse, railways, cycleways or buildings.
 * Some ways have a max speed limit.
 * @author Timo
 */
public class Way implements Serializable {
	private static final long serialVersionUID = 2942128399393060939L;
	private int maxSpeed;
	private List<Node> nodeList;
	private boolean oneWay, building;
	private String streetname, highway, landuse, railway, cycleway;
	private Map<String, String> buildingTypeMap;

	/**
	 * Default
	 */
	public Way() {}
	
	/**
	 * Constructor
	 * @param maxSpeed
	 * 			max speed limit for the street
	 * @param nodeList
	 * 			all node of the street
	 * @param oneWay
	 * 			is it a one way street
	 * @param building
	 * 			is it a building
	 * @param streetname
	 * 			a street name (if there is any)
	 * @param highway
	 * 			a highway name (if there is any)
	 * @param landuse
	 * 			a landuse tag (if there is any)
	 * @param railway
	 * 			a railway tag (if there is any)
	 * @param buildingTypeMap
	 * 			all possible information about this build (if there are any information)
	 * @param cycleway
	 * 			a cycle way tag (if there is any)
	 */
	public Way(int maxSpeed, List<Node> nodeList, boolean oneWay,
			boolean building, String streetname, String highway,
			String landuse, String railway, Map<String, String> buildingTypeMap, String cycleway) {
		this.maxSpeed = maxSpeed;
		this.nodeList = nodeList;
		this.oneWay = oneWay;
		this.building = building;
		this.streetname = streetname;
		this.highway = highway;
		this.landuse = landuse;
		this.railway = railway;
		this.buildingTypeMap = buildingTypeMap;
		this.cycleway = cycleway;
	}

	public String getHighway() {
		return this.highway;
	}

	public String getLanduse() {
		return this.landuse;
	}

	public int getMaxSpeed() {
		return this.maxSpeed;
	}

	public List<Node> getNodeList() {
		return this.nodeList;
	}

	public String getStreetname() {
		return this.streetname;
	}

	public boolean isOneWay() {
		return this.oneWay;
	}

	public void setHighway(String highway) {
		this.highway = highway;
	}

	public void setLanduse(String landuse) {
		this.landuse = landuse;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}

	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}

	public String getRailway() {
		return railway;
	}

	public void setRailway(String railway) {
		this.railway = railway;
	}

	public boolean isBuilding() {
		return building;
	}

	public void setBuilding(boolean building) {
		this.building = building;
	}

	public Map<String, String> getBuildingTypeMap() {
		return buildingTypeMap;
	}

	public void setBuildingTypeMap(Map<String, String> buildingTypeMap) {
		this.buildingTypeMap = buildingTypeMap;
	}

	public String getCycleway() {
		return cycleway;
	}

	public void setCycleway(String cycleway) {
		this.cycleway = cycleway;
	}
}
