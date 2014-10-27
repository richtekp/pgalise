/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.service;

/**
 * Represents a controller's possible states.
 *
 * @author mustafa
 *
 */
public enum ControllerStatusEnum {

  /**
   * means that the controller is ready to be initialized
   */
  INIT,
  /**
   * means that the initialization of the controller was successful
   */
  INITIALIZED, STARTED, STOPPED

}
