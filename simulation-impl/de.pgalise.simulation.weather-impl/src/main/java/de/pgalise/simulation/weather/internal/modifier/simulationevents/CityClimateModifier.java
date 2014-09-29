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
package de.pgalise.simulation.weather.internal.modifier.simulationevents;

import de.pgalise.simulation.shared.CityLocationEnum;
import de.pgalise.simulation.shared.entity.City;
import java.util.Properties;

import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.modifier.WeatherSimulationEventModifier;
import de.pgalise.simulation.weather.modifier.WeatherStrategy;
import de.pgalise.simulation.weather.util.DateConverter;
import javax.measure.Measure;
import javax.measure.unit.SI;

/**
 * Changes the values according to the changes of the city climate
 * ({@link WeatherMapModifier} and {@link WeatherStrategy}).<br />
 * <br />
 * The file weather_decorators.properties describes the default properties for
 * the implemented modifier. If no parameters are given in the constructor of an
 * implemented modifier, the standard properties of the file will be used.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (29.07.2012)
 */
public class CityClimateModifier extends WeatherSimulationEventModifier {

  /**
   * Event type
   */
  public static final WeatherEventTypeEnum TYPE = WeatherEventTypeEnum.CITYCLIMATE;

  /**
   * Order id
   */
  public static final int ORDER_ID = 2;

  /**
   * Population of Oldenburg (Date 2011). See:
   * http://de.wikipedia.org/wiki/Oldenburg_%28Oldenburg%29
   */
  private static final int POPULATION_OF_OL = 162481;

  /**
   * Serial
   */
  private static final long serialVersionUID = -2545514269078121147L;

  /**
   * Option, if it is a rain day
   */
  private boolean rainDay = false;

  /**
   * Rain interval of the day (end)
   */
  private int rainDayEnds;

  /**
   * Rain interval of the day (start)
   */
  private int rainDayStart;

  /**
   * Constructor
   *
   * @param city
   * @param seed Seed for random generators
   * @param props Properties
   * @param weatherLoader
   */
  public CityClimateModifier(City city,
    long seed,
    Properties props,
    WeatherLoader weatherLoader) {
    super(city,
      seed,
      props,
      weatherLoader);
  }

  /**
   * Constructor
   *
   * @param seed Seed for random generators
   * @param weatherLoader
   */
  public CityClimateModifier(City city,
    long seed,
    WeatherLoader weatherLoader) {
    super(city,
      seed,
      weatherLoader);
  }

  /**
   * Constructor
   *
   * @param map WeatherMap
   * @param seed Seed for random generators
   * @param weatherLoader
   */
  public CityClimateModifier(City city,
    WeatherMap map,
    long seed,
    WeatherLoader weatherLoader) {
    super(city,
      map,
      seed,
      weatherLoader);
  }

  /*
   * (non-Javadoc)
   * @see de.pgalise.simulation.weather.database.Weather#deployChanges()
   */
  @Override
  public void deployChanges() {
    // Calculate general informations
    float temperaturDiff = (float) this.getUHI(this.getCity().getPopulation());
    CityLocationEnum cityEnum = (this.getCity().isNearRiver() || this.getCity().
      isNearSea()) ? CityLocationEnum.DOWNTOWN_RIVER
      : CityLocationEnum.DOWNTOWN_NORIVER;

    // Change all values
    for (AbstractStationData weather : this.getMap().values()) {
      long time = weather.getMeasureTime().getTime();

      float temp = weather.getTemperature().floatValue(SI.CELSIUS);
      weather.setTemperature(Measure.valueOf(temp - temperaturDiff,
        SI.CELSIUS));
      weather.setPerceivedTemperature(
        weather.getPerceivedTemperature() - temperaturDiff);

      weather.setRadiation(this.getRadiation(weather.getRadiation(),
        this.getCity().getPopulation()));

      weather.setRelativHumidity(this.getRelativHumidity(weather.
        getRelativHumidity(),
        time));

      weather.setWindVelocity(this.getWindVelocity(weather.getWindVelocity(),
        cityEnum));

      weather.setPrecipitationAmount(this.getPrecipitationAmount(weather.
        getPrecipitationAmount(),
        time));
    }

    // Super class
    super.deployChanges();
  }

  /**
   * Changes the precipitation amount, which is linked to the season. Source:
   * "Diese Aufwinde verstärken die Wolkenbildung und die Anzahl der
   * Niederschläge. So ist gegenüber dem Umland der Bewölkungsgrad um 5 bis
   * 10% höher, Nebel im Winter um 100% und im Sommer 20 bis 30% häufiger
   * sowie die Anzahl der Tage mit Niederschlägen um 10% höher."
   *
   * @param precipitationAmount precipitation amount
   * @param time Timestamp
   * @return Changed value
   */
  public float getPrecipitationAmount(float precipitationAmount,
    long time) {
    int month = DateConverter.getMonthOfYear(time);

    // It is summer?
    if ((month >= 5) && (month <= 7)) {

      int hour = DateConverter.getHourOfDay(time);

      // Rain day?
      if (this.rainDay && (hour >= this.rainDayStart) && (hour <= this.rainDayEnds)) {

        // Change value
        if (precipitationAmount == 0) {
          precipitationAmount += 1.2; // Lower amount
        } else {
          precipitationAmount *= 1.05; // Add 5%
        }
      }
    }

    return precipitationAmount;
  }

