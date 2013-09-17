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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * A sensor helper without sensor interferer.
 * This is useful to serialize the sensor data without having
 * a real instance of the sensors. Real instances can only be created
 * by the appropriate controller and are unknown by the other components.
 * 
 * @author Timo
 */
public class SensorHelper implements Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 4614756690220322870L;

	/**
	 * Position
	 */
	private Coordinate position;

	/**
	 * ID of the sensor
	 */
	private int sensorID;

	/**
	 * Type of sensor
	 */
	private SensorType sensorType;

	/**
	 * Update steps
	 */
	private int updateSteps = 1;

	/**
	 * List of interferer
	 */
	private List<SensorInterfererType> sensorInterfererType;

	/**
	 * Node ID
	 */
	private String nodeId;

	/**
	 * Constructor
	 */
	public SensorHelper() {
		this.sensorInterfererType = new ArrayList<>();
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
	 * @param sensorInterfererList
	 *            List with SensorInterfererTypes
	 * @param nodeId
	 */
	public SensorHelper(int sensorID, Coordinate position, SensorType sensorType,
			List<SensorInterfererType> sensorInterfererList, String nodeId) {
		this(sensorID, position, sensorType, 1, sensorInterfererList, nodeId);
	}

	/**
	 * Constructor
	 * 
	 * @param sensorType
	 *            SensorType
	 * @param sensorID
	 *            ID of sensor
	 * @param position
	 *            (start) position of the sensor
	 * @param updateSteps
	 *            Update steps
	 * @param sensorInterfererList
	 *            List with SensorInterfererTypes
	 */
	public SensorHelper(int sensorID, Coordinate position, SensorType sensorType, int updateSteps,
			List<SensorInterfererType> sensorInterfererList, String nodeId) {
		if (sensorID < 0) {
			throw new IllegalArgumentException("sensorID < 0");
		}
		if (position == null && !SensorType.GPS.contains(sensorType)) {
			throw new IllegalArgumentException("position is null");
		}
		if (sensorType == null) {
			throw new IllegalArgumentException("sensorType is null");
		}
		if (sensorInterfererList == null) {
			throw new IllegalArgumentException("sensorInterfererList is null");
		}
		this.sensorID = sensorID;
		this.position = position;
		this.sensorType = sensorType;
		if (updateSteps >= 0) {
			this.updateSteps = updateSteps;
		}
		this.sensorInterfererType = sensorInterfererList;
		this.nodeId = nodeId;
	}

	public Coordinate getPosition() {
		return this.position;
	}

	public int getSensorID() {
		return this.sensorID;
	}

	public SensorType getSensorType() {
		return this.sensorType;
	}

	public int getUpdateSteps() {
		return this.updateSteps;
	}

	public void setPosition(Coordinate position) {
		this.position = position;
	}

	public void setSensorID(int sensorID) {
		this.sensorID = sensorID;
	}

	public void setSensorTypeID(SensorType sensorType) {
		this.sensorType = sensorType;
	}

	public void setUpdateSteps(int steps) {
		this.updateSteps = steps;
	}

	public List<SensorInterfererType> getSensorInterfererType() {
		return sensorInterfererType;
	}

	public void setSensorInterfererType(List<SensorInterfererType> sensorInterfererType) {
		this.sensorInterfererType = sensorInterfererType;
	}

	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + sensorID;
		result = prime * result + ((sensorInterfererType == null) ? 0 : sensorInterfererType.hashCode());
		result = prime * result + ((sensorType == null) ? 0 : sensorType.hashCode());
		result = prime * result + updateSteps;
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
		SensorHelper other = (SensorHelper) obj;
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (sensorID != other.sensorID) {
			return false;
		}
		if (sensorInterfererType == null) {
			if (other.sensorInterfererType != null) {
				return false;
			}
		} else if (!sensorInterfererType.equals(other.sensorInterfererType)) {
			return false;
		}
		if (sensorType != other.sensorType) {
			return false;
		}
		if (updateSteps != other.updateSteps) {
			return false;
		}
		return true;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public String toString() {
		return "SensorHelper [position=" + position + ", sensorID=" + sensorID + ", sensorType=" + sensorType
				+ ", updateSteps=" + updateSteps + ", sensorInterfererType=" + sensorInterfererType + ", nodeId="
				+ nodeId + "]";
	}
}
