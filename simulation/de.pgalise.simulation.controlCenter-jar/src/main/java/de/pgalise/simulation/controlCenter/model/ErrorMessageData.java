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
package de.pgalise.simulation.controlCenter.model;

import de.pgalise.simulation.controlCenter.internal.message.MessageTypeEnum;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Data for error messages. This is useful to send the received message type.
 *
 * @author Timo
 */
@ManagedBean
@RequestScoped
public class ErrorMessageData
{

	/**
	 * Message
	 */
	private String errorMessage;

	/**
	 * Message type
	 */
	private MessageTypeEnum recievedMessageType;

	public ErrorMessageData() {
	}

	/**
	 * Constructor
	 *
	 * @param errorMessage
	 *                            Message
	 * @param recievedMessageType
	 *                            Message type
	 */
	public ErrorMessageData(String errorMessage, MessageTypeEnum recievedMessageType) {
		this.errorMessage = errorMessage;
		this.recievedMessageType = recievedMessageType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public MessageTypeEnum getReceivedMessageType() {
		return recievedMessageType;
	}

	public void setReceivedMessageType(MessageTypeEnum recievedMessageType) {
		this.recievedMessageType = recievedMessageType;
	}
}
