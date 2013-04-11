(function (model) {
	model.messages = {};
	/**
	 * Simulation messages for the client-server communication 
	 */
	var OsmAndBusstopFileMessage = (function() {
		function OsmAndBusstopFileMessage(id, osmFileName, busStopFileName) {
			return {
				messageID : id,
				messageType : model.MessageType.OSM_AND_BUSSTOP_FILE_MESSAGE,
				content : {
					osmFileName : osmFileName,
					busStopFileName : busStopFileName
				}
			};
		}
		
		return OsmAndBusstopFileMessage;
	})();
	model.OsmAndBusstopFileMessage = OsmAndBusstopFileMessage;
	
	var AskForValidNodeMessage = (function() {
		function AskForValidNodeMessage(id, node) {
			return {
				messageID : id,
				messageType : model.MessageType.ASK_FOR_VALID_NODE,
				content : node
			};
		}
		
		return AskForValidNodeMessage;
	})();
	model.AskForValidNodeMessage = AskForValidNodeMessage;
	
	var SimulationStartParameterMessage = (function() {
		function SimulationStartParameterMessage(id, startParameter) {
			return {
				messageID : id,
				messageType : model.MessageType.SIMULATION_START_PARAMETER,
				content : startParameter
			};
		}
		
		return SimulationStartParameterMessage;
	})();
	model.SimulationStartParameterMessage = SimulationStartParameterMessage;
	
	var SimulationExportParameterMessage = (function() {
		function SimulationExportParameterMessage(id, startParameter, fileName) {
			return {
				messageID : id,
				messageType : model.MessageType.SIMULATION_EXPORT_PARAMETER,
				content : {ccSimulationStartParameter: startParameter, fileName: fileName}
			};
		}
		
		return SimulationExportParameterMessage;
	})();
	model.SimulationExportParameterMessage = SimulationExportParameterMessage;
	
	var ImportXMLStartParameterMessage = (function() {
		function ImportXMLStartParameterMessage(id, xmlFile) {
			return {
				messageID : id,
				messageType : model.MessageType.IMPORT_XML_START_PARAMETER,
				content : xmlFile
			};
		}
		
		return ImportXMLStartParameterMessage;
	})();
	model.ImportXMLStartParameterMessage = ImportXMLStartParameterMessage;
	
	var LoadSimulationStartParameterMessage = (function() {
		function LoadSimulationStartParameterMessage(id, path) {
			return {
				messageID : id,
				messageType : model.MessageType.LOAD_SIMULATION_START_PARAMETER,
				content : path
			};
		}
		
		return LoadSimulationStartParameterMessage;
	})();
	model.LoadSimulationStartParameterMessage = LoadSimulationStartParameterMessage;
	
	var SimulationEventListMessage = (function() {
		function SimulationEventListMessage(id, timestamp, events) {
			var uuidService = angular.element(document).injector().get('UUIDService');
			return {
				messageID : id,
				messageType : model.MessageType.SIMULATION_EVENT_LIST,
				content : {
					eventList : events,
					id : uuidService.get(),
					timestamp : timestamp
				}
			};
		}
		
		return SimulationEventListMessage;
	})();
	model.SimulationEventListMessage = SimulationEventListMessage;
	
	var CreateRandomVehiclesMessage = (function() {
		function CreateRandomVehiclesMessage(id, sensorBundle) {
			return {
				messageID : id,
				messageType : model.MessageType.CREATE_RANDOM_VEHICLES,
				content : sensorBundle
			};
		}
		
		return CreateRandomVehiclesMessage;
	})();
	model.CreateRandomVehiclesMessage = CreateRandomVehiclesMessage;
	
	var CreateAttractionEventsMessage = (function() {
		function CreateAttractionEventsMessage(id, attractionDataList) {
			return {
				messageID : id,
				messageType : model.MessageType.CREATE_ATTRACTION_EVENTS_MESSAGE,
				content : attractionDataList
			};
		}
		
		return CreateAttractionEventsMessage;
	})();
	model.CreateAttractionEventsMessage = CreateAttractionEventsMessage;
	
	var SimulationStopMessage = (function() {
		function SimulationStopMessage(id) {
			return {
				messageID : id,
				messageType : model.MessageType.SIMULATION_STOP
			};
		}
		
		return SimulationStopMessage;
	})();
	model.SimulationStopMessage = SimulationStopMessage;
})(model);