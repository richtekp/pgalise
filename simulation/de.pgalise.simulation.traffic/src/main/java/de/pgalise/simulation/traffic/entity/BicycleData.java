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
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

/**
 * Attributes of bicycles
 *
 * @author Marcus
 * @author Sabrina
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

  /**
   * Description of the bicycle
   */
  private String description;

  /**
   * Maximum speed
   */
  private int maxSpeed;

  /**
   * Weight
   */
  private double weight;

  /**
   * Wheelbase
   */
  private int wheelbase;

  public BicycleData() {
  }

  /**
   * Constructor
   *
   * @param id
   * @param weight Weight
   * @param maxSpeed Maximum speed
   * @param material Material
   * @param length Length in mm
   * @param wheelbase Wheelbase
   * @param description
   * @param gpsSensor
   */
  public BicycleData(Long id,
    double weight,
    int maxSpeed,
    String material,
    int length,
    int wheelbase,
    String description,
    GpsSensor gpsSensor) {
    super(id,
      length,
      wheelbase,
      0,
      2,
      VehicleTypeEnum.BIKE,
      gpsSensor);
    this.weight = weight;
    this.maxSpeed = maxSpeed;
    this.description = description;
    this.material = material;
    this.wheelbase = wheelbase;
  }

  /**
   * Constructor
   *
   * @param referenceData BicycleData
   */
  public BicycleData(BicycleData referenceData) {
    this(referenceData.getId(),
      referenceData.getWeight(),
      referenceData.getMaxSpeed(),
      referenceData.getMaterial(),
      referenceData
      .getVehicleLength(),
      referenceData.getWheelbase(),
      referenceData.getDescription(),
      referenceData
      .getGpsSensor());
  }

  public String getDescription() {
    return this.description;
  }

  public String getMaterial() {
    return this.material;
  }

  public int getMaxSpeed() {
    return this.maxSpeed;
  }

  public double getWeight() {
    return this.weight;
  }

  public int getWheelbase() {
    return this.wheelbase;
  }

  @Override
  public String toString() {
    return "BicycleData [material=" + material + ", description=" + description + ", maxSpeed=" + maxSpeed
      + ", weight=" + weight + ", wheelbase=" + wheelbase + "]";
  }

  protected static final XMLFormat<BicycleData> GRAPHIC_XML = new XMLFormat<BicycleData>(
    BicycleData.class) {
      @Override
      public void write(BicycleData g,
        OutputElement xml) throws XMLStreamException {
        xml.setAttribute("material",
          g.material);
        xml.add(g.description,
          "description");
        xml.add(g.maxSpeed,
          "maxSpeed");
        xml.add(g.weight,
          "weight");
        xml.add(g.wheelbase,
          "wheelbase");
      }

      @Override
      public void read(InputElement xml,
        BicycleData g) throws XMLStreamException {
        g.material = xml.getAttribute("material",
          "");
        g.description = xml.get("description");
        g.maxSpeed = xml.get("maxSpeed");
        g.weight = xml.get("weight");
        g.wheelbase = xml.get("wheelbase");
      }
    };
}
