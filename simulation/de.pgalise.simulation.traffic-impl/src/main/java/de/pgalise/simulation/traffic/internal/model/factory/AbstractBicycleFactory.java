/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultBicycle;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.generateRandomColor;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.randInt;
import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
public abstract class AbstractBicycleFactory extends AbstractVehicleFactory
  implements BicycleFactory {

  public final static List<String> MATERIALS = Collections.unmodifiableList(
    new ArrayList<>(Arrays.asList("steel",
        "carbon fibre")));

  public AbstractBicycleFactory() {
    super(new ArrayList<Pair<Integer, Integer>>(Arrays.asList(new MutablePair<>(
      1200,
      1800))),
      10,
      90,
      6,
      25,
      1200,
      1600,
      1100,
      1800,
      50,
      60);
  }

  @Override
  public Bicycle createVehicle(Set<TrafficEdge> edges,
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
    BicycleData carData = new BicycleData(
      MATERIALS.get(randInt(0,
          MATERIALS.size())),
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
    return new DefaultBicycle(getIdGenerator().getNextId(),
      carData,
      getTrafficGraphExtensions(),
      randomPosition);
  }

  @Override
  public Bicycle createVehicle(Output output) {
    return createVehicle(null, output);
  }
}
