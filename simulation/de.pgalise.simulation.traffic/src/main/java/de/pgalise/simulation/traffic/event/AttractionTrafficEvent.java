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

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import java.util.List;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The attraction traffic event will lead to more traffic on the given point in
 * the given time window.
 *
 * @param <D>
 * @author Timo
 */
@XmlRootElement
public class AttractionTrafficEvent<D extends VehicleData> extends CreateRandomVehiclesEvent<D> {

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
  private TrafficNode nodeID;

  public AttractionTrafficEvent(
    TrafficControllerLocal<VehicleEvent> server,
    long simulationTimestamp,
    long elapsedTime,
    long attractionStartTimestamp,
    long attractionEndTimestamp,
    TrafficNode nodeID,
    List<CreateRandomVehicleData> createRandomVehicleDataList) {
    super(
      server,
      simulationTimestamp,
      elapsedTime,
      createRandomVehicleDataList);
    this.attractionStartTimestamp = attractionStartTimestamp;
    this.attractionEndTimestamp = attractionEndTimestamp;
    this.nodeID = nodeID;
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

  public TrafficNode getNodeID() {
    return nodeID;
  }

  public void setNodeID(TrafficNode nodeID) {
    this.nodeID = nodeID;
  }

  @Override
  public EventType getType() {
    return TrafficEventTypeEnum.ATTRACTION_TRAFFIC_EVENT;
  }
}
