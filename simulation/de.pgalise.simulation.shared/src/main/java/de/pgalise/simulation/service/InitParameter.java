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

import com.vividsolutions.jts.geom.Envelope;
import java.io.Serializable;

import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.validation.constraints.NotNull;

/**
 * The init parameters will be send to every controller on init. It contains all
 * information needed in the init state.
 *
 * @author Mustafa
 * @author Timo
 */
@ManagedBean
public class InitParameter implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -39986353888978216L;

	/**
	 * Timestamp of the simulation start
	 */
	/*
	 Has to be a java.util.Date in order to work out-of-the box with Primefaces p:calendar
	 */
	@NotNull
	private Date startTimestamp;

	/**
	 * Timestamp of the simulation end
	 */
	/*
	 Has to be a java.util.Date in order to work out-of-the box with Primefaces p:calendar
	 */
	@NotNull
	private Date endTimestamp;

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
	private URL operationCenterURL;

	/**
	 * URL to the control center
	 */
	private URL controlCenterURL;

	/**
	 * Traffic fuzzy data
	 */
	private TrafficFuzzyData trafficFuzzyData;

	/**
	 * City boundary
	 */
	private Envelope cityBoundary;

	/**
	 * Default
	 */
	public InitParameter() {
		try {
			this.operationCenterURL = new URL("http://localhost:8080/operationCenter");
			this.controlCenterURL = new URL("http://localhost:8080/controlCenter");
		} catch (MalformedURLException ex) {
			Logger.getLogger(InitParameter.class.getName()).log(Level.SEVERE,
				null,
				ex);
		}
		startTimestamp = new Date();
		endTimestamp = new Date(startTimestamp.getTime() + 1000 * 60 * 60);
	}

	/**
	 * Constructor
	 *
	 * @param startTimestamp Timestamp of the simulation start
	 * @param endTimestamp Timestamp of the simulation end
	 * @param interval Simulation interval
	 * @param clockGeneratorInterval Clock generator interval
	 * @param operationCenterURL URL to the operation center
	 * @param controlCenterURL URL to the control center
	 * @param trafficFuzzyData Traffic fuzzy data
	 * @param cityBoundary City boundary
	 */
	public InitParameter(
		long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		URL operationCenterURL,
		URL controlCenterURL,
		TrafficFuzzyData trafficFuzzyData,
		Envelope cityBoundary) {
		super();
		this.startTimestamp = new Date(startTimestamp);
		this.endTimestamp = new Date(endTimestamp);
		if (this.startTimestamp.after(this.endTimestamp)) {
			throw new IllegalArgumentException(String.format(
				"endTimestamp %s lies before startTimestamp %s",
				this.endTimestamp.toString(),
				this.startTimestamp.toString()));
		}
		this.interval = interval;
		this.clockGeneratorInterval = clockGeneratorInterval;
		this.operationCenterURL = operationCenterURL;
		this.trafficFuzzyData = trafficFuzzyData;
		this.cityBoundary = cityBoundary;
		this.operationCenterURL = operationCenterURL;
		this.controlCenterURL = controlCenterURL;
	}

	public long getClockGeneratorInterval() {
		return this.clockGeneratorInterval;
	}

	public Date getEndTimestamp() {
		return this.endTimestamp;
	}

	public long getInterval() {
		return this.interval;
	}

	public URL getOperationCenterURL() {
		return this.operationCenterURL;
	}

	public Date getStartTimestamp() {
		return this.startTimestamp;
	}

	public void setClockGeneratorInterval(long clockGeneratorInterval) {
		this.clockGeneratorInterval = clockGeneratorInterval;
	}

	public void setEndTimestamp(Date endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setOperationCenterURL(URL operationCenterURL) {
		this.operationCenterURL = operationCenterURL;
	}

	public void setStartTimestamp(Date startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public TrafficFuzzyData getTrafficFuzzyData() {
		return trafficFuzzyData;
	}

	public void setTrafficFuzzyData(TrafficFuzzyData trafficFuzzyData) {
		this.trafficFuzzyData = trafficFuzzyData;
	}

	public Envelope getCityBoundary() {
		return cityBoundary;
	}

	public void setCityBoundary(Envelope cityBoundary) {
		this.cityBoundary = cityBoundary;
	}

	public URL getControlCenterURL() {
		return controlCenterURL;
	}

	public void setControlCenterURL(URL controlCenterURL) {
		this.controlCenterURL = controlCenterURL;
	}
}
