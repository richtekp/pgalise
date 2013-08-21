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

import java.util.UUID;

import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * The road barrier traffic event will block a route during the given time window.
 * 
 * @author Timo
 */
public class RoadBarrierTrafficEvent extends TrafficEvent {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6150317787250714891L;

	/**
	 * Start time of the event
	 */
	private long roadBarrierStartTimestamp;

	/**
	 * End time of the event
	 */
	private long roadBarrierEndTimestamp;

	/**
	 * Position to block
	 */
	private Coordinate roadBarrierPoint;

	/**
	 * node id in graph
	 */
	private String nodeID;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            ID of the event
	 * @param roadBarrierStartTimestamp
	 *            Start time of the event
	 * @param roadBarrierEndTimestamp
	 *            End time of the event
	 * @param roadBarrierPoint
	 *            Position to block
	 * @param nodeID
	 *            node id in graph
	 */
	public RoadBarrierTrafficEvent(UUID id, long roadBarrierStartTimestamp, long roadBarrierEndTimestamp,
			Coordinate roadBarrierPoint, String nodeID) {
		super(id, SimulationEventTypeEnum.ROAD_BARRIER_TRAFFIC_EVENT);
		this.roadBarrierStartTimestamp = roadBarrierStartTimestamp;
		this.roadBarrierEndTimestamp = roadBarrierEndTimestamp;
		this.roadBarrierPoint = roadBarrierPoint;
		this.nodeID = nodeID;
	}

	public long getRoadBarrierStartTimestamp() {
		return roadBarrierStartTimestamp;
	}

	public void setRoadBarrierStartTimestamp(long roadBarrierStartTimestamp) {
		this.roadBarrierStartTimestamp = roadBarrierStartTimestamp;
	}

	public long getRoadBarrierEndTimestamp() {
		return roadBarrierEndTimestamp;
	}

	public void setRoadBarrierEndTimestamp(long roadBarrierEndTimestamp) {
		this.roadBarrierEndTimestamp = roadBarrierEndTimestamp;
	}

	public Coordinate getRoadBarrierPoint() {
		return roadBarrierPoint;
	}

	public void setRoadBarrierPoint(Coordinate roadBarrierPoint) {
		this.roadBarrierPoint = roadBarrierPoint;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

}
