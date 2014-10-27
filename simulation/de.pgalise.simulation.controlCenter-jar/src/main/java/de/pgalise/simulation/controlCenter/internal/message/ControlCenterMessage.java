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
 * @param <T>
 */
public class ControlCenterMessage<T> {

	/**
	 * Message type
	 */
	private MessageTypeEnum messageType;

	/**
	 * Content
	 */
	private T content;

	/**
	 * Empty Constructor (only to avoid null-reference-warnings)
	 */
	protected ControlCenterMessage() {
	}

	/**
	 * Message ID will be the default ID {@link CCWebSocketMessage#DEFAULT_ID}
	 * 
	 * @param id
	 *            Message type
	 * @param content
	 *            Content
	 */
	public ControlCenterMessage(T content) {
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
	public ControlCenterMessage( MessageTypeEnum messageType, T content) {
		this(	content);
		this.messageType = messageType;
	}

	public MessageTypeEnum getMessageType() {
		return this.messageType;
	}

	public void setContent(T content) {
		this.content = content;
	}

	public T getContent() {
		return this.content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + messageType.getValue();
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
		ControlCenterMessage other = (ControlCenterMessage) obj;
		if (content == null) {
			if (other.getContent() != null) {
				return false;
			}
		} else if (!content.equals(other.getContent())) {
			return false;
		}
		if (messageType != other.getMessageType()) {
			return false;
		}
		return true;
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
