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

import java.io.IOException;
import java.util.Set;

import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;

/**
 * An oc web socket service needs to handle all online users
 * and provide functionality for sending messages to all of them.
 * @author Timo
 */
public interface OCWebSocketService {	
	/**
	 * Returns the current websocket users.
	 * @return
	 */
	public Set<OCWebSocketUser> getOCWebSocketUsers();
	
	/**
	 * Sends a message to all users.
	 * @param message
	 * 			will be parsed to json
	 * @throws IOException
	 */
	public void sendMessageToAllUsers(OCWebSocketMessage<?> message) throws IOException;
	
	/**
	 * Adds a listener to observe new user events.
	 * @param listener
	 */
	public void registerNewUserEventListener(NewUserEventListener listener);
	
	/**
	 * Removes the new user event listener.
	 * @param listener
	 */
	public void removeNewUserEventListener(NewUserEventListener listener);
	
	/**
	 * Interface for all new user event listeners.
	 * @author Timo
	 */
	public interface NewUserEventListener {
		/**
		 * Handles a new user.
		 * @param user
		 * @throws IOException 
		 */
		public void handleNewUserEvent(OCWebSocketUser user) throws IOException;
	}

	/**
	 * Called if new web socket is opened.
	 * @param user
	 */
	public void handleNewOpenedWebSocket(OCWebSocketUser user);
}
