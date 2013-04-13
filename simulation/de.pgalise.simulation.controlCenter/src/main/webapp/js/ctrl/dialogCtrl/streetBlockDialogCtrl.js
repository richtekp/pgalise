/*
 * Street Block Dialog controller
 * This controller is bound to the sensors event view of the runtime state
 * and the sensors view in the initial state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for the street block event which can be set to block streets during runtime.
 * 
 * @param $scope the scope
 * @author Dennis Höting
 */
function StreetBlockDialogCtrl($scope) {
	this.$scope = $scope;
	ctrl.streetBlockDialogCtrl = this;
	var _this = this;
	
	this.$scope.streetBlock = undefined;
    
    /**
     * Recalc the Street Block start event 
     */
    function recalcStreetBlockStart() {
    	if(typeof _this.$scope.streetBlock !== 'undefined') {
			_this.$scope.streetBlock.startTimestamp = _this.$scope.streetBlock.startDate
			+ 	_this.$scope.streetBlock.startHours*1000*60*60
			+ 	_this.$scope.streetBlock.startMinutes*1000*60;
			}
    };
    
    /**
     *Recalc the Street Block duration 
     */
    function recalcStreetBlockDuration() {
    	if(typeof _this.$scope.streetBlock !== 'undefined') {
	    	_this.$scope.streetBlock.durationMillis = _this.$scope.streetBlock.durationHours*1000*60*60
			+   _this.$scope.streetBlock.durationMinutes*1000*60;
    	}
    };
    
    /**
     * Sets the start date 
     */
    this.$scope.$watch('streetBlock', function(newBlObj) {
    	if(typeof newBlObj !== 'undefined') {
    		$('.start').datepicker('setDate', new Date(newBlObj.startDate));
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

	this.$scope.$watch('streetBlock.startDate', function() {
		recalcStreetBlockStart();
	});
	this.$scope.$watch('streetBlock.startHours', function() {
		recalcStreetBlockStart();
	});
	this.$scope.$watch('streetBlock.startMinutes', function() {
		recalcStreetBlockStart();
	});
	this.$scope.$watch('streetBlock.durationHours', function() {
		recalcStreetBlockDuration();
	});
	this.$scope.$watch('streetBlock.durationMinutes', function() {
		recalcStreetBlockDuration();
	});
};