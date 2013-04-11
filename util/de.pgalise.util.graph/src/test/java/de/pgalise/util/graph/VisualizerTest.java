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
 
package de.pgalise.util.graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.util.generic.MutableBoolean;
import de.pgalise.util.generic.function.Function;
import de.pgalise.util.vector.Vector2d;

/**
 * Tests the {@link GraphVisualizer}
 * 
 * @author Mustafa
 * @author Marina
 * @version 1.0 (Nov 22, 2012)
 */
@Ignore
public class VisualizerTest {
	@Test
	public void visualizeTest() throws InterruptedException {
		final MutableBoolean exit = new MutableBoolean(false);
		// Thread t = new Thread(new Runnable() {
		// @Override
		// public void run() {
		// while(!exit.getValue()) {
		// }
		// }
		// });
		// t.start();

		Graph graph = createGraph();

		GraphVisualizer viz = new DefaultGraphVisualizer(400, 400, graph);
		viz.addWindowCloseListener(new Function() {

			@Override
			public void delegate() {
				System.out.println("EXIT");
				exit.setValue(true);
			}

		});
		viz.scale(30, 30);

		while (!exit.getValue()) {
			viz.draw();
		}

		// t.join();
	}

	/**
	 * Create test graph
	 * 
	 * @return graph
	 */
	private Graph createGraph() {
		Graph graph = new SingleGraph("city");
		Node a = this.addNode(graph, "a", 1, 1);
		Node b = this.addNode(graph, "b", 2, 2);
		Node c = this.addNode(graph, "c", 4, 7);
		Node d = this.addNode(graph, "d", 9, 4);
		Node e = this.addNode(graph, "e", 11, 7);
		Node f = this.addNode(graph, "f", 2, 10);

		Edge ab = null;
		ab = graph.addEdge("ab", a, b);
		ab.setAttribute("weight", 1);
		Edge bc = null;
		bc = graph.addEdge("bc", b, c);
		bc.setAttribute("weight", 1);
		Edge cd = null;
		cd = graph.addEdge("cd", c, d);
		cd.setAttribute("weight", 1);
		Edge de = null;
		de = graph.addEdge("de", d, e);
		de.setAttribute("weight", 1);
		Edge fc = null;
		fc = graph.addEdge("fc", f, c);
		fc.setAttribute("weight", 1);
		return graph;
	}

	/**
	 * Add node to graph
	 * 
	 * @param graph
	 *            graph
	 * @param id
	 *            ID
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @return Node
	 */
	private Node addNode(Graph graph, String id, double x, double y) {
		Node node = graph.addNode(id);
		node.setAttribute("position", Vector2d.valueOf(x, y));
		return node;
	}
}
