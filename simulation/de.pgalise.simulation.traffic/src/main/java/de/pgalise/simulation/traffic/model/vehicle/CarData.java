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
 * Information about the car
 * 
 * @author Marcus
 * @version 1.0 (Nov 7, 2012)
 */
public class CarData extends VehicleData {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -2380378213126874308L;

	/**
	 * Color
	 */
	private Color color;

	/**
	 * Description
	 */
	private final String description;

	/**
	 * Height in mm
	 */
	private final int height; // MM

	/**
	 * Maximum speed in khm
	 */
	private final int maxSpeed;

	/**
	 * Power in kw
	 */
	private final double power; // KW

	/**
	 * Weight in kg
	 */
	private final int weight; // Kilogramm

	/**
	 * width of the wheel distance in mm
	 */
	private final int wheelDistanceWidth; // MM

	/**
	 * Width in mm
	 */
	private final int width;

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
	 */
	public CarData(Color color, int wheelDistanceWidth, int wheelbase1, int wheelbase2, int length, int width,
			int height, int weight, double power, int maxSpeed, int axleCount, String description,
			GpsSensor gpsSensor, VehicleTypeEnum type) {
		super(length, wheelbase1, wheelbase2, axleCount, type, gpsSensor);
		this.color = color;
		this.wheelDistanceWidth = wheelDistanceWidth;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.power = power;
		this.maxSpeed = maxSpeed;
		this.description = description;

	}

	/**
	 * Constructor
	 * 
	 * @param referenceData
	 *            CarData
	 */
	public CarData(CarData referenceData) {
		this(referenceData.getColor(), referenceData.getWheelDistanceWidth(), referenceData.getWheelbase1(),
				referenceData.getWheelbase2(), referenceData.getVehicleLength(), referenceData.getWidth(), referenceData
						.getHeight(), referenceData.getWeight(), referenceData.getPower(), referenceData.getMaxSpeed(),
				referenceData.getAxleCount(), referenceData.getDescription(), referenceData.getGpsSensor(), referenceData.getType());
	}

	public Color getColor() {
		return this.color;
	}

	public String getDescription() {
		return this.description;
	}

	public int getHeight() {
		return this.height;
	}

	public int getMaxSpeed() {
		return this.maxSpeed;
	}

	public double getPower() {
		return this.power;
	}

	public int getWeight() {
		return this.weight;
	}

	public int getWheelDistanceWidth() {
		return this.wheelDistanceWidth;
	}

	public int getWidth() {
		return this.width;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "CarData [color=" + color + ", description=" + description + ", height=" + height + ", maxSpeed="
				+ maxSpeed + ", power=" + power + ", weight=" + weight + ", wheelDistanceWidth=" + wheelDistanceWidth
				+ ", width=" + width + "]";
	}

}
