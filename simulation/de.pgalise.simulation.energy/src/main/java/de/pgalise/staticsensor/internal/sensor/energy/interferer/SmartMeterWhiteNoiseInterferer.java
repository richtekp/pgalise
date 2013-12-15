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

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.service.RandomSeedService;

/**
 * Represents an interferer that creates generally low errors
 * 
 * @author Marcus
 * @author Andreas
 * @version 1.0 (Nov 17, 2012)
 */
public class SmartMeterWhiteNoiseInterferer extends EnergyBaseInterferer {

	/**
	 * File path for property file
	 */
	public static final String PROPERTIES_FILE_PATH = "/interferer_smartmeter_whitenoise.properties";

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 */
	public SmartMeterWhiteNoiseInterferer(RandomSeedService randomseedservice) {
		super(randomseedservice, SmartMeterWhiteNoiseInterferer.PROPERTIES_FILE_PATH);
	}

	@Override
	public double interfere(final double mutableValue, final Coordinate position, final long simTime) {
		// Should be changed?
		if (this.getRandom().nextDouble() <= this.changeProbability) {
			double changeValue = this.changeAmplitude * this.random.nextGaussian();
			return (this.getRandom().nextBoolean()) ? mutableValue + changeValue : mutableValue - changeValue;
		}

		// Returns with no change
		return mutableValue;
	}
}