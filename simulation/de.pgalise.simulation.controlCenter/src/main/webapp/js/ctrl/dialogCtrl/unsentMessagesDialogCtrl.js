/*
 * Unsent Messages Dialog controller
 * This controller is bound to the commit view of the runtime state.
 * It also has access to the main controller's scope.
 * 
 * This controller is responsible for the uncommitet changes 
 * the controller has to commit to the operation center.
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 * @author Dennis Höting
 */
function UnsentMessagesDialogCtrl($scope, PopupService) {
	var _this = this;
	this.$scope = $scope;
    ctrl.unsentMessagesDialogCtrl = this;
    
    this.$scope.send = function() {
    	angular.forEach(_this.$scope.unsentMessages, function(msg) {
    		ctrl.mainCtrl.sendMessage(msg, function(callbackMessage) {
				if(callbackMessage.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
					PopupService.openMessageDialog('Messages successfully processed on server.');
					_this.$scope.unsentMessages = [];
				} else if(callbackMessage.messageType === model.MessageType.ERROR) {
					PopupService.openMessageDialog('Error on server.');
				}
			});
    	});
    };
    
    this.$scope.deleteMessage = function() {
		_this.$scope.unsentMessages.splice(id,1);
    };
}
