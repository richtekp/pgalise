/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

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
public abstract class AbstractServiceData<C extends WeatherCondition> extends AbstractMutableTimeSensitive implements ServiceData<C>, MutableServiceData<C> {
	
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
	private C condition;

	protected AbstractServiceData() {
	}

	public AbstractServiceData(Date measureDate, Time measureTime, City city, Float relativHumidity, Float windDirection, Float windVelocity, C condition) {
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
	@Override
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

	@Override
	public void setRelativHumidity(Float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	@Override
	public void setWindDirection(Float windDirection) {
		this.windDirection = windDirection;
	}

	@Override
	public void setWindVelocity(Float windVelocity) {
		this.windVelocity = windVelocity;
	}

	@Override
	public void setCondition(C condition) {
		this.condition = condition;
	}

	@Override
	public C getCondition() {
		return this.condition;
	}
}
