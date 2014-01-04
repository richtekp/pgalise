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

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.operationCenter.internal.strategy.DefaultGPSTimeoutStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSSensorTimeoutStrategy;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.HashSet;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.Ignore;

/**
 * J-Unit tests for {@link DefaultGPSTimeoutStrategy}
 *
 * @author Timo
 */
@Ignore //@TODO: understand/research what processUpdate does
public class DefaultGPSTimeoutStrategyTest {

	/**
	 * Simulation interval:
	 */
	private static final int INTERVAL = 1000;

	/**
	 * After how many updates steps will the GPS sensor get a timeout:
	 */
	private static final int MISSED_GPS_UPDATE_STEPS_BEFORE_TIMEOUT = 2;

	/**
	 * Test class
	 */
	private final GPSSensorTimeoutStrategy TEST_CLASS;

	/**
	 * How many sensor helpers will be produced:
	 */
	private static final int SENSOR_HELPER_LIST_ALL_SIZE = 10;

	/**
	 * How many sensor helpers will have a timeout:
	 */
	private static final double SENSOR_HELPER_WITH_TIMEOUT_PERCENTAGE = 0.5;

	/**
	 * Contains all the sensor helpers (SENSOR_HELPER_LIST_WITH +
	 * SENSOR_HELPER_LIST_WITHOUT_TIMEOUT):
	 */
	private final Set<GpsSensor> SENSOR_HELPER_LIST_ALL;

	/**
	 * Contains only sensor helpers with a timeout (SENSOR_HELPER_LIST_ALL -
	 * SENSOR_HELPER_LIST_WITHOUT_TIMESTAMP):
	 */
	private final Set<GpsSensor> SENSOR_HELPER_LIST_WITH_TIMEOUT;

	/**
	 * Contains only sensor helpers without a timeout (SENSOR_HELPER_LIST_ALL -
	 * SENSOR_HELPER_LIST_WITH_TIMEOUT):
	 */
	private final Set<GpsSensor> SENSOR_HELPER_LIST_WITHOUT_TIMEOUT;

	/**
	 * SENSOR_HELPER_LIST_WITH_TIMEOUT but with SensorData instead of
	 * SensorHelper.
	 */
	private final Set<GpsSensor> SENSOR_DATA_LIST_WITH_TIMEOUT;

	public DefaultGPSTimeoutStrategyTest() {
		TEST_CLASS = new DefaultGPSTimeoutStrategy();
		TEST_CLASS.init(
			DefaultGPSTimeoutStrategyTest.INTERVAL,
			DefaultGPSTimeoutStrategyTest.MISSED_GPS_UPDATE_STEPS_BEFORE_TIMEOUT);

		SENSOR_HELPER_LIST_ALL = new HashSet<>();
		SENSOR_HELPER_LIST_WITH_TIMEOUT = new HashSet<>();
		SENSOR_HELPER_LIST_WITHOUT_TIMEOUT = new HashSet<>();
		SENSOR_DATA_LIST_WITH_TIMEOUT = new HashSet<>();

		/* Create sensors: */
		for (int i = 0; i < DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL_SIZE; i++) {

			GpsSensor sensor = EasyMock.createNiceMock(GpsSensor.class);
			EasyMock.expect(sensor.getUpdateSteps()).andReturn(-1); //for logging -> @TODO handle logging in test, i.e. turn off
			EasyMock.replay(sensor);
			SENSOR_HELPER_LIST_ALL.add(sensor);

			if (((double) i / DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL_SIZE) > DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_WITH_TIMEOUT_PERCENTAGE) {
				SENSOR_HELPER_LIST_WITHOUT_TIMEOUT.add(
					sensor);
			} else {
				SENSOR_HELPER_LIST_WITH_TIMEOUT.
					add(sensor);
				GpsSensor s = EasyMock.createNiceMock(GpsSensor.class);
				SENSOR_DATA_LIST_WITH_TIMEOUT.add(
					(s));
			}
		}
	}

	@Test
	public void test() {
		long currentTimestamp = new Date().getTime();

		/* Start all sensors: */
		Assert.assertEquals(
				new HashSet<GpsSensor>(),
				TEST_CLASS.processUpdateStep(
					currentTimestamp,
					SENSOR_HELPER_LIST_ALL));

		/* Then use sensor helper list without timeouts : */
		for (int i = 0; i < 1000; i++) {

			currentTimestamp += DefaultGPSTimeoutStrategyTest.INTERVAL;

			/* If timestamp is before or after missed gps updates constant, than no sensor will get a timeout. */
			if (i != DefaultGPSTimeoutStrategyTest.MISSED_GPS_UPDATE_STEPS_BEFORE_TIMEOUT) {
				Assert.assertEquals(
						new HashSet<GpsSensor>(),
						TEST_CLASS.processUpdateStep(
							currentTimestamp,
							SENSOR_HELPER_LIST_WITHOUT_TIMEOUT));

				/* All sensors with timeout will be found in this step: */
			} else {
				Assert.assertEquals(
						SENSOR_DATA_LIST_WITH_TIMEOUT,
						TEST_CLASS.processUpdateStep(
							currentTimestamp,
							SENSOR_HELPER_LIST_WITHOUT_TIMEOUT));
			}
		}
	}
}
