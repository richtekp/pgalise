/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 *
 * @author richter
 */
public class RandomBusFactory implements BusFactory {

	@Override
	public BaseVehicle<BusData> createBus(
		SensorHelper gpsSensor,
		SensorHelper infraredSensor) {
		return createRandomBus(gpsSensor,
			infraredSensor);
	}

	@Override
	public BaseVehicle<BusData> createRandomBus(SensorHelper gpsSensor,
		SensorHelper infraredSensor) {
		BusData busData = new BusData(1,
			2,
			3,
			4,
			5,
			6,
			7,
			8,
			null,
			9,
			10,
			11,
			gpsSensor,
			infraredSensor);
		return new DefaultMotorizedVehicle<>(busData, null);
	}
	
}
