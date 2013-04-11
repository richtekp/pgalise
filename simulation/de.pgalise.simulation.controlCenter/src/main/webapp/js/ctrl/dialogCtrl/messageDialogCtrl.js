/*
 * Message Dialog controller
 * This controller is bound to the main view of the initial state and running state
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for displaying messages
 * 
 * @param $scope the scope
 */
function MessageDialogCtrl($scope) {
	this.$scope = $scope;
    ctrl.messageDialogCtrl = this;
	
	this.$scope.message = 'nothing to say';
}