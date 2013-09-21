/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.service.event.EventHandlerManager;

/**
 *
 * @param <H> 
 * @param <E> 
 * @param <T> 
 * @author richter
 */
public interface TrafficEventHandlerManager<H extends TrafficEventHandler<E>, E extends TrafficEvent> extends EventHandlerManager<H, E> {
	
}
