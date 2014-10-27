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
package de.pgalise.simulation.shared.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.jgrapht.DirectedGraph;

/**
 * A way is logical union of {@link NavigationEdge}s, i.e. a highway with a
 * number. Information about oneway limilations or speed limits are encapsulated
 * in {@link NavigationEdge} because these constraints might not apply to the
 * entire way as definded above.
 *
 * @param <E>
 * @param <N>
 * @author Timo
 */
/*
Do not use the term way like in OSM specification because it is highly contra-
intuitive and not useful for this simulation. There's no reason to rely to 
OSM's (tag based) data model because it makes no use to object oriented 
programming features.
*/
@Entity
public class Way<E extends NavigationEdge<N>, N extends NavigationNode> extends Identifiable {

	private static final long serialVersionUID = 2942128399393060939L;
	@OneToMany(targetEntity = NavigationEdge.class)
	private List<E> edgeList = new LinkedList<>();
	@ElementCollection
	private Set<String> tags = new HashSet<>();
	@ElementCollection
	private Set<String> landuseTags = new HashSet<>();
  /**
   * an identifer code, like highway E7, bicycle route R6, etc. 
   */
  private String wayIdentifier = "";

	/**
	 * Default
	 */
	protected Way() {
	}
  
  public Way(Long id) {
    super(id);
  }

	public Way(Long id,
		List<E> edgeList) {
    this(id, edgeList,  new HashSet<String>());
	}

	/**
	 * Constructor
	 *
	 * @param edgeList
	 * @param streetname
	 * @param wayTags
	 */
	public Way(Long id,
    List<E> edgeList,
		Set<String> wayTags) {
		this(id,edgeList,
			 wayTags, new HashSet<String>());
	}

	public Way(Long id,
		List<E> edgeList,
		Set<String> tags,
		Set<String> landuseTags) {
    this(id);
    this.edgeList = edgeList;
    this.tags = tags;
		this.landuseTags = landuseTags;
	}

	public Set<String> getWayTags() {
		return tags;
	}

	public void setLanduseTags(
		Set<String> landuseTags) {
		this.landuseTags = landuseTags;
	}

	public Set<String> getLanduseTags() {
		return landuseTags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public List<E> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<E> edgeList) {
		this.edgeList = edgeList;
	}

  public void setWayIdentifier(String wayIdentifier) {
    this.wayIdentifier = wayIdentifier;
  }

  public String getWayIdentifier() {
    return wayIdentifier;
  }

	/**
	 * retrieves the nodes of the way for the edge list. Usage is discouraged due
	 * to low performance and unnecessary copy (all information that can be
	 * retrieved from the node list might as well be retrieved from the edge list)
	 *
	 * @return
	 */
	public List<N> getNodeList() {
		List<N> retValue = new LinkedList<>();
		if (edgeList.isEmpty()) {
			return retValue;
		}
		retValue.add(edgeList.get(0).getSource());
		for (E edge : edgeList) {
			retValue.add(edge.getTarget());
		}
		return retValue;
	}

	public <G extends DirectedGraph<N, E>> void setEdgeList(List<N> nodes,
		G graph) {
		List<E> edgeList0 = new LinkedList<>();
		Iterator<N> it = nodes.iterator();
		N last = it.next();
		while (it.hasNext()) {
			N current = it.next();
			E edge = graph.getEdge(last,
				current);
			if (edge != null) {
				edgeList0.add(edge);
			}
		}
		this.edgeList = edgeList0;
	}

	/**
	 * sets the oneWay flag on every edge on the say only if all oneWay flags are
	 * <code>null</code>, thr ows @{@link IllegalStateException} otherwise. This
	 * avoids unintended overwriting of information.
	 *
	 * @param oneWay
	 */
	public void applyOneWay(boolean oneWay) {
		for (E edge : edgeList) {
			if (edge.isOneWay() != null) {
				throw new IllegalStateException(String.format(
					"oneWay flag of edge %s already set (flags on all edges have to be null before a whole way can be set to one way)",
					edge));
			}
			edge.setOneWay(oneWay);
		}
	}
}
