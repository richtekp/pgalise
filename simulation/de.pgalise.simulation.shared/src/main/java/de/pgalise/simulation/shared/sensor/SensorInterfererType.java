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
 
package de.pgalise.simulation.shared.sensor;
/**
 * 
 * This enum contains all possible sensor interferer types.
 * Add them also into {@link SensorType} to make sure they can be added to sensor.
 * @author Marcus
 * @author Andreas
 * @author Timo
 */
public enum SensorInterfererType {
	GPS_ATMOSPHERE_INTERFERER, GPS_CLOCK_INTERFERER, GPS_RECEIVER_INTERFERER, GPS_WHITE_NOISE_INTERFERER, INDUCTION_LOOP_WHITE_NOISE_INTERFERER, INFRARED_WHITE_NOISE_INTERFERER, TOPO_RADAR_WHITE_NOISE_INTERFERER, PHOTOVOLTAIK_WHITE_NOISE_INTERFERER, SMART_METER_WHITE_NOISE_INTERFERER, WIND_POWER_WHITE_NOISE_INTERFERER, ANEMOMETER_WHITE_NOISE_INTERFERER, BAROMETER_WHITE_NOISE_INTERFERER, HYGROMETER_WHITE_NOISE_INTERFERER, LUXMETER_WHITE_NOISE_INTERFERER, PYRANOMETER_WHITE_NOISE_INTERFERER, RAINSENSOR_WHITE_NOISE_INTERFERER, THERMOMETER_WHITE_NOISE_INTERFERER, WIND_FLAG_WHITE_NOISE_INTERFERER
}
