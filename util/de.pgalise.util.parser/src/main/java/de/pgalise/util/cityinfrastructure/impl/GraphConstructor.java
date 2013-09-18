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

import java.util.List;
import java.util.UUID;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.city.Node;
import de.pgalise.simulation.shared.city.Way;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.graphextension.GraphExtensions;
import javax.vecmath.Vector2d;

/**
 * The GraphConstrcutor is used to construct a graph based upon the results of the OSMParser.
 * 
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
public class GraphConstructor {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(GraphConstructor.class);

	/**
	 * Graph extension
	 */
	private GraphExtensions graphExtension;

	/**
	 * Constructor
	 * 
	 * @param graphExtensions
	 *            Graph extension
	 */
	public GraphConstructor(GraphExtensions graphExtensions) {
		this.graphExtension = graphExtensions;
	}

	/**
	 * Create graph
	 * 
	 * @param graphName
	 *            Name
	 * @param ways
	 *            List of ways
	 * @return Graph
	 */
	public Graph createGraph(String graphName, List<Way> ways) {
		if (ways == null) {
			return null;
		}

		Graph graph = new SingleGraph(graphName);
		// int i = 0;
		// String cmd="";
		int countEdges = 0;
		int countMissingEdges = 0;
		for (Way way : ways) {
			org.graphstream.graph.Node prevNode = null;
			for (Node osmNode : way.getNodeList()) {
				org.graphstream.graph.Node node = graph.getNode(osmNode.getId());
				if (node == null) {
					node = graph.addNode(osmNode.getId());
					Coordinate v = new Coordinate(osmNode.getLatitude(), osmNode.getLongitude());
					node.setAttribute("position", v);

					// cmd +=
					// "myLatlng["+i+"] = new google.maps.LatLng("+osmNode.getLatitude()+","+osmNode.getLongitude()+");\n";
					// i++;
				}
				if (prevNode != null) {
					// log.debug(String.format("adding edge between node %s and %s", prevNode, node));
					try {
						org.graphstream.graph.Edge edge = graph.addEdge(UUID.randomUUID().toString(), prevNode, node);
						Coordinate b = this.graphExtension.getPosition(node);
						Coordinate a = this.graphExtension.getPosition(prevNode);
						Vector2d bVector = new Vector2d(b.x, b.y);
						Vector2d aVector = new Vector2d(a.x, a.y);
						bVector.sub(aVector);
						double length = bVector.length();
						double vel = way.getMaxSpeed();
						edge.addAttribute("length", length);
						edge.addAttribute("velocity", vel);
						double weight = length / vel;
						double cycle = length / vel;
						if (way.getHighway() != null) {
							if (way.getHighway().equalsIgnoreCase("cycleway")) {
								weight = 0.0;
							} else if (way.getHighway().equalsIgnoreCase("motorway")
									|| way.getHighway().equalsIgnoreCase("motorway_link")
									|| way.getHighway().equalsIgnoreCase("trunk")
									|| way.getHighway().equalsIgnoreCase("trunk_link")) {
								cycle = 0.0;
							}
						}
						if (way.getCycleway() != null) {
							weight = 0.0;
							cycle = length / vel;
						}
						edge.addAttribute("weight", weight);
						edge.addAttribute("cycle", cycle);
						countEdges++;
					} catch (EdgeRejectedException e) {
						// log.error(String.format("Could not add edge between node %s and %s", prevNode, node));
						countMissingEdges++;
					}
					/*
					 * Hier Kante mit Informationen füllen...
					 */
				}
				prevNode = node;
			}
		}
		log.info(String.format("Graph constructed. Added %s edges, missing %s edges ", countEdges, countMissingEdges));
		// log.info(cmd);
		return graph;
	}

}
