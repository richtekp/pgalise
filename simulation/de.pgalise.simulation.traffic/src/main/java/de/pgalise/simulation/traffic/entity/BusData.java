/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Class with data, which define a bus.
 *
 * @author Marina
 * @author Marcus
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class BusData extends VehicleData {

  /**
   * Serial
   */
  private static final long serialVersionUID = -2348975681874480598L;
  /**
   * Description of the bus.
   */
  private String description;

  /**
   * Bus stop order of the bus stops
   */
  private List<BusStop> busStopOrder;

  /**
   * Last bus stop passed (visited); index of the bus stop order
   */
  private int lastBusStop;

  /**
   * Height of the bus in mm.
   */
  private int height;
  /**
   * Maximal passenger count.
   */
  private int maxPassengerCount;
  /**
   * Maximal speed of the bus.
   */
  private int maxSpeed;
  /**
   * Power of the bus in kW.
   */
  private double power;
  /**
   * Weight of the bus in kg.
   */
  private int weight;
  /**
   * Width of the bus in mm.
   */
  private int width;
  /**
   * Current passenger count
   */
  private int currentPassengerCount;

  /**
   * Sensor helper of the infrared sensor
   */
  @XmlTransient
  private InfraredSensor infraredSensor;

  /**
   * List of busstops
   */
  private Map<BusStop, TrafficNode> busStops;

  public BusData() {
    this.description = null;
    this.height = 0;
    this.maxPassengerCount = 0;
    this.maxSpeed = 0;
    this.power = 0;
    this.weight = 0;
    this.width = 0;
  }

  /**
   * Constructor
   *
   * @param axisDistance Distance of the axes in m.
   * @param length Length of the bus in m.
   * @param width Width of the bus in m.
   * @param height Height of the bus in m.
   * @param weight Weight of the bus in kg.
   * @param power Power of the bus in kW.
   * @param maxSpeed Maximal speed of the bus.
   * @param description Description of the bus.
   * @param maxPassengerCount Maximal passenger count.
   * @param currentPassengerCount Current passenger count
   * @param gpsSensor Sensorhelper for gps sensor
   * @param infraredSensor Sensorhelper for infrared sensor
   */
  public BusData(Long id,
    int wheelbase1,
    int wheelbase2,
    int length,
    int width,
    int height,
    int weight,
    double power,
    int maxSpeed,
    String description,
    int maxPassengerCount,
    int currentPassengerCount,
    int axleCount,
    GpsSensor gpsSensor,
    InfraredSensor infraredSensorHelper) {
    super(id,
      length,
      wheelbase1,
      wheelbase2,
      axleCount,
      VehicleTypeEnum.BUS,
      gpsSensor);
    this.width = width;
    this.height = height;
    this.weight = weight;
    this.maxPassengerCount = maxPassengerCount;
    this.power = power;
    this.maxSpeed = maxSpeed;
    this.description = description;
    this.currentPassengerCount = currentPassengerCount;
    this.infraredSensor = infraredSensorHelper;
    this.lastBusStop = 0;
    this.busStopOrder = new ArrayList<>();
  }

  /**
   * Constructor
   *
   * @param referenceData BusData
   */
  public BusData(BusData referenceData) {
    this(referenceData.getId(),
      referenceData.getWheelbase1(),
      referenceData.getWheelbase2(),
      referenceData.getVehicleLength(),
      referenceData
      .getWidth(),
      referenceData.getHeight(),
      referenceData.getWeight(),
      referenceData.getPower(),
      referenceData.getMaxSpeed(),
      referenceData.getDescription(),
      referenceData.getMaxPassengerCount(),
      referenceData.getCurrentPassengerCount(),
      referenceData.getAxleCount(),
      referenceData
      .getGpsSensor(),
      referenceData.getInfraredSensor());
  }

  public String getDescription() {
    return this.description;
  }

  public int getHeight() {
    return this.height;
  }

  public int getMaxPassengerCount() {
    return this.maxPassengerCount;
  }

  public int getMaxSpeed() {
    return this.maxSpeed;
  }

  public double getPower() {
    return this.power;
  }

  public int getWeight() {
    return this.weight;
  }

  public int getWidth() {
    return this.width;
  }

  public int getCurrentPassengerCount() {
    return currentPassengerCount;
  }

  public InfraredSensor getInfraredSensor() {
    return infraredSensor;
  }

  public void setInfraredSensor(InfraredSensor infraredSensor) {
    this.infraredSensor = infraredSensor;
  }

  public void setCurrentPassengerCount(int currentPassengerCount) {
    this.currentPassengerCount = currentPassengerCount;
  }

  public Map<BusStop, TrafficNode> getBusStops() {
    return busStops;
  }

  public void setBusStops(Map<BusStop, TrafficNode> busStops) {
    this.busStops = busStops;
  }

  @Override
  public String toString() {
    return "BusData [description=" + description + ", height=" + height + ", maxPassengerCount="
      + maxPassengerCount + ", maxSpeed=" + maxSpeed + ", power=" + power + ", weight=" + weight + ", width="
      + width + ", currentPassengerCount=" + currentPassengerCount + ", infraredSensor="
      + infraredSensor + ", busStops=" + busStops + "]";
  }

  public List<BusStop> getBusStopOrder() {
    return busStopOrder;
  }

  public void setBusStopOrder(List<BusStop> busStopOrder) {
    this.busStopOrder = busStopOrder;
  }

  public int getLastBusStop() {
    return lastBusStop;
  }

  public void setLastBusStop(int lastBusStop) {
    this.lastBusStop = lastBusStop;
  }

}
