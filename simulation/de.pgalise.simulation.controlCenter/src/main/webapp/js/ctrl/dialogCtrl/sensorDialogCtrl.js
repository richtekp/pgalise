/*
 * Sensor Dialog controller
 * This controller is bound to the sensors event view of the runtime state
 * and the sensors view in the initial state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for standard sensors where adjustment is not needed
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 */
function SensorDialogCtrl($scope, PopupService) {
	this.$scope = $scope;
	ctrl.sensorDialogCtrl = this;
	
	this.$scope.sensorInFocus = undefined;
}