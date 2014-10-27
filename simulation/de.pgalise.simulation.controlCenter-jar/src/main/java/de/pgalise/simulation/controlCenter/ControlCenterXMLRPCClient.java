/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter;

/**
 *
 * @author richter
 */
public interface ControlCenterXMLRPCClient {
  
  /**
   * invoked by the simulation in order to be able to let PGALISE 
   * push data into a client (you might e.g. want to implement this in a 
   * storm program)
   */
  void simulationUpdate();
}
