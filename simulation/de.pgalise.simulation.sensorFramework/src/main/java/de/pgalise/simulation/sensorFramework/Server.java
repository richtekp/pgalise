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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A server you can use to forward simulation output data to.
 * It depends on the sub classes what happens to the received data (e.g. writing in a file).
 *  
 * @author mustafa
 *
 */
public abstract class Server implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(Server.class);
	protected final int port;
	protected final Formatter formatter;
	private Thread thread;
	private ServerSocket serverSocket;
	
	/**
	 * Helper class to specify the format of the output string
	 * @author mustafa
	 *
	 */
	public static interface Formatter {
		public String format(long time, int sensorId, byte sensorTypeId,
				double v0, double v1, byte v2, short v3, short v4, short v5);
	}
	
	public Server(Formatter formatter,  int port) throws IOException {
		this.port = port;
		this.formatter = formatter;
		this.thread = new Thread(this);
	}
	
	protected abstract void releaseResources();
	
	public void open() {
		this.thread.start();
	}
	
	public void listen() throws InterruptedException {
		this.thread.join();
	}
	
	public void close() {
		if(serverSocket!=null ) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void waitToOpen() {
		synchronized(this) {
			if(serverSocket==null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void run() {
		try (ServerSocket tmpSocket = new ServerSocket(port)) {
			this.serverSocket = tmpSocket;
			synchronized(this) {
				notify();
			}
			log.info("Listening on port " + port);
			log.info("Waiting to accept a new client.");
			Socket socket = tmpSocket.accept();
			log.info("Connected to client at " + socket.getInetAddress());

			while (true) {
				DataInputStream in = new DataInputStream(
						socket.getInputStream());
				// read meta data
				long time = in.readLong();
				int sensorId = in.readInt();
				byte sensorTypeId = in.readByte();
				// müssten eigentlich die weiteren sachen eingelesen werden...
				// 9-3 = 6 spalten noch auslesen
//				this.getOutput().transmitDouble(this.trafficLight.getAngle1());
//				this.getOutput().transmitDouble(this.trafficLight.getAngle2());
//				this.getOutput().transmitByte((byte) this.trafficLight.getState().getStateId()); // stateId
//				this.getOutput().transmitShort((short) 0);
//				this.getOutput().transmitShort((short) 0);
//				this.getOutput().transmitShort((short) 0);
				
				double v0 = in.readDouble();
				double v1 = in.readDouble();
				byte v2 = in.readByte();
				short v3 = in.readShort();
				short v4 = in.readShort();
				short v5 = in.readShort();
				
				handleMessage(time, sensorId, sensorTypeId,
						v0, v1, v2, v3, v4, v5);
			}
		} catch (Exception e) {
			log.error("Server socket closed", e);
		}
		releaseResources();
	}

	public abstract void handleMessage(long time, int sensorId, byte sensorTypeId,
			double v0, double v1, byte v2, short v3, short v4, short v5);
}
