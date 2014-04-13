/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.AbstractSensor;
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
	private BaseCoordinate position;

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorType
	 * @param position Position of the sensor
	 * @param sensorData
	 * @throws IllegalArgumentException
	 */
	public AbstractStaticSensor(Long id,
		final Output output,
		BaseCoordinate position,
		X sensorData)
		throws IllegalArgumentException {
		this(id,
			output,
			position,
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
	public AbstractStaticSensor(Long id,
		final Output output,
		BaseCoordinate position,
		final int updateLimit,
		X sensorData)
		throws IllegalArgumentException {
		super(id,
			output,
			updateLimit,
			sensorData);
		this.position = position;
	}

	@Override
	public BaseCoordinate getPosition() {
		return position;
	}

	@Override
	public void setPosition(BaseCoordinate position) {
		this.position = position;
	}

}
