/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.weather.entity.WeatherCondition;
import de.pgalise.simulation.shared.entity.City;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 *
 * @param <C>
 * @author richter
 */
/*
 * persistable WeatherCondition needs to be enforced already at the level of 
 * the MappedSuperclass (if usage of @Type annotation should be avoided)
 */
@MappedSuperclass
public abstract class AbstractServiceData extends AbstractTimeSensitive {

  /**
   * City
   */
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private City city;

  /**
   * Relativ humidity
   */
  @Column(name = "RELATIV_HUMIDITY")
  private Float relativHumidity;

  /**
   * wind direction
   */
  @Column(name = "WIND_DIRECTION")
  private Float windDirection;

  /**
   * wind velocity
   */
  @Column(name = "WIND_VELOCITY")
  private Float windVelocity;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private WeatherCondition condition;

  protected AbstractServiceData() {
  }

  public AbstractServiceData(Long id,
    Date measureDate,
    Time measureTime,
    City city,
    Float relativHumidity,
    Float windDirection,
    Float windVelocity,
    WeatherCondition condition) {
    super(id,
      measureDate,
      measureTime);
    this.city = city;
    this.relativHumidity = relativHumidity;
    this.windDirection = windDirection;
    this.windVelocity = windVelocity;
    this.condition = condition;
  }

  public City getCity() {
    return this.city;
  }

  /**
   * @param city the city to set
   */
  public void setCity(City city) {
    this.city = city;
  }

  public Float getRelativHumidity() {
    return this.relativHumidity;
  }

  public Float getWindDirection() {
    return this.windDirection;
  }

  public Float getWindVelocity() {
    return this.windVelocity;
  }

  public void setRelativHumidity(Float relativHumidity) {
    this.relativHumidity = relativHumidity;
  }

  public void setWindDirection(Float windDirection) {
    this.windDirection = windDirection;
  }

  public void setWindVelocity(Float windVelocity) {
    this.windVelocity = windVelocity;
  }

  public void setCondition(WeatherCondition condition) {
    this.condition = condition;
  }

  public WeatherCondition getCondition() {
    return this.condition;
  }
}
