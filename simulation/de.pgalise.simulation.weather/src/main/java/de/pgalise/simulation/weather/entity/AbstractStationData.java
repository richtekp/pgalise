/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.entity;

import java.sql.Time;
import java.util.Objects;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractStationData extends AbstractTimeSensitive
  implements Comparable<AbstractStationData> {

  private static final long serialVersionUID = 1L;

  /**
   * air pressure
   */
  @Column(name = "AIR_PRESSURE")
  private Integer airPressure;

  /**
   * light intensity
   */
  @Column(name = "LIGHT_INTENSITY")
  private Integer lightIntensity;

  /**
   * perceived temperature
   */
  @Column(name = "PERCEIVED_TEMPERATURE")
  private Float perceivedTemperature;

  /**
   * precipitation amount
   */
  @Column(name = "PRECIPITATION_AMOUNT")
  private Float precipitationAmount;

  /**
   * radiation
   */
  @Column(name = "RADIATION")
  private Integer radiation;

  /**
   * relativ humidity
   */
  @Column(name = "RELATIV_HUMIDITY")
  private Float relativHumidity;

  /**
   * temperature
   */
  @Column(name = "TEMPERATURE")
  private Measure<Float, Temperature> temperature;

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

  /**
   * Default constructor
   */
  protected AbstractStationData() {
  }

  /**
   * Constructor
   *
   * @param date Date
   * @param time Time
   * @param airPressure air pressure
   * @param lightIntensity light intensity
   * @param perceivedTemperature perceived temperature
   * @param temperature temperature
   * @param precipitationAmount precipitation amount
   * @param radiation radiation
   * @param relativHumidity relativ humidity
   * @param windDirection wind direction
   * @param windVelocity wind velocity
   */
  public AbstractStationData(Long id,
    java.util.Date date,
    Time time,
    Integer airPressure,
    Integer lightIntensity,
    Float perceivedTemperature,
    Measure<Float, Temperature> temperature,
    Float precipitationAmount,
    Integer radiation,
    Float relativHumidity,
    Float windDirection,
    Float windVelocity
  ) {
    super(id,
      date,
      time);
    this.airPressure = airPressure;
    this.lightIntensity = lightIntensity;
    this.perceivedTemperature = perceivedTemperature;
    this.temperature = temperature;
    this.precipitationAmount = precipitationAmount;
    this.radiation = radiation;
    this.relativHumidity = relativHumidity;
    this.windDirection = windDirection;
    this.windVelocity = windVelocity;
  }

  @Override
  public int compareTo(AbstractStationData data) {
    long thisTime = this.getMeasureDate().getTime() + this.getMeasureTime().
      getTime();
    long anotherTime = data.getMeasureDate().getTime() + data.getMeasureTime().
      getTime();
    return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
  }

  public Integer getAirPressure() {
    return this.airPressure;
  }

  public Integer getLightIntensity() {
    return this.lightIntensity;
  }

  public Float getPerceivedTemperature() {
    return this.perceivedTemperature;
  }

  public Float getPrecipitationAmount() {
    return this.precipitationAmount;
  }

  public Integer getRadiation() {
    return this.radiation;
  }

  public Float getRelativHumidity() {
    return this.relativHumidity;
  }

  public Measure<Float, Temperature> getTemperature() {
    return this.temperature;
  }

  public Float getWindDirection() {
    return this.windDirection;
  }

  public Float getWindVelocity() {
    return this.windVelocity;
  }

  public void setAirPressure(Integer airPressure) {
    this.airPressure = airPressure;
  }

  public void setLightIntensity(Integer lightIntensity) {
    this.lightIntensity = lightIntensity;
  }

  public void setPerceivedTemperature(Float perceivedTemperature) {
    this.perceivedTemperature = perceivedTemperature;
  }

  public void setPrecipitationAmount(Float precipitationAmount) {
    this.precipitationAmount = precipitationAmount;
  }

  public void setRadiation(Integer radiation) {
    this.radiation = radiation;
  }

  public void setRelativHumidity(Float relativHumidity) {
    this.relativHumidity = relativHumidity;
  }

  public void setTemperature(Measure<Float, Temperature> temperature) {
    this.temperature = temperature;
  }

  public void setWindDirection(Float windDirection) {
    this.windDirection = windDirection;
  }

  public void setWindVelocity(Float windVelocity) {
    this.windVelocity = windVelocity;
  }

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * super.hashCode();
		hash = 83 * hash + Objects.hashCode(this.airPressure);
		hash = 83 * hash + Objects.hashCode(this.lightIntensity);
		hash = 83 * hash + Objects.hashCode(this.perceivedTemperature);
		hash = 83 * hash + Objects.hashCode(this.precipitationAmount);
		hash = 83 * hash + Objects.hashCode(this.radiation);
		hash = 83 * hash + Objects.hashCode(this.relativHumidity);
		hash = 83 * hash + Objects.hashCode(this.temperature);
		hash = 83 * hash + Objects.hashCode(this.windDirection);
		hash = 83 * hash + Objects.hashCode(this.windVelocity);
		return hash;
	}
	
	protected boolean equalsTransitive(AbstractStationData other) {
		if(!super.equalsTransitive(other)) {
			return false;
		}
		if (!Objects.equals(this.airPressure,
			other.airPressure)) {
			return false;
		}
		if (!Objects.equals(this.lightIntensity,
			other.lightIntensity)) {
			return false;
		}
		if (!Objects.equals(this.perceivedTemperature,
			other.perceivedTemperature)) {
			return false;
		}
		if (!Objects.equals(this.precipitationAmount,
			other.precipitationAmount)) {
			return false;
		}
		if (!Objects.equals(this.radiation,
			other.radiation)) {
			return false;
		}
		if (!Objects.equals(this.relativHumidity,
			other.relativHumidity)) {
			return false;
		}
		if (!Objects.equals(this.temperature,
			other.temperature)) {
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
		final AbstractStationData other = (AbstractStationData) obj;
		return equalsTransitive(other);
	}
}
