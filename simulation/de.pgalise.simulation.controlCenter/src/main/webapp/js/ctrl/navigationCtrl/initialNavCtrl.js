/*
 * Init Nav controller
 * This controller is bound to the main view of the initial state.
 * 
 * This controller encloses the navigation to the different control sites
 * which can be accessed during initial state.
 * 
 * @param $scope the scope
 * @param MapService Injected service for map
 * @author Dennis Höting
 */
function InitNavCtrl($scope, MapService) {
    ctrl.initNavCtrl = this;
    this.$scope = $scope;
    var _this = this;
    
    // current view (will be diasbled in navigation)
    this.$scope.current = false;
    
    // Flags für every view
    this.$scope.GENERAL = 1;
    this.$scope.INFRASTRUCTURE = 2;
    this.$scope.CITY = 3;
    this.$scope.BUSSYSTEM = 6;
    this.$scope.WEATHER = 4;
    this.$scope.SENSORS = -1;
    this.$scope.INTERACTION = 7;
    this.$scope.START = 5;
    
    /*
     * Navigate to certain Content partial
     * URLs are defined in mainCtrl!
     */
    this.$scope.navigate = function(id) {
    	/*
    	 * Save trafficServerIPs
    	 */
    	if(_this.$scope.current === _this.$scope.INFRASTRUCTURE) {
    		_this.$scope.startParameter.trafficServerIPList = _this.$scope.trafficServerIPList;
    	}
    	
		/*
		 * Sensor-view is requested!
		 * Show map, hide normal content
		 */
        if(id === _this.$scope.SENSORS) {
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
        
	        // Start
	        if(id === _this.$scope.START) {
	        	ctrl.mainCtrl.performUI2StartParameter();
	        }
        }
        
        // Change view
        _this.$scope.current = id;
        ctrl.mainCtrl.$scope.contentURL = ctrl.mainCtrl.initContentURLs[id];
    };
}