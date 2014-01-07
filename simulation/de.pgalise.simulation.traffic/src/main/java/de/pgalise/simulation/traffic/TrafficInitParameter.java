/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;

public class TrafficInitParameter extends InitParameter {

	private static final long serialVersionUID = 1L;
	private TrafficInfrastructureData trafficInfrastructureData;

	protected TrafficInitParameter() {
	}

	public TrafficInitParameter(
		TrafficInfrastructureData cityInfrastructureData,
		ServerConfiguration serverConfiguration,
		long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		String operationCenterURL,
		String controlCenterURL,
		TrafficFuzzyData trafficFuzzyData,
		Envelope cityBoundary) {
		super(
			serverConfiguration,
			startTimestamp,
			endTimestamp,
			interval,
			clockGeneratorInterval,
			operationCenterURL,
			controlCenterURL,
			trafficFuzzyData,
			cityBoundary);
		this.trafficInfrastructureData = cityInfrastructureData;
	}

	protected void setCityInfrastructureData(
		TrafficInfrastructureData cityInfrastructureData) {
		this.trafficInfrastructureData = cityInfrastructureData;
	}

	public TrafficInfrastructureData getCityInfrastructureData() {
		return trafficInfrastructureData;
	}

}
