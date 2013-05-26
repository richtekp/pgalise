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
 
package de.pgalise.simulation.operationCenter.internal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.GPSSensorData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SimpleSensorData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.TopoRadarSensorData;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.TrafficLightSensorData;

/**
 * Listens to the infoSphere output stream, collects the sensor data and updates the given
 * {@link OCSimulationController}.
 * 
 * @author Timo
 */
public class DefaultOCSensorStreamController implements OCSensorStreamController {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultOCSensorStreamController.class);

	/**
	 * Thread
	 */
	private Thread listenStaticSensorStreamThread, listenDynamicSensorStreamThread, listenTopoRadarSensorStreamThread,
			listenTrafficLightSensorStreamThread;

	/**
	 * OC simulation controller
	 */
	private OCSimulationController ocSimulationController;

	/**
	 * Default constructor
	 */
	public DefaultOCSensorStreamController() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void unlistenStream() {
		this.listenStaticSensorStreamThread.stop();
		this.listenDynamicSensorStreamThread.stop();
		this.listenTopoRadarSensorStreamThread.stop();
		this.listenTrafficLightSensorStreamThread.stop();
	}

	/**
	 * Gives an update to the oc simulation controller.
	 * 
	 * @param timestamp
	 *            Simulation timestamp
	 * @param sensorData
	 *            data
	 */
	private void update(long timestamp, SensorData sensorData) {
		try {
			log.debug("Send sensor data to oc simulation controller!");
			this.ocSimulationController.update(timestamp, sensorData);
		} catch (Exception e) {
			log.error("Exception", e);
		}

	}

	/**
	 * Reads the static sensor stream and informs the ocsimulationcontroller.
	 * 
	 * @author Timo
	 */
	private class ListenStaticSensorStreamThread extends Thread {

		private Socket socket;
		private BufferedInputStream in;

		/**
		 * Constructor
		 * 
		 * @param socket
		 * @param ocSimulationController
		 * @throws IOException
		 */
		private ListenStaticSensorStreamThread(Socket socket) throws IOException {
			super();
			this.socket = socket;
			this.in = new BufferedInputStream(socket.getInputStream());
			this.socket.setKeepAlive(true);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			InputStreamDecoder parser = new InputStreamDecoder(in);
			try {
				for (String[] text = parser.getLine(); text != null; text = parser.getLine()) {
					try {
						SensorData sensorData = null;

						long currentTimestamp = Long.valueOf(text[0]);
						sensorData = new SimpleSensorData(Integer.valueOf(text[2]), Integer.valueOf(text[1]),
								Double.valueOf(text[3]));
						log.debug(new Date(currentTimestamp) + " SensorId: " + sensorData.getId() + " Sensortype: "
								+ sensorData.getType());

						DefaultOCSensorStreamController.this.update(currentTimestamp, sensorData);
					} catch (Exception e) {
						log.error("Exception", e);
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

	/**
	 * Reads the dynamic sensor stream and informs the ocsimulationcontroller.
	 * 
	 * @author Timo
	 */
	private class ListenDynamicSensorStreamThread extends Thread {

		private Socket socket;
		private BufferedInputStream in;

		/**
		 * Constructor
		 * 
		 * @param socket
		 * @param ocSimulationController
		 * @throws IOException
		 */
		private ListenDynamicSensorStreamThread(Socket socket) throws IOException {
			super();
			this.socket = socket;
			this.socket.setKeepAlive(true);
			this.in = new BufferedInputStream(socket.getInputStream());
			log.debug("ListenDynamicSensorStreamThread created.");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			InputStreamDecoder parser = new InputStreamDecoder(in);
			try {
				for (String[] text = parser.getLine(); text != null; text = parser.getLine()) {
					try {
						/*
						 * (0) long timestamp, (1) int sensorid, (2) byte sensortype, (3) double measurevalue1, (4)
						 * double measurevalue2, (5) double distanceInMperStep, (6) int totalDistanceInM, (7) byte
						 * speedInKmh, (8) byte AvgSpeedInKmh, (9) short directionInGrad, (10) long travelTimeInMs.
						 */
						SensorData sensorData = null;
//						long traffelTimeInMS = 0;
//						try {
//							traffelTimeInMS = Long.valueOf(text[10]);
//						} catch (NumberFormatException e) {
//							log.debug("Exception", e);
//						}
						long currentTimestamp = Long.valueOf(text[0]);
						sensorData = new GPSSensorData(Integer.valueOf(text[2]), Integer.valueOf(text[1]),
								Double.valueOf(text[3]), Double.valueOf(text[4]),
								0d, 0, 0, 0, 0, 0l);
//								Double.valueOf(text[5]), Integer.valueOf(text[6]),
//								Integer.valueOf(text[7]), Integer.valueOf(text[8]), 
//								Integer.valueOf(text[9]), traffelTimeInMS);
						log.debug(new Date(currentTimestamp) + " SensorId: " + sensorData.getId() + " Sensortype: "
								+ sensorData.getType());

						DefaultOCSensorStreamController.this.update(currentTimestamp, sensorData);
					} catch (Exception e) {
						log.error("Exception", e);
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
			log.debug("ListenDynamicSensorStreamThread closed.");
		}
	}

	/**
	 * Reads the topo radar sensor stream and informs the ocsimulationcontroller.
	 * 
	 * @author Timo
	 */
	private class ListenTopoRadarSensorStreamThread extends Thread {

		private Socket socket;
		private BufferedInputStream in;

		/**
		 * Constructor
		 * 
		 * @param socket
		 * @param ocSimulationController
		 * @throws IOException
		 */
		private ListenTopoRadarSensorStreamThread(Socket socket) throws IOException {
			super();
			this.socket = socket;
			this.socket.setKeepAlive(true);
			this.in = new BufferedInputStream(socket.getInputStream());
			log.debug("ListenDynamicSensorStreamThread created.");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			InputStreamDecoder parser = new InputStreamDecoder(in);
			try {
				for (String[] text = parser.getLine(); text != null; text = parser.getLine()) {
					try {
						SensorData sensorData = null;

						long currentTimestamp = Long.valueOf(text[0]);
						sensorData = new TopoRadarSensorData(Integer.valueOf(text[2]), Integer.valueOf(text[1]),
								Integer.valueOf(text[3]), Integer.valueOf(text[4]), Integer.valueOf(text[5]),
								Integer.valueOf(text[6]));
						log.debug(new Date(currentTimestamp) + " SensorId: " + sensorData.getId() + " Sensortype: "
								+ sensorData.getType());

						DefaultOCSensorStreamController.this.update(currentTimestamp, sensorData);
					} catch (Exception e) {
						log.error("Exception", e);
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
			log.debug("ListenTopoRadarSensorStreamThread closed.");
		}
	}

	/**
	 * Reads the traffic light sensor stream and informs the ocsimulationcontroller.
	 * 
	 * @author Timo
	 */
	private class ListenTrafficLightSensorStreamThread extends Thread {

		private Socket socket;
		private BufferedInputStream in;

		/**
		 * Constructor
		 * 
		 * @param socket
		 * @param ocSimulationController
		 * @throws IOException
		 */
		private ListenTrafficLightSensorStreamThread(Socket socket) throws IOException {
			super();
			this.socket = socket;
			this.socket.setKeepAlive(true);
			this.in = new BufferedInputStream(socket.getInputStream());
			log.debug("ListenDynamicSensorStreamThread created.");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			InputStreamDecoder parser = new InputStreamDecoder(in);
			try {
				for (String[] text = parser.getLine(); text != null; text = parser.getLine()) {
					try {
						SensorData sensorData = null;

						long currentTimestamp = Long.valueOf(text[0]);
						sensorData = new TrafficLightSensorData(Integer.valueOf(text[6]), Integer.valueOf(text[5]),
								Double.valueOf(text[3]), Double.valueOf(text[4]), Integer.valueOf(text[1]));
						log.info(new Date(currentTimestamp) + " SensorId: " + sensorData.getId() + " Sensortype: "
								+ sensorData.getType());

						DefaultOCSensorStreamController.this.update(currentTimestamp, sensorData);
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}

			this.stop();
			log.debug("ListenTrafficLightSensorStreamThread closed.");
		}
	}

	@Override
	public void listenStream(OCSimulationController ocSimulationController, String socketStaticSensorIP,
			int socketStaticSensorPort, String socketDynamicSensorIP, int socketDynamicSensorPort,
			String socketTopoRadarIP, int socketTopoRadarPort, String socketTrafficLightIP, int socketTrafficLightPort)
			throws UnknownHostException, IOException {
		Socket staticSensorSocket = new Socket(socketStaticSensorIP, socketStaticSensorPort);
		Socket dynamicSensorSocket = new Socket(socketDynamicSensorIP, socketDynamicSensorPort);
		Socket topoRadarSensorSocket = new Socket(socketTopoRadarIP, socketTopoRadarPort);
		Socket trafficLightSensorSocket = new Socket(socketTrafficLightIP, socketTrafficLightPort);
		log.debug("Listener Sockets created");
		this.ocSimulationController = ocSimulationController;
		this.listenStaticSensorStreamThread = new ListenStaticSensorStreamThread(staticSensorSocket);
		this.listenStaticSensorStreamThread.start();
		this.listenDynamicSensorStreamThread = new ListenDynamicSensorStreamThread(dynamicSensorSocket);
		this.listenDynamicSensorStreamThread.start();
		this.listenTopoRadarSensorStreamThread = new ListenTopoRadarSensorStreamThread(topoRadarSensorSocket);
		this.listenTopoRadarSensorStreamThread.start();
		this.listenTrafficLightSensorStreamThread = new ListenTrafficLightSensorStreamThread(trafficLightSensorSocket);
		this.listenTrafficLightSensorStreamThread.start();
	}
}
