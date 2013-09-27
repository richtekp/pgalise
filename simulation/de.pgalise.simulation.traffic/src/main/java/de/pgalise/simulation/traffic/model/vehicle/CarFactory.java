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

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.sensorFramework.SensorHelper;

/**
 * Interface to provide methods to create different types of {@link Car}.
 * 
 * @author Andreas
 * @version 1.0
 */
public interface CarFactory {

	/**
	 * Method to create a {@link Car} with the given typeId.
	 * 
	 * @param id
	 *            ID of the {@link Car}
	 * @param typeId
	 *            ID of the {@link Car} type
	 * @param color
	 *            Color of the {@link Car}
	 * @return created {@link Car}
	 */
	public Vehicle<CarData> createCar( String typeId, Color color, SensorHelper gpsSensor);

	/**
	 * Method to create a random {@link Car}.
	 * 
	 * @param id
	 *            ID of the {@link Car}
	 * @return created {@link Car}
	 */
	public Vehicle<CarData> createRandomCar( SensorHelper gpsSensor);

	public RandomSeedService getRandomSeedService();
}
