/*
 * Console Dialog controller
 * This controller is bound to the main view.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for showing the message traffic
 * 
 * @param $scope the scope
 * @ConsoleService service for the message traffic
 */
function ConsoleDialogCtrl($scope, ConsoleService) {
	this.$scope = $scope;
    ctrl.consoleDialogCtrl = this;
	
    // Default content
	this.$scope.consoleContent = '';
	
	// Register at console service
	ConsoleService.register(this.$scope, 'consoleContent');
}