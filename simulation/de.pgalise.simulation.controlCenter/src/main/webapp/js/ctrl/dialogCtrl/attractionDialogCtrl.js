/*
 * Attraction Dialog controller
 * This controller is bound to the sensors event view of the runtime state
 * and the sensors view in the initial state
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for attraction event parameters
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 */
function AttractionDialogCtrl($scope, PopupService) {
    this.$scope = $scope;
    ctrl.attractionDialogCtrl = this;
    var _this = this;
    
    // current attraction
    this.$scope.attraction = undefined;
    
    /*
     * Recalc start
     */
    function recalcAttractionStart() {
    	if(typeof _this.$scope.attraction !== 'undefined') {
			_this.$scope.attraction.startTimestamp = _this.$scope.attraction.startDate
			+ 	_this.$scope.attraction.startHours*1000*60*60
			+ 	_this.$scope.attraction.startMinutes*1000*60;
		}
    };
    
    /*
     * Recalc duration
     */
    function recalcAttractionDuration() {
    	if(typeof _this.$scope.attraction !== 'undefined') {
	    	_this.$scope.attraction.durationMillis = _this.$scope.attraction.durationHours*1000*60*60
			+   _this.$scope.attraction.durationMinutes*1000*60;
    	}
    };
    
    /*
     * Watch attraction change
     */
    this.$scope.$watch('attraction', function(newAtrObj) {
    	if(typeof newAtrObj !== 'undefined') {
    		// If a new attraciton is in focus, set date picker
    		// Appropriate date is saved within attraction object
    		$('.start').datepicker('setDate', new Date(newAtrObj.startDate));
    	}
   });

	/*
	 * Workaround!
	 * Only possibility to invoke datepicker-setter AFTER its constructor in directive-Module ^^<
	 */
	this.$scope.hackVar = !this.$scope.hackVar;
	this.$scope.$watch('hackVar', function() {
        $('.start').datepicker('option', 'minDate', new Date(ctrl.mainCtrl.$scope.startParameter.startTimestamp));
        $('.start').datepicker('option', 'maxDate', new Date(ctrl.mainCtrl.$scope.startParameter.endTimestamp));
    });

	/*
	 * UI-Watchers
	 */
	this.$scope.$watch('attraction.startDate', function() {
		recalcAttractionStart();
	});
	this.$scope.$watch('attraction.startHours', function() {
		recalcAttractionStart();
	});
	this.$scope.$watch('attraction.startMinutes', function() {
		recalcAttractionStart();
	});
	this.$scope.$watch('attraction.durationHours', function() {
		recalcAttractionDuration();
	});
	this.$scope.$watch('attraction.durationMinutes', function() {
		recalcAttractionDuration();
	});
};
