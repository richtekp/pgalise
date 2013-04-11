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
 
package de.pgalise.simulation.shared.city;

/**
 * Enum with city location attributes.
 * This is useful to determine the correct weather.
 * 
 * @author Andreas Rehfeldt
 */
public enum CityLocationEnum {

	/**
	 * Downtown without river
	 */
	DOWNTOWN_NORIVER(4),

	/**
	 * Downtown with river
	 */
	DOWNTOWN_RIVER(5),

	/**
	 * Grassed area/park area
	 */
	GREEN_AREA(3),

	/**
	 * Industrial area
	 */
	INDUSTRIAL_ZONE(6),

	/**
	 * Industrial
	 */
	INDUSTRY(2),

	/**
	 * Meadow/range land
	 */
	MEADOW(7),

	/**
	 * open land / outdoor
	 */
	OPEN_LAND(0),

	/**
	 * Suburb
	 */
	SUBURBAN(1);

	/**
	 * ID
	 */
	private int id;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	CityLocationEnum(int id) {
		this.setId(id);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
