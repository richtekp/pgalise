/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import java.awt.Color;
import org.opengis.geometry.Boundary;

/**
 *
 * @author richter
 */
public class RandomCarFactory extends AbstractVehicleFactory implements
	CarFactory {

	@Override
	public Vehicle<CarData> createCar(
		Output output) {
		return createRandomCar(output);
	}

	@Override
	public Vehicle<CarData> createRandomCar(Output output) {
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
		return new DefaultMotorizedVehicle<>(getIdGenerator().getNextId(),
			carData,
			null);
	}
}
