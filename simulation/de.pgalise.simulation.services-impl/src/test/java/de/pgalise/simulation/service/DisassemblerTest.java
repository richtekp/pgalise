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
 
package de.pgalise.simulation.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Rectangle;
import de.pgalise.util.graph.disassembler.Disassembler;
import de.pgalise.util.graph.internal.QuadrantDisassembler;
import javax.vecmath.Vector2d;
import org.geotools.geometry.jts.JTS;

/**
 * Tests the {@link QuadrantDisassembler}
 * 
 * @author Marina
 * @version 1.0 (Nov 22, 2012)
 */
public class DisassemblerTest {
	@Test
	public void disassemble() {
		Disassembler dis = new QuadrantDisassembler();
		Graph graph = new SingleGraph("oldenburg");
		Node node;

		// erster qudrant
		node = graph.addNode("a");
		node.setAttribute("position", new Coordinate(10, 10));

		node = graph.addNode("b");
		node.setAttribute("position", new Coordinate(25, 25));

		node = graph.addNode("c");
		node.setAttribute("position", new Coordinate(20, 25));

		// zweiter quadrant
		node = graph.addNode("d");
		node.setAttribute("position", new Coordinate(18, 45));

		node = graph.addNode("e");
		node.setAttribute("position", new Coordinate(40, 50));

		// dritter quadrant
		node = graph.addNode("f");
		node.setAttribute("position", new Coordinate(75, 30));

		node = graph.addNode("g");
		node.setAttribute("position", new Coordinate(80, 45));

		List<Geometry> quadrants = dis.disassemble(JTS.toGeometry(new Envelope(0, 0, 100, 60)), 4);

		// es gibt 3 quadranten
		assertEquals(4, quadrants.size());

		// erster quadrant hat 3 punkte
		assertEquals(3, this.getNodes(graph.getNodeSet(), quadrants.get(0)).length());
		assertTrue(this.getNodes(graph.getNodeSet(), quadrants.get(0)).contains("a")
				&& this.getNodes(graph.getNodeSet(), quadrants.get(0)).contains("b")
				&& this.getNodes(graph.getNodeSet(), quadrants.get(0)).contains("c"));

		// erster quadrant hat 2 punkte
		assertEquals(2, this.getNodes(graph.getNodeSet(), quadrants.get(1)).length());
		assertTrue(this.getNodes(graph.getNodeSet(), quadrants.get(1)).contains("d")
				&& this.getNodes(graph.getNodeSet(), quadrants.get(1)).contains("e"));

		// dritter quadrant hat 0 punkte
		assertEquals(0, this.getNodes(graph.getNodeSet(), quadrants.get(2)).length());

		// vierter hat 2
		assertEquals(2, this.getNodes(graph.getNodeSet(), quadrants.get(3)).length());
		assertTrue(this.getNodes(graph.getNodeSet(), quadrants.get(3)).contains("f"));
		assertTrue(this.getNodes(graph.getNodeSet(), quadrants.get(3)).contains("g"));
	}
	
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	/**
	 * Returns the nodes as String
	 * 
	 * @param nodes
	 *            Nodes
	 * @param geometry
	 *            Position
	 * @return Nodes as String
	 */
	private String getNodes(Collection<Node> nodes, Geometry geometry) {
		String str = "";
		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			Node node = i.next();
			if (geometry.covers(GEOMETRY_FACTORY.createPoint(((Coordinate) node.getAttribute("position"))))) {
				str += node.getId();
			}
		}
		return str;
	}
}
