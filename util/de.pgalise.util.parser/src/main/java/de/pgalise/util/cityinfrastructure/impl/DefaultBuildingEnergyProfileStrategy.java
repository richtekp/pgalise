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
 
package de.pgalise.util.cityinfrastructure.impl;

import de.pgalise.simulation.shared.city.Building;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;

/**
 * Default implementation of {@link BuildingEnergyProfileStrategy}.
 * It uses the properties "too-small-size-for-house" and "min-size-for-industry"
 * from "properties.props" to find the best energy profile.
 * 
 * @author Timo
 */
public class DefaultBuildingEnergyProfileStrategy implements BuildingEnergyProfileStrategy {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8937195673955676784L;

	/**
	 * If nothing else can be found: a building smaller than this gets the profile household and a building bigger than
	 * this gets the profile industry_general
	 */
	private double minSquareMetersForIndustry = 100;

	/**
	 * If a building is too small, the strategy will return nothing.
	 */
	private double tooSmallBuilding = 10;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public DefaultBuildingEnergyProfileStrategy() throws IOException {
		try (InputStream propertiesInputStream = DefaultBuildingEnergyProfileStrategy.class
				.getResourceAsStream("/properties.props")) {
			Properties properties = new Properties();
			properties.load(propertiesInputStream);
			this.minSquareMetersForIndustry = Double.valueOf(properties.getProperty("too-small-size-for-house"));
			this.tooSmallBuilding = Double.valueOf(properties.getProperty("min-size-for-industry"));
		} 
	}

	@Override
	public EnergyProfileEnum getEnergyProfile(Building building) {
		if (building.getSquareMeter() <= tooSmallBuilding) {
			throw new RuntimeException("Building is too small");
		}
		if (building.getPublicTransport() != null && building.getPublicTransport().equalsIgnoreCase("stop_position")) {
			throw new RuntimeException("No energy profile found!");
		}

		if (building.getLandUseArea() != null) {
			if (building.getLandUseArea().equalsIgnoreCase("farmland")
					|| building.getLandUseArea().equalsIgnoreCase("farmyard")) {

				return EnergyProfileEnum.FARM_BUILDING;

			} else if (building.getLandUseArea().equalsIgnoreCase("industrial")) {

				return EnergyProfileEnum.INDUSTRY_GENERAL;

			} else if (building.getLandUseArea().equalsIgnoreCase("retail")) {

				return EnergyProfileEnum.SHOP;

			} else if (building.getLandUseArea().equalsIgnoreCase("military")) {

				return EnergyProfileEnum.INDUSTRY_GENERAL;

			} else if (building.getLandUseArea().equalsIgnoreCase("residential")) {

				return EnergyProfileEnum.HOUSEHOLD;

			} else if (building.getLandUseArea().equalsIgnoreCase("commercial")) {

				return EnergyProfileEnum.INDUSTRY_GENERAL;

			}
		}

		if (building.getAmenity() != null) {
			if (building.getAmenity().equalsIgnoreCase("kindergarten")) {

				return EnergyProfileEnum.INDUSTRY_ON_WORKDAYS;

			} else if (building.getAmenity().equalsIgnoreCase("pharmacy")) {

				return EnergyProfileEnum.INDUSTRY_WORKING;

			} else if (building.getAmenity().equalsIgnoreCase("restaurant")) {

				return EnergyProfileEnum.BUSINESS_ON_WEEKEND;

			} else if (building.getAmenity().equalsIgnoreCase("parking")
					|| building.getAmenity().equalsIgnoreCase("bicycle_parking")) {

				return EnergyProfileEnum.INDUSTRY_GENERAL;

			} else if (building.getAmenity().equalsIgnoreCase("car_rental")) {

				return EnergyProfileEnum.INDUSTRY_ON_WORKDAYS;

			}
		}

		if (building.getShop() != null) {

			return EnergyProfileEnum.SHOP;
		}

		if (building.getSquareMeter() < minSquareMetersForIndustry) {
			return EnergyProfileEnum.HOUSEHOLD;
		}

		return EnergyProfileEnum.INDUSTRY_GENERAL;
	}
}
