/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.server.route;

import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.TrafficGraph;
import java.util.List;

/**
 *
 * @author richter
 */
public interface BusStopParser {

	/**
	 * @return the busStops
	 */
	List<BusStop> getBusStops();

	/**
	 * Parses GTFS busstops to the graph (by use of FindNearestNode)
	 *
	 * @param graph
	 * @return
	 */
	List<BusStop> parseBusStops(TrafficGraph graph);

	/**
	 * @param busStops
	 *            the busStops to set
	 */
	void setBusStops(List<BusStop> busStops);
	
}
