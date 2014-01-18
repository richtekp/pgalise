/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.traffic.internal.model.factory.AbstractInformationBasedVehicleFactory;
import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.InformationBasedVehicleFactory;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 * creates instances of {@link Vehicle} based on information specified by a
 * {@link VehicleInformation}.
 *
 * @author richter
 */
@Stateful
@Local(InformationBasedVehicleFactory.class)
public class RandomInformationBasedVehicleFactory extends AbstractInformationBasedVehicleFactory
  implements InformationBasedVehicleFactory {

  private static final long serialVersionUID = 1L;

  public RandomInformationBasedVehicleFactory() {
  }

  @Override
  public Vehicle<?> createVehicle(VehicleInformation vehicleInformation) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
