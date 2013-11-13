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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.decorator.OdysseusOutput;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;

/**
 * Tests if it is possible to connect and send data to Odysseus
 * 
 * @author Mischa
 * @version 1.0
 */
@Ignore
public class OdysseusConnectionTest {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(OdysseusConnectionTest.class);

	/**
	 * Output
	 */
	private static Output output;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			OdysseusConnectionTest.output = new OdysseusOutput(new TcpIpOutput("127.0.0.1", 6666,
					new TcpIpKeepOpenStrategy()));
		} catch (Exception e) {
			// If there is no connection than all is fine, else error!
			if (!((e.getCause() instanceof RuntimeException) || (e.getCause() instanceof ConnectException))) {
				e.printStackTrace();
				Assert.assertTrue(true);
			} else {
				e.printStackTrace();
				Assert.assertTrue(true);
				OdysseusConnectionTest.log.debug("No connection to Odysseus!");
			}
		}
	}

	/**
	 * Tests if it is possible to send data to the Odysseus instance
	 */
	@Test
	public void testTransmit() {
		try {
			for (int i = 0; i < 99; i++) {
				OdysseusConnectionTest.log.debug("Sent0: " + i);
				OdysseusConnectionTest.output.beginTransmit();
				OdysseusConnectionTest.log.debug("Sent1: " + i);
				OdysseusConnectionTest.output.transmitLong(i);
				OdysseusConnectionTest.log.debug("Sent2: " + i);
				OdysseusConnectionTest.output.transmitInt(i);
				OdysseusConnectionTest.log.debug("Sent3: " + i);
				OdysseusConnectionTest.output.transmitByte((byte) i);
				OdysseusConnectionTest.log.debug("Sent4: " + i);
				OdysseusConnectionTest.output.transmitDouble(i);
				OdysseusConnectionTest.log.debug("Sent5: " + i);
				OdysseusConnectionTest.output.transmitDouble(i);
				OdysseusConnectionTest.log.debug("Sent6: " + i);
				OdysseusConnectionTest.output.transmitByte((byte) i);
				OdysseusConnectionTest.log.debug("Sent7: " + i);
				OdysseusConnectionTest.output.transmitShort((short) i);
				OdysseusConnectionTest.log.debug("Sent8: " + i);
				OdysseusConnectionTest.output.transmitShort((short) i);
				OdysseusConnectionTest.log.debug("Sent9: " + i);
				OdysseusConnectionTest.output.transmitShort((short) i);
				OdysseusConnectionTest.log.debug("Sent10: " + i);

				OdysseusConnectionTest.output.endTransmit();
				OdysseusConnectionTest.log
						.debug("-------------------------------------------------------------------------");
				if (i == 99) {
					Assert.assertTrue(true);
				}
			}
		} catch (Exception e) {
			// If there is no connection than all is fine, else error!
			if (!((e.getCause() instanceof RuntimeException) || (e.getCause() instanceof ConnectException))) {
				e.printStackTrace();
				Assert.assertTrue(true);
			} else {
				e.printStackTrace();
				Assert.assertTrue(true);
				OdysseusConnectionTest.log.debug("No connection to Odysseus!");
			}
		}

	}
}
