/*
 * Head controller
 * This controller is bound to the main view of the initial and runtime state.
 * It also has access to the main controller's scope.
 * 
 * This controller encloses the functionalities of the upper information bar
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 * @author Dennis Höting
 */
function HeadCtrl($scope, PopupService) {
	ctrl.headCtrl = this;
	this.$scope = $scope;

	// current css-Class for loading-stripe
    this.$scope.loadingStatus = 'notLoading';
	
    /*
     * Open unsent messages dialog
     * (not used)
     */
    this.$scope.openUnsentMessagesDialog = function() {
    	PopupService.openUnsentMessagesDialog();
	};
    
	/*
	 * Open uncommitted events dialog
	 * (not used)
	 */
	this.$scope.openUncommitedEventsDialog = function() {
		PopupService.openUncommitedEventsDialog();
	};

	/*
	 * Open console
	 */
	this.$scope.openConsole = function() {
		PopupService.openConsoleDialog();
	};
	
	/*
	 * Open settings
	 */
	this.$scope.openSettings = function() {
		PopupService.openSettingsDialog();
	};
}