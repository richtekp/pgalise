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

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultGraphVisualizer;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.util.generic.MutableBoolean;
import de.pgalise.util.generic.function.Function;
import de.pgalise.simulation.shared.city.Vector2d;

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

		TrafficGraph graph = createGraph();

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
	private TrafficGraph createGraph() {
		TrafficGraph graph = new DefaultTrafficGraph();
		TrafficNode a = new TrafficNode(new Coordinate(1,1));
		TrafficNode b = new TrafficNode(new Coordinate(2,2));
		TrafficNode c = new TrafficNode(new Coordinate(4, 7));
		TrafficNode d = new TrafficNode(new Coordinate(9, 4));
		TrafficNode e = new TrafficNode(new Coordinate(11, 7));
		TrafficNode f = new TrafficNode(new Coordinate(2, 10));
		TrafficEdge e1 = graph.addEdge(a,b);
		TrafficEdge e2 = graph.addEdge(b,c);
		TrafficEdge e3 = graph.addEdge(c,d);
		TrafficEdge e4 = graph.addEdge(d,e);
		TrafficEdge e5 = graph.addEdge(f,c);
		graph.setEdgeWeight(e1,1);
		graph.setEdgeWeight(e2,1);
		graph.setEdgeWeight(e3,1);
		graph.setEdgeWeight(e4,1);
		graph.setEdgeWeight(e5,1);
		return graph;
	}
}
