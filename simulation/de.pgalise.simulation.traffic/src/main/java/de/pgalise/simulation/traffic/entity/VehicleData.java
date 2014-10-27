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

import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.xml.ColorAdapter;
import java.awt.Color;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * General information about a vehicle.
 *
 * @author Marcus
 * @version 1.0 (Nov 7, 2012)
 */
@MappedSuperclass
@ManagedBean
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VehicleData extends Identifiable {

  /**
   * Serial
   */
  private static final long serialVersionUID = 2649565387450310817L;

  /**
   * Length in mm
   */
  /*
   length is a reserved SQL keyword
   */
  private int vehicleLength; // MM

  /**
   * wheelbases in mm (between 1st and 2nd, 2nd and 3rd, etc. wheel)
   */
  @XmlList
  @ElementCollection
  private List<Integer> wheelbases;

  /**
   * SensorHelper of the GPS sensor
   */
  @XmlTransient
  @Transient
  private GpsSensor gpsSensor;
  /**
   * Maximal speed of the bus.
   */
  private int maxSpeed;
  /**
   * Weight of the bus in kg.
   */
  private int weight;
  /**
   * Width of the bus in mm.
   */
  private int width;

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

  public VehicleData() {
  }

  public VehicleData(int vehicleLength,
    List<Integer> wheelbases,
    GpsSensor gpsSensor,
    int maxSpeed,
    int weight,
    int width,
    Color color,
    String description,
    int height,
    Long id) {
    super(id);
    this.vehicleLength = vehicleLength;
    this.wheelbases = wheelbases;
    this.gpsSensor = gpsSensor;
    this.maxSpeed = maxSpeed;
    this.weight = weight;
    this.width = width;
    this.color = color;
    this.description = description;
    this.height = height;
  }

  public int getVehicleLength() {
    return this.vehicleLength;
  }

  public abstract VehicleType getType();

  public void setVehicleLength(int vehicleLength) {
    this.vehicleLength = vehicleLength;
  }

  public void setWheelbases(List<Integer> wheelbases) {
    this.wheelbases = wheelbases;
  }

  public List<Integer> getWheelbases() {
    return wheelbases;
  }

  public GpsSensor getGpsSensor() {
    return gpsSensor;
  }

  public void setGpsSensor(GpsSensor gpsSensor) {
    this.gpsSensor = gpsSensor;
  }

  /**
   * make visible in order to be overwritable in XML vehicle factories
   *
   * @param id
   */
  @Override
  public void setId(Long id) {
    super.setId(id);
  }

  /**
   * @return the maxSpeed
   */
  public int getMaxSpeed() {
    return maxSpeed;
  }

  /**
   * @param maxSpeed the maxSpeed to set
   */
  public void setMaxSpeed(int maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  /**
   * @return the weight
   */
  public int getWeight() {
    return weight;
  }

  /**
   * @param weight the weight to set
   */
  public void setWeight(int weight) {
    this.weight = weight;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return the color
   */
  public Color getColor() {
    return color;
  }

  /**
   * @param color the color to set
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param height the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }
}
