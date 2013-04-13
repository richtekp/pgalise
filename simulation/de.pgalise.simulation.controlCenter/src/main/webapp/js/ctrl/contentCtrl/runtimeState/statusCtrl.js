/*
 * Status controller
 * This controller is bound to the status view of the runtime state.
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for controlling the simulation's status.
 * 
 * @param $scope the scope
 * @param MessageIDService Injected service for the message IDs
 * @param PopupService Injected service for pop-up management
 * @author Dennis Höting
 */
function StatusCtrl($scope, PopupService, MessageIDService) {
	var _this = this;
	this.$scope = $scope;
	ctrl.statusCtrl = this;
	
	this.$scope.stopSimulation = function() {
		PopupService.openConfirmationDialog('Stopping the simulation is not revertable.\nAre you sure about that?', {'Yes, stop!':function() {
	        var messageID = MessageIDService.get();
			_this.$scope.$apply(function() {
				ctrl.mainCtrl.sendMessage(new model.SimulationStopMessage(messageID), function(callbackMessage) {
					if(callbackMessage.messageType === model.MessageType.GENERIC_NOTIFICATION_MESSAGE) {
						PopupService.openMessageDialog('Simulation stopped on server.');
					} else if(callbackMessage.messageType === model.MessageType.ERROR) {
						PopupService.openMessageDialog('Error on server.');
					}
				});
				_this.$scope.simulation.simulationState = "stopped";
			});
			PopupService.close('confirmation');
		}, 'No, don\'t stop':function() {
			PopupService.close('confirmation');
		}});
	};
}