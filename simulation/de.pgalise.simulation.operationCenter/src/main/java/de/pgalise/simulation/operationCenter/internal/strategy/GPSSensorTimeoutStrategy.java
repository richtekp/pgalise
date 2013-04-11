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
 
package de.pgalise.simulation.operationCenter.internal.strategy;

import java.util.Collection;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.shared.sensor.SensorHelper;

/**
 * Strategy for GPS timeouts.
 * This will be init and than called on every update step. For every update step
 * the gps sensor timeout strategy needs to find the gps sensors with a time out.
 * @author Timo
 */
public interface GPSSensorTimeoutStrategy {
	/**
	 * Inits the GPS timeout strategy.
	 * @param interval
	 * 			what's the simulation interval in milliseconds?
	 * @param missedGPSUpdateStepsBeforeTimeout
	 * 			how many update steps can be missed?
	 */
	public void init(long interval, int missedGPSUpdateStepsBeforeTimeout);
	
	/**
	 * Processes the update step and returns every sensor with a timeout.
	 * @param timestamp
	 * @param sensorHelpers
	 * @return empty collection if there is no sensor with a timeout.
	 */
	public Collection<SensorData> processUpdateStep(long timestamp, Collection<SensorHelper> sensorHelpers);
}
