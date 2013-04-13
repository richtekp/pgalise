/*
 * Controller for infrastructure view
 * This controller is bound to the infrastructure view of the initial state.
 * It also has access to the main controller's scope and
 *  inherits the functionalities to set the Traffic Server properties
 * 
 * @param $scope the scope
 * @author rofriedr
 * @author Dennis Höting
 */
function InfrastructureCtrl($scope) {
	var _this = this;
	this.$scope = $scope;
	ctrl.infrastructureCtrl = this;
	
	/*
	 * Add traffic server
	 */
	this.$scope.addTrafficServer = function() {
		_this.$scope.startParameter.trafficServerIPList.push('');
	};
	
	/*
	 * Remove traffic server
	 */
	this.$scope.removeTrafficServer = function() {
		_this.$scope.startParameter.trafficServerIPList.pop();
	};
	
	this.$scope.changedTrafficServerURL = function(id, string) {
		_this.$scope.trafficServerIPList[id] = string;
	};
}