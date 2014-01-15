/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author richter
 */
@Local
public interface TrafficSensorFactory extends SensorFactory {

	public InfraredSensor createInfraredSensor(JaxRSCoordinate position,
		List<SensorInterfererType> sensorInterfererTypes);

	public InfraredSensor createInfraredSensor(List<SensorInterfererType> sensorInterfererTypes) ;

	public InductionLoopSensor createInductionLoopSensor(JaxRSCoordinate position,
		List<SensorInterfererType> sensorInterfererTypes);

	public InductionLoopSensor createInductionLoopSensor(List<SensorInterfererType> sensorInterfererTypes) ;

	public TopoRadarSensor createTopoRadarSensor(JaxRSCoordinate position,
		List<SensorInterfererType> sensorInterfererTypes) ;

	public TopoRadarSensor createTopoRadarSensor(List<SensorInterfererType> sensorInterfererTypes) ;
	
	public GpsSensor createGpsSensor(boolean withSensorInterferer);
	public GpsSensor createGpsSensor(
		List<SensorInterfererType> sensorInterfererTypes) ;
}
