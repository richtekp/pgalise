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
 
package de.pgalise.simulation.energy.profile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the profile loader
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 27, 2012)
 */
public interface EnergyProfileLoader {

	/**
	 * Loads an energy profile from a file
	 * 
	 * @param startTimestamp
	 *            Start timestamp of the load
	 * @param endTimestamp
	 *            End timestamp of the load
	 * @param energyProfileInputStream
	 *            CSV file
	 * @return energy profile with data
	 * @throws IOException
	 *             File can not be loaded
	 */
	public EnergyProfile loadProfile(long startTimestamp, long endTimestamp, InputStream energyProfileInputStream) throws IOException;

}
