/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author richter
 */
@Local
public interface TrafficSensorFactory extends SensorFactory {

  public InfraredSensor createInfraredSensor(JaxRSCoordinate position,
    List<InfraredInterferer> sensorInterfererTypes);

  public InfraredSensor createInfraredSensor(
    List<InfraredInterferer> sensorInterfererTypes);

  public InductionLoopSensor createInductionLoopSensor(JaxRSCoordinate position,
    List<InductionLoopInterferer> sensorInterfererTypes);

  public InductionLoopSensor createInductionLoopSensor(
    List<InductionLoopInterferer> sensorInterfererTypes);

  public TopoRadarSensor createTopoRadarSensor(JaxRSCoordinate position,
    List<TopoRadarInterferer> sensorInterfererTypes);

  public TopoRadarSensor createTopoRadarSensor(
    List<TopoRadarInterferer> sensorInterfererTypes);

  public GpsSensor createGpsSensor(boolean withSensorInterferer);

  GpsSensor createGpsSensor(List<GpsInterferer> gpsInterferers);
}
