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
//package de.pgalise.util.graph.internal;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Envelope;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import de.pgalise.simulation.shared.city.NavigationNode;
//import de.pgalise.simulation.traffic.TrafficGraph;
//import de.pgalise.simulation.traffic.TrafficNode;
//import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
//import de.pgalise.util.graph.disassembler.Disassembler;
//import org.geotools.geometry.jts.JTS;
//
///**
// * Tests the {@link QuadrantDisassembler}
// * 
// * @author Marina
// * @version 1.0 (Nov 22, 2012)
// */
//public class QuadrantDisassemblerTest {
//	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
//	
//	@Test
//	public void testDisassemble() {
//		Disassembler dis = new QuadrantDisassembler();
//		TrafficGraph graph = new DefaultTrafficGraph();
//		NavigationNode node;
//
//		// erster qudrant
//		graph.addVertex(new TrafficNode(new Coordinate(10, 10)));
//		graph.addVertex(new TrafficNode(new Coordinate(25, 25)));
//		graph.addVertex(new TrafficNode(new Coordinate(20, 25)));
//		
//// zweiter quadrant
//		graph.addVertex(new TrafficNode(new Coordinate(18, 45)));
//		graph.addVertex(new TrafficNode(new Coordinate(40, 50)));
//
//		// dritter quadrant
//		graph.addVertex(new TrafficNode(new Coordinate(75, 30)));
//		graph.addVertex(new TrafficNode(new Coordinate(80, 45)));
//
//		List<Geometry> quadrants = dis.disassemble(JTS.toGeometry(new Envelope(0, 100, 0, 60)), 4);
//		// es gibt 3 quadranten
//		assertEquals(4, quadrants.size());
//
//		// erster quadrant hat 3 punkte
//		assertEquals(3, this.getNodes(graph.getVertexSet(), quadrants.get(0)).length());
//		assertTrue(this.getNodes(graph.getVertexSet(), quadrants.get(0)).contains("a")
//				&& this.getNodes(graph.getVertexSet(), quadrants.get(0)).contains("b")
//				&& this.getNodes(graph.getVertexSet(), quadrants.get(0)).contains("c"));
//
//		// erster quadrant hat 2 punkte
//		assertEquals(2, this.getNodes(graph.getVertexSet(), quadrants.get(1)).length());
//		assertTrue(this.getNodes(graph.getVertexSet(), quadrants.get(1)).contains("d")
//				&& this.getNodes(graph.getVertexSet(), quadrants.get(1)).contains("e"));
//
//		// dritter quadrant hat 1 Punkte -> see below for check whether all points 
//		//belong to exactly one quadrant
//		assertEquals(1, this.getNodes(graph.getVertexSet(), quadrants.get(2)).length());
//
//		// vierter hat 2
//		assertEquals(2, this.getNodes(graph.getVertexSet(), quadrants.get(3)).length());
//		assertTrue(this.getNodes(graph.getVertexSet(), quadrants.get(3)).contains("f"));
//		assertTrue(this.getNodes(graph.getVertexSet(), quadrants.get(3)).contains("g"));
//	}
//
//	/**
//	 * Returns the nodes as String
//	 * 
//	 * @param nodes
//	 *            Nodes
//	 * @param geometry
//	 *            Position
//	 * @return Nodes as String
//	 */
//	private String getNodes(Collection<TrafficNode> nodes, Geometry geometry) {
//		String str = "";
//		for (Iterator<TrafficNode> i = nodes.iterator(); i.hasNext();) {
//			TrafficNode node = i.next();
//			if (geometry.covers(GEOMETRY_FACTORY.createPoint(((Coordinate) node.getGeoLocation())))) {
//				str += node.getId();
//			}
//		}
//		return str;
//	}
//}
