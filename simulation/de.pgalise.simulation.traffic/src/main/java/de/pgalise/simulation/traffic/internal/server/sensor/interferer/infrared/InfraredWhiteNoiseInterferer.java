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

import de.pgalise.simulation.service.RandomSeedService;

/**
 * Represents an interferer that creates generally low errors
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0 (Nov 17, 2012)
 */
public class InfraredWhiteNoiseInterferer extends InfraredBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_infrared_whitenoise.properties";
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 */
	public InfraredWhiteNoiseInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice, InfraredWhiteNoiseInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public int interfere(final int passengersCount) {
		int result = 1;

		// To many passengers
		if ((this.getRandom().nextDouble() <= this.changeProbability)
				&& (passengersCount > this.changeMaxPassengersCount)) {
			result--;
		}

		// Returns
		return (result < 0) ? 0 : result;
	}

}
