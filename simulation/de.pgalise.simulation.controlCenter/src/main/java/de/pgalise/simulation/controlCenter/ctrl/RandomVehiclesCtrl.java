/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.SimulationControllerLocal;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.event.CreateRandomBicycleData;
import de.pgalise.simulation.traffic.event.CreateRandomCarData;
import de.pgalise.simulation.traffic.event.CreateRandomMotorcycleData;
import de.pgalise.simulation.traffic.event.CreateRandomTruckData;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.entity.MotorcycleData;
import de.pgalise.simulation.traffic.entity.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class RandomVehiclesCtrl {

	@EJB
	private IdGenerator idGenerator;
	@EJB
	private TrafficServerLocal trafficServerLocal;
	@EJB
	private TcpIpOutput output;
	@EJB
	private SimulationControllerLocal simulationController;
	@EJB
	private RandomSeedService randomSeedService;
	private GpsInterferer gpsInterferer;

	private int randomCars = 0;
	private double randomCarGPSRatio = 100;
	private int randomTrucks = 0;
	private double randomTruckGPSRatio = 100;
	private int randomMotorcycles = 0;
	private double randomMotorcycleGPSRatio = 100;
	private int randomBikes = 0;
	private double randomBikeGPSRatio = 100;

	/**
	 * Creates a new instance of RandomVehiclesCtrl
	 */
	public RandomVehiclesCtrl() {
	}

	public void reset() {
		setRandomCars(0);
		setRandomCarGPSRatio(100);
		setRandomTrucks(0);
		setRandomTruckGPSRatio(100);
		setRandomMotorcycles(0);
		setRandomMotorcycleGPSRatio(100);
		setRandomBikes(0);
		setRandomBikeGPSRatio(100);
	}

	@PostConstruct
	public void init() {
		this.gpsInterferer = new GpsWhiteNoiseInterferer(randomSeedService,
			1.0);
	}

	/**
	 * @return the randomCars
	 */
	public int getRandomCars() {
		return randomCars;
	}

	/**
	 * @param randomCars the randomCars to set
	 */
	public void setRandomCars(int randomCars) {
		this.randomCars = randomCars;
	}

	/**
	 * @return the randomCarGPSRatio
	 */
	public double getRandomCarGPSRatio() {
		return randomCarGPSRatio;
	}

	/**
	 * @param randomCarGPSRatio the randomCarGPSRatio to set
	 */
	public void setRandomCarGPSRatio(double randomCarGPSRatio) {
		this.randomCarGPSRatio = randomCarGPSRatio;
	}

	/**
	 * @return the randomTrucks
	 */
	public int getRandomTrucks() {
		return randomTrucks;
	}

	/**
	 * @param randomTrucks the randomTrucks to set
	 */
	public void setRandomTrucks(int randomTrucks) {
		this.randomTrucks = randomTrucks;
	}

	/**
	 * @return the randomTruckGPSRatio
	 */
	public double getRandomTruckGPSRatio() {
		return randomTruckGPSRatio;
	}

	/**
	 * @param randomTruckGPSRatio the randomTruckGPSRatio to set
	 */
	public void setRandomTruckGPSRatio(double randomTruckGPSRatio) {
		this.randomTruckGPSRatio = randomTruckGPSRatio;
	}

	/**
	 * @return the randomMotorcycles
	 */
	public int getRandomMotorcycles() {
		return randomMotorcycles;
	}

	/**
	 * @param randomMotorcycles the randomMotorcycles to set
	 */
	public void setRandomMotorcycles(int randomMotorcycles) {
		this.randomMotorcycles = randomMotorcycles;
	}

	/**
	 * @return the randomMotorcycleGPSRatio
	 */
	public double getRandomMotorcycleGPSRatio() {
		return randomMotorcycleGPSRatio;
	}

	/**
	 * @param randomMotorcycleGPSRatio the randomMotorcycleGPSRatio to set
	 */
	public void setRandomMotorcycleGPSRatio(double randomMotorcycleGPSRatio) {
		this.randomMotorcycleGPSRatio = randomMotorcycleGPSRatio;
	}

	/**
	 * @return the randomBikes
	 */
	public int getRandomBikes() {
		return randomBikes;
	}

	/**
	 * @param randomBikes the randomBikes to set
	 */
	public void setRandomBikes(int randomBikes) {
		this.randomBikes = randomBikes;
	}

	/**
	 * @return the randomBikeGPSRatio
	 */
	public double getRandomBikeGPSRatio() {
		return randomBikeGPSRatio;
	}

	/**
	 * @param randomBikeGPSRatio the randomBikeGPSRatio to set
	 */
	public void setRandomBikeGPSRatio(double randomBikeGPSRatio) {
		this.randomBikeGPSRatio = randomBikeGPSRatio;
	}

	public List<CreateRandomVehicleData> generateCreateRandomVehicleData() {
		List<CreateRandomVehicleData> retValue = new LinkedList<>();
		for (int i = 0; i < randomBikes; i++) {
			Long id = idGenerator.getNextId();
			CreateRandomVehicleData createRandomVehicleData = new CreateRandomBicycleData(
				new GpsSensor(idGenerator.
					getNextId(),
					output,
					null,
					gpsInterferer),
				new VehicleInformation(true,
					VehicleTypeEnum.BIKE,
					VehicleModelEnum.BIKE_RANDOM,
					null,
					"random bike"));
			retValue.add(createRandomVehicleData);
		}
		for (int i = 0; i < randomTrucks; i++) {
			Long id = idGenerator.getNextId();
			CreateRandomVehicleData createRandomVehicleData = new CreateRandomTruckData(
				new GpsSensor(idGenerator.
					getNextId(),
					output,
					null,
					gpsInterferer),
				new VehicleInformation(true,
					VehicleTypeEnum.BIKE,
					VehicleModelEnum.BIKE_RANDOM,
					null,
					"random bike"));
			retValue.add(createRandomVehicleData);
		}
		for (int i = 0; i < randomMotorcycles; i++) {
			Long id = idGenerator.getNextId();
			CreateRandomVehicleData createRandomVehicleData = new CreateRandomMotorcycleData(
				new GpsSensor(idGenerator.
					getNextId(),
					output,
					null,
					gpsInterferer),
				new VehicleInformation(true,
					VehicleTypeEnum.BIKE,
					VehicleModelEnum.BIKE_RANDOM,
					null,
					"random bike"));
			retValue.add(createRandomVehicleData);
		}
		for (int i = 0; i < randomCars; i++) {
			Long id = idGenerator.getNextId();
			CreateRandomVehicleData createRandomVehicleData = new CreateRandomCarData(
				new GpsSensor(idGenerator.
					getNextId(),
					output,
					null,
					gpsInterferer),
				new VehicleInformation(true,
					VehicleTypeEnum.BIKE,
					VehicleModelEnum.BIKE_RANDOM,
					null,
					"random bike"));
			retValue.add(createRandomVehicleData);
		}
		return retValue;
	}

	public List<CreateVehiclesEvent> generateEventList() {
		List<CreateVehiclesEvent> retValue = new LinkedList<>();
		List<Vehicle> bikes = new LinkedList<>();
		for (int i = 0; i < randomBikes; i++) {
			Long id = idGenerator.getNextId();
			Vehicle<BicycleData> bicycle = trafficServerLocal.getBikeFactory().
				createRandomBicycle(
					output);
			bikes.add(bicycle);
		}
		CreateVehiclesEvent createBicyclesEvent = new CreateVehiclesEvent(
			trafficServerLocal,
			System.currentTimeMillis(),
			simulationController.getElapsedTime(),
			bikes);
		retValue.add(createBicyclesEvent);
		List<Vehicle> trucks = new LinkedList<>();
		for (int i = 0; i < randomTrucks; i++) {
			Long id = idGenerator.getNextId();
			Vehicle<TruckData> truck = trafficServerLocal.getTruckFactory().
				createRandomTruck(output);
			trucks.add(truck);
		}
		CreateVehiclesEvent createTrucksEvent = new CreateVehiclesEvent(
			trafficServerLocal,
			System.currentTimeMillis(),
			simulationController.getElapsedTime(),
			trucks);
		retValue.add(createTrucksEvent);
		List<Vehicle> cars = new LinkedList<>();
		for (int i = 0; i < randomCars; i++) {
			Long id = idGenerator.getNextId();
			Vehicle<CarData> car = trafficServerLocal.getCarFactory().createRandomCar(
				output);
			cars.add(car);
		}
		CreateVehiclesEvent createCarsEvent = new CreateVehiclesEvent(
			trafficServerLocal,
			System.currentTimeMillis(),
			simulationController.getElapsedTime(),
			cars);
		retValue.add(createCarsEvent);
		List<Vehicle> motorcycles = new LinkedList<>();
		for (int i = 0; i < randomMotorcycles; i++) {
			Long id = idGenerator.getNextId();
			Vehicle<MotorcycleData> bicycle = trafficServerLocal.
				getMotorcycleFactory().createRandomMotorcycle(output);
			motorcycles.add(bicycle);
		}
		CreateVehiclesEvent createMotorcyclesEvent = new CreateVehiclesEvent(
			trafficServerLocal,
			System.currentTimeMillis(),
			simulationController.getElapsedTime(),
			motorcycles);
		retValue.add(createMotorcyclesEvent);
		return retValue;
	}

	public void addBikes() {
		throw new UnsupportedOperationException();
	}

	public void addTrucks() {
		throw new UnsupportedOperationException();
	}

	public void addMotorcycles() {
		throw new UnsupportedOperationException();
	}

	public void addCars() {
		throw new UnsupportedOperationException();
	}
}
