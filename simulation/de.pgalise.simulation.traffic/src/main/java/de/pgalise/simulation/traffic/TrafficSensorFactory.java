/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorInterferer;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.CompositeGpsInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsAtmosphereInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsClockInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsReceiverInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.CompositeInductionLoopInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.CompositeInfraredInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.CompositeTopoRadarInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author richter
 */
public interface TrafficSensorFactory extends SensorFactory {

	public InfraredSensor createInfraredSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes);

	public InfraredSensor createInfraredSensor(List<SensorInterfererType> sensorInterfererTypes) ;

	public InductionLoopSensor createInductionLoopSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes);

	public InductionLoopSensor createInductionLoopSensor(List<SensorInterfererType> sensorInterfererTypes) ;

	public TopoRadarSensor createTopoRadarSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes) ;

	public TopoRadarSensor createTopoRadarSensor(List<SensorInterfererType> sensorInterfererTypes) ;
	
	public GpsSensor createGpsSensor(boolean withSensorInterferer);
	public GpsSensor createGpsSensor(
		List<SensorInterfererType> sensorInterfererTypes) ;
}
