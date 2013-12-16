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

import java.io.Serializable;

/**
 * Interface for the data output of an sensor
 *
 * @author Marcus
 * @version 1.0 (Aug 29, 2012)
 */
public interface Output extends Serializable {

	/**
	 * Starts the transmission process.
	 *
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void beginTransmit() throws IllegalStateException;

	/**
	 * Ends the transmission process.
	 *
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void endTransmit() throws IllegalStateException;

	/**
	 * Returns the current state of the SensorOutput as {@link OutputStateEnum}.
	 *
	 * @return the current state of the SensorOutput as {@link OutputStateEnum}
	 */
	OutputStateEnum getState();

	/**
	 * Transmits the passed boolean value.
	 *
	 * @param value any boolean value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitBoolean(final boolean value) throws IllegalStateException;

	/**
	 * Transmits the passed byte value.
	 *
	 * @param value any byte value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitByte(final byte value) throws IllegalStateException;

	/**
	 * Transmits the passed short value.
	 *
	 * @param value any short value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitShort(final short value) throws IllegalStateException;

	/**
	 * Transmits the passed int value.
	 *
	 * @param value any int value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitInt(final int value) throws IllegalStateException;

	/**
	 * Transmits the passed long value.
	 *
	 * @param value any long value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitLong(final long value) throws IllegalStateException;

	/**
	 * Transmits the passed float value.
	 *
	 * @param value any float value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitFloat(final float value) throws IllegalStateException;

	/**
	 * Transmits the passed double value.
	 *
	 * @param value any double value
	 * @throws {@link IllegalStateException} if output is not in \"Transmitting\"
	 * state
	 */
	void transmitDouble(final double value) throws IllegalStateException;

	/**
	 * Transmits the passed string.
	 *
	 * @param value any string except null
	 * @throws IllegalArgumentException if argument 'value' is 'null'
	 * @throws IllegalStateException if output is not in \"Transmitting\" state
	 */
	void transmitString(final String value) throws IllegalArgumentException, IllegalStateException;

	void transmitByteArray(final byte[] value) throws IllegalStateException;
}
