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
 
package de.pgalise.simulation.traffic.model;

import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficEdge;
import java.util.Set;

/**
 * Represents a road barrier on the graph
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Feb 19, 2013)
 */
public class RoadBarrier {

	/**
	 * Start timestamp of the road barrier
	 */
	private long start;

	/**
	 * End timestamp of the road barrier
	 */
	private long end;

	/**
	 * List of removed edges
	 */
	private Set<TrafficEdge> edges;

	/**
	 * Blocked node
	 */
	private NavigationNode node;

	/**
	 * Constructor
	 * 
	 * @param node
	 *            Blocked node
	 * @param edges
	 *            List of removed edges
	 * @param start
	 *            Start timestamp of the road barrier
	 * @param end
	 *            End timestamp of the road barrier
	 */
	public RoadBarrier(NavigationNode node, Set<TrafficEdge> edges, long start, long end) {
		this.node = node;
		this.edges = edges;
		this.start = start;
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public Set<TrafficEdge> getEdges() {
		return edges;
	}

	public void setEdges(Set<TrafficEdge> edges) {
		this.edges = edges;
	}

	public NavigationNode getNode() {
		return node;
	}

	public void setNode(NavigationNode node) {
		this.node = node;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RoadBarrier other = (RoadBarrier) obj;
		if (end != other.end) {
			return false;
		}
		if (node == null) {
			if (other.node != null) {
				return false;
			}
		} else if (!node.equals(other.node)) {
			return false;
		}
		if (start != other.start) {
			return false;
		}
		return true;
	}

}
