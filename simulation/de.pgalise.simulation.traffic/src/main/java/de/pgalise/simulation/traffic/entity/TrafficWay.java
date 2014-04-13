/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.shared.entity.Way;
import java.util.List;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class TrafficWay extends Way<TrafficEdge, TrafficNode> {

	private static final long serialVersionUID = 1L;

	protected TrafficWay() {
	}
  
  public TrafficWay(Long id) {
    super(id);
  }

	public TrafficWay(Long id,
		List<TrafficEdge> edgeList,
		String streetname) {
		super(id,edgeList,
			streetname);
	}

	public void applyMaxSpeed(int maxSpeed) {
		for (TrafficEdge edge : getEdgeList()) {
			if (edge.getMaxSpeed() != -1) {
				throw new IllegalStateException(String.format(
					"oneWay flag of edge %s already set (flags on all edges have to be null before a whole way can be set to one way)",
					edge));
			}
			edge.setMaxSpeed(maxSpeed);
		}
	}

}
