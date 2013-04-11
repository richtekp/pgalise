/*
 * Controller for details dialog
 */
function DetailsDialogCtrl($scope, SimulationService, PopupService, MapService) {
    var _this = this;
    this.$scope = $scope;
    
    // Store instance of details controller in global variable
    ctrl.detailsDialogCtrl = this;

    /*
     * Store current and last sensor in detail
     */
    this.$scope.sensorInDetail = undefined;
    this.$scope.lastSensorInDetail = undefined;

    /*
     * URLs for sensor types
     * 
     * In view, the proper html-file is then injected.
     * Method "detailsURL()" is invoked by view. 
     * The method returns the proper path.
     */
    this.detailsURLs = {};
    this.detailsURLs[-2] = 'partials/dialogs/details/empty.html'; 
    this.detailsURLs[-1] = 'partials/dialogs/details/generic.html';
    this.detailsURLs[model.VehicleType.CAR] = 'partials/dialogs/details/car.html';
    this.detailsURLs[model.VehicleType.TRUCK] = 'partials/dialogs/details/truck.html';
    this.detailsURLs[model.VehicleType.MOTORCYCLE] = 'partials/dialogs/details/motorcycle.html'; 
    this.detailsURLs[model.VehicleType.BUS] = 'partials/dialogs/details/bus.html';
    this.detailsURLs[model.VehicleType.BIKE] = 'partials/dialogs/details/bike.html';
    this.detailsURLs[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = 'partials/dialogs/details/trafficLightIntersection.html';
    this.detailsURLs[model.SensorType.INDUCTIONLOOP] = 'partials/dialogs/details/inductionLoop.html'; 
    this.detailsURLs[model.SensorType.TOPORADAR] = 'partials/dialogs/details/topoRadar.html';
    this.detailsURLs[model.SensorType.PHOTOVOLTAIK] = 'partials/dialogs/details/photovoltaik.html';
    this.detailsURLs[model.SensorType.WINDPOWERSENSOR] = 'partials/dialogs/details/windPowerSensor.html';
    this.detailsURLs[model.SensorType.SMARTMETER] = 'partials/dialogs/details/smartMeter.html';
    this.detailsURLs[model.SensorType.WEATHER_STATION] = 'partials/dialogs/details/weatherStation.html';
    this.$scope.detailsURL = function() {
        if (typeof _this.$scope.sensorInDetail === 'undefined')
            return _this.detailsURLs[-2];
        
        if(!_this.detailsURLs.hasOwnProperty(_this.$scope.sensorInDetail.type)) {
            return _this.detailsURLs[-1];
        } else {
            return _this.detailsURLs[_this.$scope.sensorInDetail.type];
        }
    };

    /*
     * Map constant "model.SensorType.TRAFFIC_LIGHT_INTERSECTION" available in view
     * 
     * A <canvas>-Object is created if and only if the current sensor in details is a trafficLight!
     */
    this.$scope.trafficLightInsersectionSensorID = model.SensorType.TRAFFIC_LIGHT_INTERSECTION;

    /*
     * Panning logic
     * 
     * Pan state and pan/unpan method
     */
    this.$scope.panningTo = false;
    this.$scope.panTo = function() {
        _this.$scope.panningTo = true;
        if(typeof _this.$scope.sensorInDetail.positioningSensor != 'undefined') {
        	MapService.panTo(_this.$scope.sensorInDetail.positioningSensor.latitude, _this.$scope.sensorInDetail.positioningSensor.longitude);
        } else {
        	MapService.panTo(_this.$scope.sensorInDetail.latitude, _this.$scope.sensorInDetail.longitude);
        }
    };
    this.$scope.unpanTo = function() {
        _this.$scope.panningTo = false;
    };

    /*
     * Bind change action to sensorInDetail.
     * This method will be invoked if "lastChange"-Property changes its value.
     */
    this.$scope.$watch('sensorInDetail.lastChange', changeAction);
    this.$scope.$watch('sensorInDetail.positioningSensor.lastChange', changeAction);
    function changeAction(changeTimestamp) {
        if ( typeof _this.$scope.sensorInDetail === 'undefined') {
            return
        }
        
        if(!ctrl.mainCtrl.$scope.autoUpdate) {
            return;
        }
        
        if(_this.$scope.sensorInDetail.type === model.SensorType.TRAFFIC_LIGHT_INTERSECTION) {
        	_this.drawIntersectionCanvas(_this.$scope.sensorInDetail);
        }
    }

    /*
     * Bind sensorInDetail change action
     * This method will be invoked, if the sensorInDetail changed.
     */
    this.$scope.$watch('sensorInDetail', function(newSensor, oldSensor) {
        _this.$scope.lastSensorInDetail = oldSensor;
        
        if(newSensor.type === model.SensorType.TRAFFIC_LIGHT_INTERSECTION) {
        	_this.drawIntersectionCanvas(newSensor);
        }
    });

    /*
     * Draw intersection canvas (for trafficLightIntersections)
     */
    this.drawIntersectionCanvas = function(newSensor) {
    	var canvas = $('#orientationCanvas')[0];
    	
    	if(typeof canvas === 'undefined') return;
    	
    	// Settings
    	var center = {x:140,y:80};
    	var radius = 70;
    	var lineWidth = 10;
    	var color = undefined;
    	
    	// Only if canvas is supported
    	if (canvas.getContext) {
        	var ctx = canvas.getContext('2d');
        	
        	// clear
        	ctx.clearRect(0,0,canvas.width, canvas.height);
        	
        	/*
        	 * For each traffic light in intersection
        	 */
        	angular.forEach(newSensor.subSensors, function(trafficLight) {
        		// calculate end points of street in canvas
            	var a = {
    	        			x:center.x + radius * Math.sin(((2*Math.PI)/360)*(trafficLight.orientation1)),
    	        			y:center.y + radius * Math.cos(((2*Math.PI)/360)*(trafficLight.orientation1+180))
            			}, 
            		b = {
    	        			x:center.x + radius * Math.sin(((2*Math.PI)/360)*(trafficLight.orientation2)),
    	        			y:center.y + radius * Math.cos(((2*Math.PI)/360)*(trafficLight.orientation2+180))
            			};
            	
            	// "calculate" color
            	switch(trafficLight.getValue()) {
            		case model.TrafficLightStates.BLINKING:
            			color = 'rgb(0,0,0)';
            			break;
            		case model.TrafficLightStates.GREEN:
            			color = 'rgb(40,255,17)';
            			break;
            		case model.TrafficLightStates.RED:
            			color = 'rgb(255,17,17)';
            			break;
            		case model.TrafficLightStates.RED_YELLOW:
            		case model.TrafficLightStates.YELLOW:
            			color = 'rgb(255,243,17)';
            			break;
            	}
            	
            	// draw
            	ctx.beginPath();
            	
            	ctx.moveTo(center.x,center.y);
            	ctx.lineTo(a.x,a.y);

            	ctx.moveTo(center.x,center.y);
            	ctx.lineTo(b.x,b.y);
            	
            	ctx.strokeStyle = color;
            	ctx.lineWidth = lineWidth;
            	ctx.stroke();
        	});
    	} else {
    		// Throw exception
    	    throw 'HTML5 support expected';
    	}
    };
    
    /*
     * Favorize current sensor
     */
    this.$scope.favorize = function() {
        if(typeof _this.$scope.sensorInDetail.positioningSensor != 'undefined') {
            MapService.addToFavorites(_this.$scope.sensorInDetail.positioningSensor);
            _this.$scope.sensorInDetail.positioningSensor.favorize();
        } else {
            MapService.addToFavorites(_this.$scope.sensorInDetail);
            _this.$scope.sensorInDetail.favorize();
        }
    };
    
    /*
     * Unfavorize current sensor
     */
    this.$scope.unfavorize = function() {
        if(typeof _this.$scope.sensorInDetail.positioningSensor != 'undefined') {
            MapService.removeFromFavorites(_this.$scope.sensorInDetail.positioningSensor);
            _this.$scope.sensorInDetail.positioningSensor.unfavorize();
        } else {
            MapService.removeFromFavorites(_this.$scope.sensorInDetail);
            _this.$scope.sensorInDetail.unfavorize();
        }
    };

    /*
     * Return weather sensor in favorite
     */
    this.$scope.isFavorized = function() {
        if(typeof _this.$scope.sensorInDetail.positioningSensor != 'undefined') {
        	return _this.$scope.sensorInDetail.positioningSensor.isFavorite;
        } else {
        	return _this.$scope.sensorInDetail.isFavorite;
        }
    };

    /*
     * Return if there is a sensorInDetail
     */
    this.$scope.filled = function() {
        return typeof _this.$scope.sensorInDetail !== 'undefined';
    };

    /*
     * Return if there is a lastSensorInDetail
     */
    this.$scope.lastFilled = function() {
        return typeof _this.$scope.lastSensorInDetail !== 'undefined';
    };
    /*
     * Remove sensor from details view
     */
    this.$scope.discard = function() {
        _this.$scope.panningTo = false;
        _this.$scope.sensorInDetail = undefined;
    };
}