/*
 * Energy event Dialog controller
 * This controller is bound to the sensors event view of the runtime state
 * and the sensors view in the initial state
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible setting the energy event parameters
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 * @author Dennis Höting
 */
function EnergyEventDialogCtrl($scope, PopupService) {
    this.$scope = $scope;
    ctrl.energyEventDialogCtrl = this;
    var _this = this;
    
    // Current energy event
    this.$scope.energyEvent = undefined;
    
    /*
     * Recalc start
     */
    function recalcEnergyEventStart() {
    	if(typeof _this.$scope.energyEvent !== 'undefined') {
			_this.$scope.energyEvent.startTimestamp = _this.$scope.energyEvent.startDate
			+ 	_this.$scope.energyEvent.startHours*1000*60*60
			+ 	_this.$scope.energyEvent.startMinutes*1000*60;
		}
    };
    
    /*
     * Recalc duration
     */
    function recalcEnergyEventDuration() {
    	if(typeof _this.$scope.energyEvent !== 'undefined') {
	    	_this.$scope.energyEvent.durationMillis = _this.$scope.energyEvent.durationHours*1000*60*60
			+   _this.$scope.energyEvent.durationMinutes*1000*60;
    	}
    };
    
    /*
     * Watch energy event change
     */
    this.$scope.$watch('energyEvent', function(newEEObj) {
    	if(typeof newEEObj !== 'undefined') {
    		// If event is defined, set start appropriately
    		$('.start').datepicker('setDate', new Date(newEEObj.startDate));
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
	this.$scope.$watch('energyEvent.startDate', function() {
		recalcEnergyEventStart();
	});
	this.$scope.$watch('energyEvent.startHours', function() {
		recalcEnergyEventStart();
	});
	this.$scope.$watch('energyEvent.startMinutes', function() {
		recalcEnergyEventStart();
	});
	this.$scope.$watch('energyEvent.durationHours', function() {
		recalcEnergyEventDuration();
	});
	this.$scope.$watch('energyEvent.durationMinutes', function() {
		recalcEnergyEventDuration();
	});
};
