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
import java.io.OutputStream;

/**
 * Directs the received data to an outputstream.
 * You can extend this class to specify the type of the outputstream (e.g. FileOutputStream)
 * 
 * @author mustafa
 */
public class OutputStreamServer extends Server {

	/**
	 * Output stream
	 */
	protected OutputStream out;

	/**
	 * Formatter
	 */
	protected Formatter formatter;

	/**
	 * Constructor
	 * 
	 * @param out
	 *            Output stream
	 * @param formatter Specification the format of the output string.
	 *            Formatter
	 * @param port
	 *            Port
	 * @throws IOException
	 */
	public OutputStreamServer(OutputStream out, Formatter formatter, int port) throws IOException {
		super(formatter, port);
		this.out = out;
	}

	@Override
	public void handleMessage(long time, int sensorId, byte sensorTypeId, double v0, double v1, byte v2, short v3,
			short v4, short v5) {
		String str = null;
		if (formatter != null) {
			str = formatter.format(time, sensorId, sensorTypeId, v0, v1, v2, v3, v4, v5) + "\n";
		} else {
			str = "" + time + ";" + sensorId + ";" + sensorTypeId + ";" + v0 + ";" + v1 + ";" + v2 + ";" + v3 + ";"
					+ v4 + ";" + v5 + "\n";
		}
		try {
			out.write(str.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * For tests
	 */
	public static void main(String args[]) throws IOException, InterruptedException {
		int port = 6666;
		if (args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
			System.out.println("Starting server on port " + port);
		}
		Server server = new OutputStreamServer(System.out, null, port);
		server.open();
		server.listen();
	}

	@Override
	protected void releaseResources() {
		try {
			if (out != null)
				this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
