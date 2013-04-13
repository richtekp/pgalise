/*
 * Controller for start view
 *
 * @author Dennis Höting
 */
function StartCtrl($scope, PopupService, MessageIDService) {
	var _this = this;
	this.$scope = $scope;
	ctrl.startCtrl = this;
	
	this.$scope.validationStack = [];
	this.validate = function() {
		// validate traffic server urls
		var counter;
		angular.forEach(_this.$scope.startParameter.trafficServerIPList, function(url1) {
			counter=0;
			angular.forEach(_this.$scope.startParameter.trafficServerIPList, function(url2) {
				if(url1 == url2) counter++;
			});
			if(counter>1) {
				_this.$scope.validationStack.push('Traffic Server URL "' + url1 + '" duplicated.');
			}
		});
	};
	this.validate();
	
	/*
	 * Start it
	 */
	this.$scope.start = function() {
        var messageID = MessageIDService.get();
		ctrl.mainCtrl.sendMessage(
			new model.SimulationStartParameterMessage(
				messageID, 
				_this.$scope.startParameter), 
			function(callbackMessage) {
				if(callbackMessage.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
					ctrl.mainCtrl.applySimulationRunning();
				} else if(callbackMessage.messageType === model.MessageType.SIMULATION_RUNNING) {
					ctrl.mainCtrl.applySimulationRunning(callbackMessage.content);
				} else if(callbackMessage.messageType === model.MessageType.ERROR) {
					PopupService.openMessageDialog('Error on server.');
				} else {
					PopupService.openMessageDialog('Error on server.');
					console.log('Unknown callback message type');
				}
			});
	};
	
	/*
     * Export it
     */
    this.$scope.export = function() {
        var messageID = MessageIDService.get();
        var date = new Date();
        var name = 'export_';
        name += date.format('yyyy')
	        + '-' + date.format('MM')
	        + '-' + date.format('dd')
	        + '_' + date.format('H')
	        + '-' + date.format('mm')
	        + '-' + date.format('ss')
	        + '.xml';
    	ctrl.mainCtrl.sendMessage(
    		new model.SimulationExportParameterMessage(
    			messageID, 
    			_this.$scope.startParameter, 
    			'export_' + new Date().getTime() + '.xml'),
    		function(callbackMessage) {
				PopupService.openLinkDialog(callbackMessage.content);
            });
    };
	
	/*
	 * print start parameters
	 * TODO: This might be more appropriately in a directive!
	 */
	this.$scope.iterateStartParameter = function() {
		var tmp = {name:'hallo',schnee:'knoten'};
		for(var prop in _this.$scope.startParameter) {
			if(typeof _this.$scope.startParameter[prop] === 'object') {
				tmp[prop] = 'some object';
			} else {
				tmp[prop] = _this.$scope.startParameter[prop];
			}
		}
		return tmp;
	};
}