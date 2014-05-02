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
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TruckData;
import de.pgalise.simulation.traffic.entity.TruckTrailerData;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultBus;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultTruck;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.generateRandomColor;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.randDouble;
import static de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory.randInt;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InfraredInterferer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
public class AbstractTruckFactory extends AbstractMotorizedVehicleFactory implements TruckFactory {
  
  private InfraredInterferer infraredInterferer;

  private TrafficGraphExtensions trafficGraphExtensions;
  @EJB
  private IdGenerator idGenerator;
  private int trailerCountMax = 3, trailerCountMin = 0;
  private int trailerDistanceMin = 800, trailerDistanceMax = 1200;
  private int trailerLengthMin = 5000, trailerLengthMax = 15000;
  private List<Integer> trailerWheelbases = new LinkedList<>();
  @EJB
  private TrafficSensorFactory sensorFactory;

  public AbstractTruckFactory() {
  }

  public AbstractTruckFactory(int trailerCountMax, 
          int trailerCountMin,
  int trailerDistanceMin, int trailerDistanceMax,
  int trailerLengthMin, int trailerLengthMax,
  List<Integer> trailerWheelbases ) {
  }

  @Override
  public Truck createVehicle(Set<TrafficEdge> edges, Output output) {
    GpsSensor gpsSensor = sensorFactory.createGpsSensor(
      new ArrayList<>(Arrays.
        asList(retrieveGpsInterferer())),
      output);
    InfraredSensor infraredSensor = sensorFactory.createInfraredSensor(null,
      output);
    List<Integer> wheelbases = generateWheelbases();
    int vehicleLength = calculateVehicleLength(wheelbases);
    List<TruckTrailerData> truckTrailerData = generateTruckTrailerData();
    TruckData busData = new TruckData(generateTruckTrailerData(),
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
    return new DefaultTruck(getIdGenerator().getNextId(),
      busData,
      trafficGraphExtensions);
  }

  @Override
  public Truck createVehicle( Output output) {
    return createVehicle(null, output);
  }
  
  private List<TruckTrailerData> generateTruckTrailerData() {
    int trailerCount = randInt(trailerCountMin, trailerCountMax);
    return generateTruckTrailerData(trailerCount);
  }

  private List<TruckTrailerData> generateTruckTrailerData(int trailerCount) {
    List<TruckTrailerData> retValue = new LinkedList<>();
    for(int i=0; i<trailerCount; i++) {
      TruckTrailerData truckTrailerData = new TruckTrailerData(
              randInt(trailerDistanceMin, trailerDistanceMax), 
              randInt(trailerLengthMin, trailerLengthMax), 
              trailerWheelbases, idGenerator.getNextId());
      retValue.add(truckTrailerData);
    }
    return retValue;
  }

  @Override
  public Truck createTruck(Color color, int trailercount, Output output) {
    Truck retValue = createVehicle(output);
    retValue.getData().setColor(color);
    retValue.getData().setTruckTrailerDatas(generateTruckTrailerData(trailercount));
    return retValue;
  }

}
