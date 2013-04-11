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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps;

import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.util.vector.Vector2d;

/**
 * Implementation of an {@link GpsInterferer} that hold several other {@link GpsInterferer}s
 * 
 * @author Marcus
 * @version 1.0
 */
public final class CompositeGpsInterferer implements GpsInterferer {

	/**
	 * the composite {@link GpsInterferer}s
	 */
	private final List<GpsInterferer> interferers;

	/**
	 * Creates a {@link CompositeGpsInterferer} with no composite {@link GpsInterferer}s attached.
	 */
	public CompositeGpsInterferer() {
		this(new LinkedList<GpsInterferer>());
	}

	/**
	 * Creates a {@link CompositeGpsInterferer} with the passed {@link GpsInterferer}s attached.
	 * 
	 * @param interferers
	 *            the {@link CompositeGpsInterferer}s to attach
	 * @throws IllegalArgumentException
	 *             if argument 'interferers' is 'null'
	 */
	public CompositeGpsInterferer(List<GpsInterferer> interferers) throws IllegalArgumentException {
		if (interferers == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("interferers"));
		}
		this.interferers = new LinkedList<>(interferers);
	}

	/**
	 * Attaches an {@link GpsInterferer} to this {@link CompositeGpsInterferer}
	 * 
	 * @param interferer
	 *            the {@link GpsInterferer} to be attached to this {@link CompositeGpsInterferer}
	 * @return 'true' whether the passed {@link GpsInterferer} could have been attached to this
	 *         {@link CompositeGpsInterferer}, otherwise 'false'
	 * @throws UnsupportedOperationException
	 */
	public boolean attach(final GpsInterferer interferer) throws UnsupportedOperationException {
		if (interferer instanceof CompositeGpsInterferer) {
			throw new UnsupportedOperationException(
					"Argument 'interferer' may not be an instance of CompositeGpsInterferer");
		}
		return this.interferers.add(interferer);
	}

	/**
	 * Detaches the passed {@link GpsInterferer} from this {@link CompositeGpsInterferer}
	 * 
	 * @param interferer
	 *            the {@link GpsInterferer} to be detached from this {@link CompositeGpsInterferer}
	 * @return true if the {@link GpsInterferer} is detached
	 */
	public boolean detach(final GpsInterferer interferer) {
		return this.interferers.remove(interferer);
	}

	@Override
	public Vector2d interfere(final Vector2d mutablePosition, final Vector2d realPosition, final long simTime,
			final double vectorUnit) {
		Vector2d result = Vector2d.valueOf(mutablePosition.getX(), mutablePosition.getY());
		for (final GpsInterferer interferer : this.interferers) {
			result = interferer.interfere(result, realPosition, simTime, vectorUnit);
		}
		// Returns the last value
		return result;
	}
}
