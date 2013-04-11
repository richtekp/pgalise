/*
 * Random Vehicles controller
 * This controller is bound to the randomVehicles view of the runtime state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for adding vehicles during runtime
 * 
 * @param $scope the scope
 * @param MessageIDService Injected service for the message IDs
 * @param PopupService Injected service for pop-up management
 * @param UUIDService service for generating 
 * @param SensorObjectIDService service for generating a ID for the created sensor
 */
function RandomVehiclesCtrl($scope, UUIDService, SensorObjectIDService, MessageIDService, PopupService) {
	var _this = this;
	this.$scope = $scope;
	ctrl.randomVehiclesCtrl = this;
	
	this.$scope.randomCars = 0;
	this.$scope.randomCarGPSRatio = 100;
	this.$scope.randomTrucks = 0;
	this.$scope.randomTruckGPSRatio = 100;
	this.$scope.randomMotorcycles = 0;
	this.$scope.randomMotorcycleGPSRatio = 100;
	this.$scope.randomBikes = 0;
	this.$scope.randomBikeGPSRatio = 100;
	
	this.reset = function() {
		_this.$scope.randomCars = 0;
		_this.$scope.randomCarGPSRatio = 100;
		_this.$scope.randomTrucks = 0;
		_this.$scope.randomTruckGPSRatio = 100;
		_this.$scope.randomMotorcycles = 0;
		_this.$scope.randomMotorcycleGPSRatio = 100;
		_this.$scope.randomBikes = 0;
		_this.$scope.randomBikeGPSRatio = 100;
	};
	// add random cars
	this.$scope.applyCars = function(amount, ratio) {
        var messageID = MessageIDService.get();
        _this.$scope.unsentMessages.push(
			new model.CreateRandomVehiclesMessage(messageID, {
				randomCarAmount : amount,
				randomBikeAmount : 0,
				randomTruckAmount : 0,
				randomMotorcycleAmount : 0,
				gpsCarRatio : ratio,
				gpsBikeRatio : 0,
				gpsTruckRatio : 0,
				gpsMotorcycleRatio : 0,
		        usedSensorIDs : SensorObjectIDService.takenIds,
		        usedUUIDs : UUIDService.takenUUIDs
			}));
		_this.reset();		
	};
	// add random trucks
	this.$scope.applyTrucks = function(amount, ratio) {
        var messageID = MessageIDService.get();
        _this.$scope.unsentMessages.push(
			new model.CreateRandomVehiclesMessage(messageID, {
				randomCarAmount : 0,
				randomBikeAmount : 0,
				randomTruckAmount : amount,
				randomMotorcycleAmount : 0,
				gpsCarRatio : 0,
				gpsBikeRatio : 0,
				gpsTruckRatio : ratio,
				gpsMotorcycleRatio : 0,
	            usedSensorIDs : SensorObjectIDService.takenIds,
	            usedUUIDs : UUIDService.takenUUIDs
			}));

		_this.reset();
	};
	// add random motorcycles
	this.$scope.applyMotorcycles = function(amount, ratio) {
        var messageID = MessageIDService.get();
        _this.$scope.unsentMessages.push(
			new model.CreateRandomVehiclesMessage(messageID, {
				randomCarAmount : 0,
				randomBikeAmount : 0,
				randomTruckAmount : 0,
				randomMotorcycleAmount : amount,
				gpsCarRatio : 0,
				gpsBikeRatio : 0,
				gpsTruckRatio : 0,
				gpsMotorcycleRatio : ratio,
	            usedSensorIDs : SensorObjectIDService.takenIds,
	            usedUUIDs : UUIDService.takenUUIDs
			}));

		_this.reset();
	};
	// add random bikes
	this.$scope.applyBikes = function(amount, ratio) {
        var messageID = MessageIDService.get();
        _this.$scope.unsentMessages.push(
			new model.CreateRandomVehiclesMessage(messageID, {
				randomCarAmount : 0,
				randomBikeAmount : amount,
				randomTruckAmount : 0,
				randomMotorcycleAmount : 0,
				gpsCarRatio : 0,
				gpsBikeRatio : ratio,
				gpsTruckRatio : 0,
				gpsMotorcycleRatio : 0,
	            usedSensorIDs : SensorObjectIDService.takenIds,
	            usedUUIDs : UUIDService.takenUUIDs
			}));

		_this.reset();
	};
}