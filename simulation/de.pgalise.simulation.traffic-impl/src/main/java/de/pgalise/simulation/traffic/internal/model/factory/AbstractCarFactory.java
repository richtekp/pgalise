/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultCar;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
public abstract class AbstractCarFactory extends AbstractMotorizedVehicleFactory
  implements
  CarFactory {

  public AbstractCarFactory() {
    super(60,
      300,
      new ArrayList<Pair<Integer, Integer>>(Arrays.asList(new ImmutablePair<>(
            2000,
            6000))),
      15,
      250,
      600,
      2500,
      1800,
      2200,
      1300,
      2400,
      500,
      1000
    );
  }

  public AbstractCarFactory(
    double powerMin,
    double powerMax,
    int wheelbaseMin,
    int wheelbaseMax,
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
      new ArrayList<Pair<Integer, Integer>>(Arrays.asList(new ImmutablePair<>(
            wheelbaseMin,
            wheelbaseMax))),
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

  @Override
  public Car createCar(Set<TrafficEdge> edges,
    Output output) {
    return createRandomCar(edges,
      output);
  }

  /**
   *
   * @param edges if edges is <code>null</code> this no position information is
   * generated
   * @return
   */
  @Override
  public Car createRandomCar(Set<TrafficEdge> edges,
    Output output) {
    BaseCoordinate randomPosition = null;
    if (edges != null) {
      randomPosition = generateRandomPosition(edges);
    }
    GpsSensor gpsSensor = getSensorFactory().createGpsSensor(new ArrayList<>(
      Arrays.
      asList(retrieveGpsInterferer())),
      output);
    List<Integer> wheelbases = generateWheelbases();
    CarData carData = new CarData(
      randDouble(getPowerMin(),
        getPowerMax()),
      calculateVehicleLength(wheelbases),
      wheelbases,
      gpsSensor,
      randInt(getMaxSpeedMin(),
        getMaxSpeedMax()),
      randInt(getWeightMin(),
        getWeightMax()),
      randInt(getWidthMin(),
        getWidthMax()),
      generateRandomColor(),
      "randomly generated car",
      randInt(getHeightMin(),
        getHeightMax()),
      getIdGenerator().getNextId()
    );
    return new DefaultCar(getIdGenerator().getNextId(),
      carData,
      getTrafficGraphExtensions(),
      randomPosition);
  }

  @Override
  public Car createCar(Output output) {
    return createCar(null,
      output);
  }

  @Override
  public Car createRandomCar(Output output) {
    return createRandomCar(null,
      output);
  }
}
