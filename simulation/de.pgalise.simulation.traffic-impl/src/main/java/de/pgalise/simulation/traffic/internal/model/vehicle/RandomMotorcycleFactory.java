/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
public class RandomMotorcycleFactory extends AbstractVehicleFactory implements
				MotorcycleFactory {
	private static final long serialVersionUID = 1L;

	@Override
	public Motorcycle createMotorcycle(Output output) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Motorcycle createRandomMotorcycle(Output output) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
