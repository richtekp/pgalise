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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared;

import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;

/**
 * Implementation of an {@link InfraredInterferer} that hold several other {@link InfraredInterferer}s
 * 
 * @author Marcus
 * @version 1.0
 */
public final class CompositeInfraredInterferer implements InfraredInterferer {

	/**
	 * the composite {@link InfraredInterferer}s
	 */
	private final List<InfraredInterferer> interferers;

	/**
	 * Creates a {@link CompositeInfraredInterferer} with no composite {@link InfraredInterferer}s attached.
	 */
	public CompositeInfraredInterferer() {
		this(new LinkedList<InfraredInterferer>());
	}

	/**
	 * Creates a {@link CompositeInfraredInterferer} with the passed {@link InfraredInterferer}s attached.
	 * 
	 * @param interferers
	 *            the {@link CompositeInfraredInterferer}s to attach
	 * @throws IllegalArgumentException
	 *             if argument 'interferers' is 'null'
	 */
	public CompositeInfraredInterferer(List<InfraredInterferer> interferers) throws IllegalArgumentException {
		if (interferers == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferers"));
		}
		this.interferers = new LinkedList<>(interferers);
	}

	/**
	 * Attaches an {@link InfraredInterferer} to this {@link CompositeInfraredInterferer}
	 * 
	 * @param interferer
	 *            the {@link InfraredInterferer} to be attached to this {@link CompositeInfraredInterferer}
	 * @return 'true' whether the passed {@link InfraredInterferer} could have been attached to this
	 *         {@link CompositeInfraredInterferer}, otherwise 'false'
	 * @throws UnsupportedOperationException
	 */
	public boolean attach(final InfraredInterferer interferer) throws UnsupportedOperationException {
		if (interferer instanceof CompositeInfraredInterferer) {
			throw new UnsupportedOperationException(
					"Argument 'interferer' may not be an instance of CompositeTrafficCountInterferer");
		}
		return this.interferers.add(interferer);
	}

	/**
	 * Detaches the passed {@link InfraredInterferer} from this {@link CompositeInfraredInterferer}
	 * 
	 * @param interferer
	 *            the {@link InfraredInterferer} to be detached from this {@link CompositeInfraredInterferer}
	 * @return true if the {@link InfraredInterferer} is detached
	 */
	public boolean detach(final InfraredInterferer interferer) {
		return this.interferers.remove(interferer);
	}

	@Override
	public int interfere(final int passengersCount) {
		int result = passengersCount;
		for (final InfraredInterferer interferer : this.interferers) {
			result += interferer.interfere(passengersCount);
		}
		// Returns the last value
		return result;
	}
}
