/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsAtmosphereInterferer;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
public class RandomBusFactory extends AbstractVehicleFactory implements
	BusFactory {

	private InfraredInterferer infraredInterferer;

	public RandomBusFactory() {
	}

	public RandomBusFactory(InfraredInterferer infraredInterferer) {
		this.infraredInterferer = infraredInterferer;
	}

	@Override
	public BaseVehicle<BusData> createBus(Output output) {
		return createRandomBus(output);
	}

	@Override
	public BaseVehicle<BusData> createRandomBus(Output output) {
		GpsSensor gpsSensor = new GpsSensor(getIdGenerator().getNextId(),
			output,
			null,
			retrieveGpsInterferer());
		InfraredSensor infraredSensor = new InfraredSensor(getIdGenerator().
			getNextId(),
			output,
			null,
			null,
			infraredInterferer);
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
		return new DefaultMotorizedVehicle<>(getIdGenerator().getNextId(),
			busData,
			null);
	}

}
