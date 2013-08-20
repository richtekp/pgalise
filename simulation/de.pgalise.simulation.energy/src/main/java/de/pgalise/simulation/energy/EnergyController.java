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


import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.geolocation.GeoLocation;
import de.pgalise.simulation.shared.sensor.SensorHelperSmartMeter;
import javax.vecmath.Vector2d;

/**
 * Interface for the energy controllers. An energy controller is able to give the energy consumption in kWh on a
 * specified point and timestamp. The energy consumption is asked by the smart meter sensors {@link SensorHelperSmartMeter}.
 * 
 * @author Timo
 */
public interface EnergyController extends Controller {

	/**
	 * Returns the current energy consumption on the given point.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param position
	 *            Position on the graph
	 * @param measureRadiusInMeter
	 *            the measure radius around the given position
	 * @return double value in KWh
	 */
	public double getEnergyConsumptionInKWh(long timestamp, GeoLocation position, int measureRadiusInMeter);

	/**
	 * Returns the current energy consumption on the given point.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @param position
	 *            Position
	 * @param measureRadiusInMeter
	 *            Radius
	 * @return double value
	 */
	public double getEnergyConsumptionInKWh(long timestamp, Vector2d position, int measureRadiusInMeter);
}
