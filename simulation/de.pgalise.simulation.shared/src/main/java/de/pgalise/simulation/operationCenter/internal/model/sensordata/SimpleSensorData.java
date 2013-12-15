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
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Entity;

/**
 * A simple sensor data with one dataValue. E.g. for {@link SensorHelperSmartMeter}.
 * @author Timo
 */
@Entity
public class SimpleSensorData extends SensorData {
	private static final long serialVersionUID = 1588118319493499615L;
	private double dataValue;
	private int intValue;

	public SimpleSensorData() {
	}

	public SimpleSensorData(
		int intValue,double dataValue) {
		this.dataValue = dataValue;
		this.intValue = intValue;
	}

	public double getDataValue() {
		return dataValue;
	}

	public void setDataValue(double dataValue) {
		this.dataValue = dataValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public int getIntValue() {
		return intValue;
	}
}
