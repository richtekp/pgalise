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
 
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;

/**
 * Abstract super class for {@link InductionLoopInterferer}
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 14, 2012)
 */
public abstract class InductionLoopBaseInterferer implements InductionLoopInterferer {
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
	 * Properties with limits
	 */
	protected Properties properties;

	/**
	 * Probability to change the values
	 */
	protected double changeProbability;

	/**
	 * Option from which maximal length can error occur
	 */
	protected int changeMaxLength;

	/**
	 * Option from which minimal length can error occur
	 */
	protected int changeMinLength;

	/**
	 * Option from which maximal vehicle count can error occur
	 */
	protected int changeMaxVehicleCount;

	/**
	 * Option from which maximal vehicle count can error occur
	 */
	protected double changeMinSpeed;

	/**
	 * Constructor
	 * 
	 * @param randomseedservice
	 *            Random Seed Service
	 * @param filepath
	 *            File path to the properties file
	 */
	public InductionLoopBaseInterferer(RandomSeedService randomseedservice, String filepath) {
		if (randomseedservice == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("randomseedservice"));
		}
		if (filepath == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("filepath"));
		}
		this.randomservice = randomseedservice;
		this.random = new Random(this.randomservice.getSeed(InductionLoopBaseInterferer.class.getName()));

		// Read property file
		try (InputStream propInFile = InductionLoopBaseInterferer.class.getResourceAsStream(filepath)) {
			this.properties = new Properties();
			this.properties.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Save default values

		/*
		 * Note every property file should have these properties
		 */
		this.changeProbability = Double.parseDouble(this.properties.getProperty("change_probability"));
		this.changeMinLength = Integer.parseInt(this.properties.getProperty("change_maxlength"));
		this.changeMaxLength = Integer.parseInt(this.properties.getProperty("change_minlength"));
		this.changeMaxVehicleCount = Integer.parseInt(this.properties.getProperty("change_maxvehiclecount"));
		this.changeMinSpeed = Double.parseDouble(this.properties.getProperty("change_minspeed"));
	}

	public int getChangeMaxLength() {
		return this.changeMaxLength;
	}

	public int getChangeMaxVehicleCount() {
		return this.changeMaxVehicleCount;
	}

	public int getChangeMinLength() {
		return this.changeMinLength;
	}

	public double getChangeMinSpeed() {
		return this.changeMinSpeed;
	}

	public double getChangeProbability() {
		return this.changeProbability;
	}

	public Random getRandom() {
		return this.random;
	}

	public void setChangeMaxLength(int changeMaxLength) {
		this.changeMaxLength = changeMaxLength;
	}

	public void setChangeMaxVehicleCount(int changeMaxVehicleCount) {
		this.changeMaxVehicleCount = changeMaxVehicleCount;
	}

	public void setChangeMinLength(int changeMinLength) {
		this.changeMinLength = changeMinLength;
	}

	public void setChangeMinSpeed(double changeMinSpeed) {
		this.changeMinSpeed = changeMinSpeed;
	}

	public void setChangeProbability(double changeProbability) {
		this.changeProbability = changeProbability;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
