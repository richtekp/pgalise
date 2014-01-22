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
 *//* 
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
package de.pgalise.simulation.traffic.internal.server.eventhandler.vehicle;

import java.util.Random;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.internal.server.eventhandler.AbstractVehicleEventHandler;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.entity.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.sensor.AbstractStaticTrafficSensor;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.util.List;

/**
 * If a vehicle reached its target a VEHICLE_REACHED_TARGET event is thrown.
 * This handler handles the event and notifies all sensors which are registered
 * on this target node. Furthermore (if the vehicle is a bus) the value of the
 * infrared sensor changes if the node is a busstop. In addition all vehicles
 * which were created by an attraction event will be prepared for its way back.
 *
 *
 * @param <D>
 * @author marcus
 * @author Lena
 */
public class VehicleReachedTargetHandler<D extends VehicleData> extends AbstractVehicleEventHandler<VehicleData, VehicleEvent> {

  public VehicleReachedTargetHandler() {
  }

  @Override
  public EventType getTargetEventType() {
    return VehicleEventTypeEnum.VEHICLE_REACHED_TARGET;
  }

  @Override
  public void handleEvent(VehicleEvent event) {
    for (final StaticTrafficSensor sensor : event.getTrafficGraphExtensions().
      getSensors(event.getVehicle().getCurrentNode())) {
      if (sensor instanceof AbstractStaticTrafficSensor) {
        ((AbstractStaticTrafficSensor) sensor).vehicleOnNodeRegistered(event.
          getVehicle());
      }
    }

    if (event.getVehicle().getData() instanceof BusData) {
      NavigationNode n = event.getVehicle().getCurrentNode();
      // only at busstops the amount of passengers can change
      if (n != null) {
        Random random = new Random(getRandomSeedService()
          .getSeed(VehicleReachedTargetHandler.class.getName()));
        int max = ((BusData) event.getVehicle().getData()).
          getMaxPassengerCount();
        BusData temp = (BusData) event.getVehicle().getData();
        temp.setCurrentPassengerCount(random.nextInt(max));

        if (temp.getInfraredSensor() != null) {
          ((InfraredSensor) temp.getInfraredSensor()).sendDataOnNextUpdate();
        }
      }
    }

    if (event.getEventForVehicleMap().containsKey(event.getVehicle().getId())) {
      TrafficEvent te = event.getEventForVehicleMap().get(event.getVehicle().
        getId());
      if (te instanceof AttractionTrafficEvent) {
        // logger.debug("Vehicle " + event.getVehicle().getName() + " reached attraction");
        AttractionTrafficEvent<?> e = ((AttractionTrafficEvent) te);
        long startTime = e.getAttractionEndTimestamp();

        if (event.getSimulationTime() >= e.getAttractionEndTimestamp()) {
          startTime = event.getSimulationTime() + event.getResponsibleServer().
            getUpdateIntervall();
        }

        /*
         * Way back: Create way from the target node ID to a random node
         */
        // use node as start node
        TrafficTrip trip = event.getResponsibleServer().createTrip(event.
          getResponsibleServer().getCityZone(),
          e.getNodeID(),
          startTime,
          true);

        if (trip != null) {
          modifyVehicleForWayBack(event,
            trip);
          // create trips for the other servers
          for (int i = 0; i < event.getResponsibleServer().getServerListSize(); i++) {
            trip = event.getResponsibleServer().createTrip(event.
              getResponsibleServer().getCityZone(),
              e.getNodeID(),
              startTime,
              true);

            modifyVehicleForWayBack(event,
              trip);
          }
        }
      }
    }
  }

  private void modifyVehicleForWayBack(VehicleEvent event,
    TrafficTrip trip) {
    List<TrafficEdge> path = event.getResponsibleServer().getShortestPath(
      trip.getStartNode(),
      trip.getTargetNode());
    if (path != null) {
      event.getVehicle().setPath(path);
      event.getVehicle().setVehicleState(VehicleStateEnum.NOT_STARTED);

      scheduleVehicle(event.getVehicle(),
        trip.getStartTime(),
        event);
    }
  }

  private void scheduleVehicle(Vehicle<?> vehicle,
    long startTime,
    VehicleEvent event) {
    if (vehicle != null) {
      // try {
      ScheduleItem item = new ScheduleItem(vehicle,
        startTime,
        event.getResponsibleServer().getUpdateIntervall());
      event.getResponsibleServer().getItemsToScheduleAfterAttractionReached().
        add(item);
      // event.getResponsibleServer().getScheduler().scheduleItem(item);
      // } catch (IllegalAccessException e1) {
      // e1.printStackTrace();
      // }
    }
  }
}
