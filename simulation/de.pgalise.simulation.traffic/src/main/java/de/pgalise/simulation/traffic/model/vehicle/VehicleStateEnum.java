/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import java.util.EnumSet;

/**
 * Status
 *
 * @author Mustafa
 * @version 1.0 (Dec 27, 2012)
 */
public enum VehicleStateEnum {
	/**
	 * Initial status, vehicle is waiting for departure.
	 */
	NOT_STARTED, /**
	 * Vehicle is currently driving.
	 */ DRIVING, /**
	 * Vehicle has arrived at its target.
	 */ REACHED_TARGET, /**
	 * Vehicle is on his way but has to wait for example on a traffic junction.
	 */ /**
	 * Paused vehicles are not allowed to register on any nodes.
	 */ PAUSED, STOPPED, /**
	 * Vehicle is trapped in a traffic rule of Marcus.
	 */ IN_TRAFFIC_RULE;
	/**
	 * Vehicles having this status are supposed to be updated when receiving an update event
	 */
	public static final EnumSet<VehicleStateEnum> UPDATEABLE_VEHICLES = EnumSet.of(VehicleStateEnum.NOT_STARTED,
		VehicleStateEnum.DRIVING,
		VehicleStateEnum.STOPPED,
		VehicleStateEnum.PAUSED);
	
}
