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
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsReceiverInterferer;
import javax.ejb.EJB;

/**
 * Represents an interferer that shows errors caused by corrupted antenna
 * calibration
 *
 * @author Andreas
 * @version 1.0 (Nov 12, 2012)
 */
public class DefaultGpsReceiverInterferer extends GpsBaseInterferer implements GpsReceiverInterferer {


	// @TODO GPSMapper als abhängigkeit hinzufügen um die VECTOR_UNIT zu bestimmen
	private static final double VECTOR_UNIT = 100.0;
	private static final long serialVersionUID = 1L;
  @EJB
  private IdGenerator idGenerator;

	/**
	 * Constructor
	 *
	 * @param randomseedservice Random Seed Service
	 */
	public DefaultGpsReceiverInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice,
			DefaultGpsReceiverInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public BaseCoordinate interfere(BaseCoordinate mutablePosition,
		BaseCoordinate realPosition,
		long simTime) {
		// Should be changed?
		if (this.getRandom().nextDouble() <= this.getChangeProbability()) {
			double changeValue = (this.getChangeAmplitude() / VECTOR_UNIT) * this.
				getRandom().nextGaussian();
			return new BaseCoordinate(idGenerator.getNextId(), mutablePosition.getX() + changeValue,
				mutablePosition.getY() + changeValue);
		}

		// Returns with no change
		return mutablePosition;
	}
}
