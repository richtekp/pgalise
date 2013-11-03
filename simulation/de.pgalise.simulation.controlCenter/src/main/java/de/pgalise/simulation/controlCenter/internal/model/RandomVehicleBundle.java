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
 
package de.pgalise.simulation.controlCenter.internal.model;

import de.pgalise.simulation.sensorFramework.Sensor;
import java.util.Set;
import java.util.UUID;

/**
 * A bundle to produce random dynamic sensors.
 * @author Timo
 */
public class RandomVehicleBundle {
	private int randomCarAmount, randomBikeAmount, randomTruckAmount, randomMotorcycleAmount;
	private double gpsCarRatio, gpsBikeRatio, gpsTruckRatio, gpsMotorcycleRatio;
	private Set<Sensor<?>> usedSensorIDs;
	private Set<UUID> usedUUIDs;
	
	/**
	 * Constructor
	 * @param randomCarAmount
	 * @param gpsCarRatio value between 0.0 and 1.0
	 * @param randomBikeAmount
	 * @param gpsBikeRatio between 0.0 and 1.0
	 * @param randomTruckAmount
	 * @param gpsTruckRatio between 0.0 and 1.0
	 * @param randomMotorcycleAmount
	 * @param gpsMotorcycleRatio between 0.0 and 1.0
	 * @param usedSensorIDs
	 * @param usedUUIDs
	 */
	public RandomVehicleBundle(int randomCarAmount, double gpsCarRatio,
			int randomBikeAmount, double gpsBikeRatio,
			int randomTruckAmount, double gpsTruckRatio,
			int randomMotorcycleAmount, double gpsMotorcycleRatio, 
			Set<Sensor<?>> usedSensorIDs, Set<UUID> usedUUIDs) {
		super();
		if(randomCarAmount < 0) {
			throw new IllegalArgumentException("randomCarAmount must be >= 0!");
		}
		if(randomTruckAmount < 0) {
			throw new IllegalArgumentException("randomTruckAmount must be >= 0!");
		}
		if(randomBikeAmount < 0) {
			throw new IllegalArgumentException("randomBikeAmount must be >= 0!");
		}
		if(randomMotorcycleAmount < 0) {
			throw new IllegalArgumentException("randomMotorcycleAmount must be >= 0!");
		}
		if(gpsCarRatio < 0.0 || gpsCarRatio > 1.0) {
			throw new IllegalArgumentException("gpsCarRation must be >= 0.0 && <= 1.0");
		}
		if(gpsBikeRatio < 0.0 || gpsBikeRatio > 1.0) {
			throw new IllegalArgumentException("gpsBikeRatio must be >= 0.0 && <= 1.0");
		}
		if(gpsTruckRatio < 0.0 || gpsTruckRatio > 1.0) {
			throw new IllegalArgumentException("gpsTruckRatio must be >= 0.0 && <= 1.0");
		}
		if(gpsMotorcycleRatio < 0.0 || gpsMotorcycleRatio > 1.0) {
			throw new IllegalArgumentException("gpsMotorcycleRatio must be >= 0.0 && <= 1.0");
		}
		
		this.randomCarAmount = randomCarAmount;
		this.randomBikeAmount = randomBikeAmount;
		this.randomTruckAmount = randomTruckAmount;
		this.randomMotorcycleAmount = randomMotorcycleAmount;
		this.gpsCarRatio = gpsCarRatio;
		this.gpsBikeRatio = gpsBikeRatio;
		this.gpsTruckRatio = gpsTruckRatio;
		this.gpsMotorcycleRatio = gpsMotorcycleRatio;
		this.usedSensorIDs = usedSensorIDs;
		this.usedUUIDs = usedUUIDs;
	}

	public int getRandomCarAmount() {
		return randomCarAmount;
	}

	public void setRandomCarAmount(int randomCarAmount) {
		this.randomCarAmount = randomCarAmount;
	}

	public int getRandomBikeAmount() {
		return randomBikeAmount;
	}

	public void setRandomBikeAmount(int randomBikeAmount) {
		this.randomBikeAmount = randomBikeAmount;
	}

	public int getRandomTruckAmount() {
		return randomTruckAmount;
	}

	public void setRandomTruckAmount(int randomTruckAmount) {
		this.randomTruckAmount = randomTruckAmount;
	}

	public int getRandomMotorcycleAmount() {
		return randomMotorcycleAmount;
	}

	public void setRandomMotorcycleAmount(int randomMotorcycleAmount) {
		this.randomMotorcycleAmount = randomMotorcycleAmount;
	}

	public double getGpsCarRatio() {
		return gpsCarRatio;
	}

	public void setGpsCarRatio(double gpsCarRatio) {
		this.gpsCarRatio = gpsCarRatio;
	}

	public double getGpsBikeRatio() {
		return gpsBikeRatio;
	}

	public void setGpsBikeRatio(double gpsBikeRatio) {
		this.gpsBikeRatio = gpsBikeRatio;
	}

	public double getGpsMotorcycleAmount() {
		return gpsMotorcycleRatio;
	}

	public void setGpsMotorcycleAmount(double gpsMotorcycleAmount) {
		this.gpsMotorcycleRatio = gpsMotorcycleAmount;
	}

	public double getGpsTruckRatio() {
		return gpsTruckRatio;
	}

	public void setGpsTruckRatio(double gpsTruckRatio) {
		this.gpsTruckRatio = gpsTruckRatio;
	}

	public double getGpsMotorcycleRatio() {
		return gpsMotorcycleRatio;
	}

	public void setGpsMotorcycleRatio(double gpsMotorcycleRatio) {
		this.gpsMotorcycleRatio = gpsMotorcycleRatio;
	}

	public Set<Sensor<?>> getUsedSensorIDs() {
		return usedSensorIDs;
	}

	public void setUsedSensorIDs(Set<Sensor<?>> usedSensorIDs) {
		this.usedSensorIDs = usedSensorIDs;
	}

	public Set<UUID> getUsedUUIDs() {
		return usedUUIDs;
	}

	public void setUsedUUIDs(Set<UUID> usedUUIDs) {
		this.usedUUIDs = usedUUIDs;
	}
}
