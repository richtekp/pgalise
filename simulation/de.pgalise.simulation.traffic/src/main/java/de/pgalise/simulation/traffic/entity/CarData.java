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
import de.pgalise.simulation.traffic.model.vehicle.xml.ColorAdapter;
import java.awt.Color;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Information about the car
 *
 * @author Marcus
 * @version 1.0 (Nov 7, 2012)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class CarData extends VehicleData {

  /**
   * Serial
   */
  private static final long serialVersionUID = -2380378213126874308L;

  /**
   * Color
   */
  @XmlJavaTypeAdapter(ColorAdapter.class)
  @XmlElement
  private Color color;

  /**
   * Description
   */
  private String description;

  /**
   * Height in mm
   */
  private int height; // MM

  /**
   * Maximum speed in khm
   */
  private int maxSpeed;

  /**
   * Power in kw
   */
  private double power; // KW

  /**
   * Weight in kg
   */
  private int weight; // Kilogramm

  /**
   * width of the wheel distance in mm
   */
  private int wheelDistanceWidth; // MM

  /**
   * Width in mm
   */
  private int width;

  public CarData() {
    this.description = null;
    this.height = 0;
    this.maxSpeed = 0;
    this.power = 0;
    this.weight = 0;
    this.wheelDistanceWidth = 0;
    this.width = 0;
  }

  /**
   * Constructor
   *
   * @param id
   * @param color Color
   * @param wheelDistanceWidth width of the wheel distance in mm
   * @param wheelbase1
   * @param wheelbase2
   * @param length Length in mm
   * @param width Width in mm
   * @param height Height in mm
   * @param weight Weight in kg
   * @param power Power in kw
   * @param maxSpeed Maximum speed in khm
   * @param axleCount Number of axle
   * @param description Description
   * @param gpsSensor
   * @param type
   */
  public CarData(Long id,
    Color color,
    int wheelDistanceWidth,
    int wheelbase1,
    int wheelbase2,
    int length,
    int width,
    int height,
    int weight,
    double power,
    int maxSpeed,
    int axleCount,
    String description,
    GpsSensor gpsSensor,
    VehicleTypeEnum type) {
    super(id,
      length,
      wheelbase1,
      wheelbase2,
      axleCount,
      type,
      gpsSensor);
    this.color = color;
    this.wheelDistanceWidth = wheelDistanceWidth;
    this.width = width;
    this.height = height;
    this.weight = weight;
    this.power = power;
    this.maxSpeed = maxSpeed;
    this.description = description;

  }

  /**
   * Constructor
   *
   * @param referenceData CarData
   */
  public CarData(CarData referenceData) {
    this(referenceData.getId(),
      referenceData.getColor(),
      referenceData.getWheelDistanceWidth(),
      referenceData.getWheelbase1(),
      referenceData.getWheelbase2(),
      referenceData.getVehicleLength(),
      referenceData.getWidth(),
      referenceData
      .getHeight(),
      referenceData.getWeight(),
      referenceData.getPower(),
      referenceData.getMaxSpeed(),
      referenceData.getAxleCount(),
      referenceData.getDescription(),
      referenceData.getGpsSensor(),
      referenceData.getType());
  }

  public Color getColor() {
    return this.color;
  }

  public String getDescription() {
    return this.description;
  }

  public int getHeight() {
    return this.height;
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

  public int getWheelDistanceWidth() {
    return this.wheelDistanceWidth;
  }

  public int getWidth() {
    return this.width;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public String toString() {
    return "CarData [color=" + color + ", description=" + description + ", height=" + height + ", maxSpeed="
      + maxSpeed + ", power=" + power + ", weight=" + weight + ", wheelDistanceWidth=" + wheelDistanceWidth
      + ", width=" + width + "]";
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @param height the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * @param maxSpeed the maxSpeed to set
   */
  public void setMaxSpeed(int maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  /**
   * @param power the power to set
   */
  public void setPower(double power) {
    this.power = power;
  }

  /**
   * @param weight the weight to set
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

  /**
   * @param wheelDistanceWidth the wheelDistanceWidth to set
   */
  public void setWheelDistanceWidth(int wheelDistanceWidth) {
    this.wheelDistanceWidth = wheelDistanceWidth;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

}
