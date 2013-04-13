/*
 * Package model
 * 
 * Classes for sensor containers.
 * Sensor containers contain sensors.
 * @author Dennis Höting
 */
(function(model) {
	/*
	 * Traffic light intersection extends staticMarker
	 */
    var TrafficLightIntersection = (function(_super) {
    	__extends(TrafficLightIntersection, _super);
    	
    	function TrafficLightIntersection(id, latitude, longitude, name, updateSteps) {
    		_super.call(this, id, latitude, longitude, name, updateSteps);
    		
    		this.className = "TrafficLightIntersection";
    		this.type = model.SensorType.TRAFFIC_LIGHT_INTERSECTION;
    		
    		/*
    		 * Map for sensors within this container
    		 */
    		this.subSensors = {};
    	}

    	TrafficLightIntersection.icon = 'img/traffic_light.png';
    	
    	return TrafficLightIntersection;
    })(model.StaticMarker);
    model.TrafficLightIntersection = TrafficLightIntersection;
    
    /*
     * Weather station extends staticMarker
     */
    var WeatherStation = (function(_super) {
        __extends(WeatherStation, _super);

        function WeatherStation(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = "WeatherStation";
            this.type = model.SensorType.WEATHER_STATION;

    		/*
    		 * Map for sensors within this container
    		 */
            this.subSensors = {};
        }

        WeatherStation.icon = 'img/weather_station.png';

        return WeatherStation;
    })(model.StaticMarker);
    model.WeatherStation = WeatherStation;
})(model);