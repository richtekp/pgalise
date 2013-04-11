/*
 * Controller for message dialog
 */
function ConfirmationDialogCtrl($scope) {
	this.$scope = $scope;
	ctrl.confirmationDialogCtrl = this;
	
	this.$scope.message = 'nothing to say';
}