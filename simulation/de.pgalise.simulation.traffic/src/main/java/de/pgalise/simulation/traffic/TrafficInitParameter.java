/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import java.net.URL;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TrafficInitParameter extends InitParameter {

	private static final long serialVersionUID = 1L;
	private CityInfrastructureData cityInfrasturctureData;
	private int TrafficServerCount = 2;

	public TrafficInitParameter() {
	}

	public TrafficInitParameter(
		CityInfrastructureData cityInfrastructureData,
		ServerConfiguration serverConfiguration,
		long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		URL operationCenterURL,
		URL controlCenterURL,
		TrafficFuzzyData trafficFuzzyData) {
		super(
			serverConfiguration,
			startTimestamp,
			endTimestamp,
			interval,
			clockGeneratorInterval,
			operationCenterURL,
			controlCenterURL,
			trafficFuzzyData,
			cityInfrastructureData.getBoundary());
		this.cityInfrasturctureData = cityInfrastructureData;
	}

	public TrafficInitParameter(
		CityInfrastructureData cityInfrastructureData,
		ServerConfiguration serverConfiguration,
		long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		URL operationCenterURL,
		URL controlCenterURL,
		TrafficFuzzyData trafficFuzzyData,
		int trafficServerCount) {
		this(cityInfrastructureData,
			serverConfiguration,
			startTimestamp,
			endTimestamp,
			interval,
			clockGeneratorInterval,
			operationCenterURL,
			controlCenterURL,
			trafficFuzzyData);
		this.TrafficServerCount = trafficServerCount;
	}

	public void setCityInfrastructureData(
		CityInfrastructureData cityInfrastructureData) {
		this.cityInfrasturctureData = cityInfrastructureData;
	}

	public CityInfrastructureData getCityInfrastructureData() {
		return cityInfrasturctureData;
	}

	public void setTrafficServerCount(int TrafficServerCount) {
		this.TrafficServerCount = TrafficServerCount;
	}

	public int getTrafficServerCount() {
		return TrafficServerCount;
	}

}
