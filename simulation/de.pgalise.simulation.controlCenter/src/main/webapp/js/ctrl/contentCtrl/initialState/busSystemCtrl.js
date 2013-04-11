/*
 * BusSystem controller
 * This controller is bound to the busSystem view of the initial state.
 * It also has access to the main controller's scope.
 * 
 * @param $scope the scope
 */
function BusSystemCtrl($scope) {
	this.$scope = $scope;
	ctrl.busSystemCtrl = this;
	var _this = this;
	
	this.$scope.routeTypeMap = {
	    0 : 'Tram, Streetcar, Light rail',
        1 : 'Subway, Metro',
        2 : 'Rail',
        3 : 'Bus',
        4 : 'Ferry',
        5 : 'Cable car',
        6 : 'Gondola',
        7 : 'Funicular'
	};
	
	/*
	 * select all possible bus routes for being used in the simulation
	 */
	this.$scope.checkAll = function() {
		angular.forEach(_this.$scope.allBusRoutes, function(busRoute) {
			busRoute.used = true;
		});
	};
	
	/*
	 * deselect all possible bus routes for not being used in the simulation
	 */
	this.$scope.checkNone = function() {
		angular.forEach(_this.$scope.allBusRoutes, function(busRoute) {
			busRoute.used = false;
		});
	};
}