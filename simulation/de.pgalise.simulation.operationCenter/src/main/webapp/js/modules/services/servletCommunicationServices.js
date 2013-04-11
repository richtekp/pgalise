/*
 * Servlet communication service module
 */
var servletCommunicationServices = angular.module('servletCommunicationServices', []);

/*
 * WebSocket implementation
 * @param EventLogService Injected event log service
 * @param CallbackService Injected callback service
 * @param ConsoleService Injected console service
 */
servletCommunicationServices.factory('WebSocketService', function(EventLogService, CallbackService, ConsoleService) {
	var WebSocketService = (function() {
		/*
		 * Constructor of service (invoked below!)
		 */
		function WebSocketService() {
			// Construct webSocket connection string
			var location = document.location.toString().replace('http://','ws://').replace('https://','wss://');
			this.ws = new WebSocket(location  +'OperationCenter');
			
			/*
			 * on open
			 */
			this.ws.onopen = function(m){
				ctrl.mainCtrl.applyConnectionOpened();
				EventLogService.push(EventLogService.types.NOTIFICATION, 'Connection established');
			};
					  
			/*
			 * on message
			 */
			this.ws.onmessage = function(message) {
				// parse json
				var data = $.parseJSON(message.data);
				
				// Push object into console
				ConsoleService.pushObject('Receiving object:', data);

				// Invoke callback
				if(CallbackService.invoke(data.messageID, data)) {
					console.log('Callback invoked');
					return;
				}
					
				// Call appropriate methods in main controller
				if(typeof data.messageType !== 'undefined') {
					// conduct action depending on messageType
				   	switch(data.messageType) {
				   		case model.MessageType.SENSOR_DATA:
				   			ctrl.mainCtrl.applySensorDataMessage(data.content, data.time);
				   			break;
				   		case model.MessageType.NEW_VEHICLE_MESSAGE:
				   			ctrl.mainCtrl.applyNewVehiclesMessage(data.content);
				   			break;
				   		case model.MessageType.NEW_SENSORS:
				   			ctrl.mainCtrl.applyNewSensorsMessage(data.content);
				   			break;
				   		case model.MessageType.REMOVE_SENSORS:
				   			ctrl.mainCtrl.applyRemoveSensorsMessage(data.content);
				   			break;
				   		case model.MessageType.GPS_SENSOR_TIMEOUT: 
				   			ctrl.mainCtrl.applyGPSSensorTimeoutMessage(data.content);
				   			break;
				   		case model.MessageType.ON_CONNECT:
				   			ctrl.mainCtrl.applyOnConnectMessage(data.content);
							EventLogService.push(EventLogService.types.NOTIFICATION, 'On connect message received');
				   			break;
					   	case model.MessageType.ERROR:
							EventLogService.push(EventLogService.types.ERROR, data.content);
							break;
				   		case model.MessageType.SIMULATION_INIT:
				   			ctrl.mainCtrl.applySimulationInitMessage(data.content);
							EventLogService.push(EventLogService.types.NOTIFICATION, 'Simulation initialized');
							break;
				  		case model.MessageType.SIMULATION_START:
				  			ctrl.mainCtrl.applySimulationStartMessage(data.content);
							EventLogService.push(EventLogService.types.NOTIFICATION, 'Simulation started');
				   			break;
				   		case model.MessageType.SIMULATION_STOP:
				  			ctrl.mainCtrl.applySimulationStopMessage();
							EventLogService.push(EventLogService.types.NOTIFICATION, 'Simulation stopped');
				   			break;
				   		case model.MessageType.GENERIC_NOTIFICATION_MESSAGE:
				   			// nothing
				   			break;
				   		case model.MessageType.TEST_SERVER_HEATMAP_DATA:
				   			ctrl.mainCtrl.applyTestServerHeatmapData(data.content);
				   			break;
					   	default:
							EventLogService.push(EventLogService.types.ERROR, data.content);
				   	}
				} else {
					// Message type is undefined
					EventLogService.push(EventLogService.types.ERROR, 'Unknown error');
				}
			};
				  
			/* 
			 * on close
			 */
			this.ws.onclose = function() {
				ctrl.mainCtrl.applyConnectionClosed();
				EventLogService.push(EventLogService.types.NOTIFICATION, 'Connection aborted');
			};
		}
		
		/*
		 * Send message
		 */
		WebSocketService.prototype.send = function(msg, callback) {
			// Push into console
			ConsoleService.pushObject('Sending object:', msg);
			
			// register callback
			if(typeof callback === 'function') {
				CallbackService.register(msg.messageID, callback);
			};
			
			// parse and send
			this.ws.send($.toJSON(msg));
		};
		
		return WebSocketService;
	})();
	
	// Constructor is invoked here
	return new WebSocketService();
});
