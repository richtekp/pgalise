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
 
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.energy.sensor.EnergySensorTypeEnum;
import de.pgalise.simulation.sensorFramework.SensorType;
import java.util.EnumSet;

import de.pgalise.simulation.sensorFramework.SensorTypeEnum;

/**
 * Local interface of static sensor controller.
 * @author Timo
 */
public interface StaticSensorControllerLocal extends StaticSensorController {
	public static EnumSet<SensorType> RESPONSIBLE_FOR_SENSOR_TYPES = 
			EnumSet.of(EnergySensorTypeEnum.PHOTOVOLTAIK, EnergySensorTypeEnum.WINDPOWERSENSOR,
					SensorTypeEnum.THERMOMETER, SensorTypeEnum.WINDFLAG, SensorTypeEnum.BAROMETER,
					SensorTypeEnum.HYGROMETER, SensorTypeEnum.PYRANOMETER, SensorTypeEnum.RAIN,
					SensorTypeEnum.ANEMOMETER, SensorTypeEnum.LUXMETER, SensorTypeEnum.SMARTMETER);
}
