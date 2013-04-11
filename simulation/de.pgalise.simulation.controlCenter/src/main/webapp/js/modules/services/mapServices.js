var mapServices = angular.module('mapServices', []);

mapServices.factory('OpenLayersService', function(PopupService, SensorObjectIDService, UUIDService, EventObjectIDService, MessageIDService) {
    var OpenLayersService = (function() {

        function OpenLayersService() {
            // Check if google plugin is loaded properly
            if ( typeof OpenLayers === 'undefined')
                return;

            this.fromProjection = new OpenLayers.Projection("EPSG:4326");
            this.toProjection = new OpenLayers.Projection("EPSG:900913");

            /*
             * Map is ready when OSM is parsed
             */
            this.readyFlag = false;
            this.initializedFlag = false;
            this.latestFlag = undefined;
            this.runtime = false;

            this.toolbarItems = [];
            this.features = {};
            this.sensorObjects = {};
            this.eventObjects = {};

            var height = 20;
            var width = 20;
            var fillOpacity = 1;
            var cursor = 'pointer';
            this.style_car = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_car.externalGraphic = "img/carM.png";
            this.style_car.fillOpacity = fillOpacity;
            this.style_car.graphicHeight = height;
            this.style_car.cursor = cursor;
            this.style_truck = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_truck.externalGraphic = "img/truckM.png";
            this.style_truck.fillOpacity = fillOpacity;
            this.style_truck.graphicHeight = height;
            this.style_truck.cursor = cursor;
            this.style_motorcycle = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_motorcycle.externalGraphic = "img/motorcycleM.png";
            this.style_motorcycle.fillOpacity = fillOpacity;
            this.style_motorcycle.graphicHeight = height;
            this.style_motorcycle.cursor = cursor;
            this.style_bus = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_bus.externalGraphic = "img/busM.png";
            this.style_bus.fillOpacity = fillOpacity;
            this.style_bus.graphicHeight = height;
            this.style_bus.cursor = cursor;
            this.style_bike = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_bike.externalGraphic = "img/bikeM.png";
            this.style_bike.fillOpacity = fillOpacity;
            this.style_bike.graphicHeight = height;
            this.style_bike.cursor = cursor;
            this.style_trafficlight = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_trafficlight.externalGraphic = "img/traffic_lightM.png";
            this.style_trafficlight.fillOpacity = fillOpacity;
            this.style_trafficlight.graphicHeight = height;
            this.style_trafficlight.cursor = cursor;
            this.style_inductionloop = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_inductionloop.externalGraphic = "img/induction_loopM.png";
            this.style_inductionloop.fillOpacity = fillOpacity;
            this.style_inductionloop.graphicHeight = height;
            this.style_inductionloop.cursor = cursor;
            this.style_toporadar = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_toporadar.externalGraphic = "img/toporadarM.png";
            this.style_toporadar.fillOpacity = fillOpacity;
            this.style_toporadar.graphicHeight = height;
            this.style_toporadar.cursor = cursor;
            this.style_photovoltaik = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_photovoltaik.externalGraphic = "img/photovoltaikM.png";
            this.style_photovoltaik.fillOpacity = fillOpacity;
            this.style_photovoltaik.graphicHeight = height;
            this.style_photovoltaik.cursor = cursor;
            this.style_windpowerplant = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_windpowerplant.externalGraphic = "img/wind_power_plantM.png";
            this.style_windpowerplant.fillOpacity = fillOpacity;
            this.style_windpowerplant.graphicWidth = width + 5;
            this.style_windpowerplant.cursor = cursor;
            this.style_smartmeter = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_smartmeter.externalGraphic = "img/smart_meterM.png";
            this.style_smartmeter.fillOpacity = fillOpacity;
            this.style_smartmeter.graphicHeight = height;
            this.style_smartmeter.cursor = cursor;
            this.style_weatherstation = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_weatherstation.externalGraphic = "img/weather_stationM.png";
            this.style_weatherstation.fillOpacity = fillOpacity;
            this.style_weatherstation.graphicWidth = width + 5;
            this.style_weatherstation.cursor = cursor;
            this.style_toporadar = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_toporadar.externalGraphic = "img/toporadarM.png";
            this.style_toporadar.fillOpacity = fillOpacity;
            this.style_toporadar.graphicHeight = height;
            this.style_toporadar.cursor = cursor;

            this.carsLayer = new OpenLayers.Layer.Vector('Cars Layer');
            this.trucksLayer = new OpenLayers.Layer.Vector('Trucks Layer');
            this.motorcyclesLayer = new OpenLayers.Layer.Vector('Motorcycles Layer');
            this.busLayer = new OpenLayers.Layer.Vector('Bus Layer');
            this.bikesLayer = new OpenLayers.Layer.Vector('Bikes Layer');
            this.trafficlightLayer = new OpenLayers.Layer.Vector('Traffic Light Layer');
            this.inductionloopLayer = new OpenLayers.Layer.Vector('Induction Loop Layer');
            this.photovoltaikLayer = new OpenLayers.Layer.Vector('Photovoltaik Layer');
            this.windpowerplantLayer = new OpenLayers.Layer.Vector('Wind Power Plant Layer');
            this.smartmeterLayer = new OpenLayers.Layer.Vector('Smart Meter Layer');
            this.weatherstationLayer = new OpenLayers.Layer.Vector('Weather Station Layer');
            this.toporadarLayer = new OpenLayers.Layer.Vector('Topo Radar Layer');
            this.radiusLayer = new OpenLayers.Layer.Vector('Smart Meter Radius Layer');

            this.miscLayer = new OpenLayers.Layer.Vector('Miscellaneous Object Layer');
            
            this.style_startflag = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_startflag.externalGraphic = "img/flag_green2.png";
            this.style_startflag.fillOpacity = fillOpacity;
            this.style_startflag.graphicHeight = height;
            this.style_startflag.cursor = cursor;
            this.style_startflag.graphicXOffset = -5;
            this.style_startflag.graphicYOffset = -19;
            this.style_endflag = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_endflag.externalGraphic = "img/flag_red2.png";
            this.style_endflag.fillOpacity = fillOpacity;
            this.style_endflag.graphicHeight = height;
            this.style_endflag.cursor = cursor;
            this.style_endflag.graphicXOffset = -5;
            this.style_endflag.graphicYOffset = -19;
            this.style_attraction = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_attraction.externalGraphic = "img/attractionM.png";
            this.style_attraction.fillOpacity = fillOpacity;
            this.style_attraction.graphicHeight = height;
            this.style_attraction.cursor = cursor;
            this.style_streetblock = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_streetblock.externalGraphic = "img/street_blockM.png";
            this.style_streetblock.fillOpacity = fillOpacity;
            this.style_streetblock.graphicHeight = height;
            this.style_streetblock.cursor = cursor;
            this.style_energyevent = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
            this.style_energyevent.externalGraphic = "img/energyeventM.png";
            this.style_energyevent.fillOpacity = fillOpacity;
            this.style_energyevent.graphicHeight = height;
            this.style_energyevent.cursor = cursor;

            this.pathflagLayer = new OpenLayers.Layer.Vector('Path Flag Layer');
            this.attractionLayer = new OpenLayers.Layer.Vector('Attraction Layer');
            this.streetblockLayer = new OpenLayers.Layer.Vector('Street Block Layer');
            this.energyEventLayer = new OpenLayers.Layer.Vector('Energy Event Layer');

            this.layerMap = {};
            this.layerMap[model.SensorType.GPS_CAR] = {
                layer : this.carsLayer,
                style : this.style_car
            };
            this.layerMap[model.SensorType.GPS_TRUCK] = {
                layer : this.trucksLayer,
                style : this.style_truck
            };
            this.layerMap[model.SensorType.GPS_MOTORCYCLE] = {
                layer : this.motorcyclesLayer,
                style : this.style_motorcycle
            };
            this.layerMap[model.SensorType.GPS_BUS] = {
                layer : this.busLayer,
                style : this.style_bus
            };
            this.layerMap[model.SensorType.GPS_BIKE] = {
                layer : this.bikesLayer,
                style : this.style_bike
            };
            this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = {
                layer : this.trafficlightLayer,
                style : this.style_trafficlight
            };
            this.layerMap[model.SensorType.INDUCTIONLOOP] = {
                layer : this.inductionloopLayer,
                style : this.style_inductionloop
            };
            this.layerMap[model.SensorType.TOPORADAR] = {
                layer : this.toporadarLayer,
                style : this.style_toporadar
            };
            this.layerMap[model.SensorType.PHOTOVOLTAIK] = {
                layer : this.photovoltaikLayer,
                style : this.style_photovoltaik
            };
            this.layerMap[model.SensorType.WINDPOWERSENSOR] = {
                layer : this.windpowerplantLayer,
                style : this.style_windpowerplant
            };
            this.layerMap[model.SensorType.SMARTMETER] = {
                layer : this.smartmeterLayer,
                style : this.style_smartmeter
            };
            this.layerMap[model.SensorType.WEATHER_STATION] = {
                layer : this.weatherstationLayer,
                style : this.style_weatherstation
            };
            this.layerMap[model.EventType.PATHSTART] = {
                layer : this.pathflagLayer,
                style : this.style_startflag
            };
            this.layerMap[model.EventType.PATHEND] = {
                layer : this.pathflagLayer,
                style : this.style_endflag
            };
            this.layerMap[model.EventType.ATTRACTION] = {
                layer : this.attractionLayer,
                style : this.style_attraction
            };
            this.layerMap[model.EventType.STREETBLOCK] = {
                layer : this.streetblockLayer,
                style : this.style_streetblock
            };
            this.layerMap[model.EventType.ENERGYEVENT] = {
                layer : this.energyEventLayer,
                style : this.style_energyevent
            };

            /*
             * The acutal map
             */
            this.map = undefined;

            /*
             * The allowed region which the whole map must be within
             */
            this.allowedBounds = undefined;
        }

		/**
		 * Initialize the map 
		 */
        OpenLayersService.prototype.init = function() {
            if (!this.readyFlag) {
                console.log('Attempted to intialize map without beeing ready!');
                return;
            } else if (this.initializedFlag) {
                console.log('Already initialized!');
                return;
            }
            var _this = this;

            var bounds = undefined;
			if (typeof this.allowedBounds !== 'undefined') {
				var northEast = new OpenLayers.LonLat(
						this.allowedBounds.northEast.longitude.degree,
						this.allowedBounds.northEast.latitude.degree);
				var southWest = new OpenLayers.LonLat(
						this.allowedBounds.southWest.longitude.degree,
						this.allowedBounds.southWest.latitude.degree);
				bounds = new OpenLayers.Bounds();
				bounds.extend(northEast);
				bounds.extend(southWest);
			} else {
				console.log('Boundary was undefined');
				var northEast = new OpenLayers.LonLat(8.313,
						53.195);
				var southWest = new OpenLayers.LonLat(8.129,
						53.089);
				bounds = new OpenLayers.Bounds();
				bounds.extend(northEast);
				bounds.extend(southWest);
			}

			$('div#map').empty();
			this.map = new OpenLayers.Map("map", {
				restrictedExtent : bounds.transform(
						this.fromProjection, this.toProjection)
			});
            this.toolbarItems = [];
            angular.forEach(ctrl.mainCtrl.$scope.mapIcons, function(icon) {
                _this.toolbarItems.push(new OpenLayers.Control.Button({
                    autoActivate : false,
                    displayClass : icon.cssClass,
                    eventListeners : {
                        'activate' : function() {
                            //Deactivate others
                            angular.forEach(_this.toolbarItems, function(toolbarItem) {
                                if (icon.cssClass === toolbarItem.displayClass)
                                    return;
                                toolbarItem.deactivate();
                            });
                            icon.onActivate();
                        },
                        'deactivate' : icon.onDeactivate
                    },
                    type : OpenLayers.Control.TYPE_TOGGLE,
                    title : 'Define ' + icon.name
                }));
            });
            var toolbar = new OpenLayers.Control.Panel();
            toolbar.addControls(_this.toolbarItems);
            this.map.addControl(toolbar);

            OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {
                defaultHandlerOptions : {
                    'single' : true,
                    'double' : false,
                    'pixelTolerance' : 0,
                    'stopSingle' : false,
                    'stopDouble' : false
                },

                initialize : function(options) {
                    this.handlerOptions = OpenLayers.Util.extend({}, this.defaultHandlerOptions);
                    OpenLayers.Control.prototype.initialize.apply(this, arguments);
                    this.handler = new OpenLayers.Handler.Click(this, {
                        'click' : this.trigger
                    }, this.handlerOptions);
                },

                trigger : function(e) {
                    _this.mapClick(_this.map.getLonLatFromPixel(e.xy).transform(_this.toProjection, _this.fromProjection));
                }
            });
            var click = new OpenLayers.Control.Click();
            this.map.addControl(click);
            click.activate();

            var osmLayer = new OpenLayers.Layer.OSM("OpenStreetMap");
            this.map.addLayer(osmLayer);

            // Add layers without click listener ...
            this.map.addLayer(this.miscLayer);
            
            var allLayers = [];
            for (var k in this.layerMap) {
            	if(k != model.EventType.PATHEND) {
                	console.log(k);
            		// leave out pathEnd... PathStart has select feature already :-)
            		allLayers.push(this.layerMap[k].layer);
            	}
                
            	this.map.addLayer(this.layerMap[k].layer);
            }
            
            angular.forEach(allLayers, function(layer) {
            	layer.events.on({
                    "featureselected" : function(e) {
                        if (_this.sensorObjects.hasOwnProperty(e.feature.attributes.objectId)) {
                            var sensorObject = _this.sensorObjects[e.feature.attributes.objectId];
                            PopupService.openSensorDialog(sensorObject);
                        } else if(_this.eventObjects.hasOwnProperty(e.feature.attributes.objectId)) {
                        	var eventObject = _this.eventObjects[e.feature.attributes.objectId];
                            if(eventObject.type === model.EventType.PATHSTART || eventObject.type === model.EventType.PATHEND) {
                                PopupService.openEventDialog(_this.eventObjects[eventObject.getPathId()]);
                            } else {
                                PopupService.openEventDialog(eventObject);
                            }
                        }
                    }
                });
            });

            this.selectControl = new OpenLayers.Control.SelectFeature(allLayers, {
                clickout : true,
                toggle : false,
                multiple : true,
                hover : false
            });
            this.map.addControl(this.selectControl);
            this.selectControl.activate();

            this.map.addControl(new OpenLayers.Control.LayerSwitcher());

            this.map.setCenter(new OpenLayers.LonLat(8.211491, 53.144711).transform(this.fromProjection, this.toProjection), 11);
            this.initializedFlag = true;
            
            if(this.runtime) {
            	this.switchToRuntimeState();
            }
        };

		/**
		 * Behavior when clicking the map (set a sensor/event)  
		 */
        OpenLayersService.prototype.mapClick = function(location) {
            if ( typeof ctrl.mainCtrl === 'undefined')
                throw "OpenLayersService.addMarker(location): location is undefined";

            var _this = this;

            // Get marker type
            var selection = ctrl.mainCtrl.$scope.chosenType;
            if ( typeof selection !== 'undefined') {
                var markerObject, onStreet, onJunction;
                var isSensor;

                switch(selection) {
                    // Marker is traffic light
                    case model.SensorType.TRAFFIC_LIGHT_INTERSECTION:
                    	var objectId = SensorObjectIDService.get();
                        markerName = 'TrafficLight' + objectId;
                        markerObject = new model.TrafficLightIntersection(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].value, [SensorObjectIDService.get(), SensorObjectIDService.get()]);
                        onStreet = true;
                        onJunction = true;
                        isSensor = true;
                        break;
                    case model.SensorType.WINDPOWERSENSOR:
                    	var objectId = SensorObjectIDService.get();
                        markerName = 'WindPowerSensor' + objectId;
                        markerObject = new model.WindPowerSensor(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.WINDPOWERSENSOR].value);
                        onStreet = false;
                        onJunction = false;
                        isSensor = true;
                        break;
                    case model.SensorType.PHOTOVOLTAIK:
                    	var objectId = SensorObjectIDService.get();
                        markerName = 'Photovoltaik' + objectId;
                        markerObject = new model.Photovoltaik(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.PHOTOVOLTAIK].value);
                        onStreet = false;
                        onJunction = false;
                        isSensor = true;
                        break;
                    case model.SensorType.INDUCTIONLOOP:
                    	var objectId = SensorObjectIDService.get();
                        markerName = 'InductionLoop' + objectId;
                        markerObject = new model.InductionLoop(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.INDUCTIONLOOP].value);
                        onStreet = true;
                        onJunction = false;
                        isSensor = true;
                        break;
                    case model.SensorType.WEATHER_STATION:
                    	var objectId = SensorObjectIDService.get();
                        markerName = 'WeatherStation' + objectId;
                        markerObject = new model.WeatherStation(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.WEATHER_STATION].value);
                        onStreet = false;
                        onJunction = false;
                        isSensor = true;
                        break;
                    case model.SensorType.SMARTMETER:
                    	var objectId = SensorObjectIDService.get();
                        markerName = 'SmartMeter' + objectId;
                        markerObject = new model.SmartMeter(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.SMARTMETER].value);
                        onStreet = false;
                        onJunction = false;
                        isSensor = true;
                        this.drawRadius(markerObject);
                        break;
                    case model.SensorType.TOPORADAR:
                    	var objectId = SensorObjectIDService.get();    
                        markerName = 'TopoRadar' + objectId;
                        markerObject = new model.TopoRadar(objectId, location.lat, location.lon, markerName, ctrl.mainCtrl.$scope.startParameter.specificUpdateSteps[model.SensorType.TOPORADAR].value);
                        onStreet = true;
                        onJunction = false;
                        isSensor = true;
                        break;
                    case model.EventType.PATHSTART:  
                    	var objectId = EventObjectIDService.get();                  
                        if (typeof this.latestFlag === 'undefined') {
                            markerObject = new model.PathStart(objectId, location.lat, location.lon, 'PathStart' + objectId);
                            $('div#icon_div').css('background-image', 'url(img/flag_red2.png)');
                            this.latestFlag = markerObject;
                        } else {
                            markerObject = new model.PathEnd(objectId, location.lat, location.lon, 'PathEnd' + objectId, this.latestFlag);
                            $('div#icon_div').css('background-image', 'url(img/flag_green2.png)');
                            // Add path
                            var path = new model.Path(markerObject.getPathId(), markerObject.partner, markerObject, UUIDService);
                            this.eventObjects[path.id] = path;
                            
                            this.drawDistanceLine(markerObject, markerObject.partner);
                            this.latestFlag = undefined;
                        }
                        onStreet = true;
                        onJunction = false;
                        isSensor = false;
                        break;
                    case model.EventType.ATTRACTION:
                    	var objectId = SensorObjectIDService.get();    
                        markerName = 'Attraction' + objectId;
                        markerObject = new model.Attraction(objectId, location.lat, location.lon, markerName);
                        onStreet = true;
                        onJunction = false;
                        isSensor = false;
                        break;
                    case model.EventType.STREETBLOCK:
                    	var objectId = UUIDService.get();    
                        markerName = 'Street block' + objectId;
                        markerObject = new model.StreetBlock(objectId, location.lat, location.lon, markerName);
                        onStreet = true;
                        onJunction = false;
                        isSensor = false;
                        break;
                    case model.EventType.ENERGYEVENT:
                    	var objectId = UUIDService.get();    
                        markerName = 'Energy event' + objectId;
                        markerObject = new model.EnergyEvent(objectId, location.lat, location.lon, markerName);
                        onStreet = false;
                        onJunction = false;
                        isSensor = false;
                        this.drawRadius(markerObject);
                        break;
                }
                
                if (isSensor) {
                    this.addSensor(markerObject);
                } else {
                    this.addEvent(markerObject);
                }
                
                if(this.runtime) {
                	switch(markerObject.type) {
                		case model.EventType.ATTRACTION:
                			ctrl.mainCtrl.$scope.unsentMessages.push(
                				new model.CreateAttractionEventsMessage(
                					MessageIDService.get(), [
                					    new model.AttractionData(markerObject, SensorObjectIDService.takenIds, UUIDService.takenUUIDs)]));
                			break;
                		case model.EventType.PATH:
                			ctrl.mainCtrl.$scope.uncommittedEvents.push(
                				new model.CreateVehiclesEvent(UUIDService.get(), markerObject));
                			break;
                		case model.EventType.STREETBLOCK:
                			ctrl.mainCtrl.$scope.uncommittedEvents.push(
                				new model.RoadBarrierTrafficEvent(markerObject));
                			break;
                		case model.EventType.ENERGYEVENT:
                			ctrl.mainCtrl.$scope.uncommittedEvents.push(
                				new model.PercentageChangeEnergyEvent(markerObject));
                			break;
                	}
                }
                
                var messageID = MessageIDService.get();
                ctrl.mainCtrl.sendMessage(
                	new model.AskForValidNodeMessage(
                		messageID, 
                		new model.Node(
                			markerObject.nodeId, 
                			onStreet, 
                			onJunction, 
                			new model.GeoLocation(
                				markerObject.latitude, 
                				markerObject.longitude)
                			)
                		), 
                	/**
                	 * Callback is Valid Node Message
                	 * Content of message is Node
                	 * node : {
                	 *     id : string
                	 *     onStreet : boolean
                	 *     onJunction : boolean
                	 *     position : {
                	 *         latitude : {
                	 *             degree : float
                	 *         },
                	 *         longitude : {
                	 *             degree : float
                	 *         }
                	 *     }
                	 * }
                	 */
                	function(callbackMessage) {
	                    if (callbackMessage.messageType === model.MessageType.VALID_NODE) {
	                    	var node = callbackMessage.content;
	                    	if(isSensor) {
		                        _this.sensorObjects[markerObject.id].setPosition(node.position.latitude.degree, node.position.longitude.degree);
		                        _this.sensorObjects[markerObject.id].nodeId = node.id;

		                        if(ctrl.mainCtrl.$scope.settings.autoOpenDialog) {
		                        	PopupService.openSensorDialog(markerObject);
	                            }
		                    } else {
		                        _this.eventObjects[markerObject.id].setPosition(node.position.latitude.degree, node.position.longitude.degree);
		                        _this.eventObjects[markerObject.id].nodeId = node.id;

		                        if(ctrl.mainCtrl.$scope.settings.autoOpenDialog) {
			                        if(markerObject.type === model.EventType.PATHSTART) {
			                        	// do nothing
			                        } else if(markerObject.type === model.EventType.PATHEND) {
			                        	// open for path
			                        	PopupService.openEventDialog(_this.eventObjects[markerObject.getPathId()]);
			                        } else {
			                        	// open event
			                        	PopupService.openEventDialog(markerObject);
			                        }
		                        }
		                    }
	                    	
	                    	_this.updateMarker(markerObject);
	                    }
                });
            }
        };

        OpenLayersService.prototype.unselect = function(objectId) {
        	if(!this.features.hasOwnProperty(objectId)) return;
            this.selectControl.unselect(this.features[objectId]);
        };

        OpenLayersService.prototype.addSensor = function(sensor) {
            this.sensorObjects[sensor.id] = sensor;
            this.addMarker(sensor);
        };

        OpenLayersService.prototype.removeSensor = function(id) {
        	if(this.runtime) {
        		PopupService.openMessageDialog('Sensors cannot be deleted during runtime.');
        		return;
        	}
        	
        	if(this.sensorObjects[id].type === model.SensorType.SMARTMETER) {
                this.removeMisc(this.sensorObjects[id].getCircleId());
        	}
        		
        	
            this.removeMarker(this.sensorObjects[id]);
            delete this.sensorObjects[id];
        };

        OpenLayersService.prototype.addEvent = function(eventObject) {
            this.eventObjects[eventObject.id] = eventObject;
            this.addMarker(eventObject);
        };

        OpenLayersService.prototype.removeEventObject = function(id) {
        	if(!this.eventObjects.hasOwnProperty(id)) {
        		console.log('not found');
        		return;
        	}
        	
            switch(this.eventObjects[id].type) {
                case model.EventType.PATHEND:
                    var start = this.eventObjects[id].partner;
                    var end = this.eventObjects[id];
                    // remove flags
                    this.removeMarker(start);
                    this.removeMarker(end);
                    this.removeMisc(start.getPathId());
                    // delete objects
                    delete this.eventObjects[start.getPathId()];
                    delete this.eventObjects[start.id];
                    delete this.eventObjects[end.id];
                    break;
                case model.EventType.PATHSTART:
                case model.EventType.ATTRACTION:
                case model.EventType.STREETBLOCK:
                    this.removeMarker(this.eventObjects[id]);
                    delete this.eventObjects[this.eventObjects[id].id];
                    break;
                case model.EventType.ENERGYEVENT:
                    this.removeMisc(this.eventObjects[id].getCircleId());
                    
                    this.removeMarker(this.eventObjects[id]);
                    delete this.eventObjects[this.eventObjects[id].id];

            }
        };

        OpenLayersService.prototype.addMarker = function(object) {
            if ( typeof object === 'undefined')
                throw "OpenLayersService.addMarker(object): object is undefined!";
            
            var feature = new OpenLayers.Feature.Vector(
            	new OpenLayers.Geometry.Point(
            		object.longitude, 
            		object.latitude)
            			.transform(this.fromProjection, this.toProjection), 
            		{
		                objectId : object.id
		            }, 
		            this.layerMap[object.type].style);
            this.layerMap[object.type].layer.addFeatures([feature]);
            this.features[object.id] = feature;
        };

		OpenLayersService.prototype.updateMarker = function(sensor) {
			if (this.features[sensor.id].getVisibility()) {
				this.features[sensor.id]
					.move(new OpenLayers.LonLat(
						sensor.longitude, 
						sensor.latitude).transform(
							this.fromProjection,
							this.toProjection));
			}
			
			if(sensor.type === model.SensorType.SMARTMETER) {
				this.features[sensor.getCircleId()]
					.move(new OpenLayers.LonLat(
						sensor.longitude, 
						sensor.latitude).transform(
							this.fromProjection,
							this.toProjection));
			} else if(sensor.type === model.EventType.ENERGYEVENT) {
				this.features[sensor.getCircleId()]
				.move(new OpenLayers.LonLat(
					sensor.longitude, 
					sensor.latitude).transform(
						this.fromProjection,
						this.toProjection));
			} else if(sensor.type === model.EventType.PATHEND) {
				this.removeMisc(sensor.getPathId());
	            
	            this.drawDistanceLine(this.eventObjects[sensor.getPathId()].start,this.eventObjects[sensor.getPathId()].end);
			} 
		};

        OpenLayersService.prototype.removeMarker = function(object) {
            if ( typeof this.features[object.id] === 'undefined')
                throw "OpenLayersService.removeMarker(object): object is invalid!";
            this.layerMap[object.type].layer.removeFeatures([this.features[object.id]]);
            delete this.features[object.id];
        };
        
        OpenLayersService.prototype.removeMisc = function(featureId) {
        	this.miscLayer.removeFeatures([this.features[featureId]]);
        	delete this.features[featureId];
        };
        
        OpenLayersService.prototype.drawDistanceLine = function(start, end) {
            var start_point = new OpenLayers.Geometry.Point(start.longitude, start.latitude);
            var end_point = new OpenLayers.Geometry.Point(end.longitude, end.latitude);
            var lineFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString([start_point, end_point]).transform(this.fromProjection, this.toProjection));
            
            if(this.features.hasOwnProperty(start.getPathId())) {
            	// line exists already
            	this.miscLayer.removeFeatures([this.features[start.getPathId()]]);
            }
            this.miscLayer.addFeatures([lineFeature]);

            this.features[start.getPathId()] = lineFeature;
        };

        OpenLayersService.prototype.drawRadius = function(sensor) {
        	if(sensor.type === model.SensorType.SMARTMETER ) {
        		var middleLonLat = new OpenLayers.LonLat(sensor.longitude, sensor.latitude).transform(this.fromProjection, this.toProjection);
                var circle = new OpenLayers.Geometry.Polygon.createRegularPolygon(new OpenLayers.Geometry.Point(middleLonLat.lon, middleLonLat.lat), sensor.measureRadiusInMeter, 20, 360);
                var radiusFeature = new OpenLayers.Feature.Vector(circle);
                
                if(this.features.hasOwnProperty(sensor.getCircleId())) {
                	this.miscLayer.removeFeatures([this.features[sensor.getCircleId()]]);
                }
                this.miscLayer.addFeatures([radiusFeature]);

                this.features[sensor.getCircleId()] = radiusFeature;
        	} else if (sensor.type === model.EventType.ENERGYEVENT){
        		var middleLonLat = new OpenLayers.LonLat(sensor.longitude, sensor.latitude).transform(this.fromProjection, this.toProjection);
                var circle = new OpenLayers.Geometry.Polygon.createRegularPolygon(new OpenLayers.Geometry.Point(middleLonLat.lon, middleLonLat.lat), sensor.ratio, 20, 360);
                var radiusFeature = new OpenLayers.Feature.Vector(circle);
                
                if(this.features.hasOwnProperty(sensor.getCircleId())) {
                	this.miscLayer.removeFeatures([this.features[sensor.getCircleId()]]);
                }
                this.miscLayer.addFeatures([radiusFeature]);

                this.features[sensor.getCircleId()] = radiusFeature;
        	}
        };
        
        OpenLayersService.prototype.setReady = function(bounds) {
            this.readyFlag = true;
            this.allowedBounds = bounds;
        };
        
        OpenLayersService.prototype.switchToInitState = function() {
        	this.runtime = false;
        	$('.olControlTrafficLightItemInactive').css('display','');
        	$('.olControlTrafficLightItemActive').css('display','');
        	$('.olControlWindPowerSensorItemInactive').css('display','');
        	$('.olControlWindPowerSensorItemActive').css('display','');
        	$('.olControlPhotovoltaikItemInactive').css('display','');
        	$('.olControlPhotovoltaikItemActive').css('display','');
        	$('.olControlInductionLoopItemInactive').css('display','');
        	$('.olControlInductionLoopItemActive').css('display','');
        	$('.olControlWeatherStationItemInactive').css('display','');
        	$('.olControlWeatherStationItemActive').css('display','');
        	$('.olControlSmartMeterItemInactive').css('display','');
        	$('.olControlSmartMeterItemActive').css('display','');
        	$('.olControlTopoRadarItemInactive').css('display','');
        	$('.olControlTopoRadarItemActive').css('display','');
        	$('.olControlPathItemInactive').css('margin-left', 35);
        	$('.olControlPathItemActive').css('margin-left', 35);
        	
        	angular.forEach(this.toolbarItems, function(item) {
        		item.deactivate();
        	});
        };
        
        OpenLayersService.prototype.switchToRuntimeState = function() {
        	this.runtime = true;
        	$('.olControlTrafficLightItemInactive').css('display','none');
        	$('.olControlTrafficLightItemActive').css('display','none');
        	$('.olControlWindPowerSensorItemInactive').css('display','none');
        	$('.olControlWindPowerSensorItemActive').css('display','none');
        	$('.olControlPhotovoltaikItemInactive').css('display','none');
        	$('.olControlPhotovoltaikItemActive').css('display','none');
        	$('.olControlInductionLoopItemInactive').css('display','none');
        	$('.olControlInductionLoopItemActive').css('display','none');
        	$('.olControlWeatherStationItemInactive').css('display','none');
        	$('.olControlWeatherStationItemActive').css('display','none');
        	$('.olControlSmartMeterItemInactive').css('display','none');
        	$('.olControlSmartMeterItemActive').css('display','none');
        	$('.olControlTopoRadarItemInactive').css('display','none');
        	$('.olControlTopoRadarItemActive').css('display','none');
        	$('.olControlPathItemInactive').css('margin-left', 0);
        	$('.olControlPathItemActive').css('margin-left', 0);
        	
        	angular.forEach(this.toolbarItems, function(item) {
        		item.deactivate();
        	});
        };

        return OpenLayersService;
    })();
    return new OpenLayersService();
});
