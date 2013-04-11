/*
 * Controller for details view
 * 
 * @param $scope Scope
 * @param SimulationService Injected service für simulation
 * @paran PopupService Injected service for popup management
 * @param MapService Injected service for map
 */
function DetailsCtrl($scope, SimulationService, PopupService, MapService) {
    var _this = this;
    this.$scope = $scope;

    // Store instance of details controller in global variable
    ctrl.detailsCtrl = this;

    /*
     * Store current and last sensor in detail
     */
    this.$scope.sensorInDetail = undefined;
    this.$scope.lastSensorInDetail = undefined;
    this.detailsURLs = {};
    this.detailsURLs[-2] = 'partials/content/details/empty.html'; 
    this.detailsURLs[-1] = 'partials/content/details/generic.html';
    this.detailsURLs[model.VehicleType.CAR] = 'partials/content/details/car.html';
    this.detailsURLs[model.VehicleType.TRUCK] = 'partials/content/details/truck.html';
    this.detailsURLs[model.VehicleType.MOTORCYCLE] = 'partials/content/details/motorcycle.html'; 
    this.detailsURLs[model.VehicleType.BUS] = 'partials/content/details/bus.html';
    this.detailsURLs[model.VehicleType.BIKE] = 'partials/content/details/bike.html';
    this.detailsURLs[model.SensorType.TRAFFIC_LIGHT_INTERSECTION] = 'partials/content/details/trafficLightIntersection.html';
    this.detailsURLs[model.SensorType.INDUCTIONLOOP] = 'partials/content/details/inductionLoop.html'; 
    this.detailsURLs[model.SensorType.TOPORADAR] = 'partials/content/details/topoRadar.html';
    this.detailsURLs[model.SensorType.PHOTOVOLTAIK] = 'partials/content/details/photovoltaik.html';
    this.detailsURLs[model.SensorType.WINDPOWERSENSOR] = 'partials/content/details/windPowerSensor.html';
    this.detailsURLs[model.SensorType.SMARTMETER] = 'partials/content/details/smartMeter.html';
    this.detailsURLs[model.SensorType.WEATHER_STATION] = 'partials/content/details/weatherStation.html';
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
     * Settings for simple sparkline graph
     */
    this.$scope.graphDataSize = 10;
    this.$scope.speedGraphData = [];
    this.$scope.$watch('speedGraphData.length', function(newVal) {
    	if(_this.$scope.speedGraphData.length>_this.$scope.graphDataSize) {
    		_this.$scope.speedGraphData.splice(0,1);
    	}
    	$('#speedHistory').sparkline(_this.$scope.speedGraphData, {width: 140});
    });
    this.$scope.valueGraphData = [];
    this.$scope.$watch('valueGraphData.length', function(newVal) {
    	if(_this.$scope.valueGraphData.length>_this.$scope.graphDataSize) {
    		_this.$scope.valueGraphData.splice(0,1);
    	}
    	$('#valueHistory').sparkline(_this.$scope.valueGraphData, {width: 140});
    });

    /*
     * Set sensor as sensorInDetail
     */
    this.setSensorInDetail = function(param) {
        var newSensorInDetail = undefined;
        if ( typeof param === 'undefined' || param === null) {
            newSensorInDetail = undefined;
        } else if ( typeof param === 'number' || typeof param === 'string') {
            newSensorInDetail = SimulationService.sensors[param]||SimulationService.vehicles[param]||SimulationService.containers[param];
        } else if ( typeof param === 'object') {
            newSensorInDetail = param;
        }
        
        _this.$scope.sensorInDetail = newSensorInDetail;
        
        ctrl.mainCtrl.applyView();
    };

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
     * Highlighter logic
     * 
     * Highlight state and show/hideHighlighter methods, which will call methods in Map service
     */
    this.$scope.highlighterShown = true;
    this.$scope.showHighlighter = function() {
        _this.$scope.highlighterShown = true;
        MapService.showHighlightLayer();
    };
    this.$scope.hideHighlighter = function() {
        _this.$scope.highlighterShown = false;
        MapService.hideHighlightLayer();
    };
    
    /*
     * Bind change action to sensorInDetail.
     * This method will be invoked if "lastChange"-Property changes its value.
     * If sensorInDetail is dynamic, change action must be bound to its positioningSensor
     */
    this.$scope.$watch('sensorInDetail.lastChange', changeAction);
    this.$scope.$watch('sensorInDetail.positioningSensor.lastChange', changeAction);
    function changeAction(changeTimestamp) {
        if ( typeof _this.$scope.sensorInDetail === 'undefined') {
            return
        }
        
        // If autoUpdate == false, do nothing
        if(!ctrl.mainCtrl.$scope.autoUpdate) {
            return;
        }

        if(_this.$scope.sensorInDetail.type === model.SensorType.TRAFFIC_LIGHT_INTERSECTION) {
        	/*
        	 * SensorInDetail is trafficLightIntersection: Refresh canvas
        	 */
        	_this.drawIntersectionCanvas(_this.$scope.sensorInDetail);
        } else if(_this.$scope.sensorInDetail.isDynamicSensor) { 
        	/*
        	 * SensorInDetail is some dynamic sensor:
        	 * Push speed into sparkline graph,
        	 * move highlighter,
        	 * pan to if state appropriate
        	 */
        	_this.$scope.speedGraphData.push(_this.$scope.sensorInDetail.positioningSensor.speed.toFixed(2));
        	
        	if(typeof _this.$scope.sensorInDetail.positioningSensor != 'undefined') {
            	MapService.moveHighlight(_this.$scope.sensorInDetail.positioningSensor.latitude, _this.$scope.sensorInDetail.positioningSensor.longitude);
            } else {
            	MapService.moveHighlight(_this.$scope.sensorInDetail.latitude, _this.$scope.sensorInDetail.longitude);
            }
            
	        if(_this.$scope.panningTo) {
	        	_this.$scope.panTo();
	        }
        } else {
        	/*
        	 * SensorInDetail is some static sensor (but not trafficLightIntersection):
        	 * Just push new value into sparkline graph
        	 */
        	_this.$scope.valueGraphData.push(_this.$scope.sensorInDetail.getValue());
        }
    }
    
    /*
     * Bind sensorInDetail change action
     * This method will be invoked, if the sensorInDetail changed.
     * 
     * Three cases possible:
     * 1. Change from nothing to a sensor
     * 2. Change from a sensor to nothing
     * 3. Change from a sensor to another
     */
    this.$scope.$watch('sensorInDetail', function(newSensor, oldSensor) {
        if (typeof newSensor === 'undefined') {
        	/*
        	 * If 2., clear hightlighter and sparklines
        	 */
            _this.$scope.hideHighlighter();
            _this.$scope.speedGraphData = [];
            _this.$scope.valueGraphData = [];
        } else {
        	if(typeof oldSensor !== 'undefined' && newSensor.id !== oldSensor.id) {
        		/*
        		 * If 3., clear sparklines. Highlighter will just be reallocated
        		 */
                _this.$scope.speedGraphData = [];
                _this.$scope.valueGraphData = [];
        	}
        	
        	// Show highlighter
        	_this.$scope.showHighlighter();
        }
        
        // store last sensor
        _this.$scope.lastSensorInDetail = oldSensor;
        
        /*
         * Move hightlighter to new sensor (if not undefined) and pan to
         */
        if(typeof newSensor !== 'undefined') {
        	if(typeof _this.$scope.sensorInDetail.positioningSensor != 'undefined') {
            	MapService.moveHighlight(_this.$scope.sensorInDetail.positioningSensor.latitude, _this.$scope.sensorInDetail.positioningSensor.longitude);
            } else {
            	MapService.moveHighlight(_this.$scope.sensorInDetail.latitude, _this.$scope.sensorInDetail.longitude);
            }
        	
	        if(_this.$scope.panningTo) {
	        	_this.$scope.panTo();
	        }
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
        		var a,b;
        		// calculate end points of street in canvas
        		if(trafficLight.orientation1>=0) {
        			a = {
    	        			x:center.x + radius * Math.sin(((2*Math.PI)/360)*(trafficLight.orientation1)),
    	        			y:center.y + radius * Math.cos(((2*Math.PI)/360)*(trafficLight.orientation1))
            			};
        		} else {
        			a = {x:center.x,y:center.y};
        		}
        		if(trafficLight.orientation2>=0) {
        			b = {
    	        			x:center.x + radius * Math.sin(((2*Math.PI)/360)*(trafficLight.orientation2)),
    	        			y:center.y + radius * Math.cos(((2*Math.PI)/360)*(trafficLight.orientation2))
            			};
        		} else {

        			a = {x:center.x,y:center.y};
        		}
            	
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
     * Recall function
     * Switch between last and current sensor in detail
     */
    this.$scope.recall = function() {
        var tmp = _this.$scope.lastSensorInDetail;
        _this.$scope.lastSensorInDetail = _this.$scope.sensorInDetail;
        _this.$scope.sensorInDetail = tmp;
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
    
    /*
     * Open detailDialog with currentSensorInDetail
     */
    this.$scope.openDetailDialog = function() {
    	PopupService.openDetailDialog(_this.$scope.sensorInDetail);
    };
}