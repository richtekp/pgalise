/*
 * Package model
 */
(function(model) {
	/*
	 * SubSensors are sensors within a container (container.js)
	 */
    var SubSensor = (function() {
    	/*
    	 * Constructor
    	 */
        function SubSensor(id, name, container) {
        	this.id = id;
        	this.name = name;
            this.container = container;
            
            this.className = 'SubSensor';
            this.isDynamicSensor = false;

            this.value = 0;
            
            //this.ringBuffer = new DynamicRingBuffer(ctrl.detailsCtrl.getHistorySize);
        }

        /*
         * Set value
         */
        SubSensor.prototype.setValue = function(value) {
            this.value = value;
            
            if (this.isFavorite || (typeof this.container !== 'undefined' && this.container.isFavorite)) {
                this.ringBuffer.push({
                    timestamp : new Date().getTime(),
                    message : 'new value: ' + value,
                    value : value
                });
            }
            
            this.lastChange = new Date().getTime();
            
            if(typeof this.container !== 'undefined')
            	this.container.lastChange = new Date().getTime();
        };
        
        /*
         * Get value
         */
        SubSensor.prototype.getValue = function() {
            return this.value;
        };

        return SubSensor;
    })();
    model.SubSensor = SubSensor;
    
    /*
     * TrafficLight-Model
     * Parent: StaticMarker
     */
    var TrafficLight = (function(_super) {
        __extends(TrafficLight, _super);

    	/*
    	 * Constructor
    	 */
        function TrafficLight(id, container) {
            _super.call(this, id, 'TrafficLight', container);

            this.className = 'TrafficLight';
            this.type = model.SensorType.TRAFFICLIGHT;
            this.value = model.TrafficLightStates.BLINKING;
            this.isOriented = false;
            this.orientation1 = 0;
            this.orientation2 = 0;
        }
        
        /*
         * Set orientations
         */
        TrafficLight.prototype.setOrientations = function(o1, o2) {
            this.isOriented = true;
            this.orientation1 = o1;
            this.orientation2 = o2;
        };

        return TrafficLight;
    })(model.SubSensor);
    model.TrafficLight = TrafficLight;

    /*
     * Anemometer-Model
     */
    var Anemometer = (function(_super) {
        __extends(Anemometer, _super);

    	/*
    	 * Constructor
    	 */
        function Anemometer(id, container) {
            _super.call(this, id, 'Anemometer', container);

            this.className = "Anemometer";
            this.type = model.SensorType.ANEMOMETER;
        }

        return Anemometer;
    })(model.SubSensor);
    model.Anemometer = Anemometer;

    /*
     * Barometer-Model
     */
    var Barometer = (function(_super) {
        __extends(Barometer, _super);

    	/*
    	 * Constructor
    	 */
        function Barometer(id, container) {
            _super.call(this, id, 'Barometer', container);

            this.className = "Barometer";
            this.type = model.SensorType.BAROMETER;
        }

        return Barometer;
    })(model.SubSensor);
    model.Barometer = Barometer;

    /*
     * Hygrometer-Model
     */
    var Hygrometer = (function(_super) {
        __extends(Hygrometer, _super);

    	/*
    	 * Constructor
    	 */
        function Hygrometer(id, container) {
            _super.call(this, id, 'Hygrometer', container);

            this.className = "Hygrometer";
            this.type = model.SensorType.HYGROMETER;
        }

        return Hygrometer;
    })(model.SubSensor);
    model.Hygrometer = Hygrometer;

    /*
     * Luxmeter-Model
     */
    var Luxmeter = (function(_super) {
        __extends(Luxmeter, _super);

    	/*
    	 * Constructor
    	 */
        function Luxmeter(id, container) {
            _super.call(this, id, 'Luxmeter', container);

            this.className = "Luxmeter";
            this.type = model.SensorType.LUXMETER;
        }

        return Luxmeter;
    })(model.SubSensor);
    model.Luxmeter = Luxmeter;

    /*
     * Pyranometer-Model
     */
    var Pyranometer = (function(_super) {
        __extends(Pyranometer, _super);

    	/*
    	 * Constructor
    	 */
        function Pyranometer(id, container) {
            _super.call(this, id, 'Pyranometer', container);

            this.className = "Pyranometer";
            this.type = model.SensorType.PYRANOMETER;
        }

        return Pyranometer;
    })(model.SubSensor);
    model.Pyranometer = Pyranometer;

    /*
     * Rain-Model
     */
    var Rain = (function(_super) {
        __extends(Rain, _super);

    	/*
    	 * Constructor
    	 */
        function Rain(id, container) {
            _super.call(this, id, 'Rain', container);

            this.className = "Rain";
            this.type = model.SensorType.RAIN;
        }

        return Rain;
    })(model.SubSensor);
    model.Rain = Rain;

    /*
     * Thermometer-Model
     */
    var Thermometer = (function(_super) {
        __extends(Thermometer, _super);

    	/*
    	 * Constructor
    	 */
        function Thermometer(id, container) {
            _super.call(this, id, 'Thermometer', container);

            this.className = "Thermometer";
            this.type = model.SensorType.THERMOMETER;
        }

        return Thermometer;
    })(model.SubSensor);
    model.Thermometer = Thermometer;

    /*
     * WindFlag-Model
     */
    var WindFlag = (function(_super) {
        __extends(WindFlag, _super);

    	/*
    	 * Constructor
    	 */
        function WindFlag(id, container) {
            _super.call(this, id, 'WindFlag', container);

            this.className = "WindFlag";
            this.type = model.SensorType.WINDFLAG;
        }

        return WindFlag;
    })(model.SubSensor);
    model.WindFlag = WindFlag;

})(model);