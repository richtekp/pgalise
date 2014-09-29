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
 
package de.pgalise.simulation.sensorFramework.output.decorator;

import java.nio.ByteBuffer;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputDecorator;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;
import javax.ejb.Stateful;

/**
 * Output decorator to send data to Odysseus
 * @author marcus
 *
 */
@Stateful
public class Odysseus2Output extends OutputDecorator {

	public Odysseus2Output() {
	}

	/**
	 * Constructor
	 * @param sensorOutput the {@link Output} to decorate
	 */
	public Odysseus2Output(final Output sensorOutput) {
		super(sensorOutput);
	}

	@Override
	public void beginTransmit() {
		this.getSensorOutput().beginTransmit();
		
		int inputSize = (Long.SIZE+Integer.SIZE+Byte.SIZE
				+Double.SIZE+Double.SIZE+Byte.SIZE
				+Short.SIZE+Short.SIZE+Short.SIZE)/8;
		
		//transmit an input size at the beginning of tuple transmission
		byte[] bytes = ByteBuffer.allocate(4).putInt(inputSize).array();
		this.transmitByteArray(bytes);
	}

	@Override
	public void endTransmit() {
		this.getSensorOutput().endTransmit();
	}

	@Override
	public OutputStateEnum getState() {
		return this.getSensorOutput().getState();
	}

	@Override
	public void transmitBoolean(final boolean value) throws IllegalStateException {
		this.getSensorOutput().transmitBoolean(value);
	}

	@Override
	public void transmitByte(byte value) throws IllegalStateException {
		this.getSensorOutput().transmitByte(value);
	}

	@Override
	public void transmitShort(short value) throws IllegalStateException {
		this.getSensorOutput().transmitShort(value);
	}

	@Override
	public void transmitInt(int value) throws IllegalStateException {
		this.getSensorOutput().transmitInt(value);
	}

	@Override
	public void transmitLong(long value) throws IllegalStateException {
		this.getSensorOutput().transmitLong(value);
	}

	@Override
	public void transmitFloat(float value) throws IllegalStateException {
		this.getSensorOutput().transmitFloat(value);
	}

	@Override
	public void transmitDouble(double value) throws IllegalStateException {
		this.getSensorOutput().transmitDouble(value);
	}

	@Override
	public void transmitString(String value) throws IllegalArgumentException,
			IllegalStateException {
		this.getSensorOutput().transmitString(value);
	}

	@Override
	public void transmitByteArray(byte[] value) throws IllegalStateException {
		this.getSensorOutput().transmitByteArray(value);
	}
}
