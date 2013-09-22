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
 
package de.pgalise.simulation.service;

import de.pgalise.simulation.service.ServerConfiguration;
import java.io.Serializable;

import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;

/**
 * The init parameters will be send to every controller on init.
 * It contains all information needed in the init state.
 * @author Mustafa
 * @author Timo
 */
public class InitParameter implements Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -39986353888978216L;

	/**
	 * Information about the city infrastructure
	 */
	private CityInfrastructureData cityInfrastructureData;

	/**
	 * Information about the server configuration
	 */
	private ServerConfiguration serverConfiguration;

	/**
	 * Timestamp of the simulation start
	 */
	private long startTimestamp;

	/**
	 * Timestamp of the simulation end
	 */
	private long endTimestamp;

	/**
	 * Simulation interval
	 */
	private long interval;

	/**
	 * Clock generator interval
	 */
	private long clockGeneratorInterval;

	/**
	 * URL to the operation center
	 */
	private String operationCenterURL;
	
	/**
	 * URL to the control center
	 */
	private String controlCenterURL;

	/**
	 * Traffic fuzzy data
	 */
	private TrafficFuzzyData trafficFuzzyData;

	/**
	 * City boundary
	 */
	private Boundary cityBoundary;

	/**
	 * Default
	 */
	public InitParameter() {
	}

	/**
	 * Constructor
	 * 
	 * @param cityInfrastructureData
	 *            Information about the city infrastructure
	 * @param serverConfiguration
	 *            Information about the server configuration
	 * @param startTimestamp
	 *            Timestamp of the simulation start
	 * @param endTimestamp
	 *            Timestamp of the simulation end
	 * @param interval
	 *            Simulation interval
	 * @param clockGeneratorInterval
	 *            Clock generator interval
	 * @param operationCenterURL
	 *            URL to the operation center
	 * @param controlCenterURL
	 * 			  URL to the control center
	 * @param trafficFuzzyData
	 *            Traffic fuzzy data
	 * @param cityBoundary
	 *            City boundary
	 */
	public InitParameter(CityInfrastructureData cityInfrastructureData, ServerConfiguration serverConfiguration,
			long startTimestamp, long endTimestamp, long interval, long clockGeneratorInterval,
			String operationCenterURL, String controlCenterURL,
			TrafficFuzzyData trafficFuzzyData, Boundary cityBoundary) {
		super();
		this.cityInfrastructureData = cityInfrastructureData;
		this.serverConfiguration = serverConfiguration;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.interval = interval;
		this.clockGeneratorInterval = clockGeneratorInterval;
		this.operationCenterURL = operationCenterURL;
		this.trafficFuzzyData = trafficFuzzyData;
		this.cityBoundary = cityBoundary;
		this.controlCenterURL = controlCenterURL;
	}

	public CityInfrastructureData getCityInfrastructureData() {
		return this.cityInfrastructureData;
	}

	public long getClockGeneratorInterval() {
		return this.clockGeneratorInterval;
	}

	public long getEndTimestamp() {
		return this.endTimestamp;
	}

	public long getInterval() {
		return this.interval;
	}

	public String getOperationCenterURL() {
		return this.operationCenterURL;
	}

	public ServerConfiguration getServerConfiguration() {
		return this.serverConfiguration;
	}

	public long getStartTimestamp() {
		return this.startTimestamp;
	}

	public void setCityInfrastructureData(CityInfrastructureData cityInfrastructureData) {
		this.cityInfrastructureData = cityInfrastructureData;
	}

	public void setClockGeneratorInterval(long clockGeneratorInterval) {
		this.clockGeneratorInterval = clockGeneratorInterval;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setOperationCenterURL(String operationCenterURL) {
		this.operationCenterURL = operationCenterURL;
	}

	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public TrafficFuzzyData getTrafficFuzzyData() {
		return trafficFuzzyData;
	}

	public void setTrafficFuzzyData(TrafficFuzzyData trafficFuzzyData) {
		this.trafficFuzzyData = trafficFuzzyData;
	}

	public Boundary getCityBoundary() {
		return cityBoundary;
	}

	public void setCityBoundary(Boundary cityBoundary) {
		this.cityBoundary = cityBoundary;
	}

	public String getControlCenterURL() {
		return controlCenterURL;
	}

	public void setControlCenterURL(String controlCenterURL) {
		this.controlCenterURL = controlCenterURL;
	}
}
