/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.model.vehicle.AbstractVehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusData;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
public class RandomBusFactory extends AbstractBusFactory implements
	BusFactory {
	private static final long serialVersionUID = 1L;

	public RandomBusFactory() {
	}
}
