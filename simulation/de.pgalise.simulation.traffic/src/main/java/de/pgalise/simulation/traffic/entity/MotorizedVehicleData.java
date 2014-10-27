/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.awt.Color;
import java.util.List;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@MappedSuperclass
public abstract class MotorizedVehicleData extends VehicleData {

  private static final long serialVersionUID = 1L;
  /**
   * Power in kw
   */
  private double power; // KW

  protected MotorizedVehicleData() {
  }

  public MotorizedVehicleData(double power,
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
    this.power = power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public double getPower() {
    return power;
  }

}
