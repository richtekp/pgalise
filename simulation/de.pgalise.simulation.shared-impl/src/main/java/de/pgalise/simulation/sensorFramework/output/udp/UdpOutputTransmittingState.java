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
 
package de.pgalise.simulation.sensorFramework.output.udp;

import java.io.IOException;
import java.net.DatagramPacket;

import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;

/**
 * Class for the transmitting-state in the state pattern
 * 
 * @author Marcus
 */
class UdpOutputTransmittingState extends UdpOutputState {

	/**
	 * Creates an instance of {@link UdpOutputTransmittingState}.
	 * 
	 * @param output
	 *            the output that can adopt this state
	 * @throws IllegalArgumentException
	 *             if argument 'output' is null
	 */
	protected UdpOutputTransmittingState(UdpOutput output) throws IllegalArgumentException {
		super(output);
	}

	@Override
	void beginTransmit() throws IllegalStateException {
		throw new IllegalStateException(
				"Output is in \"Transmitting\" state. Transmission has to be ended first in order to begin transmission again.");
	}

	@Override
	void endTransmit() throws IllegalStateException {
		byte[] buf = new byte[this.getOutput().getCurrentBufSize()];
		int index = 0;
		for (final byte[] bytes : this.getOutput().getBytesToSend()) {
			for (int i = 0; i < bytes.length; i++) {
				buf[index++] = bytes[i];
			}
		}
		try {
			final DatagramPacket datagramPacket = new DatagramPacket(buf, this.getOutput().getCurrentBufSize(), this
					.getOutput().getAddress());
			this.getOutput().getDatagramSocket().send(datagramPacket);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.getOutput().setCurrentState(this.getOutput().getTransmittingState());
		this.getOutput().getSemaphore().release();
	}

	@Override
	OutputStateEnum getState() {
		return OutputStateEnum.TRANSMITTING;
	}

	@Override
	void transmitDouble(double value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitFloat(float value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitByte(byte value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitShort(short value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitInt(int value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitLong(long value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitString(String value) throws IllegalArgumentException, IllegalStateException, IllegalArgumentException {

		if (value == null) {
			throw new IllegalArgumentException("value may not be null");
		}

		byte[] buf = value.getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitBoolean(boolean value) throws IllegalStateException {
		byte[] buf = String.valueOf(value).getBytes();
		this.getOutput().getBytesToSend().add(buf);
		this.getOutput().setCurrentBufSize(this.getOutput().getCurrentBufSize() + buf.length);
	}

	@Override
	void transmitByteArray(byte[] value) throws IllegalStateException {
		throw new RuntimeException("Not implemented...");
	}

}
