/**
 * Web Socket Service 
 * @author Dennis Höting
 */
var servletCommunicationServices = angular.module('servletCommunicationServices', []);

servletCommunicationServices.factory('WebSocketService', function(CallbackService, ConsoleService) {
	var WebSocketService = (function() {
		function WebSocketService(CallbackService, ConsoleService) {
			// Construct webSocket connection string
			var location = document.location.toString().replace('http://','ws://').replace('https://','wss://');
			
			this.ws = new WebSocket(location  +'ControlCenter');
			
			/*
			 * on open
			 */
			this.ws.onopen = function(message){
				ctrl.mainCtrl.applyConnectionOpened();
			};
			  
			/*
			 * on message
			 */
			this.ws.onmessage = function(message) {
				// parse json
				var data = $.parseJSON(message.data);
				
				ConsoleService.pushObject('Receiving object:', data);
				
				if(CallbackService.invoke(data.messageID, data)) {
					console.log('Callback invoked');
					return;
				}
				
				// conduct action depending on messageType
				switch(data.messageType) {
					case model.MessageType.ON_CONNECT:
						ctrl.mainCtrl.applyOnConnect(data.content);
						break;
				   	case model.MessageType.SIMULATION_RUNNING:
						ctrl.mainCtrl.applySimulationRunning(data.content);
				   		break;
				   	case model.MessageType.SIMULATION_UPDATE:
				   		ctrl.mainCtrl.applySimulationUpdate(data.content);
				   		break;
				   	case model.MessageType.SIMULATION_STOPPED:
				   		ctrl.mainCtrl.applySimulationStopped();
				   		break;
				   	case model.MessageType.ACCESS_DENIED:
				   		ctrl.mainCtrl.applyAccessDenied(data.content);
				   		break;
				   	default:
				   		console.log(message);
				}
			};
			  
			/* 
			 * on close
			 */
			this.ws.onclose = function(message) {
				ctrl.mainCtrl.applyConnectionClosed();
			};
		}
		
		WebSocketService.prototype.send = function(msg, callback) {
			ConsoleService.pushObject('Sending object:', msg);
			
			if(typeof callback === 'function') {
				CallbackService.register(msg.messageID, callback);
			}
			
			this.ws.send($.toJSON(msg));
		};
		
		return WebSocketService;
	})();
	
	return new WebSocketService(CallbackService, ConsoleService);
});
