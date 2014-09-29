var appSimulation = angular.module('simulationModule', []);

/*
 * Simulation Service
 * 
 * @author Dennis Höting
 */
appSimulation.factory('SimulationService', function(MapService) {
	var Simulation = (function() {
		function Simulation() {
			this.HEAT_MAP_REDRAW_FACTOR = 0.2;

			this.sensors = {};
			this.vehicles = {};
			this.containers = {};

			// Specific sensor map
			this.cars = {};
			this.trucks = {};
			this.motorcycles = {};
			this.bikes = {};
			this.busses = {};
			this.inductionLoops = {};
			this.topoRadars = {};
			this.photovoltaik = {};
			this.windPowerPlants = {};
			this.smartMeter = {};

			this.weatherStations = {};
			this.trafficLightIntersections = {};

			this.sensorAmount = 0;
			this.updateCounter = 0;
			
			this.lastRefresh = new Date().getTime();
		}

		Simulation.prototype.applyNewVehiclesMessage = function(vehicleDataList) {
			var _this = this;
			angular.forEach(vehicleDataList, function(vehicleData) {
				var vehicleID = vehicleData.vehicleID;
				
				var sensorsInVehicle = {};
				angular.forEach(vehicleData.sensors, function(sensorData) {
					var sensorObj = parseSensor(sensorData, vehicleID);
					sensorsInVehicle[sensorObj.id] = sensorObj;
					_this.sensors[sensorObj.id] = sensorObj;
					_this.sensorAmount++;
					
					/**
					 * Don't draw Infrared sensors.. they are in bus :)
					 */
					if(sensorObj.type != model.SensorType.INFRARED) {
						MapService.addMarker(sensorObj, vehicleID);
					}
				});
				
				var vehicle = undefined;
				switch (vehicleData.vehicleTypeID) {
					case model.VehicleType.CAR:
						vehicle = new model.Car(vehicleID, sensorsInVehicle);
						_this.cars[vehicle.id] = vehicle;
						break;
					case model.VehicleType.TRUCK:
						vehicle = new model.Truck(vehicleID, sensorsInVehicle);
						_this.trucks[vehicle.id] = vehicle;
						break;
					case model.VehicleType.MOTORCYCLE:
						vehicle = new model.Motorcycle(vehicleID, sensorsInVehicle);
						_this.motorcycles[vehicle.id] = vehicle;
						break;
					case model.VehicleType.BIKE:
						vehicle = new model.Bike(vehicleID, sensorsInVehicle);
						_this.bikes[vehicle.id] = vehicle;
						break;
					case model.VehicleType.BUS:
						vehicle = new model.Bus(vehicleID, sensorsInVehicle);
						_this.busses[vehicle.id] = vehicle;
						break;
				}
				_this.vehicles[vehicle.id] = vehicle;
			});
		};
		
		Simulation.prototype.applyNewSensorsMessage = function(sensorsList) {
			var _this = this;
			angular.forEach(sensorsList, function(sensorData) {
				if (_this.sensors.hasOwnProperty(sensorData.sensorHelper.sensorID)) {
					console.log('overwriting sensor with id ' + sensorData.sensorHelper.sensorID);
					return;
				}

				var sensorObj = undefined, container = undefined;
				switch (sensorData.sensorType) {
					case model.SensorType.INDUCTIONLOOP :
						sensorObj = new model.InductionLoop(
								sensorData.sensorHelper.sensorID, 
								sensorData.sensorHelper.position.latitude.degree, 
								sensorData.sensorHelper.position.longitude.degree,
								sensorData.sensorHelper.updateSteps);
						_this.inductionLoops[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						MapService.addMarker(sensorObj, sensorObj.id);
						break;
					case model.SensorType.TOPORADAR :
						sensorObj = new model.TopoRadar(
								sensorData.sensorHelper.sensorID, 
								sensorData.sensorHelper.position.latitude.degree, 
								sensorData.sensorHelper.position.longitude.degree,
								sensorData.sensorHelper.updateSteps);
						_this.topoRadars[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						MapService.addMarker(sensorObj, sensorObj.id);
						break;
					case model.SensorType.PHOTOVOLTAIK :
						sensorObj = new model.Photovoltaik(
								sensorData.sensorHelper.sensorID,
								sensorData.sensorHelper.position.latitude.degree, 
								sensorData.sensorHelper.position.longitude.degree, 
								sensorData.sensorHelper.area,
								sensorData.sensorHelper.updateSteps);
						_this.photovoltaik[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						MapService.addMarker(sensorObj, sensorObj.id);
						break;
					case model.SensorType.WINDPOWERSENSOR :
						sensorObj = new model.WindPowerSensor(
								sensorData.sensorHelper.sensorID, 
								sensorData.sensorHelper.position.latitude.degree, 
								sensorData.sensorHelper.position.longitude.degree,
								sensorData.sensorHelper.activityValue, 
								sensorData.sensorHelper.rotorLength,
								sensorData.sensorHelper.updateSteps);
						_this.windPowerPlants[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						MapService.addMarker(sensorObj, sensorObj.id);
						break;
					case model.SensorType.SMARTMETER :
						sensorObj = new model.SmartMeter(
								sensorData.sensorHelper.sensorID, 
								sensorData.sensorHelper.position.latitude.degree, 
								sensorData.sensorHelper.position.longitude.degree, 
								sensorData.sensorHelper.measureRadiusInMeter,
								sensorData.sensorHelper.updateSteps);
						_this.smartMeter[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						MapService.addMarker(sensorObj, sensorObj.id);
						break;
					case model.SensorType.TRAFFIC_LIGHT_INTERSECTION :
						sensorObj = new model.TrafficLightIntersection(
								sensorData.sensorHelper.sensorID, 
								sensorData.sensorHelper.position.latitude.degree, 
								sensorData.sensorHelper.position.longitude.degree, 
								'Intersection_'+sensorData.sensorHelper.sensorID, 
								sensorData.sensorHelper.updateSteps);
						_this.trafficLightIntersections[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						_this.containers[sensorObj.id] = sensorObj;
						MapService.addMarker(sensorObj, sensorObj.id);
						
						/* Add traffic lights */
						angular.forEach(sensorData.sensorHelper.trafficLightIds, function(id) {
							var trafficLight = new model.TrafficLight(id, sensorObj);
							sensorObj.subSensors[id] = trafficLight;
							_this.sensors[id] = trafficLight;
						});
						break;
					case model.SensorType.ANEMOMETER :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Anemometer(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.BAROMETER :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Barometer(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.HYGROMETER :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Hygrometer(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.LUXMETER :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Luxmeter(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.PYRANOMETER :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Pyranometer(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.RAIN :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Rain(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.THERMOMETER :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.Thermometer(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
					case model.SensorType.WINDFLAG :
						/*
						 * Put into weather station
						 */
						container = _this.weatherStations[sensorData.sensorHelper.weatherStationID];
						if (typeof container === 'undefined') {
							container = new model.WeatherStation(sensorData.sensorHelper.weatherStationID, 
									sensorData.sensorHelper.position.latitude.degree,
									sensorData.sensorHelper.position.longitude.degree, 
									'WeatherStation_' + sensorData.sensorHelper.weatherStationID,
									sensorData.sensorHelper.updateSteps);
							_this.weatherStations[container.id] = container;
							_this.containers[container.id] = container;
							MapService.addMarker(container, container.id);
						}

						sensorObj = new model.WindFlag(sensorData.sensorHelper.sensorID, container);
						container.subSensors[sensorObj.id] = sensorObj;
						_this.sensors[sensorObj.id] = sensorObj;
						break;
				}
				
				_this.sensorAmount++;
			});
		};
		

		Simulation.prototype.applySensorDataMessage = function(sensorDataList) {
			var time = new Date().getTime();
			if(!ctrl.mainCtrl.$scope.fastMapRefresh && (time - this.lastRefresh) < ctrl.mainCtrl.$scope.refreshRate) {
				return;
			} 
			
			var _this = this;
			angular.forEach(sensorDataList, function(sensorData) {
				var sensor = _this.sensors[sensorData.id];
				if (typeof sensor === 'undefined') {
					throw 'SimulationModule.applySensorDataMessage(data): Sensor with id ' + sensorData.id + ' not found!';
				}

				if (sensorData.type !== sensor.type) {
					throw 'sensorDataList.type !== sensor.type, expecting type ' + sensor.type + ' but getting ' + sensorData.type;
				};

				switch (sensor.type) {
					case model.SensorType.GPS_CAR :
					case model.SensorType.GPS_BIKE :
					case model.SensorType.GPS_MOTORCYCLE :
					case model.SensorType.GPS_BUS :
					case model.SensorType.GPS_TRUCK :
						/*
						 * Dynamic Sensors
						 */
						sensor.setActive();
						sensor.setPosition(sensorData.lat, sensorData.lng);

						sensor.distance = sensorData.distanceInMperStep;
						sensor.totalDistance = sensorData.totalDistanceInM;
						sensor.speed = sensorData.speedInKmh;
						sensor.averageSpeed = sensorData.avgSpeedInKmh;
						sensor.direction = sensorData.directionInGrad;
						sensor.travelTime = sensorData.travelTimeInMs - 3600000; // GTM+1
						break;
					case model.SensorType.TOPORADAR :
						console.log('got topo radar data');
						sensor.setActive();
						sensor.setValues(sensorData.axleCount, sensorData.length, sensorData.axialDistance1, sensorData.axialDistance2);
						break;
					case model.SensorType.INDUCTIONLOOP :
					case model.SensorType.PHOTOVOLTAIK :
					case model.SensorType.WINDPOWERSENSOR :
					case model.SensorType.SMARTMETER :
						/*
						 * Static Sensors
						 */
						sensor.setActive();
						sensor.setValue(sensorData.value);
						break;
					case model.SensorType.INFRARED :
						/*
						 * InfraredSensor in Bus
						 */
						sensor.setValue(sensorData.value);
						break;
					case model.SensorType.TRAFFIC_LIGHT_INTERSECTION :
						/*
						 * Traffic light in intersection
						 */
						console.log('got traffic light data');
						var trafficLight = _this.sensors[sensorData.trafficLightID];
						if(typeof trafficLight !== 'undefined') {
							trafficLight.setValue(sensorData.value);
							if (!trafficLight.isOriented) {
								trafficLight.setOrientations(sensorData.angle1, sensorData.angle2);
							}
						}
						sensor.setActive();
						break;
					case model.SensorType.ANEMOMETER :
					case model.SensorType.BAROMETER :
					case model.SensorType.HYGROMETER :
					case model.SensorType.LUXMETER :
					case model.SensorType.PYRANOMETER :
					case model.SensorType.RAIN :
					case model.SensorType.THERMOMETER :
					case model.SensorType.WINDFLAG :
						/*
						 * SubSensor in WeatherStation
						 */
						sensor.setValue(sensorData.value);
						sensor.container.setActive();
						break;

				}

				_this.updateSensor(sensor);
			});
			
			this.lastRefresh = new Date().getTime();
		};
		
		Simulation.prototype.updateSensor = function(sensor) {
			if(!this.sensors.hasOwnProperty(sensor.id)) return;
			
			if (ctrl.mainCtrl.$scope.autoUpdate) {
				this.updateCounter++;
				// direct update
				switch (MapService.depictionStrategy) {
					case MapService.DepictionStrategies.POINT:
					case MapService.DepictionStrategies.ICON:
					case MapService.DepictionStrategies.FAVORITES:
						if (sensor.isDynamicSensor) {
							MapService.updateMarker(sensor);
						}
						break;
					case MapService.DepictionStrategies.GPS_HEAT_MAP:
						if (sensor.isDynamicSensor) {
							if (this.updateCounter > parseInt(this.sensorAmount * this.HEAT_MAP_REDRAW_FACTOR)) {
								var sensorsForHeatMap = [];
								if (ctrl.listCtrl.$scope.carsChecked) {
									sensorsForHeatMap.push(this.cars);
								}
								if (ctrl.listCtrl.$scope.trucksChecked) {
									sensorsForHeatMap.push(this.trucks);
								}
								if (ctrl.listCtrl.$scope.motorcyclesChecked) {
									sensorsForHeatMap.push(this.motorcycles);
								}
								if (ctrl.listCtrl.$scope.bussesChecked) {
									sensorsForHeatMap.push(this.busses);
								}
								if (ctrl.listCtrl.$scope.bikesChecked) {
									sensorsForHeatMap.push(this.bikes);
								}
								
								MapService.redrawGPSHeatMap(sensorsForHeatMap);
	
								this.updateCounter = 0;
							}
						}
						break;
					case MapService.DepictionStrategies.SPEED_HEAT_MAP:
						if (sensor.isDynamicSensor) {
							if (this.updateCounter > parseInt(this.sensorAmount * this.HEAT_MAP_REDRAW_FACTOR)) {
								var sensorsForHeatMap = [];
								if (ctrl.listCtrl.$scope.carsChecked) {
									sensorsForHeatMap.push(this.cars);
								}
								if (ctrl.listCtrl.$scope.trucksChecked) {
									sensorsForHeatMap.push(this.trucks);
								}
								if (ctrl.listCtrl.$scope.motorcyclesChecked) {
									sensorsForHeatMap.push(this.motorcycles);
								}
								if (ctrl.listCtrl.$scope.bussesChecked) {
									sensorsForHeatMap.push(this.busses);
								}
								if (ctrl.listCtrl.$scope.bikesChecked) {
									sensorsForHeatMap.push(this.bikes);
								}
								
								MapService.redrawSpeedHeatMap(sensorsForHeatMap);
	
								this.updateCounter = 0;
							}
						}
						break;
					case MapService.DepictionStrategies.ENERGY_PRODUCTION_HEAT_MAP:
						if(this.updateCounter > parseInt(this.sensorAmount * this.HEAT_MAP_REDRAW_FACTOR)) {
							if(sensor.type == model.SensorType.PHOTOVOLTAIK
							|| sensor.type == model.SensorType.WINDPOWERSENSOR) {
								var sensorsForHeatMap = [];
								if (ctrl.listCtrl.$scope.photovoltaikChecked)
									sensorsForHeatMap.push(this.photovoltaik);
								if (ctrl.listCtrl.$scope.windPowerPlants)
									sensorsForHeatMap.push(this.windPowerPlants);
								MapService.redrawEnergyProductionHeatMap(sensorsForHeatMap);
	
								this.updateCounter = 0;
							}
						}
						break;
					case MapService.DepictionStrategies.ENERGY_CONSUMPTION_HEAT_MAP :
						if (this.updateCounter > parseInt(this.sensorAmount * this.HEAT_MAP_REDRAW_FACTOR)) {
							if(sensor.type == model.SensorType.SMARTMETER) {
								var sensorsForHeatMap = [];
								if (ctrl.listCtrl.$scope.smartMeterChecked) {
									sensorsForHeatMap.push(this.smartMeter);
								}
								MapService.redrawEnergyConsumptionHeatMap(sensorsForHeatMap);

								this.updateCounter = 0;
							}
						}
						break;
				}
			} else {
				ctrl.mainCtrl.$scope.simulationInfo.simulationStepCounter++;
			}
		};

		Simulation.prototype.removeSensor = function(sensor) {
			if (typeof sensor === 'undefined') {
				throw "sensor is undefined";
			}
			if (!this.sensors.hasOwnProperty(sensor.id)) {
				throw 'trying to delete non-existent sensor with id ' + sensor.id;
			}

			switch (sensor.type) {
				case model.SensorType.GPS_CAR :
					delete this.cars[sensor.id];
					break;
				case model.SensorType.GPS_TRUCK :
					delete this.trucks[sensor.id];
					break;
				case model.SensorType.GPS_MOTORCYCLE :
					delete this.motorcycles[sensor.id];
					break;
				case model.SensorType.GPS_BIKE :
					delete this.bikes[sensor.id];
					break;
				case model.SensorType.GPS_BUS :
					delete this.busses[sensor.id];
					break;
				case model.SensorType.INDUCTIONLOOP :
					delete this.inductionLoops[sensor.id];
					break;
				case model.SensorType.TOPORADAR :
					delete this.topoRadars[sensor.id];
					break;
				case model.SensorType.PHOTOVOLTAIK :
					delete this.photovoltaik[sensor.id];
					break;
				case model.SensorType.WINDPOWERSENSOR :
					delete this.windPowerPLant[sensor.id];
					break;
				case model.SensorType.SMARTMETER :
					delete this.smartMeter[sensor.id];
					break;
				case model.SensorType.TRAFFICLIGHT :
					delete this.trafficLightIntersections[sensor.container.id].infraredSensor;
					if (typeof this.trafficLightIntersections[sensor.container.id].infraredSensor) {
						delete this.trafficLightIntersections[sensor.container.id];
					}
					break;
				case model.SensorType.INFRARED :
					delete this.busses[sensor.container.id].subSensors[sensor.id];
					if (count(this.busses[sensor.container.id].subSensors) < 1) {
						delete this.busses[sensor.container.id];
					}
					break;
				case model.SensorType.ANEMOMETER :
				case model.SensorType.BAROMETER :
				case model.SensorType.HYGROMETER :
				case model.SensorType.LUXMETER :
				case model.SensorType.PYRANOMETER :
				case model.SensorType.RAIN :
				case model.SensorType.THERMOMETER :
				case model.SensorType.WINDFLAG :
					delete this.weatherStations[sensor.container.id].subSensors[sensor.id];
					if (count(this.weatherStations[sensor.container.id].subSensors) < 1) {
						delete this.weatherStations[sensor.container.id];
					}
					break;
			}

			MapService.removeMarker(sensor);
			delete this.sensors[sensor.id];
			this.sensorAmount--;
		};

		Simulation.prototype.showSensor = function(sensor) {
			MapService.showMarker(sensor);
			sensor.isShown = true;
		};

		Simulation.prototype.hideSensor = function(sensor) {
			MapService.hideMarker(sensor);
			sensor.isShown = false;
		};

		Simulation.prototype.applyGPSSensorTimeoutMessage = function(sensors) {
			var _this = this;
			
			angular.forEach(sensors, function(sensor) {
				_this.sensors[sensor.id].setInactive();
			});
		};

		Simulation.prototype.applyRemoveSensorsMessage = function(idList) {
			var _this = this;

			angular.forEach(idList, function(id) {
				_this.removeSensor(ctrl.simulation.sensors[id]);
			});
		};
		
		function parseSensor(sensorData, vehicleId) {
			switch (sensorData.sensorType) {
				case model.SensorType.GPS_CAR :
					return new model.GPSCar(
							sensorData.sensorHelper.sensorID, 
							sensorData.sensorHelper.position.latitude.degree, 
							sensorData.sensorHelper.position.longitude.degree,
							vehicleId,
							sensorData.sensorHelper.updateSteps);
				case model.SensorType.GPS_TRUCK :
					return new model.GPSTruck(
							sensorData.sensorHelper.sensorID, 
							sensorData.sensorHelper.position.latitude.degree,
							sensorData.sensorHelper.position.longitude.degree,
							vehicleId,
							sensorData.sensorHelper.updateSteps);
				case model.SensorType.GPS_MOTORCYCLE :
					return new model.GPSMotorcycle(
							sensorData.sensorHelper.sensorID, 
							sensorData.sensorHelper.position.latitude.degree, 
							sensorData.sensorHelper.position.longitude.degree,
							vehicleId,
							sensorData.sensorHelper.updateSteps);
				case model.SensorType.GPS_BIKE :
					return new model.GPSBike(
							sensorData.sensorHelper.sensorID, 
							sensorData.sensorHelper.position.latitude.degree, 
							sensorData.sensorHelper.position.longitude.degree,
							vehicleId,
							sensorData.sensorHelper.updateSteps);
				case model.SensorType.GPS_BUS :
					return new model.GPSBus(
							sensorData.sensorHelper.sensorID, 
							sensorData.sensorHelper.position.latitude.degree, 
							sensorData.sensorHelper.position.longitude.degree,
							vehicleId,
							sensorData.sensorHelper.updateSteps);
				case model.SensorType.INFRARED :
					return new model.Infrared(sensorData.sensorHelper.sensorID, 
							vehicleId,
							sensorData.sensorHelper.updateSteps);
			}
		};

		return Simulation;
	})();
	return new Simulation();
});