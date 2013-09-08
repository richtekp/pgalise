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

import java.net.ConnectException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpForceCloseStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;

/**
 * Tests the Connection to the datastream management system
 * 
 * @author Marcus
 * @version 1.0 (Feb 6, 2013)
 */
@Ignore
public class AdvancedStreamsTest {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(AdvancedStreamsTest.class);

	/**
	 * Output
	 */
	private final TcpIpOutput outputKeepOpen = new TcpIpOutput("127.0.0.1", 6666, new TcpIpKeepOpenStrategy());

	/**
	 * Output
	 */
	private final TcpIpOutput outputForceClose = new TcpIpOutput("127.0.0.1", 6666, new TcpIpForceCloseStrategy());

	/**
	 * Tests if it is possible to send all data to the streams instance in one stream
	 */
	@Test
	@Ignore
	public void testTransmitForceClose() {
		this.testTransmit(this.outputForceClose);
	}

	/**
	 * Tests if it is possible to send all data to the streams instance in one stream
	 */
	@Test
	public void testTransmitKeepOpen() {
		this.testTransmit(this.outputKeepOpen);
	}

	/**
	 * Transmit test
	 * 
	 * @param output
	 *            TcpIpOutput
	 */
	private void testTransmit(TcpIpOutput output) {
		try {
			for (int j = 0; j < 100; j++) {
				AdvancedStreamsTest.log.debug("J= " + j);
				for (int i = 0; i < 21; i++) {
					AdvancedStreamsTest.log.debug("I= " + i);
					output.beginTransmit();
					// Timestamp
					output.transmitLong(j);
					// Sensorid
					output.transmitInt(j);
					// sensortype
					output.transmitByte((byte) i);
					// measureValue1
					output.transmitDouble(j);
					// measureValue2
					output.transmitDouble(j);
					// axcleCount
					output.transmitByte((byte) i);
					// length
					output.transmitShort((short) i);
					// wheelbase1
					output.transmitShort((short) i);
					// wheelbase2
					output.transmitShort((short) i);

					output.endTransmit();
				}
			}
		} catch (Exception e) {
			// If there is no connection than all is fine, else error!
			if (!((e.getCause() instanceof RuntimeException) || (e.getCause() instanceof ConnectException))) {
				e.printStackTrace();
				Assert.assertTrue(false);
			} else {
				e.printStackTrace();
				Assert.assertTrue(false);
				AdvancedStreamsTest.log.debug("No connection to Odysseus!");
			}
		}
	}
}
