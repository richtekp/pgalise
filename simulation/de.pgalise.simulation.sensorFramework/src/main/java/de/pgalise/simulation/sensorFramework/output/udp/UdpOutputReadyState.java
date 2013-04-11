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

import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;

/**
 * Ready State for an {@link UdpOutput}
 * @author marcus
 *
 */
final class UdpOutputReadyState extends UdpOutputState {

	/**
	 * Constructor
	 * 
	 * @param output
	 *            the output that can adopt this state
	 * @throws IllegalArgumentException
	 *             if argument 'output' is null
	 */
	protected UdpOutputReadyState(UdpOutput output)
			throws IllegalArgumentException {
		super(output);
	}

	@Override
	void beginTransmit() throws IllegalStateException {
		try {
			this.getOutput().getSemaphore().acquire();
		} catch(final InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		this.getOutput().getBytesToSend().clear();
		this.getOutput().setCurrentBufSize(0);
		this.getOutput().setCurrentState(this.getOutput().getTransmittingState());
	}

	@Override
	void endTransmit() throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	OutputStateEnum getState() {
		return OutputStateEnum.READY;
	}

	@Override
	void transmitDouble(double value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitFloat(float value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitByte(byte value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitShort(short value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitInt(int vaue) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitLong(long value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitString(String value) throws IllegalArgumentException,
			IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

	@Override
	void transmitBoolean(boolean value) throws IllegalStateException {
		throw new IllegalStateException("Output must be in \"Transmitting\" state in order to end transmission process.");
	}

}
