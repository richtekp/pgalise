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
 
package de.pgalise.simulation.sensorFramework.output.tcpip;

import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;


/**
 * Abstract base class for all types of state those a {@link TcpIpOutput}
 * can adopt
 * 
 * @author Marcus
 */
abstract class TcpIpOutputState {

	/**
	 * holds the passed instance of the passed {@link TcpIpOutput} that
	 * can adopt the passed states
	 */
	private final TcpIpOutput output;

	/**
	 * Super constructor for all sub types of {@linkTcpIpSensorOutputState}.
	 * 
	 * @param output
	 *            the passed output that can adopt this state
	 * @throws IllegalArgumentException
	 *             if argument 'output' is null
	 */
	protected TcpIpOutputState(final TcpIpOutput output) throws IllegalArgumentException {
		if(output == null) {
			throw new IllegalArgumentException("Argument \"output\" must not be \"null\".");
		}
		this.output = output;
	}

	/**
	 * Returns the passed {@link TcpIpOutput} instance for this state.
	 * 
	 * @return the passed {@link TcpIpOutput} instance for this state
	 */
	protected TcpIpOutput getOutput() {
		return this.output;
	}

	/**
	 * Starts the passed transmission process.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 */
	abstract void beginTransmit() throws IllegalStateException;

	/**
	 * Ends the passed transmission process.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 */
	abstract void endTransmit() throws IllegalStateException;

	/**
	 * Returns the current state of the sub class as
	 * {@link OutputStateEnum}.
	 * 
	 * @return the current state of the sub class as
	 *         {@link OutputStateEnum}
	 */
	abstract OutputStateEnum getState();

	/**
	 * Transmits the passed double value.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any double value
	 */
	abstract void transmitDouble(double value) throws IllegalStateException;

	/**
	 * Transmits the passed float value.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any float value
	 */
	abstract void transmitFloat(float value) throws IllegalStateException;

	/**
	 * Transmits the passed byte value.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any byte value
	 */
	abstract void transmitByte(byte value) throws IllegalStateException;

	/**
	 * Transmits the passed short value.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any short value
	 */
	abstract void transmitShort(short value) throws IllegalStateException;

	/**
	 * Transmits the passed integer value.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any integer value
	 */
	abstract void transmitInt(int vaue) throws IllegalStateException;

	/**
	 * Transmits the passed long value.
	 * 
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any long value
	 */
	abstract void transmitLong(long value) throws IllegalStateException;

	/**
	 * Transmits the passed string.
	 * 
	 * @throws IllegalArgumentException
	 *             if argument 'value' is null
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any string except null
	 */
	abstract void transmitString(String value) throws IllegalArgumentException, IllegalStateException;

	/**
	 * Transmits the passed boolean.
	 * 
	 * @throws IllegalArgumentException
	 *             if argument 'value' is null
	 * @throws IllegalStateException
	 *             if the currents state forbids this action
	 * @param value
	 *            any boolean value
	 */
	abstract void transmitBoolean(boolean value) throws IllegalStateException;

}
