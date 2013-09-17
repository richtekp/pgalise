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
 
package de.pgalise.simulation.energy.internal;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.ejb.EJB;

import de.pgalise.simulation.energy.EnergyConsumptionManagerLocal;
import de.pgalise.simulation.energy.profile.EnergyProfile;
import de.pgalise.simulation.energy.profile.EnergyProfileLoader;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Handles all energy consumption calculations and the load of the energy profiles from CSV files.
 * It uses the under 'META-INF/ejb-jar.xml' specified {@link EnergyProfileLoader} to load the CSV files.
 * 
 * @author Andreas Rehfeldt
 * @author Timo
 */
public class CSVEnergyConsumptionManager implements EnergyConsumptionManagerLocal {

	/**
	 * 24 hours in millis
	 */
	public static final long NEXT_DAY_IN_MILLIS = 86400000L;

	/**
	 * File path for property file
	 */
	private static final String PROPERTIES_FILE_PATH = "/profile/profiles.properties";

	/**
	 * Convert the date object to the format 00:00:00 (removes the hours, minutes, seconds and millis)
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Date object
	 */
	public static long convertDateToMidnight(long timestamp) {
		if (timestamp < 1) {
			throw new IllegalArgumentException("timestamp");
		}

		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);

		// Timestamp is correct?
		if ((cal.get(Calendar.HOUR_OF_DAY) == 0) && (cal.get(Calendar.MINUTE) == 0) && (cal.get(Calendar.SECOND) == 0)
				&& (cal.get(Calendar.MILLISECOND) == 0)) {
			return timestamp;
		}

		// Correct the date
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTimeInMillis();
	}

	/**
	 * Interpolate two values
	 * 
	 * @param minTime
	 *            min time
	 * @param minValue
	 *            min value
	 * @param maxTime
	 *            max time
	 * @param maxValue
	 *            max value
	 * @param actTime
	 *            time for the new value
	 * @return interpolated value
	 */
	private static double interpolate(long minTime, double minValue, long maxTime, double maxValue, long actTime) {
		/*
		 * Linear interpolation: f(x)=f0+((f1-f0)/(x1-x0))*(x-x0)
		 */
		double value = minValue + (((maxValue - minValue) / (maxTime - minTime)) * (actTime - minTime));

		/*
		 * Round
		 */
		// value = Math.round(value * 1000.) / 1000.;

		return value;
	}

	/**
	 * Loader for the energy profiles
	 */
	@EJB
	private EnergyProfileLoader loader;

	/**
	 * Loaded energy profile
	 */
	private Map<EnergyProfileEnum, EnergyProfile> profiles;

	/**
	 * Properties
	 */
	private Properties props = null;

	/**
	 * Semaphore
	 */
	private final Semaphore semaphore = new Semaphore(1, true);

	/**
	 * Constructor
	 */
	public CSVEnergyConsumptionManager() {
		this.profiles = new HashMap<>();

		// Read props with the filepath to the energy profiles
		try (InputStream propInFile = CSVEnergyConsumptionManager.class.getResourceAsStream(PROPERTIES_FILE_PATH)) {
			this.props = new Properties();
			this.props.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getEnergyConsumptionInKWh(long timestamp, EnergyProfileEnum key, Coordinate position) {
		try {
			this.semaphore.acquire();

			return this.internalGetEnergyConsumptionInKWh(timestamp, key);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.semaphore.release();
		}

		throw new RuntimeException("Value can not be returned.");
	}

	public EnergyProfileLoader getLoader() {
		return this.loader;
	}

	public Map<EnergyProfileEnum, EnergyProfile> getProfiles() {
		return this.profiles;
	}

	private void loadEnergyProfiles(long start, long end) {
		try {
			// Only one Thread
			this.semaphore.acquire();

			/*
			 * Prepare timestamps
			 */
			start = convertDateToMidnight(start);
			end = convertDateToMidnight(end) + NEXT_DAY_IN_MILLIS;

			/*
			 * Load profiles
			 */

			// Load all profiles
			for (EnergyProfileEnum profileEnum : EnergyProfileEnum.values()) {
				try {
					// Delete old data
					if (this.profiles.get(profileEnum) != null) {
						this.profiles.remove(profileEnum);
					}

					// Load profile with enum key
					this.profiles.put(profileEnum, this.loader.loadProfile(start, end,
							CSVEnergyConsumptionManager.class.getResourceAsStream(this.props.getProperty(profileEnum
									.getKey()))));
				} catch (IOException e) {
					throw new RuntimeException("Can't load " + profileEnum.toString());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.semaphore.release();
		}
	}

	public void setLoader(EnergyProfileLoader loader) {
		this.loader = loader;
	}

	/**
	 * Calculate the consumption for the given energy profile
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param key
	 *            enum of the energy profile
	 * @return energy consumption
	 */
	private double internalGetEnergyConsumptionInKWh(long timestamp, EnergyProfileEnum key) {
		if (timestamp < 1) {
			throw new IllegalArgumentException("timestamp");
		} else if (key == null) {
			throw new IllegalArgumentException("key");
		}

		// Get correct profile
		EnergyProfile profile = this.profiles.get(key);
		if ((profile == null) || profile.getData().isEmpty()) {
			throw new RuntimeException("Energy profile " + key.getKey() + " is not loaded!");
		}

		Double result;
		long difference = timestamp % EnergyProfile.DATA_INTERVAL;

		if (difference > 0) { // Interpolate?
			// Convert the timestamp to the last 15min interval
			long minTimestamp = timestamp - difference;
			long maxTimestamp = minTimestamp + EnergyProfile.DATA_INTERVAL;

			// Get value
			Double minValue = profile.getData().get(minTimestamp);
			Double maxValue = profile.getData().get(maxTimestamp);

			// Nothing found?
			if ((minValue == null) || (maxValue == null)) {
				throw new RuntimeException("No energy data found for the timestamp " + new Date(timestamp));
			}

			// Interpolate
			result = interpolate(minTimestamp, minValue, maxTimestamp, maxValue, timestamp);
		} else { // No interpolation

			// Get data
			result = profile.getData().get(timestamp);

			// Nothing found?
			if (result == null) {
				throw new RuntimeException("No energy data found for the timestamp " + new Date(timestamp));
			}
		}

		// Return value
		return result;
	}

	@Override
	public void init(long start, long end,
			WeatherController weatherController) {
		this.loadEnergyProfiles(start, end);
	}
}
