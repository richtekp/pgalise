/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
public class RandomMotorcycleFactory extends AbstractVehicleFactory implements
				MotorcycleFactory {

	@Override
	public Vehicle<MotorcycleData> createMotorcycle(Output output) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Vehicle<MotorcycleData> createRandomMotorcycle(Output output) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
