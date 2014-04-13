/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.TopoRadarInterferer;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author richter
 */
@Local
public interface TrafficSensorFactory extends SensorFactory {

  public InfraredSensor createInfraredSensor(BaseCoordinate position,
    List<InfraredInterferer> sensorInterfererTypes,
    Output output);

  public InfraredSensor createInfraredSensor(
    List<InfraredInterferer> sensorInterfererTypes,
    Output output);

  public InductionLoopSensor createInductionLoopSensor(BaseCoordinate position,
    List<InductionLoopInterferer> sensorInterfererTypes,
    Output output);

  public InductionLoopSensor createInductionLoopSensor(
    List<InductionLoopInterferer> sensorInterfererTypes,
    Output output);

  public TopoRadarSensor createTopoRadarSensor(BaseCoordinate position,
    List<TopoRadarInterferer> sensorInterfererTypes,
    Output output);

  public TopoRadarSensor createTopoRadarSensor(
    List<TopoRadarInterferer> sensorInterfererTypes,
    Output output);

  public GpsSensor createGpsSensor(boolean withSensorInterferer,
    Output output);

  GpsSensor createGpsSensor(List<GpsInterferer> gpsInterferers,
    Output output);
}
