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
package de.pgalise.simulation.sensorFramework.internal;

import de.pgalise.simulation.sensorFramework.output.tcpip.DefaultTcpIpOutput;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;

/**
 * Tests of the TcpIpSensorOutput
 *
 * @author Marcus
 * @version 1.0 (Oct 23, 2012)
 */
@Ignore
public class TcpIpSensorOutputTest {

  /**
   * Test class
   */
  private final TcpIpOutput output = new DefaultTcpIpOutput("127.0.0.1",
    6666,
    TcpIpKeepOpenStrategy.getInstance());

  @Test
  public void testTransmit_forceClose() {

    for (int i = 0; i < 100; i++) {
      this.output.beginTransmit();
      this.output.transmitLong(i);
      this.output.transmitInt(i);
      this.output.transmitInt(i);
      this.output.transmitDouble(i);
      this.output.transmitDouble(i);
      this.output.transmitByte((byte) i);
      this.output.transmitShort((short) i);
      this.output.transmitShort((short) i);
      this.output.transmitShort((short) i);
      this.output.endTransmit();
    }
  }

  @Test
  public void testTransmit_keepOpen() {

    for (int i = 0; i < 100; i++) {
      this.output.beginTransmit();
      this.output.transmitLong(i);
      this.output.transmitInt(i);
      this.output.transmitInt(i);
      this.output.transmitDouble(i);
      this.output.transmitDouble(i);
      this.output.transmitByte((byte) i);
      this.output.transmitShort((short) i);
      this.output.transmitShort((short) i);
      this.output.transmitShort((short) i);
      this.output.endTransmit();
    }
  }
}
