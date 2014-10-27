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
import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Informations about the motorcycle
 *
 * @author Sabrina
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class MotorcycleData extends MotorizedVehicleData {

  /**
   * Serial
   */
  private static final long serialVersionUID = -97081705088299060L;

  public MotorcycleData() {
  }

  /**
   * Constructor
   *
   * @param power
   * @param id
   * @param vehicleLength
   * @param color Color of the vehicle
   * @param wheelbases
   * @param type
   * @param weight Weight in kg
   * @param width
   * @param horsePower Horse power
   * @param length Length in mm
   * @param height
   * @param maxSpeed Maximum speed in kmh
   * @param wheelbase Wheelbase in mm
   * @param axleCount Number of axles
   * @param description Description of the motorcycle
   * @param gpsSensor
   */
  public MotorcycleData(double power,
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
  }

  public MotorcycleData(MotorcycleData referenceData) {
    this(referenceData.getPower(),
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

  @Override
  public VehicleType getType() {
    return VehicleTypeEnum.MOTORCYCLE;
  }

}
