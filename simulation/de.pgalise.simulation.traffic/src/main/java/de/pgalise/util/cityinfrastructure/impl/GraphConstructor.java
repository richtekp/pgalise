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
package de.pgalise.util.cityinfrastructure.impl;

import de.pgalise.simulation.shared.city.Way;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficWay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import java.util.List;

/**
 * The GraphConstrcutor is used to construct a graph based upon the results of
 * the OSMParser.
 *
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
public class GraphConstructor {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(
		GraphConstructor.class);

	/**
	 * Constructor
	 *
	 */
	public GraphConstructor() {
	}

	/**
	 * Create graph
	 *
	 * @param ways List of ways
	 * @return Graph
	 */
	public TrafficGraph createGraph(
		List<TrafficWay> ways) {
		if (ways == null) {
			return null;
		}

		TrafficGraph graph = new DefaultTrafficGraph();
		int countEdges = 0;
		for (Way<?, TrafficNode> way : ways) {
			TrafficNode prevNode = null;
			for (TrafficNode node : way.getNodeList()) {
				if (node == null) {
					graph.addVertex(node);
				}
				if (prevNode != null) {
					TrafficEdge edge = graph.addEdge(
						prevNode,
						node);
					countEdges++;
				}
				prevNode = node;
			}
		}
		log.info(String.format(
			"Graph constructed. Added %s edges",
			countEdges
		));
		return graph;
	}

}
