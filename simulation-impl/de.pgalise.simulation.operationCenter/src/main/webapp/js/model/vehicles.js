/*
 * Package model
 * @author Dennis Höting
 */
(function(model) {
	/*
	 * Vehicles
 	 * @author Dennis Höting
	 */
	var Vehicle = (function() {
        /*
         * Constructor
         */
		function Vehicle(id, name, sensors) {
			this.id = id;
			this.name = name;
			this.sensors = sensors;
			this.positioningSensor = undefined;
			this.isVehicle = true;

			var _this = this;
			angular.forEach(sensors, function(sensor, id) {
				if(sensor.className.startsWith('GPS')) {
					_this.positioningSensor = sensor;
					delete _this.sensors[id];
				}
			});
		}
		
		/*
		 * Get latitude
		 */
		Vehicle.prototype.getLatitude = function() {
			this.positioningSensor.latitude;
		};
		
		/*
		 * Get longitude
		 */
		Vehicle.prototype.getLongitude = function() {
			this.positioningSensor.longitude;
		};
		
		return Vehicle;
	})();
	model.Vehicle = Vehicle;
	
    /*
     * Truck-Model
     * Parent: Vehicle
 	 * @author Dennis Höting
     */
    var Car = (function(_super) {
        __extends(Car, _super);

        /*
         * Constructor
         */
        function Car(id, sensors) {
        	_super.call(this, id, 'Car_'+id, sensors);
            this.className = 'Car';
            this.type = model.VehicleType.CAR;
        	this.icon = 'img/car.png';
        }

        return Car;
    })(model.Vehicle);
    model.Car = Car;
    
    /*
     * Truck-Model
     * Parent: Vehicle
 	 * @author Dennis Höting
     */
    var Truck = (function(_super) {
        __extends(Truck, _super);

        /*
         * Constructor
         */
        function Truck(id, sensors) {
        	_super.call(this, id, 'Truck_'+id, sensors);
            this.className = 'Truck';
            this.type = model.VehicleType.TRUCK;
        	this.icon = 'img/truck.png';
        }

        return Truck;
    })(model.Vehicle);
    model.Truck = Truck;
    
    
    /*
     * Motorcycle-Model
     * Parent: Vehicle
 	 * @author Dennis Höting
     */
    var Motorcycle = (function(_super) {
        __extends(Motorcycle, _super);

        /*
         * Constructor
         */
        function Motorcycle(id, sensors) {
        	_super.call(this, id, 'Motorcycle_'+id, sensors);
            this.className = 'Motorcycle';
            this.type = model.VehicleType.MOTORCYCLE;
        	this.icon = 'img/motorcycle.png';
        }

        return Motorcycle;
    })(model.Vehicle);
    model.Motorcycle = Motorcycle;
    
    
    /*
     * Bike-Model
     * Parent: Vehicle
 	 * @author Dennis Höting
     */
    var Bike = (function(_super) {
        __extends(Bike, _super);

        /*
         * Constructor
         */
        function Bike(id, sensors) {
        	_super.call(this, id, 'Bike_'+id, sensors);
            this.className = 'Bike';
            this.type = model.VehicleType.BIKE;
        	this.icon = 'img/bike.png';
        }

        return Bike;
    })(model.Vehicle);
    model.Bike = Bike;
    
    
    /*
     * Bus-Model
     * Parent: Vehicle
 	 * @author Dennis Höting
     */
    var Bus = (function(_super) {
        __extends(Bus, _super);

        /*
         * Constructor
         */
        function Bus(id, sensors) {
        	_super.call(this, id, 'Bus_'+id, sensors);
            this.className = 'Bus';
            this.type = model.VehicleType.BUS;
        	this.icon = 'img/bus.png';
        }

        return Bus;
    })(model.Vehicle);
    model.Bus = Bus;
})(model);