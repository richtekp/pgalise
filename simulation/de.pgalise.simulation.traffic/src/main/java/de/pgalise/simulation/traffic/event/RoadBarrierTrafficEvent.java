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
package de.pgalise.simulation.traffic.event;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The road barrier traffic event will block a route during the given time
 * window.
 *
 * @param <N>
 * @param <E>
 * @author Timo
 */
@XmlRootElement
public class RoadBarrierTrafficEvent< D extends VehicleData> extends AbstractTrafficEvent<D, VehicleEvent> {

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
  private BaseCoordinate roadBarrierPoint;

  /**
   * node id in graph
   */
  private TrafficNode nodeID;

  public RoadBarrierTrafficEvent(
    TrafficControllerLocal<VehicleEvent> responsibleServer,
    long simulationTimestamp,
    long elapsedTime,
    long roadBarrierStartTimestamp,
    long roadBarrierEndTimestamp,
    BaseCoordinate roadBarrierPoint,
    TrafficNode nodeID
  ) {
    super(responsibleServer,
      simulationTimestamp,
      elapsedTime);
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

  public BaseCoordinate getRoadBarrierPoint() {
    return roadBarrierPoint;
  }

  public void setRoadBarrierPoint(BaseCoordinate roadBarrierPoint) {
    this.roadBarrierPoint = roadBarrierPoint;
  }

  public TrafficNode getNodeID() {
    return nodeID;
  }

  public void setNodeID(TrafficNode nodeID) {
    this.nodeID = nodeID;
  }

  @Override
  public EventType getType() {
    return TrafficEventTypeEnum.ROAD_BARRIER_TRAFFIC_EVENT;
  }

}
