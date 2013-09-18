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

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventType;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;

/**
 * If a vehicle passes a node a VEHICLE_PASSED_NODE event is thrown. This handler handles 
 * the event and notifies all sensors which are registered on this node. Furthermore (if 
 * the vehicle is a bus) the value of the infrared sensor changes if the node is a busstop.
 * 
 * @author Marcus
 * @author Andreas Rehfeldt
 * @author Lena
 */
public class VehiclePassedNodeHandler implements VehicleEventHandler {
	@Override
	public VehicleEventType getTargetEventType() {
		return VehicleEventType.VEHICLE_PASSED_NODE;
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
				int lastBusStop = ((BusData) event.getVehicle().getData()).getBusStopOrder().indexOf(n.getId());
				((BusData) event.getVehicle().getData()).setLastBusStop(lastBusStop);
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
	}
}
