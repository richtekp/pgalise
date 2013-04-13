/*
 * Main controller
 * This controller is bound to body-Element in view,
 * that is, all components in view have access to main controller scope.
 * All other controllers scopes are inherited in main controller scope.
 * 
 * Since the body-element in view is ubiquitous, the main controller is ubiquitous as well.
 * Thus, it is stored in the global object variable "ctrl".
 * 
 * @param $scope the scope
 * @param SimulationService Injected service for simulation 
 * @param ServletCommunicationService Injected service for communication with server
 * @param MapService Injected service for map
 * @param PopupService Injected service for popup management
 * @author Dennis Höting
 */
function MainCtrl($scope, SimulationService, ServletCommunicationService, MapService, PopupService) {
	var _this = this;
	ctrl.mainCtrl = this;
	this.$scope = $scope;

	/*
	 * Store simulation information
	 */
	this.$scope.simulationInfo = {
		simulationState : 'unknown',
		simulationStateColor : function() {
			return _this.$scope.simulationInfo.simulationState == 'paused' ? 'orange' : (_this.$scope.simulationInfo.simulationState == 'running' ? 'green' : 'red');
		},
		simulationStepCounter : 0,
		initParameter : undefined,
		startParameter : undefined,
		simulationTime : 0,
		percent : 0,
		left : 0,
		sensorTypeMap : undefined,
		sensorUnitMap : undefined
	};

	/*
	 * Read sensorUnitMap in simulation information
	 */
	this.$scope.getUnit = function(type) {
		return _this.$scope.simulationInfo.sensorUnitMap[type];
	};

	/*
	 * Watch simulation time change
	 * If changed, reallocate progress bar state
	 */ 
	this.$scope.$watch('simulationInfo.simulationTime', function(newValue) {
		if (typeof newValue === 'undefined' || newValue === 0)
			return;

		var elapsed = newValue - _this.$scope.simulationInfo.initParameter.startTimestamp;
		var total = _this.$scope.simulationInfo.initParameter.endTimestamp - _this.$scope.simulationInfo.initParameter.startTimestamp;
		var left = total - elapsed;
		var percent = parseInt((elapsed / total) * 100);
		_this.$scope.simulationInfo.percent = percent;
		_this.$scope.simulationInfo.left = left;
		$('#simulationProgress').progressbar('option', 'value', percent);
	});

	/*
	 * Store connection information
	 */
	this.$scope.connectionInfo = {
		connectionState : 'unknown',
		connectionStateColor : function() {
			return this.connectionState == 'connecting' ? 'orange' : (this.connectionState == 'connected' ? 'green' : 'red');
		}
	};

	/*
	 * Connection opened
	 * Called in ServletCommunicationService
	 */
	this.applyConnectionOpened = function() {
		if (!_this.$scope.$$phase) {
			_this.$scope.$apply(function() {
				_this.$scope.connectionInfo.connectionState = 'connected';
			});
		}
	};

	/*
	 * Connection closed
	 * Called in ServletCommunicationService
	 */
	this.applyConnectionClosed = function() {
		if (!_this.$scope.$$phase) {
			_this.$scope.$apply(function() {
				_this.$scope.connectionInfo.connectionState = 'disconnected';
				_this.$scope.simulationInfo.simulationState = 'unknown';
			});
		}
	};

	/*
	 * Refresh rate for ui update (and map refresh is fastMapRefresh=false)
	 */
	this.$scope.refreshRate = 1000;
	
	/*
	 * Auto update state and method to be invoked at state change
	 */
	this.$scope.autoUpdate = true;
	this.$scope.autoUpdateChange = function() {
		_this.$scope.autoUpdate = !_this.$scope.autoUpdate;

		if (_this.$scope.autoUpdate) {
			_this.$scope.update();
		}
	};
	
	/*
	 * FastMapRefresh state and method to be invoked at state change
	 * FastMapRefresh=false should lead to performance advantages
	 * TODO: To be tested propertly
	 */
	this.$scope.fastMapRefresh = true;
	this.$scope.fastMapRefreshChange = function() {
		_this.$scope.fastMapRefresh = !_this.$scope.fastMapRefresh;
	};
	
	/*
	 * Update method
	 */
	this.$scope.update = function() {
		for ( var id in SimulationService.sensors) {
			MapService.updateMarker(SimulationService.sensors[id]);
			_this.$scope.simulationInfo.simulationStepCounter = 0;
		}
	};

	/*
	 * Apply view method for "lazy view"
	 * This method is invoked every time, anything changed on the model.
	 * Since a full view update would cause performance issues, 
	 * this method synchronized the view update with the set refresh rate.
	 * 
	 * This causes huge performance advantages!
	 */
	this.lastApply = new Date().getTime();
	this.applyView = function() {
		var time = new Date().getTime();
		if (time - _this.lastApply > _this.$scope.refreshRate) {
			if (!_this.$scope.$$phase) {
				_this.lastApply = new Date().getTime();
				_this.$scope.$apply();
			}
		}
	};

	/*
	 * Apply server message "OnConnectMessage"
	 * Called in ServletCommunicationService
	 * 
	 * store sensorTypeMap and sensorUnitMap and 
	 * apply view
	 */
	this.applyOnConnectMessage = function(data) {
		_this.$scope.simulationInfo.sensorTypeMap = data.sensorTypeMap;
		_this.$scope.simulationInfo.sensorUnitMap = data.sensorUnitMap;

		_this.applyView();
	};

	/*
	 * Apply server message "SimulationInitMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Set simulation state and time,
	 * store initParameter,
	 * initialize map with given boundaries and
	 * apply view
	 */
	this.applySimulationInitMessage = function(data) {
		_this.$scope.simulationInfo.simulationState = 'initialized';
		_this.$scope.simulationInfo.simulationTime = data.startTimestamp;
		_this.$scope.simulationInfo.initParameter = data;

		MapService.init(data.cityBoundary);

		_this.applyView();
	};

	/*
	 * Apply server message "SimulationStartMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Set simulation state,
	 * store startParameter and
	 * apply view
	 */
	this.applySimulationStartMessage = function(data) {
		_this.$scope.simulationInfo.simulationState = 'running';
		_this.$scope.simulationInfo.startParameter = data;

		_this.applyView();
	};

	/*
	 * Apply server message "NewVehiclesMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Pass vehicles into simulation and apply view
	 */
	this.applyNewVehiclesMessage = function(data) {
		SimulationService.applyNewVehiclesMessage(data);

		_this.applyView();
	};

	/*
	 * Apply server message "NewSensorsMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Pass sensors into simulation and apply view
	 */
	this.applyNewSensorsMessage = function(data) {
		SimulationService.applyNewSensorsMessage(data);

		_this.applyView();
	};

	/*
	 * Apply server message "SensorDataMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Set simulation time,
	 * pass data into simulation and 
	 * apply view
	 */
	this.applySensorDataMessage = function(data, timestamp) {
		_this.$scope.simulationInfo.simulationTime = timestamp;
		SimulationService.applySensorDataMessage(data);

		_this.applyView();
	};

	/*
	 * Apply server message "GPSSensorTimeoutMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Pass data into simulation and apply view
	 */
	this.applyGPSSensorTimeoutMessage = function(sensorData) {
		SimulationService.applyGPSSensorTimeoutMessage(sensorData);

		_this.applyView();
	};

	/*
	 * Apply server message "RemoveSensorsMessage"
	 * Called in ServletCommunicationService
	 * 
	 * Pass data into simulation and apply view
	 */
	this.applyRemoveSensorsMessage = function(data) {
		SimulationService.applyRemoveSensorsMessage(data);

		_this.applyView();
	};

	/*
	 * Showcase for server-side heat map
	 * 
	 * On the server side, a HeatMap.java-Object can be constructed and passed to the client
	 * This data is then just shown on a certain heat map.
	 * Called in ServletCommunicationService
	 * 
	 * This method acts as a template for data as such.
	 * 
	 * Data is passed into map service and view is applied
	 */
	this.applyTestServerHeatmapData = function(heatMapData) {
		MapService.redrawTestServerHeatmap(heatMapData);

		_this.applyView();
	};
	
	/*
	 * Apply server message "SimulationStop"
	 * Called in ServletCommunicationService
	 * 
	 * Set simulation state and
	 * apply view
	 */
	this.applySimulationStopMessage = function() {
		_this.$scope.simulationInfo.simulationState = 'stopped';

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateFavoritesDepiction = function() {
		ctrl.listCtrl.$scope.favoriteGPSDisabled = false;
		ctrl.listCtrl.$scope.favoriteTrafficDisabled = false;
		ctrl.listCtrl.$scope.favoriteEnergyDisabled = false;
		ctrl.listCtrl.$scope.favoriteWeatherDisabled = false;

		MapService.activateFavoritesDepiction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateFavoritesDepiction = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateFavoritesDepiction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activatePointDepiction = function() {
		ctrl.listCtrl.$scope.carsDisabled = false;
		ctrl.listCtrl.$scope.trucksDisabled = false;
		ctrl.listCtrl.$scope.motorcyclesDisabled = false;
		ctrl.listCtrl.$scope.bussesDisabled = false;
		ctrl.listCtrl.$scope.bikesDisabled = false;
		ctrl.listCtrl.$scope.trafficLightIntersectionsDisabled = false;
		ctrl.listCtrl.$scope.inductionLoopsDisabled = false;
		ctrl.listCtrl.$scope.topoRadarsDisabled = false;
		ctrl.listCtrl.$scope.photovoltaikDisabled = false;
		ctrl.listCtrl.$scope.windPowerPlantsDisabled = false;
		ctrl.listCtrl.$scope.smartMeterDisabled = false;
		ctrl.listCtrl.$scope.weatherStationsDisabled = false;

		MapService.activatePointDepiction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivatePointDepiction = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivatePointDepiction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateIconDepiction = function() {
		ctrl.listCtrl.$scope.carsDisabled = false;
		ctrl.listCtrl.$scope.trucksDisabled = false;
		ctrl.listCtrl.$scope.motorcyclesDisabled = false;
		ctrl.listCtrl.$scope.bussesDisabled = false;
		ctrl.listCtrl.$scope.bikesDisabled = false;
		ctrl.listCtrl.$scope.trafficLightIntersectionsDisabled = false;
		ctrl.listCtrl.$scope.inductionLoopsDisabled = false;
		ctrl.listCtrl.$scope.topoRadarsDisabled = false;
		ctrl.listCtrl.$scope.photovoltaikDisabled = false;
		ctrl.listCtrl.$scope.windPowerPlantsDisabled = false;
		ctrl.listCtrl.$scope.smartMeterDisabled = false;
		ctrl.listCtrl.$scope.weatherStationsDisabled = false;

		MapService.activateIconDepiction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateIconDepiction = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateIconDepiction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateHeatMapDepictionGPS = function() {
		ctrl.listCtrl.$scope.carsDisabled = false;
		ctrl.listCtrl.$scope.trucksDisabled = false;
		ctrl.listCtrl.$scope.motorcyclesDisabled = false;
		ctrl.listCtrl.$scope.bussesDisabled = false;
		ctrl.listCtrl.$scope.bikesDisabled = false;

		MapService.activateHeatMapDepictionGPS();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateHeatMapDepictionGPS = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateHeatMapDepictionGPS();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateHeatMapDepictionSpeed = function() {
		ctrl.listCtrl.$scope.carsDisabled = false;
		ctrl.listCtrl.$scope.trucksDisabled = false;
		ctrl.listCtrl.$scope.motorcyclesDisabled = false;
		ctrl.listCtrl.$scope.bussesDisabled = false;
		ctrl.listCtrl.$scope.bikesDisabled = false;

		MapService.activateHeatMapDepictionSpeed();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateHeatMapDepictionSpeed = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateHeatMapDepictionSpeed();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateHeatMapDepictionEnergyProduction = function() {
		ctrl.listCtrl.$scope.photovoltaikDisabled = false;
		ctrl.listCtrl.$scope.windPowerPlantsDisabled = false;

		MapService.activateHeatMapDepictionEnergyProduction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateHeatMapDepictionEnergyProduction = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateHeatMapDepictionEnergyProduction();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateHeatMapDepictionEnergyConsumption = function() {
		ctrl.listCtrl.$scope.smartMeterDisabled = false;

		MapService.activateHeatMapDepictionEnergyConsumption();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateHeatMapDepictionEnergyConsumption = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateHeatMapDepictionEnergyConsumption();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.activateTestServerHeatMap = function() {
		MapService.activateTestServerHeatMap();

		_this.applyView();
	};

	/*
	 * Activate/Deactivate depiction
	 * 
	 * Set variabled to enable/disable checkboxes,
	 * call method in MapService and
	 * apply view
	 */
	this.deactivateTestServerHeatMap = function() {
		ctrl.listCtrl.disableAll();
		MapService.deactivateTestServerHeatMap();

		_this.applyView();
	};

	/*
	 * Open Cognos dialog
	 * 
	 * Call method in popup service
	 */
	this.activateCognos = function() {
		PopupService.openCognosDialog();
	};

	// Change Content Size on window resize event and right now
	calculateAreaSize();
	$(window).resize(calculateAreaSize);

	/*
	 * Calculate and set sizes for main areas
	 */
	function calculateAreaSize() {
		var height = $(window).height();
		var headerHeight = $('div#div_top').css('height');
		var topHeight = $('div#layout_top').css('height');
		var resultHeight = parseInt(height) - parseInt(headerHeight) - parseInt(topHeight) - 15;
		$('div#div_control').css('height', resultHeight);
		$('div#div_content').css('height', resultHeight);
		$('div#map').css('height', resultHeight);

		var width = $(window).width();
		var controlWidth = 350;
		var resultWidth = parseInt(width) - controlWidth;
		$('div#div_control').css('width', controlWidth);
		$('div#div_content').css('width', resultWidth);
		$('div#map').css('width', resultWidth);
	}
	
	/*
	 * Get timespan in proper format
	 */
	this.$scope.timespanFormat = function(timespanInMillis) {
		var s = 1000;
		var m = s * 60;
		var h = m * 60;
		var d = h * 24;

		var days = parseInt(timespanInMillis / d);
		var hours = parseInt((timespanInMillis % d) / h);
		var minutes = parseInt((timespanInMillis % h) / m);
		var seconds = parseInt((timespanInMillis % m) / s);

		return days + 'days, ' + ("0" + hours).slice(-2) + ':' + ("0" + minutes).slice(-2) + ':' + ("0" + seconds).slice(-2);
	};

	/*
	 * Bind to util-methods (util.js)
     * Like this, these will be available in view
	 */
    this.$scope.count = count;
    
    /*
     * Bind to native methods
     * Like this, these will be available in view
     */
    this.$scope.parseInt = parseInt;
    
	/*
	 * bind test method (mock.js)
     * Like this, it will be available in view
	 */ 
	this.$scope.testMethod = testMethod;
	
	/*
	 * Mock-method to mock server message receipt
	 */
	this.receive = function(msg) {
		ServletCommunicationService.ws.onmessage({
			data : msg
		});
	};
}