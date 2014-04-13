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
package de.pgalise.simulation.traffic.server.sensor.interferer.gps;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;

/**
 * Implementation of an {@link GpsInterferer} that hold several other
 * {@link GpsInterferer}s
 *
 * @author Marcus
 * @version 1.0
 */
/*
not an EJB because instance handling of stateful EJB is too complicated with 
no advantage over this POJO
*/
public class CompositeGpsInterferer implements GpsInterferer {
	private static final long serialVersionUID = 1L;
  private IdGenerator idGenerator;

	/**
	 * the composite {@link GpsInterferer}s
	 */
	private final List<GpsInterferer> interferers;

	/**
	 * Creates a {@link CompositeGpsInterferer} with no composite
	 * {@link GpsInterferer}s attached.
	 */
	public CompositeGpsInterferer(IdGenerator idGenerator) {
		this(new LinkedList<GpsInterferer>(), idGenerator);
	}

	/**
	 * Creates a {@link CompositeGpsInterferer} with the passed
	 * {@link GpsInterferer}s attached.
	 *
	 * @param interferers the {@link CompositeGpsInterferer}s to attach
	 * @throws IllegalArgumentException if argument 'interferers' is 'null'
	 */
	public CompositeGpsInterferer(List<GpsInterferer> interferers, IdGenerator idGenerator) throws IllegalArgumentException {
		if (interferers == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
				"interferers"));
		}
		this.interferers = new LinkedList<>(interferers);
    this.idGenerator = idGenerator;
	}

	/**
	 * Attaches an {@link GpsInterferer} to this {@link CompositeGpsInterferer}
	 *
	 * @param interferer the {@link GpsInterferer} to be attached to this
	 * {@link CompositeGpsInterferer}
	 * @return 'true' whether the passed {@link GpsInterferer} could have been
	 * attached to this {@link CompositeGpsInterferer}, otherwise 'false'
	 * @throws IllegalArgumentException if <tt>interferer</tt> is a
	 * <tt>CompositeGpsInterferer</tt>
	 */
	public boolean attach(final GpsInterferer interferer) throws IllegalArgumentException {
		if (interferer instanceof CompositeGpsInterferer) {
			throw new IllegalArgumentException(
				"Argument 'interferer' may not be an instance of CompositeGpsInterferer");
		}
		return this.interferers.add(interferer);
	}

	/**
	 * Detaches the passed {@link GpsInterferer} from this
	 * {@link CompositeGpsInterferer}
	 *
	 * @param interferer the {@link GpsInterferer} to be detached from this
	 * {@link CompositeGpsInterferer}
	 * @return true if the {@link GpsInterferer} is detached
	 */
	public boolean detach(final GpsInterferer interferer) {
		return this.interferers.remove(interferer);
	}

	@Override
	public BaseCoordinate interfere(final BaseCoordinate mutablePosition,
		final BaseCoordinate realPosition,
		final long simTime) {
		BaseCoordinate result = new BaseCoordinate(idGenerator.getNextId(),mutablePosition.getX(),
			mutablePosition.getY());
		for (final GpsInterferer interferer : this.interferers) {
			result = interferer.interfere(result,
				realPosition,
				simTime);
		}
		// Returns the last value
		return result;
	}
}
