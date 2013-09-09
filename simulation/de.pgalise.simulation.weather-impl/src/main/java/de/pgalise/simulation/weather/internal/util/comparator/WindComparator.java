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
 
package de.pgalise.simulation.weather.internal.util.comparator;

import de.pgalise.simulation.weather.model.StationData;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for wind velocity
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 6, 2012)
 */
public class WindComparator implements Comparator<StationData>, Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -2071861916907372961L;

	@Override
	public int compare(StationData o1, StationData o2) {
		float thisValue = o1.getWindVelocity();
		float anotherValue = o2.getWindVelocity();

		return (thisValue < anotherValue ? -1 : (thisValue == anotherValue ? 0 : 1));
	}

}
