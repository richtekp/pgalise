/*
 * Commit controller
 * This controller is bound to the overview view of the runtime state.
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for listing the committed event
 * messages to the operation center during runtime
 * 
 * @param $scope the scope
 */
function OverviewCtrl($scope) {
	var _this = this;
	this.$scope = $scope;
	ctrl.overviewCtrl = this;
}