/*
 * Controller for message dialog
 * @author Dennis Höting
 */
function ConfirmationDialogCtrl($scope) {
	this.$scope = $scope;
	ctrl.confirmationDialogCtrl = this;
	
	this.$scope.message = 'nothing to say';
}