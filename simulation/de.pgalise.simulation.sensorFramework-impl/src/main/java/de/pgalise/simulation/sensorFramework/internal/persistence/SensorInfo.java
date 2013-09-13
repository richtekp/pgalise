/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.sensorFramework.internal.persistence;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Info class dor {@link Sensor}s
 *
 * @author Marcus
 * @version 1.0 (Aug 29, 2012)
 */
@Entity
@Table(name = "PGALISE.SENSOR")
public class SensorInfo extends AbstractIdentifiable {
	private static final long serialVersionUID = 1L;
	/**
	 * Latitude
	 */
	@Column(name = "LATITUDE")
	private double latitude;
	/**
	 * Longitude
	 */
	@Column(name = "LONGITUDE")
	private double longitude;

	/**
	 * SensorType ID
	 */
	@Column(name = "SENSOR_TYPE_ID")
	private int sensorTypeId;

	protected SensorInfo() {
	}

	/**
	 * Creates a {@link SensorInfo} from the passed {@link Sensor}
	 *
	 * @param sensor
	 *            the {@link Sensor} for which this {@link SensorInfo} shall be created
	 * @throws IllegalArgumentException
	 *             if argument 'sensor' is 'null'
	 */
	private SensorInfo(final Sensor sensor) throws IllegalArgumentException {
		super(sensor.getId());
		this.sensorTypeId = sensor.getSensorType().ordinal();
		this.longitude = sensor.getPosition().x;
		this.latitude = sensor.getPosition().y;
	}
	
}
