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
 
package de.pgalise.simulation.controlCenter.internal.model;

import de.pgalise.simulation.shared.geolocation.GeoLocation;

/**
 * Data to create an attraction event.
 * @author Timo
 */
public class AttractionData {
	private int id;
	
	/**
	 * when will the attraction start.
	 */
	private long attractionStartTimestamp;

	/**
	 * when will the attraction end.
	 */
	private long attractionEndTimestamp;

	/**
	 * where is the point of interest.
	 */
	private GeoLocation attractionPoint;
	
	/**
	 * Node id in graph
	 */
	private String nodeID;
	
	/**
	 * This vehicles will drive to the attraction point.
	 */
	private RandomVehicleBundle randomVehicleBundle;

	/**
	 * Constructor
	 * @param attractionStartTimestamp
	 * @param attractionEndTimestamp
	 * @param attractionPoint
	 * @param nodeID
	 * 			needs to be a valid node ID on the graph
	 * @param randomVehicleBundle
	 * 			defines which vehicle will drive to the attraction
	 */
	public AttractionData(int id, long attractionStartTimestamp,
			long attractionEndTimestamp, GeoLocation attractionPoint,
			String nodeID, RandomVehicleBundle randomVehicleBundle) {
		this.id = id;
		this.attractionStartTimestamp = attractionStartTimestamp;
		this.attractionEndTimestamp = attractionEndTimestamp;
		this.attractionPoint = attractionPoint;
		this.nodeID = nodeID;
		this.randomVehicleBundle = randomVehicleBundle;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getAttractionStartTimestamp() {
		return attractionStartTimestamp;
	}

	public void setAttractionStartTimestamp(long attractionStartTimestamp) {
		this.attractionStartTimestamp = attractionStartTimestamp;
	}

	public long getAttractionEndTimestamp() {
		return attractionEndTimestamp;
	}

	public void setAttractionEndTimestamp(long attractionEndTimestamp) {
		this.attractionEndTimestamp = attractionEndTimestamp;
	}

	public GeoLocation getAttractionPoint() {
		return attractionPoint;
	}

	public void setAttractionPoint(GeoLocation attractionPoint) {
		this.attractionPoint = attractionPoint;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public RandomVehicleBundle getRandomVehicleBundle() {
		return randomVehicleBundle;
	}

	public void setRandomVehicleBundle(RandomVehicleBundle randomVehicleBundle) {
		this.randomVehicleBundle = randomVehicleBundle;
	}
}
