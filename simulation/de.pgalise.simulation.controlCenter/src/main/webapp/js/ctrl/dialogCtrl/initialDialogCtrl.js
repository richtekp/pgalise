/*
 * Initial Dialog controller
 * This controller is bound to the main view of the initial state
 * it also has access to the main controller's scope.
 * 
 * This controller is responsible for setting/loading
 * the initial parameters for the simulation
 * 
 * @param $scope the scope
 * @param PopupService Injected service for pop-up management
 * @param MapService Injected service for map
 * @param MessageIDService Injected service for the message IDs
 * @author Dennis Höting
 */
function InitialDialogCtrl($scope, PopupService, MapService, MessageIDService) {
	var _this = this;
	this.$scope = $scope;
    ctrl.initialDialogCtrl = this;
	
    // Initial radio button state (create, recent, import)
	this.$scope.chosenInitialType = 'create';
	
	// Chosen recent scenario
	this.$scope.chosenRecentScenario = undefined;
	
	// Chosen xml for import
	this.importedXML = undefined;
	
	/*
	 * Send OSM and BusStopFile to server and process callback
	 */
	this.sendOsmAndBusstop = function(osmAndBusstopFileData) {
	    ctrl.mainCtrl.$scope.osm.osmParsedState = 'in progress';
        var messageID = MessageIDService.get();
		ctrl.mainCtrl.sendMessage(
				new model.OsmAndBusstopFileMessage(
					messageID, 
					osmAndBusstopFileData.osmFileName, 
					osmAndBusstopFileData.busStopFileName),
				/**
				 * Callback
				 * callbackMessage is of type OSMParsedMessage
				 * content : {
				 *     northEast {
				 *         latitude : {
				 *             degree : float
				 *         },
				 *         longitude : {
				 *             degree : float
				 *         }
				 *     },
				 *     southWest {
				 *         latitude : {
				 *             degree : float
				 *         },
				 *         longitude : {
				 *             degree : float
				 *         }
				 *     },
				 * }
				 */
				function(callbackMessage) {
					// Check if callback is appropriate
					if(callbackMessage.messageType !== model.MessageType.OSM_PARSED) {
						// Callback is error
						if(callbackMessage.messageType === model.MessageType.ERROR) {
							PopupService.openMessageDialog('Error while parsing OSM and BusStops on server.');
							console.log(callbackMessage.content);
							return;
						}
						console.log('Unrecognizable callback called ... dafuq?');
						return;
					}

					// Set bounds to mapService, set ready and initialize
					MapService.setReady(callbackMessage.content);
					if (typeof _this.$scope.contentURL === 'undefined') {
						MapService.init();
					}
					
					// Set status
					_this.$scope.osm.osmParsedState = 'done';
				});
	};
	
	/*
	 * Callback thatis invoked when importing xml (or recent scenario)
	 */
	this.importCallback = function(callbackMessage) {
		// Check appropriate callback
		if(callbackMessage.messageType !== model.MessageType.SIMULATION_START_PARAMETER) {
			// Callback is error
			if(callbackMessage.messageType === model.MessageType.ERROR) {
				PopupService.openMessageDialog('Error while parsing OSM and BusStops on server.');
				console.log(callbackMessage.content);
				return;
			}
			console.log('Unrecognizable callback called ... dafuq?');
			return;
		}
		
		// Temporarily save start paraneters
		var importedStartParameter = callbackMessage.content;
		
		// Request OSM and BusStop parsing
		_this.sendOsmAndBusstop(importedStartParameter.osmAndBusstopFileData);
		
		// Import into UI
		ctrl.mainCtrl.performStartParameter2UI(importedStartParameter);		
		
		// Close Dialog
		PopupService.close('initial');
	};
	
	/**
	 * On click on "Confirm"
	 */
	this.$scope.confirmInitialDialog = function() {
		switch(_this.$scope.chosenInitialType) {
			/*
			 * New Scenario is to be created
			 */
			case 'create':
				if(typeof _this.$scope.startParameter.osmAndBusstopFileData.osmFileName === 'undefined'
				|| typeof _this.$scope.startParameter.osmAndBusstopFileData.busStopFileName === 'undefined')
					return;
				
				_this.sendOsmAndBusstop(_this.$scope.startParameter.osmAndBusstopFileData);
				PopupService.close('initial');
				break;
			/*
			 * Recently started scenario is to be loaded
			 */
			case 'recent':
				if(typeof _this.$scope.chosenRecentScenario === 'undefined')
					return;

		        var messageID = MessageIDService.get();
				ctrl.mainCtrl.sendMessage(
					new model.LoadSimulationStartParameterMessage(
						messageID, 
						_this.$scope.chosenRecentScenario.path), 
						_this.importCallback);
				PopupService.close('initial');
				break;
			/*
			 * XML is to be imported
			 */
			case 'import':
				if(typeof _this.importedXML === 'undefined') 
					return;
		        var messageID = MessageIDService.get();
				ctrl.mainCtrl.sendMessage(
					new model.ImportXMLStartParameterMessage(
						messageID, 
						_this.importedXML), 
						_this.importCallback);
				PopupService.close('initial');
				break;
		}
	};
	
	/*
	 * Watch uploaded XML file
	 */
	$('#fileUpload').change(function(evt) {
		if($(this).val()) {
			var file = evt.target.files[0];
			var reader = new FileReader();
			reader.onloadend = function(evt) {
				if(evt.target.readyState == FileReader.DONE) {
					_this.importedXML = evt.target.result;
				}
			};
			reader.readAsBinaryString(file);
		};
	});
};