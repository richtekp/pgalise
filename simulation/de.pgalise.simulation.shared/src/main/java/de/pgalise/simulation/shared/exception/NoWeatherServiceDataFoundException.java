/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author richter
 */
@ApplicationException
public class NoWeatherServiceDataFoundException extends NoWeatherDataFoundException {

  private static final long serialVersionUID = 1L;

  public NoWeatherServiceDataFoundException(long timestamp) {
    super(timestamp);
  }

}
