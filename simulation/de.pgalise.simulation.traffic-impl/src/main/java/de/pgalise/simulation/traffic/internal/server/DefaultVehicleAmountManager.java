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
 
package de.pgalise.simulation.traffic.internal.server;

import de.pgalise.simulation.service.RandomSeedService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.traffic.governor.TrafficGovernor;
import de.pgalise.simulation.traffic.model.vehicle.BicycleData;
import de.pgalise.simulation.traffic.model.vehicle.CarData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.VehicleAmountManager;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import javax.ejb.EJB;

/**
 * This manager checks the informations of the TrafficGovernor and adapts the amount 
 * of vehicle corresponding to the fuzzy logic.
 * 
 * @author Lena
 * @version 1.0 (Apr 5, 2013)
 */
public class DefaultVehicleAmountManager implements VehicleAmountManager {
	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(DefaultVehicleAmountManager.class);
	/**
	 * Tolerance in percentage as double, e.g. 0.10 = 10%
	 */
	private double tolerance;

	/**
	 * The Timebuffer is the range in which new added cars will start driving (in min)
	 */
	private int timebuffer;

	/**
	 * Initial percentage of active cars
	 */
	private double percentageValueCars;

	/**
	 * Initial percentage of active Bicycles
	 */
	private double percentageValueBicycles;

	/**
	 * Initial percentage of active Trucks
	 */
	private double percentageValueTrucks;

	/**
	 * Initial percentage of active Motorcycles
	 */
	private double percentageValueMotorcycles;

	/**
	 * Percentage of the last updated amount of cars.
	 */
	private double oldPercentageValueCars;

	/**
	 * Percentage of the last updated amount of Bicycles.
	 */
	private double oldPercentageValueBicycles;

	/**
	 * Percentage of the last updated amount of Trucks.
	 */
	private double oldPercentageValueTrucks;

	/**
	 * Percentage of the last updated amount of Motorcycles.
	 */
	private double oldPercentageValueMotorcycles;

	/**
	 * The TrafficServer where the VehicleFuzzyManager belongs to. Each Server has his own VehicleFuzzyManager.
	 */
	private TrafficServerLocal ts;

	/**
	 * The quantity of steps, how often the VehicleAmount will be checked. One steps is one simulation update.
	 */
	private int updateSteps;

	/**
	 * The already done steps. One steps is one simulation update.
	 */
	private int doneSteps = 0;

	/**
	 * The FuzzyTrafficGonvernor will deliver the Amount of each vehicle in percentage, depending on Fuzzy Rules (e.g.
	 * depending on weather events)
	 */
	private TrafficGovernor fuzzy;

	/**
	 * Handle the Vehicles with all their Trips. Delivers all planned Vehicles with their Trip-information.
	 */
	private Scheduler scheduler;

	/**
	 * Shows if the VehicleFuzyyManager has been initialized
	 */
	private boolean initialized = false;

	/**
	 * Holds the current Date of the month. It will be generated out of the timestamp.
	 */
	private int currentDay;

	/**
	 * List of cars which can be scheduled if the percentage of cars increases
	 */
	private List<Vehicle<?>> spareCars;

	/**
	 * List of trucks which can be scheduled if the percentage of trucks increases
	 */
	private List<Vehicle<?>> spareTrucks;

	/**
	 * List of motorcycles which can be scheduled if the percentage of motorcycles increases
	 */
	private List<Vehicle<?>> spareMotorcycles;

	/**
	 * List of bicycles which can be scheduled if the percentage of bicycles increases
	 */
	private List<Vehicle<?>> spareBicycles;

	private int maxBikes;

	private int maxCars;

	private int maxMotorcycles;

	private int maxTrucks;

	private Random rand;
	
	@EJB
	private RandomSeedService randomSeedService;

	/**
	 * Constructor
	 * 
	 * @param ts
	 *            The TrafficServer, where this instance belongs to
	 * @param tolerance
	 *            the tolerance between old and new percentage of active vehicles
	 * @param updateSteps
	 *            steps how often the TrafficFuzzyGoverner will be called
	 * @param timebuffer
	 *            the range in which new added cars will start driving (in min)
	 * @param fuzzy
	 *            delivers the amount of each vehicle in percentage, depending on Fuzzy Rules (e.g. depending on weather
	 *            events)
	 */
	public DefaultVehicleAmountManager(TrafficServerLocal ts, double tolerance, int updateSteps, int timebuffer,
			TrafficGovernor fuzzy) {
		this.ts = ts;
		this.scheduler = ts.getScheduler();
		this.tolerance = tolerance;
		this.updateSteps = updateSteps;
		this.timebuffer = timebuffer;
		this.fuzzy = fuzzy;
		this.spareBicycles = new ArrayList<>();
		this.spareCars = new ArrayList<>();
		this.spareMotorcycles = new ArrayList<>();
		this.spareTrucks = new ArrayList<>();
		this.maxBikes = 0;
		this.maxCars = 0;
		this.maxMotorcycles = 0;
		this.maxTrucks = 0;
	}

