/*
 * MainCtrl
 * The scope of this ctrl can be referenced by all controlers by default,
 * as it encapsulates their scopes.
 * Additionally, the controller can be referenced using the global package ctrl
 * E.g. Use ctrl.mainCtrl.$scope etc.
 *
 * @author Dennis Höting
 */
function MainCtrl($scope, ServletCommunicationService, MapService, PopupService, ConsoleService, EventObjectIDService, SensorObjectIDService, MessageIDService, UUIDService) {
    var _this = this;
    ctrl.mainCtrl = this;
    this.$scope = $scope;

    /**
     * Open initial Dialog on startup
     */
    PopupService.openInitialDialog();
    
    /**
     * Preferences concerning UI
     */
    this.$scope.ui = {
        state : 'initial'
    };
	
    /**
     * Settings
     */
	this.$scope.settings = {
		autoOpenDialog : true
	};

	/**
	 * Add mouse move listener for map icon on mouse
	 */
	$(document).mousemove(function(e) {
	    var mapDiv = document.getElementById('div_map');
	    if(e.pageY > mapDiv.offsetTop-10 && e.pageX > mapDiv.offsetLeft-15 && 
	        e.pageX < (mapDiv.offsetLeft + mapDiv.offsetWidth-25) && e.pageY < (mapDiv.offsetTop + mapDiv.offsetHeight-25)) {
                $('div#icon_div').css('top', e.pageY + 10); 
                $('div#icon_div').css('left', e.pageX + 15);    
	    }
	});

    /**
     * Connection info
     */
    this.$scope.connection = {
        connectionState : 'unknown',
        connectionStateColor : function() {
            return _this.$scope.connection.connectionState == 'unknown' ? 'orange' : (_this.$scope.connection.connectionState == 'connected' ? 'green' : 'red');
        }
    };

    /**
     * Simulation info
     */
    this.$scope.simulation = {
        simulationState : 'unknown',
        simulationStateColor : function() {
            return _this.$scope.simulation.simulationState == 'unknown' ? 'orange' : (_this.$scope.simulation.simulationState == 'running' ? 'green' : 'red');
        },
        simulationTime : 0
    };

    /**
     * OSM Parsed
     */
    this.$scope.osm = {
        osmParsedState : 'not yet started',
        osmParsedStateColor : function() {
            return _this.$scope.osm.osmParsedState == 'in progress' ? 'orange' : (_this.$scope.osm.osmParsedState == 'done' ? 'green' : 'red');
        }
    };

    /**
     * URLs for partials
     */
    this.navigationURLs = ['partials/navigation/initialNavigation.html', 'partials/navigation/runtimeNavigation.html'];
    this.$scope.navigationURL = this.navigationURLs[0];
    this.initContentURLs = [
        'partials/content/empty.html', 
        'partials/content/initialState/general.html', 
        'partials/content/initialState/infrastructure.html', 
        'partials/content/initialState/city.html', 
        'partials/content/initialState/weather.html', 
        'partials/content/initialState/start.html', 
        'partials/content/initialState/busSystem.html', 
        'partials/content/initialState/interaction.html'];
    this.runtimeContentURLs = [
        'partials/content/empty.html', 
        'partials/content/runtimeState/status.html', 
        'partials/content/runtimeState/weatherEvents.html',
        'partials/content/runtimeState/randomVehicles.html',
        'partials/content/runtimeState/commit.html',
        'partials/content/runtimeState/overview.html'];
    this.$scope.contentURL = this.initContentURLs[0];


    /**
     * Weather events, used in weather view and weather event view
     */
    this.$scope.weatherEvents = [{
        name : 'Hot day',
        valueName : String.fromCharCode(176) + 'C',
        enumId : model.WeatherEvents.HOTDAY,
        min : 20,
        max : 50,
        standardValue : 30
    }, {
        name : 'Cold day',
        valueName : String.fromCharCode(176) + 'C',
        enumId : model.WeatherEvents.COLDDAY,
        min : -30,
        max : 5,
        standardValue : -5
    }, {
        name : 'Rainy day',
        valueName : 'mm',
        enumId : model.WeatherEvents.RAINDAY,
        min : 1,
        max : 10,
        standardValue : 4
    }, {
        name : 'Stormy day',
        valueName : 'km/h',
        enumId : model.WeatherEvents.STORMDAY,
        min : 6,
        max : 35,
        standardValue : 20
    }];

    /**
     * Map Icons, used in map service
     */
    this.$scope.chosenType = undefined;
    this.$scope.mapIcons = [{
        sensortype : model.SensorType.TRAFFICLIGHT,
        cssClass : 'olControlTrafficLight',
        name : "Traffic Light",
        onActivate : function() {
            console.log(_this.$scope.chosenType);
            _this.$scope.chosenType = model.SensorType.TRAFFIC_LIGHT_INTERSECTION;
            console.log(_this.$scope.chosenType);
            $('div#icon_div').css('background-image', 'url(img/traffic_lightM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        sensortype : model.SensorType.WINDPOWERSENSOR,
        cssClass : 'olControlWindPowerSensor',
        name : "Wind Power Sensor",
        onActivate : function() {
            _this.$scope.chosenType = model.SensorType.WINDPOWERSENSOR;
            $('div#icon_div').css('background-image', 'url(img/wind_power_plantM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        sensortype : model.SensorType.PHOTOVOLTAIK,
        cssClass : 'olControlPhotovoltaik',
        name : "Photovoltaik",
        onActivate : function() {
            _this.$scope.chosenType = model.SensorType.PHOTOVOLTAIK;
            $('div#icon_div').css('background-image', 'url(img/photovoltaikM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        sensortype : model.SensorType.INDUCTIONLOOP,
        cssClass : 'olControlInductionLoop',
        name : "Induction Loop",
        onActivate : function() {
            _this.$scope.chosenType = model.SensorType.INDUCTIONLOOP;
            $('div#icon_div').css('background-image', 'url(img/induction_loopM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        sensortype : model.SensorType.WEATHER_STATION,
        cssClass : 'olControlWeatherStation',
        name : "Weather Station",
        onActivate : function() {
            _this.$scope.chosenType = model.SensorType.WEATHER_STATION;
            $('div#icon_div').css('background-image', 'url(img/weather_stationM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        sensortype : model.SensorType.SMARTMETER,
        cssClass : 'olControlSmartMeter',
        name : "Smart Meter",
        onActivate : function() {
            _this.$scope.chosenType = model.SensorType.SMARTMETER;
            $('div#icon_div').css('background-image', 'url(img/smart_meterM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        sensortype : model.SensorType.TOPORADAR,
        cssClass : 'olControlTopoRadar',
        name : "Topo Radar",
        onActivate : function() {
            _this.$scope.chosenType = model.SensorType.TOPORADAR;
            $('div#icon_div').css('background-image', 'url(img/toporadarM.png)');	
       		$('div#icon_div').css('visibility', 'visible');	
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        eventtype : model.EventType.PATHSTART,
        cssClass : 'olControlPath',
        name : "PathStart",
        onActivate : function() {
            _this.$scope.chosenType = model.EventType.PATHSTART;
            $('div#icon_div').css('background-image', 'url(img/flag_green2.png)'); 
            $('div#icon_div').css('visibility', 'visible'); 
        },
        onDeactivate : function() {
        	if (typeof MapService.latestFlag !== 'undefined') {
        		if(MapService.latestFlag.isStart) {
                	MapService.removeEventObject(MapService.latestFlag.id);
                	MapService.latestFlag = undefined;
        		}
        	}
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        eventtype : model.EventType.ATTRACTION,
        cssClass : 'olControlAttraction',
        name : "Attraction",
        onActivate : function() {
            _this.$scope.chosenType = model.EventType.ATTRACTION;
            $('div#icon_div').css('background-image', 'url(img/attractionM.png)'); 
            $('div#icon_div').css('visibility', 'visible'); 
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        eventtype : model.EventType.STREETBLOCK,
        cssClass : 'olControlStreetBlock',
        name : "Street Block",
        onActivate : function() {
            _this.$scope.chosenType = model.EventType.STREETBLOCK;
            $('div#icon_div').css('background-image', 'url(img/street_blockM.png)'); 
            $('div#icon_div').css('visibility', 'visible'); 
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }, {
        eventtype : model.EventType.ENERGYEVENT,
        cssClass : 'olControlEnergyEvent',
        name : "Energy Event",
        onActivate : function() {
            _this.$scope.chosenType = model.EventType.ENERGYEVENT;
            $('div#icon_div').css('background-image', 'url(img/energyeventM.png)'); 
            $('div#icon_div').css('visibility', 'visible'); 
        },
        onDeactivate : function() {
            _this.$scope.chosenType = undefined;
            $('div#icon_div').css('visibility', 'hidden');
        },
        checked : true
    }];
    
    /**
     * Global variables
     */
    this.$scope.accessDenied = false;
    this.$scope.onConnectMessageReceived = false;
    this.$scope.busSystems = [];
    this.$scope.streetMaps = [];
    this.$scope.recentScenarios = [];
    this.$scope.allBusRoutes = [];

    /**
     * Maps for communication purposes
     */
    this.sensorTypeMap = {};
    this.weatherEventTypeMap = {};
    this.simulationEventTypeMap = {};
    this.vehicleTypeMap = {};
    this.vehicleModelMap = {};
    
    /**
     * Save standard values for general-view
     */
	this.$scope.randomCars = 0;
	this.$scope.randomCarGPSRatio = 0;
	this.$scope.randomTrucks = 0;
	this.$scope.randomTruckGPSRatio = 0;
	this.$scope.randomMotorcycles = 0;
	this.$scope.randomMotorcycleGPSRatio = 0;
	this.$scope.randomBikes = 0;
	this.$scope.randomBikeGPSRatio = 0;
		
	/**
	 * Save trafficServerIPList
	 */
	this.$scope.trafficServerIPList = ['localhost:8080'];
	
	/**
	 * Save weather event data
	 */
    this.$scope.currentWeatherEventViewData = {
		datetime : 0,
		duration : 6,
		decorator : this.$scope.weatherEvents[0],
		value : 0,
		events : []
	};

    /**
     * Save commit data
     */
	this.$scope.eventCommitData = {
		datetime : 0,
	};

	/**
	 * Events and messages for runtime state overview
	 */
    this.$scope.uncommittedEvents = [];
    this.$scope.uncommittedWeatherEvents = [];
    this.$scope.unsentMessages = [];
    this.$scope.sentMessages = {};
    
    /**
     * Start parameter object
     * This Object is processed into a JAVA-Object which is pushed into the simulation
     */
    this.$scope.startParameter = new model.SimulationStartParameter(SensorObjectIDService, UUIDService);

    this.$scope.startParameter.specificUpdateSteps[model.SensorType.INDUCTIONLOOP] = {name:'Induction Loop',value:10};
    this.$scope.startParameter.specificUpdateSteps[model.SensorType.SMARTMETER] = {name:'Smart Meter',value:1};
    this.$scope.startParameter.specificUpdateSteps[model.SensorType.WINDPOWERSENSOR] = {name:'Wind Power Sensor',value:1};
    this.$scope.startParameter.specificUpdateSteps[model.SensorType.TOPORADAR] = {name:'Topo Radar',value:1};
    this.$scope.startParameter.specificUpdateSteps[model.SensorType.PHOTOVOLTAIK] = {name:'Photovoltaik',value:1};
    this.$scope.startParameter.specificUpdateSteps[model.SensorType.WEATHER_STATION] = {name:'Weather Station',value:1};
    this.$scope.startParameter.specificUpdateSteps[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = {name:'Traffic Light Intersection',value:1};
    
    /**
     * Watch simulation start timestamp change
     */
    this.$scope.$watch('startParameter.startTimestamp', function(newVal) {
        _this.$scope.simulation.simulationTime = newVal;
        _this.$scope.currentWeatherEventViewData.datetime = newVal;
        _this.$scope.eventCommitData.datetime = newVal;
    });

    /**
     * Put sensors etc. into the start parameter object
     * This method is invoked when start view in chosen
     */
    this.performUI2StartParameter = function() {
        /**
         * BusSystem: Send only active lines
         */
        var tmp = [];
        angular.forEach(_this.$scope.allBusRoutes, function(busLine) {
        	tmp.push(busLine);
        });
        _this.$scope.startParameter.busRouteList = tmp;

        /**
         * WEATHER
         */
        // Clear
        _this.$scope.startParameter.weatherEventList = [];
        
        // Add standard weather events
        _this.$scope.startParameter.weatherEventList.push({
			event : _this.weatherEventTypeMap[model.WeatherEvents.USE_CITY_CLIMATE],
			timestamp : _this.$scope.startParameter.startTimestamp,
			value : _this.$scope.startParameter.useCityClimate ? 1 : -1,
			duration : 0
        });
        _this.$scope.startParameter.weatherEventList.push({
			event : _this.weatherEventTypeMap[model.WeatherEvents.USE_REFERENCE_CITY],
			timestamp : _this.$scope.startParameter.startTimestamp,
			value : _this.$scope.startParameter.useCityClimate ? 1 : -1,
			duration : 0
        });

        // Add other weather events
        _this.$scope.startParameter.weatherEventList = _this.$scope.startParameter.weatherEventList
        	.concat(_this.$scope.currentWeatherEventViewData.events);
        
        /**
         * SensorObjects
         */
        // Clear
        _this.$scope.startParameter.sensorHelperList = [];
        
        // Add SensorHelpers
        for (var id in MapService.sensorObjects) {
            var sensor = MapService.sensorObjects[id];
            switch(sensor.type) {
                case model.SensorType.PHOTOVOLTAIK:
                    _this.$scope.startParameter.sensorHelperList.push(
                    	new model.SensorHelperPhotovoltaik(sensor));
                    break;
                case model.SensorType.SMARTMETER:
                    _this.$scope.startParameter.sensorHelperList.push(
                    	new model.SensorHelperSmartMeter(sensor));
                    break;
                case model.SensorType.WINDPOWERSENSOR:
                    _this.$scope.startParameter.sensorHelperList.push(
                    	new model.SensorHelperWindPower(sensor));
                    break;
                case model.SensorType.TRAFFIC_LIGHT_INTERSECTION:
                    _this.$scope.startParameter.sensorHelperList.push(
                    	new model.SensorHelperTrafficLightIntersection(sensor));
                    break;
                case model.SensorType.WEATHER_STATION:
                    angular.forEach(sensor.subSensors, function(value) {
                        if (value.check) {
                            _this.$scope.startParameter.sensorHelperList.push(
                            	new model.SensorHelperWeather(
                            		SensorObjectIDService.get(), 
                            		value.type, 
                            		sensor.latitude, 
                            		sensor.longitude, 
                            		sensor.updateSteps, 
                            		sensor.id));
                        }
                    });
                    break;
                default:
                    _this.$scope.startParameter.sensorHelperList.push(
                    	new model.SensorHelper(sensor));
            }
        }

        /*
         * Simulation events
         */
        // Clear
        _this.$scope.startParameter.simulationEventLists = [];
		_this.$scope.startParameter.attractionCollection = [];
		
		// Add simulation events
        var simulationEvents = [];
        for(var id in MapService.eventObjects) {
        	var event = MapService.eventObjects[id];
        	switch(event.type) {
        		case model.EventType.ATTRACTION:
        			_this.$scope.startParameter.attractionCollection.push(new model.AttractionData(event, SensorObjectIDService.takenIds, UUIDService.takenUUIDs));
        			break;
        		case model.EventType.PATH:
            		var simEvent = new model.CreateVehiclesEvent(UUIDService.get(), event);
            		simulationEvents.push(simEvent);
        			break;
        		case model.EventType.STREETBLOCK:
        			var simEvent = new model.RoadBarrierTrafficEvent(event);
        			simulationEvents.push(simEvent);
        			break;
        		case model.EventType.ENERGYEVENT:
        			var simEvent = new model.PercentageChangeEnergyEvent(event);
        			simulationEvents.push(simEvent);
        			break;
        	}
        }
        var simEventList = new model.SimulationEventList(simulationEvents, UUIDService.get(), ctrl.mainCtrl.$scope.startParameter.startTimestamp);	 
        _this.$scope.startParameter.simulationEventLists.push(simEventList);
    };
    
    /**
     * Put sensors at runtime into unsent events or messages
     */
    this.performUI2EventsAndMessages = function() {
    	/*
    	 * delete existing attraction messages
    	 */
    	var tmp = [];
    	angular.forEach(_this.$scope.unsentMessages, function(message) {
    		if(message.messageType !== model.MessageType.CREATE_ATTRACTION_EVENTS_MESSAGE) {
        		tmp.push(message);
    		}
    	});
    	_this.$scope.unsentMessages = tmp;
    	
    	/*
    	 * delete existing path/streetBlock/energy-Events
    	 */
    	var tmp2 = [];
    	angular.forEach(_this.$scope.uncommittedEvents, function(event) {
    		if(event.eventType !== ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CREATE_RANDOM_VEHICLES_EVENT]
    		&& event.eventType !== ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.ROAD_BARRIER_TRAFFIC_EVENT]
    		&& event.eventType !== ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT]) {
        		tmp2.push(event);
    		}
    	});
    	_this.$scope.uncommittedEvents = tmp2;
    	
    	/*
    	 * Add everything
    	 */
    	angular.forEach(MapService.eventObjects, function(event) {
    		switch(event.type) {
        		case model.EventType.ATTRACTION:
        			_this.$scope.unsentMessages.push(
        				new model.CreateAttractionEventsMessage(
        					MessageIDService.get(), [
        					    new model.AttractionData(event, SensorObjectIDService.takenIds, UUIDService.takenUUIDs)]));
        			break;
        		case model.EventType.PATH:
        			_this.$scope.uncommittedEvents.push(
        				new model.CreateVehiclesEvent(event));
        			break;
        		case model.EventType.STREETBLOCK:
        			_this.$scope.uncommittedEvents.push(
        				new model.RoadBarrierTrafficEvent(event));
        			break;
        		case model.EventType.ENERGYEVENT:
        			_this.$scope.uncommittedEvents.push(
        				new model.PercentageChangeEnergyEvent(event));
        			break;
        	}
    	});
    };
    
    /**
     * Import start parameters into the UI
     */
    this.performStartParameter2UI = function(startParameter) {
       _this.$scope.startParameter = startParameter;
       
       // workaround
       if(typeof _this.$scope.startParameter.specificUpdateSteps[model.SensorType.INDUCTIONLOOP] === 'undefined') {
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.INDUCTIONLOOP] = {name:'Induction Loop',value:10};
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.SMARTMETER] = {name:'Smart Meter',value:1};
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.WINDPOWERSENSOR] = {name:'Wind Power Sensor',value:1};
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.TOPORADAR] = {name:'Topo Radar',value:1};
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.PHOTOVOLTAIK] = {name:'Photovoltaik',value:1};
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.WEATHER_STATION] = {name:'Weather Station',value:1};
    	   _this.$scope.startParameter.specificUpdateSteps[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = {name:'Traffic Light Intersection',value:1}; 
       }
       
       _this.$scope.allBusRoutes =  startParameter.busRouteList;
       
       /*
        * WEATHER
        */
       var tmp = [];
       angular.forEach(startParameter.weatherEventList, function(someEvent) {
    	   // dont take standard weather events
    	   if(someEvent.event != _this.weatherEventTypeMap[model.WeatherEvents.USE_CITY_CLIMATE]
    	   && someEvent.event != _this.weatherEventTypeMap[model.WeatherEvents.USE_REFERENCE_CITY]) {
    		   tmp.push(someEvent);
    	   }
       });
       _this.$scope.currentWeatherEventViewData.events = tmp;


       /*
        * SensorObjects
        */
       angular.forEach(startParameter.sensorHelperList, function(sensorHelper) {
    	   switch(sensorHelper.sensorType) {
               // Marker is traffic light
               case ctrl.mainCtrl.sensorTypeMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION]:
            	   var objectId = sensorHelper.sensorID;
                   markerName = 'TrafficLight' + objectId;
                   markerObject = new model.TrafficLightIntersection(objectId, sensorHelper.position.latitude.degree, sensorHelper.position.longitude.degree, markerName, sensorHelper.updateSteps, sensorHelper.trafficLightIds);
                   markerObject.nodeId = sensorHelper.nodeId;

                   MapService.addSensor(markerObject);
                   break;
               case ctrl.mainCtrl.sensorTypeMap[model.SensorType.WINDPOWERSENSOR]:
            	   var objectId = sensorHelper.sensorID;
                   markerName = 'WindPowerSensor' + objectId;
                   markerObject = new model.WindPowerSensor(objectId, sensorHelper.position.latitude.degree, sensorHelper.position.longitude.degree, markerName, sensorHelper.updateSteps);
                   markerObject.activityValue = sensorHelper.activityValue;
                   markerObject.rotorLength = sensorHelper.rotorLength;

                   MapService.addSensor(markerObject);
                   break;
               case ctrl.mainCtrl.sensorTypeMap[model.SensorType.PHOTOVOLTAIK]:
            	   var objectId = sensorHelper.sensorID;
                   markerName = 'Photovoltaik' + objectId;
                   markerObject = new model.Photovoltaik(objectId, sensorHelper.position.latitude.degree, sensorHelper.position.longitude.degree, markerName, sensorHelper.updateSteps);
                   markerObject.area = sensorHelper.area;

                   MapService.addSensor(markerObject);
                   break;
               case ctrl.mainCtrl.sensorTypeMap[model.SensorType.INDUCTIONLOOP]:
            	   var objectId = sensorHelper.sensorID;
                   markerName = 'InductionLoop' + objectId;
                   markerObject = new model.InductionLoop(objectId, sensorHelper.position.latitude.degree, sensorHelper.position.longitude.degree, markerName, sensorHelper.updateSteps);

                   MapService.addSensor(markerObject);
                   break;
               case ctrl.mainCtrl.sensorTypeMap[model.SensorType.SMARTMETER]:
            	   var objectId = sensorHelper.sensorID;
                   markerName = 'SmartMeter' + objectId;
                   markerObject = new model.SmartMeter(objectId, sensorHelper.position.latitude.degree, sensorHelper.position.longitude.degree, markerName, sensorHelper.updateSteps);
                   markerObject.measureRadiusInMeter = sensorHelper.measureRadiusInMeter;
                   MapService.drawRadius(markerObject);
                   
                   MapService.addSensor(markerObject);
                   break;
               case ctrl.mainCtrl.sensorTypeMap[model.SensorType.TOPORADAR]:
            	   var objectId = sensorHelper.sensorID;
                   markerName = 'TopoRadar' + objectId;
                   markerObject = new model.TopoRadar(objectId, sensorHelper.position.latitude.degree, sensorHelper.position.longitude.degree, markerName, sensorHelper.updateSteps);

                   MapService.addSensor(markerObject);
                   break;
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.ANEMOMETER]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.BAROMETER]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.HYGROMETER]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.LUXMETER]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.PYRANOMETER]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.RAIN]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.THERMOMETER]:
				case ctrl.mainCtrl.sensorTypeMap[model.SensorType.WINDFLAG]:
					console.log('its a weather station');
					var container = MapService.sensorObjects[sensorHelper.weatherStationID];
					if (typeof container === 'undefined') {
						container = new model.WeatherStation(sensorHelper.weatherStationID, 
								sensorHelper.position.latitude.degree,
								sensorHelper.position.longitude.degree, 
								'WeatherStation_' + sensorHelper.weatherStationID);
				        container.uncheckAll();
				        MapService.addSensor(container);
					}

					angular.forEach(container.subSensors, function(subSensor) {
						if(subSensor.type === sensorHelper.sensorType) {
							subSensor.check = true;
						}
					});
					break;
           }
       });
       
       /*
        * Simulation events
        */
       angular.forEach(startParameter.simulationEventLists, function(simulationEventList) {
    	   angular.forEach(simulationEventList, function(simulationEvent) {
    		   switch(simulationEvent.eventType) {
    			   /* PATH */
    			   case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CREATE_RANDOM_VEHICLES_EVENT]:
    			   	   var vehicles = {};
    			   	   var oneVehicle = undefined;
    			   	   angular.forEach(simulationEvent.vehicles, function(vehicleInformation) {
    			   		   vehicles[vehicleInformation.vehicleID] = new model.Vehicle(
    			   				   vehicleInformation.vehicleID,
    			   				   vehicleInformation.vehicleTypeEnum, 
    			   				   vehicleInformation.vehicleModelEnum, 
    			   				   vehicleInformation.name, 
    			   				   vehicleInformation.gpsActivated, 
    			   				   vehicleInformation.trip.startTime);
    			   		   if(typeof oneVehicle === 'undefined') oneVehicle = vehicles[vehicleInformation.vehicleID];
    			   	   });
    			   	   

    				   var objectIdStart = EventObjectIDService.get(); 
    			   	   var start = new model.PathStart(objectIdStart, oneVehicle.trip.startPosition.latitude.degree, oneVehicle.trip.startPosition.longitude.degree, 'PathStart' + objectIdStart); 
    			   	   start.nodeId = startNode;
    			   	   MapService.addEvent(start);

    				   var objectIdEnd = EventObjectIDService.get(); 
    			   	   var end = new model.PathEnd(objectIdEnd, oneVehicle.trip.targetPosition.latitude.degree, oneVehicle.trip.targetPosition.longitude.degree, 'PathEnd' + objectIdEnd, start); 
    			   	   MapService.addEvent(end);
                       
    			   	   var path = new model.Path(start.getPathId(), start, end, UUIDService);
    			   	   path.vehicles = vehicles;
    			   	   MapService.eventObjects[path.id] = path;
    			   	   MapService.drawDistanceLine(start, end);
    				   break;
    			   /* STREET BLOCK */
    			   case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.ROAD_BARRIER_TRAFFIC_EVENT]:
                   	   var objectId = simulationEvent.id;    
                       markerName = 'Street block' + objectId;
                       markerObject = new model.StreetBlock(objectId, roadBarrierPoint.latitude.degree, roadBarrierPoint.longitude.degree, markerName);
                       markerObject.startTimestamp = simulationEvent.roadBarrierStartTimestamp;
                       markerObject.durationMillis = simulationEvent.roadBarrierEndTimestamp - simulationEvent.roadBarrierStartTimestamp;
                       markerObject.nodeId = simulationEvent.nodeID;
                       MapService.addEvent(markerObject);
    				   break;
    			   /* ENERGY EVENT */
    			   case ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT]:
                   	   var objectId = simulationEvent.id;    
                       markerName = 'Energy event' + objectId;
                       markerObject = new model.EnergyEvent(objectId, roadBarrierPoint.latitude.degree, roadBarrierPoint.longitude.degree, markerName);
                       markerObject.startTimestamp = simulationEvent.startTimestamp;
                       markerObject.durationMillis = simulationEvent.endTimestamp - simulationEvent.startTimestamp;
                       markerObject.value = simulationEvent.percentage*100;
                       markerObject.ratio = simulationEvent.measureRadiusInMeter;
                       MapService.drawRadius(markerObject);
                       MapService.addEvent(markerObject);
    				   break;
    		   }
    	   });
       });

       /*
        * Attraction
        */
       angular.forEach(startParameter.attractionCollection, function(attraction) {
    	   var objectId = attraction.id;    
           markerName = 'Attraction' + objectId;
           markerObject = new model.Attraction(objectId, attraction.attractionPoint.latitude.degree, attraction.attractionPoint.longitude.degree, markerName);
       });
	};

	this.$scope.isStandard = function(type, value) {
		if(typeof type === 'undefined' || typeof value === 'undefined') return;
		return (_this.$scope.startParameter.specificUpdateSteps[type].value == value);
	};

	this.$scope.makeStandard = function(type, value) {
		if(typeof type === 'undefined' || typeof value === 'undefined') return;
		_this.$scope.startParameter.specificUpdateSteps[type].value = value;
	};

	/*
	 * On web socket connection opened
	 */
    this.applyConnectionOpened = function() {
        _this.$scope.$apply(function() {
            _this.$scope.connection.connectionState = 'connected';
			ctrl.headCtrl.$scope.loadingStatus = 'loading';
        });
    };

    /*
     * On web socket connection closed
     */
    this.applyConnectionClosed = function() {
        _this.$scope.$apply(function() {
            _this.$scope.connection.connectionState = 'disconnected';
        });
    };

    /*
     * On OnConnect-Message
     */
    this.applyOnConnect = function(data) {
        if (!_this.$scope.onConnectMessageReceived) {
			ctrl.headCtrl.$scope.loadingStatus = 'notLoading';
            _this.$scope.$apply(function() {
            	// Save GTFS-Files
                $.each(data.busstopResourcesList, function(key, value) {
                    _this.$scope.busSystems.push(value);
                });

                // Save OSM Files
                $.each(data.osmResourcesList, function(key, value) {
                    _this.$scope.streetMaps.push(value);
                });
                
                // Save recent scenarios
                _this.$scope.recentScenarios = data.savedStartParameterList;

                // Save communication maps
                _this.sensorTypeMap = data.sensorTypeMap;
                _this.trafficEventTypeMap = data.trafficEventTypeMap;
                _this.weatherEventTypeMap = data.weatherEventTypeMap;
                _this.simulationEventTypeMap = data.simulationEventTypeMap;
                _this.vehicleTypeMap = data.vehicleTypeMap;
                _this.vehicleModelMap = data.vehicleModelMap;
                

                
                // Add bus routes
                angular.forEach(data.busRouteList, function(busLine) {
                    _this.$scope.allBusRoutes.push({
                    	routeId : busLine.routeId,
                    	routeShortName : busLine.routeShortName,
                    	routeLongName : busLine.routeLongName,
                        routeType : busLine.routeType,
                        used : false
                    });
                });

                // Ignore following onConnect-Messages :-)
                _this.$scope.onConnectMessageReceived = true;
            });
        }
    };

    /**
     * On SimulationRunning message
     */
    this.applySimulationRunning = function(idWrapper) {
        _this.$scope.simulation.simulationState = 'running';
        _this.$scope.navigationURL = _this.navigationURLs[1];

		$('div#div_map').css('display','none');
		$('div#icon_div').css('display','none');
		$('div#div_content').css('display','block');
	    ctrl.mainCtrl.$scope.contentURL = ctrl.mainCtrl.runtimeContentURLs[1];
	    MapService.switchToRuntimeState();
	    
	    angular.forEach(idWrapper.integerIDList, function(id) {
			SensorObjectIDService.add(id);
		});
		angular.forEach(idWrapper.uuidList, function(uuid) {
			UUIDService.add(uuid);
		});

        PopupService.openMessageDialog('Simulation started');

        _this.$scope.ui.state = 'runtime';
    };

    /**
     * On SimulationUpdate message
     */
    this.applySimulationUpdate = function(data) {
    	_this.$scope.$apply(function() {
    		_this.$scope.simulation.simulationTime = data;
    	});
    };
    
    /**
     * On SimulationStopped message
     */
    this.applySimulationStopped = function() {
        _this.$scope.$apply(function() {
            _this.$scope.simulation.simulationState = 'stopped';
        });
    };

    /**
     * ON Access denied
     */
    this.applyAccessDenied = function(data) {
        _this.$scope.$apply(function() {
        	_this.$scope.accessDenied = true;
            alert('access denied');
        });
    };
    
    /**
     * Send a message
     */
    this.sendMessage = function(message, callback) {
    	_this.$scope.sentMessages[new Date().getTime()] = message;
    	ServletCommunicationService.send(message, callback);
    };
    
    this.$scope.count = count;
    
    /**
     * Change Content Size on window resize event
     */
    $(window).resize(calculateAreaSize);
    function calculateAreaSize() {
        // Calculate appropriate height
        var height = $(window).height();
        var width = $(window).width();

        var topHeight = $('div#div_top').css('height');
        var resultHeight = parseInt(height) - parseInt(topHeight) - 20;
        $('div#div_navigation').css('height', resultHeight);
        $('div#div_content').css('height', resultHeight);
        $('div#div_map').css('height', resultHeight);
        $('div#map').css('height', resultHeight);

        var navWidth = $('div#div_navigation').css('width');
        var resultWidth = parseInt(width) - parseInt(navWidth) - 20;
        $('div#div_map').css('width', resultWidth);
        $('div#map').css('width', resultWidth);
    }
    // .. and initially.. :) just in case
    calculateAreaSize();
    
    /**
     * Testing function
     */
    this.$scope.keypressMockEnabled = false;
	
    this.$scope.test = function(event) {
        if (event) {
            if (event.keyCode === 114) {
                //R
                MapService.setReady();
                if ( typeof _this.$scope.contentURL === 'undefined') {
                    MapService.init();
                }
                _this.$scope.osm.osmParsedState = 'done';
            } else if (event.keyCode === 101) {
                //E
                var data = {
                    "osmResourcesList" : ["oldenburg_pg.osm"],
                    "busstopResourcesList" : ["oldenburg_busstops.txt"],
                    "sensorTypeMap" : {
                        "0" : "ANEMOMETER",
                        "1" : "BAROMETER",
                        "2" : "GPS_BIKE",
                        "3" : "GPS_BUS",
                        "4" : "GPS_CAR",
                        "5" : "HYGROMETER",
                        "6" : "INDUCTIONLOOP",
                        "7" : "INFRARED",
                        "8" : "LUXMETER",
                        "9" : "PHOTOVOLTAIK",
                        "10" : "PYRANOMETER",
                        "11" : "RAIN",
                        "12" : "SMARTMETER",
                        "13" : "THERMOMETER",
                        "14" : "TRAFFICLIGHT",
                        "15" : "WINDFLAG",
                        "17" : "WEATHER_STATION",
                        "16" : "WINDPOWERSENSOR",
                        "19" : "GPS_TRUCK",
                        "18" : "TOPORADAR",
                        "19" : "GPS_TRUCK",
                        "20" : "GPS_MOTORCYCLE"
                    },
                    "trafficEventTypeMap" : {
                        "0" : "PATHSTART",
                        "1" : "PATHEND",
                        "2" : "ATTRACTION",
                        "3" : "STREET_BLOCK"
                    }, 
                    "weatherEventTypeMap" : {
                        "0" : "CITYCLIMATE",
                        "1" : "REFERENCECITY",
                        "2" : "COLDDAY",
                        "3" : "HOTDAY",
                        "4" : "RAINDAY",
                        "5" : "STORMDAY"
                    },
                    "simulationEventTypeMap" : {
                        //TODO
                    },
                    "vehicleTypeMap" : {
                        "0" : "CAR",
                        "1" : "MOTORCYCLE",
                        "2" : "BIKE",
                        "3" : "TRUCK",
                        "4" : "BUS"
                    },
                    "vehicleModelMap" : {
                        "4" : "BIKE_RANDOM",
                        "10" : "BUS_RANDOM",
                        "19" : "CAR_RANDOM",
                        "21" : "MOTORCYCLE_RANDOM",
                        "27" : "TRUCK_RANDOM"
                    },
                    "savedStartParameterList" : [{
                        "timestamp" : 1354625123000,
                        "name" : "export1354625122917",
                        "path" : "export1354625122917.xml"
                    }],
                    "busRouteList" : [
                        {
                        	routeId : '301a',
                        	routeShortName : "301",
                        	routeLongName : "Eversten",
                            routeType : 3
                        }, {
                        	routeId : '301b',
                        	routeShortName : "301",
                        	routeLongName : "Ofenerfeld",
                            routeType : 3
                        }, {
                        	routeId : '302a',
                        	routeShortName : "302",
                        	routeLongName : "Borchersweg",
                            routeType : 3
                        }]
                    };

                _this.applyOnConnect(data);
                _this.$scope.$apply(function() {
                    _this.$scope.startParameter.osmAndBusstopFileData.osmFileName = _this.$scope.streetMaps[0];
                    _this.$scope.startParameter.osmAndBusstopFileData.busStopFileName = _this.$scope.busSystems[0];
                });
                
            } else if (event.keyCode === 116) {
                // T
                _this.applySimulationRunning();
            } else if (event.keyCode === 122) {
            	_this.applySimulationUpdate(new Date().getTime());
            }
        } else {
            ConsoleService.pushLine('huu');
        }
    };
    
	this.$scope.km = function() {
		_this.$scope.keypressMockEnabled = !_this.$scope.keypressMockEnabled;
		
		if(_this.$scope.keypressMockEnabled) {
			console.log('adding');
			window.onkeypress = _this.$scope.test;
		} else {
			console.log('unadding');
			window.onkeypress = undefined;
		}
	};
}