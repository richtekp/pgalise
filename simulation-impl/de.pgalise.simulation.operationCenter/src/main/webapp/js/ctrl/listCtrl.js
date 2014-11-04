/*
 * Controller for list tab view
 * 
 * @param $scope Scope
 * @param SimulationService Injected service for simulation
 * @param MapService Injected service for map
 * @author Dennis Höting
 */
function ListCtrl($scope, SimulationService, MapService) {
    var _this = this;
    ctrl.listCtrl = this;
    this.$scope = $scope;

    /*
     * make sensor types available in view
     */
    this.$scope.sensorType = model.SensorType;

    /*
     * Models for checkboxes
     */
    this.$scope.carsChecked = true;
    this.$scope.trucksChecked = true;
    this.$scope.motorcyclesChecked = true;
    this.$scope.bussesChecked = true;
    this.$scope.bikesChecked = true;
    this.$scope.trafficLightIntersectionsChecked = true;
    this.$scope.inductionLoopsChecked = true;
    this.$scope.topoRadarsChecked = true;
    this.$scope.photovoltaikChecked = true;
    this.$scope.windPowerPlantsChecked = true;
    this.$scope.smartMeterChecked = true;
    this.$scope.weatherStationsChecked = true;
    
    this.$scope.favoriteGPSChecked = true;
    this.$scope.favoriteTrafficChecked = true;
    this.$scope.favoriteEnergyChecked = true;
    this.$scope.favoriteWeatherChecked = true;
    
    /*
     * Models für disabled states
     */
    this.$scope.favoriteGPSDisabled = true;
    this.$scope.favoriteTrafficDisabled = true;
    this.$scope.favoriteEnergyDisabled = true;
    this.$scope.favoriteWeatherDisabled = true;
    this.$scope.carsDisabled = true;
    this.$scope.trucksDisabled = true;
    this.$scope.motorcyclesDisabled = true;
    this.$scope.bussesDisabled = true;
    this.$scope.bikesDisabled = true;
    this.$scope.trafficLightIntersectionsDisabled = true;
    this.$scope.inductionLoopsDisabled = true;
    this.$scope.topoRadarsDisabled = true;
    this.$scope.photovoltaikDisabled = true;
    this.$scope.windPowerPlantsDisabled = true;
    this.$scope.smartMeterDisabled = true;
    this.$scope.weatherStationsDisabled = true;
    
    /*
     * Disable all checkboxes
     */
    this.disableAll = function() {
        this.$scope.favoriteGPSDisabled = true;
        this.$scope.favoriteTrafficDisabled = true;
        this.$scope.favoriteEnergyDisabled = true;
        this.$scope.favoriteWeatherDisabled = true;
        this.$scope.carsDisabled = true;
        this.$scope.trucksDisabled = true;
        this.$scope.motorcyclesDisabled = true;
        this.$scope.bussesDisabled = true;
        this.$scope.bikesDisabled = true;
        this.$scope.trafficLightIntersectionsDisabled = true;
        this.$scope.inductionLoopsDisabled = true;
        this.$scope.topoRadarsDisabled = true;
        this.$scope.photovoltaikDisabled = true;
        this.$scope.windPowerPlantsDisabled = true;
        this.$scope.smartMeterDisabled = true;
        this.$scope.weatherStationsDisabled = true;
    };
    
    /*
     * Make dynamic sensors available for view
     */
    this.$scope.cars = SimulationService.cars;
    this.$scope.trucks = SimulationService.trucks;
    this.$scope.motorcycles = SimulationService.motorcycles;
    this.$scope.busses = SimulationService.busses;
    this.$scope.bikes = SimulationService.bikes;

    /*
     * Make static sensors available for view
     */
    this.$scope.trafficLightIntersections = SimulationService.trafficLightIntersections;
    this.$scope.inductionLoops = SimulationService.inductionLoops;
    this.$scope.topoRadars = SimulationService.topoRadars;
    this.$scope.photovoltaik = SimulationService.photovoltaik;
    this.$scope.windPowerPlants = SimulationService.windPowerPlants;
    this.$scope.smartMeter = SimulationService.smartMeter;
    this.$scope.weatherStations = SimulationService.weatherStations;

    /*
     * Change actions for checkboxes
     */
    this.$scope.favoriteGPSCheckChange = function() {
    	var checked = _this.$scope.favoriteGPSChecked;
        if (checked) {
            MapService.hideFavoriteLayer('gps');
        } else {
            MapService.showFavoriteLayer('gps');
        }
        _this.$scope.favoriteGPSChecked = !checked;
        check('favoriteGPSChecked', !checked);
    };
    this.$scope.favoriteTrafficCheckChange = function() {
    	var checked = _this.$scope.favoriteTrafficChecked;
        if (checked) {
            MapService.hideFavoriteLayer('traffic');
        } else {
            MapService.showFavoriteLayer('traffic');
        }
        _this.$scope.favoriteTrafficChecked = !checked;
        check('favoriteTrafficChecked', !checked);
    };
    this.$scope.favoriteEnergyCheckChange = function() {
    	var checked = _this.$scope.favoriteEnergyChecked;
        if (checked) {
            MapService.hideFavoriteLayer('traffic');
        } else {
            MapService.showFavoriteLayer('traffic');
        }
        _this.$scope.favoriteEnergyChecked = !checked;
        check('favoriteEnergyChecked', !checked);
    };
    this.$scope.favoriteWeatherCheckChange = function() {
    	var checked = _this.$scope.favoriteWeatherChecked;
        if (checked) {
            MapService.hideFavoriteLayer('weather');
        } else {
            MapService.showFavoriteLayer('weather');
        }
        _this.$scope.favoriteWeatherChecked = !checked;
        check('favoriteWeatherChecked', !checked);
    };
    this.$scope.checkChange = function(type) {
        var checked;
        switch(type) {
            case model.SensorType.GPS_CAR:
                checked = _this.$scope.carsChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.GPS_CAR);
                } else {
                    MapService.showLayer(model.SensorType.GPS_CAR);
                }
                _this.$scope.carsChecked = !checked;
                check('carsChecked', !checked);
                break;
            case model.SensorType.GPS_TRUCK:
                checked = _this.$scope.trucksChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.GPS_TRUCK);
                    _this.$scope.trucksChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.GPS_TRUCK);
                    _this.$scope.trucksChecked = true;
                }
                _this.$scope.trucksChecked = !checked;
                check('trucksChecked', !checked);
                break;
            case model.SensorType.GPS_MOTORCYCLE:
                checked = _this.$scope.motorcyclesChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.GPS_MOTORCYCLE);
                    _this.$scope.motorcyclesChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.GPS_MOTORCYCLE);
                    _this.$scope.motorcyclesChecked = true;
                }
                _this.$scope.motorcyclesChecked = !checked;
                check('motorcyclesChecked', !checked);
                break;
            case model.SensorType.GPS_BUS:
                checked = _this.$scope.bussesChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.GPS_BUS);
                    _this.$scope.bussesChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.GPS_BUS);
                    _this.$scope.bussesChecked = true;
                }
                _this.$scope.bussesChecked = !checked;
                check('bussesChecked', !checked);
                break;
            case model.SensorType.GPS_BIKE:
                checked = _this.$scope.bikesChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.GPS_BIKE);
                    _this.$scope.bikesChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.GPS_BIKE);
                    _this.$scope.bikesChecked = true;
                }
                _this.$scope.bikesChecked = !checked;
                check('bikesChecked', !checked);
                break;
            case model.SensorType.TRAFFIC_LIGHT_INTERSECTION:
                    checked = _this.$scope.trafficLightIntersectionsChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.TRAFFIC_LIGHT_INTERSECTION);
                    _this.$scope.trafficLightIntersectionsChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.TRAFFIC_LIGHT_INTERSECTION);
                    _this.$scope.trafficLightIntersectionsChecked = true;
                }
                _this.$scope.trafficLightIntersectionsChecked = !checked;
                check('trafficLightIntersectionsChecked', !checked);
                break;
            case model.SensorType.INDUCTIONLOOP:
                    checked = _this.$scope.inductionLoopsChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.INDUCTIONLOOP);
                    _this.$scope.inductionLoopsChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.INDUCTIONLOOP);
                    _this.$scope.inductionLoopsChecked = true;
                }
                _this.$scope.inductionLoopsChecked = !checked;
                check('inductionLoopsChecked', !checked);
                break;
            case model.SensorType.TOPORADAR:
                    checked = _this.$scope.topoRadarsChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.TOPORADAR);
                    _this.$scope.topoRadarsChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.TOPORADAR);
                    _this.$scope.topoRadarsChecked = true;
                }
                _this.$scope.topoRadarsChecked = !checked;
                check('topoRadarsChecked', !checked);
                break;
            case model.SensorType.PHOTOVOLTAIK:
                    checked = _this.$scope.photovoltaikChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.PHOTOVOLTAIK);
                    _this.$scope.photovoltaikChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.PHOTOVOLTAIK);
                    _this.$scope.photovoltaikChecked = true;
                }
                _this.$scope.photovoltaikChecked = !checked;
                check('photovoltaikChecked', !checked);
                break;
            case model.SensorType.WINDPOWERSENSOR:
                    checked = _this.$scope.windPowerPlantsChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.WINDPOWERSENSOR);
                    _this.$scope.windPowerPlantsChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.WINDPOWERSENSOR);
                    _this.$scope.windPowerPlantsChecked = true;
                }
                _this.$scope.windPowerPlantsChecked = !checked;
                check('windPowerPlantsChecked', !checked);
                break;
            case model.SensorType.SMARTMETER:
                    checked = _this.$scope.smartMeterChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.SMARTMETER);
                    _this.$scope.smartMeterChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.SMARTMETER);
                    _this.$scope.smartMeterChecked = true;
                }
                _this.$scope.smartMeterChecked = !checked;
                check('smartMeterChecked', !checked);
                break;
            case model.SensorType.WEATHER_STATION:
                    checked = _this.$scope.weatherStationsChecked;
                if (checked) {
                    MapService.hideLayer(model.SensorType.WEATHER_STATION);
                    _this.$scope.weatherStationsChecked = false;
                } else {
                    MapService.showLayer(model.SensorType.WEATHER_STATION);
                    _this.$scope.weatherStationsChecked = true;
                }
                _this.$scope.weatherStationsChecked = !checked;
                check('weatherStationsChecked', !checked);
                break;
        }
    };

    /*
     * Favorize sensor
     */
    this.$scope.favorize = function(id) {
        var sensor = SimulationService.sensors[id];
        if (sensor.isFavorite) {
            MapService.removeFromFavorites(sensor);
            sensor.unfavorize();
        } else {
            MapService.addToFavorites(sensor);
            sensor.favorize();
        }
    };

    /*
     * Detailize
     */
    this.$scope.detailize = function(id) {
        ctrl.detailsCtrl.setSensorInDetail(id);
        ctrl.tabCtrl.switchTo('details');
    };

    /*
     * Count functions
     */
    this.$scope.countFavorized = function(mySet) {
        if (!undef(mySet)) {
            var _tmp = 0;
            for (var k in mySet) {
            	if(mySet.hasOwnProperty(k)) {
                    if (mySet[k].isFavorite) {
                        _tmp++;
                    }
            	}
            }
            return _tmp;
        }
        return 0;
    };
    this.$scope.countFavorizedVehicles = function(mySet) {
        if (!undef(mySet)) {
            var _tmp = 0;
            for (var k in mySet) {
            	if(mySet.hasOwnProperty(k) && typeof mySet[k].positioningSensor !== 'undefined') {
	                if (mySet[k].positioningSensor.isFavorite) {
	                    _tmp++;
	                }
                }
            }
            return _tmp;
        }
        return 0;
    };

    /*
     * Return filtered sensors
     * 
     * @param query Query
     * @param size Result size
     */
    this.$scope.filteredSensors = function(query, size) {
        if ( typeof query === undefined || query === "")
            return null;

        var count = 0;
        var queryRegExp = RegExp(query, 'i');
        var result = {};
        for (var id in SimulationService.sensors) {
            if (count >= size)
                break;
            if (SimulationService.sensors[id].name.match(queryRegExp)) {
                result[id] = SimulationService.sensors[id];
                count++;
            }
        }
        return result;
    };
    
    /*
     * Return active sensors
     * 
     * @param set From set
     * @param size Result size
     */
    this.$scope.activeSensors = function(set, size) {
    	var count = 0;
    	var result = {};
    	for(var id in set) {
    		if(count>=size) break;
    		if((typeof set[id].positioningSensor !== 'undefined' && set[id].positioningSensor.isActive) || set[id].isActive) {
    			result[id]=set[id];
    		}
    	}
    	return result;
    };
}