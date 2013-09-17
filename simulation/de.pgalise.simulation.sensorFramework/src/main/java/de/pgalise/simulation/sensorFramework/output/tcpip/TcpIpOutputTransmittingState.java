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
 * Class for the transmitting-state in the state pattern
 * 
 * @author Marcus
 */
class TcpIpOutputTransmittingState extends TcpIpOutputState {

	/**
	 * Creates an instance of {@link TcpIpOutputTransmittingState}.
	 * 
	 * @param output
	 *            the output that can adopt this state
	 * @throws IllegalArgumentException
	 *             if argument 'output' is null
	 */
	TcpIpOutputTransmittingState(final TcpIpOutput output) {
		super(output);
	}

	/**
	 * The {@link TcpIpOutputTransmittingState} is only able to transmit
	 * values and end the transmission, not to begin it. <br>
	 * Hence this method always throws an {@link IllegalStateException}.
	 * 
	 * @throws IllegalStateException
	 *             on each call
	 */
	@Override
	void beginTransmit() throws IllegalStateException {
		throw new IllegalStateException("Output is in \"Transmitting\" state. Transmission has to be ended first in order to begin transmission again.");
	}

	/**
	 * Ends the transmission process.
	 * 
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void endTransmit() throws RuntimeException {
		this.getOutput().closeConnection();
		this.getOutput().setCurrentState(this.getOutput().getReadyState());
		this.getOutput().getSemaphore().release();
	}

	/**
	 * Returns always {@literal SensorOutputStateEnum.TRANSMITTING}.
	 * 
	 * @return {@literal SensorOutputStateEnum.TRANSMITTING}
	 * @see OutputStateEnum
	 */
	@Override
	OutputStateEnum getState() {
		return OutputStateEnum.TRANSMITTING;
	}

	/**
	 * Transmits the passed double value.
	 * 
	 * @param any
	 *            double value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitDouble(final double value) throws RuntimeException {
		try {
			this.getOutput().getSocketOutputStream().writeDouble(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed float value.
	 * 
	 * @param any
	 *            float value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitFloat(final float value) throws RuntimeException {
		try {
			this.getOutput().getSocketOutputStream().writeFloat(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed byte value.
	 * 
	 * @param any
	 *            float value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitByte(final byte value) throws IllegalStateException {
		try {
			this.getOutput().getSocketOutputStream().writeByte(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed integer value.
	 * 
	 * @param any
	 *            integer value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitInt(final int value) throws RuntimeException {
		try {
			this.getOutput().getSocketOutputStream().writeInt(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed long value.
	 * 
	 * @param any
	 *            long value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitLong(final long value) throws RuntimeException {
		try {
			this.getOutput().getSocketOutputStream().writeLong(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed short value.
	 * 
	 * @param any
	 *            short value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitShort(final short value) throws RuntimeException {
		try {
			this.getOutput().getSocketOutputStream().writeShort(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed double value.
	 * 
	 * @param any
	 *            string value except null
	 * @exception IllegalArgumentException
	 *                if argument 'value' is null
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitString(final String value) throws IllegalArgumentException, RuntimeException {
		if(value == null) {
			throw new IllegalArgumentException("Argument \"value\" must not be \"null\".");
		}
		try {
			this.getOutput().getSocketOutputStream().writeUTF(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Transmits the passed boolean value.
	 * 
	 * @param any
	 *            short value
	 * @exception RuntimeException
	 *                if something went wrong
	 */
	@Override
	void transmitBoolean(final boolean value) throws IllegalStateException {
		try {
			this.getOutput().getSocketOutputStream().writeBoolean(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	void transmitByteArray(byte[] value) throws IllegalStateException {
		try {
			this.getOutput().getSocketOutputStream().write(value);
		} catch(final Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
