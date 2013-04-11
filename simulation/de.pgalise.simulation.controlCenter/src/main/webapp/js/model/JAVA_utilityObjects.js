(function(model) {
	var Vehicle = (function() {
		function Vehicle(id, type, name, gpsActivated, startTime) {
			this.id = id;
			this.type = type;
			this.name = name;
			this.gpsActivated = gpsActivated;
			this.startTime = startTime;
		}

		return Vehicle;
	})();
	model.Vehicle = Vehicle;

	var Node = (function() {
		function Node(id, onStreet, onJunction, position) {
			this.id = id;
			this.onStreet = onStreet;
			this.onJunction = onJunction;
			this.position = position;
		}

		return Node;
	})();
	model.Node = Node;

	var GeoLocation = (function() {
		function GeoLocation(latitude, longitude) {
			this.latitude = {
				degree : latitude
			};
			this.longitude = {
				degree : longitude
			};
		}

		return GeoLocation;
	})();
	model.GeoLocation = GeoLocation;

	var TrafficTrip = (function() {
		function TrafficTrip(startNode, startPosition, endNode, endPosition, startTime) {
			return {
				startNode : startNode,
				startPosition : startPosition,
				targetNode : endNode,
				targetPosition : endPosition,
				startTime : startTime
			};
		}

		return TrafficTrip;
	})();
	model.TrafficTrip = TrafficTrip;
	
	var VehicleInformation = (function() {
		function VehicleInformation(vehicle, trip) {
			return {
				vehicleID : vehicle.id,
				gpsActivated : vehicle.gpsActivated,
				vehicleTypeEnum : ctrl.mainCtrl.vehicleTypeMap[vehicle.type.id],
				vehicleModelEnum : ctrl.mainCtrl.vehicleTypeMap[vehicle.type.id]+'_RANDOM',	//WorkAround!
				trip : trip,
				name : vehicle.name
			};
		}
		
		return VehicleInformation;
	})();
	model.VehicleInformation = VehicleInformation;
	
	var SimulationStartParameter = (function() {
		function SimulationStartParameter(SensorObjectIDService, UUIDService) {
			return {
		    	withSensorInterferes : false,
		    	startTimestamp : new Date(2012, 1, 27, 6, 0).getTime(),
		    	endTimestamp : new Date(2012, 1, 28, 18, 0).getTime(),
		    	interval : 10000,
		    	clockGeneratorInterval : 250,
		    	sensorUpdateSteps : 1,
		    	aggregatedWeatherDataEnabled : false,
		    	ipSimulationController : 'localhost:8080',
		    	ipTrafficController : 'localhost:8080',
		    	ipWeatherController : 'localhost:8080',
		    	ipStaticSensorController : 'localhost:8080',
		        ipEnergyController : 'localhost:8080',
		        operationCenterAddress : 'http://localhost:8080/operationCenter/OCSimulationServlet',
		        controlCenterAddress : 'http://localhost:8080/controlCenter/ControlCenter',
		        trafficServerIPList : ['localhost:8080'],
		        city : {
		        	altitude : 4,
		        	id : 1,
		        	name : 'Oldenburg (Oldb)',
		        	nearRiver : false,
		        	nearSea : false,
		        	population : 162481,
		        	rate : 0
		        },
		        sensorHelperList : [],
		        simulationEventLists : [],
		        weatherEventList : [],
		        osmAndBusstopFileData : {
		        	osmFileName : ctrl.mainCtrl.$scope.busSystems[0],
		        	busStopFileName : ctrl.mainCtrl.$scope.streetMaps[0]
		        },
		        randomDynamicSensorBundle : {
		        	randomCarAmount : 100,
		            gpsCarRatio : 1.0,
		            randomBikeAmount : 100,
		            gpsBikeRatio : 1.0,
		            randomTruckAmount : 100,
		            gpsTruckRatio : 1.0,
		            randomMotorcycleAmount : 100,
		            gpsMotorcycleRatio : 1.0,
		            usedSensorIDs : SensorObjectIDService.takenIds,
		            usedUUIDs : UUIDService.takenUUIDs
		        },
		        busRouteList : {},
		        trafficFuzzyData : {
		            updateSteps : 1,
		            tolerance : 0.1,
		            buffer : 30
		        },
		        attractionCollection : [],
		        specificUpdateSteps : {}
		    };
		};
		
		return SimulationStartParameter;
	})();
	model.SimulationStartParameter = SimulationStartParameter;
})(model);