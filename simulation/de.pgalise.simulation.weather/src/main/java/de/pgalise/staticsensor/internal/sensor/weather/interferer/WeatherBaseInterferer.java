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
 
package de.pgalise.staticsensor.internal.sensor.weather.interferer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;

/**
 * Abstract super class for weather interferers
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 14, 2012)
 */
public abstract class WeatherBaseInterferer implements WeatherInterferer {
	private static final long serialVersionUID = 1L;

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
	protected double changeAmplitude;

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
	public WeatherBaseInterferer(RandomSeedService randomseedservice, String filepath) {
		if (randomseedservice == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomseedservice"));
		}
		if (filepath == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("filepath"));
		}
		this.randomservice = randomseedservice;
		this.random = new Random(this.randomservice.getSeed(WeatherBaseInterferer.class.getName()));

		// Read property file
		try (InputStream propInFile = WeatherBaseInterferer.class.getResourceAsStream(filepath)) {
			properties = new Properties();
			properties.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Save default values

		/*
		 * Note every property file should have these properties
		 */
		changeAmplitude = Double.parseDouble(this.properties.getProperty("change_amplitude"));
		changeProbability = Double.parseDouble(this.properties.getProperty("change_probability"));
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public double getChangeAmplitude() {
		return changeAmplitude;
	}

	public void setChangeAmplitude(double changeAmplitude) {
		this.changeAmplitude = changeAmplitude;
	}

	public double getChangeProbability() {
		return changeProbability;
	}

	public void setChangeProbability(double changeProbability) {
		this.changeProbability = changeProbability;
	}

}
