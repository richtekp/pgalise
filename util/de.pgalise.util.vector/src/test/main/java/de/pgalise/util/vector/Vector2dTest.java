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
 
package de.pgalise.util.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the class Vector2d
 * 
 * @author Mustafa
 * @version 1.0 (Nov 21, 2012)
 */
public class Vector2dTest {

	@Test
	public void equalsTest() {
		Vector2d a = Vector2d.valueOf(3, 8);
		Vector2d b = Vector2d.valueOf(3, 8);
		Vector2d c = Vector2d.valueOf(3, 8);
		Vector2d d = Vector2d.valueOf(-1, 3);

		// assertTrue(a==b); // geht nicht, weil das 2 unterschiedliche instanzen sind
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertTrue(b.equals(c));
		assertTrue(a.equals(c));
		assertFalse(a.equals(null));
		assertFalse(a.equals(Double.valueOf(0)));
		assertFalse(a.equals(d));
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void addTest() {
		Vector2d a = Vector2d.valueOf(-1, 3);
		Vector2d b = Vector2d.valueOf(4, 5);

		Vector2d c = a.add(b);
		// kummutativität prüfen
		Vector2d d = b.add(a);

		assertTrue(c.getX() == 3 && c.getY() == 8);
		assertTrue(d.getX() == 3 && d.getY() == 8);
	}

	@Test
	public void subTest() {
		Vector2d a = Vector2d.valueOf(-1, 3);
		Vector2d b = Vector2d.valueOf(4, 5);

		Vector2d c = a.sub(b);

		assertTrue(c.getX() == -5 && c.getY() == -2);
	}

	@Test
	public void dotTest() {
		Vector2d a = Vector2d.valueOf(-1, 3);
		Vector2d b = Vector2d.valueOf(4, 5);

		double c = a.dot(b);
		double d = b.dot(a);

		assertEquals((-1 * 4 + 3 * 5), c, 0);
		assertTrue(c == d);
	}

	@Test
	public void lengthTest() {
		Vector2d a = Vector2d.valueOf(-1, 3);

		double c = a.length();

		assertEquals(Math.sqrt(-1 * -1 + 3 * 3), c, 0);
	}

	@Test
	public void scalarTest() {
		Vector2d a = Vector2d.valueOf(-1, 3);

		Vector2d b = a.scale(3);

		assertTrue(b.getX() == (-1 * 3) && b.getY() == (3 * 3));
	}

	@Test
	public void angleTest() {
		Vector2d a = Vector2d.valueOf(1, 0);
		Vector2d b = Vector2d.valueOf(1, 1);
		Vector2d c = Vector2d.valueOf(0, 1);
		Vector2d d = Vector2d.valueOf(-1, 1);
		Vector2d e = Vector2d.valueOf(-1, 0);

		assertEquals(Math.PI / 4, a.angle(b), 0.001);
		assertEquals(Math.PI / 2, a.angle(c), 0.001);
		assertEquals(Math.PI * 3 / 4, a.angle(d), 0.001);
		assertEquals(Math.PI, a.angle(e), 0.001);
	}
}
