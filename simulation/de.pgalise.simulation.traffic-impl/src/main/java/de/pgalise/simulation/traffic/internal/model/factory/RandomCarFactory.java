/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.traffic.internal.model.factory.AbstractCarFactory;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
@Local(CarFactory.class)
public class RandomCarFactory extends AbstractCarFactory implements
	CarFactory {

	private static final long serialVersionUID = 1L;

	public RandomCarFactory() {
	}
}
