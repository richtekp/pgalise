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
 
package de.pgalise.simulation.weather.internal.dataloader.entity;

import de.pgalise.simulation.weather.dataloader.WeatherMap;

/**
 * Map that do nothing (no modification).
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 23, 2012)
 */
public class StationDataMap extends WeatherMap {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -2597132635843869636L;

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.database.Weather#deployChanges()
	 */
	@Override
	public void deployChanges() {
		// Do nothing
	}

}
