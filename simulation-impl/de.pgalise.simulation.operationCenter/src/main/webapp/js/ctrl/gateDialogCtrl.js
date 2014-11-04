/*
 * Controller for GateDialog
 * 
 * @param SimulationService Injected service for simulation
 * @param ServletCommunicationService Injected service for communication
 * @param PopupService Injected service for popup management
 * @param MessageIDService Injected service for message id management
 * @author Dennis Höting
 */
function GateDialogCtrl($scope, SimulationService, ServletCommunicationService, PopupService, MessageIDService) {
    var _this = this;
    this.$scope = $scope;
    
    // If gate was changed by user
    this.$scope.changed = false;
    
    /*
     * store original gate map and make a copy of it
     */
    this.$scope.originalGateMap = ctrl.gateCtrl.$scope.gateMap;
    this.$scope.gateMap = jQuery.extend(true, {}, this.$scope.originalGateMap);
    
    /*
     * Reset everything
     */
    this.$scope.reset = function(gateInfo) {
        var originalGateInfo = undefined;
        angular.forEach(_this.$scope.originalGateMap, function(tmp) {
            if(tmp.name === gateInfo.name) {
                originalGateInfo = tmp;
            }
        });
        gateInfo.percentage = originalGateInfo.percentage;
    };
    
    /*
     * Invoked if user moves a slider
     */
    this.$scope.valueChanged = function(gateInfo) {
        var originalGateInfo = undefined
        
        // get original value for changed slider;
        angular.forEach(_this.$scope.originalGateMap, function(tmp) {
            if(tmp.name === gateInfo.name) {
                originalGateInfo = tmp;
            }
        });
        
        // Check, weather value has changed
        var currentValueChanged = gateInfo.percentage != originalGateInfo.percentage;
        
        // return value changed (true/false)
        if(!_this.$scope.changed) _this.$scope.changed = currentValueChanged;
        return currentValueChanged;
    };
    
    /*
     * Reload (if gateMap changed while gate map dialog id opened)
     */
    this.$scope.reload = function() {
        _this.$scope.gateMap = jQuery.extend(true, {}, _this.$scope.originalGateMap);
    };
    
    /*
     * Make sensors available for view
     */
    this.$scope.sensors = SimulationService.sensors;

    /*
     * Calculate overall sensor amount
     */
    this.$scope.sensorAmount = function() {
    	var tmp = 0;
    	angular.forEach(_this.$scope.gateMap, function(gateInfo) {
    		tmp += _this.$scope.count(gateInfo.set);
    	});
    	return tmp;
    };
    
    /*
     * Calculate current number of sensors (that pass through gate)
     */
    this.$scope.overallNumber = function() {
    	var result = parseInt(_this.$scope.sensorAmount() / 100 * _this.$scope.overallPercentage());
    	if(isNaN(result)) result = 0;
    	return result;
    };
    
    /*
     * Calculate overall percentage of sensors (passing through gate)
     */
    this.$scope.overallPercentage = function() {
        var tmp = 0;
        angular.forEach(_this.$scope.gateMap, function(gateInfo) {
            tmp += _this.$scope.count(gateInfo.set) / 100 * gateInfo.percentage;
        });
        var result = parseInt(tmp / _this.$scope.sensorAmount() * 100);
    	if(isNaN(result)) result = 100;
    	return result;
    };

    /*
     * Apply the current gate
     */
    this.$scope.applyGate = function() {
    	// Construct empty gate message
        var gateMessage = new model.GateMessage(MessageIDService.get());

        angular.forEach(_this.$scope.gateMap, function(value, key) {
        	// Fill gateMessage
        	gateMessage.add(value.typeId, value.percentage/100);
            
            // Check if something changed
            if(value.percentage !== _this.$scope.originalGateMap[key].percentage) _this.$scope.changed = true;
        });

        // If something changed, send message
        if(_this.$scope.changed) {
        	ServletCommunicationService.send(gateMessage, function(callbackData) {
        		/*
        		 * Callback
        		 */
        		if(callbackData.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
        			/*
        			 * If generic notification (means: everythings fine!)
        			 */
        			
    		        // Store new values
        			angular.forEach(_this.$scope.gateMap, function(value, key) {
        		        _this.$scope.originalGateMap[key] = jQuery.extend(true, {}, value);
        		    });
        			
        			/*
        			 * confirm and close 
        			 */
        			PopupService.openConfirmationDialog('Gate successfully changed', {
            			'close': function() {
            				PopupService.close('confirmation');
            			}
            		});
        			
        			// Synchronized again
                	_this.$scope.changed = false;
        		} else {
        			PopupService.openMessageDialog('Error on server while changing gate!', 'Error');
        		}
        	});
        }
    };
}