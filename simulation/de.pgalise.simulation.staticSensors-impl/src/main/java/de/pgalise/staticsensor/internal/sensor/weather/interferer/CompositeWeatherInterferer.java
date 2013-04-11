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
 
package de.pgalise.staticsensor.internal.sensor.weather.interferer;

import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.util.vector.Vector2d;

/**
 * Implementation of an {@link WeatherInterferer} that hold several other {@link WeatherInterferer}s
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public final class CompositeWeatherInterferer implements WeatherInterferer {

	/**
	 * the composite {@link WeatherInterferer}s
	 */
	private final List<WeatherInterferer> interferers;

	/**
	 * Creates a {@link CompositeWeatherInterferer} with no composite {@link WeatherInterferer}s attached.
	 */
	public CompositeWeatherInterferer() {
		this(new LinkedList<WeatherInterferer>());
	}

	/**
	 * Creates a {@link CompositeWeatherInterferer} with the passed {@link WeatherInterferer}s attached.
	 * 
	 * @param interferers
	 *            the {@link CompositeWeatherInterferer}s to attach
	 * @throws IllegalArgumentException
	 *             if argument 'interferers' is 'null'
	 */
	public CompositeWeatherInterferer(List<WeatherInterferer> interferers) throws IllegalArgumentException {
		if (interferers == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferers"));
		}
		this.interferers = new LinkedList<>(interferers);
	}

	/**
	 * Attaches an {@link WeatherInterferer} to this {@link CompositeWeatherInterferer}
	 * 
	 * @param interferer
	 *            the {@link WeatherInterferer} to be attached to this {@link CompositeWeatherInterferer}
	 * @return 'true' whether the passed {@link WeatherInterferer} could have been attached to this
	 *         {@link CompositeWeatherInterferer}, otherwise 'false'
	 * @throws UnsupportedOperationException
	 */
	public boolean attach(final WeatherInterferer interferer) throws UnsupportedOperationException {
		if (interferer instanceof CompositeWeatherInterferer) {
			throw new UnsupportedOperationException(
					"Argument 'interferer' may not be an instance of CompositeGpsInterferer");
		}
		return this.interferers.add(interferer);
	}

	/**
	 * Detaches the passed {@link WeatherInterferer} from this {@link CompositeWeatherInterferer}
	 * 
	 * @param interferer
	 *            the {@link WeatherInterferer} to be detached from this {@link CompositeWeatherInterferer}
	 * @return true if the {@link WeatherInterferer} is detached
	 */
	public boolean detach(final WeatherInterferer interferer) {
		return this.interferers.remove(interferer);
	}

	@Override
	public double interfere(final double mutableValue, final Vector2d position, final long simTime) {
		double result = mutableValue;
		for (final WeatherInterferer interferer : this.interferers) {
			result = interferer.interfere(result, position, simTime);
		}
		// Returns the last value
		return result;
	}
}
