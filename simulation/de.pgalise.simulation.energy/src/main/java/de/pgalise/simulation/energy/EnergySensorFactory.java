/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.energy;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author richter
 */
public interface EnergySensorFactory extends SensorFactory {
	

	public PhotovoltaikSensor createPhotovoltaikSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes,
		int area) throws InterruptedException, ExecutionException;

	public PhotovoltaikSensor createPhotovoltaikSensor(
		List<SensorInterfererType> sensorInterfererTypes,
		int area) throws InterruptedException, ExecutionException;
}
