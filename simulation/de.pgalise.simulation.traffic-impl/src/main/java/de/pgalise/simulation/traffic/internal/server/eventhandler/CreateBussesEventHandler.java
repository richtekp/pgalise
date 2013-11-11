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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.traffic.BusRoute;
import de.pgalise.simulation.traffic.BusStop;
import de.pgalise.simulation.traffic.BusTrip;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.traffic.BusTrip;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.internal.model.vehicle.RandomBusFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.util.GTFS.service.BusService;
import de.pgalise.util.GTFS.service.DefaultBusService;
import java.util.Date;

/**
 * Create busses
 * 
 * @author Lena
 * @author Andreas
 * @version 1.0
 */
public class CreateBussesEventHandler extends AbstractTrafficEventHandler<BusData,CreateBussesEvent<BusData>> {

	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(CreateBussesEventHandler.class);

	/**
	 * Simulation event type
	 */
	private static final EventType type = TrafficEventTypeEnum.CREATE_BUSSES_EVENT;

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
				List<TrafficEdge> path = this.server.getBusRoute(trip.getBusStops());
				if (path != null) {
					Vehicle<BusData> b = this.server.getBusFactory().createRandomBus(trip.getGpsSensor(),
							trip.getInfraredSensor());
					b.getData().setBusStopOrder(trip.getBusStops());
					b.setName(trip.getRouteShortName() + " " + trip.getRouteLongName());
					b.setPath(path);
					b.getData().setBusStops(this.server.getBusStopNodes(trip.getBusStops()));

					// Correct the bus stop order
					for (Iterator<BusStop> l = b.getData().getBusStopOrder().iterator(); l.hasNext();) {
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

					Date arrivalTime = trip.getBusStopStopTimes().get(trip.getBusStops().get(0)).getArrivalTime();

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

					ScheduleItem item = new DefaultScheduleItem(b, cal.getTimeInMillis(), this.server.getUpdateIntervall());
					// item.setLastUpdate(cal.getTimeInMillis() - this.server.getUpdateIntervall());
					this.server.getScheduler().scheduleItem(item);
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
	public EventType getTargetEventType() {
		return CreateBussesEventHandler.type;
	}

	@Override
	public void handleEvent(CreateBussesEvent<BusData> cbe) {
		log.info("Processing CREATE_BUSSES_EVENT");
		this.lastUpdate = cbe.getSimulationTime();

		List<Vehicle<BusData>> buses = new ArrayList<>();
		List<SensorHelper> gpsSensors = new ArrayList<>();
		List<SensorHelper> infraredSensors = new ArrayList<>();

		// Create busses
		for (CreateRandomVehicleData vehicleData : cbe.getCreateRandomVehicleDataList()) {
			SensorHelper infraredSensor = null, gpsSensor = null;
			for(SensorHelper sensorHelper : vehicleData.getSensorHelpers()) {
				if(sensorHelper.getSensorType().equals(SensorTypeEnum.GPS_BUS)) {
					gpsSensor= sensorHelper;
				}else if(sensorHelper.getSensorType().equals(SensorTypeEnum.INFRARED)) {
					infraredSensor = sensorHelper;
				}
			}
			Vehicle<BusData> bus = new RandomBusFactory().createRandomBus(gpsSensor,
				infraredSensor);
			buses.add(bus);

			// Save all sensors
			for (SensorHelper sensorHelper : vehicleData.getSensorHelpers()) {
				/*
				 * YOU CAN ADD NEW SENSORS FOR BUSSES HERE
				 */
				if (sensorHelper.getSensorType() == SensorTypeEnum.INFRARED) {
					infraredSensors.add(sensorHelper);
					break;
				}else {
					gpsSensors.add(sensorHelper);
					break;
				}
			}
		}

		// Create all trips
		List<BusTrip> allTrips = new ArrayList<>();
		BusService bs = new DefaultBusService();
		for (BusRoute busRoute : cbe.getBusRoutes()) {
				allTrips.addAll(bs.getBusLineData(busRoute, cbe.getSimulationTime()));
		}

		// Combine busses, other sensors with trips
		List<BusTrip> trips = new ArrayList<>();
		for (int i = 0; i < allTrips.size(); i++) {
			BusTrip t = allTrips.get(i);
			t.setBus(buses.get(i));
			t.setGpsSensor(gpsSensors.get(i));
			t.setInfraredSensor(infraredSensors.get(i));

			tempCal.setTime(t.getBusStopStopTimes().get(t.getBusStops().get(0)).getArrivalTime());

			cal.setTimeInMillis(cbe.getSimulationTime());
			cal.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, tempCal.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, 0);

			if (cal.getTimeInMillis() > cbe.getSimulationTime()) {
				trips.add(t);
			}
		}

		// Create busses
		this.createBusses(trips);
	}

	@Override
	public void init(TrafficServerLocal<CreateBussesEvent<BusData>> server) {
		this.server = server;
	}
}
