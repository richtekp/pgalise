/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.service.internal.event;

import de.pgalise.simulation.service.event.EventHandler;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.event.EventTypeEnum;
import javax.ejb.EJB;

/**
 *
 * @param <E> 
 * @param <T> 
 * @author richter
 */
public abstract class AbstractEventHandler<E extends Event> implements EventHandler<E> {
	
	private EventType targetEventType;

	public AbstractEventHandler(EventType targetEventType) {
		this.targetEventType = targetEventType;
	}
	
	@Override
	public EventType getTargetEventType() {
		return targetEventType;
	}
	
}
