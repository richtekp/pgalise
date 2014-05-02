/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.entity.MotorcycleData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import static de.pgalise.simulation.traffic.internal.model.factory.AbstractBicycleFactory.MATERIALS;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultBicycle;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultMotorcycle;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.generateRandomColor;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.randInt;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateful;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
@Stateful
@Local(MotorcycleFactory.class)
public class RandomMotorcycleFactory extends AbstractMotorizedVehicleFactory implements
  MotorcycleFactory {

  private static final long serialVersionUID = 1L;

  public RandomMotorcycleFactory() {
    super(15.0, //powerMin
            170, //powerMax
            new ArrayList<Pair<Integer, Integer>>(Arrays.asList(
      new ImmutablePair<>(1800,
        2200))), //wheelbaseMinMaxPairs
      60, //maxSpeedMin
      280, //maxSpeedMax
      800, //weightMin
      1800, //weightMax
      1500, //widthMin
      1800, //widthMax
      1400, //heightMin
      2000, //heightMax
      500, //wheelbaseLengthDifferenceMin
      600 //wheelbaseLengthDifferenceMax
    );
  }

  @Override
  public Motorcycle createVehicle(Output output) {
    return createVehicle(null, output);
  }

  @Override
  public Motorcycle createVehicle(Set<TrafficEdge> edges, Output output) {
    BaseCoordinate randomPosition = null;
    if (edges != null) {
      randomPosition = generateRandomPosition(edges);
    }
    GpsSensor gpsSensor = getSensorFactory().createGpsSensor(new ArrayList<>(
      Arrays.
      asList(retrieveGpsInterferer())),
      output);
    List<Integer> wheelbases = generateWheelbases();
    MotorcycleData carData = new MotorcycleData(
            randDouble(getPowerMin(), getPowerMax()),
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
    return new DefaultMotorcycle(getIdGenerator().getNextId(),
      carData,
      getTrafficGraphExtensions(),
      randomPosition);
  }

}
