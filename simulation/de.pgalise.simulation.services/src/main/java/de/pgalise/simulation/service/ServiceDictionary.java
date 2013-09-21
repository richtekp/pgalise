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
 
package de.pgalise.simulation.service;

import java.util.Collection;

import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.shared.controller.ServerConfiguration;

/**
 * A dictionary to retrieve as singleton implemented services may reside on different servers. None of the services
 * listed here are supposed to be referenced directly! Instead a component that depends on one of these services has to
 * use this dictionary to get a reference.
 * 
 * @param <S> 
 * @author mustafa
 */
public interface ServiceDictionary {
	public static final String FRONT_CONTROLLER = "de.pgalise.simulation.FrontController";
	public static final String TRAFFIC_SERVER = "de.pgalise.simulation.traffic.server.TrafficServer";
	public static final String TRAFFIC_CONTROLLER = "de.pgalise.simulation.traffic.TrafficController";
	public static final String WEATHER_CONTROLLER = "de.pgalise.simulation.weather.service.WeatherController";
	public static final String ENERGY_CONTROLLER = "de.pgalise.simulation.energy.EnergyController";
	public static final String STATIC_SENSOR_CONTROLLER = "de.pgalise.simulation.staticsensor.StaticSensorController";
	public static final String SIMULATION_CONTROLLER = "de.pgalise.simulation.SimulationController";

	public static final String RANDOM_SEED_SERVICE = RandomSeedService.class.getName();
	public static final String CONFIG_READER_SERVICE = ConfigReader.class.getName();

	/**
	 * Initializes this ServiceFactory. Used to determine which service lies on which server.
	 * 
	 * @param serverConfig 
	 */
	public void init(ServerConfiguration serverConfig);

	/**
	 * @return list of all simulation controllers
	 */
	public Collection<Service> getControllers();

	/**
	 * @param <C> 
	 * @param clazz
	 *            class name of the desired controller
	 * @return a specific controller identified by its class name if available otherwise null.
	 */
	public <C extends Service> C getController(Class<? extends C> clazz);

	/**
	 * @return the RandomSeedService
	 */
	public RandomSeedService getRandomSeedService();

	/**
	 * @return the ConfigReader
	 */
	public ConfigReader getGlobalConfigReader();
}
