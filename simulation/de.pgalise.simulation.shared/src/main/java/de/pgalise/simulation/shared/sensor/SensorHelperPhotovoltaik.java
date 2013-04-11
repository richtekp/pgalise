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

import de.pgalise.simulation.shared.geolocation.GeoLocation;

/**
 * SensorHelper for PhotovoltaikSensors.
 * @author Timo
 */
public class SensorHelperPhotovoltaik extends SensorHelper {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3444395619468088788L;

	/**
	 * Roof area in m²
	 */
	private int area;

	/**
	 * Default
	 */
	public SensorHelperPhotovoltaik() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param area
	 *            roof area in m².
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 */
	public SensorHelperPhotovoltaik(int sensorID, GeoLocation position, int area,
			List<SensorInterfererType> sensorInterfererList, String nodeId) {
		super(sensorID, position, SensorType.PHOTOVOLTAIK, sensorInterfererList, nodeId);
		this.area = area;
	}

	/**
	 * Constructor.
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param area
	 *            roof area in m².
	 * @param updateSteps
	 *            Update steps
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 */
	public SensorHelperPhotovoltaik(int sensorID, GeoLocation position, int area, int updateSteps,
			List<SensorInterfererType> sensorInterfererList, String nodeId) {
		this(sensorID, position, area, sensorInterfererList, nodeId);
		super.setUpdateSteps(updateSteps);
	}

	public int getArea() {
		return this.area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + area;
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
		SensorHelperPhotovoltaik other = (SensorHelperPhotovoltaik) obj;
		if (area != other.area)
			return false;
		return true;
	}
}
