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
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.controller.SimulationComponent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.sensor.SensorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Abstract super class of a Sensor. To create a concrete sensor instantiate a SensorDomain and use the add-method to
 * pass the class object into the SensorDomain so that it can be created.
 * 
 * @author Marcus
 * @version 1.0
 */
@Entity
public abstract class Sensor extends AbstractIdentifiable implements SimulationComponent {

	/**
	 * Determines whether the sensor is activated
	 */
	private boolean activated = true;

	/**
	 * will be incremented on each update and be set to 0 after update limit has been reached
	 */
	private int currentUpdateStep = 0;

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
	 * The position of the sensor in the environment
	 */
	@Embedded
	private Coordinate position;

	/**
	 * Update limit
	 */
	private int updateLimit;

	protected Sensor() {
	}

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 */
	protected Sensor(final Output output, final long sensorId, final Coordinate position)
			throws IllegalArgumentException {
		this(output, sensorId, position, 1);
	}

	/**
	 * Constructor
	 * 
	 * @param output
	 *            Output of the sensor
	 * @param sensorId
	 *            ID of the sensor
	 * @param position
	 *            Position of the sensor
	 * @param updateLimit
	 *            Update limit
	 */
	protected Sensor(final Output output, final long sensorId, final Coordinate position, final int updateLimit)
			throws IllegalArgumentException {
		super(sensorId);
		if (output == null) {
			throw new IllegalArgumentException("Argument 'output' must not be 'null'.");
		}
		// if (position != null && (position.getX() < 0) || (position.getY() < 0)) {
		// throw new IllegalArgumentException("Argument 'position' must have only positive values.");
		// }
		if (updateLimit < 1) {
			throw new IllegalArgumentException("Argument 'updateLimit' must be greater than '0'.");
		}

		this.output = output;
		this.position = position;
		this.updateLimit = updateLimit;

	}

	/**
	 * returns the number of measured values of the sensor
	 * 
	 * @return measuredValues
	 */
	public long getMeasuredValues() {
		return this.measuredValues;
	}

	/**
	 * returns the output of the sensor
	 * 
	 * @return output
	 */
	public Output getOutput() {
		return this.output;
	}

	/**
	 * Returns the position of the sensor in the environment
	 * 
	 * @return position
	 */
	public Coordinate getPosition() {
		return this.position;
	}

	/**
	 * Returns the sensortype
	 * 
	 * @return sensortype
	 */
	public abstract SensorType getSensorType();

	/**
	 * determines whether the sensor is activated
	 * 
	 * @return activated
	 */
	public boolean isActivated() {
		return this.activated;
	}

	/**
	 * sets the sensor activated
	 * 
	 * @param activated
	 *            determines whether the sensor is activated
	 */
	public void setActivated(final boolean activated) {
		this.activated = activated;
	}

	/**
	 * Sets the position of the sensor in the environment
	 * 
	 * @param position
	 *            Position
	 */
	public void setPosition(final Coordinate position) {
		this.position = position;
	}

	/**
	 * Makes the sensor measure its environment and doing the transmission sequence if it is activated.
	 * 
	 * @param eventList
	 *            List with SimulationEvents
	 */
	@Override
	public void update(final SimulationEventList eventList) {
		if (eventList == null) {
			throw new IllegalArgumentException("\"eventList\" must not be null");
		}
		if (this.isActivated()) {
			this.transmitData(eventList);
		}
	}

	/**
	 * subclasses are supposed to implement this method to send their usage data through their output
	 * 
	 * @param eventList
	 *            List with SimulationEvents
	 */
	public abstract void transmitUsageData(SimulationEventList eventList);

	/**
	 * performs the whole transmission sequence
	 * 
	 * @param eventList
	 *            List with SimulationEvents
	 */
	protected void transmitData(final SimulationEventList eventList) {
		if (++this.currentUpdateStep >= this.updateLimit) {

			this.beginTransmit();
			this.transmitMetaData(eventList);
			this.transmitUsageData(eventList);
			this.endTransmit();
			this.measuredValues++;
			this.currentUpdateStep = 0;
		}
		// else
		// log.debug("Sensor " + this.getSensorType() + " is not allowed to send data because of the updateSteps");

		// logValueToSend(eventList); // for test purpose (damit nichts an infosphere gesendet wird)
	}

	protected void logValueToSend(SimulationEventList eventList) {

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
	 * Transmits the metadata of the sensor. The current Timestamp (long), the sensorId (int) and the sensorTypeId (int)
	 * 
	 * @param eventList
	 *            List with SimulationEvents
	 */
	private void transmitMetaData(final SimulationEventList eventList) {
		// Timestamp
		this.getOutput().transmitLong(eventList.getTimestamp());
		// SensorId
		this.getOutput().transmitLong(this.getId());
		// SensorTypeId
		this.getOutput().transmitByte((byte) this.getSensorType().getSensorTypeId());
	}
}
