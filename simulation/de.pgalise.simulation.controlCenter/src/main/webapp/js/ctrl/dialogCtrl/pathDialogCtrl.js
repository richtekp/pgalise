/*
 * Path Dialog controller
 * This controller is bound to the sensors event view of the runtime state
 * and the sensors view in the initial state.
 * 
 * This controller is responsible for setting a path event where the start and end 
 * of defined traffic participants can be set
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 * @param MapService Injected service for map
 * @param UUIDService Injected service for Path's internal IDs
 */
function PathDialogCtrl($scope, PopupService, MapService, UUIDService) {
    var _this = this;
    this.$scope = $scope;
    ctrl.pathDialogCtrl = this;
    
    this.$scope.path = undefined;
    
    this.$scope.types = [];
    for(var id in ctrl.mainCtrl.vehicleTypeMap) {
        this.$scope.types.push({id:id,name:ctrl.mainCtrl.vehicleTypeMap[id]});
    }
    
    this.$scope.$watch('path', function(newPathObj) {
    	if(typeof newPathObj !== 'undefined') {
		   	 $('.wayThere').datepicker('setDate', new Date(newPathObj.current.wayThereDate));
			 var id;
			 if(typeof newPathObj.current.type==='undefined') {
				 id=0;
			 } else {
				 id=newPathObj.current.type.id;
			 }
			 newPathObj.current.type = _this.$scope.types[id];
    	}
    });
    
    /**
     * Recalc the needed arrival time 
     */
    function recalcWayThereTime() {
    	if(typeof _this.$scope.path !== 'undefined') {
			_this.$scope.path.current.wayThereTimestamp = _this.$scope.path.current.wayThereDate
			+ (_this.$scope.path.current.wayThereHours*1000*60*60)
			+ (_this.$scope.path.current.wayThereMinutes*1000*60);
    	}
    };

	/*
	 * Workaround!
	 * Only possibility to invoke datepicker-setter AFTER its constructor in directive-Module ^^<
	 */
	this.$scope.hackVar = !this.$scope.hackVar;
	this.$scope.$watch('hackVar', function() {
        $('.wayThere').datepicker('option', 'minDate', new Date(ctrl.mainCtrl.$scope.startParameter.startTimestamp));
        $('.wayThere').datepicker('option', 'maxDate', new Date(ctrl.mainCtrl.$scope.startParameter.endTimestamp));
	});
	
	this.$scope.$watch('path.current.wayThereDate', function() {
		recalcWayThereTime();
	});
	this.$scope.$watch('path.current.wayThereHours', function() {
		recalcWayThereTime();
	});
	this.$scope.$watch('path.current.wayThereMinutes', function() {
		recalcWayThereTime();
	});
	
	/**
	 *Adds a specific vehicle type to the event list as a traffic participant 
	 */
    this.$scope.addVehicle = function() {
    	if(typeof _this.$scope.path.current.type === 'undefined') {
    		PopupService.openMessageDialog('Type undefined!');
    		return;
    	}
    	
        _this.$scope.path.addVehicle(new model.Vehicle(
        	UUIDService.get(),
            _this.$scope.path.current.type,
            _this.$scope.path.current.name,
            _this.$scope.path.current.gpsActivated,
            _this.$scope.path.current.wayThereTimestamp
        ));
    };
}