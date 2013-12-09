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
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;

/**
 * Sensor data for traffic light. The sensorID of the
 * traffic light sensor data is the intersection ID. It also provides
 * an ID for the single traffic light, called trafficlightID {@link TrafficLightSensorData#getTrafficLightID()}.
 * @author Dennis
 */
public class TrafficLightSensorData extends SensorData {
	private static final long serialVersionUID = 1588118319493499615L;
	private int value;
	private double angle1, angle2;
	private int trafficLightID;
	
	/**
	 * Constructor
	 * @param sensorType
	 * 			the type of sensor
	 * @param sensorID
	 * 			the ID of the traffic light intersection
	 * @param sensorValue
	 * 			the measured value of the sensor
	 * @param trafficLightID
	 * 			the id of this traffic light
	 */
	public TrafficLightSensorData(Sensor sensorID, int sensorValue, double angle1, double angle2, int trafficLightID) {
		super(SensorTypeEnum.TRAFFIC_LIGHT_INTERSECTION.getSensorTypeId(), sensorID);
		this.value = sensorValue;
		this.angle1 = angle1;
		this.angle2 = angle2;
		this.trafficLightID = trafficLightID;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public double getAngle1() {
		return angle1;
	}

	public void setAngle1(float angle1) {
		this.angle1 = angle1;
	}

	public double getAngle2() {
		return angle2;
	}

	public void setAngle2(double angle2) {
		this.angle2 = angle2;
	}

	public int getTrafficLightID() {
		return trafficLightID;
	}

	public void setTrafficLightID(int trafficLightID) {
		this.trafficLightID = trafficLightID;
	}

	public void setAngle1(double angle1) {
		this.angle1 = angle1;
	}
}
