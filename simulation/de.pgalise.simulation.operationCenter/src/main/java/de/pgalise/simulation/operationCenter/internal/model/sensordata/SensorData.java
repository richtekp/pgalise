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

import java.io.Serializable;

/**
 * Super class for all sensor data.
 * 
 * @author Timo
 */
public class SensorData implements Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 6576465794843244817L;

	/**
	 * Type of the sensor
	 */
	private int type;

	/**
	 * ID of the sensor
	 */
	private int id;

	/**
	 * Constructor
	 * 
	 * @param sensorType
	 *            Type of the sensor
	 * @param sensorID
	 *            ID of the sensor
	 */
	public SensorData(int sensorType, int sensorID) {
		this.type = sensorType;
		this.id = sensorID;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SensorData other = (SensorData) obj;
		if (id != other.id) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
}
