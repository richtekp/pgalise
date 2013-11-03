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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.operationCenter.internal.strategy.DefaultGPSTimeoutStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSSensorTimeoutStrategy;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;

/**
 * J-Unit tests for {@link DefaultGPSTimeoutStrategy}
 * 
 * @author Timo
 */
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
	private static GPSSensorTimeoutStrategy TEST_CLASS;

	/**
	 * How many sensor helpers will be produced:
	 */
	private static final int SENSOR_HELPER_LIST_ALL_SIZE = 10;

	/**
	 * How many sensor helpers will have a timeout:
	 */
	private static final double SENSOR_HELPER_WITH_TIMEOUT_PERCENTAGE = 0.5;

	/**
	 * Contains all the sensor helpers (SENSOR_HELPER_LIST_WITH + SENSOR_HELPER_LIST_WITHOUT_TIMEOUT):
	 */
	private static List<SensorHelper> SENSOR_HELPER_LIST_ALL;

	/**
	 * Contains only sensor helpers with a timeout (SENSOR_HELPER_LIST_ALL - SENSOR_HELPER_LIST_WITHOUT_TIMESTAMP):
	 */
	private static List<SensorHelper> SENSOR_HELPER_LIST_WITH_TIMEOUT;

	/**
	 * Contains only sensor helpers without a timeout (SENSOR_HELPER_LIST_ALL - SENSOR_HELPER_LIST_WITH_TIMEOUT):
	 */
	private static List<SensorHelper> SENSOR_HELPER_LIST_WITHOUT_TIMEOUT;

	/**
	 * SENSOR_HELPER_LIST_WITH_TIMEOUT but with SensorData instead of SensorHelper.
	 */
	private static List<SensorData> SENSOR_DATA_LIST_WITH_TIMEOUT;

	@BeforeClass
	public static void setUp() {
		DefaultGPSTimeoutStrategyTest.TEST_CLASS = new DefaultGPSTimeoutStrategy();
		DefaultGPSTimeoutStrategyTest.TEST_CLASS.init(DefaultGPSTimeoutStrategyTest.INTERVAL,
				DefaultGPSTimeoutStrategyTest.MISSED_GPS_UPDATE_STEPS_BEFORE_TIMEOUT);

		DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL = new LinkedList<>();
		DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_WITH_TIMEOUT = new LinkedList<>();
		DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_WITHOUT_TIMEOUT = new LinkedList<>();
		DefaultGPSTimeoutStrategyTest.SENSOR_DATA_LIST_WITH_TIMEOUT = new LinkedList<>();

		/* Create sensors: */
		for (int i = 0; i < DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL_SIZE; i++) {

			SensorHelper tmpSensorHelper = new SensorHelper(i, new Coordinate(), SensorTypeEnum.GPS_CAR,
					new LinkedList<SensorInterfererType>(), "");
			DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL.add(tmpSensorHelper);

			if (((double) i / DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL_SIZE) > DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_WITH_TIMEOUT_PERCENTAGE) {
				DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_WITHOUT_TIMEOUT.add(tmpSensorHelper);
			} else {
				DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_WITH_TIMEOUT.add(tmpSensorHelper);
				DefaultGPSTimeoutStrategyTest.SENSOR_DATA_LIST_WITH_TIMEOUT.add(new SensorData(tmpSensorHelper
						.getSensorType().getSensorTypeId(), tmpSensorHelper.getSensorID()));
			}
		}
	}

	/**
	 * Compares two sensor data collections. The order of the collections doesn't matter.
	 * 
	 * @param collection1
	 *            Data 1
	 * @param collection2
	 *            Data 2
	 * @return true if the collections are equals
	 */
	private static boolean compareSensorHelperCollections(Collection<SensorData> collection1,
			Collection<SensorData> collection2) {

		if (collection1.size() != collection2.size()) {
			return false;
		}

		collection1Loop: for (SensorData sensorData1 : collection1) {
			for (SensorData sensorData2 : collection2) {
				if (sensorData1.equals(sensorData2)) {
					continue collection1Loop;
				}
			}
			return false;
		}

		return true;
	}

	@Test
	public void test() {
		long currentTimestamp = new Date().getTime();

		/* Start all sensors: */
		Assert.assertEquals(true, DefaultGPSTimeoutStrategyTest.compareSensorHelperCollections(
				new LinkedList<SensorData>(), DefaultGPSTimeoutStrategyTest.TEST_CLASS.processUpdateStep(
						currentTimestamp, DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_ALL)));

		/* Then use sensor helper list without timeouts : */
		for (int i = 0; i < 1000; i++) {

			currentTimestamp += DefaultGPSTimeoutStrategyTest.INTERVAL;

			/* If timestamp is before or after missed gps updates constant, than no sensor will get a timeout. */
			if (i != DefaultGPSTimeoutStrategyTest.MISSED_GPS_UPDATE_STEPS_BEFORE_TIMEOUT) {
				Assert.assertEquals(true, DefaultGPSTimeoutStrategyTest.compareSensorHelperCollections(
						new LinkedList<SensorData>(), DefaultGPSTimeoutStrategyTest.TEST_CLASS.processUpdateStep(
								currentTimestamp, DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_WITHOUT_TIMEOUT)));

				/* All sensors with timeout will be found in this step: */
			} else {
				Assert.assertEquals(true, DefaultGPSTimeoutStrategyTest.compareSensorHelperCollections(
						DefaultGPSTimeoutStrategyTest.SENSOR_DATA_LIST_WITH_TIMEOUT,
						DefaultGPSTimeoutStrategyTest.TEST_CLASS.processUpdateStep(currentTimestamp,
								DefaultGPSTimeoutStrategyTest.SENSOR_HELPER_LIST_WITHOUT_TIMEOUT)));
			}
		}
	}
}
