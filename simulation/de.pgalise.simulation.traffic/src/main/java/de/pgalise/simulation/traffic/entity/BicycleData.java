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
 * Attributes of bicycles
 *
 * @author Marcus
 * @author Sabrina
 */
/*
 bicycles have different width e.g. when tricycle, differnt height when laying high speed bike
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class BicycleData extends VehicleData {

  /**
   * Serial
   */
  private static final long serialVersionUID = -4032771017292260561L;

  /**
   * Material
   */
  private String material;

  public BicycleData() {
  }

  public BicycleData(String material,
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
    super(vehicleLength,
      wheelbases,
      gpsSensor,
      maxSpeed,
      weight,
      width,
      color,
      description,
      height,
      id);
    this.material = material;
  }

  /**
   * Constructor
   *
   * @param referenceData BicycleData
   */
  public BicycleData(BicycleData referenceData) {
    this(referenceData.getMaterial(),
      referenceData.getVehicleLength(),
      referenceData.getWheelbases(),
      referenceData.getGpsSensor(),
      referenceData.getMaxSpeed(),
      referenceData.getWeight(),
      referenceData.getWidth(),
      referenceData.getColor(),
      referenceData.getDescription(),
      referenceData.getHeight(),
      referenceData.getId()
    );
  }

  public String getMaterial() {
    return this.material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  @Override
  public VehicleType getType() {
    return VehicleTypeEnum.BIKE;
  }
}
