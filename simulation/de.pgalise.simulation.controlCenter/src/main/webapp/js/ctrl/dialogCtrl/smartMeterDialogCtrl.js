/*
 * Smart Meter Dialog controller
 * This controller is bound to the sensors view in the initial state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible smart meter sensor's settings.
 * 
 * @param $scope the scope
 * @author Dennis Höting
 */
function SmartMeterDialogCtrl($scope) {
	this.$scope = $scope;
	ctrl.smartMeterDialogCtrl = this;
	var _this = this;
	
	this.$scope.sensorInFocus = undefined;
};