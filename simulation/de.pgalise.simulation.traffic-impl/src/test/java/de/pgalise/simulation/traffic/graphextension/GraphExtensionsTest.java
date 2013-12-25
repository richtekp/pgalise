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
//package de.pgalise.simulation.traffic.graphextension;
//
//import de.pgalise.simulation.shared.city.Coordinate;
//import de.pgalise.simulation.traffic.TrafficGraph;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertSame;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//import org.graphstream.graph.Edge;
//import org.graphstream.graph.Graph;
//import org.graphstream.graph.Node;
//import org.graphstream.graph.implementations.SingleGraph;
//import org.junit.Test;
//
//import de.pgalise.simulation.traffic.internal.graphextension.DefaultGraphExtensions;
//import de.pgalise.simulation.traffic.graphextension.GraphExtensions;
//import de.pgalise.simulation.shared.city.Vector2d;
//
///**
// * Tests the {@link GraphExtensions}
// * 
// * @author Marcus
// * @version 1.0 (Mar 20, 2013)
// */
//public class GraphExtensionsTest {
//
//	/**
//	 * Test class
//	 */
//	private final GraphExtensions graphExtensions = new DefaultGraphExtensions();
//
//	/**
//	 * Create edge
//	 * 
//	 * @return edge
//	 */
//	private static Edge createEdge() {
//		Graph graph = new SingleGraph("SomeGraph");
//		graph.addNode("A");
//		graph.addNode("B");
//		return graph.addEdge("AB", "A", "B");
//	}
//
//	/**
//	 * Create node
//	 * 
//	 * @return Node
//	 */
//	private static Node createNode(GraphExtensions graphExtensions) {
//		TrafficGraph graph = new TrafficGraph(DefaultNavigationEdge.class);
//
//		// add a node without attached position data
//		final Node node = graph.addNode("A");
//		graphExtensions.setPosition(graph.addNode("A1"), new Coordinate(1, 1));
//		graphExtensions.setPosition(graph.addNode("A2"), new Coordinate(1, 19));
//		graphExtensions.setPosition(graph.addNode("A3"), new Coordinate(19, 19));
//		graph.addEdge("AA1", "A", "A1");
//		graph.addEdge("AA2", "A", "A2");
//		graph.addEdge("AA3", "A", "A3");
//
//		return node;
//	}
//
//	/**
//	 * This method tests whether the correct length of an edge is set. Only after both of the edge's nodes have got
//	 * attached correct position data the "getLenght"-method should return instead a non-null value the correct
//	 * calculated length of the edge.
//	 */
//	@Test
//	public void testGetLength() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.getLength(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		Edge edge = GraphExtensionsTest.createEdge();
//
//		// since the edge's nodes havn't any position data the method under test
//		// should return null
//		assertNull(graphExtensions.getLength(edge));
//
//		// now attach position data to the edge's first node
//		graphExtensions.setPosition(edge.getNode0(), new Coordinate(14, 1));
//
//		// the method under test should still return null since the second node
//		// still
//		// hasn't any position data yet
//		assertNull(graphExtensions.getLength(edge));
//
//		// now attach position data to the edge's second node
//		graphExtensions.setPosition(edge.getNode1(), new Coordinate(3, 1));
//
//		// now test whether the correct length has been calculated
//		assertEquals(Double.valueOf(11), graphExtensions.getLength(edge), 0.0);
//		// test again to check side effects
//		assertEquals(Double.valueOf(11), graphExtensions.getLength(edge), 0.0);
//
//		// now create a second edge which nodes have more complicated positions
//		edge = GraphExtensionsTest.createEdge();
//		graphExtensions.setPosition(edge.getNode0(), new Coordinate(4, 54));
//		graphExtensions.setPosition(edge.getNode1(), new Coordinate(87, 112));
//
//		assertEquals(Double.valueOf(101.25709851659783758135491008371), graphExtensions.getLength(edge), 0.0);
//	}
//
//	/**
//	 * This method tests whether the correct maxSpeed value is returned under various circumstances.
//	 */
//	@Test
//	public void testGetMaxSpeed() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.getMaxSpeed(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// normal use
//		graphExtensions.setMaxSpeed(edge, 50d);
//		assertEquals(Double.valueOf(50), graphExtensions.getMaxSpeed(edge));
//
//	}
//
//	/**
//	 * This method tests whether the correct maxSpeed value is returned under various circumstances.
//	 */
//	@Test
//	public void testGetStreetName() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.getStreetName(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// normal use
//		graphExtensions.setStreetName(edge, "Musterstraße");
//		assertEquals("Musterstraße", graphExtensions.getStreetName(edge));
//	}
//
//	/**
//	 * This method tests whether the correct vector of an edge is set. Only after both of the edge's nodes have got
//	 * attached correct position data the "getVector"-method should return instead a non-null value the correct
//	 * calculated vector of the edge.
//	 */
//	@Test
//	public void testGetVector() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.getVector(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		Edge edge = GraphExtensionsTest.createEdge();
//
//		// since the edge's nodes havn't any position data the method under test
//		// should return null
//		assertNull(graphExtensions.getVector(edge));
//
//		// now attach position data to the edge's first node
//		graphExtensions.setPosition(edge.getNode0(), new Coordinate(14, 1));
//
//		// the method under test should still return null since the second node
//		// still
//		// hasn't any position data yet
//		assertNull(graphExtensions.getVector(edge));
//
//		// now attach position data to the edge's second node
//		graphExtensions.setPosition(edge.getNode1(), new Coordinate(3, 1));
//
//		// now test whether the correct vector has been calculated
//		assertEquals(new Vector2d(11, 0), graphExtensions.getVector(edge));
//		// test again to check side effects
//		assertEquals(new Vector2d(11, 0), graphExtensions.getVector(edge));
//
//		// now create a second edge which nodes have more complicated positions
//		edge = GraphExtensionsTest.createEdge();
//		graphExtensions.setPosition(edge.getNode0(), new Coordinate(4, 54));
//		graphExtensions.setPosition(edge.getNode1(), new Coordinate(87, 112));
//
//		assertEquals(new Vector2d(-83, -58), graphExtensions.getVector(edge));
//	}
//
//	/**
//	 * This method tests whether the "hasLength"-method returns correct values under various circumstances.
//	 */
//	@Test
//	public void testHasLength() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.hasLength(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// the edge should have no length property set
//		assertFalse(graphExtensions.hasLength(edge));
//
//		// set the position of the edge's first node and the edge still has no
//		// length property set
//		graphExtensions.setPosition(edge.getNode0(), new Coordinate(12, 13));
//		assertFalse(graphExtensions.hasLength(edge));
//
//		// set the position of the edge's second node and the edge now should
//		// have the length property set
//		graphExtensions.setPosition(edge.getNode1(), new Coordinate(1, 34));
//		assertTrue(graphExtensions.hasLength(edge));
//
//	}
//
//	/**
//	 * This method tests whether the "hasMaxSpeed"-method returns correct values under various circumstances.
//	 */
//	@Test
//	public void testHasMaxSpeed() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.hasMaxSpeed(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// the edge should have a maxSpeed property set
//		assertTrue(graphExtensions.hasMaxSpeed(edge));
//
//		// After setting the edge's maxSpeed property to null the method should
//		// still return false.
//		graphExtensions.setMaxSpeed(edge, null);
//		assertTrue(graphExtensions.hasMaxSpeed(edge));
//
//		// After setting the edge's maxSpeed to 50 the method should return
//		// true.
//		graphExtensions.setMaxSpeed(edge, 50d);
//		assertTrue(graphExtensions.hasMaxSpeed(edge));
//	}
//
//	/**
//	 * This method tests whether the "hasStreetName"-method returns correct values under various circumstances.
//	 */
//	@Test
//	public void testHasStreetName() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.hasStreetName(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// the edge should have no streetName property set
//		assertFalse(graphExtensions.hasStreetName(edge));
//
//		// After setting the edge's streetName property to null the method
//		// should still return false.
//		graphExtensions.setStreetName(edge, null);
//		assertFalse(graphExtensions.hasStreetName(edge));
//
//		// After setting the edge's streetName property to "Musterstraße" the
//		// method should return true.
//		graphExtensions.setStreetName(edge, "Musterstraße");
//		assertTrue(graphExtensions.hasStreetName(edge));
//	}
//
//	/**
//	 * This method tests whether the "hasVector"-method returns correct values under various circumstances.
//	 */
//	@Test
//	public void testHasVector() {
//		// try to use a null value as parameter
//		try {
//			graphExtensions.hasVector(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create an edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// the edge should have no length property set
//		assertFalse(graphExtensions.hasVector(edge));
//
//		// set the position of the edge's first node and the edge still has no
//		// length property set
//		graphExtensions.setPosition(edge.getNode0(), new Coordinate(12, 13));
//		assertFalse(graphExtensions.hasVector(edge));
//
//		// set the position of the edge's second node and the edge now should
//		// have the length property set
//		graphExtensions.setPosition(edge.getNode1(), new Coordinate(1, 34));
//		assertTrue(graphExtensions.hasVector(edge));
//	}
//
//	/**
//	 * This method tests whether the maxSpeed property is set correctly under various circumstances.
//	 */
//	@Test
//	public void testSetMaxSpeed() {
//		// try to use two null values as parameters
//		try {
//			graphExtensions.setMaxSpeed(null, null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to use a null value for edge
//		try {
//			graphExtensions.setMaxSpeed(null, 50d);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create a edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// use a null value for maxSpeed
//		graphExtensions.setMaxSpeed(edge, null);
//		assertTrue(graphExtensions.getMaxSpeed(edge) != null);
//
//		// try to use the zero integer value for max speed
//		try {
//			graphExtensions.setMaxSpeed(edge, -1d);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to use a negative integer value for max speed
//		try {
//			graphExtensions.setMaxSpeed(edge, -20d);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// normal use
//		graphExtensions.setMaxSpeed(edge, 50d);
//		assertEquals(Double.valueOf(50), graphExtensions.getMaxSpeed(edge));
//
//		// test method chaining
//		assertSame(edge, graphExtensions.setMaxSpeed(edge, 50d));
//	}
//
//	/**
//	 * This method tests whether the streetName property is set correctly under various circumstances.
//	 */
//	@Test
//	public void testSetStreetName() {
//
//		// try to use two null values as parameters
//		try {
//			graphExtensions.setStreetName(null, null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to use a null value for edge
//		try {
//			graphExtensions.setStreetName(null, "Musterstraße");
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create a edge
//		final Edge edge = GraphExtensionsTest.createEdge();
//
//		// use a null value for streetName
//		graphExtensions.setStreetName(edge, null);
//		assertNull(graphExtensions.getStreetName(edge));
//
//		// use an empty string for streetName
//		graphExtensions.setStreetName(edge, "");
//		assertEquals("", graphExtensions.getStreetName(edge));
//
//		// normal use
//		graphExtensions.setStreetName(edge, "Musterstraße");
//		assertEquals("Musterstraße", graphExtensions.getStreetName(edge));
//
//		// test method chaining
//		assertSame(edge, graphExtensions.setStreetName(edge, "Musterstraße"));
//	}
//
//	/**
//	 * This method tests whether the correct position value is returned under various circumstances.
//	 */
//	@Test
//	public void testGetPosition() {
//
//		// try to get a position from a null value
//		try {
//			graphExtensions.getPosition(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create a node
//		final Node node = GraphExtensionsTest.createNode(graphExtensions);
//
//		// node's position should be null since it has never been set
//		assertNull(graphExtensions.getPosition(node));
//
//		// Attach a position to the node
//		graphExtensions.setPosition(node, new Coordinate(12, 12));
//
//		// now test whether the attached position is correct
//		assertEquals(new Coordinate(12, 12), graphExtensions.getPosition(node));
//
//	}
//
//	@Test
//	public void testGetVectorBetween() {
//		// Try to test with two null values
//		try {
//			graphExtensions.getVectorBetween(null, null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to pass the second argument as null
//		try {
//			graphExtensions.getVectorBetween(GraphExtensionsTest.createNode(graphExtensions), null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to pass the first argument as null
//		try {
//			graphExtensions.getVectorBetween(null, GraphExtensionsTest.createNode(graphExtensions));
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to pass the both nodes without position data attached
//		try {
//			graphExtensions.getVectorBetween(GraphExtensionsTest.createNode(graphExtensions),
//					GraphExtensionsTest.createNode(graphExtensions));
//			fail("this code should have never been reached");
//		} catch (IllegalStateException ex) {
//			assertTrue(true);
//		}
//
//		// create two nodes with attached position data
//		final Node node1 = graphExtensions.setPosition(GraphExtensionsTest.createNode(graphExtensions),
//				new Coordinate(32, 57));
//		final Node node2 = graphExtensions.setPosition(GraphExtensionsTest.createNode(graphExtensions),
//				new Coordinate(21, 123));
//
//		// try to pass the second node without position data attached
//		try {
//			graphExtensions.getVectorBetween(node1, GraphExtensionsTest.createNode(graphExtensions));
//			fail("this code should have never been reached");
//		} catch (IllegalStateException ex) {
//			assertTrue(true);
//		}
//
//		// try to pass the first node without position data attached
//		try {
//			graphExtensions.getVectorBetween(GraphExtensionsTest.createNode(graphExtensions), node2);
//			fail("this code should have never been reached");
//		} catch (IllegalStateException ex) {
//			assertTrue(true);
//		}
//
//		// now use method normally
//		assertEquals(new Vector2d(11, -66), graphExtensions.getVectorBetween(node1, node2));
//
//		// inverted normal use
//		assertEquals(new Vector2d(-11, 66), graphExtensions.getVectorBetween(node2, node1));
//
//		// same parameter
//		assertEquals(new Vector2d(0, 0), graphExtensions.getVectorBetween(node1, node1));
//	}
//
//	/**
//	 * This method tests whether the "hasPosition"-method returns correct values under various circumstances.
//	 */
//	@Test
//	public void testHasPosition() {
//		// try to use a null node
//		try {
//			graphExtensions.hasPosition(null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// create a node without position data
//		final Node node = GraphExtensionsTest.createNode(graphExtensions);
//
//		// test whether method returns false
//		assertFalse(graphExtensions.hasPosition(node));
//
//		// now attach a position to the node
//		graphExtensions.setPosition(node, new Coordinate(1, 1));
//
//		// test whether method returns false
//		assertTrue(graphExtensions.hasPosition(node));
//	}
//
//	/**
//	 * This method tests whether the position value is set correctly under various circumstances.
//	 */
//	@Test
//	public void testSetPosition() {
//
//		// try to use a null node and a null Vector2d
//		try {
//			graphExtensions.setPosition(null, null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// try to use a null node and a default Vector2d
//		try {
//			graphExtensions.setPosition(null, new Coordinate(0, 0));
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		Node node = GraphExtensionsTest.createNode(graphExtensions);
//
//		// try to use a default node and a null Vector2d
//		try {
//			graphExtensions.setPosition(node, null);
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// now test whether the node returns null for position since it hasn't
//		// been set yet.
//		assertNull(graphExtensions.getPosition(node));
//
//		// set a new position
//		graphExtensions.setPosition(node, new Coordinate(12.3, 1232.231));
//
//		// test whether the position is correctly set
//		assertEquals(new Coordinate(12.3, 1232.231), graphExtensions.getPosition(node));
//
//		// try to change the node's position
//		try {
//			graphExtensions.setPosition(node, new Coordinate(2, 3));
//			fail("this code should have never been reached");
//		} catch (IllegalStateException ex) {
//			assertTrue(true);
//		}
//
//		// try to pass negative position data
//		try {
//			graphExtensions.setPosition(GraphExtensionsTest.createNode(graphExtensions), new Coordinate(-12, 23));
//			fail("this code should have never been reached");
//		} catch (IllegalArgumentException ex) {
//			assertTrue(true);
//		}
//
//		// test method chaining
//		node = GraphExtensionsTest.createNode(graphExtensions);
//		assertSame(node, graphExtensions.setPosition(node, new Coordinate(12, 12)));
//	}
//
//}
