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

import java.io.IOException;

/**
 * Writes the received data to  /dev/null (nirvana)
 * 
 * @author mustafa
 */
public class NullOutputServer extends Server {

	/**
	 * Constructor
	 * 
	 * @param port
	 *            Port
	 * @throws IOException
	 */
	public NullOutputServer(int port) throws IOException {
		super(null, port);
	}

	@Override
	protected void releaseResources() {
	}

	@Override
	public void handleMessage(long time, int sensorId, byte sensorTypeId, double v0, double v1, byte v2, short v3,
			short v4, short v5) {
	}
}
