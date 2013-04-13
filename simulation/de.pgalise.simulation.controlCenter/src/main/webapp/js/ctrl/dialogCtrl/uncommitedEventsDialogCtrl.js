/*
 * Uncommited Events Dialog controller
 * This controller is bound to the commit view of the runtime state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for the uncommitet changes 
 * the controller has to commit to the operation center.
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 * @param MessageIDService Injected service for the message IDs
 * @author Dennis Höting
 */
function UncommitedEventsDialogCtrl($scope, MessageIDService, PopupService) {
	var _this = this;
	this.$scope = $scope;
    ctrl.uncommitedEventsDialogCtrl = this;
	
    this.$scope.commit = function() {
    	ctrl.mainCtrl.sendMessage(new model.SimulationEventListMessage(MessageIDService.get(), _this.$scope.eventCommitData.datetime, _this.$scope.uncommittedEvents), function(callbackMessage) {
			if(callbackMessage.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
				PopupService.openMessageDialog('Events successfully processed on server.');
				_this.$scope.uncommittedEvents = [];
			} else if(callbackMessage.messageType === model.MessageType.ERROR) {
				PopupService.openMessageDialog('Error on server.');
			}
		});
    };
    
	/*
	 * Initialize
	 */
	this.$scope.chosenTimeDate = this.$scope.eventCommitData.datetime;
	this.$scope.chosenTimeHours = parseInt(new Date(this.$scope.eventCommitData.datetime).format('HH'));
	this.$scope.chosenTimeMinutes = parseInt(new Date(this.$scope.eventCommitData.datetime).format('MM'));

    this.$scope.hackVar = false;
    this.$scope.$watch('hackVar', function() {
        $('.commitDate').datepicker('option', 'minDate', new Date(_this.$scope.startParameter.startTimestamp));
        $('.commitDate').datepicker('option', 'maxDate', new Date(_this.$scope.startParameter.endTimestamp));
	});
    
	/*
	 * Recalc
	 */
	this.$scope.recalcChosenTime = function() {
		_this.$scope.eventCommitData.datetime = _this.$scope.chosenTimeDate
			- (12*1000*60*60)		// datePicker-Date is based on 12 o'clock :-I
			+ (_this.$scope.chosenTimeHours*1000*60*60)
			+ (_this.$scope.chosenTimeMinutes*1000*60);
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
	
	this.$scope.deleteEvent = function(id) {
		_this.$scope.uncommittedEvents.splice(id,1);
	};
}
