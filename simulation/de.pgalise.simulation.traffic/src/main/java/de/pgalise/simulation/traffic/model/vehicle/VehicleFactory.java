/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import java.util.Set;

/**
 *
 * @author richter
 * @param <V>
 */
public interface VehicleFactory<V extends Vehicle> extends BaseVehicleFactory {
  
  V createVehicle(Output output);
  
  V createVehicle(Set<TrafficEdge> edges, Output output);
}
