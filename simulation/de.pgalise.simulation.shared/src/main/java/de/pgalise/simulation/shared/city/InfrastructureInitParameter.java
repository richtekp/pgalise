/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;


public class InfrastructureInitParameter extends InitParameter {
	private static final long serialVersionUID = 1L;
	private CityInfrastructureData cityInfrastructureData;
	
	protected InfrastructureInitParameter() {
	}

	public InfrastructureInitParameter(
		CityInfrastructureData cityInfrastructureData,
		ServerConfiguration serverConfiguration,
		long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		String operationCenterURL,
		String controlCenterURL,
		TrafficFuzzyData trafficFuzzyData,
		Boundary cityBoundary) {
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
		this.cityInfrastructureData = cityInfrastructureData;
	}

	protected void setCityInfrastructureData(
		CityInfrastructureData cityInfrastructureData) {
		this.cityInfrastructureData = cityInfrastructureData;
	}

	public CityInfrastructureData getCityInfrastructureData() {
		return cityInfrastructureData;
	}
	
}
