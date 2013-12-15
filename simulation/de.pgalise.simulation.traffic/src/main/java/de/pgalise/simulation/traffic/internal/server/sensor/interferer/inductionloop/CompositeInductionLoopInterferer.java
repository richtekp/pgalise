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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop;

import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;

/**
 * Implementation of an {@link InductionLoopInterferer} that hold several other {@link InductionLoopInterferer}s
 * 
 * @author Marcus
 * @version 1.0
 */
public class CompositeInductionLoopInterferer implements InductionLoopInterferer {

	/**
	 * the composite {@link InductionLoopInterferer}s
	 */
	private final List<InductionLoopInterferer> interferers;

	/**
	 * Creates a {@link CompositeInductionLoopInterferer} with no composite {@link InductionLoopInterferer}s attached.
	 */
	public CompositeInductionLoopInterferer() {
		this(new LinkedList<InductionLoopInterferer>());
	}

	/**
	 * Creates a {@link CompositeInductionLoopInterferer} with the passed {@link InductionLoopInterferer}s attached.
	 * 
	 * @param interferers
	 *            the {@link CompositeInductionLoopInterferer}s to attach
	 * @throws IllegalArgumentException
	 *             if argument 'interferers' is 'null'
	 */
	public CompositeInductionLoopInterferer(List<InductionLoopInterferer> interferers) throws IllegalArgumentException {
		if (interferers == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferers"));
		}
		this.interferers = new LinkedList<>(interferers);
	}

	/**
	 * Attaches an {@link InductionLoopInterferer} to this {@link CompositeInductionLoopInterferer}
	 * 
	 * @param interferer
	 *            the {@link InductionLoopInterferer} to be attached to this {@link CompositeInductionLoopInterferer}
	 * @return 'true' whether the passed {@link InductionLoopInterferer} could have been attached to this
	 *         {@link CompositeInductionLoopInterferer}, otherwise 'false'
	 * @throws UnsupportedOperationException
	 */
	public boolean attach(final InductionLoopInterferer interferer) throws UnsupportedOperationException {
		if (interferer instanceof CompositeInductionLoopInterferer) {
			throw new UnsupportedOperationException(
					"Argument 'interferer' may not be an instance of CompositeTrafficCountInterferer");
		}
		return this.interferers.add(interferer);
	}

	/**
	 * Detaches the passed {@link InductionLoopInterferer} from this {@link CompositeInductionLoopInterferer}
	 * 
	 * @param interferer
	 *            the {@link InductionLoopInterferer} to be detached from this {@link CompositeInductionLoopInterferer}
	 * @return true if the {@link InductionLoopInterferer} is detached
	 */
	public boolean detach(final InductionLoopInterferer interferer) {
		return this.interferers.remove(interferer);
	}

	@Override
	public int interfere(final int vehicleLength, final int vehicleCount, final double vehicleVelocity) {
		int result = 0;
		for (final InductionLoopInterferer interferer : this.interferers) {
			result += interferer.interfere(vehicleLength, vehicleCount, vehicleVelocity);
		}
		// Returns the last value
		return result;
	}
}
