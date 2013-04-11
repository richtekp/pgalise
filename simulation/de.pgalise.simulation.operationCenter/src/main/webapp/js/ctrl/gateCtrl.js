/*
 * Controller for Gate
 * 
 * @param $scope 
 * @param SimulationService Injected service for simulation
 * @param PopupService Injected service für popup management
 */
function GateCtrl($scope, SimulationService, PopupService) {
    var _this = this;
    this.$scope = $scope;
    
    // Store instance of details controller in global variable
    ctrl.gateCtrl = this;

    /*
     * Current gate map (applied!)
     * This map should be synchronized with InfoSphere "gate status"
     */
    this.$scope.gateMap = {
        10 : {
            name : 'Cars',
            percentage : 100,
            set : SimulationService.cars,
            typeId : model.SensorType.GPS_CAR
        },
        11 : {
            name : 'Trucks',
            percentage : 100,
            set : SimulationService.trucks,
            typeId : model.SensorType.GPS_TRUCK
        },
        12 : {
            name : 'Motorcycles',
            percentage : 100,
            set : SimulationService.motorcycles,
            typeId : model.SensorType.GPS_MOTORCYCLE
        },
        13 : {
            name : 'Busses',
            percentage : 100,
            set : SimulationService.busses,
            typeId : model.SensorType.GPS_BUS
        },
        14 : {
            name : 'Bikes',
            percentage : 100,
            set : SimulationService.bikes,
            typeId : model.SensorType.GPS_BIKE
        }
    };
    
    /*
     * Calculate overall sensor amount
     */
    this.$scope.sensorAmount = function() {
    	var tmp = 0;
    	angular.forEach(_this.$scope.gateMap, function(gateInfo) {
    		tmp += count(gateInfo.set);
    	});
    	return tmp;
    };
    
    /*
     * Calculate current number of sensors (that pass through gate)
     */
    this.$scope.overallNumber = function() {
    	var result = parseInt(_this.$scope.sensorAmount() / 100 * _this.$scope.overallPercentage());
    	if(isNaN(result)) result = 0;
    	return result;
    };
    
    /*
     * Calculate overall percentage of sensors (passing through gate)
     */
    this.$scope.overallPercentage = function() {
        var tmp = 0;
        angular.forEach(_this.$scope.gateMap, function(gateInfo) {
            tmp += count(gateInfo.set) / 100 * gateInfo.percentage;
        });
        var result = parseInt(tmp / _this.$scope.sensorAmount() * 100);
    	if(isNaN(result)) result = 100;
    	return result;
    };

    /*
     * Open ConfigGateDialog
     */
    this.$scope.openConfigGatePopup = function() {
        PopupService.openConfigGatePopup();
    };
}