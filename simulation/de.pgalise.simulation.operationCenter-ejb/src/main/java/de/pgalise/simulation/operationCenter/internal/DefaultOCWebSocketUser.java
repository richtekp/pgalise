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

import de.pgalise.simulation.operationcenter.OCWebSocketUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.pgalise.simulation.operationCenter.internal.message.ErrorMessage;
import de.pgalise.simulation.operationCenter.internal.message.GPSSensorTimeoutMessage;
import de.pgalise.simulation.operationCenter.internal.message.GateMessage;
import de.pgalise.simulation.operationCenter.internal.message.GenericNotificationMessage;
import de.pgalise.simulation.operationCenter.internal.message.NewSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;
import de.pgalise.simulation.operationCenter.internal.message.SensorDataMessage;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the server side representation of an online user in the
 * operation center.
 * An online user can send and receive messages, which are instance of
 * {@link OCWebSocketMessage}.
 *
 * @author Timo
 * @author Dennis
 */
@Stateless
public class DefaultOCWebSocketUser extends Endpoint implements
				OCWebSocketUser
{

	private static final Logger log = LoggerFactory.getLogger(
					DefaultOCWebSocketUser.class);
	private Session session;

	/**
	 * To serialize and deserialize the messages
	 */
	private Gson gson;

	/**
	 * To devide messages in more than one message
	 */
	private int maxSensorDataInOneMessage, maxSensorHelperInOneMessage;

	/**
	 * The oc web socket server that creates this instance
	 */
	private OCWebSocketService ocWebSocketService;
	@EJB
	private OCSimulationController oCSimulationController;

	public DefaultOCWebSocketUser() {
	}

	/**
	 * Constructor
	 *
	 * @param gson
	 * @param ocWebSocketService
	 */
	public DefaultOCWebSocketUser(Gson gson, OCWebSocketService ocWebSocketService) {
		this.gson = gson;
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
							"/properties.props");
			properties.load(is);
		} catch (Exception e) {
			try {
				is = DefaultOCWebSocketUser.class.getResourceAsStream(
								"/properties.props");
				properties.load(is);
			} catch (IOException e1) {
				throw new RuntimeException("Can not load properties!");
			}
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		this.maxSensorDataInOneMessage = Integer.valueOf(properties.getProperty(
						"MaxSensorDataInOneMessage"));
		this.maxSensorHelperInOneMessage = Integer.valueOf(properties.getProperty(
						"MaxSensorHelperInOneMessage"));
		this.ocWebSocketService = ocWebSocketService;
	}

	/**
	 * Invoked when message received.
	 * Every messages needs to be an with GSON serialized version of an
	 * {@link OCWebSocketMessage}.
	 *
	 * @param message
	 */
	private void onMessage(String message) {
		log.debug("Receiving: " + message);

		OCWebSocketMessage<?> webSocketMessage = this.gson.fromJson(message,
						OCWebSocketMessage.class);
		int type = webSocketMessage.getMessageType();

		switch (type) {
			case OCWebSocketMessage.MessageType.GATE_MESSAGE:
				log.debug("Recieved gate message");
				GateMessage gateMessage = this.gson.fromJson(message, GateMessage.class);
				try {
					oCSimulationController.handleGateMessage(gateMessage);
				} catch (JsonSyntaxException | IOException e1) {
					try {
						this.sendMessage(new ErrorMessage(gateMessage.getMessageID(), e1.
										getLocalizedMessage()));
					} catch (IOException e) {
						log.error("Exception", e);
					}
					return;
				}
				break;

			default:
				try {
					this.sendMessage(new ErrorMessage("Unknown message: " + message));
					return;
				} catch (IOException e) {
					log.error("Exception", e);
				}
				try {
					this.sendMessage(new GenericNotificationMessage(webSocketMessage.
									getMessageID(), ""));
				} catch (IOException e) {
					log.error("Exception", e);
				}
		}
	}

	/**
	 * Send message to the user. The message will be serialized with gson first.
	 *
	 * @param message
	 * @throws java.io.IOException
	 */
	public void sendMessage(OCWebSocketMessage<?> message) throws IOException {
		if (this.session == null) {
			return;
		}
		/* Some message types must be splited: */
		switch (message.getMessageID()) {
			case OCWebSocketMessage.MessageType.GPS_SENSOR_TIMEOUT:

				log.debug("Try to send gps time out.");

				GPSSensorTimeoutMessage gpsSensorTimeoutMessage = (GPSSensorTimeoutMessage) message;
				if (gpsSensorTimeoutMessage.getContent().size()
										> this.maxSensorDataInOneMessage) {
					List<Sensor<?,?>> tmpSensorDataList = new LinkedList<>();
					for (Sensor sensorData : gpsSensorTimeoutMessage.getContent()) {
						tmpSensorDataList.add(sensorData);
						if (tmpSensorDataList.size() % this.maxSensorDataInOneMessage == 0) {
							GPSSensorTimeoutMessage tmpMessage = new GPSSensorTimeoutMessage(
											tmpSensorDataList);
							String json = tmpMessage.toJson(this.gson);
							log.debug("Sending: " + json);
							this.session.getBasicRemote().sendText(json);
							tmpSensorDataList = new LinkedList<>();
						}
					}
					if (!tmpSensorDataList.isEmpty()) {
						GPSSensorTimeoutMessage tmpMessage = new GPSSensorTimeoutMessage(
										tmpSensorDataList);
						String json = tmpMessage.toJson(this.gson);
						log.debug("Sending: " + json);
						this.session.getBasicRemote().sendText(json);
					}
					return;
				}

			case OCWebSocketMessage.MessageType.NEW_SENSORS:
				NewSensorsMessage newSensorMessage = (NewSensorsMessage) message;
				if (newSensorMessage.getContent().size()
										> this.maxSensorHelperInOneMessage) {
					List<Sensor<?,?>> tmpSensorDataList = new LinkedList<>();
					for (Sensor sensor : newSensorMessage.getContent()) {
						tmpSensorDataList.add(sensor);
						if (tmpSensorDataList.size() % this.maxSensorHelperInOneMessage == 0) {
							NewSensorsMessage tmpMessage = new NewSensorsMessage(
											tmpSensorDataList);
							String json = tmpMessage.toJson(this.gson);
							log.debug("Sending: " + json);
							this.session.getBasicRemote().sendText(json);
							tmpSensorDataList = new LinkedList<>();
						}
					}
					if (!tmpSensorDataList.isEmpty()) {
						NewSensorsMessage tmpMessage = new NewSensorsMessage(
										tmpSensorDataList);
						String json = tmpMessage.toJson(this.gson);
						log.debug("Sending: " + json);
						this.session.getBasicRemote().sendText(json);
					}
					return;
				}

			case OCWebSocketMessage.MessageType.SENSOR_DATA:
				SensorDataMessage sensorDataMessage = (SensorDataMessage) message;
				if (sensorDataMessage.getContent().size()
										> this.maxSensorDataInOneMessage) {
					List<Sensor<?,?>> tmpSensorDataList = new LinkedList<>();
					for (Sensor sensorData : sensorDataMessage.getContent()) {
						tmpSensorDataList.add(sensorData);
						if (tmpSensorDataList.size() % this.maxSensorDataInOneMessage == 0) {
							SensorDataMessage tmpMessage = new SensorDataMessage(
											tmpSensorDataList, sensorDataMessage.getTime());
							String json = tmpMessage.toJson(this.gson);
							log.debug("Sending: " + json);
							this.session.getBasicRemote().sendText(json);
							tmpSensorDataList = new LinkedList<>();
						}
					}
					if (!tmpSensorDataList.isEmpty()) {
						SensorDataMessage tmpMessage = new SensorDataMessage(
										tmpSensorDataList, sensorDataMessage.getTime());
						String json = tmpMessage.toJson(this.gson);
						log.debug("Sending: " + json);
						this.session.getBasicRemote().sendText(json);
					}
					return;
				}
		}

		String json = message.toJson(this.gson);
		log.debug("Sending: " + json);
		this.session.getBasicRemote().sendText(json);
	}

	/**
	 * Message from client
	 */
	@OnMessage
	protected void onMessage(Session session, String message) {
		this.onMessage(message);
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
	}
}
