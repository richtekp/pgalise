/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter;

import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import java.io.IOException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;

/**
 *
 * @author richter
 */
public interface ControlCenterUser
{

	/**
	 * Invoked when message received.
	 * All the messages needs to be a subclass of {@link ControlCenterMessage}.
	 * If a new message will be added, the behavior needs to be added here.
	 *
	 * @param message
	 */
	void onMessage(String message);

	/**
	 * Message from client
	 *
	 * @param message
	 * @param session
	 * @return
	 */
	@OnMessage
	String onMessage(String message, Session session);

	void onOpen(Session session, EndpointConfig config);

	/**
	 * Send a message to the client.
	 *
	 * @param message
	 * @throws IOException
	 */
	void sendMessage(ControlCenterMessage<?> message) throws IOException;

}
