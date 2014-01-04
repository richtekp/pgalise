/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.event;

import java.io.Serializable;

/**
 *
 * @author richter
 */
public interface EventType extends Serializable {
	
	Class<?> getImplementationClass();
}
