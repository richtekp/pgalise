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
package de.pgalise.simulation.operationCenter;

import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;
import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;
import de.pgalise.simulation.operationCenter.internal.message.SensorDataMessage;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.shared.entity.SimpleSensorData;
import de.pgalise.simulation.operationCenter.internal.strategy.IntervalSendSensorDataStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.SendSensorDataStrategy;
import de.pgalise.simulation.operationcenter.OCWebSocketUser;
import de.pgalise.simulation.sensorFramework.Sensor;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * J-Unit tests for {@link IntervalSendSensorDataStrategy}
 *
 * @author Timo
 */
public class IntervalSendSensorDataStrategyTest
{

	/**
	 * {@link OCWebSocketService} mock to test
	 * {@link IntervalSendSensorDataStrategy}
	 *
	 * @author Timo
	 */
	private static class OCWebsocketServiceMock implements OCWebSocketService
	{

		/**
		 * Counter
		 */
		private int intervalCounter;

		/**
		 * Constructor
		 */
		public OCWebsocketServiceMock() {
			this.intervalCounter = 0;
		}

		public int getIntervalCounter() {
			return this.intervalCounter;
		}

		@Override
		public Set<OCWebSocketUser> getOCWebSocketUsers() {
			return null;
		}

		@Override
		public void handleNewOpenedWebSocket(OCWebSocketUser user) {

		}

		@Override
		public void registerNewUserEventListener(NewUserEventListener listener) {
		}

		@Override
		public void removeNewUserEventListener(NewUserEventListener listener) {
		}

		@Override
		public void sendMessageToAllUsers(OCWebSocketMessage<?> message) throws
						IOException {
			if (message instanceof SensorDataMessage) {
				this.intervalCounter++;
			}
		}
	}

	/**
	 * Test class
	 */
	private static SendSensorDataStrategy TEST_CLASS;

	/**
	 * Mock of OCWebsocketService
	 */
	private static OCWebsocketServiceMock OC_WEBSOCKET_SERVICE_MOCK;

	/**
	 * Start timestamp
	 */
	private static long START_TIMESTAMP;

	/**
	 * End timestamp
	 */
	private static long END_TIMESTAMP;

	/**
	 * Simulation interval
	 */
	private static long INTERVAL;

	@BeforeClass
	public static void setUp() {
		Calendar cal = new GregorianCalendar();
		cal.set(2013, 1, 29, 1, 0);
		IntervalSendSensorDataStrategyTest.START_TIMESTAMP = cal.getTimeInMillis();
		cal.set(2013, 2, 20, 1, 0);
		IntervalSendSensorDataStrategyTest.END_TIMESTAMP = cal.getTimeInMillis();
		IntervalSendSensorDataStrategyTest.INTERVAL = 1000;

		IntervalSendSensorDataStrategyTest.OC_WEBSOCKET_SERVICE_MOCK = new OCWebsocketServiceMock();

		IntervalSendSensorDataStrategyTest.TEST_CLASS = new IntervalSendSensorDataStrategy(
						IntervalSendSensorDataStrategyTest.OC_WEBSOCKET_SERVICE_MOCK);
		IntervalSendSensorDataStrategyTest.TEST_CLASS.init(
						IntervalSendSensorDataStrategyTest.START_TIMESTAMP,
						IntervalSendSensorDataStrategyTest.END_TIMESTAMP,
						IntervalSendSensorDataStrategyTest.INTERVAL);
	}

	@Test
	public void test() throws IOException {

		/* First interval: */
		long timestampFirstInterval = IntervalSendSensorDataStrategyTest.START_TIMESTAMP;
		Sensor sensor = EasyMock.createNiceMock(Sensor.class);
		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						timestampFirstInterval, sensor);
		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						timestampFirstInterval, sensor);
		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						timestampFirstInterval, sensor);

		/* Strategy doesn't know that the first interval is over: */
		Assert.assertEquals(
						IntervalSendSensorDataStrategyTest.OC_WEBSOCKET_SERVICE_MOCK.
						getIntervalCounter(), 0);

		/* Second interval: */
		long timestampSecondInterval = IntervalSendSensorDataStrategyTest.START_TIMESTAMP
																	 + IntervalSendSensorDataStrategyTest.INTERVAL;
		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						timestampSecondInterval, sensor);

		/* Now the strategy should know that the first interval is over: */
		Assert.assertEquals(
						IntervalSendSensorDataStrategyTest.OC_WEBSOCKET_SERVICE_MOCK.
						getIntervalCounter(), 1);

		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						timestampSecondInterval, sensor);
		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						timestampSecondInterval, sensor);

		/* Strategy doesn't know that the first interval is over: */
		Assert.assertEquals(
						IntervalSendSensorDataStrategyTest.OC_WEBSOCKET_SERVICE_MOCK.
						getIntervalCounter(), 1);

		/* Last interval: */
		IntervalSendSensorDataStrategyTest.TEST_CLASS.addSensorData(
						IntervalSendSensorDataStrategyTest.END_TIMESTAMP,
						sensor);

		/*
		 * Now the strategy should know that the second interval is over AND it must send every single sensor data,
		 * because it:
		 */
		Assert.assertEquals(
						IntervalSendSensorDataStrategyTest.OC_WEBSOCKET_SERVICE_MOCK.
						getIntervalCounter(), 2);
	}
}
