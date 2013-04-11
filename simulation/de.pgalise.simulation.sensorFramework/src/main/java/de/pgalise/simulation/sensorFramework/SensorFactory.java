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
 
package de.pgalise.simulation.sensorFramework;

import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorType;

/**
 * Factory to create sensors
 * 
 * @author marcus
 */
public interface SensorFactory {

	/**
	 * Creates a sensor of a specific type.
	 * 
	 * @param sensorType
	 *            Type of the sensor to create
	 * @return sensor Returns the created sensor
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Sensor createSensor(SensorHelper sensorHelper, EnumSet<SensorType> allowedTypes)
			throws InterruptedException, ExecutionException;

	/**
	 * Returns the Output of the {@link SensorFactory}
	 * 
	 * @return the Output of the {@link SensorFactory}
	 */
	public Output getOutput();
}
