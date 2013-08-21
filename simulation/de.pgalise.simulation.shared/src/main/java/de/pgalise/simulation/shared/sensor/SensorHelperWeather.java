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
 * Extends the SensorHelper with a weather station ID.
 * 
 * @author Timo
 */
public class SensorHelperWeather extends SensorHelper {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 5671804078636416006L;

	/**
	 * ID of the weather station
	 */
	private int weatherStationID;

	/**
	 * Default
	 */
	public SensorHelperWeather() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param sensorType
	 *            Type of the sensor
	 * @param weatherStationID
	 *            ID of the weather station
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 * @param nodeId
	 */
	public SensorHelperWeather(int sensorID, Coordinate position, SensorType sensorType, int weatherStationID,
			List<SensorInterfererType> sensorInterfererList, String nodeId) {
		super(sensorID, position, sensorType, sensorInterfererList, nodeId);
		this.weatherStationID = weatherStationID;
	}

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param sensorType
	 *            Type of the sensor
	 * @param updateSteps
	 *            Update steps
	 * @param weatherStationID
	 *            ID of the weather station
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 * @param nodeId
	 */
	public SensorHelperWeather(int sensorID, Coordinate position, SensorType sensorType, int updateSteps,
			int weatherStationID, List<SensorInterfererType> sensorInterfererList, String nodeId) {
		this(sensorID, position, sensorType, weatherStationID, sensorInterfererList, nodeId);
		super.setUpdateSteps(updateSteps);
	}

	public int getWeatherStationID() {
		return weatherStationID;
	}

	public void setWeatherStationID(int weatherStationID) {
		this.weatherStationID = weatherStationID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + weatherStationID;
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
		SensorHelperWeather other = (SensorHelperWeather) obj;
		if (weatherStationID != other.weatherStationID)
			return false;
		return true;
	}
}
