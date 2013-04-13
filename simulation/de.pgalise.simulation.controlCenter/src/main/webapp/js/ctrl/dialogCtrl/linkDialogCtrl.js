/*
 * Link Dialog controller
 * This controller is bound to the main view of the initial state
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for showing the start parameter export link
 * 
 * @param $scope the scope
 * @author Dennis Höting
 */
function LinkDialogCtrl($scope) {
	this.$scope = $scope;
    ctrl.linkDialogCtrl = this;
	
	this.$scope.link = {url:'', name:'nix'};
}