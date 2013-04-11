/*
 * General controller
 * This controller is bound to the general view of the initial state.
 * it also has access to the main controller's scope.
 *
 * This controller is responsible for general simulation parameter.
 * 
 * @param $scope the scope
 * @author rofriedr
 */
function GeneralCtrl($scope) {
	var _this = this;
	this.$scope = $scope;
	ctrl.generalCtrl = this;
	
	/*
	 * Initialize
	 */
	this.$scope.clockGeneratorInterval = this.$scope.startParameter.clockGeneratorInterval;
	this.$scope.interval = this.$scope.startParameter.interval;
	this.$scope.sensorUpdateSteps = this.$scope.startParameter.sensorUpdateSteps;
	this.$scope.startHours = parseInt(new Date(this.$scope.startParameter.startTimestamp).format('HH'));
	this.$scope.startMinutes = parseInt(new Date(this.$scope.startParameter.startTimestamp).format('MM'));
	this.$scope.start = this.$scope.startParameter.startTimestamp
		- this.$scope.startHours*1000*60*60
		- this.$scope.startMinutes*1000*60;
	this.$scope.endHours = parseInt(new Date(this.$scope.startParameter.endTimestamp).format('HH'));
	this.$scope.endMinutes = parseInt(new Date(this.$scope.startParameter.endTimestamp).format('MM'));
	this.$scope.end = this.$scope.startParameter.endTimestamp
		- this.$scope.endHours*1000*60*60
		- this.$scope.endMinutes*1000*60;
	
	/*
	 * Update
	 */
	this.$scope.recalcStart = function() {
	    $('#generalEndDate').datepicker('option', 'minDate', new Date(_this.$scope.start));
		_this.$scope.startParameter.startTimestamp = _this.$scope.start 
			+ (_this.$scope.startHours*1000*60*60)
			+ (_this.$scope.startMinutes*1000*60);
	};
	this.$scope.recalcEnd = function() {
        $('#generalStartDate').datepicker('option', 'maxDate', new Date(_this.$scope.end));
		_this.$scope.startParameter.endTimestamp = _this.$scope.end
			+ (_this.$scope.endHours*1000*60*60)
			+ (_this.$scope.endMinutes*1000*60);
	};
	
	this.$scope.isOwnProperty = function(key, set) {
		return set.hasOwnProperty(key);
	};
	
	/*
	 * The start property watcher
	 * watches out for changes in model and digest
	 */
	this.$scope.$watch('clockGeneratorInterval', function(value) {
		_this.$scope.startParameter.clockGeneratorInterval = value;
	});
	this.$scope.$watch('interval', function(value) {
		_this.$scope.startParameter.interval = value;
	});
	this.$scope.$watch('sensorUpdateSteps', function(value) {
		_this.$scope.startParameter.sensorUpdateSteps = value;
	});
	this.$scope.$watch('start', function(value) {
		_this.$scope.recalcStart();
	});
	this.$scope.$watch('startHours', function(value) {
		_this.$scope.recalcStart();
	});
	this.$scope.$watch('startMinutes', function(value) {
		_this.$scope.recalcStart();
	});
	this.$scope.$watch('end', function(value) {
		_this.$scope.recalcEnd();
	});
	this.$scope.$watch('endHours', function(value) {
		_this.$scope.recalcEnd();
	});
	this.$scope.$watch('endMinutes', function(value) {
		_this.$scope.recalcEnd();
	});
}