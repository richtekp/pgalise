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
 
package de.pgalise.simulation.operationCenter.internal.model.sensordata;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;

/**
 * Data from a GPS sensor. E.g. for for a {@link SensorHelper} with type {@link SensorType#GPS_CAR}.
 * @author Timo
 */
public class GPSSensorData extends SensorData {

	/**
	 * A GPSSensor with latitude and longitude as sensor values.
	 */
	private static final long serialVersionUID = -4537439047671111611L;
	private double lat, lng, distanceInMperStep;
	private int totalDistanceInM, speedInKmh, avgSpeedInKmh, directionInGrad;
	private long travelTimeInMs;

	/**
	 * Constructor
	 * @param sensorType
	 * 			the sensor type
	 * @param sensorID
	 * 			the ID of the sensor
	 * @param latitude
	 * 			the measured latitude
	 * @param longitude
	 * 			the measured longitude
	 * @param distanceInMperStep
	 * 			the distance in meter per update step (since the last update)
	 * @param totalDistanceInM
	 * 			the total distance in kilometer
	 * @param speedInKmh
	 * 			the current speed
	 * @param avgSpeedInKmh
	 * 			the average speed
	 * @param directionInGrad
	 * 			the direction of the car
	 * @param travelTimeInMs
	 * 			the travel time in milliseconds since beginning to drive
	 */
	public GPSSensorData(int sensorType, Sensor sensorID,
			double latitude, double longitude,
			double distanceInMperStep,
			int totalDistanceInM,
			int speedInKmh,
			int avgSpeedInKmh,
			int directionInGrad,
			long travelTimeInMs) {
		super(sensorType, sensorID);
		this.lat = latitude;
		this.lng = longitude;
		this.distanceInMperStep = distanceInMperStep;
		this.totalDistanceInM = totalDistanceInM;
		this.speedInKmh = speedInKmh;
		this.avgSpeedInKmh = avgSpeedInKmh;
		this.directionInGrad = directionInGrad;
		this.travelTimeInMs = travelTimeInMs;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getDistanceInMperStep() {
		return distanceInMperStep;
	}

	public void setDistanceInMperStep(double distanceInMperStep) {
		this.distanceInMperStep = distanceInMperStep;
	}

	public int getTotalDistanceInM() {
		return totalDistanceInM;
	}

	public void setTotalDistanceInM(int totalDistanceInM) {
		this.totalDistanceInM = totalDistanceInM;
	}

	public int getSpeedInKmh() {
		return speedInKmh;
	}

	public void setSpeedInKmh(int speedInKmh) {
		this.speedInKmh = speedInKmh;
	}

	public int getAvgSpeedInKmh() {
		return avgSpeedInKmh;
	}

	public void setAvgSpeedInKmh(int avgSpeedInKmh) {
		this.avgSpeedInKmh = avgSpeedInKmh;
	}

	public int getDirectionInGrad() {
		return directionInGrad;
	}

	public void setDirectionInGrad(int directionInGrad) {
		this.directionInGrad = directionInGrad;
	}

	public long getTravelTimeInMs() {
		return travelTimeInMs;
	}

	public void setTravelTimeInMs(long travelTimeInMs) {
		this.travelTimeInMs = travelTimeInMs;
	}
}
