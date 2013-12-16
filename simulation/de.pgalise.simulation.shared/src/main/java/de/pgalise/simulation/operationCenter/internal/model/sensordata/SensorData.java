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
import java.io.Serializable;
import javax.faces.bean.ManagedBean;

/**
 * Super class for all sensor data.<tt>SensorData</tt>encapsulates information
 * which changes with every update step
 *
 * @author Timo
 */
/*
SensorData doesn't know it's owning sensor because this just complicates garbage collection
*/
@ManagedBean
public abstract class SensorData extends AbstractIdentifiable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 6576465794843244817L;

	/**
	 * Constructor
	 *
	 * @param sensorType Type of the sensor
	 * @param sensor ID of the sensor
	 */
	public SensorData() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		return true;
	}
}
