/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.rules;

import de.pgalise.simulation.traffic.entity.TrafficLightIntersectionSensorData;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.staticsensor.AbstractStaticSensor;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;

/**
 *
 * @author richter
 */
public class TrafficLightIntersectionSensor extends AbstractStaticSensor<TrafficEvent, TrafficLightIntersectionSensorData> {

	private static final long serialVersionUID = 1L;
	private TrafficLightIntersection trafficLightIntersection;

	public TrafficLightIntersectionSensor(Long id,
		TrafficLightIntersection trafficLightIntersection,
		Output output,
		BaseCoordinate position,
		int updateLimit,
		TrafficLightIntersectionSensorData sensorData) throws IllegalArgumentException {
		super(id,
			output,
			position,
			updateLimit,
			sensorData);
		this.trafficLightIntersection = trafficLightIntersection;
	}

	public void setTrafficLightIntersection(
		TrafficLightIntersection trafficLightIntersection) {
		this.trafficLightIntersection = trafficLightIntersection;
	}

	public TrafficLightIntersection getTrafficLightIntersection() {
		return trafficLightIntersection;
	}

	@Override
	public void transmitUsageData(EventList<TrafficEvent> eventList) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SensorType getSensorType() {
		return TrafficSensorTypeEnum.TRAFFIC_LIGHT_INTERSECTION;
	}

}
