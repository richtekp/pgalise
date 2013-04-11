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
 
package de.pgalise.simulation.traffic.model.vehicle;

import java.awt.Color;

import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;

/**
 * Information about a truck
 * 
 * @author Andreas
 * @version 1.0 (Nov 7, 2012)
 */
public class TruckData extends CarData {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 6626940882630925074L;

	/**
	 * Number of trailers
	 */
	private int trailerCount;

	/**
	 * Distance between tailers in mm
	 */
	private int trailerDistance;

	/**
	 * Length of each trailer. Assumption every trailer has the same length.
	 */
	private int trailerLength;

	/**
	 * Constructor
	 * 
	 * @param color
	 *            Color
	 * @param wheelDistanceWidth
	 *            width of the wheel distance in mm
	 * @param wheelbase
	 *            Wheelbase in mm
	 * @param length
	 *            Length in mm
	 * @param width
	 *            Width in mm
	 * @param height
	 *            Height in mm
	 * @param weight
	 *            Weight in kg
	 * @param power
	 *            Power in kw
	 * @param maxSpeed
	 *            Maximum speed in khm
	 * @param axleCount
	 *            Number of axle
	 * @param description
	 *            Description
	 * @param trailerCount
	 *            Number of the trailers
	 * @param trailerDistance
	 *            Distance between the trailers
	 * @param trailerLength
	 *            Length of each trailer
	 */
	public TruckData(Color color, int wheelDistanceWidth, int wheelbase1, int wheelbase2, int length, int width,
			int height, int weight, double power, int maxSpeed, int axleCount, String description, int trailerCount,
			int trailerDistance, int trailerLength, SensorHelper gpsSensor) {
		super(color, wheelDistanceWidth, wheelbase1, wheelbase2, length, width, height, weight, power, maxSpeed,
				axleCount, description, gpsSensor, VehicleTypeEnum.TRUCK);
		this.trailerCount = trailerCount;
		this.trailerDistance = trailerDistance;
		this.trailerLength = trailerLength;
	}

	/**
	 * Constructor
	 * 
	 * @param referenceData
	 *            TruckData
	 */
	public TruckData(TruckData referenceData) {
		this(referenceData.getColor(), referenceData.getWheelDistanceWidth(), referenceData.getWheelbase1(),
				referenceData.getWheelbase2(), referenceData.getLength(), referenceData.getWidth(), referenceData
						.getHeight(), referenceData.getWeight(), referenceData.getPower(), referenceData.getMaxSpeed(),
				referenceData.getAxleCount(), referenceData.getDescription(), referenceData.getTrailerCount(),
				referenceData.getTrailerDistance(), referenceData.getTrailerLength(), referenceData.getGpsSensorHelper());
	}

	public int getTrailerCount() {
		return this.trailerCount;
	}

	public void setTrailerCount(int trailerCount) {
		this.trailerCount = trailerCount;
	}

	public int getTrailerDistance() {
		return this.trailerDistance;
	}

	public void setTrailerDistance(int trailerDistance) {
		this.trailerDistance = trailerDistance;
	}

	public int getTrailerLength() {
		return this.trailerLength;
	}

	public void setTrailerLength(int trailerLength) {
		this.trailerLength = trailerLength;
	}

	/**
	 * Overrides the length of the vehicle with the length of vehicle plus the trailers
	 */
	@Override
	public int getLength() {
		return (super.getLength() + (this.trailerCount * (this.trailerDistance + this.trailerLength)));
	}

	@Override
	public String toString() {
		return "TruckData [trailerCount=" + trailerCount + ", trailerDistance=" + trailerDistance + ", trailerLength="
				+ trailerLength + "]";
	}

}
