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
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of GPSTimeoutStrategy. This works only, if every GPS
 * sensor data will be passed to the operation center! If you want InfoSphere to
 * ignore some GPS sensor values (e.g. because they have the same measure
 * value), you have to implement another version.
 *
 * @author Timo
 */
public class DefaultGPSTimeoutStrategy implements GPSSensorTimeoutStrategy {

	private static final Logger log = LoggerFactory.getLogger(
		DefaultGPSTimeoutStrategy.class);
	private int missedGPSUpdateStepsBeforeTimeout;
	private long interval;
	/**
	 * <Long = next possible timestamp (if this will not arrive, the sensor gets a
	 * timeout) , Map<Integer = sensorID, SensorData> = contains all sensors that
	 * have the same last update timestamp and update step.
	 */
	private Map<Long, Set<GpsSensor>> timeoutToSensorsMap;

	/**
	 * Default
	 */
	public DefaultGPSTimeoutStrategy() {
	}

	@Override
	public void init(long interval,
		int missedGPSUpdateStepsBeforeTimeout) {
		log.debug(
			"Init with interval: " + interval + " and missedGPSUpdateStepsBeforeTimeout: " + missedGPSUpdateStepsBeforeTimeout);
		this.timeoutToSensorsMap = new HashMap<>();
		this.missedGPSUpdateStepsBeforeTimeout = missedGPSUpdateStepsBeforeTimeout;
		this.interval = interval;
	}

	@Override
	public Set<GpsSensor> processUpdateStep(long timestamp,
		Set<GpsSensor> sensors) {

		log.debug("Current timestamp: " + timestamp);

		for (GpsSensor sensor : sensors) {
			/* Add to map: */
			long nextUpdateTimestamp = timestamp + (sensor.getUpdateSteps() * this.interval);

			log.debug(
				"Check sensor: " + sensor.getId() + " with update steps: " + sensor.
				getUpdateSteps() + " next update will be on: " + nextUpdateTimestamp);

			Set<GpsSensor> tmpMap = this.timeoutToSensorsMap.get(
				nextUpdateTimestamp);
			if (tmpMap == null) {
				tmpMap = new HashSet<>();
				this.timeoutToSensorsMap.put(nextUpdateTimestamp,
					tmpMap);
			}
			tmpMap.add(
				sensor);

			/* Remove from old map, if there is any entry: */
			long possibleOldTimestamp = timestamp;
			Set<GpsSensor> possibleOldSensors = this.timeoutToSensorsMap.
				get(possibleOldTimestamp);
			if (possibleOldSensors != null) {
				possibleOldSensors.remove(sensor);
				if (possibleOldSensors.isEmpty()) {
					this.timeoutToSensorsMap.remove(possibleOldTimestamp);
				}
			}
		}

		/* Check for timeouts and remove them from map: */
		Set<GpsSensor> sensorsWithTimeout;
		long lastCheckpointTimestamp = timestamp - (this.interval * this.missedGPSUpdateStepsBeforeTimeout);
		Set<GpsSensor> missedSensors = this.timeoutToSensorsMap.remove(lastCheckpointTimestamp);
		if (missedSensors != null) {
			sensorsWithTimeout = new HashSet<>(missedSensors);
		}else {
			sensorsWithTimeout = new HashSet<>();
		}
		return sensorsWithTimeout;
	}
}
