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
 
package de.pgalise.simulation.traffic.internal.server.sensor;

import de.pgalise.simulation.shared.city.Coordinate;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.sensor.AbstractStaticTrafficSensor;

/**
 * ...
 * 
 * @author Lena
 * @version 1.0
 */
public class DefaultSensorController extends AbstractController<TrafficEvent, InfrastructureStartParameter, TrafficInitParameter> implements TrafficSensorController<TrafficEvent> {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultSensorController.class);
	private static final String NAME = "SensorController";
	private static final long serialVersionUID = 1L;

	/**
	 * Sensor domain
	 */
	private final SensorRegistry sensorRegistry;

	/**
	 * TrafficServer
	 */
	private TrafficServerLocal<VehicleEvent> server;

	/**
	 * TrafficSensorFactory which can create different Traffic-Sensors
	 */
	private SensorFactory sensorFactory;

	/**
	 * {@link TrafficGraphExtensions}
	 */
	private TrafficGraphExtensions trafficGraphExtensions;

	/**
	 * GPS mapper
	 */
	private final Coordinate mapper;

	public DefaultSensorController(TrafficServerLocal<VehicleEvent> server, SensorRegistry sensorRegistry,
			SensorFactory sensorFactory, Coordinate mapper, TrafficGraphExtensions trafficGraphExtensions) {
		this.server = server;
		this.sensorRegistry = sensorRegistry;
		this.sensorFactory = sensorFactory;
		if (mapper == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("mapper"));
		}
		this.mapper = mapper;
		this.trafficGraphExtensions = trafficGraphExtensions;
	}

	@Override
	public void createSensor(Sensor<?,?> sensor) throws SensorException {
		final Sensor<?,?> newSensor = sensor;
		if (newSensor != null) {
			if (newSensor instanceof InductionLoopSensor) {
				newSensor.setActivated(true);
			}
			// else if(newSensor instanceof GpsSensor) {
			// return newSensor.getSensorId();
			// }
			// else if (newSensor instanceof InfraredSensor) {
			//
			// }
			
			// log.debug("Add sensor to Registry " + sensorRegistry.hashCode());
			sensorRegistry.addSensor(newSensor);
			if (newSensor instanceof AbstractStaticTrafficSensor) {
				trafficGraphExtensions.addSensor(null,
					(AbstractStaticTrafficSensor) newSensor);
				log.info("Sensor " + sensor+ " added to node " + sensor);
			}
		}
	}

	@Override
	public void deleteSensor(Sensor<?,?> sensor) throws SensorException {
		// Check sensor
		boolean isRemoved = false;
		Sensor<?,?> newSensor = sensorRegistry.getSensor(sensor);
		if (newSensor != null) {
			if (newSensor instanceof GpsSensor) {
				((GpsSensor) newSensor).setActivated(false);
			}
			sensorRegistry.removeSensor(newSensor);
			isRemoved = true;
		}

		if (!isRemoved) {
			throw new SensorException("Can't remove sensor: " + sensor);
		}
	}

	@Override
	public boolean statusOfSensor(Sensor<?,?> sensor) throws SensorException {
		boolean state = false;
		// Check sensor
		Sensor newSensor = sensorRegistry.getSensor(sensor);
		if (newSensor != null) {
			state = newSensor.isActivated();
		}
		return state;
	}

	@Override
	protected void onInit(TrafficInitParameter param) throws InitializationException {
		/* Nothing to do here: */
	}

	@Override
	protected void onReset() {
		// Remove all sensors
		sensorRegistry.removeAllSensors();
	}

	@Override
	protected void onStart(InfrastructureStartParameter param) {
		/* Nothing to do here: */
	}

	@Override
	protected void onStop() {
		// Deactivate sensors
		sensorRegistry.setSensorsActivated(false);
	}

	@Override
	protected void onResume() {
		// Activate sensors
		sensorRegistry.setSensorsActivated(true);
	}

	@Override
	protected void onUpdate(EventList<TrafficEvent> simulationEventList) {
		/* Nothing to do here: */
	}

	@Override
	public void onUpdate(Vehicle<?> vehicle, EventList<TrafficEvent> eventList) {
	}

	public void prepareUpdate(Vehicle<?> vehicle) {
		VehicleData data = vehicle.getData();

		// Prepare GPS sensor
		Sensor<?,?> sensor = data.getGpsSensor();
		if (sensor == null) {
			GpsSensor newSensor;
			newSensor = data.getGpsSensor();
			newSensor.setVehicle(vehicle);
			vehicle.getData().setGpsSensor(newSensor);
			sensorRegistry.addSensor(newSensor);
		}
		// log.debug("sending gps sensor data for vehicle " + vehicle.getId());
		// sensor.update(eventList); //auskommentiert lassen, sonst werden die Werte doppelt gesendet

		// Prepare infrared sensor (only for busses)
		if (data instanceof BusData) {
			BusData busdata = (BusData) data;
			if (busdata.getInfraredSensor() != null) {
				sensor = busdata.getInfraredSensor();
				if (sensor == null) {
					InfraredSensor newSensor;
					newSensor = busdata.getInfraredSensor();
					newSensor.setVehicle((Vehicle<BusData>) vehicle);
					((Vehicle<BusData>) vehicle).getData().setInfraredSensor(newSensor);
					log.debug("InfraredSensor " + newSensor.getId()+ " registered for vehicle "
						+ vehicle.getName());
					sensorRegistry.addSensor(newSensor);
				} else {
					// workaround (warum auch immer ist in der registry sonst ein falsches fahrzeug gespeichert)
					// evtl. sensorregistry beim start clearen?
					((InfraredSensor) sensor).setVehicle((Vehicle<BusData>) vehicle);
				}

				// log.debug("sending infrared sensor data for vehicle " + vehicle.getId());
				// sensor.update(eventList); //auskommentiert lassen, sonst werden die Werte doppelt gesendet
			}
		}
	}

	@Override
	public void onRemove(Vehicle<?> vehicle) {
		// Check sensor
		Sensor<?,?> newSensor = sensorRegistry.getSensor(vehicle.getData().getGpsSensor());
		if (newSensor != null) {
			vehicle.getData().getGpsSensor().setActivated(false);
			sensorRegistry.removeSensor(newSensor);
		}
	}

	@Override
	public void onSchedule(Vehicle<?> vehicle) {
		prepareUpdate(vehicle);
	}

	@Override
	public void createSensors(Collection<Sensor<?,?>> sensors) throws SensorException {
		for (Sensor<?,?> sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensors(Collection<Sensor<?,?>> sensors) throws SensorException {
		for (Sensor<?,?> sensor : sensors) {
			this.deleteSensor(sensor);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
