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
 
package de.pgalise.simulation.energy;

import java.util.List;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.entity.BaseCoordinate;

/**
 * An energy event strategy changes the energy consumption by considering given instances of {@link EnergyEvent}.
 * Because the calculation, if an event shall be considered or not can be done on several ways, this is a strategy.
 * @author Timo
 */
public interface EnergyEventStrategy {
	/**
	 * Updates the energy event strategy.
	 * @param timestamp
	 * 			the current simulation time stamp
	 * @param energyEventList
	 * 			energy events
	 */
	public void update(long timestamp, List<EnergyEvent> energyEventList);
	
	/**
	 * Returns the energy consumption in KWH with consideration of the energy events.
	 * @param timestamp
	 * 			the current simulation time stamp
	 * @param key
	 * 			the used energy profile
	 * @param geoLocation
	 * 			the position on the graph/map
	 * @param consumptionWithoutEvents
	 * 			the consumption before considering the energy events
	 * @return
	 */
	public double getEnergyConsumptionInKWh(long timestamp, EnergyProfileEnum key, BaseCoordinate geoLocation, double consumptionWithoutEvents);
}
