/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import java.net.URL;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TrafficInitParameter extends InitParameter {

	private static final long serialVersionUID = 1L;
	private TrafficInfrastructureData trafficInfrastructureData;

	public TrafficInitParameter() {
	}

	public TrafficInitParameter(
		TrafficInfrastructureData cityInfrastructureData,
		ServerConfiguration serverConfiguration,
		long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		URL operationCenterURL,
		URL controlCenterURL,
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

	public void setCityInfrastructureData(
		TrafficInfrastructureData cityInfrastructureData) {
		this.trafficInfrastructureData = cityInfrastructureData;
	}

	public TrafficInfrastructureData getCityInfrastructureData() {
		return trafficInfrastructureData;
	}

}
