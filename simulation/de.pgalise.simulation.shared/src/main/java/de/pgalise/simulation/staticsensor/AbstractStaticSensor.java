/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.Event;

/**
 *
 * @author richter
 * @param <E>
 * @param <X>
 */
public abstract class AbstractStaticSensor<E extends Event, X extends SensorData>
	extends AbstractSensor<E, X> implements StaticSensor<E, X> {

	private static final long serialVersionUID = 1L;
	private Coordinate position;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorType
	 * @param position Position of the sensor
	 * @param sensorData
	 * @throws IllegalArgumentException
	 */
	public AbstractStaticSensor(final Output output,
		Coordinate position,
		SensorType sensorType,
		X sensorData)
		throws IllegalArgumentException {
		this(output,
			position,
			sensorType,
			1,
			sensorData);
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorType
	 * @param position Position of the sensor
	 * @param updateLimit Update limit
	 * @param sensorData
	 * @throws IllegalArgumentException
	 */
	public AbstractStaticSensor(final Output output,
		Coordinate position,
		SensorType sensorType,
		final int updateLimit,
		X sensorData)
		throws IllegalArgumentException {
		super(output,
			sensorType,
			updateLimit,
			sensorData);
		this.position = position;
	}

	@Override
	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(Coordinate position) {
		this.position = position;
	}

}
