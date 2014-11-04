/*
 * Package model
 * @author Dennis Höting
 */
(function(model) {
    /*
     * Base Class of all model elements
     * Children: DynamicMarker, StaticMarker
 	 * @author Dennis Höting
     */
    var OCMarker = (function() {
    	/*
    	 * Constructor
    	 */
        function OCMarker(id, latitude, longitude, name, updateSteps) {
        	/*
        	 * If Constructor is invoked, main controller is ready as well.
        	 * Thus, the map- and simulation services can be extracted to make them available here.
        	 */
            if( typeof OCMarker.mapService === 'undefined')
                OCMarker.mapService = angular.element(document).injector().get('MapService');
            if( typeof OCMarker.simulationService === 'undefined')
                OCMarker.simulationService = angular.element(document).injector().get('SimulationService');

            this.id = id;
            this.name = name;
            this.icon = undefined;
            this.latitude = latitude;
            this.longitude = longitude;
            this.className = 'OCMarker';
            this.isFavorite = false;
            this.isActive = false;
            this.isShown = false;
            this.isDynamicSensor = false;
            this.lastChange = 0;
            this.updateSteps = updateSteps;
        }

        /*
         * Store map- and simulationServices.
         * Set in constructor
         */
        OCMarker.mapService = undefined;
        OCMarker.simulationService = undefined;

        /*
         * Activate sensor
         */
        OCMarker.prototype.setActive = function() {
            if(this.isActive) return;

            this.isActive = true;
            if(!this.isShown) {
                OCMarker.simulationService.showSensor(this);
            }
        };

        /*
         * Deactivate sensor
         */
        OCMarker.prototype.setInactive = function() {
            if(!this.isActive) return;

            this.isActive = false;
            if(this.isShown) {
                OCMarker.simulationService.hideSensor(this);
            }
        };

        /*
         * Show marker
         */
        OCMarker.prototype.show = function() {
            if(this.isShown) return;

            OCMarker.simulationService.showSensor(this);
        };

        /*
         * Hide marker
         */
        OCMarker.prototype.hide = function() {
            if(!this.isShown) return;

            OCMarker.simulationService.hideSensor(this);
        };

        /*
         * Favorize sensor
         */
        OCMarker.prototype.favorize = function() {
            if(this.isFavorite) return;
            
            this.isFavorite = true;
        };

        /*
         * Unfavorize sensor
         */
        OCMarker.prototype.unfavorize = function() {
            if(!this.isFavorite) return;
            
            this.isFavorite = false;
        };

        return OCMarker;
    })();
    model.OCMarker = OCMarker;

    /*
     * Base Class for DynamicMarkers
     * Parent: OCMarker
     * Children: GPS-Sensors
 	 * @author Dennis Höting
     */
    var DynamicMarker = (function(_super) {
        __extends(DynamicMarker, _super);

        /*
         * Constructor
         */
        function DynamicMarker(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = 'DynamicMarker';
            this.isDynamicSensor = true;
			
			this.distance = 0;
			this.totalDistance = 0;
			this.speed = 0;
			this.averageSpeed = 0;
			this.direction = 0;
			this.travelTime = 0;
        }
        
        /*
         * Set position
         */
        DynamicMarker.prototype.setPosition = function(lat, lng) {
            this.latitude = lat;
            this.longitude = lng;
            
            this.lastChange = new Date().getTime();
        };
        
        /*
         * Get value
         * Returns always 1 (for heat map)
         */
        DynamicMarker.prototype.getValue = function() {
            return 1;
        };

        return DynamicMarker;
    })(model.OCMarker);
    model.DynamicMarker = DynamicMarker;
    
    /*
     * Base Class for StaticMarkers
     * Parent: OCMarker
     * Children: TrafficLight, InductionLoop
 	 * @author Dennis Höting
     */
    var StaticMarker = (function(_super) {
        __extends(StaticMarker, _super);

        /*
         * Constructor
         */
        function StaticMarker(id, latitude, longitude, name, updateSteps) {
            _super.call(this, id, latitude, longitude, name, updateSteps);

            this.className = 'StaticMarker';
            this.value = 0;
            this.isDynamicSensor = false;

            //this.ringBuffer = new DynamicRingBuffer(ctrl.detailsCtrl.getHistorySize);
            
            this.setActive();
        }

        /*
         * Set value
         */
        StaticMarker.prototype.setValue = function(value) {
            this.value = value;
            
            /*
            if (this.isFavorite || (typeof this.container !== 'undefined' && this.container.isFavorite)) {
                this.ringBuffer.push({
                    timestamp : new Date().getTime(),
                    message : 'new value: ' + value,
                    value : value
                });
            }
            */
            
            this.lastChange = new Date().getTime();
            
            if(typeof this.container !== 'undefined')
            	this.container.lastChange = new Date().getTime();
        };
        
        /*
         * Get value
         */
        StaticMarker.prototype.getValue = function() {
            return this.value;
        };

        return StaticMarker;
    })(model.OCMarker);
    model.StaticMarker = StaticMarker;
})(model);
