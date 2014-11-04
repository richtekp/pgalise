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
package de.pgalise.simulation.weather.internal.positionconverter;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.positionconverter.WeatherPositionConverter;
import de.pgalise.simulation.weather.positionconverter.WeatherPositionConverterBase;
import de.pgalise.simulation.weather.util.DateConverter;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 * The implementation {@link LinearWeatherPositionConverter} calculates the
 * values on a linear base away from the reference point.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 12, 2012)
 */
@Stateful()
@Local(WeatherPositionConverter.class)
public class LinearWeatherPositionConverter extends WeatherPositionConverterBase
  implements WeatherPositionConverter {

  public LinearWeatherPositionConverter() {
  }

  /**
   * Constructor
   *
   * @param grid
   */
  public LinearWeatherPositionConverter(Polygon grid) {
    super(grid);
  }

  /**
   * Returns the modified reference value for the given position
   *
   * @param <T>
   * @param key WeatherParameterEnum
   * @param time Timestamp
   * @param position Position
   * @param refValue Reference value
   * @return Modified value
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T extends Number> T getValue(WeatherParameterEnum key,
    long time,
    BaseCoordinate position,
    T refValue,
    Polygon grid) {
    // Change the value pro vector unit?
    if (!key.isChangePerVectorUnit()) {
      return refValue;
    }

    // Calculate distance
    double distance
      = LatLngTool.distance(new LatLng(this.getReferencePosition().getX(),
          this.getReferencePosition().getY()),
        new LatLng(position.getX(),
          position.getY()),
        LengthUnit.METER);
			//Math.abs(Math.sqrt(Math.pow(this.getReferencePosition().getX() - position.getX(), 2)
//				+ Math.pow(this.getReferencePosition().getY() - position.getY(), 2)));

    // Get the change value
    T changeValue = this.getChangeValue(key,
      time,
      distance,
      refValue);

    // Add the linear change rate
    double modValue = WeatherPositionConverterBase.round(
      refValue.doubleValue() + changeValue.doubleValue(),
      3);

    // Check value
    Double mValue = this.checkValues(key,
      modValue);

    return (T) mValue;
  }

  /**
   * Check values of their limits
   *
   * @param key WeatherParameterEnum
   * @param value Modified value
   * @return Correct value
   */
  private double checkValues(WeatherParameterEnum key,
    double value) {
    switch (key) {
      case PRECIPITATION_AMOUNT:
        if (value < 0) {
          return 0;
        }
        break;
      case RADIATION:
        if (value < 0) {
          return 0;
        }
        break;
      case RELATIV_HUMIDITY:
        if (value < 0) {
          return 0;
        } else if (value > 100) {
          return 100;
        }
        break;
      case LIGHT_INTENSITY:
        if (value < 0) {
          return 0;
        }
        break;
      case WIND_VELOCITY:
        if (value < 0) {
          return 0;
        }
        break;
      default:
        return value;
    }

    return value;
  }

  /**
   * Returns the change value for one vector unit according to the weather
   * parameter
   *
   * @param key WeatherParameterEnum
   * @param time Timestamp
   * @param distance Distance
   * @param refValue Reference value
   * @return change value for one vector unit
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getChangeValue(WeatherParameterEnum key,
    long time,
    double distance,
    T refValue) {

    // Calculate value
    switch (key) {
      case TEMPERATURE:
      case PERCEIVED_TEMPERATURE:
        return this.getTemperature(time,
          distance);

      case WIND_VELOCITY:
        return this.getWindVelocity(refValue,
          distance);

      case RADIATION:
        return this.getRadiation(time,
          refValue,
          distance);

      case RELATIV_HUMIDITY:
        return this.getRelativHumidity(time,
          refValue,
          distance);

      case PRECIPITATION_AMOUNT:
        return this.getPrecipitationAmount(time,
          refValue,
          distance);

      case LIGHT_INTENSITY:
        return this.getLightIntensity(refValue,
          distance);

      default: // Error
        return (T) new Double(0);
    }
  }

  /**
   * Returns the light intensity: surrounding land has +5%
   *
   * @param refValue Reference value
   * @return Change value for the parameter
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getLightIntensity(T refValue,
    double distance) {
    Double max = refValue.intValue() * 0.05;

    // Calculate for one vector unit
    Double result = (max / this.getGridDistance()) * distance;

    // Check result?
    return (T) ((result > max) ? max : (T) result);
  }

  /**
   * Returns the precipitation amount: surrounding land has -1.0 (day) and +0,8
   * (night)
   *
   * @param time Timestamp
   * @param refValue Reference value
   * @param distance Distance from reference point to the given position
   * @return Change value for the parameter
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getPrecipitationAmount(long time,
    T refValue,
    double distance) {
    Double max;

    /*
     * No new rain
     */
    if (refValue.floatValue() == 0) {
      return (T) new Double(0);
    }

    /*
     * Else
     */
    // For unit
    int hour = DateConverter.getHourOfDay(time);
    if ((hour >= 6) && (hour <= 18)) { // Day
      max = -1.0;
    } else { // Night
      max = 0.8;
    }

    // Calculate for one vector unit
    Double result = (max / this.getGridDistance()) * distance;

    // Check result?
    return (T) ((result > max) ? max : (T) result);
  }

  /**
   * Returns the radiation: surrounding land has -3% (day) and -10% (night)
   *
   * @param time Timestamp
   * @param refValue Reference value
   * @param distance Distance from reference point to the given position
   * @return Change value for the parameter
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getRadiation(long time,
    T refValue,
    double distance) {
    Integer max;
    Integer result = null;

    /*
     * Not more than zero
     */
    if (refValue.intValue() == 0) {
      return (T) new Double(0);
    }

    /*
     * Else
     */
    int hour = DateConverter.getHourOfDay(time);
    if ((hour >= 6) && (hour <= 18)) { // Day
      max = (int) -(refValue.intValue() * 0.03);
    } else { // Night
      max = (int) -(refValue.intValue() * 0.1);
    }

    // Calculate for one vector unit
    result = (int) ((max / this.getGridDistance()) * distance);

    // Check result?
    return (T) ((result < max) ? max : (T) result);
  }

  /**
   * Returns the relativ humidity : surrounding land has +1% (winter) and
   * +30-40% (sommer)
   *
   * @param time Timestamp
   * @param refValue Reference value
   * @param distance Distance from reference point to the given position
   * @return Change value for the parameter
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getRelativHumidity(long time,
    T refValue,
    double distance) {
    Float max;
    Float result;

    int season = DateConverter.getSeason(time);
    if (season == 0) { // Winter
      max = (refValue.floatValue() * 0.01F);
    } else if (season == 2) { // Summer
      max = (refValue.floatValue() * 0.3F);
    } else {
      max = (refValue.floatValue() * 0.10F);
    }

    // Calculate for one vector unit
    result = (float) ((max / this.getGridDistance()) * distance);

    // Check result?
    return (T) ((result < max) ? max : (T) result);
  }

  /**
   * Returns the temperature: surrounding land has + (day) and - (night)
   *
   * @param time Timestamp
   * @param distance Distance from reference point to the given position
   * @return Change value for the parameter
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getTemperature(long time,
    double distance) {
    Double result;

    // Change value
    int hour = DateConverter.getHourOfDay(time);
    if ((hour >= 6) && (hour <= 18)) { // Day
      result = 0.02;
    } else { // Night
      result = -0.02;
    }

    // Calculate for one vector unit
    result = ((1 / this.getGridDistance()) * result) * distance;

    // Check result?
    return (T) ((result > 3) ? 3 : (T) result);
  }

  /**
   * Returns the wind velocity: surrounding land has 100% and downtown 57% (or
   * downtown with river 71%)
   *
   * @param refValue Reference value
   * @param distance Distance from reference point to the given position
   * @return Change value for the parameter
   */
  @SuppressWarnings("unchecked")
  private <T extends Number> T getWindVelocity(T refValue,
    double distance) {
    Double max = refValue.floatValue() * 0.43;

    // Calculate for one vector unit
    Double result = (max / this.getGridDistance()) * distance;

    // Check result?
    return (T) ((result > max) ? max : (T) result);
  }
}
