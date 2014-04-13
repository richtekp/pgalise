/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.JaxbVector2d;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsClockInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
public abstract class AbstractVehicleFactory implements BaseVehicleFactory {

  private static final long serialVersionUID = 1L;
  @EJB
  private TrafficGraphExtensions trafficGraphExtensions;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private TrafficSensorFactory sensorFactory;

  private final static Random RANDOM = new Random();

  public static Color generateRandomColor() {
    return Color.getHSBColor((float) Math.random(),
      (float) Math.random(),
      (float) Math.random());
  }

  public static int randInt(int min,
    int max) {
    int randomNum = RANDOM.nextInt((max - min)) + min;
    return randomNum;
  }

  public static double randDouble(double min,
    double max) {
    double randomNum = RANDOM.nextDouble() * (max - min) + min;
    return randomNum;
  }

  public int calculateVehicleLength(List<Integer> wheelbases) {
    int retValue = randInt(wheelbaseLengthDifferenceMin,
      wheelbaseLengthDifferenceMax);
    for (Integer wheelbase : wheelbases) {
      retValue += wheelbase;
    }
    return retValue;
  }

  private List<Pair<Integer, Integer>> wheelbaseMinMaxPairs;
  private int maxSpeedMin, maxSpeedMax;
  private int weightMin, weightMax;
  private int widthMin, widthMax;
  private int heightMin, heightMax;
  private int wheelbaseLengthDifferenceMin, wheelbaseLengthDifferenceMax;
  @EJB
  private GpsClockInterferer gpsClockInterferer;

  public AbstractVehicleFactory() {
  }

  /*
   using List<Pair<...>> in order to avoid data validation
   */
  public AbstractVehicleFactory(
    List<Pair<Integer, Integer>> wheelbaseMinMaxPairs,
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
    int i = 0;
    for (Pair<Integer, Integer> wheelbaseMinMaxPair : wheelbaseMinMaxPairs) {
      if (wheelbaseMinMaxPair.getLeft() > wheelbaseMinMaxPair.getRight()) {
        throw new IllegalArgumentException(String.format(
          "%d st/nd/rd/th entry of wheelbaseMins is larger than correponding entry in wheelbasesMaxs",
          i));
      }
      i++;
    }
    this.wheelbaseMinMaxPairs = wheelbaseMinMaxPairs;
    this.maxSpeedMin = maxSpeedMin;
    this.maxSpeedMax = maxSpeedMax;
    this.weightMin = weightMin;
    this.weightMax = weightMax;
    this.widthMin = widthMin;
    this.widthMax = widthMax;
    this.heightMin = heightMin;
    this.heightMax = heightMax;
    this.wheelbaseLengthDifferenceMin = wheelbaseLengthDifferenceMin;
    this.wheelbaseLengthDifferenceMax = wheelbaseLengthDifferenceMax;
  }

  public AbstractVehicleFactory(TrafficGraphExtensions trafficGraphExtensions,
    IdGenerator idGenerator,
    RandomSeedService randomSeedService) {
    this.trafficGraphExtensions = trafficGraphExtensions;
    this.idGenerator = idGenerator;
    this.randomSeedService = randomSeedService;
  }

  public List<Integer> generateWheelbases() {
    List<Integer> retValue = new LinkedList<>();
    for (Pair<Integer, Integer> wheelbaseMinMaxPair : wheelbaseMinMaxPairs) {
      retValue.add(randInt(wheelbaseMinMaxPair.getLeft(),
        wheelbaseMinMaxPair.getRight()));
    }
    return retValue;
  }

  public void setTrafficGraphExtensions(
    TrafficGraphExtensions trafficGraphExtensions) {
    this.trafficGraphExtensions = trafficGraphExtensions;
  }

  public TrafficGraphExtensions getTrafficGraphExtensions() {
    return trafficGraphExtensions;
  }

