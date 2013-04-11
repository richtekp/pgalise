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
 
package de.pgalise.simulation.sensorFramework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes the received data into a file.
 * 
 * @author mustafa
 */
public class FileOutputServer extends OutputStreamServer {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(FileOutputServer.class);

	/**
	 * Output file
	 */
	private File file;

	/**
	 * Constructor
	 * 
	 * @param file
	 *            Output file
	 * @param formatter Specification of the output string
	 *            Formatter
	 * @param port
	 *            Port
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public FileOutputServer(File file, Formatter formatter, int port) throws FileNotFoundException, IOException {
		super(init(file), formatter, port);
		this.file = file;
	}

	/**
	 * Creates a new output stream
	 * 
	 * @param file
	 *            Output fle
	 * @return new output stream
	 * @throws FileNotFoundException
	 */
	private static OutputStream init(File file) throws FileNotFoundException {
		if (file.exists()) {
			file.delete();
		}
		return new FileOutputStream(file);
	}

	@Override
	public void handleMessage(long time, int sensorId, byte sensorTypeId, double v0, double v1, byte v2, short v3,
			short v4, short v5) {
		if (!file.exists()) {
			log.info("Outputfile does not exists. Creating new file.");
			try {
				if (out != null) {
					this.releaseResources();
				}
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.handleMessage(time, sensorId, sensorTypeId, v0, v1, v2, v3, v4, v5);
	}
}
