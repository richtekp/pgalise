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
 
package de.pgalise.simulation.shared.entity;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import javax.faces.bean.ManagedBean;
import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * Data from a GPS sensor. E.g. for for a {@link SensorHelper} with type {@link SensorType#GPS_CAR}.
 * @author Timo
 */
@Entity
@ManagedBean
public class GPSSensorData extends SensorData {

	/**
	 * A GPSSensor with latitude and longitude as sensor values.
	 */
	private static final long serialVersionUID = -4537439047671111611L;
	@Embedded
	private BaseCoordinate position;
	private double distanceInMperStep;
	private int totalDistanceInM, speedInKmh, avgSpeedInKmh, directionInGrad;
	private long travelTimeInMs;

	/**
	 * Constructor
	 */
	public GPSSensorData( ) {
	}

	public GPSSensorData(BaseCoordinate position,
		double distanceInMperStep,
		int totalDistanceInM,
		int speedInKmh,
		int avgSpeedInKmh,
		int directionInGrad,
		long travelTimeInMs) {
		this.position = position;
		this.distanceInMperStep = distanceInMperStep;
		this.totalDistanceInM = totalDistanceInM;
		this.speedInKmh = speedInKmh;
		this.avgSpeedInKmh = avgSpeedInKmh;
		this.directionInGrad = directionInGrad;
		this.travelTimeInMs = travelTimeInMs;
	}

	public BaseCoordinate getPosition() {
		return position;
	}

	public void setPosition(BaseCoordinate position) {
		this.position = position;
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
