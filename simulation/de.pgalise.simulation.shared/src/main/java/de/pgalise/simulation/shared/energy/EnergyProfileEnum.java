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
 
package de.pgalise.simulation.shared.energy;

/**
 * Enum for all various energy profiles
 * 
 * @author Andreas Rehfeldt
 */
public enum EnergyProfileEnum {

	/**
	 * G5 Bakery
	 */
	BAKERY("g5"),

	/**
	 * G6 Business on weekend
	 */
	BUSINESS_ON_WEEKEND("g6"),

	/**
	 * L0 Farm building
	 */
	FARM_BUILDING("l0"),

	/**
	 * L2 other farm buildings
	 */
	FARM_BUILDING_OTHER("l2"),

	/**
	 * L1 farm buildings with animal breeding
	 */
	FARM_BUILDING_WITH_ANIMAL_BREEDING("l1"),

	/**
	 * H0 Households
	 */
	HOUSEHOLD("h0"),

	/**
	 * G0 general industrial buildings
	 */
	INDUSTRY_GENERAL("g0"),

	/**
	 * G1 Industry on workdays (8:00 - 18:00)
	 */
	INDUSTRY_ON_WORKDAYS("g1"),

	/**
	 * G2 Industries with a consumption night
	 */
	INDUSTRY_WITH_CONSUMPTION_NIGHT("g2"),

	/**
	 * G3 Industries without night or weekend pauses
	 */
	INDUSTRY_WORKING("g3"),

	/**
	 * G4 Shops
	 */
	SHOP("g4");

	/**
	 * Property key for the file path
	 */
	private String key;

	/**
	 * Constructor
	 * 
	 * @param key
	 *            Property key for the file path
	 */
	EnergyProfileEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
