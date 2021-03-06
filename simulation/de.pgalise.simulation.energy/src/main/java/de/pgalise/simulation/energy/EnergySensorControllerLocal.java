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

import de.pgalise.simulation.energy.sensor.EnergySensorTypeEnum;
import de.pgalise.simulation.sensorFramework.SensorType;
import java.util.EnumSet;

import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorTypeEnum;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Local interface of static sensor controller.
 *
 * @author Timo
 */
public interface EnergySensorControllerLocal extends EnergySensorController {

	public static Set<SensorType> RESPONSIBLE_FOR_SENSOR_TYPES
		= new HashSet<SensorType>(Arrays.asList(
			EnergySensorTypeEnum.PHOTOVOLTAIK,
				EnergySensorTypeEnum.WINDPOWERSENSOR,
				WeatherSensorTypeEnum.THERMOMETER,
				WeatherSensorTypeEnum.WINDFLAG,
				WeatherSensorTypeEnum.BAROMETER,
				WeatherSensorTypeEnum.HYGROMETER,
				WeatherSensorTypeEnum.PYRANOMETER,
				WeatherSensorTypeEnum.RAIN,
				WeatherSensorTypeEnum.ANEMOMETER,
				WeatherSensorTypeEnum.LUXMETER,
				EnergySensorTypeEnum.SMARTMETER));
}
