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
 
package de.pgalise.simulation.internal.visualizationcontroller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.google.gson.Gson;

import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.visualizationcontroller.ControlCenterController;

/**
 * Implementation of the control center controller. Sends all the information via HTTP to
 * the REST-ful interface of the simulation control center.
 * 
 * @author Timo
 */
@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.visualizationcontroller.ControlCenterController")
public class DefaultControlCenterController implements ControlCenterController {

	private static final String NAME = "ControlCenterController";
	private int connectionTimeout;
	private String servletURL;
	@EJB
	private GsonService gsonService;
	private Gson gson;

	/**
	 * Automatically called on post construct.
	 */
	@PostConstruct
	public void onPostContruct() {
		this.gson = this.gsonService.getGson();
	}

	/**
	 * Default
	 */
	public DefaultControlCenterController() {
	}

	@Override
	public void init(InitParameter param) throws InitializationException, IllegalStateException {
		this.servletURL = param.getControlCenterURL();

	}

	@Override
	public void reset() throws IllegalStateException {
	}

	@Override
	public void start(StartParameter param) throws IllegalStateException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("start", "true");
		this.performRequest(requestParameterMap);
	}

	@Override
	public void stop() throws IllegalStateException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("stop", "true");
		this.performRequest(requestParameterMap);
	}

	@Override
	public void update(EventList simulationEventList) throws IllegalStateException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("update", "true");
		requestParameterMap.put("json", this.gson.toJson(simulationEventList));
		this.performRequest(requestParameterMap);
	}

	@Override
	public StatusEnum getStatus() {
		return null;
	}

	@Override
	public void displayException(Exception exception) throws IllegalStateException {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("exception", exception.getMessage());
		this.performRequest(requestMap);
	}

	/**
	 * Performs the http request.
	 * 
	 * @param requestParameterMap
	 *            <Stirng = key, String = item>
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private void performRequest(Map<String, String> requestParameterMap) {
		StringBuilder parameters;
		try {
			HttpURLConnection connection;
			connection = (HttpURLConnection) new java.net.URL(this.servletURL).openConnection();
			connection.setRequestMethod("POST");

			connection.setDoOutput(true);
			connection.setConnectTimeout(this.connectionTimeout);

			parameters = new StringBuilder();
			for (java.util.Map.Entry<String, String> entry : requestParameterMap.entrySet()) {
				if (parameters.length() > 0) {
					parameters.append("&");
				}

				parameters.append(entry.getKey().replaceAll("\\s", "%20")).append("=").append(entry.getValue().replaceAll("\\s", "%20"));
			}
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(parameters.toString());
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Got responseCode: " + responseCode + " for url: " + this.servletURL);
			}
			connection.disconnect();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
