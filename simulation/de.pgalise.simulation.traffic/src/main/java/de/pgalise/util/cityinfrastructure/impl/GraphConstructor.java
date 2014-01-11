/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.cityinfrastructure.impl;

import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficWay;
import java.util.List;

/**
 *
 * @author richter
 */
public interface GraphConstructor {

	/**
	 * Create graph
	 *
	 * @param ways List of ways
	 * @return Graph
	 */
	TrafficGraph createGraph(
		List<TrafficWay> ways);
	
}
