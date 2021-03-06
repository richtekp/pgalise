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
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import java.util.UUID;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Removes the vehicles with the given {@link UUID}.
 *
 * @param <D>
 * @author Timo
 */
@XmlRootElement
public class DeleteVehiclesEvent<D extends VehicleData> extends AbstractVehicleEvent<D> {

  /**
   * Serial
   */
  private static final long serialVersionUID = 1533612793042541169L;

  public DeleteVehiclesEvent(TrafficControllerLocal<VehicleEvent> server,
    long simulationTime,
    long elapsedTime,
    Vehicle<D> deleteVehicle) {
    super(server,
      simulationTime,
      elapsedTime,
      deleteVehicle);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((getVehicle() == null) ? 0 : getVehicle().
      hashCode());
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
    DeleteVehiclesEvent<?> other = (DeleteVehiclesEvent) obj;
    if (getVehicle() == null) {
      if (other.getVehicle() != null) {
        return false;
      }
    } else if (!getVehicle().equals(other.getVehicle())) {
      return false;
    }
    return true;
  }

  @Override
  public EventType getType() {
    return TrafficEventTypeEnum.DELETE_VEHICLES_EVENT;
  }
}
