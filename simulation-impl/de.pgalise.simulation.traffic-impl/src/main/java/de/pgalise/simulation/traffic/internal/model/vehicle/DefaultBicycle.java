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
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a bicycle
 *
 * @author Mustafa
 * @version 1.0 (Feb 11, 2013)
 */
public class DefaultBicycle extends BaseVehicle<BicycleData> implements Bicycle {

  /**
   * Serial
   */
  private static final long serialVersionUID = -7152958132550318840L;

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.
    getLogger(DefaultBicycle.class);

  protected DefaultBicycle() {
  }

  /**
   * Default constructor
   *
   * @param id
   * @param data Information
   * @param trafficGraphExtensions TrafficGraphExtensions
   */
  public DefaultBicycle(Long id,
    BicycleData data,
    TrafficGraphExtensions trafficGraphExtensions) {
    super(id,
      data,
      trafficGraphExtensions);
  }

  /**
   * Default constructor
   *
   * @param id
   * @param data Information
   * @param trafficGraphExtensions TrafficGraphExtensions
   * @param position
   */
  public DefaultBicycle(Long id,
    BicycleData data,
    TrafficGraphExtensions trafficGraphExtensions,
    BaseCoordinate position) {
    super(id,
      data,
      trafficGraphExtensions,
      position);
  }

  @Override
  protected void passedNode(TrafficNode node) {
    if (this.getPreviousEdge() != null) {
      log.debug("Unregistering bycicle " + this.getName() + " from edge: "
        + this.getPreviousEdge().getId());
      this.getTrafficGraphExtensions().
        unregisterFromEdge(this.getPreviousEdge(),
          this.getPreviousNode(),
          this.getCurrentNode(),
          this);
    }

    if (VehicleStateEnum.UPDATEABLE_VEHICLES.contains(this.getVehicleState())) {
      if (this.getCurrentEdge() != null) {
        log.debug("Registering bycicle " + this.getName() + " on edge: " + this.
          getCurrentEdge().getId());
        this.getTrafficGraphExtensions().registerOnEdge(this.getCurrentEdge(),
          this.getCurrentNode(),
          this.getNextNode(),
          this);
      }
    }
  }

  @Override
  protected void postUpdate(TrafficNode node) {
  }

  @Override
  protected void preUpdate(long elapsedTime) {

  }
}
