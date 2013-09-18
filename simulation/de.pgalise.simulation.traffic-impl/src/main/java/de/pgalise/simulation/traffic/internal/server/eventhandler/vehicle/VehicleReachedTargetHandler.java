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
 
package de.pgalise.simulation.traffic.internal.server.eventhandler.vehicle;

import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.event.traffic.AttractionTrafficEvent;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle.State;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventType;
import de.pgalise.simulation.traffic.server.scheduler.Item;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;

/**
 * If a vehicle reached its target a VEHICLE_REACHED_TARGET event is thrown. This handler handles 
 * the event and notifies all sensors which are registered on this target node. Furthermore (if 
 * the vehicle is a bus) the value of the infrared sensor changes if the node is a busstop. 
 * In addition all vehicles which were created by an attraction event will be prepared for its way back.
 * 
 * 
 * @author marcus
 * @author Lena
 */
public class VehicleReachedTargetHandler implements VehicleEventHandler {

	@Override
	public VehicleEventType getTargetEventType() {
		return VehicleEventType.VEHICLE_REACHED_TARGET;
	}

	@Override
	public void handleEvent(VehicleEvent event) {
		for (final Sensor sensor : event.getTrafficGraphExtensions().getSensors(event.getVehicle().getCurrentNode())) {
			if (sensor instanceof StaticTrafficSensor) {
				((StaticTrafficSensor) sensor).vehicleOnNodeRegistered(event.getVehicle());
			}
		}

		if (event.getVehicle().getData() instanceof BusData) {
			Node n = ((BusData) event.getVehicle().getData()).getBusStops().get(
					event.getVehicle().getCurrentNode().getId());
			// only at busstops the amount of passengers can change
			if (n != null) {
				Random random = new Random(event.getServiceDictionary().getRandomSeedService()
						.getSeed(DefaultTrafficServer.class.getName()));
				int max = ((BusData) event.getVehicle().getData()).getMaxPassengerCount();
				BusData temp = (BusData) event.getVehicle().getData();
				temp.setCurrentPassengerCount(random.nextInt(max));

				if (temp.getInfraredSensor() != null) {
					((InfraredSensor) temp.getInfraredSensor()).sendDataOnNextUpdate();
				}
			}
		}

		if (event.getEventForVehicleMap().containsKey(event.getVehicle().getId())) {
			TrafficEvent te = event.getEventForVehicleMap().get(event.getVehicle().getId());
			if (te instanceof AttractionTrafficEvent) {
				// logger.debug("Vehicle " + event.getVehicle().getName() + " reached attraction");
				AttractionTrafficEvent e = ((AttractionTrafficEvent) te);
				long startTime = e.getAttractionEndTimestamp();

				if (event.getSimulationTime() >= e.getAttractionEndTimestamp()) {
					startTime = event.getSimulationTime() + event.getServer().getUpdateIntervall();
				}

				/*
				 * Way back: Create way from the target node ID to a random node
				 */

				// use node as start node
				TrafficTrip trip = event.getServer().createTrip(event.getServer().getCityZone(), e.getNodeID(),
						startTime, true);

				if (trip != null) {
					modifyVehicleForWayBack(event, trip);
					// create trips for the other servers
					for (int i = 0; i < event.getServer().getServerListSize(); i++) {
						trip = event.getServer().createTrip(event.getServer().getCityZone(), e.getNodeID(), startTime,
								true);

						modifyVehicleForWayBack(event, trip);
					}
				}
			}
		}
	}

	private void modifyVehicleForWayBack(VehicleEvent event, TrafficTrip trip) {
		Path path = event.getServer().getShortestPath(event.getServer().getGraph().getNode(trip.getStartNode()),
				event.getServer().getGraph().getNode(trip.getTargetNode()));
		if (path.getNodeCount() > 1) {
			event.getVehicle().setPath(path);
			event.getVehicle().setState(State.NOT_STARTED);

			scheduleVehicle(event.getVehicle(), trip.getStartTime(), event);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private void scheduleVehicle(Vehicle vehicle, long startTime, VehicleEvent event) {
		if (vehicle != null) {
			// try {
			Item item = new Item(vehicle, startTime, event.getServer().getUpdateIntervall());
			event.getServer().getItemsToScheduleAfterAttractionReached().add(item);
			// event.getServer().getScheduler().scheduleItem(item);
			// } catch (IllegalAccessException e1) {
			// e1.printStackTrace();
			// }
		}
	}
}
