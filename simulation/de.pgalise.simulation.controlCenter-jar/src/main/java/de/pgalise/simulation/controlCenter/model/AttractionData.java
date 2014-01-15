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
 
package de.pgalise.simulation.controlCenter.model;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.traffic.entity.TrafficNode;

/**
 * Data to create an attraction event.
 * @author Timo
 */
public class AttractionData extends Identifiable {
	private static final long serialVersionUID = 1L;
	
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
	private JaxRSCoordinate attractionPoint;
	
	/**
	 * Node id in graph
	 */
	private TrafficNode nodeID;
	
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
	public AttractionData(long attractionStartTimestamp,
			long attractionEndTimestamp, JaxRSCoordinate attractionPoint,
			TrafficNode nodeID, RandomVehicleBundle randomVehicleBundle) {
		this.attractionStartTimestamp = attractionStartTimestamp;
		this.attractionEndTimestamp = attractionEndTimestamp;
		this.attractionPoint = attractionPoint;
		this.nodeID = nodeID;
		this.randomVehicleBundle = randomVehicleBundle;
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

	public JaxRSCoordinate getAttractionPoint() {
		return attractionPoint;
	}

	public void setAttractionPoint(JaxRSCoordinate attractionPoint) {
		this.attractionPoint = attractionPoint;
	}

	public TrafficNode getNodeID() {
		return nodeID;
	}

	public void setNodeID(TrafficNode nodeID) {
		this.nodeID = nodeID;
	}

	public RandomVehicleBundle getRandomVehicleBundle() {
		return randomVehicleBundle;
	}

	public void setRandomVehicleBundle(RandomVehicleBundle randomVehicleBundle) {
		this.randomVehicleBundle = randomVehicleBundle;
	}
}
