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

import java.util.Random;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputDecorator;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;
import de.pgalise.simulation.service.RandomSeedService;

/**
 * Output decorator to imitate radio transmission
 * @author marcus
 *
 */
public final class RadioOutput extends OutputDecorator{

	/** Random generator **/
	private final Random random;
	
	/**
	 * Constructor
	 * @param sensorOutput the {@link Output} to decorate
	 * @param randomSeedService the {@link RandomSeedService}
	 */
	public RadioOutput(final Output sensorOutput, final RandomSeedService randomSeedService) {
		super(sensorOutput);
		if(randomSeedService == null) {
			throw new IllegalArgumentException("randomSeedService may not be null");
		}
		this.random = new Random(randomSeedService.getSeed(RadioOutput.class.getName()));
	}

	@Override
	public void beginTransmit() throws IllegalStateException {
		this.getOutput().beginTransmit();
	}

	@Override
	public void endTransmit() throws IllegalStateException {
		this.getOutput().endTransmit();
	}

	@Override
	public OutputStateEnum getState() {
		return this.getOutput().getState();
	}

	@Override
	public void transmitBoolean(boolean value) throws IllegalStateException {
		this.getOutput().transmitBoolean(this.random.nextDouble() <= 0.01 ? !value : value);
	}

	@Override
	public void transmitByte(byte value) throws IllegalStateException {
		byte diff = 0;
		if (this.random.nextDouble() <= 0.02) {
			diff = 1;
		}
		if (this.random.nextDouble() <= 0.01) {
			diff = 2;
		}
		this.getOutput().transmitByte((byte) (this.random.nextBoolean() ? value + diff : value - diff));
	}

	@Override
	public void transmitShort(short value) throws IllegalStateException {
		short diff = 0;
		if (this.random.nextDouble() <= 0.02) {
			diff = 1;
		}
		if (this.random.nextDouble() <= 0.01) {
			diff = 2;
		}
		this.getOutput().transmitShort((short) (this.random.nextBoolean() ? value + diff : value - diff));
	}

	@Override
	public void transmitInt(int value) throws IllegalStateException {
		int diff = 0;
		if (this.random.nextDouble() <= 0.02) {
			diff = 1;
		}
		if (this.random.nextDouble() <= 0.01) {
			diff = 2;
		}
		this.getOutput().transmitInt(this.random.nextBoolean() ? value + diff : value - diff);

	}

	@Override
	public void transmitLong(long value) throws IllegalStateException {
		long diff = 0;
		if (this.random.nextDouble() <= 0.02) {
			diff = 1;
		}
		if (this.random.nextDouble() <= 0.01) {
			diff = 2;
		}
		this.getOutput().transmitLong(this.random.nextBoolean() ? value + diff : value - diff);

	}

	@Override
	public void transmitFloat(float value) throws IllegalStateException {
		final float diff = this.random.nextFloat() * 0.1f;
		this.getOutput().transmitFloat(this.random.nextBoolean() ? value + diff : value - diff);
	}

	@Override
	public void transmitDouble(double value) throws IllegalStateException {
		final double diff = this.random.nextFloat() * 0.1f;
		this.getOutput().transmitDouble(this.random.nextBoolean() ? value + diff : value - diff);
	}

	@Override
	public void transmitString(String value) throws IllegalArgumentException {
		this.getOutput().transmitString(value);
		
	}

}
