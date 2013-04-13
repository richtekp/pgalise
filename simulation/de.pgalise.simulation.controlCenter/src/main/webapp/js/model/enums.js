(function(model) {
	/**
	 * Message Types
	 * @author Dennis Höting
	 */
	var MessageType = {
		ERROR : -1,
		ON_CONNECT : 0,							// by Server
		SIMULATION_START : 1,					// by Server 
		SIMULATION_PAUSED : 2,					// by Server 
		SIMULATION_STOP : 3,					// by Server 
		SIMULATION_RESUMED : 4,					// by Server 
		ACCESS_DENIED : 5,						// by Server 
		SIMULATION_START_PARAMETER : 10,		// by Client and Server
		SIMULATION_EXPORT_PARAMETER : 11,		// by Client
		SIMULATION_EXPORTET : 12,				// by Server
		OSM_AND_BUSSTOP_FILE_MESSAGE : 13, 		// by Client
		ASK_FOR_VALID_NODE : 14,				// by Client
		VALID_NODE : 15,						// by Server
		OSM_PARSED : 16,						// by Server
		LOAD_SIMULATION_START_PARAMETER : 17,	// By Client
		SIMULATION_EVENT_LIST : 18,				// By Client
		IMPORT_XML_START_PARAMETER : 20,		// By Client
		CREATE_SENSORS_MESSAGE : 21,			// By Client
		DELETE_SENSORS_MESSAGE : 22,			// By Client
		CREATE_RANDOM_VEHICLES : 23,			// By Client
		USED_IDS_MESSAGE : 24,					// By Server
		CREATE_ATTRACTION_EVENTS_MESSAGE : 25,	// By Client
		SIMULATION_RUNNING : 26,
		SIMULATION_STOPPED : 27,
		SIMULATION_UPDATE : 28,				// By Server
		GENERIC_NOTIFICATION_MESSAGE : 100		// By Server
	};
	model.MessageType = MessageType;
		
 	/**
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
        TRAFFICLIGHT_SENSOR : 14,
        WINDFLAG : 15,
        WINDPOWERSENSOR : 16,
        WEATHER_STATION : 17,
        TOPORADAR : 18,
        GPS_TRUCK : 19,
        GPS_MOTORCYCLE : 20,
        TRAFFIC_LIGHT_INTERSECTION : 21
    };
    model.SensorType = SensorType;
		
 	/**
	 * SensorTypes
	 * @author Dennis Höting
	 */
    var VehicleModel = {
        BIKE_RANDOM : 4,
        BUS_RANDOM : 10,
        CAR_RANDOM : 19,
        MOTORCYCLE_RANDOM : 21,
        TRUCK_RANDOM : 27
    };
    model.VehicleModel = VehicleModel;

 	/**
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
    
    var VehicleTypeSensorTypeMap = {};
    VehicleTypeSensorTypeMap[model.VehicleTypeCAR] = model.SensorType.GPS_CAR;
    VehicleTypeSensorTypeMap[model.VehicleTypeMOTORCYCLE] = model.SensorType.GPS_MOTORCYCLE;
    VehicleTypeSensorTypeMap[model.VehicleTypeBIKE] = model.SensorType.GPS_BIKE;
    VehicleTypeSensorTypeMap[model.VehicleTypeTRUCK] = model.SensorType.GPS_TRUCK;
    VehicleTypeSensorTypeMap[model.VehicleTypeBUS] = model.SensorType.GPS_BUS;
    
    model.VehicleTypeSensorTypeMap = VehicleTypeSensorTypeMap;
    
 	/**
	 * EventTypes
	 * @author Dennis Höting
	 */
    var EventType = {
        PATH : 100,
        PATHSTART : 101,
        PATHEND : 102,
        ATTRACTION : 103,
        STREETBLOCK : 104,
        ENERGYEVENT : 105
    };
    model.EventType = EventType;
    
 	/**
	 * WeatherEvents
	 * @author Dennis Höting
	 */
	var WeatherEvents = {
		USE_CITY_CLIMATE : 0,
		USE_REFERENCE_CITY : 1,
		COLDDAY : 2,
		HOTDAY : 3,
		RAINDAY : 4,
		STORMDAY : 5
	};
	model.WeatherEvents = WeatherEvents;
	
 	/**
	 * SimulationEvents
	 * @author Dennis Höting
	 */
	var SimulationEvents = {
		CREATE_RANDOM_VEHICLES_EVENT : 0,
		CREATE_BUSSES_EVENT : 1,
		CREATE_RANDOM_BUSSES_EVENT : 2,
		CREATE_VEHICLES_EVENT : 3,
		DELETE_VEHICLES_EVENT : 4,
		CHANGE_WEATHER_EVENT : 5,
		NEW_DAY_EVENT : 6,
		NULL_EVENT : 11,
		PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT : 12,
		ATTRACTION_TRAFFIC_EVENT : 13,
		ROAD_BARRIER_TRAFFIC_EVENT : 14
	};
	model.SimulationEvents = SimulationEvents;
})(model);