/*
 * Confirmation Dialog controller
 * This controller is bound to the sensors event view of the runtime state
 * and the sensors view in the initial state
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for getting 
 * the confirmation in case of a user's decision
 * 
 * @param $scope the scope
 * @author Dennis Höting
 */
function ConfirmationDialogCtrl($scope) {
	this.$scope = $scope;
    ctrl.confirmationDialogCtrl = this;
	
    // Default message
	this.$scope.message = 'nothing to say';
}