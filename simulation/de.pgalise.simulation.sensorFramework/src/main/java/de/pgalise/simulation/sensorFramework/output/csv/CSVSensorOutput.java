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
 
package de.pgalise.simulation.sensorFramework.output.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;
import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * Class for a CSV sensor output.
 * 
 * @author Andreas
 * @version 1.0
 */
public final class CSVSensorOutput implements Output {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CSVSensorOutput.class);

	/**
	 * File path for sensor output
	 */
	private final String filepath;

	/**
	 * Semaphore
	 */
	private final Semaphore semaphore = new Semaphore(1);

	/**
	 * FileWriter
	 */
	private FileWriter fileWriter;

	/**
	 * State of the sensor output
	 */
	private OutputStateEnum currentState;

	/**
	 * Number of the transmitted column
	 */
	private int transmittedColumn = 0;

	/**
	 * Constructor
	 * 
	 * @param filepath
	 *            File path
	 */
	public CSVSensorOutput(String filepath) {
		if (filepath.equals("")) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("filepath"));
		}
		this.filepath = filepath;
		this.currentState = OutputStateEnum.READY;
	}

	@Override
	public void beginTransmit() throws IllegalStateException {
		try {
			// Begin transmit
			this.semaphore.acquire();
			this.currentState = OutputStateEnum.TRANSMITTING;
			this.transmittedColumn = 0;

			// Create file writer
			try {
				this.fileWriter = new FileWriter(filepath, true);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			this.currentState = OutputStateEnum.READY;
		} finally {
			this.semaphore.release();
		}
	}

	@Override
	public void endTransmit() throws IllegalStateException {
		try {
			// End transmit
			this.fileWriter.flush();
			this.fileWriter.close();
		} catch (final IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			this.currentState = OutputStateEnum.READY;
			this.semaphore.release();
		}
	}

	@Override
	public OutputStateEnum getState() {
		return this.currentState;
	}

	@Override
	public void transmitBoolean(boolean value) throws IllegalStateException {
		this.transmitString(value + "");
	}

	@Override
	public void transmitByte(byte value) throws IllegalStateException {
		this.transmitString((int) value + "");
	}

	@Override
	public void transmitShort(short value) throws IllegalStateException {
		this.transmitString((int) value + "");
	}

	@Override
	public void transmitInt(int value) throws IllegalStateException {
		this.transmitString(value + "");
	}

	@Override
	public void transmitLong(long value) throws IllegalStateException {
		this.transmitString((long) value + "");
	}

	@Override
	public void transmitFloat(float value) throws IllegalStateException {
		this.transmitString(value + "");
	}

	@Override
	public void transmitDouble(double value) throws IllegalStateException {
		this.transmitString(value + "");
	}

	@Override
	public void transmitString(String value) throws IllegalArgumentException, IllegalStateException {
		try {
			if (this.currentState != OutputStateEnum.TRANSMITTING) {
				throw new IllegalStateException("Sensor is not transmitting!");
			}

			// Add to file
			this.fileWriter.append(value);
			this.fileWriter.append(';');

			// Add to new line
			if (this.transmittedColumn == 8) {
				this.fileWriter.append('\n');
				this.transmittedColumn = 0;
			} else {
				this.transmittedColumn++;
			}
		} catch (final IOException e) {
			log.debug(e.getMessage(), e);
		}
	}

	public String getFilepath() {
		return filepath;
	}

}
