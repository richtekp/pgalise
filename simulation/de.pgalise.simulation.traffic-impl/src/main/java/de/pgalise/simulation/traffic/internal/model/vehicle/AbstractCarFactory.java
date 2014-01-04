/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import java.awt.Color;

/**
 *
 * @author richter
 */
public class AbstractCarFactory extends AbstractVehicleFactory implements CarFactory {

	public AbstractCarFactory() {
	}

	@Override
	public Car createCar(
		Output output) {
		return createRandomCar(output);
	}

	@Override
	public Car createRandomCar(Output output) {
		Coordinate randomPosition = generateRandomPosition(
			getTrafficGraphExtensions().getGraph().edgeSet());
		GpsSensor gpsSensor = new GpsSensor(getIdGenerator().getNextId(),
			output,
			null,
			retrieveGpsInterferer());
		CarData carData = new CarData(
			Color.GREEN,
			1,
			2,
			3,
			4,
			5,
			6,
			7,
			7.0,
			8,
			9,
			"description",
			gpsSensor,
			VehicleTypeEnum.CAR);
		return new DefaultCar(getIdGenerator().getNextId(),
			"random car",
			carData,
			null);
	}
}
