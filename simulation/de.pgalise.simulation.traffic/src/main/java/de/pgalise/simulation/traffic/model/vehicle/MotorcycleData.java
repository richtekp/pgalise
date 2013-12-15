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

import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;

/**
 * Informations about the motorcycle
 * 
 * @author Sabrina
 */
public class MotorcycleData extends VehicleData {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -97081705088299060L;

	/**
	 * Color
	 */
	private Color color;

	/**
	 * Maximum speed in kmh
	 */
	private final int maxSpeed;

	/**
	 * Weight in kg
	 */
	private final int weight;

	/**
	 * Horse power
	 */
	private final double horsePower;

	/**
	 * Description of the motorcycle
	 */
	private final String description;

	/**
	 * Constructor
	 * 
	 * @param color
	 *            Color of the vehicle
	 * @param weight
	 *            Weight in kg
	 * @param horsePower
	 *            Horse power
	 * @param length
	 *            Length in mm
	 * @param maxSpeed
	 *            Maximum speed in kmh
	 * @param wheelbase
	 *            Wheelbase in mm
	 * @param axleCount
	 *            Number of axles
	 * @param description
	 *            Description of the motorcycle
	 */
	public MotorcycleData(Color color, int weight, double horsePower, int length, int maxSpeed, int wheelbase,
			int axleCount, String description, GpsSensor gpsSensor) {
		super(length, wheelbase, 0, axleCount, VehicleTypeEnum.MOTORCYCLE, gpsSensor);
		this.color = color;
		this.weight = weight;
		this.maxSpeed = maxSpeed;
		this.horsePower = horsePower;
		this.description = description;
	}

	/**
	 * Constructor
	 * 
	 * @param referenceData
	 *            MotorcycleData
	 */
	public MotorcycleData(MotorcycleData referenceData) {
		super(referenceData.getVehicleLength(), referenceData.getWheelbase1(), referenceData.getWheelbase2(), referenceData
				.getAxleCount(), referenceData.getType(), referenceData.getGpsSensor());
		this.color = referenceData.getColor();
		this.weight = referenceData.getWeight();
		this.maxSpeed = referenceData.getMaxSpeed();
		this.horsePower = referenceData.getHorsePower();
		this.description = referenceData.getDescription();
	}

	public int getMaxSpeed() {
		return this.maxSpeed;
	}

	public int getWeight() {
		return this.weight;
	}

	public double getHorsePower() {
		return this.horsePower;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "MotorcycleData [color=" + color + ", maxSpeed=" + maxSpeed + ", weight=" + weight + ", horsePower="
				+ horsePower + ", description=" + description + "]";
	}

}
