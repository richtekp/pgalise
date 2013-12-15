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
 
package de.pgalise.simulation.operationCenter.internal.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;
import de.pgalise.simulation.operationCenter.internal.message.SensorDataMessage;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;

/**
 * The SendSensorDataStrategy is thread save and decides when it's time to send the sensor data to the user.
 * It collects all sensor data and send it to the users, when the subclass decided it.
 * @author Timo
 */
public abstract class SendSensorDataStrategy {
	private static final Logger logger = LoggerFactory.getLogger(SendSensorDataStrategy.class);
	private OCWebSocketService webSocketService;
	private List<Sensor<?,?>> sensorDataList;
	protected long startTimestamp, endTimestamp, interval;
	
	/**
	 * Constructor
	 * @param webSocketServlet
	 */
	public SendSensorDataStrategy(OCWebSocketService webSocketService) {
		this.webSocketService = webSocketService;
		this.sensorDataList = new ArrayList<>();
	}
	
	/**
	 * Inits the this strategy.
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param interval
	 */
	public void init(long startTimestamp, long endTimestamp, long interval) {
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.interval = interval;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	protected List<Sensor<?,?>> getSensorDataList() {
		return sensorDataList;
	}

	/**
	 * This will be called before a new sensor data is added to the list.
	 * If it returns true, it will send the current sensor data to the user and clears the list with old sensor data.
	 * @param timestamp
	 * 			current timestamp
	 * @return	true, if sensor data should be send now, false if not.
	 */
	protected abstract boolean isItTimeToSend(long timestamp);
	
	/**
	 * Adds new sensor data and send it to the user, if it's time to.
	 * @param timestamp
	 * @param sensorData
	 * @throws IOException
	 */
	public synchronized void addSensorData(long timestamp, Sensor sensorData) throws IOException {
		logger.debug("AddSensorData");
		if(this.isItTimeToSend(timestamp)) {
			logger.debug("SendSensorData");
			this.webSocketService.sendMessageToAllUsers(new SensorDataMessage(this.sensorDataList, timestamp));
			this.sensorDataList = new ArrayList<>();
		}
		this.sensorDataList.add(sensorData);
	}
}