  /**
   * Changes the radiation, which is linked to the population. Source:
   * http://www.atmosphere.mpg.de/enid/25ad7f28526e46866b8548ca86d6a25b,0/2__Stadtklima/-_Strahlung_42e.html
   * "Die Gesamtstrahlung der Sonne (direkte und diffuse Sonnenstrahlung) kann
   * in einer Stadt durch Luftverschmutzung und erhöhte Bewölkung um 10-20%
   * reduziert sein."
   *
   * @param radiation Radiation
   * @param population population of the city
   * @return Changed value
   */
  public int getRadiation(int radiation,
    int population) {
    // Assumption: The limits are random
    if (population < CityClimateModifier.POPULATION_OF_OL) {
      radiation *= 1.05;
    } else if ((population >= CityClimateModifier.POPULATION_OF_OL) && (population < 500000)) {
      radiation /= 1.05;
    } else if ((population >= 500000) && (population < 1000000)) {
      radiation /= 1.1;
    } else {
      radiation /= 1.15;
    }

    // LIMIT: Can not be higher than the radiation of the sun
    if (radiation >= 1365) {
      radiation /= 1.02;
    }

    return radiation;
  }

  /**
   * Changes the relativ humidity, which is linked to the season. Source: "Die
   * Luftfeuchtigkeit in der Stadt ist aufgrund des hohen Anteils von bebauter
   * Fläche und der Einstrahlungsverhältnisse im Sommer um 5 bis 7% geringer
   * als außerhalb, während im Winter die Unterschiede größtenteils
   * verschwinden")
   *
   * @param relativHumidity relativ humidity
   * @param time Timestamp
   * @return Changed value
   */
  public float getRelativHumidity(float relativHumidity,
    long time) {
    int month = DateConverter.getMonthOfYear(time);

    // It is summer?
    if ((month >= 5) && (month <= 7)) {
      relativHumidity /= (month == 6) ? 1.04 : 1.02;
    }

    // Important: Can not be under zero
    if (relativHumidity < 0) {
      relativHumidity = 0;
    }

    return relativHumidity;
  }

  @Override
  public WeatherEventTypeEnum getType() {
    return CityClimateModifier.TYPE;
  }

  /**
   * Returns the max uhi value (urban heat island).
   *
   * @param population population of the city
   * @return max UHI
   */
  public double getUHI(int population) {
    // Calculation for European cities: UHImax = 2.01 * log(P) - 4.06
    double uhi = (2.01f * Math.log10(population)) - 4.06f;

    // UHI of Oldenburg
    double uhi_ol = (2.01f * Math.log10(CityClimateModifier.POPULATION_OF_OL)) - 4.06f;

    return uhi - uhi_ol;
  }

  /**
   * Returns the wind velocity, which is linked to the city locations.
   *
   * @param windVelocity wind velocity
   * @param cityLocation city location
   * @return Changed value
   */
  public float getWindVelocity(float windVelocity,
    CityLocationEnum cityLocation) {
    float refvalue = 0;

    if (cityLocation.equals(CityLocationEnum.SUBURBAN)) {
      refvalue = 0.69f;
    } else if (cityLocation.equals(CityLocationEnum.INDUSTRY)) {
      refvalue = 0.60f;
    } else if (cityLocation.equals(CityLocationEnum.GREEN_AREA)) {
      refvalue = 0.49f;
    } else if (cityLocation.equals(CityLocationEnum.DOWNTOWN_NORIVER)) {
      refvalue = 0.57f;
    } else if (cityLocation.equals(CityLocationEnum.DOWNTOWN_RIVER)) {
      refvalue = 0.71f;
    } else if (cityLocation.equals(CityLocationEnum.INDUSTRIAL_ZONE)) {
      refvalue = 0.54f;
    } else if (cityLocation.equals(CityLocationEnum.MEADOW)) {
      refvalue = 0.74f;
    } else if (cityLocation.equals(CityLocationEnum.OPEN_LAND)) {
      refvalue = 1;
    }
    return windVelocity * refvalue;
  }

  public boolean isRainDay() {
    return this.rainDay;
  }

  /**
   * Set the option for rain day with a perception of 10 percent.
   */
  private void setRainDay() {
    int value = this.getRandomInt(100);
    this.rainDay = (value < 10) ? true : false;

    // Rain day interval
    if (this.rainDay) {
      // Start
      this.rainDayStart = this.getRandomInt(23);

      // End
      value = 24 - this.rainDayStart;
      this.rainDayEnds = this.getRandomInt(value) + this.rainDayStart;
    }
  }

  /**
   * Initiate the decorator
   */
  @Override
  protected void initDecorator() {
    if (this.getProps() != null) {
      // Load properties from file
      this.setOrderID(Integer.parseInt(this.getProps().
        getProperty("cityclimate_order_id")));
      this.rainDayStart = Integer.parseInt(this.getProps().getProperty(
        "cityclimate_rainday_start"));
      this.rainDayEnds = Integer.parseInt(this.getProps().getProperty(
        "cityclimate_rainday_end"));
    } else {
      // Take default values
      this.setOrderID(CityClimateModifier.ORDER_ID);
      this.rainDayStart = 0;
      this.rainDayEnds = 23;
    }

    // Calculate rainday
    this.setRainDay();
  }
}
