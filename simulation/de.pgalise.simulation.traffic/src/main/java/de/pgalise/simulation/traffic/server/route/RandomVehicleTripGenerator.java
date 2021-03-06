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
package de.pgalise.simulation.traffic.server.route;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import java.util.Date;
import java.util.List;

/**
 * Generates (random) trips.
 *
 * @author Lena
 */
public interface RandomVehicleTripGenerator {

  /**
   * Creates a trip for the given vehicle type.
   *
   * @param startHomeNodes
   * @param startWorkNodes
   * @param vehicleType Vehicle type
   * @param date
   * @param buffer
   * @return TrafficTrip
   */
  public TrafficTrip createVehicleTrip(List<TrafficNode> startHomeNodes,
    List<TrafficNode> startWorkNodes,
    VehicleType vehicleType,
    Date date,
    int buffer);

  public List<TrafficNode> getHomeNodes();

  public RandomSeedService getRandomSeedService();

  public List<TrafficNode> getWorkNodes();

  public void setHomeNodes(List<TrafficNode> homeNodes);

  public void setRandomSeedService(RandomSeedService randomSeedService);

  public void setWorkNodes(List<TrafficNode> workNodes);

  /**
   * Create a traffic trip with given target node ID and time
   *
   * @param startHomeNodes List with start home nodes
   * @param targetNode Target node ID
   * @param startTimestamp startTimeWayThere
   * @return TrafficTrip
   */
  public TrafficTrip createVehicleTrip(List<TrafficNode> startHomeNodes,
    TrafficNode targetNode,
    long startTimestamp);

  /**
   * Create a traffic trip with given start node ID and time
   *
   * @param homeNodes List with target home nodes
   * @param startNode Start node ID
   * @param startTimestamp startTimeWayThere
   * @return TrafficTrip
   */
  public TrafficTrip createVehicleTrip(TrafficNode startNode,
    List<TrafficNode> homeNodes,
    long startTimestamp);
}
