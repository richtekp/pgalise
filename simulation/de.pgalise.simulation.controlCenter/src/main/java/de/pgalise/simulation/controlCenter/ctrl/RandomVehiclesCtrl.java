/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class RandomVehiclesCtrl {

	/**
	 * Creates a new instance of RandomVehiclesCtrl
	 */
	public RandomVehiclesCtrl() {
	}
	
	private int randomCars = 0;
	private double randomCarGPSRatio = 100;
	private int randomTrucks = 0;
	private double randomTruckGPSRatio = 100;
	private int randomMotorcycles = 0;
	private double randomMotorcycleGPSRatio = 100;
	private int randomBikes = 0;
	private double randomBikeGPSRatio = 100;
	
	public void reset() {
		setRandomCars(0);
		setRandomCarGPSRatio(100);
		setRandomTrucks(0);
		setRandomTruckGPSRatio(100);
		setRandomMotorcycles(0);
		setRandomMotorcycleGPSRatio(100);
		setRandomBikes(0);
		setRandomBikeGPSRatio(100);
	};
	
	//should be unnecessary due to JSF bean management
//	// add random cars
//	this.$scope.applyCars = function(amount, ratio) {
//        var messageID = MessageIDService.get();
//        _this.$scope.unsentMessages.push(
//			new model.CreateRandomVehiclesMessage(messageID, {
//				randomCarAmount : amount,
//				randomBikeAmount : 0,
//				randomTruckAmount : 0,
//				randomMotorcycleAmount : 0,
//				gpsCarRatio : ratio,
//				gpsBikeRatio : 0,
//				gpsTruckRatio : 0,
//				gpsMotorcycleRatio : 0,
//		        usedSensorIDs : SensorObjectIDService.takenIds,
//		        usedUUIDs : UUIDService.takenUUIDs
//			}));
//		_this.reset();		
//	};
//	// add random trucks
//	this.$scope.applyTrucks = function(amount, ratio) {
//        var messageID = MessageIDService.get();
//        _this.$scope.unsentMessages.push(
//			new model.CreateRandomVehiclesMessage(messageID, {
//				randomCarAmount : 0,
//				randomBikeAmount : 0,
//				randomTruckAmount : amount,
//				randomMotorcycleAmount : 0,
//				gpsCarRatio : 0,
//				gpsBikeRatio : 0,
//				gpsTruckRatio : ratio,
//				gpsMotorcycleRatio : 0,
//	            usedSensorIDs : SensorObjectIDService.takenIds,
//	            usedUUIDs : UUIDService.takenUUIDs
//			}));
//
//		_this.reset();
//	};
//	// add random motorcycles
//	this.$scope.applyMotorcycles = function(amount, ratio) {
//        var messageID = MessageIDService.get();
//        _this.$scope.unsentMessages.push(
//			new model.CreateRandomVehiclesMessage(messageID, {
//				randomCarAmount : 0,
//				randomBikeAmount : 0,
//				randomTruckAmount : 0,
//				randomMotorcycleAmount : amount,
//				gpsCarRatio : 0,
//				gpsBikeRatio : 0,
//				gpsTruckRatio : 0,
//				gpsMotorcycleRatio : ratio,
//	            usedSensorIDs : SensorObjectIDService.takenIds,
//	            usedUUIDs : UUIDService.takenUUIDs
//			}));
//
//		_this.reset();
//	};
//	// add random bikes
//	this.$scope.applyBikes = function(amount, ratio) {
//        var messageID = MessageIDService.get();
//        _this.$scope.unsentMessages.push(
//			new model.CreateRandomVehiclesMessage(messageID, {
//				randomCarAmount : 0,
//				randomBikeAmount : amount,
//				randomTruckAmount : 0,
//				randomMotorcycleAmount : 0,
//				gpsCarRatio : 0,
//				gpsBikeRatio : ratio,
//				gpsTruckRatio : 0,
//				gpsMotorcycleRatio : 0,
//	            usedSensorIDs : SensorObjectIDService.takenIds,
//	            usedUUIDs : UUIDService.takenUUIDs
//			}));
//
//		_this.reset();
//	};
//}

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
	
	public void addBikes() {
		
	}
	
	public void addTrucks() {
		
	}
	
	public void addMotorcycles() {
		
	}
	
	public void addCars() {
		
	}
}
