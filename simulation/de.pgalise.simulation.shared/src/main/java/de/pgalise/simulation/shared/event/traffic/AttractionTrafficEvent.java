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
 
package de.pgalise.simulation.shared.event.traffic;

import java.util.List;
import java.util.UUID;

import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * The attraction traffic event will lead to more traffic on the given point in the given time window.
 * 
 * @author Timo
 */
public class AttractionTrafficEvent extends CreateRandomVehiclesEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8298021404824259440L;

	/**
	 * when will the attraction start.
	 */
	private long attractionStartTimestamp;

	/**
	 * when will the attraction end.
	 */
	private long attractionEndTimestamp;
	
	/**
	 * Node id in graph
	 */
	private String nodeID;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param attractionStartTimestamp
	 *            when will the attraction start.
	 * @param attractionEndTimestamp
	 *            when will the attraction end.
	 * @param nodeID
	 * 			node id in graph.
	 */
	public AttractionTrafficEvent(long attractionStartTimestamp, long attractionEndTimestamp,
			String nodeID, List<CreateRandomVehicleData> createRandomVehicleDataList) {
		super(createRandomVehicleDataList);
		this.attractionStartTimestamp = attractionStartTimestamp;
		this.attractionEndTimestamp = attractionEndTimestamp;
		this.nodeID = nodeID;
		super.setType(SimulationEventTypeEnum.ATTRACTION_TRAFFIC_EVENT);
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

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
}
