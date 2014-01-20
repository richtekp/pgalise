/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.sensor;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.CompositeGpsInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.CompositeInductionLoopInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.CompositeInfraredInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.CompositeTopoRadarInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarNoInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.AbstractEnergySensorFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.ejb.Stateful;
import javax.ejb.Local;

/**
 *
 * @author richter
 */
@Stateful
@Local(TrafficSensorFactory.class)
public class DefaultTrafficSensorFactory extends AbstractEnergySensorFactory
  implements TrafficSensorFactory {

  public DefaultTrafficSensorFactory() {
    super();
  }

  public DefaultTrafficSensorFactory(RandomSeedService rss,
    IdGenerator idGenerator,
    WeatherController wctrl,
    EnergyControllerLocal ectrl,
    TcpIpOutput sensorOutput,
    int updateLimit) {
    super(rss,
      idGenerator,
      wctrl,
      ectrl,
      sensorOutput,
      updateLimit);
  }

  @Override
  public InductionLoopSensor createInductionLoopSensor(
    List<InductionLoopInterferer> sensorInterfererTypes) {
    InductionLoopInterferer inductionLoopInterferer;
    if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
      inductionLoopInterferer = new CompositeInductionLoopInterferer(
        sensorInterfererTypes);
    } else {
      inductionLoopInterferer = new InductionLoopNoInterferer();
    }

    JaxRSCoordinate position = createRandomPositionInductionLoopSensor();
    return new InductionLoopSensor(getIdGenerator().getNextId(),
      getSensorOutput(),
      null,
      getUpdateLimit(),
      inductionLoopInterferer
    );
  }

  @Override
  public TopoRadarSensor createTopoRadarSensor(
    List<TopoRadarInterferer> sensorInterfererTypes) {
    TopoRadarInterferer toporadarInterferer;
    if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
      toporadarInterferer = new CompositeTopoRadarInterferer(
        sensorInterfererTypes);
    } else {
      toporadarInterferer = new TopoRadarNoInterferer();
    }

    JaxRSCoordinate position = createRandomPositionTopoRadarSensor();
    return new TopoRadarSensor(getIdGenerator().getNextId(),
      getSensorOutput(),
      null,
      getUpdateLimit(),
      toporadarInterferer);
  }

  @Override
  public InfraredSensor createInfraredSensor(
    List<InfraredInterferer> sensorInterfererTypes) {
    InfraredInterferer infraredInterferer;
    if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
      infraredInterferer = new CompositeInfraredInterferer(sensorInterfererTypes);
    } else {
      infraredInterferer = new InfraredNoInterferer();
    }

    JaxRSCoordinate position = createRandomPositionInfraredSensor();
    return new InfraredSensor(getIdGenerator().getNextId(),
      getSensorOutput(),
      null,
      position,
      getUpdateLimit(),
      infraredInterferer);
  }

  public final static List<GpsInterferer> DEFAULT_SENSOR_INTERFERER = new LinkedList<>();

  @Override
  public GpsSensor createGpsSensor(boolean withSensorInterferer) {
    return createGpsSensor(
      withSensorInterferer ? DEFAULT_SENSOR_INTERFERER : new ArrayList<GpsInterferer>(
        0));
  }

  @Override
  public GpsSensor createGpsSensor(
    List<GpsInterferer> sensorInterfererTypes) {
    GpsInterferer gpsInterferer;
    if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
      gpsInterferer = new CompositeGpsInterferer(sensorInterfererTypes);
    } else {
      gpsInterferer = new GpsNoInterferer();
    }

    return new GpsSensor(getIdGenerator().getNextId(),
      getSensorOutput(),
      null,
      getUpdateLimit(),
      gpsInterferer);
  }

  @Override
  public Sensor<?, ?> createSensor(SensorType sensorType,
    List<SensorInterfererType> sensorInterfererTypes)
    throws InterruptedException, ExecutionException {
    if (sensorType.equals(TrafficSensorTypeEnum.TRAFFIC_LIGHT_INTERSECTION)) {
      throw new UnsupportedOperationException(
        "there's no traffic light intersection sensor yet");
    } else if (sensorType.equals(TrafficSensorTypeEnum.INFRARED)) { // Infrared

    } else if (sensorType.equals(TrafficSensorTypeEnum.INDUCTIONLOOP)) { // Inductionloop

    } else if (sensorType.equals(TrafficSensorTypeEnum.TOPORADAR)) { // Toporadar

    } else if (sensorType.
      equals(TrafficSensorTypeEnum.GPS)) {

    }
    throw new IllegalArgumentException();
  }

  @Override
  public InfraredSensor createInfraredSensor(JaxRSCoordinate position,
    List<InfraredInterferer> sensorInterfererTypes) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public InductionLoopSensor createInductionLoopSensor(JaxRSCoordinate position,
    List<InductionLoopInterferer> sensorInterfererTypes) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public TopoRadarSensor createTopoRadarSensor(JaxRSCoordinate position,
    List<TopoRadarInterferer> sensorInterfererTypes) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
