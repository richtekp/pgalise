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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.SensorHelper;
/**
 * Default implementation of GPSTimeoutStrategy.
 * This works only, if every GPS sensor data will be passed to the operation center!
 * If you want InfoSphere to ignore some GPS sensor values (e.g. because they
 * have the same measure value), you have to implement another version.
 * @author Timo
 */
public class DefaultGPSTimeoutStrategy implements GPSSensorTimeoutStrategy {
	private static final Logger log = LoggerFactory.getLogger(DefaultGPSTimeoutStrategy.class);
	private int missedGPSUpdateStepsBeforeTimeout;
	private long interval;
	/**
	 * <Long = next possible timestamp (if this will not arrive, the sensor gets a timeout) , 
	 * Map<Integer = sensorID, SensorData> = contains all sensors that have the same last update timestamp
	 * and update step.
	 */
	private Map<Long, Map<Integer, SensorHelper>> timeoutSensorHelperMap;
	
	/**
	 * Default
	 */
	public DefaultGPSTimeoutStrategy() {}

	@Override
	public void init(long interval, int missedGPSUpdateStepsBeforeTimeout) {
		log.debug("Init with interval: " +interval +" and missedGPSUpdateStepsBeforeTimeout: " +missedGPSUpdateStepsBeforeTimeout);
		this.timeoutSensorHelperMap = new HashMap<>();
		this.missedGPSUpdateStepsBeforeTimeout = missedGPSUpdateStepsBeforeTimeout;
		this.interval = interval;
	}

	@Override
	public Collection<SensorData> processUpdateStep(long timestamp, Collection<SensorHelper> sensorHelpers) {
		List<SensorData> sensorsWithTimeout = new ArrayList<>();
		
		log.debug("Current timestamp: " +timestamp);
		
		for(SensorHelper sensor : sensorHelpers) {
			/* Add to map: */
			long nextTimestamp = timestamp + (sensor.getUpdateSteps() * this.interval);
			
			log.debug("Check sensor: " +sensor.getSensorID() +" with update steps: " +sensor.getUpdateSteps() +" next update will be on: " +nextTimestamp);
			
			Map<Integer, SensorHelper> tmpMap = this.timeoutSensorHelperMap.get(nextTimestamp);
			if(tmpMap == null) {
				tmpMap = new HashMap<>();
				this.timeoutSensorHelperMap.put(nextTimestamp, tmpMap);
			}
			tmpMap.put(sensor.getSensorID(), sensor);
			
			/* Remove from old map, if there is any entry: */
			long possibleOldTimestamp = timestamp;
			Map<Integer, SensorHelper> possibleOldMap = this.timeoutSensorHelperMap.get(possibleOldTimestamp);
			if(possibleOldMap != null) {
				possibleOldMap.remove(sensor.getSensorID());
				if(possibleOldMap.isEmpty()) {
					this.timeoutSensorHelperMap.remove(possibleOldTimestamp);
				}
			}
		}
		
		/* Check for timeouts and remove them from map: */
		Map<Integer, SensorHelper> timeoutMap = this.timeoutSensorHelperMap.remove(timestamp - (this.interval * this.missedGPSUpdateStepsBeforeTimeout));
		if(timeoutMap != null) {
			for(SensorHelper sensorHelper : timeoutMap.values()) {
				sensorsWithTimeout.add(new SensorData(sensorHelper.getSensorType().getSensorTypeId(), sensorHelper.getSensorID()));
			}
		}
		return sensorsWithTimeout;
	}
}
