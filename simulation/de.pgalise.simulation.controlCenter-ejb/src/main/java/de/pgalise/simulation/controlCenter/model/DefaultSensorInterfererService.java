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
 
package de.pgalise.simulation.controlCenter.internal.util.service;

import de.pgalise.simulation.sensorFramework.SensorType;
import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
/**
 * The default implementation of {@link SensorInterfererService}.
 * It just returns every possible sensor interferer as composite.
 * @author Timo
 */
public class DefaultSensorInterfererService implements SensorInterfererService {

	@Override
	public List<SensorInterfererType> getSensorInterferes(SensorType sensorType, boolean isWithSensorInterferer) {

		List<SensorInterfererType> sensorInterfererTypeList = new LinkedList<>();
		
		if(isWithSensorInterferer) {
			sensorInterfererTypeList.addAll(sensorType.getSensorInterfererTypeSet());
		}

		return sensorInterfererTypeList;
	}
}
