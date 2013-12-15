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

import de.pgalise.simulation.sensorFramework.output.Output;

/**
 * Interface to provide methods to create different types of {@link Bus}.
 *
 * @author Andreas
 * @version 1.0
 */
public interface BusFactory extends VehicleFactory {

	/**
	 * Method to create a {@link Bus} with the given typeId.All position related
	 * data is <code>null</code>
	 *
	 * @param id ID of the {@link Bus}
	 * @param typeId ID of the {@link Bus} type
	 * @return created {@link Bus}
	 */
	public Vehicle<BusData> createBus(Output output);

	/**
	 * Method to create a random {@link Bus}.All position related data is
	 * <code>null</code>
	 *
	 * @param id ID of the {@link Bus}
	 * @return created {@link Bus}
	 */
	public Vehicle<BusData> createRandomBus(Output output);
}
