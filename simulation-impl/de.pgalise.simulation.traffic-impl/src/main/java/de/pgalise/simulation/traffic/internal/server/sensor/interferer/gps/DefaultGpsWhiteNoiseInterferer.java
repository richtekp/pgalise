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

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsWhiteNoiseInterferer;
import javax.ejb.EJB;

/**
 * Represents an interferer that creates generally low errors
 *
 * @author Marcus
 * @version 1.0 (Nov 17, 2012)
 */
public class DefaultGpsWhiteNoiseInterferer extends GpsBaseInterferer implements GpsWhiteNoiseInterferer {

	private static final long serialVersionUID = 1L;
  @EJB
  private IdGenerator idGenerator;
	
	public DefaultGpsWhiteNoiseInterferer(RandomSeedService randomseedservice) {
		this(randomseedservice,
			1.0);
	}

	/**
	 * Constructor
	 *
	 * @param randomseedservice Random Seed Service
	 * @param changeAmplitude
	 */
	public DefaultGpsWhiteNoiseInterferer(RandomSeedService randomseedservice,
		double changeAmplitude) {
		super(randomseedservice,
			changeAmplitude,
			1.0);
	}

	@Override
	public BaseCoordinate interfere(final BaseCoordinate mutablePosition,
		final BaseCoordinate realPosition,
		final long simTime) {
		// Should be changed?
		if (this.getRandom().nextDouble() <= this.getChangeProbability()) {
			final double x = 1d / ((1d / ((this.getRandom().nextDouble() * this.
				getChangeAmplitude()) + Double.MIN_NORMAL)));
			final double y = 1d / ((1d / ((this.getRandom().nextDouble() * this.
				getChangeAmplitude()) + Double.MIN_NORMAL)));
			return new BaseCoordinate(
				this.getRandom().nextBoolean() ? mutablePosition.getX() + x : mutablePosition.
				getX() - x,
				this
				.getRandom().nextBoolean() ? mutablePosition.getY() + y : mutablePosition.
				getY() - y);
		}
		return mutablePosition;
	}
}
