/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.sensorFramework;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.sensor.SensorInterferer;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract super class of a Sensor. To create a concrete sensor instantiate a
 * SensorDomain and use the add-method to pass the class object into the
 * SensorDomain so that it can be created.
 *
 * @param <E>
 * @author Marcus
 * @version 1.0
 * @param <X>
 */
@MappedSuperclass
public abstract class AbstractSensor<E extends Event, X extends SensorData> extends AbstractIdentifiable
	implements Sensor<E,X> {

	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractSensor.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Determines whether the sensor is activated
	 */
	private boolean activated = true;

	/**
	 * will be incremented on each update and be set to 0 after update limit has
	 * been reached
	 */
	private int updateSteps = 0;

	/**
	 * The number of measured values
	 */
	private long measuredValues = 0;

	/**
	 * The output used by the sensor to send his measured values.
	 */
	@Transient
	private Output output;

	/**
	 * Update limit
	 */
	private int updateLimit;

	private SensorType sensorType;
	
	private X sensorData;
	
	private List<SensorInterfererType> sensorInterferers;

	protected AbstractSensor() {
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorType
	 * @param sensorData
	 * @throws IllegalArgumentException
	 */
	public AbstractSensor(final Output output,
		SensorType sensorType, X sensorData)
		throws IllegalArgumentException {
		this(output,
			sensorType,
			1,sensorData);
	}

	/**
	 * Constructor
	 *
	 * @param output Output of the sensor
	 * @param sensorType
	 * @param updateLimit Update limit
	 * @param sensorData
	 * @throws IllegalArgumentException
	 */
	public AbstractSensor(final Output output,
		SensorType sensorType,
		final int updateLimit, X sensorData)
		throws IllegalArgumentException {
		if (output == null) {
			throw new IllegalArgumentException("Argument 'output' must not be 'null'.");
		}
		// if (position != null && (position.getX() < 0) || (position.getY() < 0)) {
		// throw new IllegalArgumentException("Argument 'position' must have only positive values.");
		// }
		if (updateLimit < 1) {
			throw new IllegalArgumentException(
				"Argument 'updateLimit' must be greater than '0'.");
		}

		this.output = output;
		this.updateLimit = updateLimit;
		this.sensorType = sensorType;
		this.sensorData = sensorData;
	}

	@Override
	public int getUpdateSteps() {
		return updateSteps;
	}

	protected void setUpdateSteps(int updateSteps) {
		this.updateSteps = updateSteps;
	}

	@Override
	public SensorType getSensorType() {
		return sensorType;
	}

	public void setSensorType(SensorTypeEnum sensorType) {
		this.sensorType = sensorType;
	}

	/**
	 * returns the number of measured values of the sensor
	 *
	 * @return measuredValues
	 */
	@Override
	public long getMeasuredValues() {
		return this.measuredValues;
	}

	/**
	 * returns the output of the sensor
	 *
	 * @return output
	 */
	@Override
	public Output getOutput() {
		return this.output;
	}

	/**
	 * determines whether the sensor is activated
	 *
	 * @return activated
	 */
	@Override
	public boolean isActivated() {
		return this.activated;
	}

	/**
	 * sets the sensor activated
	 *
	 * @param activated determines whether the sensor is activated
	 */
	@Override
	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

	/**
	 * Makes the sensor measure its environment and doing the transmission
	 * sequence if it is activated.
	 *
	 * @param eventList List with SimulationEvents
	 */
	@Override
	public void update(final EventList<E> eventList) {
		if (eventList == null) {
			throw new IllegalArgumentException("\"eventList\" must not be null");
		}
		if (this.isActivated()) {
			this.transmitData(eventList);
		}
	}

	/**
	 * performs the whole transmission sequence
	 *
	 * @param eventList List with SimulationEvents
	 */
	protected void transmitData(final EventList<E> eventList) {
		if (++this.updateSteps >= this.updateLimit) {

			this.beginTransmit();
			this.transmitMetaData(eventList);
			this.transmitUsageData(eventList);
			this.endTransmit();
			this.measuredValues++;
			this.updateSteps = 0;
		}
		// else
		// log.debug("Sensor " + this.getSensorType() + " is not allowed to send data because of the updateSteps");

		// logValueToSend(eventList); // for test purpose (damit nichts an infosphere gesendet wird)
	}

	protected void logValueToSend(EventList<E> eventList) {
		LOGGER.debug("sending event list %s",
			eventList);
	}

	/**
	 * Initializes the transmission sequence
	 */
	private void beginTransmit() {
		this.getOutput().beginTransmit();
	}

	/**
	 * Finalizes the transmission sequence
	 */
	private void endTransmit() {
		this.getOutput().endTransmit();
	}

	/**
	 * Transmits the metadata of the sensor. The current Timestamp (long), the
	 * sensorId (int) and the sensorTypeId (int)
	 *
	 * @param eventList List with SimulationEvents
	 */
	private void transmitMetaData(final EventList<E> eventList) {
		// Timestamp
		this.getOutput().transmitLong(eventList.getTimestamp());
		// SensorId
		this.getOutput().transmitLong(this.getId());
		// SensorTypeId
		this.getOutput().transmitByte((byte) this.getSensorType().getSensorTypeId());
	}

	@Override
	public X getSensorData() {
		return sensorData;
	}

	@Override
	public void setSensorData(X sensorData) {
		this.sensorData = sensorData;
	}

	@Override
	public List<SensorInterfererType> getSensorInterfererTypes() {
		return sensorInterferers;
	}

	@Override
	public void setSensorInterfererTypes(List<SensorInterfererType> sensorInterferer) {
		this.sensorInterferers = sensorInterferer;
	}
}
