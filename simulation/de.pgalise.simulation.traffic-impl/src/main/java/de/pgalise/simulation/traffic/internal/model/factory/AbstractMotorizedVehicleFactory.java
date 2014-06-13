/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;


public abstract class AbstractMotorizedVehicleFactory extends AbstractVehicleFactory {
  private static final long serialVersionUID = 1L;

  private double powerMin, powerMax;

  public AbstractMotorizedVehicleFactory() {
  }

  public AbstractMotorizedVehicleFactory(double powerMin,
    double powerMax,
    List<Pair<Integer, Integer>> wheelbaseMins,
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
    super(
      wheelbaseMins,
      maxSpeedMin,
      maxSpeedMax,
      weightMin,
      weightMax,
      widthMin,
      widthMax,
      heightMin,
      heightMax,
      wheelbaseLengthDifferenceMin,
      wheelbaseLengthDifferenceMax);
    this.powerMin = powerMin;
    this.powerMax = powerMax;
  }

  public double getPowerMin() {
    return powerMin;
  }

  public double getPowerMax() {
    return powerMax;
  }
}
