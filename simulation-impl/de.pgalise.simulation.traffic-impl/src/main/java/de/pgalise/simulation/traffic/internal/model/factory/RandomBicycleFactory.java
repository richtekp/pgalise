/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
@Local(BicycleFactory.class)
public class RandomBicycleFactory extends AbstractBicycleFactory implements
  BicycleFactory {

  private static final long serialVersionUID = 1L;

  public RandomBicycleFactory() {
  }

}
