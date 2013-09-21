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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.visualizationcontroller.OperationCenterController;

/**
 * Implementation of operation center controller. Sends all the information via HTTP to
 * the REST-ful interface of the simulation control center.
 * It will not send CityInfrastructureData to avoid traffic.
 * @author Timo
 */
@Lock(LockType.READ)
@Local
@Singleton(name = "de.pgalise.simulation.visualizationcontroller.OperationCenterControler")
public class DefaultOperationCenterController extends AbstractController<Event> implements OperationCenterController {
	private static final Logger log = LoggerFactory.getLogger(DefaultOperationCenterController.class);
	private static final String NAME = "OperationCenterController";
	private static final TypeToken<Collection<SensorHelper>> sensorCollectionTypeToken = new TypeToken<Collection<SensorHelper>>() {
	};
	private int connectionTimeout;
	private String servletURL;
	@EJB
	private GsonService gsonService;
	private Gson gson;

	/**
	 * Automatically called on post contruct.
	 */
	@PostConstruct
	public void onPostContruct() {
		this.gson = this.gsonService.getGson();
	}

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 */
	public DefaultOperationCenterController() throws IOException {

	}

	@Override
	public void createSensor(SensorHelper sensor) throws IllegalStateException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("createsensor", "true");
		requestParameterMap.put("json", gson.toJson(sensor));
		this.performRequest(requestParameterMap);
	}

	@Override
	public void deleteSensor(SensorHelper sensor) throws IllegalStateException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("deletesensor", "true");
		requestParameterMap.put("json", gson.toJson(sensor));
		this.performRequest(requestParameterMap);
	}

	@Override
	public void displayException(Exception exception) throws IllegalStateException {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("exception", exception.getMessage());
		this.performRequest(requestMap);
	}

	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public String getServletURL() {
		return this.servletURL;
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

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setServletURL(String servletURL) {
		this.servletURL = servletURL;
	}

	@Override
	public boolean statusOfSensor(SensorHelper sensor) throws SensorException, IllegalStateException {
		return false;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		log.debug("init");
		this.servletURL = param.getOperationCenterURL();
		InitParameter initParameter = new InitParameter(null, param.getServerConfiguration(),
				param.getStartTimestamp(), param.getEndTimestamp(), param.getInterval(),
				param.getClockGeneratorInterval(), param.getOperationCenterURL(), param.getControlCenterURL(),
				param.getTrafficFuzzyData(), param.getCityBoundary());
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("init", "true");
		requestParameterMap.put("json", this.gson.toJson(initParameter));
		this.performRequest(requestParameterMap);
	}

	@Override
	protected void onReset() {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("reset", "true");
	}

	@Override
	protected void onStart(StartParameter param) {
		log.debug("start");
		StartParameter startParameterCopy = new StartParameter(param.getCity(),
				param.isAggregatedWeatherDataEnabled(),
				param.getWeatherEventHelperList(),
				param.getBusRouteList());
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("start", "true");
		requestParameterMap.put("json", this.gson.toJson(startParameterCopy));
		this.performRequest(requestParameterMap);
	}

	@Override
	protected void onStop() {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("stop", "true");
		this.performRequest(requestParameterMap);
	}

	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("update", "true");
		requestParameterMap.put("json", this.gson.toJson(simulationEventList));		
		this.performRequest(requestParameterMap);
	}

	@Override
	protected void onResume() {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("resume", "true");
	}

	@Override
	public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("createsensors", "true");
		requestParameterMap.put("json", gson.toJson(sensors, sensorCollectionTypeToken.getType()));
		this.performRequest(requestParameterMap);
	}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		Map<String, String> requestParameterMap = new HashMap<>();
		requestParameterMap.put("deletesensors", "true");
		requestParameterMap.put("json", gson.toJson(sensors, sensorCollectionTypeToken.getType()));
		this.performRequest(requestParameterMap);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
