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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar;

import de.pgalise.simulation.service.RandomSeedService;

/**
 * Represents an interferer that creates generally low errors
 * 
 * @author Marcus
 * @version 1.0 (Nov 17, 2012)
 */
public class TopoRadarWhiteNoiseInterferer extends TopoRadarBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_toporadar_whitenoise.properties";

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 */
	public TopoRadarWhiteNoiseInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice, TopoRadarWhiteNoiseInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public int[] interfere(final int mutableAxleCount, final int mutableLength, final int mutableWheelbase1,
			final int mutableWheelbase2, final long simTime) {
		int[] result = new int[] { mutableAxleCount, mutableLength, mutableWheelbase1, mutableWheelbase2 };
		// Change Length
		if (this.getRandom().nextDouble() <= this.changeProbability) {
			int changeLength = (int) (this.changeLengthAmplitude * this.getRandom().nextGaussian());

			result[1] = (this.getRandom().nextBoolean()) ? mutableAxleCount + changeLength : mutableAxleCount
					- changeLength;
		}

		// Change Wheelbase 1
		if (this.getRandom().nextDouble() <= this.changeProbability) {
			int changeWheelbase = (int) (this.changeWheelbaseAmplitude * this.getRandom().nextGaussian());
			result[2] = (this.getRandom().nextBoolean()) ? mutableWheelbase1 + changeWheelbase : mutableWheelbase1
					- changeWheelbase;
		}

		// New car?
		if ((this.getRandom().nextDouble() <= this.changeProbability) && (mutableAxleCount > 2)) {
			result[0] = 2;
			result[2] = 0;
		} else if ((this.getRandom().nextDouble() <= this.changeProbability) && (mutableAxleCount > 2)) {
			int changeWheelbase = (int) (this.changeWheelbaseAmplitude * this.getRandom().nextGaussian());
			result[2] = (this.getRandom().nextBoolean()) ? mutableWheelbase2 + changeWheelbase : mutableWheelbase2
					- changeWheelbase;
		}

		// Returns
		return result;
	}

}