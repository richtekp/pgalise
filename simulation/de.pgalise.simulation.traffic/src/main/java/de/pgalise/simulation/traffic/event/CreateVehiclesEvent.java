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
import java.util.List;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Creates vehicles with given path.
 *
 * @param <D>
 * @author Timo
 * @author Andreas Rehfeldt
 */
@XmlRootElement
public class CreateVehiclesEvent<D extends VehicleData> extends AbstractVehicleEvent<D> {

  /**
   * Serial
   */
  private static final long serialVersionUID = -5015354611010459052L;

  /**
   * List with vehicle informations
   */
  private List<CreateRandomVehicleData> vehicles;

  public CreateVehiclesEvent(
    TrafficControllerLocal<VehicleEvent> server,
    long simulationTime,
    long elapsedTime,
    List<CreateRandomVehicleData> vehicles) {
    super(
      server,
      simulationTime,
      elapsedTime,
      null);
    this.vehicles = vehicles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((vehicles == null) ? 0 : vehicles.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CreateVehiclesEvent<?> other = (CreateVehiclesEvent) obj;
    if (vehicles == null) {
      if (other.vehicles != null) {
        return false;
      }
    } else if (!vehicles.equals(other.vehicles)) {
      return false;
    }
    return true;
  }

  public List<CreateRandomVehicleData> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<CreateRandomVehicleData> vehicles) {
    this.vehicles = vehicles;
  }

  @Override
  public EventType getType() {
    return TrafficEventTypeEnum.CREATE_VEHICLES_EVENT;
  }
}
