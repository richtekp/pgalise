/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.server.rules;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class TrafficLightIntersectionSensorData extends SensorData {
	private static final long serialVersionUID = 1L;
	private TrafficLightStateEnum trafficLightState;

	public TrafficLightIntersectionSensorData() {
	}

	public void setTrafficLightState(TrafficLightStateEnum trafficLightState) {
		this.trafficLightState = trafficLightState;
	}

	public TrafficLightStateEnum getTrafficLightState() {
		return trafficLightState;
	}
	
}
