/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import javax.ejb.Local;
import javax.ejb.Stateful;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author richter
 */
@Stateful
@Local(MotorcycleFactory.class)
public class RandomMotorcycleFactory extends AbstractVehicleFactory implements
  MotorcycleFactory {

  private static final long serialVersionUID = 1L;

  public RandomMotorcycleFactory() {
    super(new ArrayList<Pair<Integer, Integer>>(Arrays.asList(
      new ImmutablePair<>(1800,
        2200))),
      60,
      280,
      800,
      1800,
      1500,
      1800,
      1400,
      2000,
      500,
      600);
  }

  @Override
  public Motorcycle createMotorcycle() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Motorcycle createRandomMotorcycle() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
