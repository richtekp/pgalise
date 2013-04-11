/*
 * Controller for Console
 */
function ConsoleDialogCtrl($scope, ConsoleService) {
	this.$scope = $scope;
	
    // Default content
	this.$scope.consoleContent = '';
	
	// Register at console service
	ConsoleService.register(this.$scope, 'consoleContent');
}