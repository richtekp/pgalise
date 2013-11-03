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
	DOWNTOWN_NORIVER,

	/**
	 * Downtown with river
	 */
	DOWNTOWN_RIVER,

	/**
	 * Grassed area/park area
	 */
	GREEN_AREA,

	/**
	 * Industrial area
	 */
	INDUSTRIAL_ZONE,

	/**
	 * Industrial
	 */
	INDUSTRY,

	/**
	 * Meadow/range land
	 */
	MEADOW,

	/**
	 * open land / outdoor
	 */
	OPEN_LAND,

	/**
	 * Suburb
	 */
	SUBURBAN;

}
