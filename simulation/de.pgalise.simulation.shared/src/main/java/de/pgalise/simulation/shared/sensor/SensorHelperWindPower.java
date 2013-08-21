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
 
package de.pgalise.simulation.shared.sensor;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Sensorhelper for WindPowerSensors.
 * 
 * @author Timo
 */
public class SensorHelperWindPower extends SensorHelper {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -7223666531535845013L;

	/**
	 * usually between 0.3 and 0.8
	 */
	private double activityValue;

	/**
	 * rotor length in m (usually between 7.5m and 63m).
	 */
	private double rotorLength;

	/**
	 * Default
	 */
	public SensorHelperWindPower() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param activityValue
	 *            usually between 0.3 and 0.8
	 * @param rotorLength
	 *            Rotor length in m (usually between 7.5m and 63m).
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 * @param nodeId
	 */
	public SensorHelperWindPower(int sensorID, Coordinate position, double activityValue, double rotorLength,
			List<SensorInterfererType> sensorInterfererList, String nodeId) {
		super(sensorID, position, SensorType.WINDPOWERSENSOR, sensorInterfererList, nodeId);
		this.activityValue = activityValue;
		this.rotorLength = rotorLength;
	}

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param rotorLength
	 *            Rotor length in m (usually between 7.5m and 63m).
	 * @param activityValue
	 *            usually between 0.3 and 0.8
	 * @param updateSteps
	 *            Update steps
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 * @param nodeId
	 */
	public SensorHelperWindPower(int sensorID, Coordinate position, double activityValue, double rotorLength,
			int updateSteps, List<SensorInterfererType> sensorInterfererList, String nodeId) {
		this(sensorID, position, activityValue, rotorLength, sensorInterfererList, nodeId);
		super.setUpdateSteps(updateSteps);
	}

	public double getActivityValue() {
		return this.activityValue;
	}

	public double getRotorLength() {
		return this.rotorLength;
	}

	public void setActivityValue(double activityValue) {
		this.activityValue = activityValue;
	}

	public void setRotorLength(double rotorLength) {
		this.rotorLength = rotorLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(activityValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(rotorLength);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorHelperWindPower other = (SensorHelperWindPower) obj;
		if (Double.doubleToLongBits(activityValue) != Double.doubleToLongBits(other.activityValue))
			return false;
		if (Double.doubleToLongBits(rotorLength) != Double.doubleToLongBits(other.rotorLength))
			return false;
		return true;
	}
}
