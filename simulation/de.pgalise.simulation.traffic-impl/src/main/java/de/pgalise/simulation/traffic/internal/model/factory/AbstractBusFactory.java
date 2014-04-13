/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.entity.BusData;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultBus;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InfraredInterferer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
public abstract class AbstractBusFactory extends AbstractMotorizedVehicleFactory
  implements
  BusFactory {

  private InfraredInterferer infraredInterferer;

  private TrafficGraphExtensions trafficGraphExtensions;
  @EJB
  private IdGenerator idGenerator;
  private int maxPassengerCountMin = 9, maxPassengerCountMax = 80;
  @EJB
  private TrafficSensorFactory sensorFactory;

  public AbstractBusFactory() {
    super(300,
      500,
      new ArrayList<Pair<Integer, Integer>>(Arrays.asList(new ImmutablePair<>(
            8000,
            10000),
          new ImmutablePair<>(8000,
            10000))),
      80,
      120,
      6000,
      8000,
      2200,
      2500,
      2200,
      2600,
      1000,
      1500);
  }

  public AbstractBusFactory(
    double powerMin,
    double powerMax,
    List<Pair<Integer, Integer>> wheelbaseMins,
    int maxSpeedMin,
    int maxSpeedMax,
    int weightMin,
    int weightMax,
    int widthMin,
    int widthMax,
    int heightMin,
    int heightMax,
    int wheelbaseLengthDifferenceMin,
    int wheelbaseLengthDifferenceMax) {
    super(powerMin,
      powerMax,
      wheelbaseMins,
      maxSpeedMin,
      maxSpeedMax,
      weightMin,
      weightMax,
      widthMin,
      widthMax,
      heightMin,
      heightMax,
      wheelbaseLengthDifferenceMin,
      wheelbaseLengthDifferenceMax);
  }

  public AbstractBusFactory(InfraredInterferer infraredInterferer,
    TrafficGraphExtensions trafficGraphExtensions) {
    this.infraredInterferer = infraredInterferer;
    this.trafficGraphExtensions = trafficGraphExtensions;
  }

  @Override
  public Bus createBus(Output output) {
    return createRandomBus(output);
  }

  @Override
  public Bus createRandomBus(Output output) {
    GpsSensor gpsSensor = sensorFactory.createGpsSensor(
      new ArrayList<>(Arrays.
        asList(retrieveGpsInterferer())),
      output);
    InfraredSensor infraredSensor = sensorFactory.createInfraredSensor(null,
      output);
    int maxPassengerCount = randInt(maxPassengerCountMin,
      maxPassengerCountMax);
    int currentPassengerCount = randInt(0,
      maxPassengerCount);
    List<Integer> wheelbases = generateWheelbases();
    int vehicleLength = calculateVehicleLength(wheelbases);
    BusData busData = new BusData(
      null,
      -1,
      maxPassengerCount,
      currentPassengerCount,
      infraredSensor,
      randDouble(getPowerMin(),
        getPowerMax()),
      vehicleLength,
      wheelbases,
      gpsSensor,
      randInt(getMaxSpeedMin(),
        getMaxSpeedMax()),
      randInt(getWeightMin(),
        getWeightMax()),
      randInt(getWidthMin(),
        getWidthMax()),
      generateRandomColor(),
      "randomly generated bus",
      randInt(getHeightMin(),
        getHeightMax()),
      idGenerator.getNextId());
    return new DefaultBus(getIdGenerator().getNextId(),
      busData,
      trafficGraphExtensions);
  }

}
