/*
 * Controller for weather view
 *
 * @author Dennis Höting
 */
function WeatherCtrl($scope, UUIDService) {
	var _this = this;
	this.$scope = $scope;
	ctrl.weatherCtrl = this;
	
	/*
	 * Initialize
	 */
	this.$scope.chosenTimeTimestamp = this.$scope.currentWeatherEventViewData.datetime;
	this.$scope.chosenTimeHours = parseInt(new Date(this.$scope.currentWeatherEventViewData.datetime).format('HH'));
	this.$scope.chosenTimeMinutes = parseInt(new Date(this.$scope.currentWeatherEventViewData.datetime).format('MM'));
	this.$scope.chosenTimeDate = this.$scope.currentWeatherEventViewData.datetime
		- this.$scope.chosenTimeHours*1000*60*60
		- this.$scope.chosenTimeMinutes*1000*60;
	this.$scope.durationHours = parseInt(this.$scope.currentWeatherEventViewData.duration);
	this.$scope.durationMinutes = parseInt((this.$scope.currentWeatherEventViewData.duration%1)*60);
	
	/*
	 * Workaround!
	 * Only possibility to invoke datepicker-setter AFTER its constructor in directive-Module ^^<
	 */
	this.$scope.hackVar = false;
	this.$scope.$watch('hackVar', function() {
        $('.weatherDate').datepicker('option', 'minDate', new Date(_this.$scope.startParameter.startTimestamp));
        $('.weatherDate').datepicker('option', 'maxDate', new Date(_this.$scope.startParameter.endTimestamp));
	});
	
	this.$scope.$watch('currentWeatherEventViewData.decorator', function() {
		_this.$scope.currentWeatherEventViewData.value = _this.$scope.currentWeatherEventViewData.decorator.standardValue;
	});
	
	/*
	 * Add weather event
	 */
	this.$scope.addWeatherEvent = function() {
		_this.$scope.currentWeatherEventViewData.events.push(
			new model.ChangeWeatherEvent(
				UUIDService.get(),
				_this.$scope.currentWeatherEventViewData.decorator.enumId, 
				_this.$scope.currentWeatherEventViewData.datetime, 
				_this.$scope.currentWeatherEventViewData.value, 
				_this.$scope.currentWeatherEventViewData.duration));
	};
	
	/*
	 * Remove weather event
	 */
	this.$scope.deleteWeatherEvent = function(id) {
		_this.$scope.currentWeatherEventViewData.events.splice(id,1);
	};
	
	/*
	 * Recalc
	 */
	function recalcChosenTime() {
		_this.$scope.currentWeatherEventViewData.datetime = _this.$scope.chosenTimeDate
			+ (_this.$scope.chosenTimeHours*1000*60*60)
			+ (_this.$scope.chosenTimeMinutes*1000*60);
	};
	function recalcDuration() {
		_this.$scope.currentWeatherEventViewData.duration = _this.$scope.durationHours
		 	+ _this.$scope.durationMinutes/60;
	};
	
	/*
	 * Watchers
	 * Watch model change and digest.
	 * //TODO: Is there a better workaround?
	 */
	this.$scope.$watch('chosenTimeDate', function() {
		recalcChosenTime();
	});
	this.$scope.$watch('chosenTimeHours', function() {
		recalcChosenTime();
	});
	this.$scope.$watch('chosenTimeMinutes', function() {
		recalcChosenTime();
	});
	this.$scope.$watch('durationHours', function() {
		recalcDuration();
	});
	this.$scope.$watch('durationMinutes', function() {
		recalcDuration();
	});
}