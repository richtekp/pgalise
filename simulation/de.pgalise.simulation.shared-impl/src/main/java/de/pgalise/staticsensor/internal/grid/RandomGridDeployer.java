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
 
package de.pgalise.staticsensor.internal.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.pgalise.simulation.staticsensor.grid.GridDeployer;
import javax.vecmath.Vector2d;

/**
 * Create random positions for sensors
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 21, 2012)
 * @see {@link GridDeployer}
 */
public class RandomGridDeployer implements GridDeployer {

	/**
	 * Random generator
	 */
	private Random random;

	/**
	 * Constructor
	 * 
	 * @param randomSeed
	 *            Seed
	 */
	public RandomGridDeployer(long randomSeed) {
		this.random = new Random(randomSeed);
	}

	@Override
	public List<Vector2d> getPositions(double width, double height, int number) {
		List<Vector2d> positions = new ArrayList<>();

		// Create positions
		double x, y;
		for (int i = 0; i < number; i++) {
			do {
				// Get x
				x = this.random.nextInt((int) width) + this.random.nextDouble();

				// Get y
				y = this.random.nextInt((int) height) + this.random.nextDouble();
			} while ((x > width) && (y > height));

			// Add to list
			positions.add(new Vector2d(x, y));
		}

		// return list
		return positions;
	}

}
