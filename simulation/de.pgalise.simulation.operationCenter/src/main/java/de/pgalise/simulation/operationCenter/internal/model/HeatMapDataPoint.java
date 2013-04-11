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
 
package de.pgalise.simulation.operationCenter.internal.model;

import de.pgalise.simulation.shared.geolocation.GeoLocation;
/**
 * Not used so far.
 * @author Dennis
 */
public class HeatMapDataPoint {
	private GeoLocation lonlat;
	private double count;
	
	public HeatMapDataPoint(GeoLocation position, double count) {
		this.lonlat = position;
		this.count = count;
	}

	public GeoLocation getPosition() {
		return lonlat;
	}

	public void setPosition(GeoLocation position) {
		this.lonlat = position;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}
}
