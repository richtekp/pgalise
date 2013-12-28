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
 
package de.pgalise.simulation.weather.modifier;

import java.util.Properties;

import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.WeatherCondition;

/**
 * The {@link WeatherMap} serves as the root for the weather modifiers that are all derived from the class
 * {@link WeatherMapModifier}. For that reason the decorator pattern is realized. Furthermore, the decorators can also
 * function as a {@link WeatherStrategy} because the strategy pattern is implemented. The different weather modifiers
 * are separated into two groups: Some are derived from the class {@link WeatherDayEventModifier} and others expand the
 * class {@link WeatherSimulationEventModifier}. The simulation event modifiers, which expand the
 * {@link WeatherSimulationEventModifier}, can be activated only once and are always active for the complete simulation
 * runtime.
 * 
 * @param <C> 
 * @author Andreas Rehfeldt
 * @version 1.0 (08.10.2012)
 */
public abstract class WeatherSimulationEventModifier extends AbstractWeatherMapModifier {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 2214588605839775963L;

	/**
	 * Constructor
	 * 
	 * @param props
	 *            Properties
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherSimulationEventModifier(long seed, Properties props, WeatherLoader weatherLoader) {
		super(seed, props, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherSimulationEventModifier(long seed, WeatherLoader weatherLoader) {
		super(seed, weatherLoader);
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 *            WeatherMap
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherSimulationEventModifier(WeatherMap map, long seed, WeatherLoader weatherLoader) {
		super(map, seed, weatherLoader);
	}

}
