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

import java.awt.Color;

import de.pgalise.simulation.sensorFramework.SensorHelper;

/**
 * Interface to provide methods to create different types of {@link Motorcycle}.
 * 
 * @author Andreas
 * @version 1.0
 */
public interface MotorcycleFactory {

	/**
	 * Method to create a {@link Motorcycle} with the given typeId.
	 * 
	 * @param id
	 *            ID of the {@link Motorcycle}
	 * @param typeId
	 *            ID of the {@link Motorcycle} type
	 * @param color
	 *            Color of the {@link Motorcycle}
	 * @return created {@link Motorcycle}
	 */
	public Vehicle<MotorcycleData> createMotorcycle( String typeId, Color color, SensorHelper gpsSensor);

	/**
	 * Method to create a random {@link Motorcycle}.
	 * 
	 * @param id
	 *            ID of the {@link Motorcycle}
	 * @return created {@link Motorcycle}
	 */
	public Vehicle<MotorcycleData>  createRandomMotorcycle( SensorHelper gpsSensor);
}
