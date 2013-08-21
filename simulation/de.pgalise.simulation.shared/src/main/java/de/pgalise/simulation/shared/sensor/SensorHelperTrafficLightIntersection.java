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
 * Sensorhelper for traffic light intersection
 * @author Marcus
 */
public class SensorHelperTrafficLightIntersection extends SensorHelper {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8614545583840698333L;
	
	private List<Integer> trafficLightIds;

	/**
	 * Constructor
	 * @param sensorID
	 * 			the sensor id
	 * @param position
	 * 			the position of the traffic light
	 * @param trafficLightIds
	 * 			the IDs of the traffic light
	 * @param sensorInterfererList
	 * 			the interferes for the traffic light
	 * @param nodeId
	 */
	public SensorHelperTrafficLightIntersection(
			int sensorID, 
			Coordinate position, 
			List<Integer> trafficLightIds,
			List<SensorInterfererType> sensorInterfererList, 
			String nodeId) {
		super(sensorID, position, SensorType.TRAFFIC_LIGHT_INTERSECTION, sensorInterfererList, nodeId);
		this.trafficLightIds = trafficLightIds;
	}

	public List<Integer> getTrafficLightIds() {
		return trafficLightIds;
	}

	public void setTrafficLightIds(List<Integer> trafficLightIds) {
		this.trafficLightIds = trafficLightIds;
	}
}
