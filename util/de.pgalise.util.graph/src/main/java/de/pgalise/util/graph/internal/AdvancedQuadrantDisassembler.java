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
 
package de.pgalise.util.graph.internal;

import java.util.ArrayList;
import java.util.List;

import de.pgalise.simulation.shared.geometry.Geometry;
import de.pgalise.simulation.shared.geometry.Rectangle;
import de.pgalise.util.graph.disassembler.Disassembler;

/**
 * Divides a Geometry into smaller pieces.
 * 
 * @author Timo
 */
public class AdvancedQuadrantDisassembler implements Disassembler {

	@Override
	public List<Geometry> disassemble(Geometry mapper, int numServers) {
		List<Geometry> GeometryList = new ArrayList<Geometry>();
		GeometryList.add(mapper);

		for (int i = 1; i < numServers; i++) {
			List<Geometry> tmpGeometryList = new ArrayList<Geometry>();
			Geometry tmpBiggestGeometry = null;

			for (Geometry Geometry : GeometryList) {
				if ((tmpBiggestGeometry == null)
						|| ((tmpBiggestGeometry.getWidth() * tmpBiggestGeometry.getHeight()) <= (Geometry.getWidth() * Geometry
								.getHeight()))) {
					tmpBiggestGeometry = Geometry;
				}
			}

			tmpGeometryList.addAll(this.halveGeometry(tmpBiggestGeometry));

			for (Geometry Geometry : GeometryList) {
				if (!Geometry.equals(tmpBiggestGeometry)) {
					tmpGeometryList.add(Geometry);
				}
			}

			GeometryList = tmpGeometryList;
		}

		return new ArrayList<Geometry>(GeometryList);
	}

	/**
	 * Divides the Geometry into two parts.
	 * 
	 * @param Geometry
	 * @return List with Geometry objects
	 */
	private List<Geometry> halveGeometry(Geometry Geometry) {
		List<Geometry> dividedGeometryList = new ArrayList<Geometry>();

		/* horizontal or vertical cut? */
		if (Geometry.getWidth() <= Geometry.getHeight()) {
			dividedGeometryList.add(new Rectangle(Geometry.getStartX(), Geometry.getStartY(), Geometry.getEndX(),
					Geometry.getEndY() / 2));
			dividedGeometryList.add(new Rectangle(Geometry.getStartX(), Geometry.getEndY() / 2, Geometry.getEndX(),
					Geometry.getEndY()));
		} else {
			dividedGeometryList.add(new Rectangle(Geometry.getStartX(), Geometry.getStartY(), Geometry.getEndX() / 2,
					Geometry.getEndY()));
			dividedGeometryList.add(new Rectangle(Geometry.getEndX() / 2, Geometry.getStartY(), Geometry.getEndX(),
					Geometry.getEndY()));
		}

		return dividedGeometryList;
	}
}
