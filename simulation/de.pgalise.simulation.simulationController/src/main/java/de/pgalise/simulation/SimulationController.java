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
package de.pgalise.simulation;

import de.pgalise.simulation.energy.EnergySensorController;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorManagerController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorController;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.TrafficServer;

/**
 * The simulation controller inits, starts, stops, resets and updates the whole
 * simulation. It's the only touch point for the control center.
 *
 * @author Kamil
 * @auther Jens
 * @auther Timo
 */
public interface SimulationController extends
	SensorManagerController<Event, TrafficStartParameter, TrafficInitParameter, Sensor<?, ?>> {

	/**
	 * Adds an event list. The other controllers will be updated with the given
	 * events.
	 *
	 * @param simulationEventList an event list with a timestamp and events
	 */
	public void addSimulationEventList(EventList<?> simulationEventList);

	/**
	 * Returns the current simulation timestamp.
	 *
	 * @TODO: check usage (timestamps should be determined by System simply)
	 *
	 * @return
	 */
	public long getSimulationTimestamp();

	public long getElapsedTime();

	/**
	 * Use this only for testing.
	 *
	 * @return
	 */
	public EventInitiator getEventInitiator();
	
	public WeatherSensorController getWeatherSensorController();
	
	public EnergySensorController getEnergySensorController();
	
	public TrafficSensorController getTrafficSensorController();
}
