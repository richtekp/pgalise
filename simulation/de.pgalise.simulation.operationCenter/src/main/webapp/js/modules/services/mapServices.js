/*
 * Map service module
 * @author Dennis Höting
 */
var mapServices = angular.module('mapServices', []);

/*
 * OpenLayers implementation
 * @author Dennis Höting
 */
mapServices.factory('OpenLayersService',
	function() {
		var OpenLayersService = (function() {
			/*
			 * Constructor (invoked below)
			 */
			function OpenLayersService() {
				// Check if google plugin is loaded properly
				if (typeof OpenLayers === 'undefined') {
					return;
				}
				
				// if map is already initialized
				this.initFlag = false;

				/*
				 * from- and to-projections
				 * This is for translating coordinates between layers
				 */
				this.fromProjection = new OpenLayers.Projection(
						"EPSG:4326");
				this.toProjection = new OpenLayers.Projection(
						"EPSG:900913");

				// Map
				this.map = undefined;
				
				/*
				 * Current depiction strategy
				 */
				this.depictionStrategy = undefined;
				
				/*
				 * Store features
				 */
				this.features = {};
				
				/*
				 * This is for storing path-Flags that are not bound to one another!
				 */
				this.pathFeatures = [];
				
				/*
				 * Store layers in arrays
				 */
				this.vectorLayers = [];

				/*
				 * OSM map layer
				 */
				this.osm = new OpenLayers.Layer.OSM("OpenStreetMap");

				/*
				 * Styles
				 */
				this.style_car = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_truck = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_motorcycle = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_bus = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_bike = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_trafficlightIntersection = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_inductionloop = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_toporadar = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_photovoltaik = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_windpowerplant = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_smartmeter = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);
				this.style_weatherstation = OpenLayers.Util.extend({},
						OpenLayers.Feature.Vector.style['default']);

				/*
				 * Hightlight layer + styling + feature
				 */
				this.highlightLayer = new OpenLayers.Layer.Vector('Hightlight Layer');
				this.highlightStyle = OpenLayers.Util.extend({},OpenLayers.Feature.Vector.style['default']);
				this.highlightStyle.pointRadius = 15;
				this.highlightStyle.strokeWidth = 2;
				this.highlightStyle.fillOpacity = 0.2;
				this.highlightStyle.strokeColor = 'red';
				this.highlightFeature = new OpenLayers.Feature.Vector(
						new OpenLayers.Geometry.Point(0, 0), null,this.highlightStyle);
				this.highlightLayer.addFeatures([ this.highlightFeature ]);

				/*
				 * Path style
				 */
				this.pathPointStyle = OpenLayers.Util.extend({},OpenLayers.Feature.Vector.style['default']);
				this.pathPointStyle.pointRadius = 5;
				this.pathPointStyle.strokeWidth = 2;
				this.pathPointStyle.fillOpacity = 0.2;
				this.pathPointStyle.strokeColor = 'red';

				/*
				 * Layers for sensor types
				 */
				this.carsLayer = new OpenLayers.Layer.Vector(
						'Cars Layer', {
							sensorType : model.SensorType.GPS_CAR
						});
				this.trucksLayer = new OpenLayers.Layer.Vector(
						'Trucks Layer', {
							sensorType : model.SensorType.GPS_TRUCK
						});
				this.motorcyclesLayer = new OpenLayers.Layer.Vector(
						'Motorcycles Layer',
						{
							sensorType : model.SensorType.GPS_MOTORCYCLE
						});
				this.busLayer = new OpenLayers.Layer.Vector(
						'Bus Layer', {
							sensorType : model.SensorType.GPS_BUS
						});
				this.bikesLayer = new OpenLayers.Layer.Vector(
						'Bikes Layer', {
							sensorType : model.SensorType.GPS_BIKE
						});
				this.trafficlightIntersectionLayer = new OpenLayers.Layer.Vector(
						'Traffic Light Intersection Layer',
						{
							sensorType : model.SensorType.TRAFFIC_LIGHT_INTERSECTION
						});
				this.inductionloopLayer = new OpenLayers.Layer.Vector(
						'Induction Loop Layer',
						{
							sensorType : model.SensorType.INDUCTIONLOOP
						});
				this.toporadarLayer = new OpenLayers.Layer.Vector(
						'Topo Radar Layer', {
							sensorType : model.SensorType.TOPORADAR
						});
				this.photovoltaikLayer = new OpenLayers.Layer.Vector(
						'Photovoltaik Layer',
						{
							sensorType : model.SensorType.PHOTOVOLTAIK
						});
				this.windpowerplantLayer = new OpenLayers.Layer.Vector(
						'Wind Power Plant Layer',
						{
							sensorType : model.SensorType.WINDPOWERSENSOR
						});
				this.smartmeterLayer = new OpenLayers.Layer.Vector(
						'Smart Meter Layer',
						{
							sensorType : model.SensorType.SMARTMETER
						});
				this.weatherstationLayer = new OpenLayers.Layer.Vector(
						'Weather Station Layer',
						{
							sensorType : model.SensorType.WEATHER_STATION
						});

				/*
				 * Favorite-Layers for GPS, Traffic, Energy and Weather
				 */
				this.favoriteGPSLayer = new OpenLayers.Layer.Vector(
						'Favorite GPS', {
							sensorType : 'favGps'
						});
				this.favoriteTrafficLayer = new OpenLayers.Layer.Vector(
						'Favorite Traffic', {
							sensorType : 'favTraffic'
						});
				this.favoriteEnergyLayer = new OpenLayers.Layer.Vector(
						'Favorite Energy', {
							sensorType : 'favEnergy'
						});
				this.favoriteWeatherLayer = new OpenLayers.Layer.Vector(
						'Favorite Weather', {
							sensorType : 'favWeather'
						});

				/*
				 * Heat map layers
				 */
				this.gpsHeatMap = undefined;
				this.speedHeatMap = undefined;
				this.energyProductionHeatMap = undefined;
				this.energyConsumptionHeatMap = undefined;
				this.testServerHeatmap = undefined;

				/*
				 * Layer map
				 * 
				 * This is for mapping a sensortype to approrpiate layer and styling
				 */
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
				this.layerMap[model.SensorType.ANEMOMETER] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.BAROMETER] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.HYGROMETER] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.INFRARED] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.LUXMETER] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.PYRANOMETER] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.RAIN] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.THERMOMETER] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.WINDFLAG] = this.layerMap[model.SensorType.WEATHER_STATION];
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = {
					layer : this.trafficlightIntersectionLayer,
					style : this.style_trafficlightIntersection
				};
				this.layerMap[model.SensorType.TRAFFICLIGHT] = this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION];

				/*
				 * Add layers to vector layers array
				 */
				this.vectorLayers.push(this.carsLayer);
				this.vectorLayers.push(this.trucksLayer);
				this.vectorLayers.push(this.motorcyclesLayer);
				this.vectorLayers.push(this.busLayer);
				this.vectorLayers.push(this.bikesLayer);
				this.vectorLayers.push(this.trafficlightIntersectionLayer);
				this.vectorLayers.push(this.inductionloopLayer);
				this.vectorLayers.push(this.toporadarLayer);
				this.vectorLayers.push(this.photovoltaikLayer);
				this.vectorLayers.push(this.windpowerplantLayer);
				this.vectorLayers.push(this.smartmeterLayer);
				this.vectorLayers.push(this.weatherstationLayer);
				this.vectorLayers.push(this.favoriteGPSLayer);
				this.vectorLayers.push(this.favoriteTrafficLayer);
				this.vectorLayers.push(this.favoriteEnergyLayer);
				this.vectorLayers.push(this.favoriteWeatherLayer);
				
				/*
				 * Add select-feature to vectorlayers 
				 * 
				 * Like this, a click on a feature will result in adding the appropriate sensor to details view 
				 * and switching to details view
				 */ 
				angular.forEach(
					this.vectorLayers,
					function(layer) {
						layer.events.on({
							"featureselected" : function(e) {
								ctrl.detailsCtrl.setSensorInDetail(e.feature.attributes.referenceId);
								ctrl.tabCtrl.switchTo('details');
							}
						});
					});
			}

			/*
			 * Initialization of map components
			 * @param boundary Boundary of the map
			 */
			OpenLayersService.prototype.init = function(boundary) {
				/*
				 * Only if map is not initialized yet
				 */
				if (this.initFlag) {
					console.log('already initialized');
					return;
				}
				
				// Self reference
				var _this = this;

				/*
				 * Process boundary
				 */
				var bounds = undefined;
				if (typeof boundary !== 'undefined') {
					// Boundary defined
					var northEast = new OpenLayers.LonLat(
							boundary.northEast.longitude.degree,
							boundary.northEast.latitude.degree);
					var southWest = new OpenLayers.LonLat(
							boundary.southWest.longitude.degree,
							boundary.southWest.latitude.degree);
					bounds = new OpenLayers.Bounds();
					bounds.extend(northEast);
					bounds.extend(southWest);
				} else {
					// standard-boundary ( ... is always Oldenburg ;-D)
					var northEast = new OpenLayers.LonLat(8.313,
							53.195);
					var southWest = new OpenLayers.LonLat(8.129,
							53.089);
					bounds = new OpenLayers.Bounds();
					bounds.extend(northEast);
					bounds.extend(southWest);
				}

				// Clear div
				$('div#map').empty();
				
				// Construct map
				this.map = new OpenLayers.Map("map", {
					restrictedExtent : bounds.transform(this.fromProjection, this.toProjection)
				});
				
				// add osm layer
				// Add other layers here
				this.map.addLayer(this.osm);

				// Add vector layers
				angular.forEach(this.vectorLayers, function(layer) {
					_this.map.addLayer(layer);
				});
				
				// add hightlight layer
				this.map.addLayer(this.highlightLayer);

				/*
				 * Construct heat map layers, add to map and clear
				 */
				this.gpsHeatMap = new OpenLayers.Layer.Heatmap(
						"GPS Heat Map Layer", this.map, this.osm, {
							visible : false,
							radius : 20
						}, {
							isBaseLayer : false,
							opacity : 0.3,
							projection : this.fromProjection
						});
				this.map.addLayer(this.gpsHeatMap);
				this.redrawGPSHeatMap([{}]);
				
				this.speedHeatMap = new OpenLayers.Layer.Heatmap(
						"Speed Heat Map Layer", this.map, this.osm, {
							visible : false,
							radius : 20
						}, {
							isBaseLayer : false,
							opacity : 0.3,
							projection : this.fromProjection
						});
				this.map.addLayer(this.speedHeatMap);
				this.redrawSpeedHeatMap([{}]);

				this.energyProductionHeatMap = new OpenLayers.Layer.Heatmap(
						"Energy Production Heat Map Layer",
						this.map, this.osm, {
							visible : false,
							radius : 20
						}, {
							isBaseLayer : false,
							opacity : 0.3,
							projection : this.fromProjection
						});
				this.map.addLayer(this.energyProductionHeatMap);
				this.redrawEnergyProductionHeatMap([{}]);

				this.energyConsumptionHeatMap = new OpenLayers.Layer.Heatmap(
						"Energy Consumption Heat Map Layer",
						this.map, this.osm, {
							visible : false,
							radius : 20
						}, {
							isBaseLayer : false,
							opacity : 0.3,
							projection : this.fromProjection
						});
				this.map.addLayer(this.energyConsumptionHeatMap);
				this.redrawEnergyConsumptionHeatMap([{}]);

				this.testServerHeatmap = new OpenLayers.Layer.Heatmap(
						"Test Server Heat Map Layer", this.map,
						this.osm, {
							visible : false,
							radius : 20
						}, {
							isBaseLayer : false,
							opacity : 0.3,
							projection : this.fromProjection
						});
				this.map.addLayer(this.testServerHeatmap);
				this.redrawTestServerHeatmap({});

				/*
				 * Construct toolbar items
				 */
				var toolbarItems = [
				        /*
				         * Favorites depiction button
				         */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlFavoritesDepiction',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateFavoritesDepiction();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateFavoritesDepiction();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Favorites Depiction'
								}),
								
						/*
						 * Point depiction button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlPointDepiction',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activatePointDepiction();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivatePointDepiction();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Point Depiction'
								}),
								
						/*
						 * Icon depiction Button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlIconDepiction',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateIconDepiction();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateIconDepiction();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Icon Depiction'
								}),
								
						/*
						 * GPS Heat map depiction Button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlHeatMapDepictionGPS',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateHeatMapDepictionGPS();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateHeatMapDepictionGPS();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'GPS Heat Map'
								}),
								
						/*
						 * Speed heat map depiction Button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlHeatMapDepictionSpeed',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateHeatMapDepictionSpeed();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateHeatMapDepictionSpeed();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Speed Heat Map'
								}),
								
						/*
						 * Energy production heat map depiction Button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlHeatMapDepictionEnergyProduction',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateHeatMapDepictionEnergyProduction();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateHeatMapDepictionEnergyProduction();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Energy Production Heat Map'
								}),
								
						/*
						 * Energy consumption heat map depiction Button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlHeatMapDepictionEnergyConsumption',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateHeatMapDepictionEnergyConsumption();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateHeatMapDepictionEnergyConsumption();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Energy Consumption Heat Map'
								}),
						/*
						 * TestHeatMapDepiction
						 *
						 *new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olControlTest',
									eventListeners : {
										'activate' : function() {
											ctrl.mainCtrl
													.activateTestServerHeatMap();
										},
										'deactivate' : function() {
											ctrl.mainCtrl
													.deactivateTestServerHeatMap();
										}
									},
									type : OpenLayers.Control.TYPE_TOOL,
									title : 'Test Server Heat Map'
								}),*/
						/*
						 * Open layers button
						 */
						new OpenLayers.Control.Button(
								{
									autoActivate : false,
									displayClass : 'olCognos',
									trigger : ctrl.mainCtrl.activateCognos,
									type : OpenLayers.Control.TYPE_BUTTON,
									title : 'Open Diagram Interface'
								})];
				
				// Construct Panel
				var toolbar = new OpenLayers.Control.Panel({
					defaultControl : toolbarItems[1]
				});
				
				// Add items
				toolbar.addControls(toolbarItems);
				
				// Add panel to map
				this.map.addControl(toolbar);

				/*
				 * Add select features
				 */
				var selectControl = new OpenLayers.Control.SelectFeature(
						this.vectorLayers, 
						{
							clickout : true,
							toggle : true
						});
				this.map.addControl(selectControl);
				selectControl.activate();

				// Activate point depiction as default depiction strategy
				this.activatePointDepiction();

				// center map
				this.map.setCenter(bounds.getCenterLonLat(), 11);

				// initialized
				this.initFlag = true;
				
				// hide hightlight layer initially
				this.hideHighlightLayer();
			};

			/*
			 * Add a marker
			 * @param sensor Sensor
			 * @param referenceId id
			 */
			OpenLayersService.prototype.addMarker = function(sensor, referenceId) {
				if (!this.initFlag) {
					console.log('not initialized yet');
					return;
				}
					
				if (typeof sensor === 'undefined') {
					return;
				}
				
				/*
				 * Construct feature
				 */
				var feature = new OpenLayers.Feature.Vector(
					new OpenLayers.Geometry.Point(
						sensor.longitude, 
						sensor.latitude), 
					{
						referenceId : referenceId
					}, this.layerMap[sensor.type].style);
				
				// Add feature to map
				this.layerMap[sensor.type].layer.addFeatures([feature]);
				
				// store
				this.features[sensor.id] = feature;
				
				// update
				this.updateMarker(sensor);
			};

			/*
			 * Update a marker
			 * @param sensor Sensor
			 */
			OpenLayersService.prototype.updateMarker = function(sensor) {
				if (typeof sensor === 'undefined') {
					return;
				}
				
				// Type of update action is dependend on depiction strategy
				switch(this.depictionStrategy) {
					case this.DepictionStrategies.POINT:
					case this.DepictionStrategies.ICON:
						// If sensor is visible, move it
						if (this.features[sensor.id].getVisibility()) {
							this.features[sensor.id]
									.move(new OpenLayers.LonLat(
											sensor.longitude, 
											sensor.latitude).transform(
												this.fromProjection,
												this.toProjection));
						}
						break;
					case this.DepictionStrategies.FAVORITES:
						// If sensor is favorite and visible, move it
						if (typeof this.features['f' + sensor.id] !== 'undefined'
								&& this.features['f' + sensor.id].getVisibility()) {
							this.features['f' + sensor.id]
									.move(new OpenLayers.LonLat(sensor.longitude, 
											sensor.latitude).transform(
												this.fromProjection,
												this.toProjection));
						}
						break;
				}
			};

			/*
			 * Show marker
			 * @param sensor Sensor
			 */
			OpenLayersService.prototype.showMarker = function(sensor) {
				// Add marker to layer
				if(this.layerMap.hasOwnProperty(sensor.type)) {
					this.layerMap[sensor.type].layer.addFeatures([this.features[sensor.id]]);
				}
			};

			/*
			 * Hide marker
			 * @param sensor Sensor
			 */
			OpenLayersService.prototype.hideMarker = function(sensor) {
				// Remove marker from layer
				this.layerMap[sensor.type].layer
						.removeFeatures([ this.features[sensor.id] ]);
			};

			/*
			 * Remove a marker
			 */
			OpenLayersService.prototype.removeMarker = function(sensor) {
				if (typeof sensor === 'undefined') {
					return;
				}
				
				// remove
				this.layerMap[sensor.type].layer.removeFeatures([this.features[sensor.id]]);
				delete this.features[sensor.id];
			};

			/*
			 * Move highlighter to
			 * @param lat Latitude
			 * @param lng Longitude
			 */
			OpenLayersService.prototype.moveHighlight = function(lat, lng) {
				if (!this.initFlag) {
					return;
				}
					
				/*
				 * Move highlighter
				 */
				this.highlightFeature.move(new OpenLayers.LonLat(lng, lat)
					.transform(this.fromProjection,this.toProjection));
			};

			/*
			 * Redraw GPS HeatMap
			 */
			OpenLayersService.prototype.redrawGPSHeatMap = function(sensorMaps) {
				if (!this.initFlag) {
					return;
				}
					
				// data
				var transformedTestData = {
					max : 5,
					data : []
				};

				/*
				 * Add data points
				 */
				var sensor;
				angular.forEach(sensorMaps, function(sensorMap) {
					for ( var i in sensorMap) {
						sensor = sensorMap[i];
						transformedTestData.data.push({
							lonlat : new OpenLayers.LonLat(
									sensor.positioningSensor.longitude, 
									sensor.positioningSensor.latitude),
							count : 1
						});
					}
				});

				// Set data
				this.gpsHeatMap.setDataSet(transformedTestData);
			};
			
			/*
			 * Redraw speed heat map
			 * FIXME: Interrelation between data points!
			 */
			OpenLayersService.prototype.redrawSpeedHeatMap = function(sensorMaps) {
				if (!this.initFlag) {
					return;
				}
					
				var sensor;
				var data = [];
				var max = 0; // assuming values > 0
				angular.forEach(sensorMaps, function(sensorMap) {
					for (var i in sensorMap) {
						sensor = sensorMap[i];
						if (sensor.positioningSensor.speed > max) {
							max = sensor.positioningSensor.speed;
						}

						data.push({
							lonlat : new OpenLayers.LonLat(
									sensor.positioningSensor.longitude, 
									sensor.positioningSensor.latitude),
							count : sensor.positioningSensor.speed
						});
					}
				});

				this.speedHeatMap.setDataSet({
					max : max,
					data : data
				});
			};

			/*
			 * Redraw energy production heat map
			 * FIXME: Interrelation between data points!
			 */
			OpenLayersService.prototype.redrawEnergyProductionHeatMap = function(sensorMaps) {
				if (!this.initFlag) {
					return;
				}
					
				var sensor;
				var data = [];
				var max = 0; // assuming values > 0
				angular.forEach(sensorMaps, function(sensorMap) {
					for ( var i in sensorMap) {
						sensor = sensorMap[i];
						if (sensor.getValue() > max) {
							max = sensor.getValue();
						}

						data.push({
							lonlat : new OpenLayers.LonLat(
									sensor.longitude, 
									sensor.latitude),
							count : sensor.getValue()
						});
					}
				});

				this.energyProductionHeatMap.setDataSet({
					max : max,
					data : data
				});
			};

			/*
			 * Redraw energy consumption heat map
			 * FIXME: Interrelation between data points!
			 */
			OpenLayersService.prototype.redrawEnergyConsumptionHeatMap = function(sensorMaps) {
				if (!this.initFlag) {
					return;
				}
					
				var sensor;
				var data = [];
				var max = 0; // assuming values > 0
				angular.forEach(sensorMaps, function(sensorMap) {
					for ( var i in sensorMap) {
						sensor = sensorMap[i];
						if (sensor.getValue() > max) {
							max = sensor.getValue();
						}

						data.push({
							lonlat : new OpenLayers.LonLat(sensor
									.longitude, sensor
									.latitude),
							count : sensor.getValue()
						});
					}
				});

				this.energyConsumptionHeatMap.setDataSet({
					max : max,
					data : data
				});
			};

			/*
			 * Redraw test server heat map
			 */
			OpenLayersService.prototype.redrawTestServerHeatmap = function(heatMapData) {
				if (!this.initFlag) {
					return;
				}
					
				if (typeof heatMapData.data === 'undefined') {
					heatMapData.data = [];
				}
				;

				angular.forEach(heatMapData.data, function(
						dataEntry) {
					var tmp = new OpenLayers.LonLat(
							dataEntry.lonlat.longitude.degree,
							dataEntry.lonlat.latitude.degree);
					dataEntry.lonlat = tmp;
				});
				this.testServerHeatmap.setDataSet(heatMapData);
			};

			/*
			 * Draw a path
			 * @param path Array of latitude-longitude-pairs
			 */
			OpenLayersService.prototype.drawPath = function(path) {
				if (!this.initFlag) {
					return;
				}
				
				if (path === null) {
					return;
				}
					
				// self reference
				var _this = this;
				
				// clear path features
				this.highlightLayer.removeFeatures(this.pathFeatures);
				this.pathFeatures = [];

				/*
				 * Draw features for each item in path
				 */
				angular.forEach(path, function(pathItem) {
					// construct pathItem
					var newFeature = new OpenLayers.Feature.Vector(
							new OpenLayers.Geometry.Point(
									pathItem.lat, pathItem.lng),
							null, _this.pathPointStyle);
					_this.highlightLayer.addFeatures([ newFeature ]);
					newFeature.move(new OpenLayers.LonLat(
							pathItem.lng, pathItem.lat).transform(
							_this.fromProjection,
							_this.toProjection));
					_this.pathFeatures.push(newFeature);
				});
				var c = 0;
				angular.forEach(path, function() {
					if (++c >= path.length) {
						return;
					}
					var line = new OpenLayers.Geometry.LineString([
							new OpenLayers.Geometry.Point(
									path[c - 1].lng,
									path[c - 1].lat),
							new OpenLayers.Geometry.Point(
									path[c].lng, path[c].lat) ]);
					var newFeature = new OpenLayers.Feature.Vector(
							line);
					_this.highlightLayer.addFeatures([newFeature]);
					_this.pathFeatures.push(newFeature);
				});
			};

			/*
			 * Add to favorites
			 * @param sensor Sensor
			 */
			OpenLayersService.prototype.addToFavorites = function(sensor) {
				if (!this.initFlag) {
					return;
				}
					
				var feature = this.features[sensor.id];
				if (typeof feature === 'undefined') {
					return;
				}

				// clone feature and add to featuresArray (f-Prefix)
				var favFeature = feature.clone();
				this.features['f' + sensor.id] = favFeature;

				// add to appropriate layer
				switch (sensor.type) {
					case model.SensorType.GPS_CAR:
					case model.SensorType.GPS_TRUCK:
					case model.SensorType.GPS_MOTORCYCLE:
					case model.SensorType.GPS_BUS:
					case model.SensorType.GPS_BIKE:
						this.favoriteGPSLayer.addFeatures([ favFeature ]);
					break;
					case model.SensorType.INDUCTIONLOOP:
					case model.SensorType.TRAFFIC_LIGHT_INTERSECTION:
					case model.SensorType.TOPORADAR:
						this.favoriteTrafficLayer.addFeatures([ favFeature ]);
					break;
					case model.SensorType.PHOTOVOLTAIK:
					case model.SensorType.WINDPOWERSENSOR:
					case model.SensorType.SMARTMETER:
						this.favoriteEnergyLayer.addFeatures([ favFeature ]);
					break;
					case model.SensorType.WEATHER_STATION:
						this.favoriteWeatherLayer.addFeatures([ favFeature ]);
					break;
				}

				// update
				this.updateMarker(sensor);
			};

			/*
			 * Remove from favorites
			 * @param sensor Sensor
			 */
			OpenLayersService.prototype.removeFromFavorites = function(sensor) {
				if (!this.initFlag) {
					return;
				}
					
				// Remove from layer
				switch (sensor.type) {
					case model.SensorType.GPS_CAR:
					case model.SensorType.GPS_TRUCK:
					case model.SensorType.GPS_MOTORCYCLE:
					case model.SensorType.GPS_BUS:
					case model.SensorType.GPS_BIKE:
						this.favoriteGPSLayer
								.removeFeatures([ this.features['f' + sensor.id] ]);
						break;
					case model.SensorType.INDUCTIONLOOP:
					case model.SensorType.TRAFFIC_LIGHT_INTERSECTION:
					case model.SensorType.TOPORADAR:
						this.favoriteTrafficLayer
								.removeFeatures([ this.features['f' + sensor.id] ]);
						break;
					case model.SensorType.PHOTOVOLTAIK:
					case model.SensorType.WINDPOWERSENSOR:
					case model.SensorType.SMARTMETER:
						this.favoriteEnergyLayer
								.removeFeatures([ this.features['f' + sensor.id] ]);
						break;
					case model.SensorType.WEATHER_STATION:
						this.favoriteWeatherLayer
								.removeFeatures([ this.features['f' + sensor.id] ]);
						break;
				}
				
				// delete
				delete this.features['f' + sensor.id];
			};

			/*
			 * Show a layer
			 * @param layerID id of layer (sensor type)
			 */
			OpenLayersService.prototype.showLayer = function(layerID) {
				if (!this.initFlag) {
					return;
				}
					
				/*
				 * layerID is sensorType.
				 * These sensorTypeLayers are only shown when POINT- or ICON-Depiction is active.
				 * Only show if these depiction startegies are active
				 */
				if (this.depictionStrategy === this.DepictionStrategies.POINT
				|| this.depictionStrategy === this.DepictionStrategies.ICON)
					this.layerMap[layerID].layer.setVisibility(true);
			};

			/*
			 * Hide a layer
			 * @param layerID id of layer (sensor type)
			 */
			OpenLayersService.prototype.hideLayer = function(layerID) {
				if (!this.initFlag) {
					return;
				}
					
				/*
				 * Hide layer
				 */
				this.layerMap[layerID].layer.setVisibility(false);
			};

			/*
			 * Show hightlight layer
			 */
			OpenLayersService.prototype.showHighlightLayer = function() {
				if (!this.initFlag) {
					return;
				}
					
				this.highlightLayer.setVisibility(true);
			};

			/*
			 * Hide hightlight layer
			 */
			OpenLayersService.prototype.hideHighlightLayer = function() {
				if (!this.initFlag) {
					return;
				}
					
				this.highlightLayer.setVisibility(false);
			};

			/*
			 * Show favorite layer
			 * @param favoriteLayerName Name of layer (gps,traffic,energy or weather)
			 */
			OpenLayersService.prototype.showFavoriteLayer = function(favoriteLayerName) {
				if (!this.initFlag) {
					return;
				}
					
				switch (favoriteLayerName) {
					case 'gps':
						this.favoriteGPSLayer.setVisibility(true);
						break;
					case 'traffic':
						this.favoriteTrafficLayer.setVisibility(true);
						break;
					case 'energy':
						this.favoriteEnergyLayer.setVisibility(true);
						break;
					case 'weather':
						this.favoriteWeatherLayer.setVisibility(true);
						break;
				}
			};

			/*
			 * Hide favorite layer
			 * @param favoriteLayerName Name of layer (gps,traffic,energy or weather)
			 */
			OpenLayersService.prototype.hideFavoriteLayer = function(favoriteLayerName) {
				if (!this.initFlag) {
					return;
				}
					
				switch (favoriteLayerName) {
				case 'gps':
					this.favoriteGPSLayer.setVisibility(false);
					break;
				case 'traffic':
					this.favoriteTrafficLayer.setVisibility(false);
					break;
				case 'energy':
					this.favoriteEnergyLayer.setVisibility(false);
					break;
				case 'weather':
					this.favoriteWeatherLayer.setVisibility(false);
					break;
				}
			};

			/*
			 * Pan to certrain position
			 * @param latitude Latitude
			 * @param longitude Longitude
			 */
			OpenLayersService.prototype.panTo = function(latitude, longitude) {
				if (!this.initFlag) {
					return;
				}
					
				this.map.panTo(new OpenLayers.LonLat(longitude, latitude)
					.transform(this.fromProjection, this.toProjection));
			};
			
			/*
			 *================ ACTIVATE / DEACTIVATE STRATEGIES
			 */
			/*
			 * Favorites
			 */
			OpenLayersService.prototype.activateFavoritesDepiction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}
				
				/*
				 * Set styling of layers
				 */
				this.layerMap[model.SensorType.GPS_CAR].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_CAR].style.externalGraphic = model.GPSCar.icon;
				this.layerMap[model.SensorType.GPS_CAR].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_TRUCK].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_TRUCK].style.externalGraphic = model.GPSTruck.icon;
				this.layerMap[model.SensorType.GPS_TRUCK].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.externalGraphic = model.GPSMotorcycle.icon;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_BUS].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_BUS].style.externalGraphic = model.GPSBus.icon;
				this.layerMap[model.SensorType.GPS_BUS].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_BIKE].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_BIKE].style.externalGraphic = model.GPSBike.icon;
				this.layerMap[model.SensorType.GPS_BIKE].style.fillOpacity = 1;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.pointRadius = 10;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.externalGraphic = model.TrafficLightIntersection.icon;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.fillOpacity = 1;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.pointRadius = 10;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.externalGraphic = model.InductionLoop.icon;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.fillOpacity = 1;
				this.layerMap[model.SensorType.TOPORADAR].style.pointRadius = 10;
				this.layerMap[model.SensorType.TOPORADAR].style.externalGraphic = model.TopoRadar.icon;
				this.layerMap[model.SensorType.TOPORADAR].style.fillOpacity = 1;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.pointRadius = 10;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.externalGraphic = model.Photovoltaik.icon;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.fillOpacity = 1;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.pointRadius = 10;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.externalGraphic = model.WindPowerSensor.icon;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.fillOpacity = 1;
				this.layerMap[model.SensorType.SMARTMETER].style.pointRadius = 10;
				this.layerMap[model.SensorType.SMARTMETER].style.externalGraphic = model.SmartMeter.icon;
				this.layerMap[model.SensorType.SMARTMETER].style.fillOpacity = 1;
				this.layerMap[model.SensorType.WEATHER_STATION].style.pointRadius = 10;
				this.layerMap[model.SensorType.WEATHER_STATION].style.externalGraphic = model.WeatherStation.icon;
				this.layerMap[model.SensorType.WEATHER_STATION].style.fillOpacity = 1;

				/*
				 * Show layers if checkbox is checked
				 */
				this.favoriteGPSLayer.setVisibility($(
						'#check_favGPS').is(':checked'));
				this.favoriteTrafficLayer.setVisibility($(
						'#check_favTraffic').is(':checked'));
				this.favoriteEnergyLayer.setVisibility($(
						'#check_favEnergy').is(':checked'));
				this.favoriteWeatherLayer.setVisibility($(
						'#check_favWeather').is(':checked'));

				this.depictionStrategy = this.DepictionStrategies.FAVORITES;
			};

			/*
			 * Favorites
			 */
			OpenLayersService.prototype.deactivateFavoritesDepiction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Hide all favorite layers
				 */
				this.favoriteGPSLayer.setVisibility(false);
				this.favoriteTrafficLayer.setVisibility(false);
				this.favoriteEnergyLayer.setVisibility(false);
				this.favoriteWeatherLayer.setVisibility(false);

				this.depictionStrategy = null;
			};

			/*
			 * Point
			 */
			OpenLayersService.prototype.activatePointDepiction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Set styling of layers
				 */
				this.layerMap[model.SensorType.GPS_CAR].style.strokeColor = "blue";
				this.layerMap[model.SensorType.GPS_CAR].style.fillColor = "blue";
				this.layerMap[model.SensorType.GPS_CAR].style.pointRadius = 2;
				this.layerMap[model.SensorType.GPS_CAR].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.GPS_CAR].style.externalGraphic = '';
				this.layerMap[model.SensorType.GPS_CAR].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_TRUCK].style.strokeColor = "black";
				this.layerMap[model.SensorType.GPS_TRUCK].style.fillColor = "black";
				this.layerMap[model.SensorType.GPS_TRUCK].style.pointRadius = 2;
				this.layerMap[model.SensorType.GPS_TRUCK].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.GPS_TRUCK].style.externalGraphic = '';
				this.layerMap[model.SensorType.GPS_TRUCK].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.strokeColor = "green";
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.fillColor = "green";
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.pointRadius = 2;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.externalGraphic = '';
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_BUS].style.strokeColor = "yellow";
				this.layerMap[model.SensorType.GPS_BUS].style.fillColor = "yellow";
				this.layerMap[model.SensorType.GPS_BUS].style.pointRadius = 2;
				this.layerMap[model.SensorType.GPS_BUS].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.GPS_BUS].style.externalGraphic = '';
				this.layerMap[model.SensorType.GPS_BUS].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_BIKE].style.strokeColor = "red";
				this.layerMap[model.SensorType.GPS_BIKE].style.fillColor = "red";
				this.layerMap[model.SensorType.GPS_BIKE].style.pointRadius = 2;
				this.layerMap[model.SensorType.GPS_BIKE].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.GPS_BIKE].style.externalGraphic = '';
				this.layerMap[model.SensorType.GPS_BIKE].style.cursor = 'pointer';
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.strokeColor = "black";
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.fillColor = "white";
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.pointRadius = 2;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.externalGraphic = '';
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.cursor = 'pointer';
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.strokeColor = "black";
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.fillColor = "white";
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.pointRadius = 2;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.externalGraphic = '';
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.cursor = 'pointer';
				this.layerMap[model.SensorType.TOPORADAR].style.strokeColor = "black";
				this.layerMap[model.SensorType.TOPORADAR].style.fillColor = "white";
				this.layerMap[model.SensorType.TOPORADAR].style.pointRadius = 2;
				this.layerMap[model.SensorType.TOPORADAR].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.TOPORADAR].style.externalGraphic = '';
				this.layerMap[model.SensorType.TOPORADAR].style.cursor = 'pointer';
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.strokeColor = "black";
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.fillColor = "white";
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.pointRadius = 2;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.externalGraphic = '';
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.cursor = 'pointer';
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.strokeColor = "black";
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.fillColor = "white";
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.pointRadius = 2;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.externalGraphic = '';
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.cursor = 'pointer';
				this.layerMap[model.SensorType.SMARTMETER].style.strokeColor = "black";
				this.layerMap[model.SensorType.SMARTMETER].style.fillColor = "white";
				this.layerMap[model.SensorType.SMARTMETER].style.pointRadius = 2;
				this.layerMap[model.SensorType.SMARTMETER].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.SMARTMETER].style.externalGraphic = '';
				this.layerMap[model.SensorType.SMARTMETER].style.cursor = 'pointer';
				this.layerMap[model.SensorType.WEATHER_STATION].style.strokeColor = "black";
				this.layerMap[model.SensorType.WEATHER_STATION].style.fillColor = "white";
				this.layerMap[model.SensorType.WEATHER_STATION].style.pointRadius = 2;
				this.layerMap[model.SensorType.WEATHER_STATION].style.fillOpacity = 0.4;
				this.layerMap[model.SensorType.WEATHER_STATION].style.cursor = 'pointer';

				/*
				 * Show vector layers if checkbox is checked
				 */
				angular.forEach(this.vectorLayers, function(layer) {
					layer.setVisibility($(
							'#check_' + layer.sensorType).is(
							':checked'));
				});

				this.depictionStrategy = this.DepictionStrategies.POINT;
			};

			/*
			 * Point
			 */
			OpenLayersService.prototype.deactivatePointDepiction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Hide all vector layers
				 */
				angular.forEach(this.vectorLayers, function(layer) {
					layer.setVisibility(false);
				});

				this.depictionStrategy = null;
			};

			/*
			 * Icon
			 */
			OpenLayersService.prototype.activateIconDepiction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Set styling of layers
				 */
				this.layerMap[model.SensorType.GPS_CAR].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_CAR].style.externalGraphic = model.GPSCar.icon;
				this.layerMap[model.SensorType.GPS_CAR].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_CAR].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_TRUCK].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_TRUCK].style.externalGraphic = model.GPSTruck.icon;
				this.layerMap[model.SensorType.GPS_TRUCK].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_TRUCK].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.externalGraphic = model.GPSMotorcycle.icon;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_MOTORCYCLE].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_BUS].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_BUS].style.externalGraphic = model.GPSBus.icon;
				this.layerMap[model.SensorType.GPS_BUS].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_BUS].style.cursor = 'pointer';
				this.layerMap[model.SensorType.GPS_BIKE].style.pointRadius = 10;
				this.layerMap[model.SensorType.GPS_BIKE].style.externalGraphic = model.GPSBike.icon;
				this.layerMap[model.SensorType.GPS_BIKE].style.fillOpacity = 1;
				this.layerMap[model.SensorType.GPS_BIKE].style.cursor = 'pointer';
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.pointRadius = 10;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.externalGraphic = model.TrafficLightIntersection.icon;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.fillOpacity = 1;
				this.layerMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION].style.cursor = 'pointer';
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.pointRadius = 10;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.externalGraphic = model.InductionLoop.icon;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.fillOpacity = 1;
				this.layerMap[model.SensorType.INDUCTIONLOOP].style.cursor = 'pointer';
				this.layerMap[model.SensorType.TOPORADAR].style.pointRadius = 10;
				this.layerMap[model.SensorType.TOPORADAR].style.externalGraphic = model.TopoRadar.icon;
				this.layerMap[model.SensorType.TOPORADAR].style.fillOpacity = 1;
				this.layerMap[model.SensorType.TOPORADAR].style.cursor = 'pointer';
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.pointRadius = 10;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.externalGraphic = model.Photovoltaik.icon;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.fillOpacity = 1;
				this.layerMap[model.SensorType.PHOTOVOLTAIK].style.cursor = 'pointer';
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.pointRadius = 10;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.externalGraphic = model.WindPowerSensor.icon;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.fillOpacity = 1;
				this.layerMap[model.SensorType.WINDPOWERSENSOR].style.cursor = 'pointer';
				this.layerMap[model.SensorType.SMARTMETER].style.pointRadius = 10;
				this.layerMap[model.SensorType.SMARTMETER].style.externalGraphic = model.SmartMeter.icon;
				this.layerMap[model.SensorType.SMARTMETER].style.fillOpacity = 1;
				this.layerMap[model.SensorType.SMARTMETER].style.cursor = 'pointer';
				this.layerMap[model.SensorType.WEATHER_STATION].style.pointRadius = 10;
				this.layerMap[model.SensorType.WEATHER_STATION].style.externalGraphic = model.WeatherStation.icon;
				this.layerMap[model.SensorType.WEATHER_STATION].style.fillOpacity = 1;
				this.layerMap[model.SensorType.WEATHER_STATION].style.cursor = 'pointer';

				/*
				 * Show vector layers if checkbox is checked
				 */
				angular.forEach(this.vectorLayers, function(layer) {
					layer.setVisibility($(
							'#check_' + layer.sensorType).is(
							':checked'));
				});

				this.depictionStrategy = this.DepictionStrategies.ICON;
			};

			/*
			 * Icon
			 */
			OpenLayersService.prototype.deactivateIconDepiction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Hide all vector layers
				 */
				angular.forEach(this.vectorLayers, function(layer) {
					layer.setVisibility(false);
				});

				this.depictionStrategy = null;
			};

			/*
			 * GPS Heat Map
			 */
			OpenLayersService.prototype.activateHeatMapDepictionGPS = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.gpsHeatMap.toggle();

				this.depictionStrategy = this.DepictionStrategies.GPS_HEAT_MAP;
			};

			/*
			 * GPS Heat Map
			 */
			OpenLayersService.prototype.deactivateHeatMapDepictionGPS = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.gpsHeatMap.toggle();
				
				/*
				 * Clear
				 */
				this.redrawGPSHeatMap([ {} ]);
				
				this.depictionStrategy = null;
			};

			/*
			 * Speed Heat Map
			 */
			OpenLayersService.prototype.activateHeatMapDepictionSpeed = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.speedHeatMap.toggle();
				this.depictionStrategy = this.DepictionStrategies.SPEED_HEAT_MAP;
			};

			/*
			 * Speed Heat Map
			 */
			OpenLayersService.prototype.deactivateHeatMapDepictionSpeed = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.speedHeatMap.toggle();
				
				/*
				 * Clear
				 */
				this.redrawSpeedHeatMap([ {} ]);
				
				this.depictionStrategy = null;
			};

			/*
			 * Energy Production Heat Map
			 */
			OpenLayersService.prototype.activateHeatMapDepictionEnergyProduction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.energyProductionHeatMap.toggle();
				this.depictionStrategy = this.DepictionStrategies.ENERGY_PRODUCTION_HEAT_MAP;
			};

			/*
			 * Energy Production Heat Map
			 */
			OpenLayersService.prototype.deactivateHeatMapDepictionEnergyProduction = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.energyProductionHeatMap.toggle();
				
				/*
				 * Clear
				 */
				this.redrawEnergyProductionHeatMap([ {} ]);
				
				this.depictionStrategy = null;
			};

			/*
			 * Energy Consumption Heat Map
			 */
			OpenLayersService.prototype.activateHeatMapDepictionEnergyConsumption = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.energyConsumptionHeatMap.toggle();
				this.depictionStrategy = this.DepictionStrategies.ENERGY_CONSUMPTION_HEAT_MAP;
			};

			/*
			 * Energy Consumption Heat Map
			 */
			OpenLayersService.prototype.deactivateHeatMapDepictionEnergyConsumption = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.energyConsumptionHeatMap.toggle();

				/*
				 * Clear
				 */
				this.redrawEnergyConsumptionHeatMap([ {} ]);
				
				this.depictionStrategy = null;
			};

			/*
			 * Test Server Heat Map
			 */
			OpenLayersService.prototype.activateTestServerHeatMap = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.testServerHeatmap.toggle();
				this.depictionStrategy = this.DepictionStrategies.TEST_SERVER_HEATMAP;
			};

			/*
			 * Test Server Heat Map
			 */
			OpenLayersService.prototype.deactivateTestServerHeatMap = function() {
				if (typeof this.map === 'undefined') {
					return;
				}

				/*
				 * Toggle (show/hide) layer
				 */
				this.testServerHeatmap.toggle();

				/*
				 * Clear
				 */
				this.redrawTestServerHeatmap({});
				
				this.depictionStrategy = null;
			};

			/*
			 * Depiction strategies
			 */
			OpenLayersService.prototype.DepictionStrategies = {
				POINT : 0,
				ICON : 1,
				GPS_HEAT_MAP : 2,
				FAVORITES : 3,
				ENERGY_PRODUCTION_HEAT_MAP : 4,
				ENERGY_CONSUMPTION_HEAT_MAP : 5,
				SPEED_HEAT_MAP : 6,
				TEST_SERVER_HEATMAP : 10
			};

			return OpenLayersService;
		})();
		
		// Invoke Constructor
		return new OpenLayersService();
	});
