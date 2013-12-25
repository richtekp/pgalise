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
 
package de.pgalise.simulation.traffic.internal.server.eventhandler;

import de.pgalise.simulation.shared.city.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;

/**
 * The event handler create two vehicles with the same ID and properties. The class are used by the
 * {@link CreateVehiclesEvent}. The first vehicle drives from the given start node to the give target node. The start
 * timestamp are used here. The second vehicle drives from the given target node to the give start node. For this trip,
 * the end timestamp are used.
 * 
 * @param <D> 
 * @author Andreas
 * @version 1.0
 */
public class CreateVehicleEventHandler<D extends VehicleData> extends AbstractVehicleEventHandler<D,CreateVehiclesEvent<D>> {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateVehicleEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final EventType type = TrafficEventTypeEnum.CREATE_VEHICLES_EVENT;

	/**
	 * Constructor
	 */
	public CreateVehicleEventHandler() {

	}

	@Override
	public EventType getTargetEventType() {
		return CreateVehicleEventHandler.type;
	}
	
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	@Override
	public void handleEvent(CreateVehiclesEvent<D> event) {
		if(!event.getResponsibleServer().equals(this.getResponsibleServer())) {
			return;
		}
		else {
			log.info("Processing CREATE_VEHICLES_EVENT: Vehicles=" + event.getVehicles().size());
		}
					

		int i=0;
		for (CreateRandomVehicleData data : event.getVehicles()) {
			TrafficTrip trip = data.getVehicleInformation().getTrip();
			
			if((trip.getStartNode()==null ) &&
					(trip.getTargetNode()==null )) {
//				log.debug("No trip was specified. Creating random route...");
				trip = getResponsibleServer().createTrip(getResponsibleServer().getCityZone(), 
						data.getVehicleInformation().getVehicleType());
				trip.setStartTime(
						data.getVehicleInformation()
						.getTrip().getStartTime());
			}
			else if((trip.getStartNode()==null ) &&
					!(trip.getTargetNode()==null )) {
//				log.debug("Just the target node was specified. Generating random start node...");
				trip = getResponsibleServer().createTrip(getResponsibleServer().getCityZone(), 
						trip.getTargetNode(),
						trip.getStartTime(), false);
			}
			else if(!(trip.getStartNode()==null || trip.getStartNode().equals("")) &&
					(trip.getTargetNode()==null || trip.getTargetNode().equals(""))){
//				log.debug("Just the start node was specified. Generating random target node...");
				trip = getResponsibleServer().createTrip(getResponsibleServer().getCityZone(), 
						trip.getStartNode(),
						trip.getStartTime(), true);
			}
			data.getVehicleInformation().setTrip(trip);			
			
			if (this.getResponsibleServer().getCityZone().covers(
					GEOMETRY_FACTORY.createPoint(
						trip.getStartNode().getGeoLocation()))) {
				// Create vehicle
				Vehicle<?> v = this.createVehicle(data, trip);
	
				// Schedule vehicle
				this.scheduleVehicle(v, trip.getStartTime());
			}
			
			i++;
			if(i%1000==0) {
				log.info(i+" vehicles created");
			}
		}
		log.info("Processing CREATE_VEHICLES_EVENT complete");
	}
}
