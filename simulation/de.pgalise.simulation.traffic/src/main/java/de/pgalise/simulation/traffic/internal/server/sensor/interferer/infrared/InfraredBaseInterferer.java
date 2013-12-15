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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;

/**
 * Abstract super class for {@link InfraredInterferer}
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 14, 2012)
 */
public abstract class InfraredBaseInterferer implements InfraredInterferer {

	/**
	 * Random Seed Service
	 */
	protected RandomSeedService randomservice;

	/**
	 * Random Object
	 */
	protected Random random;

	/**
	 * Change amplitude
	 */
	protected int changeMaxPassengersCount;

	/**
	 * Properties with limits
	 */
	protected Properties properties;

	/**
	 * Probability to change the values
	 */
	protected double changeProbability;

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 * @param filepath
	 *            File path to the properties file
	 */
	public InfraredBaseInterferer(RandomSeedService randomseedservice, String filepath) {
		if (randomseedservice == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("null"));
		}
		if (filepath == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("filepath"));
		}
		this.randomservice = randomseedservice;
		this.random = new Random(this.randomservice.getSeed(InfraredBaseInterferer.class.getName()));

		// Read property file
		try (InputStream propInFile = InfraredBaseInterferer.class.getResourceAsStream(filepath)) {
			this.properties = new Properties();
			this.properties.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Save default values

		/*
		 * Note every property file should have these properties
		 */
		this.changeMaxPassengersCount = Integer.parseInt(this.properties.getProperty("change_maxpassengerscount"));
		this.changeProbability = Double.parseDouble(this.properties.getProperty("change_probability"));
	}

	public int getChangeMaxPassengersCount() {
		return this.changeMaxPassengersCount;
	}

	public double getChangeProbability() {
		return this.changeProbability;
	}

	public Random getRandom() {
		return this.random;
	}

	public void setChangeMaxPassengersCount(int changeMaxPassengersCount) {
		this.changeMaxPassengersCount = changeMaxPassengersCount;
	}

	public void setChangeProbability(double changeProbability) {
		this.changeProbability = changeProbability;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
