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

import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.graphstream.graph.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;
import de.pgalise.simulation.shared.event.traffic.CreateBussesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.traffic.BusTrip;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.scheduler.Item;
import de.pgalise.util.GTFS.service.BusService;
import de.pgalise.util.GTFS.service.DefaultBusService;

/**
 * Create busses
 * 
 * @author Lena
 * @author Andreas
 * @version 1.0
 */
public class CreateBussesEventHandler implements TrafficEventHandler {

	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateBussesEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final SimulationEventTypeEnum type = SimulationEventTypeEnum.CREATE_BUSSES_EVENT;

	/**
	 * Traffic server
	 */
	private TrafficServerLocal server;

	/**
	 * 
	 */
	private long lastUpdate;

	private Calendar cal;

	private Calendar tempCal;

	/**
	 * Default constructor
	 */
	public CreateBussesEventHandler() {
		cal = new GregorianCalendar();
		tempCal = new GregorianCalendar();
	}

	/**
	 * Creates busses with bus trips
	 * 
	 * @param busTrips
	 *            List with bus trips
	 */
	public void createBusses(List<BusTrip> busTrips) {
		int c = 1;
		for (int i = 0; i < busTrips.size(); i++) {
			BusTrip trip = busTrips.get(i);
			if (trip.getBusStops().size() > 0) {
				Path path = this.server.getBusRoute(trip.getBusStops());
				if (path.getNodeCount() > 1) {
					Vehicle<BusData> b = this.server.getBusFactory().createRandomBus(trip.getId(), trip.getGpsSensor(),
							trip.getInfraredSensor());
					b.getData().setBusStopOrder(trip.getBusStops());
					b.setName(trip.getRouteShortName() + " " + trip.getRouteLongName());
					b.setPath(path);
					b.getData().setBusStops(this.server.getBusStopNodes(trip.getBusStops()));

					// Correct the bus stop order
					for (Iterator<String> l = b.getData().getBusStopOrder().iterator(); l.hasNext();) {
						if (!b.getData().getBusStops().containsKey(l.next())) {
							l.remove();
						}
					}

					// Map mp = b.getData().getBusStops();
					// Iterator it = mp.entrySet().iterator();
					// while (it.hasNext()) {
					// Map.Entry pairs = (Map.Entry)it.next();
					// System.out.println(pairs.getKey() + " = " + pairs.getValue());
					// it.remove(); // avoids a ConcurrentModificationException
					// }

					Time arrivalTime = trip.getBusStopStopTimes().get(trip.getBusStops().get(0)).getArrivalTime();

					cal.setTimeInMillis(this.lastUpdate);

					tempCal.setTime(arrivalTime);

					cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
					cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
					cal.set(Calendar.SECOND, tempCal.get(Calendar.SECOND));
					cal.set(Calendar.MILLISECOND, 0);

					CreateBussesEventHandler.log.debug("Calculating route " + trip.getRouteShortName() + " of trip " + c + "; Starttime: "

							+ cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":"
							+ cal.get(Calendar.SECOND) + "." + cal.get(Calendar.MILLISECOND) + " "
							+ cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.MONTH) + "."
							+ cal.get(Calendar.YEAR));

					try {
						Item item = new Item(b, cal.getTimeInMillis(), this.server.getUpdateIntervall());
						// item.setLastUpdate(cal.getTimeInMillis() - this.server.getUpdateIntervall());
						this.server.getScheduler().scheduleItem(item);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					// try {
					// this.createGpsSensor(trip.getGpsSensor(), b);
					// b.setHasGPS(true);
					// } catch (SensorException e) {
					// e.printStackTrace();
					// }
				}
				c++;
			}
		}
	}

	@Override
	public SimulationEventTypeEnum getType() {
		return CreateBussesEventHandler.type;
	}

	@Override
	public void handleEvent(SimulationEvent event) {
		log.info("Processing CREATE_BUSSES_EVENT");
		CreateBussesEvent cbe = (CreateBussesEvent) event;
		this.lastUpdate = cbe.getTime();

		List<UUID> busIDList = new ArrayList<>();
		List<SensorHelper> gpsSensors = new ArrayList<>();
		List<SensorHelper> infraredSensors = new ArrayList<>();

		// Create busses
		for (CreateRandomVehicleData vehicleData : cbe.getCreateRandomVehicleDataList()) {
			busIDList.add(vehicleData.getVehicleInformation().getVehicleID());

			// Save all sensors
			for (SensorHelper sensorHelper : vehicleData.getSensorHelpers()) {
				/*
				 * YOU CAN ADD NEW SENSORS FOR BUSSES HERE
				 */
				switch (sensorHelper.getSensorType()) {
					case INFRARED: // Infrared sensors
						infraredSensors.add(sensorHelper);
						break;

					default: // GPS sensors
						gpsSensors.add(sensorHelper);
						break;
				}
			}
		}

		// Create all trips
		List<BusTrip> allTrips = new ArrayList<>();
		BusService bs = new DefaultBusService();
		try {
			for (String busRoute : cbe.getRouteIds()) {
				allTrips.addAll(bs.getBusLineData(busRoute, cbe.getTime()));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		// Combine busses, other sensors with trips
		List<BusTrip> trips = new ArrayList<>();
		for (int i = 0; i < allTrips.size(); i++) {
			BusTrip t = allTrips.get(i);
			t.setId(busIDList.get(i));
			t.setGpsSensor(gpsSensors.get(i));
			t.setInfraredSensor(infraredSensors.get(i));

			tempCal.setTime(t.getBusStopStopTimes().get(t.getBusStops().get(0)).getArrivalTime());

			cal.setTimeInMillis(cbe.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, tempCal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, 0);

			if (cal.getTimeInMillis() > cbe.getTime()) {
				trips.add(t);
			}
		}

		// Create busses
		this.createBusses(trips);
	}

	@Override
	public void init(TrafficServerLocal server) {
		this.server = server;
	}
}
