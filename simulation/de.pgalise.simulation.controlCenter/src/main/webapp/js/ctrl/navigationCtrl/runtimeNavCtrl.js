/*
 * Runtime Nav controller
 * This controller is bound to the main view of the runtime state.
 * It also has access to the main controller's scope.
 * 
 * This controller encloses the navigation to the different control sites
 * which can be accessed during runtime state.
 * 
 * @param $scope the scope
 * @param MapService Injected service for map
 */
function RuntimeNavCtrl($scope, MapService) {
    ctrl.runtimeNavCtrl = this;
    this.$scope = $scope;
    var _this = this;
    
    // Current view (will be disabled in navigation)
    _this.$scope.current = false;
    
    // Flag for every view
    this.$scope.STATUS = 1;
    this.$scope.RANDOMVEHICLES = 3;
    this.$scope.WEATHEREVENTS = 2;
    this.$scope.SENSOREVENTS = -1;
    this.$scope.COMMIT = 4;
    this.$scope.OVERVIEW = 5;
    /*
     * Navigate to certain Content partial
     * URLs are defined in mainCtrl!
     */
    this.$scope.navigate = function(id) {
    	if(id === -1) {
			$('div#div_map').css('display','block');
            $('div#icon_div').css('display','block');
			$('div#div_content').css('display','none');
        	MapService.init();
        } else 
        /*
         * Normal view is requested
         * Hide map, show normal content
         */
        {
			$('div#div_map').css('display','none');
			$('div#icon_div').css('display','none');
			$('div#div_content').css('display','block');
			
			if(id === _this.$scope.COMMIT) {
                ctrl.mainCtrl.performUI2EventsAndMessages();
			}
        }

        _this.$scope.current = id;
        ctrl.mainCtrl.$scope.contentURL = ctrl.mainCtrl.runtimeContentURLs[id];
    };
}