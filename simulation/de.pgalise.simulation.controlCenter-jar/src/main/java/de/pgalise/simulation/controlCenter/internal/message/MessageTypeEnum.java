/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal.message;

/**
 * Self-made enum (appropriate for JSON transport, including fixed int bindings
 * New message types need to be added here and in the JavaScript client code.
 * @author dhoeting
 * @author Timo
 */
public enum MessageTypeEnum {
	
	ERROR(-1),
	ON_CONNECT(0), // By Server
	SIMULATION_START(1), // By Server
	SIMULATION_PAUSED(2), // By Server
	SIMULATION_STOP(3), // By Server
	SIMULATION_RESUMED(4), // By Server
	ACCESS_DENIED(5), // By Server
	SIMULATION_START_PARAMETER(10), // By Client and Server
	SIMULATION_EXPORT_PARAMETER(11), // By Client
	SIMULATION_EXPORTET(12), // By Server
	OSM_AND_BUSSTOP_FILE_MESSAGE(13), // By Client
	ASK_FOR_VALID_NODE(14), // By Client
	VALID_NODE(15), // by Server
	OSM_PARSED(16), // by Server
	LOAD_SIMULATION_START_PARAMETER(17), // By Client
	SIMULATION_EVENT_LIST(18), // By Client
	IMPORT_XML_START_PARAMETER(20), // By Client
	CREATE_SENSORS_MESSAGE(21), // By Client
	DELETE_SENSORS_MESSAGE(22), // By Client
	CREATE_RANDOM_VEHICLES(23), // By Client
	USED_IDS_MESSAGE(24), // By Server
	CREATE_ATTRACTION_EVENTS_MESSAGE(25), // By Client
	SIMULATION_RUNNING(26), // By Server
	SIMULATION_STOPPED(27), // By Server
	SIMULATION_UPDATE(28), // By Server
	GENERIC_NOTIFICATION_MESSAGE(100); // By Server
	
	private int value;
	
	MessageTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
