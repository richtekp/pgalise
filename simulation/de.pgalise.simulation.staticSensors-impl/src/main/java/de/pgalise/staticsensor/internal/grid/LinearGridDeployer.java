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

import de.pgalise.simulation.shared.geometry.Geometry;
import de.pgalise.simulation.shared.geometry.Rectangle;
import de.pgalise.simulation.staticsensor.grid.GridDeployer;
import de.pgalise.util.graph.disassembler.Disassembler;
import de.pgalise.util.graph.internal.QuadrantDisassembler;
import de.pgalise.util.vector.Vector2d;

/**
 * Create positions for sensors depending on the limits of the grid.
 * 
 * @author Andreas Rehfeldt, Marina, Mustafa
 * @version 1.0 (Aug 21, 2012)
 * @see {@link GridDeployer}
 */
public class LinearGridDeployer implements GridDeployer {
	/**
	 * Disassembler to disassemble a graph into different sections.
	 */
	private Disassembler dis;

	/**
	 * Constructor
	 */
	public LinearGridDeployer() {
		this.dis = new QuadrantDisassembler();
	}

	@Override
	public List<Vector2d> getPositions(double width, double height, int number) {
		int newNumber = number;
		List<Vector2d> positions = new ArrayList<>();

		if ((number % 2) == 1) {
			newNumber++;
		}

		List<Geometry> list = this.dis.disassemble(new Rectangle(0, 0, width, height), newNumber);

		if (number != newNumber) {
			newNumber = number;
		}

		for (int i = 0; i < newNumber; i++) {
			Geometry geo = list.get(i);
			positions.add(Vector2d.valueOf(geo.getStartX(), geo.getStartY()));
		}
		return positions;
	}
}
