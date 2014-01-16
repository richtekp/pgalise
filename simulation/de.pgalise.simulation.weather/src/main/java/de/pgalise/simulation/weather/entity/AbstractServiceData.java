/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.shared.entity.City;
import java.sql.Time;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 *
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
    java.util.Date measureDate,
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
	
	protected boolean equalsTransitive(AbstractServiceData other) {
		if(!super.equalsTransitive(other)) {
			return false;
		}
		if (!Objects.equals(this.city,
			other.city)) {
			return false;
		}
		if (!Objects.equals(this.relativHumidity,
			other.relativHumidity)) {
			return false;
		}
		if (!Objects.equals(this.windDirection,
			other.windDirection)) {
			return false;
		}
		if (!Objects.equals(this.windVelocity,
			other.windVelocity)) {
			return false;
		}
		if (!Objects.equals(this.condition,
			other.condition)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractServiceData other = (AbstractServiceData) obj;
		return equalsTransitive(other);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + super.hashCode();
		hash = 97 * hash + Objects.hashCode(this.city);
		hash = 97 * hash + Objects.hashCode(this.relativHumidity);
		hash = 97 * hash + Objects.hashCode(this.windDirection);
		hash = 97 * hash + Objects.hashCode(this.windVelocity);
		hash = 97 * hash + Objects.hashCode(this.condition);
		return hash;
	}
}
