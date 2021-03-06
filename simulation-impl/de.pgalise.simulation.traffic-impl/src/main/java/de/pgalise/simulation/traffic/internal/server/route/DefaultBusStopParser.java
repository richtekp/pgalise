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
package de.pgalise.simulation.traffic.internal.server.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.server.route.BusStopParser;
import java.util.LinkedList;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @author Lena
 */
@Stateful
public class DefaultBusStopParser implements BusStopParser {

	private static final Logger log = LoggerFactory.getLogger(
		DefaultBusStopParser.class);

	private List<BusStop> busStops = new LinkedList<>();
	@PersistenceContext(unitName = "pgalise-traffic")
	private EntityManager entityManager;

	public DefaultBusStopParser() {
	}

	/**
	 * Parses GTFS busstops to the graph (by use of FindNearestNode)
	 *
	 * @param graph
	 * @return
	 */
	/*
	 @TODO: no interaction with trafficGraph (check original version before JPA)
	 */
	@Override
	public List<BusStop> parseBusStops(TrafficGraph graph) {
		TypedQuery<BusStop> query = entityManager.createQuery(
			"SELECT x FROM BusStop x",
			BusStop.class);
		List<BusStop> queriedBusStops = query.getResultList();
		log.info("Number of parsed BusStops: %d",
			this.busStops.size());
		log.info("Number of parsed BusStops being outside of the boundary: %d",
			queriedBusStops.size() - this.busStops.size());
		return this.busStops;
	}

	/**
	 * @return the busStops
	 */
	@Override
	public List<BusStop> getBusStops() {
		return this.busStops;
	}

	/**
	 * @param busStops the busStops to set
	 */
	@Override
	public void setBusStops(List<BusStop> busStops) {
		this.busStops = busStops;
	}
}
