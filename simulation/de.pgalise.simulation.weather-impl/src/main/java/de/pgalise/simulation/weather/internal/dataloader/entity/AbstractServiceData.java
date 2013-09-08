/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader.entity;

import de.pgalise.simulation.shared.city.City;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractServiceData extends ServiceData {
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

	protected AbstractServiceData() {
	}

	public AbstractServiceData(Float relativHumidity, Float windDirection, Float windVelocity, City city, Date measureDate) {
		super(city, measureDate);
		this.relativHumidity = relativHumidity;
		this.windDirection = windDirection;
		this.windVelocity = windVelocity;
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
}
