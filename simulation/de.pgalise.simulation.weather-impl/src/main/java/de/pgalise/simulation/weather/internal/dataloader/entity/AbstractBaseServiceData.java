/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader.entity;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.weather.model.BaseServiceData;
import de.pgalise.simulation.weather.model.Condition;
import de.pgalise.simulation.weather.model.ServiceDataCurrent;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractBaseServiceData extends AbstractIdentifiable implements BaseServiceData {
	
	/**
	 * City
	 */
	@ManyToOne
	@JoinColumn(name="CITY")
	private City city;

	/**
	 * Timestamp
	 */
	@Column(name = "MEASURE_DATE")
	private Date measureDate;
	
	private Time measureTime;
	
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
	
	@OneToOne(cascade = CascadeType.PERSIST)
	private DefaultCondition condition;

	protected AbstractBaseServiceData() {
	}

	public AbstractBaseServiceData(Date measureDate, Time measureTime, City city, Float relativHumidity, Float windDirection, Float windVelocity, DefaultCondition condition) {
		this.measureDate = measureDate;
		this.measureTime = measureTime;
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

	/**
	 * @return the measureDate
	 */
	@Override
	public Date getMeasureDate() {
		return measureDate;
	}

	@Override
	public Time getMeasureTime() {
		return measureTime;
	}

	/**
	 * @param measureDate the measureDate to set
	 */
	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}

	public void setMeasureTime(Time measureTime) {
		this.measureTime = measureTime;
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

	public void setCondition(DefaultCondition condition) {
		this.condition = condition;
	}

	@Override
	public DefaultCondition getCondition() {
		return this.condition;
	}
}
