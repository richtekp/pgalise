/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.traffic.server;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Interface to manage the Amount of Vehicle with the FuzzyLogic
 * 
 * @author mischa
 * @version 0.8 (16.12.2012)
 */
public interface VehicleAmountManager {

	// /**
	// * Updates the Amount of Cars. It creates or deletes Cars.
	// *
	// * @throws TrafficServerException
	// */
	// public void updateCarAmount(double addPercentage, long timestamp);

	// /**
	// * Updates the Amount of Truck. It creates or deletes Trucks.
	// *
	// * @throws TrafficServerException
	// */
	// public void updateTruckAmount(double addPercentage, long timestamp);

	// /**
	// * Updates the Amount of Motorcycles. It creates or deletes Motorcycles.
	// *
	// * @throws TrafficServerException
	// */
	// public void updateMotorcycleAmount(double addPercentage, long timestamp);

	// /**
	// * Updates the Amount of Bikes. It creates or deletes Motorcycles.
	// *
	// * @throws TrafficServerException
	// */
	// public void updateBicycleAmount(double addPercentage, long timestamp);

	/**
	 * This method checks the amount of Vehicle at a given timestamp. It will called on an update in the TrafficServer.
	 * The old percentage of vehicle will be compared with the actual percentage at the specific time (timestamp).
	 * Therefore the TrafficfuzzyGovernor will be called. If the difference between old value and new is bigger than the
	 * tolerance. The different updateAmount-methods will be called.
	 * 
	 * @param timestamp
	 *            Time of the Simulation
	 * @throws TrafficServerException
	 */
	public void checkVehicleAmount(long timestamp);

	// public void initialize(long timestamp);

	/**
	 * The tolerance for the comparison of the actual percentage and the old percentage of "active" vehicles.
	 * 
	 * @param tol
	 *            Tolerance in percentage as double, e.g. 0.10 for 10%
	 */
	public void setTolerance(double tol);

	/**
	 * Return the actual tolerance
	 * 
	 * @return tolerance Tolerance in percentage as double, e.g. 0.10 for 10%
	 */
	public double getTolerance();

	// /**
	// * Return if the VehicleFuzzyManager has been initialized.
	// *
	// * @return boolean
	// */
	// public boolean inInitializedState();

	/**
	 * Updates the Manager. This means the hashmaps, which contains the amount of vehicles at different times (hourly)
	 * will be updated. This will be called in CreateRandomVehicle and internally if the day has changed.
	 */
	public void updateManager();

	public double getPercentageValueCars();

	public double getPercentageValueBicycles();

	public double getPercentageValueTrucks();

	public double getPercentageValueMotorcycles();

	@SuppressWarnings("rawtypes")
	public void addSpareCar(Vehicle v);

	@SuppressWarnings("rawtypes")
	public void addSpareTruck(Vehicle v);

	@SuppressWarnings("rawtypes")
	public void addSpareMotorcycle(Vehicle v);

	@SuppressWarnings("rawtypes")
	public void addSpareBicycle(Vehicle v);

	public void increaseMaxBikes();

	public void increaseMaxCars();

	public void increaseMaxMotorcycles();

	public void increaseMaxTrucks();
}
