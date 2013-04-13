/**
 * Simulation Event Objects for the client-server communication
 * @author Dennis Höting
 */
(function(model) {
	var SimulationEvent = (function() {
		function SimulationEvent(id, eventType) {
			return {
				id : id,
				eventType : eventType
			};
		}

		return SimulationEvent;
	})();
	model.SimulationEvent = SimulationEvent;
		
	var SimulationEventList = (function() {
		function SimulationEventList(eventList, id, timestamp) {
			return {
				eventList : eventList,
				id : id,
				timestamp : timestamp
			};
		}

		return SimulationEventList;
	})();
	model.SimulationEventList = SimulationEventList;

	var AttractionData = (function() {
		function AttractionData(attraction, takenIds, takenUUIDs) {
			var randomVehicleBundle = {
				randomCarAmount : attraction.randomCars,
				randomBikeAmount : attraction.randomBikes,
				randomTruckAmount : attraction.randomTrucks,
				randomMotorcycleAmount : attraction.randomMotorcycles,
				gpsCarRatio : attraction.randomCarGPSRatio,
				gpsBikeRatio : attraction.randomBikeGPSRatio,
				gpsTruckRatio : attraction.randomTruckGPSRatio,
				gpsMotorcycleRatio : attraction.randomMotorcycleGPSRatio,
				usedSensorIDs : takenIds,
				usedUUIDs : takenUUIDs
			};

			return {
				id : attraction.id,
				attractionStartTimestamp : attraction.startTimestamp,
				attractionEndTimestamp : attraction.startTimestamp + attraction.durationMillis,
				attractionPoint : new model.GeoLocation(attraction.latitude, attraction.longitude),
				nodeID : attraction.nodeId,
				randomVehicleBundle : randomVehicleBundle
			};
		}

		return AttractionData;
	})();
	model.AttractionData = AttractionData;

	var CreateVehiclesEvent = (function() {
		function CreateVehiclesEvent(path) {
			var vehicleList = [];
			for (var vid in path.vehicles) {
				var vehicle = path.vehicles[vid];
				vehicleList.push({
					sensorHelpers : [
						{
							position : new model.GeoLocation(path.start.latitude, path.start.longitude),
							sensorID : path.idService.get(),
							sensorType : model.VehicleTypeSensorTypeMap[vehicle.type],
							updateSteps : ctrl.mainCtrl.$scope.startParameter.sensorUpdateSteps,
							sensorInterfererType : [],
							nodeId : path.start.nodeId
						}
					],
					vehicleInformation : new model.VehicleInformation(vehicle, new model.TrafficTrip(path.start.nodeId, new model.GeoLocation(path.start.latitude, path.start.longitude), path.end.nodeId, new model.GeoLocation(path.end.latitude, path.end.longitude), vehicle.startTime))
					});
			}

			return {
				id : path.id,
				eventType : ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CREATE_RANDOM_VEHICLES_EVENT],
				vehicles : vehicleList
			};
		}

		return CreateVehiclesEvent;
	})();
	model.CreateVehiclesEvent = CreateVehiclesEvent;

	var RoadBarrierTrafficEvent = (function() {
		function RoadBarrierTrafficEvent(streetBlock) {
			return {
				id : streetBlock.id,
				eventType : ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.ROAD_BARRIER_TRAFFIC_EVENT],
				roadBarrierStartTimestamp : streetBlock.startTimestamp,
				roadBarrierEndTimestamp : streetBlock.startTimestamp + streetBlock.durationMillis,
				roadBarrierPoint : new model.GeoLocation(streetBlock.latitude, streetBlock.longitude),
				nodeID : streetBlock.nodeId
			};
		}

		return RoadBarrierTrafficEvent;
	})();
	model.RoadBarrierTrafficEvent = RoadBarrierTrafficEvent;

	var PercentageChangeEnergyEvent = (function() {
		function PercentageChangeEnergyEvent(energyEvent) {
			return {
				id : energyEvent.id,
				eventType : ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT],
				position : new model.GeoLocation(energyEvent.latitude, energyEvent.longitude),
				measureRadiusInMeter : energyEvent.ratio,
				startTimestamp : energyEvent.startTimestamp,
				endTimestamp : energyEvent.startTimestamp + energyEvent.durationMillis,
				percentage : energyEvent.value / 100
			};
		}

		return PercentageChangeEnergyEvent;
	})();
	model.PercentageChangeEnergyEvent = PercentageChangeEnergyEvent;

	var ChangeWeatherEvent = (function() {
		function ChangeWeatherEvent(id, event, timestamp, value, duration) {
			return {
				id : id,
				eventType : ctrl.mainCtrl.simulationEventTypeMap[model.SimulationEvents.CHANGE_WEATHER_EVENT],
				event : ctrl.mainCtrl.weatherEventTypeMap[event],
				timestamp : timestamp,
				value : value,
				duration : duration
			};
		}

		return ChangeWeatherEvent;
	})();
	model.ChangeWeatherEvent = ChangeWeatherEvent;
})(model); 