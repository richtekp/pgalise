/*
 * Package model
 */
(function(model) {
	/*
	 * Generic GPS sensor
	 * Parent: DynamicMarker
	 * Children: Specific GPS sensors
	 */
	var GPSSensor = (function(_super) {
		__extends(GPSSensor, _super);
		
		/*
		 * Constructor
		 */
		function GPSSensor(id, latitude, longitude, name, updateSteps) {
			_super.call(this, id, latitude, longitude, name, updateSteps);
			
			this.className = 'GPSSensor';
		}
		
		return GPSSensor;
	})(model.DynamicMarker);
	model.GPSSensor = GPSSensor;
	
	/*
	 * GPS Sensor for cars
     * Parent: GPSSensor
	 */
	var GPSCar = (function(_super) {
        __extends(GPSCar, _super);
        
        /*
         * Constructor
         */
        function GPSCar(id, latitude, longitude, vehicleId, updateSteps) {
        	_super.call(this, id, latitude, longitude, 'GPS_Car_'+id, updateSteps);
        	
        	this.className = 'GPSCar';
        	this.type = model.SensorType.GPS_CAR;
        	this.vehicleId = vehicleId;
        	this.icon = GPSCar.icon;
        }
        
        GPSCar.icon = 'img/car.png';
        
        return GPSCar;
	})(model.GPSSensor);
	model.GPSCar = GPSCar;
	
	/*
	 * GPS Sensor for trucks
     * Parent: GPSSensor
	 */
	var GPSTruck = (function(_super) {
        __extends(GPSTruck, _super);
        
        /*
         * Constructor
         */
        function GPSTruck(id, latitude, longitude, vehicleId, updateSteps) {
        	_super.call(this, id, latitude, longitude, 'GPS_Truck_'+id, updateSteps);
        	
        	this.className = 'GPSTruck';
        	this.type = model.SensorType.GPS_TRUCK;
        	this.vehicleId = vehicleId;
        	this.icon = GPSTruck.icon;
        }

        GPSTruck.icon = 'img/truck.png';
    	
        return GPSTruck;
	})(model.GPSSensor);
	model.GPSTruck = GPSTruck;
	
	/*
	 * GPS Sensor for motorcycles
     * Parent: GPSSensor
	 */
	var GPSMotorcycle = (function(_super) {
        __extends(GPSMotorcycle, _super);
        
        /*
         * Constructor
         */
        function GPSMotorcycle(id, latitude, longitude, vehicleId, updateSteps) {
        	_super.call(this, id, latitude, longitude, 'GPS_Motorcycle_'+id, updateSteps);
        	
        	this.className = 'GPSMotorcycle';
        	this.type = model.SensorType.GPS_MOTORCYCLE;
        	this.vehicleId = vehicleId;
        	this.icon = GPSMotorcycle.icon;
        }
        
        GPSMotorcycle.icon = 'img/motorcycle.png';
        
        return GPSMotorcycle;
	})(model.GPSSensor);
	model.GPSMotorcycle = GPSMotorcycle;
	
	/*
	 * GPS Sensor for bike
     * Parent: GPSSensor
	 */
	var GPSBike = (function(_super) {
        __extends(GPSBike, _super);
        
        /*
         * Constructor
         */
        function GPSBike(id, latitude, longitude, vehicleId, updateSteps) {
        	_super.call(this, id, latitude, longitude, 'GPS_Bike_'+id, updateSteps);
        	
        	this.className = 'GPSBike';
        	this.type = model.SensorType.GPS_BIKE;
        	this.vehicleId = vehicleId;
        	this.icon = GPSBike.icon;
        }
        
        GPSBike.icon = 'img/bike.png';
        
        return GPSBike;
	})(model.GPSSensor);
	model.GPSBike = GPSBike;
	
	/*
	 * GPS Sensor for bus
     * Parent: GPSSensor
	 */
	var GPSBus = (function(_super) {
        __extends(GPSBus, _super);
        
        /*
         * Constructor
         */
        function GPSBus(id, latitude, longitude, vehicleId, updateSteps) {
        	_super.call(this, id, latitude, longitude, 'GPS_Bus_'+id, updateSteps);
        	
        	this.className = 'GPSBus';
        	this.type = model.SensorType.GPS_BUS;
        	this.vehicleId = vehicleId;
        	this.icon = GPSBus.icon;
        }
        
        GPSBus.icon = 'img/bus.png';
        
        return GPSBus;
	})(model.GPSSensor);
	model.GPSBus = GPSBus;
	
	/*
	 * Infrared-Model
     * Parent: StaticMarker
	 */
    var Infrared = (function(_super) {
        __extends(Infrared, _super);

        /*
         * Constructor
         */
        function Infrared(id, vehicleId, updateSteps) {
            _super.call(this, id, 0, 0, 'Infrared', updateSteps);

            this.className = "Infrared";
            this.type = model.SensorType.INFRARED;
        	this.vehicleId = vehicleId;
        }

        return Infrared;
    })(model.StaticMarker);
    model.Infrared = Infrared;
	
    /*
     * InductionLoop-Model
     * Parent: StaticMarker
     */
    var InductionLoop = (function(_super) {
        __extends(InductionLoop, _super);

        /*
         * Constructor
         */
        function InductionLoop(id, latitude, longitude, updateSteps) {
            _super.call(this, id, latitude, longitude, 'InductionLoop_'+id, updateSteps);

            this.className = 'InductionLoop';
            this.type = model.SensorType.INDUCTIONLOOP;
            this.icon = InductionLoop.icon;
            this.sumValue = 0;
        }
        InductionLoop.icon = 'img/induction_loop.png';
        
        InductionLoop.prototype.setValue = function(value) {
            this.value = value;
            this.sumValue += value;
            
            this.lastChange = new Date().getTime();
        };
        
        InductionLoop.prototype.getSumValue = function() {
        	return this.sumValue;
        };

        return InductionLoop;
    })(model.StaticMarker);
    model.InductionLoop = InductionLoop;

    /*
     * SolarCollector-Model
     * Parent: StaticMarker
     */
    var Photovoltaik = (function(_super) {
        __extends(Photovoltaik, _super);

        /*
         * Constructor
         */
        function Photovoltaik(id, latitude, longitude, area, updateSteps) {
            _super.call(this, id, latitude, longitude, 'Photovoltaik_'+id, updateSteps);

            this.className = 'Photovoltaik';
            this.area = area;
            this.type = model.SensorType.PHOTOVOLTAIK;
            this.icon = Photovoltaik.icon;
        }
        Photovoltaik.icon = 'img/solar_power.png';

        return Photovoltaik;
    })(model.StaticMarker);
    model.Photovoltaik = Photovoltaik;

    /*
     * SmartMeter-Model
     */
    var SmartMeter = (function(_super) {
        __extends(SmartMeter, _super);

        /*
         * Constructor
         */
        function SmartMeter(id, latitude, longitude, measureRadiusInMeter, updateSteps) {
            _super.call(this, id, latitude, longitude, 'SmartMeter'+id, updateSteps);

            this.className = "SmartMeter";
            this.measureRadiusInMeter = measureRadiusInMeter;
            this.type = model.SensorType.SMARTMETER;
            this.icon = SmartMeter.icon;
        }
        SmartMeter.icon = 'img/smart_meter.png';

        return SmartMeter;
    })(model.StaticMarker);
    model.SmartMeter = SmartMeter;

    /*
     * WindPowerPlant-Model
     * Parent: StaticMarker
     */
    var WindPowerSensor = (function(_super) {
        __extends(WindPowerSensor, _super);

        /*
         * Constructor
         */
        function WindPowerSensor(id, latitude, longitude, activityValue, rotorLength, updateSteps) {
            _super.call(this, id, latitude, longitude, 'WindPowerSensor_'+id, updateSteps);

            this.className = "WindPowerplant";
            this.activityValue = activityValue;
            this.rotorLength = rotorLength;
            this.type = model.SensorType.WINDPOWERSENSOR;
            this.icon = WindPowerSensor.icon;
        }
        WindPowerSensor.icon = 'img/wind_power_plant.png';

        return WindPowerSensor;
    })(model.StaticMarker);
    model.WindPowerSensor = WindPowerSensor;

    /*
     * TopoRadar-Model
     * Parent: StaticMarker
     */
    var TopoRadar = (function(_super) {
        __extends(TopoRadar, _super);

        /*
         * Constructor
         */
        function TopoRadar(id, latitude, longitude, updateSteps) {
            _super.call(this, id, latitude, longitude, 'TopoRadar_'+id, updateSteps);

            this.className = "TopoRadar";
            this.type = model.SensorType.TOPORADAR;
            this.icon = TopoRadar.icon;
            this.axleCount = 0;
            this.length = 0;
            this.axialDistance1 = 0;
            this.axialDistance2 = 0;
        }
        TopoRadar.icon = 'img/topo_radar.png';
        
        TopoRadar.prototype.setValues = function(axleCount, length, axialDistance1, axialDistance2) {
        	this.axleCount = axleCount;
        	this.length = length;
        	this.axialDistance1 = axialDistance1;
        	this.axialDistance2 = axialDistance2;
        };

        return TopoRadar;
    })(model.StaticMarker);
    model.TopoRadar = TopoRadar;
})(model);
