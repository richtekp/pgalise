/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.InformationBasedVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

/**
 * creates instances of {@link Vehicle} based on information specified by a
 * {@link VehicleInformation}.
 *
 * @author richter
 */
@Stateful
public class RandomInformationBasedVehicleFactory extends AbstractInformationBasedVehicleFactory
	implements InformationBasedVehicleFactory {
	private static final long serialVersionUID = 1L;

	public RandomInformationBasedVehicleFactory() {
	}
}
