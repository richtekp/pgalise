/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.Way;
import java.util.List;

/**
 *
 * @author richter
 */
public abstract class TrafficInfrastructureData<E extends NavigationEdge<N>, N extends NavigationNode, W extends Way<E, N>>
	extends CityInfrastructureData<N, E, W> {

	private static final long serialVersionUID = 1L;
	private List<TrafficNode> roundAbouts;

	public List<TrafficNode> getRoundAbouts() {
		return roundAbouts;
	}

	public void setRoundAbouts(
		List<TrafficNode> roundAbouts) {
		this.roundAbouts = roundAbouts;
	}

	public abstract TrafficGraph getTrafficGraph();
}
