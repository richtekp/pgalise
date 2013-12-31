/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.event;

import de.pgalise.simulation.shared.persistence.Identifiable;
import java.util.Date;

/**
 *
 * @author richter
 */
public interface Event extends Identifiable {
	/**
	 * @return type of the events this EventHandler is able to process 
	 */
	public EventType getType();
	
	public Date getCommitDateTime();
	
}
