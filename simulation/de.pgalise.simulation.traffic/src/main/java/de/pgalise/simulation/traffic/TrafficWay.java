/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.Way;
import java.util.List;

/**
 *
 * @author richter
 */
public class TrafficWay extends Way<TrafficEdge, TrafficNode> {

	private static final long serialVersionUID = 1L;

	protected TrafficWay() {
	}

	public TrafficWay(
		List<TrafficEdge> edgeList,
		String streetname) {
		super(edgeList,
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