	private void initialize(long timestamp) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		this.percentageValueCars = fuzzy.getPercentageOfActiveCars(timestamp);
		this.percentageValueBicycles = fuzzy.getPercentageOfActiveBicycles(timestamp);
		this.percentageValueTrucks = fuzzy.getPercentageOfActiveTrucks(timestamp);
		this.percentageValueMotorcycles = fuzzy.getPercentageOfActiveMotorcycles(timestamp);
		this.oldPercentageValueCars = this.percentageValueCars;
		this.oldPercentageValueBicycles = this.percentageValueBicycles;
		this.oldPercentageValueTrucks = this.percentageValueTrucks;
		this.oldPercentageValueMotorcycles = this.percentageValueMotorcycles;

		this.rand = new Random(randomSeedService
				.getSeed(DefaultVehicleAmountManager.class.getName()));
	}

	@Override
	public void checkVehicleAmount(long timestamp) {
		if (!initialized) {
			initialized = true;
			initialize(timestamp);
		}

		if (updateSteps == doneSteps) {
			// log.debug("Old percentage cars: " + Math.floor(this.oldPercentageValueCars * 100)
			// + " new percentage cars: "
			// + Math.floor(fuzzy.getPercentageOfActiveCars(timestamp) * 100));
			// log.debug("Old percentage trucks: " + Math.floor(this.oldPercentageValueTrucks * 100)
			// + " new percentage trucks "
			// + Math.floor(fuzzy.getPercentageOfActiveTrucks(timestamp) * 100));
			// log.debug("Old percentage motorcycle: "
			// + Math.floor(this.oldPercentageValueMotorcycles * 100)
			// + " new percentage motorcycles "
			// + Math.floor(fuzzy.getPercentageOfActiveMotorcycles(timestamp) * 100));
			// log.debug("Old percentage bicycle: "
			// + Math.floor(this.oldPercentageValueBicycles * 100)
			// + " new percentage bicycle "
			// + Math.floor(fuzzy.getPercentageOfActiveBicycles(timestamp) * 100));

			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(timestamp);
			int tempDay = cal.get(Calendar.DAY_OF_MONTH);
			// if the difference between old and new percentage is smaler/bigger
			// than tolerance, can be positive or negative
			// then the amount of Vehicles will be updated

			if (this.oldPercentageValueCars < (fuzzy.getPercentageOfActiveCars(timestamp) - tolerance)
					|| this.oldPercentageValueCars > (fuzzy.getPercentageOfActiveCars(timestamp) + tolerance)) {
				updateCarAmount(this.oldPercentageValueCars - fuzzy.getPercentageOfActiveCars(timestamp), timestamp);
				oldPercentageValueCars = fuzzy.getPercentageOfActiveCars(timestamp);
			}
			if (this.oldPercentageValueBicycles < (fuzzy.getPercentageOfActiveCars(timestamp) - tolerance)
					|| this.oldPercentageValueBicycles > (fuzzy.getPercentageOfActiveBicycles(timestamp) + tolerance)) {
				updateBicycleAmount(this.oldPercentageValueBicycles - fuzzy.getPercentageOfActiveBicycles(timestamp),
						timestamp);
				oldPercentageValueBicycles = fuzzy.getPercentageOfActiveBicycles(timestamp);
			}
			if (this.oldPercentageValueTrucks < (fuzzy.getPercentageOfActiveTrucks(timestamp) - tolerance)
					|| this.oldPercentageValueTrucks > (fuzzy.getPercentageOfActiveTrucks(timestamp) + tolerance)) {
				updateTruckAmount(this.oldPercentageValueTrucks - fuzzy.getPercentageOfActiveTrucks(timestamp),
						timestamp);
				oldPercentageValueTrucks = fuzzy.getPercentageOfActiveTrucks(timestamp);
			}
			if (this.oldPercentageValueMotorcycles < (fuzzy.getPercentageOfActiveMotorcycles(timestamp) - tolerance)
					|| this.oldPercentageValueMotorcycles > (fuzzy.getPercentageOfActiveMotorcycles(timestamp) + tolerance)) {
				updateMotorcycleAmount(
						this.oldPercentageValueMotorcycles - fuzzy.getPercentageOfActiveMotorcycles(timestamp),
						timestamp);
				oldPercentageValueMotorcycles = fuzzy.getPercentageOfActiveMotorcycles(timestamp);
			}
			// if the day has changed clear all maps and update them
			if (this.currentDay != tempDay) {
				this.currentDay = tempDay;
				clearAllMaps();
				updateManager();
			}
			doneSteps = 0;

			// if (timestamp == 1315720803000L) {
			// log.debug("Fake change Fuzzy -10%, buffer: " + this.timebuffer);
			// updateTruckAmount(-0.1, timestamp);
			// }
		}
		doneSteps++;
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private void updateBicycleAmount(double addPercentage, long timestamp) {
		int changingAmount = (int) Math.floor(this.maxBikes * Math.abs(addPercentage));
		if (addPercentage > 0) {
			log.debug("changingAmount of bicycles: " + changingAmount);
			int count = 1;
			for (Iterator<Vehicle<?>> i = this.spareBicycles.iterator(); i.hasNext();) {
				if (count > changingAmount) {
					break;
				}
				Vehicle<?> v = i.next();
				long startTime = (rand.nextInt(this.timebuffer + 1) * 60 * 1000) + timestamp;

				DefaultScheduleItem item = new DefaultScheduleItem(v, startTime, ts.getUpdateIntervall());
				ts.getItemsToScheduleAfterFuzzy().add(item);
				i.remove();

				count++;
			}
		} else if (addPercentage < 0) {
			log.debug("changingAmount of bicycles: -" + changingAmount);
			List<ScheduleItem> bicycles = new ArrayList<>();
			for (ScheduleItem item : this.scheduler.getScheduledItems()) {
				if (item.getVehicle().getData().getClass().equals(BicycleData.class)) {
					bicycles.add(item);
				}
			}
			int tmp = 0;
			if (bicycles.size() < changingAmount) {
				tmp = bicycles.size();
			}
			else {
				tmp = changingAmount;
			}

			for (int i = 0; i < tmp; i++) {
				int index = rand.nextInt(bicycles.size());
				ts.getItemsToRemoveAfterFuzzy().add(bicycles.get(index).getVehicle());
				// this.scheduler.getScheduledItems().remove(bicycles.get(index));
				this.spareBicycles.add(bicycles.get(index).getVehicle());
				bicycles.remove(index);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private void updateCarAmount(double addPercentage, long timestamp) {
		// Number of new Cars, percental to all cars. The concrete number will
		// be rounded.
		int changingAmount = (int) Math.floor(this.maxCars * Math.abs(addPercentage));
		if (addPercentage > 0) {
			log.debug("changingAmount of cars: " + changingAmount);
			int count = 1;
			for (Iterator<Vehicle<?>> i = this.spareCars.iterator(); i.hasNext();) {
				if (count > changingAmount) {
					break;
				}
				Vehicle<?> v = i.next();

				long startTime = (rand.nextInt(this.timebuffer + 1) * 60 * 1000) + timestamp;

				DefaultScheduleItem<?> item = new DefaultScheduleItem<>(v, startTime, ts.getUpdateIntervall());
				ts.getItemsToScheduleAfterFuzzy().add(item);
				i.remove();

				count++;
			}
		} else if (addPercentage < 0) {
			log.debug("changingAmount of cars: -" + changingAmount);
			List<ScheduleItem> cars = new ArrayList<>();
			for (ScheduleItem item : this.scheduler.getScheduledItems()) {
				if (item.getVehicle().getData().getClass().equals(CarData.class)) {
					cars.add(item);
				}
				// if (item.getVehicle().getData() instanceof CarData)
			}

			int tmp = 0;
			if (cars.size() < changingAmount) {
				tmp = cars.size();
			}
			else {
				tmp = changingAmount;
			}

			for (int i = 0; i < tmp; i++) {
				int index = rand.nextInt(cars.size());
				ts.getItemsToRemoveAfterFuzzy().add(cars.get(index).getVehicle());
				// this.scheduler.getScheduledItems().remove(cars.get(index));
				this.spareCars.add(cars.get(index).getVehicle());
				cars.remove(index);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private void updateMotorcycleAmount(double addPercentage, long timestamp) {
		int changingAmount = (int) Math.floor(this.maxMotorcycles * Math.abs(addPercentage));
		if (addPercentage > 0) {
			log.debug("changingAmount of motorcycles: " + changingAmount);
			int count = 1;
			for (Iterator<Vehicle<?>> i = this.spareMotorcycles.iterator(); i.hasNext();) {
				if (count > changingAmount) {
					break;
				}
				Vehicle v = i.next();
				long startTime = (rand.nextInt(this.timebuffer + 1) * 60 * 1000) + timestamp;

				DefaultScheduleItem item = new DefaultScheduleItem(v, startTime, ts.getUpdateIntervall());
				ts.getItemsToScheduleAfterFuzzy().add(item);
				i.remove();

				count++;
			}
		} else if (addPercentage < 0) {
			log.debug("changingAmount of motorcycles: -" + changingAmount);
			List<ScheduleItem> motorcycles = new ArrayList<>();
			for (ScheduleItem item : this.scheduler.getScheduledItems()) {
				if (item.getVehicle().getData().getClass().equals(MotorcycleData.class)) {
					motorcycles.add(item);
				}
			}

			int tmp = 0;
			if (motorcycles.size() < changingAmount) {
				tmp = motorcycles.size();
			}
			else {
				tmp = changingAmount;
			}

			for (int i = 0; i < tmp; i++) {
				int index = rand.nextInt(motorcycles.size());
				ts.getItemsToRemoveAfterFuzzy().add(motorcycles.get(index).getVehicle());
				// this.scheduler.getScheduledItems().remove(motorcycles.get(index));
				this.spareMotorcycles.add(motorcycles.get(index).getVehicle());
				motorcycles.remove(index);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	private void updateTruckAmount(double addPercentage, long timestamp) {
		int changingAmount = (int) Math.floor(this.maxTrucks * Math.abs(addPercentage));
		if (addPercentage > 0) {
			log.debug("changingAmount of trucks: " + changingAmount);
			int count = 1;
			for (Iterator<Vehicle<?>> i = this.spareTrucks.iterator(); i.hasNext();) {
				if (count > changingAmount) {
					break;
				}
				Vehicle v = i.next();
				long startTime = (rand.nextInt(this.timebuffer + 1) * 60 * 1000) + timestamp;

				DefaultScheduleItem item = new DefaultScheduleItem(v, startTime, ts.getUpdateIntervall());
				ts.getItemsToScheduleAfterFuzzy().add(item);
				i.remove();

				count++;
			}
		} else if (addPercentage < 0) {
			log.debug("changingAmount of trucks: -" + changingAmount);
			List<ScheduleItem> trucks = new ArrayList<>();
			for (ScheduleItem item : this.scheduler.getScheduledItems()) {
				if (item.getVehicle().getData().getClass().equals(TruckData.class)) {
					trucks.add(item);
				}
			}

			int tmp = 0;
			if (trucks.size() < changingAmount) {
				tmp = trucks.size();
			}
			else {
				tmp = changingAmount;
			}

			for (int i = 0; i < tmp; i++) {
				int index = rand.nextInt(trucks.size());
				ts.getItemsToRemoveAfterFuzzy().add(trucks.get(index).getVehicle());
				// this.scheduler.getScheduledItems().remove(trucks.get(index));
				this.spareTrucks.add(trucks.get(index).getVehicle());
				trucks.remove(index);
			}
		}
	}

	@Override
	public void setTolerance(double tol) {
		this.tolerance = tol;
	}

	@Override
	public double getTolerance() {
		return tolerance;
	}

	@Override
	public void updateManager() {

	}

	/**
	 * Clears all Maps
	 */
	private void clearAllMaps() {
		this.spareBicycles.clear();
		this.spareCars.clear();
		this.spareMotorcycles.clear();
		this.spareTrucks.clear();
		this.maxBikes = 0;
		this.maxCars = 0;
		this.maxMotorcycles = 0;
		this.maxTrucks = 0;
	}

	@Override
	public double getPercentageValueCars() {
		return percentageValueCars;
	}

	@Override
	public double getPercentageValueBicycles() {
		return percentageValueBicycles;
	}

	@Override
	public double getPercentageValueTrucks() {
		return percentageValueTrucks;
	}

	@Override
	public double getPercentageValueMotorcycles() {
		return percentageValueMotorcycles;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addSpareCar(Vehicle v) {
		spareCars.add(v);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addSpareTruck(Vehicle v) {
		spareTrucks.add(v);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addSpareMotorcycle(Vehicle v) {
		spareMotorcycles.add(v);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addSpareBicycle(Vehicle v) {
		spareBicycles.add(v);
	}

	@Override
	public void increaseMaxBikes() {
		this.maxBikes++;
	}

	@Override
	public void increaseMaxCars() {
		this.maxCars++;
	}

	@Override
	public void increaseMaxMotorcycles() {
		this.maxMotorcycles++;
	}

	@Override
	public void increaseMaxTrucks() {
		this.maxTrucks++;
	}
}
