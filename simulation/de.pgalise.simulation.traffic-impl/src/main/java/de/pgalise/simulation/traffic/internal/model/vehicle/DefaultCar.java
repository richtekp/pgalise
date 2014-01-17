/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.entity.CarData;

/**
 *
 * @author richter
 */
public class DefaultCar extends ExtendedMotorizedVehicle<CarData> implements Car {

	private static final long serialVersionUID = 1L;

	protected DefaultCar() {
	}

	public DefaultCar(Long id,
		String name,
		CarData data,
		TrafficGraphExtensions trafficGraphExtensions) {
		super(id,
			name,
			data,
			trafficGraphExtensions);
	}

	}
