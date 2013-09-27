///* 
// * Copyright 2013 PG Alise (http://www.pg-alise.de/)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License. 
// */
// 
//package de.pgalise.simulation.sensorframework;
//
//import de.pgalise.simulation.sensorFramework.SensorType;
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Test;
//
///**
// * Tests the {@link SensorType}
// * 
// * @author Marcus
// * @version 1.0 (Oct 23, 2012)
// */
//public class SensorTypeTest {
//
//	@Test
//	public void testGetForSensorTypeId() {
//		assertEquals(SensorType.ANEMOMETER, SensorType.getForSensorTypeId(0));
//		assertEquals(SensorType.BAROMETER, SensorType.getForSensorTypeId(1));
//		assertEquals(SensorType.GPS_BIKE, SensorType.getForSensorTypeId(2));
//		assertEquals(SensorType.GPS_BUS, SensorType.getForSensorTypeId(3));
//		assertEquals(SensorType.GPS_CAR, SensorType.getForSensorTypeId(4));
//		assertEquals(SensorType.HYGROMETER, SensorType.getForSensorTypeId(5));
//		assertEquals(SensorType.INDUCTIONLOOP, SensorType.getForSensorTypeId(6));
//		assertEquals(SensorType.INFRARED, SensorType.getForSensorTypeId(7));
//		assertEquals(SensorType.LUXMETER, SensorType.getForSensorTypeId(8));
//		assertEquals(SensorType.PHOTOVOLTAIK, SensorType.getForSensorTypeId(9));
//		assertEquals(SensorType.PYRANOMETER, SensorType.getForSensorTypeId(10));
//		assertEquals(SensorType.RAIN, SensorType.getForSensorTypeId(11));
//		assertEquals(SensorType.SMARTMETER, SensorType.getForSensorTypeId(12));
//		assertEquals(SensorType.THERMOMETER, SensorType.getForSensorTypeId(13));
//		assertEquals(SensorType.TRAFFICLIGHT_SENSOR, SensorType.getForSensorTypeId(14));
//		assertEquals(SensorType.WINDFLAG, SensorType.getForSensorTypeId(15));
//		assertEquals(SensorType.WINDPOWERSENSOR, SensorType.getForSensorTypeId(16));
//	}
//
//	@Test
//	public void testGetSensorTypeId() {
//		assertEquals(0, SensorType.ANEMOMETER.getSensorTypeId());
//		assertEquals(1, SensorType.BAROMETER.getSensorTypeId());
//		assertEquals(2, SensorType.GPS_BIKE.getSensorTypeId());
//		assertEquals(3, SensorType.GPS_BUS.getSensorTypeId());
//		assertEquals(4, SensorType.GPS_CAR.getSensorTypeId());
//		assertEquals(5, SensorType.HYGROMETER.getSensorTypeId());
//		assertEquals(6, SensorType.INDUCTIONLOOP.getSensorTypeId());
//		assertEquals(7, SensorType.INFRARED.getSensorTypeId());
//		assertEquals(8, SensorType.LUXMETER.getSensorTypeId());
//		assertEquals(9, SensorType.PHOTOVOLTAIK.getSensorTypeId());
//		assertEquals(10, SensorType.PYRANOMETER.getSensorTypeId());
//		assertEquals(11, SensorType.RAIN.getSensorTypeId());
//		assertEquals(12, SensorType.SMARTMETER.getSensorTypeId());
//		assertEquals(13, SensorType.THERMOMETER.getSensorTypeId());
//		assertEquals(14, SensorType.TRAFFICLIGHT_SENSOR.getSensorTypeId());
//		assertEquals(15, SensorType.WINDFLAG.getSensorTypeId());
//		assertEquals(16, SensorType.WINDPOWERSENSOR.getSensorTypeId());
//	}
//
//	@Test
//	public void testValues() {
//		for (SensorType sensorType : SensorType.values()) {
//			assertEquals(sensorType, SensorType.getForSensorTypeId(sensorType.getSensorTypeId()));
//		}
//	}
//}
