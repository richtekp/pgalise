/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.sensorFramework.output.tcpip;

/**
 * Abstract class for strategies to connect to an address
 *
 * @author Marcus
 */
public abstract class AbstractTcpIpConnectionStrategy implements
  TcpIpConnectionStrategy {

  /**
   * Protected constructor used for code style issues
   */
  protected AbstractTcpIpConnectionStrategy() {
  }

  /**
   * This method is invoked before the connection is opened. <br>
   * It can be used by subclasses to implement their own logic
   */
  @Override
  public void beforeOpenConnection() {
  }

  /**
   * This method is invoked after the connection is opened. <br>
   * It can be used by subclasses to implement their own logic
   */
  @Override
  public void afterOpenConnection() {
  }

  /**
   * This method is invoked before the connection is flushed. <br>
   * It can be used by subclasses to implement their own logic
   */
  @Override
  public void beforeFlushConnection() {
  }

  /**
   * This method is invoked after the connection is flushed. <br>
   * It can be used by subclasses to implement their own logic
   */
  @Override
  public void afterFlushConnection() {
  }

  /**
   * This method is invoked before the connection is closed. <br>
   * It can be used by subclasses to implement their own logic
   */
  @Override
  public void beforeCloseConnection() {
  }

  /**
   * This method is invoked after the connection is closed. <br>
   * It can be used by subclasses to implement their own logic
   */
  @Override
  public void afterCloseConnection() {
  }
}
