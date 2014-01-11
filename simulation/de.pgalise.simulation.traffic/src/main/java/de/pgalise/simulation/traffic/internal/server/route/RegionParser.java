/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.route;

import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficNode;
import java.util.List;

/**
 *
 * @author richter
 */
public interface RegionParser {

	void parseLanduse(TrafficGraph graph,
		CityInfrastructureData cityInfrastructureData);

	List<TrafficNode> getHomeNodes();

	List<TrafficNode> getWorkNodes();
}
