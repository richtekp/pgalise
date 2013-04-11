/*
 * Settings Dialog controller
 * This controller is bound to the main view of the initial state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for the simulations settings
 * 
 * @param $scope the scope
 */
function SettingsDialogCtrl($scope) {
	this.$scope = $scope;
    ctrl.settingsDialogCtrl = this;
}