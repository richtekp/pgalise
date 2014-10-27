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
package de.pgalise.util.cityinfrastructure;

import de.pgalise.simulation.shared.tag.AmenityTagEnum;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.tag.LanduseTagEnum;
import de.pgalise.simulation.shared.tag.PublicTransportTagEnum;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;

/**
 * Default implementation of {@link BuildingEnergyProfileStrategy}. It uses the
 * properties "too-small-size-for-house" and "min-size-for-industry" from
 * "properties.props" to find the best energy profile.
 *
 * @author Timo
 */
public class DefaultBuildingEnergyProfileStrategy implements
  BuildingEnergyProfileStrategy {

  /**
   * Serial
   */
  private static final long serialVersionUID = -8937195673955676784L;

  /**
   * If nothing else can be found: a building smaller than this gets the profile
   * household and a building bigger than this gets the profile industry_general
   */
  private double minSquareMetersForIndustry = 100;

  /**
   * If a building is too small, the strategy will return nothing.
   */
  private double tooSmallBuilding = 10;

  private final static String PROPERTIES_FILE_PATH = "energy_profile_strategy.properties";

  /**
   * Constructor
   *
   */
  public DefaultBuildingEnergyProfileStrategy() {
    try (InputStream propertiesInputStream = Thread.currentThread().
      getContextClassLoader().
      getResourceAsStream(PROPERTIES_FILE_PATH)) {
      Properties properties = new Properties();
      properties.load(propertiesInputStream);
      this.minSquareMetersForIndustry = Double.valueOf(properties.getProperty(
        "too-small-size-for-house"));
      this.tooSmallBuilding = Double.valueOf(properties.getProperty(
        "min-size-for-industry"));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public EnergyProfileEnum getEnergyProfile(Building building) {
    if (building.getBoundary().getBoundary().getArea() <= tooSmallBuilding) {
      throw new IllegalArgumentException("Building is too small");
    }
    if (building.getPublicTransportTags() != null && building.
      getPublicTransportTags().contains(PublicTransportTagEnum.STOP_POSITION.
        getStringValue())) {
      throw new IllegalArgumentException("No energy profile found!");
    }

    if (building.getLanduseTags() != null) {
      if (building.getLanduseTags().contains(LanduseTagEnum.FARMLAND.
        getStringValue())
        || building.getLanduseTags().contains(LanduseTagEnum.FARMYARD.
          getStringValue())) {

        return EnergyProfileEnum.FARM_BUILDING;

      } else if (building.getLanduseTags().contains(LanduseTagEnum.INDUSTRY.
        getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_GENERAL;

      } else if (building.getLanduseTags().contains(LanduseTagEnum.RETAIL.
        getStringValue())) {

        return EnergyProfileEnum.SHOP;

      } else if (building.getLanduseTags().contains(LanduseTagEnum.MILITARY.
        getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_GENERAL;

      } else if (building.getLanduseTags().contains(LanduseTagEnum.RESIDENTIAL.
        getStringValue())) {

        return EnergyProfileEnum.HOUSEHOLD;

      } else if (building.getLanduseTags().contains(LanduseTagEnum.COMMERCIAL.
        getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_GENERAL;

      }
    }

    if (building.getAmenityTags() != null) {
      if (building.getAmenityTags().contains(AmenityTagEnum.KINDERGARTEN.
        getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_ON_WORKDAYS;

      } else if (building.getAmenityTags().contains(AmenityTagEnum.PHARMACY.
        getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_WORKING;

      } else if (building.getAmenityTags().contains(AmenityTagEnum.RESTAURANT.
        getStringValue())) {

        return EnergyProfileEnum.BUSINESS_ON_WEEKEND;

      } else if (building.getAmenityTags().contains(AmenityTagEnum.KINDERGARTEN.
        getStringValue())
        || building.getAmenityTags().contains(AmenityTagEnum.BICYKLE_PARKING.
          getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_GENERAL;

      } else if (building.getAmenityTags().contains(AmenityTagEnum.CAR_RENTAL.
        getStringValue())) {

        return EnergyProfileEnum.INDUSTRY_ON_WORKDAYS;

      }
    }

    if (building.getShopTags() != null) {

      return EnergyProfileEnum.SHOP;
    }

    if (building.getBoundary().getBoundary().getArea() < minSquareMetersForIndustry) {
      return EnergyProfileEnum.HOUSEHOLD;
    }

    return EnergyProfileEnum.INDUSTRY_GENERAL;
  }
}
