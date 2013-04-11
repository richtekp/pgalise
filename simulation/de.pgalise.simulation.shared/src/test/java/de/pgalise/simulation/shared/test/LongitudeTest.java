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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.pgalise.simulation.shared.geolocation.Longitude;

/**
 * Tests the class {@link Longitude}
 * 
 * @author Marcus
 * @version 1.0 (Nov 22, 2012)
 */
public class LongitudeTest {

	@Test
	public void testParse() {
		try {
			assertEquals(new Longitude(65.023), Longitude.parse("65.023"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			assertNotSame(new Longitude(65), Longitude.parse("65."));
		} catch (Exception e) {
		}
		try {
			assertEquals(new Longitude(65.023), Longitude.parse("65.023°"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			assertEquals(new Longitude(66.72), Longitude.parse("66° 43' 12''"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			assertEquals(new Longitude(66.72), Longitude.parse("66° 43' 12.0''"));
		} catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			assertEquals(new Longitude(66.72).getDegree(), Longitude.parse("66° 43.20'").getDegree(), 0.1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		try {
			assertEquals(new Longitude(66.72), Longitude.parse("66.7200°"));
		} catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			assertEquals(new Longitude(-66.72), Longitude.parse("-66.7200°"));
		} catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			assertEquals(new Longitude(-66.72), Longitude.parse("-66° 43' 12''"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
