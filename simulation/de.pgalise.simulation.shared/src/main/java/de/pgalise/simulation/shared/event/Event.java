/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.event;

import java.util.Date;

/**
 *
 * @author richter
 */
public interface Event {
	
	Long getId();
	
	/**
	 * @return type of the events this EventHandler is able to process 
	 */
	EventType getType();
	
	Date getCommitDateTime();
}
