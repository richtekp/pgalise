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
 * Tests the class Vector3d
 * 
 * @author Mustafa
 * @version 1.0 (Nov 21, 2012)
 */
public class Vector3dTest {

	@Test
	public void equalsTest() {
		Vector3d a = Vector3d.valueOf(3, 8, 2);
		Vector3d b = Vector3d.valueOf(3, 8, 2);
		Vector3d c = Vector3d.valueOf(3, 8, 2);
		Vector3d d = Vector3d.valueOf(-1, 3, 2);

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
		Vector3d a = Vector3d.valueOf(-1, 3, 2);
		Vector3d b = Vector3d.valueOf(4, 5, 3);

		Vector3d c = a.add(b);
		// kummutativität prüfen
		Vector3d d = b.add(a);

		assertTrue(c.getX() == 3 && c.getY() == 8 && c.getZ() == 5);
		assertTrue(d.getX() == 3 && d.getY() == 8 && d.getZ() == 5);
	}

	@Test
	public void subTest() {
		Vector3d a = Vector3d.valueOf(-1, 3, 2);
		Vector3d b = Vector3d.valueOf(4, 5, 3);

		Vector3d c = a.sub(b);

		assertTrue(c.getX() == -5 && c.getY() == -2 && c.getZ() == -1);
	}

	@Test
	public void dotTest() {
		Vector3d a = Vector3d.valueOf(-1, 3, 2);
		Vector3d b = Vector3d.valueOf(4, 5, 3);

		double c = a.dot(b);
		double d = b.dot(a);

		assertEquals((-1 * 4 + 3 * 5 + 2 * 3), c, 0);
		assertTrue(c == d);
	}

	@Test
	public void lengthTest() {
		Vector3d a = Vector3d.valueOf(-1, 3, 2);

		double c = a.length();

		assertEquals(Math.sqrt(-1 * -1 + 3 * 3 + 2 * 2), c, 0);
	}

	@Test
	public void scalarTest() {
		Vector3d a = Vector3d.valueOf(-1, 3, 2);

		Vector3d b = a.scale(3);

		assertTrue(b.getX() == (-1 * 3) && b.getY() == (3 * 3) && b.getZ() == (3 * 2));
	}

	@Test
	public void crossTest() {
		Vector3d a = Vector3d.valueOf(-1, 3, 2);
		Vector3d b = Vector3d.valueOf(4, 5, 3);
		Vector3d c = a.cross(b);

		assertEquals(a.getY() * b.getZ() - a.getZ() * b.getY(), c.getX(), 0);

		assertEquals(a.getZ() * b.getX() - a.getX() * b.getZ(), c.getY(), 0);

		assertEquals(a.getX() * b.getY() - a.getY() * b.getX(), c.getZ(), 0);
	}

	@Test
	public void angleTest() {
		Vector3d a = Vector3d.valueOf(1, 0, 0);
		Vector3d b = Vector3d.valueOf(1, 1, 0);
		Vector3d c = Vector3d.valueOf(0, 1, 0);
		Vector3d d = Vector3d.valueOf(-1, 1, 0);
		Vector3d e = Vector3d.valueOf(-1, 0, 0);

		assertEquals(Math.PI / 4, a.angle(b), 0.001);
		assertEquals(Math.PI / 2, a.angle(c), 0.001);
		assertEquals(Math.PI * 3 / 4, a.angle(d), 0.001);
		assertEquals(Math.PI, a.angle(e), 0.001);
	}
}
