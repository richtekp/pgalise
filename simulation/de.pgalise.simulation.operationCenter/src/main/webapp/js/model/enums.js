/*
 * Package model
 * @author Dennis Höting
 */
(function(model) {
	/*
	 * SensorTypes
	 * @author Dennis Höting
	 */
	var SensorType = {
		ANEMOMETER : 0,
		BAROMETER : 1,
		GPS_BIKE : 2,
		GPS_BUS : 3,
		GPS_CAR : 4,
		HYGROMETER : 5,
		INDUCTIONLOOP : 6,
		INFRARED : 7,
		LUXMETER : 8,
		PHOTOVOLTAIK : 9,
		PYRANOMETER : 10,
		RAIN : 11,
		SMARTMETER : 12,
		THERMOMETER : 13,
		TRAFFICLIGHT : 14,
		WINDFLAG : 15,
		WINDPOWERSENSOR : 16,
		TOPORADAR : 18,
		GPS_TRUCK : 19,
		GPS_MOTORCYCLE : 20,
		TRAFFIC_LIGHT_INTERSECTION : 21,
		WEATHER_STATION : 50
	};
	model.SensorType = SensorType;

	/*
	 * MessageTypes
	 * @author Dennis Höting
	 */
	var MessageType = {
		ERROR : -1,
		SIMULATION_START : 1, // Server only
		SIMULATION_STOP : 3, // Server only
		SIMULATION_INIT : 5, // Server only
		SENSOR_DATA : 13, // Server only
		GPS_SENSOR_TIMEOUT : 18, // Server only
		NEW_SENSORS : 19, // Server only
		REMOVE_SENSORS : 20, // Server only
		GATE_MESSAGE : 21, // Client only
		GENERIC_NOTIFICATION_MESSAGE : 22, // Server only
		ON_CONNECT : 23, // Server only
		NEW_VEHICLE_MESSAGE : 24, // Server only
		DEFAULT_MESSAGE_ID : -99	// Server only
	};
	model.MessageType = MessageType;

	/*
	 * VehicleTypes
	 * @author Dennis Höting
	 */
	var VehicleType = {
		CAR : 0,
		MOTORCYCLE : 1,
		BIKE : 2,
		TRUCK : 3,
		BUS : 4
	};
	model.VehicleType = VehicleType;

	/*
	 * TrafficLightStates
	 * @author Dennis Höting
	 */
	var TrafficLightStates = {
		BLINKING : 0,
		GREEN : 1,
		RED : 2,
		RED_YELLOW : 3,
		YELLOW : 4
	};
	model.TrafficLightStates = TrafficLightStates;
})(model); 