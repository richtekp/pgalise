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
 
package de.pgalise.simulation.shared.traffic;

import org.graphstream.graph.Node;

/**
 * Contains all information for a bus stop.
 * The id, name and the graph {@link Node}.
 * @author Lena
 */
public class BusStop {
	private String stopId;
	private String stopName;
	private Node graphNode;
	
	public BusStop(String id, String name, Node node) {
		this.stopId = id;
		this.stopName = name;
		this.graphNode = node;
	}

	/**
	 * @return the stopId
	 */
	public String getStopId() {
		return stopId;
	}

	/**
	 * @param stopId the stopId to set
	 */
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}

	/**
	 * @return the stopName
	 */
	public String getStopName() {
		return stopName;
	}

	/**
	 * @param stopName the stopName to set
	 */
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	/**
	 * @return the graphNode
	 */
	public Node getGraphNode() {
		return graphNode;
	}

	/**
	 * @param graphNode the graphNode to set
	 */
	public void setGraphNode(Node graphNode) {
		this.graphNode = graphNode;
	}
}
