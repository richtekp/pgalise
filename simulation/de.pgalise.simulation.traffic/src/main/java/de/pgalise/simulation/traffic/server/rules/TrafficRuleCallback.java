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
 
package de.pgalise.simulation.traffic.server.rules;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Callback object which is passed into a {@link TrafficRule} by a {@link Vehicle}.
 * The {@link TrafficRule} then calls the methods to signalize its state to the {@link Vehicle}. So
 * the {@link Vehicle} is able to react (drive in, wait, drive out, etc.).
 * @author Marcus
 * @version 1.0 (Oct 28, 2012)
 */
public interface TrafficRuleCallback {
	
	/**
	 * Is called by {@link TrafficRule} when it's ready to enter by the {@link Vehicle}.
	 * @return true if {@link Vehicle} has actually entered the {@link TrafficRule}, otherwise false
	 */
	public boolean onEnter();
	
	/**
	 * Is called by {@link TrafficRule} when it's is ready to exit by the {@link Vehicle}.
	 * @return true if {@link Vehicle} has actually exited the {@link TrafficRule}, otherwise false
	 */
	public boolean onExit();
}
