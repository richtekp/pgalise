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
 *//* 
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

import de.pgalise.simulation.service.ControllerStatusEnum;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Ignores the gate messages. Only for testing or if you can not use InfoSphere
 * or other DSMS, which can not handle a gate.
 *
 * @author Timo
 *
 */
public class NoGPSGateStrategy extends Identifiable implements
	GPSGateStrategy {

	@Override
	public void init(TrafficInitParameter param) throws
		IllegalStateException {
	}

	@Override
	public void reset() throws IllegalStateException {
	}

	@Override
	public void start(TrafficStartParameter param) throws IllegalStateException {
	}

	@Override
	public void stop() throws IllegalStateException {
	}

	@Override
	public void update(EventList<Event> simulationEventList)
		throws IllegalStateException {
	}

	@Override
	public ControllerStatusEnum getStatus() {
		return null;
	}

	@Override
	public void createSensor(GpsSensor sensor) {
	}

	@Override
	public void createSensors(Set<GpsSensor> sensors) {
	}

	@Override
	public void deleteSensor(GpsSensor sensor) {
	}

	@Override
	public void deleteSensors(Set<GpsSensor> sensors) {
	}

	@Override
	public boolean isActivated(GpsSensor sensor) {
		return false;
	}

	@Override
	public void handleGateMessage(Map<Integer, Double> gateInformationMap)
		throws UnknownHostException, IOException {
	}

	@Override
	public String getName() {
		return NoGPSGateStrategy.class.getName();
	}

	/**
	 *
	 * @return an unmodifiable {@link Set}
	 */
	@Override
	public Set<GpsSensor> getAllManagedSensors() {
		return Collections.unmodifiableSet(new HashSet(0));
	}
}
