(function(model) {
    /*
     * TrafficLight-Model
     * Parent: StaticMarker
     */
    var TrafficLightIntersection = (function(_super) {
        __extends(TrafficLightIntersection, _super);

        function TrafficLightIntersection(id, latitude, longitude, name, updateSteps, trafficLightIds) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = 'TrafficLight';
            this.icon = 'img/traffic_lightM.png';
            this.type = model.SensorType.TRAFFIC_LIGHT_INTERSECTION;
            this.trafficLightIds = trafficLightIds;
        }

        return TrafficLightIntersection;
    })(model.StaticMarker);
    model.TrafficLightIntersection = TrafficLightIntersection;

    /*
     * InductionLoop-Model
     * Parent: StaticMarker
     */
    var InductionLoop = (function(_super) {
        __extends(InductionLoop, _super);

        function InductionLoop(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = 'InductionLoop';
            this.icon = 'img/induction_loopM.png';
            this.type = model.SensorType.INDUCTIONLOOP;
        }

        return InductionLoop;
    })(model.StaticMarker);
    model.InductionLoop = InductionLoop;

    /*
     * SolarCollector-Model
     * Parent: StaticMarker
     */
    var Photovoltaik = (function(_super) {
        __extends(Photovoltaik, _super);

        function Photovoltaik(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = 'Photovoltaik';
            this.area = 500;
            this.icon = 'img/solar_powerM.png';
            this.type = model.SensorType.PHOTOVOLTAIK;
        }

        return Photovoltaik;
    })(model.StaticMarker);
    model.Photovoltaik = Photovoltaik;

    /*
     * WindPowerPlant-Model
     * Parent: StaticMarker
     */
    var WindPowerSensor = (function(_super) {
        __extends(WindPowerSensor, _super);

        function WindPowerSensor(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = "WindPowerplant";
            this.activityValue = 50;
            this.rotorLength = 20;
            this.icon = 'img/wind_power_plantM.png';
            this.type = model.SensorType.WINDPOWERSENSOR;
        }

        return WindPowerSensor;
    })(model.StaticMarker);
    model.WindPowerSensor = WindPowerSensor;

    /*
     * SmartMeter-Model
     */
    var SmartMeter = (function(_super) {
        __extends(SmartMeter, _super);

        function SmartMeter(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = "SmartMeter";
            this.measureRadiusInMeter = 500;
            this.icon = 'img/smart_meterM.png';
            this.type = model.SensorType.SMARTMETER;
        }

        SmartMeter.prototype.getCircleId = function() {
        	return this.id + 'C';
        };

        return SmartMeter;
    })(model.StaticMarker);
    model.SmartMeter = SmartMeter;

    /*
     * TopoRadar-Model
     */
    var TopoRadar = (function(_super) {
        __extends(TopoRadar, _super);

        function TopoRadar(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = "TopoRadar";
            this.icon = 'img/toporadarM.png';
            this.type = model.SensorType.TOPORADAR;
        }

        return TopoRadar;
    })(model.StaticMarker);
    model.TopoRadar = TopoRadar;
    /*
     * WeatherSensor-Model
     */
    var WeatherStation = (function(_super) {
        __extends(WeatherStation, _super);

        function WeatherStation(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            var defaultCheck = true;

            this.className = "WeatherStation";
            this.icon = 'img/weather_stationM.png';
            this.type = model.SensorType.WEATHER_STATION;

            this.subSensors = [{
                name : "Anemometer",
                type : model.SensorType.ANEMOMETER,
                check : defaultCheck
            }, {
                name : "Barometer",
                type : model.SensorType.BAROMETER,
                check : defaultCheck
            }, {
                name : "Hygrometer",
                type : model.SensorType.HYGROMETER,
                check : defaultCheck
            }, {
                name : "Luxmeter",
                type : model.SensorType.LUXMETER,
                check : defaultCheck
            }, {
                name : "Pyranometer",
                type : model.SensorType.PYRANOMETER,
                check : defaultCheck
            }, {
                name : "Rain",
                type : model.SensorType.RAIN,
                check : defaultCheck
            }, {
                name : "Thermometer",
                type : model.SensorType.THERMOMETER,
                check : defaultCheck
            }, {
                name : "Windflag",
                type : model.SensorType.WINDFLAG,
                check : defaultCheck
            }];
        }
        
        WeatherStation.prototype.checkAll = function() {
        	angular.forEach(this.subSensors, function(subSensor) {
        		subSensor.check = true;
        	});
        }; 
        
        WeatherStation.prototype.uncheckAll = function() {
        	angular.forEach(this.subSensors, function(subSensor) {
        		subSensor.check = false;
        	});
        };

        return WeatherStation;
    })(model.StaticMarker);
    model.WeatherStation = WeatherStation;
})(model);

