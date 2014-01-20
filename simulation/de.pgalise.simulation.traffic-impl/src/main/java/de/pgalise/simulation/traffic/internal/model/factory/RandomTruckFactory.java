/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import java.awt.Color;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
@Local(TruckFactory.class)
public class RandomTruckFactory extends AbstractVehicleFactory implements
  TruckFactory {

  private static final long serialVersionUID = 1L;

  @Override
  public Truck createTruck(Color color,
    int trailercount,
    Output output
  ) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Truck createRandomTruck(Output output) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
