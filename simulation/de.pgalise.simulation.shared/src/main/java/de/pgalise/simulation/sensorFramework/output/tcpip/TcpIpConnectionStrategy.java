/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.sensorFramework.output.tcpip;

/**
 *
 * @author richter
 */
public interface TcpIpConnectionStrategy {

  void handleOpen(final TcpIpOutput sensorOutput);

  void handleClose(final TcpIpOutput sensorOutput);

  /**
   * Returns the enumeration to the {@link TcpIpConnectionStrategy}
   * implementation.
   *
   * @return the enumeration to the {@link TcpIpConnectionStrategy}
   * implementation
   */
  TcpIpConnectionStrategyEnum getConnectionStrategyEnum();

  /**
   * This method is invoked before the connection is opened. <br>
   * It can be used by subclasses to implement their own logic
   */
  void beforeOpenConnection();

  /**
   * This method is invoked after the connection is opened. <br>
   * It can be used by subclasses to implement their own logic
   */
  void afterOpenConnection();

  /**
   * This method is invoked before the connection is flushed. <br>
   * It can be used by subclasses to implement their own logic
   */
  void beforeFlushConnection();

  /**
   * This method is invoked after the connection is flushed. <br>
   * It can be used by subclasses to implement their own logic
   */
  void afterFlushConnection();

  /**
   * This method is invoked before the connection is closed. <br>
   * It can be used by subclasses to implement their own logic
   */
  void beforeCloseConnection();

  /**
   * This method is invoked after the connection is closed. <br>
   * It can be used by subclasses to implement their own logic
   */
  void afterCloseConnection();
}
