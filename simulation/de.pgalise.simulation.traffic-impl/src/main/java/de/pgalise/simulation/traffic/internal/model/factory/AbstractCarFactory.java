/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultCar;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
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
public abstract class AbstractCarFactory extends AbstractMotorizedVehicleFactory
  implements
  CarFactory {

  @EJB
  private IdGenerator idGenerator;
  @EJB
  private TrafficSensorFactory sensorFactory;

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
  public Car createCar() {
    return createRandomCar();
  }

  @Override
  public Car createRandomCar() {
    JaxRSCoordinate randomPosition = generateRandomPosition(
      getTrafficGraphExtensions().getGraph().edgeSet());
    GpsSensor gpsSensor = sensorFactory.createGpsSensor(new ArrayList<>(Arrays.
      asList(retrieveGpsInterferer())));
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
      idGenerator.getNextId()
    );
    return new DefaultCar(getIdGenerator().getNextId(),
      "random car",
      carData,
      null);
  }
}
