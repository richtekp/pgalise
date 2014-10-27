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
 
package de.pgalise.simulation.operationCenter.internal.message;

import com.google.gson.Gson;

/**
 * The superclass for all messages.
 * Message to be passed over web socket after being parsed to JSON by GSON.
 * @author dhoeting
 * @author Timo
 * @param <T>
 */
public class OCWebSocketMessage<T> {
	private int messageType, messageID;
	private T content;

	/**
	 *  Empty Constructor (only to avoid null-reference-warnings)
	 */
	public OCWebSocketMessage() {
		this.messageID = OCWebSocketMessage.MessageType.DEFAULT_MESSAGE_ID;
	}
	
	/**
	 * Constructor
	 * @param messageType
	 * 			needs to be an known one of {@link MessageType}.
	 */
	public OCWebSocketMessage(int messageType) {
		super();
		this.messageType = messageType;
	}
	
	/**
	 * Constructor
	 * @param messageType
	 * 			needs to be an known one of {@link MessageType}.
	 * @param content
	 * 			the content is generic
	 */
	public OCWebSocketMessage(int messageType, T content) {
		super();
		this.messageType = messageType;
		this.content = content;
	}
	
	/**
	 * Constructor
	 * @param messageID 
	 * 		useful to response on a server message. This is only set, if the client
	 * 		has sent a message id before.
	 * @param messageType Type of message
	 * @param content the content of the message.
	 */
	public OCWebSocketMessage(int messageID, int messageType, T content) {
		super();
		this.messageType = messageType;
		this.messageID = messageID;
		this.content = content;
	}

	public int getMessageType() {
		return this.messageType;
	}
	
	public void setContent(T content) {
		this.content = content;
	}
	
	public T getContent() {
		return content;
	}
	
	/**
	 * Serializes this object.
	 * @return
	 */
	public String toJson(Gson gson) {
		return gson.toJson(this);
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	/**
	 * Self-made enum (appropriate for JSON transport, including fixed int bindings
	 * @author dhoeting
	 * @author Timo
	 */
	public static class MessageType {
		public static final int ERROR = -1;
		public static final int SIMULATION_START = 1;				// Server only
		public static final int SIMULATION_STOP = 3;				// Server only
		public static final int SIMULATION_INIT = 5;				// Server only
		public static final int SENSOR_DATA = 13;					// Server only
		public static final int GPS_SENSOR_TIMEOUT = 18; 			// Server only
		public static final int NEW_SENSORS = 19;					// Server only
		public static final int REMOVE_SENSORS = 20;				// Server only
		public static final int GATE_MESSAGE = 21; 					// Client only
		public static final int GENERIC_NOTIFICATION_MESSAGE = 22;	// Server only
		public static final int ON_CONNECT = 23;					// Server only
		public static final int NEW_VEHICLES_MESSAGE = 24;			// Server only
		public static final int REMOVE_VEHICLES_MESSAGE = 25;		// Server only
		public static final int DEFAULT_MESSAGE_ID = -99;			// Server only
	}
}
