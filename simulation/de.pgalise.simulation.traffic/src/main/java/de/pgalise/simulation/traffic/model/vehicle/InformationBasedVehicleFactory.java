/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface InformationBasedVehicleFactory extends BaseVehicleFactory {

  /**
   * creates a {@link Vehicle} without any position information
   *
   * @param vehicleInformation
   * @param output
   * @return
   */
  public Vehicle<?> createVehicle(VehicleInformation vehicleInformation,
    Output output);

  /**
   * generates a vehicle with a random position on a random edge of
   * <tt>edges</tt>.
   *
   * @param vehicleInformation
   * @param edges
   * @param output
   * @return
   */
  public Vehicle<?> createVehicle(VehicleInformation vehicleInformation,
    Set<TrafficEdge> edges,
    Output output);
}
