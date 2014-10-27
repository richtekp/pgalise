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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.TopoRadarInterferer;

/**
 * Abstract super class for traffic count interferers
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 14, 2012)
 */
public abstract class TopoRadarBaseInterferer implements TopoRadarInterferer {
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
	 * Change length amplitude
	 */
	protected double changeLengthAmplitude;

	/**
	 * Change wheelbase amplitude
	 */
	protected double changeWheelbaseAmplitude;

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
	public TopoRadarBaseInterferer(RandomSeedService randomseedservice, String filepath) {
		if (randomseedservice == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomseedservice"));
		}
		if (filepath == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("filepath"));
		}
		this.randomservice = randomseedservice;
		this.random = new Random(this.randomservice.getSeed(TopoRadarBaseInterferer.class.getName()));

		// Read property file
		try (InputStream propInFile = TopoRadarBaseInterferer.class.getResourceAsStream(filepath)) {
			this.properties = new Properties();
			this.properties.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Save default values

		/*
		 * Note every property file should have these properties
		 */
		this.changeLengthAmplitude = Double.parseDouble(this.properties.getProperty("change_amplitude_length"));
		this.changeWheelbaseAmplitude = Double.parseDouble(this.properties.getProperty("change_amplitude_wheelbase"));
		this.changeProbability = Double.parseDouble(this.properties.getProperty("change_probability"));
	}

	public double getChangeLengthAmplitude() {
		return this.changeLengthAmplitude;
	}

	public double getChangeProbability() {
		return this.changeProbability;
	}

	public double getChangeWheelbaseAmplitude() {
		return this.changeWheelbaseAmplitude;
	}

	public Random getRandom() {
		return this.random;
	}

	public void setChangeLengthAmplitude(double changeLengthAmplitude) {
		this.changeLengthAmplitude = changeLengthAmplitude;
	}

	public void setChangeProbability(double changeProbability) {
		this.changeProbability = changeProbability;
	}

	public void setChangeWheelbaseAmplitude(double changeWheelbaseAmplitude) {
		this.changeWheelbaseAmplitude = changeWheelbaseAmplitude;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
