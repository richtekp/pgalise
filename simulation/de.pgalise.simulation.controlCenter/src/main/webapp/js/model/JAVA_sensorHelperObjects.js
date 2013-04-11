(function(model) {
	/**
	 * SENSOR HELPER
	 */
	var SensorHelper = (function() {
		function SensorHelper(sensor) {
			return {
				position : {
					latitude : {
						degree : sensor.latitude
					},
					longitude : {
						degree : sensor.longitude
					}
				},
				sensorID : sensor.id,
				sensorType : ctrl.mainCtrl.sensorTypeMap[sensor.type],
				updateSteps : sensor.updateSteps
			};
		}
		
		return SensorHelper;
	})();
	model.SensorHelper = SensorHelper;
	
	/**
	 * Sensor Helper for the photovoltaik sensor 
	 */
	var SensorHelperPhotovoltaik = (function() {
		function SensorHelperPhotovoltaik(sensor) {
			return {
				position : {
					latitude : {
						degree : sensor.latitude
					},
					longitude : {
						degree : sensor.longitude
					}
				},
				sensorID : sensor.id,
				sensorType : ctrl.mainCtrl.sensorTypeMap[model.SensorType.PHOTOVOLTAIK],
				updateSteps : sensor.updateSteps,
				area : sensor.area
			};
		}
		
		return SensorHelperPhotovoltaik;
	})();
	model.SensorHelperPhotovoltaik = SensorHelperPhotovoltaik;
	
	/**
	 * Sensor Helper for the Smart Meter sensor 
	 */
	var SensorHelperSmartMeter = (function() {
		function SensorHelperSmartMeter(sensor) {
			return {
				position : {
					latitude : {
						degree : sensor.latitude
					},
					longitude : {
						degree : sensor.longitude
					}
				},
				sensorID : sensor.id,
				sensorType : ctrl.mainCtrl.sensorTypeMap[model.SensorType.SMARTMETER],
				updateSteps : sensor.updateSteps,
				measureRadiusInMeter : sensor.measureRadiusInMeter
			};
		}
		
		return SensorHelperSmartMeter;
	})();
	model.SensorHelperSmartMeter = SensorHelperSmartMeter;
	
	/**
	 * Sensor Helper for the wind power sensor 
	 */
	var SensorHelperWindPower = (function() {
		function SensorHelperWindPower(sensor) {
			return {
				position : {
					latitude : {
						degree : sensor.latitude
					},
					longitude : {
						degree : sensor.longitude
					}
				},
				sensorID : sensor.id,
				sensorType : ctrl.mainCtrl.sensorTypeMap[model.SensorType.WINDPOWERSENSOR],
				updateSteps : sensor.updateSteps,
				activityValue : sensor.activityValue,
				rotorLength : sensor.rotorLength
			};
		}
		
		return SensorHelperWindPower;
	})();
	model.SensorHelperWindPower = SensorHelperWindPower;
	
	/**
	 * Sensor Helper for the Traffic Light sensors
	 */
	var SensorHelperTrafficLightIntersection = (function() {
		function SensorHelperTrafficLightIntersection(sensor) {
			return {
				position : {
					latitude : {
						degree : sensor.latitude
					},
					longitude : {
						degree : sensor.longitude
					}
				},
				sensorID : sensor.id,
				sensorType : ctrl.mainCtrl.sensorTypeMap[model.SensorType.TRAFFIC_LIGHT_INTERSECTION],
				updateSteps : sensor.updateSteps,
				sensorInterfererType : [],
				nodeId : sensor.nodeId,
				trafficLightIds : sensor.trafficLightIds
			};
		}
		
		return SensorHelperTrafficLightIntersection;
	})();
	model.SensorHelperTrafficLightIntersection = SensorHelperTrafficLightIntersection;
	
	var SensorHelperWeather = (function() {
		function SensorHelperWeather(sensorID, sensorType, lat, lng, updateSteps, weatherStationID) {
			return {
				position : {
					latitude : {
						degree : lat
					},
					longitude : {
						degree : lng
					}
				},
				sensorID : sensorID,
				sensorType : ctrl.mainCtrl.sensorTypeMap[sensorType],
				updateSteps : updateSteps,
				weatherStationID : weatherStationID
			};
		}
		
		return SensorHelperWeather;
	})();
	model.SensorHelperWeather = SensorHelperWeather;
})(model);