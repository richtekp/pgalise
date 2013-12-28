/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author richter
 */
@ManagedBean
@RequestScoped
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
		randomCars = 0;
		randomCarGPSRatio = 100;
		randomTrucks = 0;
		randomTruckGPSRatio = 100;
		randomMotorcycles = 0;
		randomMotorcycleGPSRatio = 100;
		randomBikes = 0;
		randomBikeGPSRatio = 100;
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
}
