/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.event;

/**
 *
 * @param <T> 
 * @author richter
 */
public interface Event<T extends EventType>  {
	/**
	 * @return type of the events this EventHandler is able to process 
	 */
	public T getType();
	
}
