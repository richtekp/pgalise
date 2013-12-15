/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal.message;

import de.pgalise.simulation.shared.persistence.Identifiable;

/**
 *
 * @author richter
 */
public interface IdentifiableCCWebSocketMessage<T> extends CCWebSocketMessage<T>,Identifiable {
	
}
