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
 
package de.pgalise.simulation.sensorFramework.output;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * {@link Output} which can contain several other {@link Output}s
 * 
 * @author Marcus
 */
public final class CompositeOutput implements Output {
	/**
	 * {@link Set} with {@link Output}s
	 */
	private final Set<Output> sensorOutputs = new HashSet<>();

	/**
	 * Creates a {@link CompositeOutput} with no {@link Output}s.
	 * attached.
	 */
	public CompositeOutput() {}

	/**
	 * Creates a {@link CompositeOutput} with the passed
	 * {@link Output}s.
	 * 
	 * @param sensorOutputs
	 *            the {@link Output}s to attach to this
	 *            {@link CompositeOutput}
	 * @throws IllegalArgumentException
	 *             if argument 'sensorOutputs' is 'null'
	 * @throws UnsupportedOperationException
	 *             if any of the passed {@link Output}s is an instance of
	 *             {@link CompositeOutput}
	 */
	public CompositeOutput(final Set<Output> sensorOutputs) throws IllegalArgumentException, UnsupportedOperationException {
		if(sensorOutputs == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensorOutputs"));
		}
		for(final Output sensorOutput : sensorOutputs) {
			this.attach(sensorOutput);
		}
	}

	/**
	 * Attaches the passed {@link Output} to this
	 * {@link CompositeOutput}. If the passed {@link Output} is
	 * already attached it won't be attached any more.
	 * 
	 * @param sensorOutput
	 *            the {@link Output} to be attached to this
	 *            {@link CompositeOutput}
	 * @return 'true' if the passed {@link Output} could have been
	 *         attached to this {@link CompositeOutput}, otherwise 'false'
	 * @throws IllegalArgumentException
	 *             if argument 'sensorOutput' is 'null'
	 * @throws UnsupportedOperationException
	 *             if argument 'sensorOutput' is an instance of
	 *             {@link CompositeOutput}
	 */
	public boolean attach(final Output sensorOutput) throws IllegalArgumentException, UnsupportedOperationException {
		if(sensorOutput == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensorOutput"));
		}
		if(sensorOutput instanceof CompositeOutput) {
			throw new UnsupportedOperationException("Argument 'sensorOutput' may not be a CompositSensorOutput.");
		}
		return this.sensorOutputs.add(sensorOutput);
	}

	/**
	 * Detaches the passed {@link Output} from this
	 * {@link CompositeOutput}.
	 * 
	 * @param sensorOutput
	 *            the {@link Output} to detach from this
	 *            {@link CompositeOutput}
	 * @return 'true' if the passed {@link Output} could be detached from
	 *         this {@link CompositeOutput}, otherwise 'false'
	 * @throws IllegalArgumentException
	 *             if argument 'sensorOutput' is 'null'
	 */
	public boolean detach(final Output sensorOutput) throws IllegalArgumentException {
		if(sensorOutput == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("sensorOutput"));
		}
		return this.sensorOutputs.remove(sensorOutput);
	}

	/**
	 * Removes all attached {@link Output}s of this
	 * {@link CompositeOutput}.
	 */
	public void clearAttached() {
		this.sensorOutputs.clear();
	}

	/**
	 * Invokes all attached {@link Output}'s 'beginTransmit'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void beginTransmit() throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.beginTransmit();
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'endTransmit'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void endTransmit() throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.endTransmit();
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitBoolean'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitBoolean(final boolean value) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitBoolean(value);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitByte'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitByte(final byte value) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitByte(value);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitShort'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitShort(final short value) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitShort(value);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitInt'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitInt(final int vaue) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitInt(vaue);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitLong'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitLong(final long value) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitLong(value);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitFloat'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitFloat(final float value) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitFloat(value);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitDouble'-method.
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitDouble(final double value) throws IllegalStateException {
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitDouble(value);
		}
	}

	/**
	 * Invokes all attached {@link Output}'s 'transmitString'-method.
	 * 
	 * @throws IllegalArgumentException
	 *             if argument 'value' is 'null'
	 * 
	 * @throws IllegalStateException
	 *             if any of the attached {@link Output}s throws it
	 */
	@Override
	public void transmitString(final String value) throws IllegalArgumentException, IllegalStateException {
		if(value == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("value"));
		}
		for(final Output sensorOutput : this.sensorOutputs) {
			sensorOutput.transmitString(value);
		}
	}

	/**
	 * Returns the current {@link OutputStateEnum} of this
	 * {@link CompositeOutput}. A non null value is only returned if all
	 * attached {@link Output}s have the same state.
	 * 
	 * @return 'null' if at least one of the attached {@link Output}s has
	 *         a different state than the others, otherwise the state of all
	 *         {@link Output} will be returned
	 */
	@Override
	public OutputStateEnum getState() {
		if(this.sensorOutputs.size() == 0) {
			return null;
		}
		final Iterator<Output> it = this.sensorOutputs.iterator();
		final OutputStateEnum first = it.next().getState();
		while(it.hasNext()) {
			if(it.next().getState() != first) {
				return null;
			}
		}
		return first;
	}
}
