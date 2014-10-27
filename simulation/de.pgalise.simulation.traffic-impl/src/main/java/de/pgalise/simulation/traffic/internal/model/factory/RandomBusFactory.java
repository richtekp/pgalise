/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.traffic.internal.model.factory.AbstractBusFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
@Local(BusFactory.class)
public class RandomBusFactory extends AbstractBusFactory implements
	BusFactory {

	private static final long serialVersionUID = 1L;

	public RandomBusFactory() {
	}
}
