/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal.message;

import com.google.gson.Gson;

/**
 *
 * @author richter
 */
public interface CCWebSocketMessage<T> {

	T getContent();

	MessageTypeEnum getMessageType();

	void setContent(T content);

	/**
	 * Returns this instance as a json string.
	 * This will be used to serialize the message before shipping it to the client.
	 * @param gson
	 *            GSON object
	 * @return JSON as String
	 */
	String toJson(Gson gson);
	
}
