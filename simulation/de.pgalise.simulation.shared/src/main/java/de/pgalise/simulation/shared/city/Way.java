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
 
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.LanduseTagEnum;
import de.pgalise.simulation.shared.city.WayTagEnum;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;

/**
 * A way is logical union of {@link NavigationEdge}s, i.e. a highway with a number. Information about oneway limilations or speed limits are encapsulated in {@link NavigationEdge} because these constraints might not apply to the entire way as definded above.
 * @param <E> 
 * @param <N> 
 * @author Timo
 */
public class Way<E extends NavigationEdge<N,E>, N extends NavigationNode> implements Serializable {
	private static final long serialVersionUID = 2942128399393060939L;
	private List<E> edgeList;
	private String streetName;
	private Set<WayTagEnum> tags;
	private Set<LanduseTagEnum> landuseTags;
	
	/**
	 * Default
	 */
	protected Way() {}
	
	/**
	 * Constructor
	 * @param edgeList 
	 * @param streetname 
	 * @param tags 
	 */
	public Way(List<E> edgeList, String streetname, Set<WayTagEnum> tags) {
		this(edgeList,
			streetname);
		this.tags = tags;
		this.landuseTags = new HashSet<>();
	}

	public Way(
		List<E> edgeList,
		String streetname) {
		this.edgeList = edgeList;
		this.streetName = streetname;
		this.tags = new HashSet<>();
		this.landuseTags = new HashSet<>();
	}

	public Way(
		List<E> edgeList,
		String streetname,
		Set<WayTagEnum> tags,
		Set<LanduseTagEnum> landuseTags) {
		this(edgeList,
			streetname,
			tags);
		this.landuseTags = landuseTags;
	}

	public Collection<WayTagEnum> getTags() {
		return tags;
	}

	public void setLanduseTags(
		Set<LanduseTagEnum> landuseTags) {
		this.landuseTags = landuseTags;
	}

	public Set<LanduseTagEnum> getLanduseTags() {
		return landuseTags;
	}

	public void setTags(Set<WayTagEnum> tags) {
		this.tags = tags;
	}

	public List<E> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<E> edgeList) {
		this.edgeList = edgeList;
	}

	public String getStreetName() {
		return this.streetName;
	}

	public void setStreetName(String streetname) {
		this.streetName = streetname;
	}
	
	/**
	 * retrieves the nodes of the way for the edge list. Usage is discouraged 
	 * due to low performance and unnecessary copy (all information that can be retrieved from the node list might as well be retrieved from the edge list)
	 * @return 
	 */
	public List<N> getNodeList() {
		List<N> retValue = new LinkedList<>();
		retValue.add(edgeList.get(0).getSource());
		for(NavigationEdge<N,E> edge : edgeList) {
			retValue.add(edge.getTarget());
		}
		return retValue;
	}
	
	public void setEdgeList(List<N> nodes, DirectedGraph<N,E> graph) {
		List<E> edgeList0 = new LinkedList<>();
		Iterator<N> it = nodes.iterator();
		N last = it.next();
		while(it.hasNext()) {
			N current = it.next();
			E edge = graph.getEdge(last,
				current);
			if(edge != null) {
				edgeList0.add(edge);
			}
		}
		this.edgeList = edgeList0;
	}
	
	public void setOneWay() {
		for(E edge : edgeList) {
			if(!edge.)
		}
	}
}
