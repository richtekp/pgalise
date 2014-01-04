/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.VehicleInformation;
import javax.ejb.Local;

/**
 *
 * @author richter
 */
@Local
public interface InformationBasedVehicleFactory  extends VehicleFactory 
{
	
	public Vehicle<?> createVehicle(VehicleInformation vehicleInformation, Output output);
}
