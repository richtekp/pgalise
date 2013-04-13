/*
 * Weather Events controller
 * This controller is bound to the randomVehicles view of the runtime state.
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for adding weather events during runtime
 * 
 * @param $scope the scope
 * @param UUIDService service for generating 
 * @author Dennis Höting
 */
function WeatherEventsCtrl($scope, UUIDService) {
	var _this = this;
	this.$scope = $scope;
	ctrl.weatherEventsCtrl = this;
	/*
	 * Initialize
	 */
	ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime = ctrl.mainCtrl.$scope.simulation.simulationTime + (1000*60*5);
	
	this.$scope.chosenTimeTimestamp = ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime;
	this.$scope.chosenTimeHours = parseInt(new Date(ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime).format('HH'));
	this.$scope.chosenTimeMinutes = parseInt(new Date(ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime).format('MM'));
	this.$scope.chosenTimeDate = ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime
		- this.$scope.chosenTimeHours*1000*60*60
		- this.$scope.chosenTimeMinutes*1000*60;
	this.$scope.durationHours = parseInt(ctrl.mainCtrl.$scope.currentWeatherEventViewData.duration);
	this.$scope.durationMinutes = parseInt((ctrl.mainCtrl.$scope.currentWeatherEventViewData.duration%1)*60);
	
    /*
     * Workaround!
     * Only possibility to invoke datepicker-setter AFTER its constructor in directive-Module ^^<
     */
    this.$scope.hackVar = false;
    this.$scope.$watch('hackVar', function() {
        $('.weatherEventDate').datepicker('option', 'minDate', new Date(ctrl.mainCtrl.$scope.startParameter.startTimestamp));
        $('.weatherEventDate').datepicker('option', 'maxDate', new Date(ctrl.mainCtrl.$scope.startParameter.endTimestamp));
	});

	this.$scope.$watch('currentWeatherEventViewData.decorator', function() {
		ctrl.mainCtrl.$scope.currentWeatherEventViewData.value = ctrl.mainCtrl.$scope.currentWeatherEventViewData.decorator.standardValue;
	});
	
	/*
	 * Recalc
	 */
	this.$scope.recalcChosenTime = function() {
		ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime = _this.$scope.chosenTimeDate
			+ (_this.$scope.chosenTimeHours*1000*60*60)
			+ (_this.$scope.chosenTimeMinutes*1000*60);
	};
	this.$scope.recalcDuration = function() {
		ctrl.mainCtrl.$scope.currentWeatherEventViewData.duration = _this.$scope.durationHours
		 	+ _this.$scope.durationMinutes/60;
	};
	
	/*
	 * Remove weather event
	 */
	this.$scope.deleteWeatherEvent = function(id) {
		ctrl.mainCtrl.$scope.uncommittedWeatherEvents.splice(id,1);
	};
	
	/*
	 * Watchers
	 * Watch model change and digest.
	 */
	this.$scope.$watch('chosenTimeDate', function() {
		_this.$scope.recalcChosenTime();
	});
	this.$scope.$watch('chosenTimeHours', function() {
		_this.$scope.recalcChosenTime();
	});
	this.$scope.$watch('chosenTimeMinutes', function() {
		_this.$scope.recalcChosenTime();
	});
	this.$scope.$watch('durationHours', function() {
		_this.$scope.recalcDuration();
	});
	this.$scope.$watch('durationMinutes', function() {
		_this.$scope.recalcDuration();
	});
	
	this.$scope.applyEvent = function() {
		_this.$scope.uncommittedWeatherEvents.push(
			new model.ChangeWeatherEvent(
				UUIDService.get(),
				ctrl.mainCtrl.$scope.currentWeatherEventViewData.decorator.enumId, 
				ctrl.mainCtrl.$scope.currentWeatherEventViewData.datetime, 
				ctrl.mainCtrl.$scope.currentWeatherEventViewData.value, 
				ctrl.mainCtrl.$scope.currentWeatherEventViewData.duration));
	};
}