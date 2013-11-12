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
 
package de.pgalise.simulation.sensorFramework;

import de.pgalise.simulation.sensorFramework.SensorHelper;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;

/**
 * Sensorhelper for smartmeter sensors.
 * 
 * @author Timo
 */
public class SensorHelperSmartMeter extends SensorHelper {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 7207876472648836453L;

	/**
	 * Measure Radius in m
	 */
	private int measureRadiusInMeter;

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param measureRadiusInMeter
	 *            Radius in meter
	 * @param interferer
	 *            Interferer of the sensor
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 */
	public SensorHelperSmartMeter(Sensor<?> sensorID, Coordinate position, int measureRadiusInMeter,
			List<SensorInterfererType> sensorInterfererList) {
		super(sensorID, position, SensorTypeEnum.SMARTMETER, sensorInterfererList);
		this.measureRadiusInMeter = measureRadiusInMeter;
	}

	/**
	 * Constructor
	 * 
	 * @param sensorID
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param measureRadiusInMeter
	 *            Radius in meter
	 * @param updateSteps
	 *            Update steps
	 * @param sensorInterfererList
	 *            List of interferer for the sensor
	 */
	public SensorHelperSmartMeter(Sensor<?> sensorID, Coordinate position, int measureRadiusInMeter, int updateSteps,
			List<SensorInterfererType> sensorInterfererList) {
		this(sensorID, position, measureRadiusInMeter, sensorInterfererList);
		super.setUpdateSteps(updateSteps);
	}

	public int getMeasureRadiusInMeter() {
		return this.measureRadiusInMeter;
	}

	public void setMeasureRadiusInMeter(int measureRadiusInMeter) {
		this.measureRadiusInMeter = measureRadiusInMeter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + measureRadiusInMeter;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SensorHelperSmartMeter other = (SensorHelperSmartMeter) obj;
		if (measureRadiusInMeter != other.measureRadiusInMeter) {
			return false;
		}
		return true;
	}
}