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

import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import java.awt.Color;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlElement;

/**
 * Class with data, which define a bus.
 *
 * @author Marina
 * @author Marcus
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class BusData extends MotorizedVehicleData {

  /**
   * Serial
   */
  private static final long serialVersionUID = -2348975681874480598L;

  /**
   * Bus stop order of the bus stops
   */
  @ManyToMany
  private List<BusStop> busStopOrder;

  /**
   * Last bus stop passed (visited); index of the bus stop order
   */
  private int lastBusStop;

  /**
   * Maximal passenger count.
   */
  @XmlElement(required = true)
  private int maxPassengerCount;
  /**
   * Current passenger count
   */
  private int currentPassengerCount;

  /**
   * Sensor helper of the infrared sensor
   */
  @XmlTransient
  @Transient
  private InfraredSensor infraredSensor;

  public BusData() {
  }

  /**
   * Constructor
   *
   * @param id
   * @param wheelbases
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
   * @param infraredSensorHelper
   */
  public BusData(List<BusStop> busStopOrder,
    int lastBusStop,
    int maxPassengerCount,
    int currentPassengerCount,
    InfraredSensor infraredSensor,
    double power,
    int vehicleLength,
    List<Integer> wheelbases,
    GpsSensor gpsSensor,
    int maxSpeed,
    int weight,
    int width,
    Color color,
    String description,
    int height,
    Long id) {
    super(power,
      vehicleLength,
      wheelbases,
      gpsSensor,
      maxSpeed,
      weight,
      width,
      color,
      description,
      height,
      id);
    this.busStopOrder = busStopOrder;
    this.lastBusStop = lastBusStop;
    this.maxPassengerCount = maxPassengerCount;
    this.currentPassengerCount = currentPassengerCount;
    this.infraredSensor = infraredSensor;
  }

  /**
   * Constructor
   *
   * @param referenceData BusData
   */
  public BusData(BusData referenceData) {
    this(referenceData.getBusStopOrder(),
      referenceData.getLastBusStop(),
      referenceData.getMaxPassengerCount(),
      referenceData.getCurrentPassengerCount(),
      referenceData.getInfraredSensor(),
      referenceData.getPower(),
      referenceData.getVehicleLength(),
      referenceData.getWheelbases(),
      referenceData.getGpsSensor(),
      referenceData.getMaxSpeed(),
      referenceData.getWeight(),
      referenceData.getWidth(),
      referenceData.getColor(),
      referenceData.getDescription(),
      referenceData.getHeight(),
      referenceData.getId());
  }

  public int getMaxPassengerCount() {
    return this.maxPassengerCount;
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

  @Override
  public VehicleType getType() {
    return VehicleTypeEnum.BUS;
  }

}
