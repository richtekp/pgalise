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

import de.pgalise.simulation.service.StatusEnum;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.sensor.SensorHelper;
/**
 * Ignores the gate messages.
 * Only for testing or if you can not use InfoSphere or other DSMS, which
 * can not handle a gate.
 * @author Timo
 *
 */
public class NoGPSGateStrategy implements GPSGateStrategy {

	@Override
	public void init(InitParameter param) throws InitializationException,
			IllegalStateException {}

	@Override
	public void reset() throws IllegalStateException {}

	@Override
	public void start(StartParameter param) throws IllegalStateException {}

	@Override
	public void stop() throws IllegalStateException {}

	@Override
	public void update(SimulationEventList simulationEventList)
			throws IllegalStateException {}

	@Override
	public StatusEnum getStatus() {
		return null;
	}

	@Override
	public void createSensor(SensorHelper sensor) throws SensorException {}

	@Override
	public void createSensors(Collection<SensorHelper> sensors)
			throws SensorException {}

	@Override
	public void deleteSensor(SensorHelper sensor) throws SensorException {}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors)
			throws SensorException {}

	@Override
	public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
		return false;
	}

	@Override
	public void handleGateMessage(Map<Integer, Double> gateInformationMap)
			throws UnknownHostException, IOException {}

	@Override
	public String getName() {
		return NoGPSGateStrategy.class.getName();
	}
}
