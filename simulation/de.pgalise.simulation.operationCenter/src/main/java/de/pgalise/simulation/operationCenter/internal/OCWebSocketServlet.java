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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;
import de.pgalise.simulation.service.GsonService;

/**
 * This servlet is the default implementation of an {@link OCWebSocketService}
 * and it creates the web socket users, because it's also an implementation
 * of {@link WebSocketServlet}.
 * 
 * @author Timo
 */
public class OCWebSocketServlet extends WebSocketServlet implements OCWebSocketService {
	private static final Logger LOG = LoggerFactory.getLogger(OCWebSocketServlet.class);
	private static final long serialVersionUID = 3385629233339378162L;
	/**
	 * All online users.
	 */
	private static final Set<OCWebSocketUser> onlineUsers = new CopyOnWriteArraySet<OCWebSocketUser>();

	@EJB
	private GsonService gsonService;
	
	private static final Set<NewUserEventListener> newUserEventListeners = new HashSet<>();

	/**
	 * Default
	 */
	public OCWebSocketServlet() {}
	
	/**
	 * removes an online user.
	 * 
	 * @param OperationCenterUserWebSocket
	 */
	public static void removeUser(OCWebSocketUser operationCenterUserWebSocket) {
		LOG.debug("remove user");
		synchronized (onlineUsers) {
			onlineUsers.remove(operationCenterUserWebSocket);
		}
		LOG.debug("Users online: " +onlineUsers.size());
	}

	@Override
	public Set<OCWebSocketUser> getOCWebSocketUsers() {
		return onlineUsers;
	}

	@Override
	public void sendMessageToAllUsers(OCWebSocketMessage<?> message) throws IOException {
		List<OCWebSocketUser> webSocketUsers = new ArrayList<>(onlineUsers);
		LOG.debug("Send message to all users. Userlist size: " + webSocketUsers.size());
		for (OCWebSocketUser user : webSocketUsers) {
			user.sendMessage(message);
		}
	}

	@Override
	protected StreamInbound createWebSocketInbound(String protocol, HttpServletRequest request) {
		return new OCWebSocketUser(this.gsonService.getGson(), this);
	}
	
	@Override
	public void handleNewOpenedWebSocket(OCWebSocketUser user) {
		synchronized (newUserEventListeners) {
			for(NewUserEventListener listener : newUserEventListeners) {
				try {
					listener.handleNewUserEvent(user);
				} catch (IOException e) {
					Log.error("Exception", e);
				}
			}
		}
		synchronized (onlineUsers) {
			LOG.debug("New User!");
			onlineUsers.add(user);
		}
	}

	@Override
	public void registerNewUserEventListener(NewUserEventListener listener) {
		synchronized (newUserEventListeners) {
			newUserEventListeners.add(listener);
		}
	}

	@Override
	public void removeNewUserEventListener(NewUserEventListener listener) {
		synchronized (newUserEventListeners) {
			newUserEventListeners.remove(listener);	
		}
	}
}
