/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
public class RandomBicycleFactory extends AbstractBicycleFactory implements BicycleFactory {
	private static final long serialVersionUID = 1L;

	public RandomBicycleFactory() {
	}
	
}
