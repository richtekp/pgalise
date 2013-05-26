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
 * Class for the ready-state in the state pattern
 * 
 * @author Marcus
 */
final class TcpIpOutputReadyState extends TcpIpOutputState {

	/**
	 * Creates an instance of {@link TcpIpOutputReadyState}.
	 * 
	 * @param output
	 *            the output that can adopt this state
	 * @throws IllegalArgumentException
	 *             if argument 'output' is null
	 */
	TcpIpOutputReadyState(final TcpIpOutput output) throws IllegalArgumentException {
		super(output);
	}

	/**
	 * Starts the transmission process and sets the output's state to
	 * {@link TcpIpOutputTransmittingState}.
	 * 
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void beginTransmit() {
		try {
			this.getOutput().getSemaphore().acquire();
		} catch(final InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		this.getOutput().openConnection();
		this.getOutput().setCurrentState(this.getOutput().getTransmittingState());
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to
	 * beginTransmission, not to end it. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void endTransmit() {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	/**
	 * Returns always {@literal SensorOutputStateEnum.READY}.
	 * 
	 * @return {@literal SensorOutputStateEnum.READY}
	 * @see OutputStateEnum
	 */
	@Override
	OutputStateEnum getState() {
		return OutputStateEnum.READY;
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitDouble(final double value) {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitFloat(final float value) {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitByte(final byte value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitInt(final int vaue) {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitLong(final long value) {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitShort(final short value) {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitString(final String value) {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	/**
	 * The {@link TcpIpOutputReadyState} is only able to beginTransmission
	 * not to transmit values. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @exception IllegalStateException
	 *                on each call
	 */
	@Override
	void transmitBoolean(final boolean value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}

	@Override
	void transmitByteArray(byte[] value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to transmit something.");
	}
}
