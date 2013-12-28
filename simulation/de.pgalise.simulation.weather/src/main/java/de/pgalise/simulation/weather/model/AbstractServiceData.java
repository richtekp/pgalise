/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.City;
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
public abstract class AbstractServiceData extends AbstractTimeSensitive implements ServiceData {
	
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

	public AbstractServiceData(Date measureDate, Time measureTime, City city, Float relativHumidity, Float windDirection, Float windVelocity, WeatherCondition condition) {
		super(measureDate,
			measureTime);
		this.city = city;
		this.relativHumidity = relativHumidity;
		this.windDirection = windDirection;
		this.windVelocity = windVelocity;
		this.condition = condition;
	}

	@Override
	public City getCity() {
		return this.city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public Float getRelativHumidity() {
		return this.relativHumidity;
	}

	@Override
	public Float getWindDirection() {
		return this.windDirection;
	}

	@Override
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

	@Override
	public WeatherCondition getCondition() {
		return this.condition;
	}
}
