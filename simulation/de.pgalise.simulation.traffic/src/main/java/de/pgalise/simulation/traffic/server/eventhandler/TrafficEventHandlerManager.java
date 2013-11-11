/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.service.event.EventHandlerManager;

/**
 *
 * @param <H> 
 * @param <F> 
 * @param <N> 
 * @param <E> 
 * @param <D> 
 * @param <V> 
 * @author richter
 */
public interface TrafficEventHandlerManager<
	H extends TrafficEventHandler<F>, 
	F extends TrafficEvent
> extends EventHandlerManager<H, F> {
	
}
