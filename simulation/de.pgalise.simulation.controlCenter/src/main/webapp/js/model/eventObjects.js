/**
 * This Class contains all the simulation events which can be set.  
 * @param {Object} model which contains the event
 * @author Dennis Höting
 */
(function(model) {
	var Path = (function() {
		function Path(id, start, end, idService) {
		    this.id = id;
		    this.start = start;
		    this.end = end;
		    this.vehicles = {};
		    this.type = model.EventType.PATH;
		    this.idService = idService;

		    this.current = {};
		    this.current.id = this.idService.peek();
		    this.current.name = 'Custom'+this.current.id.substring(this.current.id.length-7);
		    this.current.type = undefined;
		    this.current.gpsActivated = true;
		    this.current.wayThereTimestamp = ctrl.mainCtrl.$scope.startParameter.startTimestamp+24*60*60*1000;
			this.current.wayThereHours = parseInt(new Date(this.current.wayThereTimestamp).format('HH'));
			this.current.wayThereMinutes = parseInt(new Date(this.current.wayThereTimestamp).format('MM'));
			this.current.wayThereDate = this.current.wayThereTimestamp
				- this.current.wayThereHours*1000*60*60
				- this.current.wayThereMinutes*1000*60;
		}
		
		Path.prototype.addVehicle = function(vehicle) {
		    this.vehicles[vehicle.id] = vehicle;
		    
		    this.current.id = this.idService.peek();
		    this.current.name = 'Custom'+this.current.id.substring(this.current.id.length-7);
		};
		
		Path.prototype.deleteVehicle = function(vehicleId) {
			delete this.vehicles[vehicleId];
		};
		
		return Path;
	})();
	model.Path = Path;
	
    var PathStart = (function(_super) {
        __extends(PathStart, _super);

        function PathStart(id, latitude, longitude, name) {
            _super.call(this, id, latitude, longitude, name);
            this.className = 'PathStart';
            this.partner = undefined;
            this.isStart = true;

            this.icon = 'img/flag_green2.png';
            this.type = model.EventType.PATHSTART;
        }

        PathStart.prototype.getPathId = function() {
            return this.id + '_' + this.partner.id;
        };

        return PathStart;
    })(model.StaticMarker);
    model.PathStart = PathStart;

    var PathEnd = (function(_super) {
        __extends(PathEnd, _super);

        function PathEnd(id, latitude, longitude, name, partner) {
            _super.call(this, id, latitude, longitude, name);
            this.className = 'PathEnd';
            this.partner = undefined;
            this.isStart = false;

            this.icon = 'img/flag_red2.png';

            // Bind the two flags
            this.partner = partner;
            this.partner.partner = this;

            this.type = model.EventType.PATHEND;
        }


        PathEnd.prototype.getPathId = function() {
            return this.partner.id + '_' + this.id;
        };

        return PathEnd;
    })(model.StaticMarker);
    model.PathEnd = PathEnd;

    var Attraction = (function(_super) {
        __extends(Attraction, _super);

        function Attraction(id, latitude, longitude, name) {
            _super.call(this, id, latitude, longitude, name, 0);
            this.className = 'Attraction';
            this.start = new Date(ctrl.mainCtrl.$scope.startParameter.startTimestamp);
            this.duration = 1000*60*60;
            this.expectedVisitors = 100;
            this.icon = 'img/attractionM.png';
            this.type = model.EventType.ATTRACTION;
            
            this.randomCars = 0;
            this.randomCarGPSRatio = 100;
            this.randomTrucks = 0;
            this.randomTruckGPSRatio = 100;
            this.randomMotorcycles = 0;
            this.randomMotorcycleGPSRatio = 100;
            this.randomBikes = 0;
            this.randomBikeGPSRatio = 100;

            this.startTimestamp = ctrl.mainCtrl.$scope.startParameter.startTimestamp+24*60*60*1000;
            this.startHours = parseInt(new Date(this.startTimestamp).format('HH'));
            this.startMinutes = parseInt(new Date(this.startTimestamp).format('MM'));
			this.startDate = this.startTimestamp
			- this.startHours*1000*60*60
			- this.startMinutes*1000*60;
            this.durationHours = 3;
            this.durationMinutes = 0;
			this.durationMillis = this.durationHours*60*60*1000 + this.durationMinutes*60*1000;
        }

        return Attraction;
    })(model.StaticMarker);
    model.Attraction = Attraction;

    var StreetBlock = (function(_super) {
        __extends(StreetBlock, _super);

        function StreetBlock(id, latitude, longitude, name) {
            _super.call(this, id, latitude, longitude, name, 0);
            this.className = 'StreetBlock';
            this.start = new Date();
            this.duration = 1000*60*60*24;
            this.icon = 'img/street_blockM.png';
            this.type = model.EventType.STREETBLOCK;

            this.startTimestamp = ctrl.mainCtrl.$scope.startParameter.startTimestamp+24*60*60*1000;
            this.startHours = parseInt(new Date(this.startTimestamp).format('HH'));
            this.startMinutes = parseInt(new Date(this.startTimestamp).format('MM'));
			this.startDate = this.startTimestamp
			- this.startHours*1000*60*60
			- this.startMinutes*1000*60;
            this.durationHours = 3;
            this.durationMinutes = 0;
			this.durationMillis = this.durationHours*60*60*1000 + this.durationMinutes*60*1000;
        }

        return StreetBlock;
    })(model.StaticMarker);
    model.StreetBlock = StreetBlock;

    var EnergyEvent = (function(_super) {
        __extends(EnergyEvent, _super);

        function EnergyEvent(id, latitude, longitude, name) {
            _super.call(this, id, latitude, longitude, name, 0);
            this.className = 'EnergyEvent';
            this.ratio = 500;//m
            this.value = 100;//%
            this.start = new Date();
            this.duration = 1000*60*60;
            this.icon = 'img/street_blockM.png';
            this.type = model.EventType.ENERGYEVENT;

            this.startTimestamp = ctrl.mainCtrl.$scope.startParameter.startTimestamp+24*60*60*1000;
            this.startHours = parseInt(new Date(this.startTimestamp).format('HH'));
            this.startMinutes = parseInt(new Date(this.startTimestamp).format('MM'));
			this.startDate = this.startTimestamp
			- this.startHours*1000*60*60
			- this.startMinutes*1000*60;
            this.durationHours = 3;
            this.durationMinutes = 0;
			this.durationMillis = this.durationHours*60*60*1000 + this.durationMinutes*60*1000;
        }
        
		EnergyEvent.prototype.getCircleId = function() {
        	return this.id + 'C';
        };

        return EnergyEvent;
    })(model.StaticMarker);
    model.EnergyEvent = EnergyEvent;
})(model); 