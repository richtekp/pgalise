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
 
package de.pgalise.simulation.operationCenter.internal.hqf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;

import de.pgalise.simulation.operationCenter.internal.hqf.persistence.DefaultHQFPersistenceService;
import de.pgalise.simulation.operationCenter.internal.hqf.persistence.HQFData;
import de.pgalise.simulation.operationCenter.internal.hqf.persistence.HQFPersistenceService;

/**
 * Listens to the infoSphere output stream, collects the sensor data and sends it to the client.
 * 
 * @author Kamil
 */
public class DefaultHQFStreamController implements OCHQFDataStreamController {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultHQFStreamController.class);

	/**
	 * Thread
	 */
	private Thread listenHQFStreamThread;

	/**
	 * Persistence Service
	 */
	private HQFPersistenceService hqfPersistenceService;

	/**
	 * Default
	 */
	public DefaultHQFStreamController() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void unlistenStream() {
		this.listenHQFStreamThread.stop();
	}

	@Override
	public void listenStream(String socketIP, int socketHQFDataPort) throws UnknownHostException, IOException {
		// log.debug("Socket: " + socketIP + " - Port: " + socketHQFDataPort);
		Socket staticHQFDataSocket = new Socket(socketIP, socketHQFDataPort);
		// log.debug("Listener Sockets created");
		this.listenHQFStreamThread = new ListenHQFStreamThread(staticHQFDataSocket);
		this.listenHQFStreamThread.start();
		this.hqfPersistenceService = new DefaultHQFPersistenceService(new DerbyJDBCConnection());
		this.hqfPersistenceService.deleteData();
	}

	/**
	 * Gives an update to the oc simulation controller.
	 * 
	 * @param sensorData
	 */
	private void update(HQFData hqfData) {
		try {
			log.debug("update: " + hqfData.getSimulationTimestamp() + " - " + hqfData.getSensorTypeID());
			this.hqfPersistenceService.insertData(hqfData);
		} catch (Exception e) {
			log.error("Exception", e);
		}

	}

	/**
	 * Reads the static sensor stream
	 * 
	 * @author Timo
	 */
	private class ListenHQFStreamThread extends Thread {

		/**
		 * Socket
		 */
		private Socket socket;

		/**
		 * Reader
		 */
		private BufferedReader in;

		/**
		 * Constructor
		 * 
		 * @param socket
		 *            Socket
		 * @throws IOException
		 */
		private ListenHQFStreamThread(Socket socket) throws IOException {
			super();
			this.socket = socket;
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			this.socket.setKeepAlive(true);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			CSVParse parser = new CSVParser(in);
			try {
				for (String[] text = parser.getLine(); text != null; text = parser.getLine()) {
					try {
						HQFData hqfData = null;
						// log.debug("***** Neues HQFElement: "+text.length);
						// for (int i = 0; i < text.length; i++) {
						// log.debug("Elemente " + i + ":" + text[i]);
						// }
						// log.debug("***** HQFElement Ende");
						// log.debug("Stream: "+text.toString());
						hqfData = new HQFData(new Timestamp(Long.valueOf(text[0])), Integer.valueOf(text[2]),
								Integer.valueOf(text[3]));
						log.debug(Long.valueOf(text[0]) + " SensortypeID: " + hqfData.getSensorTypeID() + " Anzahl: "
								+ hqfData.getAmount());

						DefaultHQFStreamController.this.update(hqfData);
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}
				}
			} catch (Exception e) {
				log.error("Exception", e);
			}
			try {
				socket.close();
			} catch (Exception e) {
				log.error("Exception", e);
			}
			try {
				in.close();
			} catch (Exception e) {
				log.error("Exception", e);
			}

			this.stop();
		}
	}
}
