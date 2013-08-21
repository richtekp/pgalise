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
 
package de.pgalise.simulation.traffic.route;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Vector2d;

/**
 * @author Marcus
 * @version 1.0 (Mar 20, 2013)
 */
@Ignore
public class ParalellableDijkstraTest {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(PathTest.class);

	@Test
	public void test() {
		Graph graph = new SingleGraph("Tutorial 1");

		double velocity = 50;
		double distance = 200;

		// log.debug("velocity = " + velocity);
		// log.debug("distance 2000m = " + distance);
		Node a = graph.addNode("a");
		a.addAttribute("position", new Vector2d(0, 0));

		Node b = graph.addNode("b");
		b.addAttribute("position", new Vector2d(distance, 0));

		Node c = graph.addNode("c");
		c.addAttribute("position", new Vector2d(distance, distance));

		Edge ab = graph.addEdge("ab", "a", "b");
		ab.addAttribute("distance", distance);
		ab.addAttribute("maxVelocity", velocity);
		ab.addAttribute("weight", distance / velocity);
		log.debug("Weight of ab: " + ab.getAttribute("weight"));

		Edge bc = graph.addEdge("bc", "b", "c");
		bc.addAttribute("distance", distance);
		bc.addAttribute("maxVelocity", velocity);
		bc.addAttribute("weight", distance / velocity);
		log.debug("Weight of bc: " + bc.getAttribute("weight"));

		Edge ac = graph.addEdge("ac", "a", "c");
		Vector2d v = (Vector2d) a.getAttribute("position");
		v.sub((Vector2d) c.getAttribute("position"));
		double length = v.length();

		ac.addAttribute("distance", length);
		ac.addAttribute("maxVelocity", velocity);
		ac.addAttribute("weight", length / velocity);
		log.debug("Weight of ac: " + ac.getAttribute("weight"));
	}
}
