/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.operationcenter;

import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author richter
 */
public interface OCWebSocketUser
{

	Charset charset = Charset.forName("UTF-8");
	CharsetDecoder decoder = charset.newDecoder();

	/**
	 * Send message to the user. The message will be serialized with gson first.
	 */
	void sendMessage(
					OCWebSocketMessage<?> message) throws IOException;

}
