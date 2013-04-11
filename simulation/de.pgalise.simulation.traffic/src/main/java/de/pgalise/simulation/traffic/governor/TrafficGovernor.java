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
 
package de.pgalise.simulation.traffic.governor;

/**
 * Interface that returns the percentage of active vehicles to a given time stamp
 * 
 * @author Marcus
 */
public interface TrafficGovernor {

	/**
	 * Returns the percentage of active cars to the passed time.
	 * 
	 * @param simTime
	 *            the time to which the active cars shall be extracted
	 * @return the percentage of active cars to the passed time
	 */
	public double getPercentageOfActiveCars(long simTime);

	public double getPercentageOfActiveBicycles(long simTime);

	public double getPercentageOfActiveTrucks(long simTime);

	public double getPercentageOfActiveMotorcycles(long simTime);

	public double getAverageBusManningPercentage(long simTime);
}
