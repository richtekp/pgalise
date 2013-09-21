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
 
package de.pgalise.simulation.traffic.model.vehicle;


import de.pgalise.simulation.shared.sensor.SensorHelper;

/**
 * Interface to provide methods to create different types of {@link Bus}.
 * 
 * @author Andreas
 * @version 1.0
 */
public interface BusFactory {

	/**
	 * Method to create a {@link Bus} with the given typeId.
	 * 
	 * @param id
	 *            ID of the {@link Bus}
	 * @param typeId
	 *            ID of the {@link Bus} type
	 * @return created {@link Bus}
	 */
	public Vehicle<BusData> createBus( String typeId, SensorHelper gpsSensor, SensorHelper infraredSensor);

	/**
	 * Method to create a random {@link Bus}.
	 * 
	 * @param id
	 *            ID of the {@link Bus}
	 * @return created {@link Bus}
	 */
	public Vehicle<BusData> createRandomBus( SensorHelper gpsSensor, SensorHelper infraredSensor);
}
