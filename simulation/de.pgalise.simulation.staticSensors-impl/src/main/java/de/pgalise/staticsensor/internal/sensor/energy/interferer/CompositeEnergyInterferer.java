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
 
package de.pgalise.staticsensor.internal.sensor.energy.interferer;

import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergyInterferer;
import de.pgalise.util.vector.Vector2d;

/**
 * Implementation of an {@link EnergyInterferer} that hold several other {@link EnergyInterferer}s
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public final class CompositeEnergyInterferer implements EnergyInterferer {

	/**
	 * the composite {@link EnergyInterferer}s
	 */
	private final List<EnergyInterferer> interferers;

	/**
	 * Creates a {@link CompositeEnergyInterferer} with no composite {@link EnergyInterferer}s attached.
	 */
	public CompositeEnergyInterferer() {
		this(new LinkedList<EnergyInterferer>());
	}

	/**
	 * Creates a {@link CompositeEnergyInterferer} with the passed {@link EnergyInterferer}s attached.
	 * 
	 * @param interferers
	 *            the {@link CompositeEnergyInterferer}s to attach
	 * @throws IllegalArgumentException
	 *             if argument 'interferers' is 'null'
	 */
	public CompositeEnergyInterferer(List<EnergyInterferer> interferers) throws IllegalArgumentException {
		if (interferers == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferers"));
		}
		this.interferers = new LinkedList<>(interferers);
	}

	/**
	 * Attaches an {@link EnergyInterferer} to this {@link CompositeEnergyInterferer}
	 * 
	 * @param interferer
	 *            the {@link EnergyInterferer} to be attached to this {@link CompositeEnergyInterferer}
	 * @return 'true' whether the passed {@link EnergyInterferer} could have been attached to this
	 *         {@link CompositeEnergyInterferer}, otherwise 'false'
	 * @throws UnsupportedOperationException
	 */
	public boolean attach(final EnergyInterferer interferer) throws UnsupportedOperationException {
		if (interferer instanceof CompositeEnergyInterferer) {
			throw new UnsupportedOperationException(
					"Argument 'interferer' may not be an instance of CompositeGpsInterferer");
		}
		return this.interferers.add(interferer);
	}

	/**
	 * Detaches the passed {@link EnergyInterferer} from this {@link CompositeEnergyInterferer}
	 * 
	 * @param interferer
	 *            the {@link EnergyInterferer} to be detached from this {@link CompositeEnergyInterferer}
	 * @return true if the {@link EnergyInterferer} is detached
	 */
	public boolean detach(final EnergyInterferer interferer) {
		return this.interferers.remove(interferer);
	}

	@Override
	public double interfere(final double mutableValue, final Vector2d position, final long simTime) {
		double result = mutableValue;
		for (final EnergyInterferer interferer : this.interferers) {
			result = interferer.interfere(result, position, simTime);
		}
		// Returns the last value
		return result;
	}
}
