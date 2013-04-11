/*
 * Controller for heading area
 * 
 * @param $scope 
 * @param PopupService Injected service for popup management
 */
function HeadCtrl($scope, PopupService) {
    this.$scope = $scope;
    
    /*
     * Current status of loading area
     */
    this.$scope.loadingStatus = 'notLoading';

    /*
     * Open console
     */
    this.$scope.openConsole = function() {
        PopupService.openConsoleDialog();
    };
    
    /*
     * Open info
     */
    this.$scope.openInfoDialog = function() {
    	PopupService.openSimulationInfoDialog();
    };
}