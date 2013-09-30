/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.scheduler;

import java.util.EnumSet;

/**
 *
 * @author richter
 */
public enum ScheduleModus {
	READ, WRITE;
	public static final EnumSet<ScheduleModus> READ_OR_WRITE = EnumSet.of(READ,
		WRITE);
	
}
