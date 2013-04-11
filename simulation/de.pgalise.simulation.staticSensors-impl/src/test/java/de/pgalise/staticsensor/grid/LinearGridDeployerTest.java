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
 
package de.pgalise.staticsensor.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.staticsensor.internal.grid.LinearGridDeployer;
import de.pgalise.util.vector.Vector2d;

/**
 * Tests the class {@link LinearGridDeployer}
 * 
 * @author Marina
 * @version 1.0 (Nov 22, 2012)
 */
public class LinearGridDeployerTest {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(LinearGridDeployerTest.class);

	@Test
	public void validatePosition() {
		LinearGridDeployer dep = new LinearGridDeployer();
		List<Vector2d> positions = dep.getPositions(8, 4, 8);

		assertEquals(8, positions.size());

		for (Vector2d pos : positions) {
			log.debug(String.format("(%s, %s)", pos.getX(), pos.getY()));
		}

		assertTrue(this.contains(positions, 0, 0));
		assertTrue(this.contains(positions, 2, 0));
		assertTrue(this.contains(positions, 0, 2));
		assertTrue(this.contains(positions, 2, 2));
		assertTrue(this.contains(positions, 4, 0));
		assertTrue(this.contains(positions, 4, 2));
		assertTrue(this.contains(positions, 6, 0));
		assertTrue(this.contains(positions, 6, 2));
	}

	private boolean contains(List<Vector2d> ps, double x, double y) {
		for (Vector2d p : ps) {
			if ((p.getX() == x) && (p.getY() == y)) {
				return true;
			}
		}
		return false;
	}
}
