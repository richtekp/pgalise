/*
 * Commit controller
 * This controller is bound to the commit view of the runtime state.
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for committing
 * events to the operation center during runtime
 * 
 * @param $scope the scope
 * @param MessageIDService Injected service for the message IDs
 * @param PopupService Injected service for pop-up management
 * @param MapService Injected service for map
 */
function CommitCtrl($scope, MessageIDService, PopupService, MapService, SensorObjectIDService, UUIDService) {
	var _this = this;
	this.$scope = $scope;
	ctrl.commitCtrl = this;
	
	this.$scope.eventCommitData.datetime = ctrl.mainCtrl.$scope.simulation.simulationTime + 1000*60*5;

	this.$scope.validationStack = [];
	this.validate = function() {
		_this.$scope.validationStack = [];
		// validate weather event times
		angular.forEach(_this.$scope.uncommittedWeatherEvents, function(uwe) {
			if(uwe.timestamp < _this.$scope.eventCommitData.datetime) {
				_this.$scope.validationStack.push('Weather event timestamp (' + new Date(uwe.timestamp).format('mm/dd/yyyy @ h:MM:ss') + ') is prior to commit timestamp.');
			}
		});
		
		// validate events
		angular.forEach(_this.$scope.uncommittedEvents, function(ue) {
			switch(ue.eventType) {
				case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CREATE_RANDOM_VEHICLES_EVENT]:
					if(ue.vehicles.length<1) {
						_this.$scope.validationStack.push('No vehicles in path event.');
						return;
					}
					if(ue.vehicles[0].trip.startTime <= ctrl.mainCtrl.$scope.eventCommitData.datetime) {
						_this.$scope.validationStack.push('Vehicle start time in path is prior to commit timestamp');
					}
					break;
				case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT]:
					if(ue.startTimestamp <= ctrl.mainCtrl.$scope.eventCommitData.datetime) {
						_this.$scope.validationStack.push('Energy event start time is prior to commit timestamp');
					}
					break;
				case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.ROAD_BARRIER_TRAFFIC_EVENT]:
					if(ue.roadBarrierStartTimestamp <= ctrl.mainCtrl.$scope.eventCommitData.datetime) {
						_this.$scope.validationStack.push('Street block event start time is prior to commit timestamp');
					}
					break;
			}
		});
	};
	
	this.$scope.send = function() {
    	angular.forEach(_this.$scope.unsentMessages, function(msg) {
    		ctrl.mainCtrl.sendMessage(msg, function(callbackMessage) {
				if(callbackMessage.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
					PopupService.openMessageDialog('Messages successfully processed on server.');
				} else if(callbackMessage.messageType === model.MessageType.ERROR) {
					PopupService.openMessageDialog('Error on server.');
				} else if(callbackMessage.messageType === model.MessageType.USED_IDS_MESSAGE) {
					PopupService.openMessageDialog('Messages successfully processed on server.');
					angular.forEach(callbackMessage.content.integerIDList, function(id) {
						SensorObjectIDService.add(id);
					});
					angular.forEach(callbackMessage.content.uuidList, function(uuid) {
						UUIDService.add(uuid);
					});
				}
			});
    	});
    	
    	ctrl.mainCtrl.$scope.unsentMessages = [];
		_this.validate();
    };
    
	this.$scope.commit = function() {
		var allEvents = _this.$scope.uncommittedEvents.concat(_this.$scope.uncommittedWeatherEvents);
    	ctrl.mainCtrl.sendMessage(new model.SimulationEventListMessage(
    			MessageIDService.get(), 
    			_this.$scope.eventCommitData.datetime, 
    			allEvents), 
    		function(callbackMessage) {
				if(callbackMessage.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
					PopupService.openMessageDialog('Events successfully processed on server.');
				} else if(callbackMessage.messageType === model.MessageType.ERROR) {
					PopupService.openMessageDialog('Error on server.');
				}
			});

		ctrl.mainCtrl.$scope.uncommittedEvents = [];
		ctrl.mainCtrl.$scope.uncommittedWeatherEvents = [];
		_this.validate();
    };
	/*
	 * Initialize
	 */
	this.$scope.chosenTimeHours = parseInt(new Date(this.$scope.eventCommitData.datetime).format('HH'));
	this.$scope.chosenTimeMinutes = parseInt(new Date(this.$scope.eventCommitData.datetime).format('MM'));
	this.$scope.chosenTimeDate = this.$scope.eventCommitData.datetime
		- this.$scope.chosenTimeHours *1000*60*60
		- this.$scope.chosenTimeMinutes *1000*60;
	
	/*
     * Workaround!
     * Only possibility to invoke datepicker-setter AFTER its constructor in directive-Module ^^<
     */
    this.$scope.hackVar = false;
    this.$scope.$watch('hackVar', function() {
        $('#commitDate').datepicker('option', 'minDate', new Date(_this.$scope.startParameter.startTimestamp));
        $('#commitDate').datepicker('option', 'maxDate', new Date(_this.$scope.startParameter.endTimestamp));
    	_this.validate();
    });
	
	/*
	 * Recalc
	 */
	this.$scope.recalcChosenTime = function() {
		_this.$scope.eventCommitData.datetime = _this.$scope.chosenTimeDate
			+ (_this.$scope.chosenTimeHours*1000*60*60)
			+ (_this.$scope.chosenTimeMinutes*1000*60);
	};
	
	/*
	 * Watchers
	 * Watch model change and digest.
	 * //TODO: Is there a better workaround?
	 */
	this.$scope.$watch('chosenTimeDate', function(newDate, oldDate) {
		console.log(oldDate);
		console.log(newDate);
		_this.$scope.recalcChosenTime();
		_this.validate();
	});
	this.$scope.$watch('chosenTimeHours', function() {
		_this.$scope.recalcChosenTime();
		_this.validate();
	});
	this.$scope.$watch('chosenTimeMinutes', function() {
		_this.$scope.recalcChosenTime();
		_this.validate();
	});

	this.$scope.deleteMessage = function(id) {
		var message = _this.$scope.unsentMessages[id];
		if(message.messageType === model.MessageType.CREATE_ATTRACTION_EVENTS_MESSAGE) {
			angular.forEach(message.content, function(attractionData) {
				MapService.removeEventObject(attractionData.id);
			});
		}
		
		_this.$scope.unsentMessages.splice(id,1);
		_this.validate();
	};
	
	this.$scope.deleteEvent = function(id) {
		var event = _this.$scope.uncommittedEvents[id];
		
		if(typeof event !== 'undefined') {
			switch(event.eventType) {
				case  ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.ROAD_BARRIER_TRAFFIC_EVENT]:
				case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT]:
					MapService.removeEventObject(event.id);
					break;
				case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CREATE_RANDOM_VEHICLES_EVENT]:
					MapService.removeEventObject(MapService.eventObjects[event.id].end.id);
					break;
			}
		}
		
		_this.$scope.uncommittedEvents.splice(id,1);
		_this.validate();
	};
	
	this.$scope.deleteWeatherEvent = function(id) {
		_this.$scope.uncommittedWeatherEvents.splice(id,1);
		_this.validate();
	};
	
	this.validate();
}