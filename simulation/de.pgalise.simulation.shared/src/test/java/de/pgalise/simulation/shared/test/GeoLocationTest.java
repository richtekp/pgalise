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
 
package de.pgalise.simulation.shared.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.pgalise.simulation.shared.geolocation.GeoLocation;

/**
 * Tests the class {@link GeoLocation}
 * 
 * @author Marcus
 * @version 1.0 (Nov 22, 2012)
 */
public class GeoLocationTest {

	@Test
	public void testParse() {
		assertEquals(new GeoLocation(-12, 12), GeoLocation.parse("(-12° 0'),(12° 0')"));
	}

	@Test
	public void testToString() {
		System.out.println(GeoLocation.parse("(-12° 0'),(12° 0')").toString(GeoLocation.FORMAT_DEGREE_DECIMALMINUTES));
	}
}
