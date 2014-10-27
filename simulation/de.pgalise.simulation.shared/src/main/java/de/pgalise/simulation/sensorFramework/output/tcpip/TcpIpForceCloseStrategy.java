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

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * This class extends {@link TcpIpConnectionStrategy} and closes and recreates
 * the socket after each tuple transmission.
 *
 * The class is designed as stateless singelton. Obtain the only instance with {@link #getInstance()
 * }.
 *
 * @author Marcus
 */
public class TcpIpForceCloseStrategy extends AbstractTcpIpConnectionStrategy {

  /**
   * Returns always TcpIpConnectionStrategyEnum.FORCE_CLOSE.
   *
   * @return always TcpIpConnectionStrategyEnum.FORCE_CLOSE
   */
  @Override
  public TcpIpConnectionStrategyEnum getConnectionStrategyEnum() {
    return TcpIpConnectionStrategyEnum.FORCE_CLOSE;
  }

  private TcpIpForceCloseStrategy() {
  }

  private static TcpIpForceCloseStrategy instance;

  public static TcpIpForceCloseStrategy getInstance() {
    if (instance == null) {
      instance = new TcpIpForceCloseStrategy();
    }
    return instance;
  }

  @Override
  public void handleOpen(final TcpIpOutput sensorOutput) {
    try {
      sensorOutput.setSocketChannel(SocketChannel.
        open(sensorOutput.getAddress()));
      sensorOutput.setDataOutputStream(new DataOutputStream(sensorOutput.
        getSocketChannel().socket()
        .getOutputStream()));
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleClose(final TcpIpOutput sensorOutput) {
    if (sensorOutput == null) {
      throw new IllegalArgumentException("sensorOutput may not be null");
    }
    if (sensorOutput.getSocketChannel() != null) {
      try {
        sensorOutput.getSocketChannel().close();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
      sensorOutput.setSocketChannel(null);
      sensorOutput.setDataOutputStream(null);
    }
  }
}
