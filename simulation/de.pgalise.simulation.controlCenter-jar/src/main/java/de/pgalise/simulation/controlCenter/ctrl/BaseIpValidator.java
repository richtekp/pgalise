/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import java.io.Serializable;


public class BaseIpValidator implements Serializable {

	private static final String IP_ADDRESS_PATTERN
		= "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
  private static final long serialVersionUID = 1L;
  
  public boolean validIp(String value) {
    return value.matches(IP_ADDRESS_PATTERN);
  }
}
