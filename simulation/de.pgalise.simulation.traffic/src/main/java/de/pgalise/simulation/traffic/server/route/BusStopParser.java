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
 
package de.pgalise.simulation.traffic.server.route;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.traffic.BusStop;

/**
 * @author Lena
 */
public class BusStopParser {
	private static final Logger log = LoggerFactory.getLogger(BusStopParser.class);

	private CityInfrastructureData trafficInformation;
	private List<BusStop> busStops;

	public BusStopParser(CityInfrastructureData trafficInformation) {
		this.trafficInformation = trafficInformation;
	}

	
	/**
	 * Parses GTFS busstops to the graph (by use of FindNearestNode)
	 * 
	 * @param graph
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public List<BusStop> parseBusStops(Graph graph) {
		this.busStops = new ArrayList<>();
		int count = 0;

		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			Connection con = DriverManager.getConnection("jdbc:derby://127.0.0.1:5201/database", "pgalise",
					"somepw");

			String query = "select stop_name, stop_id, stop_lat, stop_lon from pgalise.bus_stops";

			Statement s = con.createStatement();
			ResultSet rs = s.executeQuery(query);

			while (rs.next()) {
				double lat = rs.getDouble("stop_lat");
				double lon = rs.getDouble("stop_lon");
				Boundary boundary = this.trafficInformation.getBoundary();
				double latSW = boundary.getSouthWest().x;
				double lonSW = boundary.getSouthWest().y;
				double latNE = boundary.getNorthEast().x;
				double lonNE = boundary.getNorthEast().y;
				if ((lat > latSW) && (lat < latNE) && (lon > lonSW) && (lon < lonNE)) {
					de.pgalise.simulation.shared.city.Node osmNode = this.trafficInformation.getNearestNode(lat,
							lon);
					org.graphstream.graph.Node node = graph.getNode(osmNode.getId());
					if (node != null) {
						BusStop stop = new BusStop(rs.getString("stop_id"), rs.getString("stop_name"), node);
						this.busStops.add(stop);
					} else {
						log.debug("No node found next to the busstop '" + rs.getString("stop_name"));
					}
				} else {
					count++;
					// log.debug("'" + rs.getString("stop_name") + "' is outside boundary");
				}
			}

			con.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		log.info("Number of parsed BusStops: " + this.busStops.size());
		log.info("Number of parsed BusStops being outside of the boundary: " + count);

		return this.busStops;
	}

	/**
	 * @return the busStops
	 */
	public List<BusStop> getBusStops() {
		return this.busStops;
	}

	/**
	 * @param busStops
	 *            the busStops to set
	 */
	public void setBusStops(List<BusStop> busStops) {
		this.busStops = busStops;
	}
}
