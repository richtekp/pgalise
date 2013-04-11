/*
 * City controller 
 * This controller is bound to the city view in the initial state.
 * It also has access to the main controller's scope.
 * 
 * It inherits the methods for watching the parameters 
 * which can be set for the city.
 * 
 * @param $scope the scope
 */
function CityCtrl($scope) {
	var _this = this;
	this.$scope = $scope;
	ctrl.cityCtrl = this;
	
	/*
	 * The city's altitude
	 */
	this.$scope.altitude = this.$scope.startParameter.city.altitude;
	this.$scope.$watch('altitude', function(value) {
		_this.$scope.startParameter.city.altitude = value;
	});
	
	/*
	 * The city's population
	 */
	this.$scope.population = this.$scope.startParameter.city.population;
	this.$scope.$watch('population', function(value) {
		_this.$scope.startParameter.city.population = value;
	});
	
	/*
	 * The total amount of cars in the simulation
	 */
	this.$scope.randomCars = this.$scope.startParameter.randomDynamicSensorBundle.randomCarAmount;
	this.$scope.$watch('randomCars', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.randomCarAmount = value;
	});
	
	/*
	 * The ratio of cars which are equipped with gps
	 */
	this.$scope.randomCarGPSRatio = parseInt(this.$scope.startParameter.randomDynamicSensorBundle.gpsCarRatio*100);
	this.$scope.$watch('randomCarGPSRatio', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.gpsCarRatio = value/100;
	});
	
	/*
	 * The total amount of trucks in the simulation
	 */
	this.$scope.randomTrucks = this.$scope.startParameter.randomDynamicSensorBundle.randomTruckAmount;
	this.$scope.$watch('randomTrucks', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.randomTruckAmount = value;
	});
	
	/*
	 * The ratio of trucks which are equipped with gps
	 */
	this.$scope.randomTruckGPSRatio = parseInt(this.$scope.startParameter.randomDynamicSensorBundle.gpsTruckRatio*100);
	this.$scope.$watch('randomTruckGPSRatio', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.gpsTruckRatio = value/100;
	});
	
	/*
	 * The total amount of motorcycles in the simulation
	 */
	this.$scope.randomMotorcycles = this.$scope.startParameter.randomDynamicSensorBundle.randomMotorcycleAmount;
	this.$scope.$watch('randomMotorcycles', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.randomMotorcycleAmount = value;
	});
	
	/*
	 * The ratio of motorcycles which are equipped with gps
	 */
	this.$scope.randomMotorcycleGPSRatio = parseInt(this.$scope.startParameter.randomDynamicSensorBundle.gpsMotorcycleRatio*100);
	this.$scope.$watch('randomMotorcycleGPSRatio', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.gpsMotorcycleRatio = value/100;
	});
	
	/*
	 * The total amount of bikes in the simulation
	 */
	this.$scope.randomBikes = this.$scope.startParameter.randomDynamicSensorBundle.randomBikeAmount;
	this.$scope.$watch('randomBikes', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.randomBikeAmount = value;
	});
	
	/*
	 * The ratio of bikes which are equipped with gps
	 */
	this.$scope.randomBikeGPSRatio = parseInt(this.$scope.startParameter.randomDynamicSensorBundle.gpsBikeRatio*100);
	this.$scope.$watch('randomBikeGPSRatio', function(value) {
		_this.$scope.startParameter.randomDynamicSensorBundle.gpsBikeRatio = value/100;
	});
}