  public void setIdGenerator(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public IdGenerator getIdGenerator() {
    return idGenerator;
  }

  public BaseCoordinate generateRandomPosition(Set<TrafficEdge> edges) {
    int edgeCount = edges.size();
    int chosenIndex = (int) (Math.random() * edgeCount);
    int i = 0;
    Iterator<TrafficEdge> it = edges.iterator();
    while (i < chosenIndex) {
      //needs to be < in order to be select the position in the iterator which 
      //will be before the selected position below (another call to it.next)
      it.next();
      i++;
    }
    TrafficEdge chosenEdge = it.next();
    double chosenOffset = chosenEdge.getEdgeLength() * Math.random();
    JaxbVector2d offsetVector = new JaxbVector2d(chosenEdge.getVector());
    offsetVector.scale(chosenOffset);
    JaxbVector2d positionVector = new JaxbVector2d(chosenEdge.getSource().
      getX(),
      chosenEdge.getSource().getY());
    positionVector.add(offsetVector);
    return new BaseCoordinate(idGenerator.getNextId(),positionVector.getX(),
      positionVector.getY());
  }

  public void setRandomSeedService(RandomSeedService randomSeedService) {
    this.randomSeedService = randomSeedService;
  }

  @Override
  public RandomSeedService getRandomSeedService() {
    return randomSeedService;
  }

  /**
   *
   * @return
   */
  /*
   encapsulating the access to GpsInterferer(s) allows to change the mechanism of retrieval later (e.g. get references from a pool)
   */
  public GpsInterferer retrieveGpsInterferer() {
    return gpsClockInterferer;
  }

  /**
   * @return the maxSpeedMin
   */
  public int getMaxSpeedMin() {
    return maxSpeedMin;
  }

  /**
   * @param maxSpeedMin the maxSpeedMin to set
   */
  public void setMaxSpeedMin(int maxSpeedMin) {
    this.maxSpeedMin = maxSpeedMin;
  }

  /**
   * @return the maxSpeedMax
   */
  public int getMaxSpeedMax() {
    return maxSpeedMax;
  }

  /**
   * @param maxSpeedMax the maxSpeedMax to set
   */
  public void setMaxSpeedMax(int maxSpeedMax) {
    this.maxSpeedMax = maxSpeedMax;
  }

  /**
   * @return the weightMin
   */
  public int getWeightMin() {
    return weightMin;
  }

  /**
   * @param weightMin the weightMin to set
   */
  public void setWeightMin(int weightMin) {
    this.weightMin = weightMin;
  }

  /**
   * @return the weightMax
   */
  public int getWeightMax() {
    return weightMax;
  }

  /**
   * @param weightMax the weightMax to set
   */
  public void setWeightMax(int weightMax) {
    this.weightMax = weightMax;
  }

  /**
   * @return the widthMin
   */
  public int getWidthMin() {
    return widthMin;
  }

  /**
   * @param widthMin the widthMin to set
   */
  public void setWidthMin(int widthMin) {
    this.widthMin = widthMin;
  }

  /**
   * @return the widthMax
   */
  public int getWidthMax() {
    return widthMax;
  }

  /**
   * @param widthMax the widthMax to set
   */
  public void setWidthMax(int widthMax) {
    this.widthMax = widthMax;
  }

  /**
   * @return the heightMin
   */
  public int getHeightMin() {
    return heightMin;
  }

  /**
   * @param heightMin the heightMin to set
   */
  public void setHeightMin(int heightMin) {
    this.heightMin = heightMin;
  }

  /**
   * @return the heightMax
   */
  public int getHeightMax() {
    return heightMax;
  }

  /**
   * @param heightMax the heightMax to set
   */
  public void setHeightMax(int heightMax) {
    this.heightMax = heightMax;
  }

  public int getWheelbaseLengthDifferenceMin() {
    return wheelbaseLengthDifferenceMin;
  }

  public int getWheelbaseLengthDifferenceMax() {
    return wheelbaseLengthDifferenceMax;
  }

  public List<Pair<Integer, Integer>> getWheelbaseMinMaxPairs() {
    return wheelbaseMinMaxPairs;
  }

  public TrafficSensorFactory getSensorFactory() {
    return sensorFactory;
  }
}
