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
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.OneToMany;

/**
 * Information about a truck
 *
 * @author Andreas
 * @version 1.0 (Nov 7, 2012)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class TruckData extends MotorizedVehicleData {

  /**
   * Serial
   */
  private static final long serialVersionUID = 6626940882630925074L;

  @OneToMany //@ElementCollection @TODO: make an ElementCollection again if hibernate bug HHH-8860 is fixed
  private List<TruckTrailerData> truckTrailerDatas = new LinkedList<>();

  public TruckData() {
  }

  /**
   * Constructor
   *
   * @param truckTrailerDatas
   * @param id
   * @param vehicleLength
   * @param wheelbases
   * @param color Color
   * @param wheelDistanceWidth width of the wheel distance in mm
   * @param wheelbase1
   * @param length Length in mm
   * @param wheelbase2
   * @param width Width in mm
   * @param height Height in mm
   * @param weight Weight in kg
   * @param power Power in kw
   * @param maxSpeed Maximum speed in khm
   * @param axleCount Number of axle
   * @param description Description
   * @param trailerCount Number of the trailers
   * @param trailerDistance Distance between the trailers
   * @param trailerLength Length of each trailer
   * @param gpsSensor
   */
  public TruckData(List<TruckTrailerData> truckTrailerDatas,
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
    this.truckTrailerDatas = truckTrailerDatas;
  }

  /**
   * Constructor
   *
   * @param referenceData TruckData
   */
  public TruckData(TruckData referenceData) {
    this(referenceData.truckTrailerDatas,
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

  public void setTruckTrailerDatas(List<TruckTrailerData> truckTrailerDatas) {
    this.truckTrailerDatas = truckTrailerDatas;
  }

  public List<TruckTrailerData> getTruckTrailerDatas() {
    return truckTrailerDatas;
  }

  @Override
  public VehicleType getType() {
    return VehicleTypeEnum.TRUCK;
  }

}
