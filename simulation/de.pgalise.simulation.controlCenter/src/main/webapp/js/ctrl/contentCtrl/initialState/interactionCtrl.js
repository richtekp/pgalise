/*
 * Controller for interaction view
 * This controller is bound to the interaction view of the initial state.
 * It also has access to the main controller's scope.
 * 
 * It inherits the functionalities to set the Traffic Server properties
 * 
 * @param $scope the scope
 * @author rofriedr
 */
function InteractionCtrl($scope) {
	this.$scope = $scope;
	ctrl.busSystemCtrl = this;
	var _this = this;
	
	this.$scope.tolerance = this.$scope.startParameter.trafficFuzzyData.tolerance*100;
	this.$scope.updateSteps = this.$scope.startParameter.trafficFuzzyData.updateSteps;
	this.$scope.buffer = this.$scope.startParameter.trafficFuzzyData.buffer;
	
	this.$scope.$watch('updateSteps', function(value) {
		_this.$scope.startParameter.trafficFuzzyData.updateSteps = value;
	});
	this.$scope.$watch('buffer', function(value) {
		_this.$scope.startParameter.trafficFuzzyData.buffer = value;
	});
	this.$scope.$watch('tolerance', function(value) {
		_this.$scope.startParameter.trafficFuzzyData.tolerance = value/100;
	});
}