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
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.model.vehicle.InformationBasedVehicleFactory;
import java.awt.Color;
import javax.ejb.Stateful;

/**
 * creates instances of {@link Vehicle} based on information specified by a
 * {@link VehicleInformation}.
 *
 * @author richter
 */
@Stateful
public class RandomInformationBasedVehicleFactory extends AbstractVehicleFactory
	implements InformationBasedVehicleFactory {

	private RandomBicycleFactory bicycleFactory;
	private RandomBusFactory busFactory;
	private RandomCarFactory carFactory;
	private RandomMotorcycleFactory motorcycleFactory;
	private RandomTruckFactory truckFactory;

	@Override
	public Vehicle<?> createVehicle(VehicleInformation vehicleInformation,
		Output output) {
		GpsSensor gpsSensor = new GpsSensor(getIdGenerator().getNextId(),
			output,
			null,
			null);
		if (vehicleInformation.getVehicleType().equals(VehicleTypeEnum.BIKE)) {
			return bicycleFactory.createBicycle(output);
		} else if (vehicleInformation.getVehicleType().equals(VehicleTypeEnum.BUS)) {
			return busFactory.createBus(output);
		} else if (vehicleInformation.getVehicleType().equals(VehicleTypeEnum.CAR)) {
			return carFactory.createCar(output);
		} else if (vehicleInformation.getVehicleType().equals(
			VehicleTypeEnum.MOTORCYCLE)) {
			motorcycleFactory.createRandomMotorcycle(output);
		} else if (vehicleInformation.getVehicleType().equals(VehicleTypeEnum.TRUCK)) {
			return truckFactory.createRandomTruck(output);
		}
		throw new IllegalArgumentException();
	}

}
