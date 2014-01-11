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
package de.pgalise.simulation.controlCenter.web.internal;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.controlCenter.ControlCenterUser;
import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationStoppedMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationUpdateMessage;
import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.shared.event.EventList;
import javax.servlet.http.HttpServlet;

/**
 * Servlet for Control Center. It creates one user, which can create and control
 * the simulation. It's not possible to enter the control center with more than
 * one user. This servlet can also receive update and stop information from the
 * simulation and sends it to the control center user.
 *
 * @author Timo
 */
public class CCWebSocketServlet extends HttpServlet {

	private static final long serialVersionUID = 3385629233339378162L;
	private static final Logger log = LoggerFactory.getLogger(
		CCWebSocketServlet.class);
	/**
	 * The user. Only one user.
	 */
	private static ControlCenterUser user = null;

	@EJB
	private GsonService gsonService;

	/**
	 * Gson to serialize and deserialize the messages.
	 */
	private Gson gson;

	/**
	 * Post construct method. Don't call this separately. It will be called on
	 * it's own after construct.
	 */
	@PostConstruct
	public void initGson() {
		this.gson = this.gsonService.getGson();
	}

	/**
	 * Removes an online user. Another user can enter the control center after
	 * this method.
	 *
	 * @param controlCenterUserWebSocket
	 */
	public static synchronized void removeUser(
		ControlCenterUser controlCenterUserWebSocket) {
		if (user == controlCenterUserWebSocket) {
			user = null;
		}
	}

	/**
	 * Send a message to the user.
	 *
	 * @param message will be parsed to JSON.
	 * @throws IOException
	 */
	public void sendMessage(ControlCenterMessage<?> message) throws IOException {
		if (user != null) {
			user.sendMessage(message);
		} else {
			throw new RuntimeException("No user online!");
		}
	}

	/**
	 * Produces a look up for the simulation controller.
	 *
	 * @param address
	 * @return
	 * @throws NamingException
	 */
	public SimulationController getSimulationController(String address) throws NamingException {
		String[] ipPortArray = address.split(":");
		String ip = ipPortArray[0];
		String port = ipPortArray[1];
		Properties props = new Properties();
		if (ip.equals("127.0.0.1") || ip.equals("localhost")) {
			props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.client.LocalInitialContextFactory");
			Context ctx = new InitialContext(props);

			return (SimulationController) ctx.lookup(
				"de.pgalise.simulation.SimulationControllerLocal");
		} else {
			props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.client.InitialContextFactory");
			props.put(Context.PROVIDER_URL,
				"http://" + ip + ":" + port + "/tomee/ejb");
			Context ctx = new InitialContext(props);

			return (SimulationController) ctx.lookup(
				"de.pgalise.simulation.SimulationController");
		}
	}

	/**
	 * Receives updates and stop from the simulation and sends it to the user.
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	@Override
	protected void doPost(HttpServletRequest req,
		HttpServletResponse resp) throws ServletException, IOException {
		if (user != null) {
			if (req.getParameter("update") != null) {
				user.sendMessage(new SimulationUpdateMessage(this.gson.fromJson(req.
					getParameter("json"),
					EventList.class).getTimestamp()));
			} else if (req.getParameter("stopped") != null && req.getParameter(
				"stopped").equalsIgnoreCase("true")) {
				log.debug("Simulation stopped");
				user.sendMessage(new SimulationStoppedMessage());
			}
		}
	}

	/**
	 * Returns the control center user.
	 *
	 * @return user or null, if no one is online.
	 */
	public static ControlCenterUser getUser() {
		return user;
	}
}
