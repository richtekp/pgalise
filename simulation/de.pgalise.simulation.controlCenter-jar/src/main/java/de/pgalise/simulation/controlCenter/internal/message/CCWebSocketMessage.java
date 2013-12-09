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
 
package de.pgalise.simulation.controlCenter.internal.message;

import com.google.gson.Gson;

/**
 * Message to be passed over web socket after beeing parsed to JSON.
 * 
 * @author dhoeting
 * @author Timo
 */
public class CCWebSocketMessage<T extends Object> {
	/**
	 * Default ID
	 */
	public static int DEFAULT_ID = -1;

	/**
	 * Message id to invoke callback services on client.
	 */
	private int messageID;

	/**
	 * Message type
	 */
	private int messageType;

	/**
	 * Content
	 */
	private T content;

	/**
	 * Empty Constructor (only to avoid null-reference-warnings)
	 */
	public CCWebSocketMessage() {
		this.messageID = DEFAULT_ID;
	}

	/**
	 * Message ID will be the default ID {@link CCWebSocketMessage#DEFAULT_ID}
	 * 
	 * @param messageType
	 *            Message type
	 * @param content
	 *            Content
	 */
	public CCWebSocketMessage(int messageType, T content) {
		super();
		this.messageType = messageType;
		this.content = content;
	}

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            Message id to invoke callback services on client.
	 * @param messageType
	 *            Type of message
	 * @param content
	 *            Content
	 */
	public CCWebSocketMessage(int messageID, int messageType, T content) {
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
		return this.content;
	}

	/**
	 * Self-made enum (appropriate for JSON transport, including fixed int bindings
	 * New message types need to be added here and in the JavaScript client code.
	 * @author dhoeting
	 * @author Timo
	 */
	public static class MessageType {
		public static final int ERROR = -1;
		public static final int ON_CONNECT = 0; // By Server
		public static final int SIMULATION_START = 1; // By Server
		public static final int SIMULATION_PAUSED = 2; // By Server
		public static final int SIMULATION_STOP = 3; // By Server
		public static final int SIMULATION_RESUMED = 4; // By Server
		public static final int ACCESS_DENIED = 5; // By Server
		public static final int SIMULATION_START_PARAMETER = 10; // By Client and Server
		public static final int SIMULATION_EXPORT_PARAMETER = 11; // By Client
		public static final int SIMULATION_EXPORTET = 12; // By Server
		public static final int OSM_AND_BUSSTOP_FILE_MESSAGE = 13; // By Client
		public static final int ASK_FOR_VALID_NODE = 14; // By Client
		public static final int VALID_NODE = 15; // by Server
		public static final int OSM_PARSED = 16; // by Server
		public static final int LOAD_SIMULATION_START_PARAMETER = 17; // By Client
		public static final int SIMULATION_EVENT_LIST = 18; // By Client
		public static final int IMPORT_XML_START_PARAMETER = 20; // By Client
		public static final int CREATE_SENSORS_MESSAGE = 21; // By Client
		public static final int DELETE_SENSORS_MESSAGE = 22; // By Client
		public static final int CREATE_RANDOM_VEHICLES = 23; // By Client
		public static final int USED_IDS_MESSAGE = 24; // By Server
		public static final int CREATE_ATTRACTION_EVENTS_MESSAGE = 25; // By Client
		public static final int SIMULATION_RUNNING = 26; // By Server
		public static final int SIMULATION_STOPPED = 27; // By Server
		public static final int SIMULATION_UPDATE = 28; // By Server
		public static final int GENERIC_NOTIFICATION_MESSAGE = 100; // By Server
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + messageType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		CCWebSocketMessage other = (CCWebSocketMessage) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (messageType != other.messageType) {
			return false;
		}
		return true;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	/**
	 * Returns this instance as a json string.
	 * This will be used to serialize the message before shipping it to the client.
	 * @param gson
	 *            GSON object
	 * @return JSON as String
	 */
	public String toJson(Gson gson) {
		return gson.toJson(this);
	}
}
