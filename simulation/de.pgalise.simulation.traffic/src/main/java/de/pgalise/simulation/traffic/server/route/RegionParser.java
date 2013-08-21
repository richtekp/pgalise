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

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.Node;
import de.pgalise.simulation.shared.city.Way;

/**
 * @author Lena
 */
public class RegionParser {

	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(RegionParser.class);

	public static boolean isInsidePolygon(Polygon p, int x, int y) {
		if (p.contains(x, y)) {
			return true;
		} else {
			return false;
		}
	}

	private List<org.graphstream.graph.Node> homeNodes = new ArrayList<org.graphstream.graph.Node>();

	private CityInfrastructureData trafficInformation;

	private List<org.graphstream.graph.Node> workNodes = new ArrayList<org.graphstream.graph.Node>();

	/**
	 * Constructor
	 * 
	 * @param gpsMapper
	 * @param trafficInformation
	 */
	public RegionParser(CityInfrastructureData trafficInformation) {
		this.trafficInformation = trafficInformation;
	}

	public List<org.graphstream.graph.Node> getHomeNodes() {
		return this.homeNodes;
	}

	public List<org.graphstream.graph.Node> getWorkNodes() {
		return this.workNodes;
	}

	public void parseLanduse(Graph graph) {
		List<EnrichedPolygon> polygons = new ArrayList<EnrichedPolygon>();
		List<Way> ways = this.trafficInformation.getMotorWays();
		List<Way> landuses = this.trafficInformation.getLandUseWays();

		for (Way landuse : landuses) {
			int npoints = landuse.getNodeList().size();
			int[] xpoints = new int[npoints];
			int[] ypoints = new int[npoints];

			for (int i = 0; i < landuse.getNodeList().size(); i++) {
				Node landuseNode = landuse.getNodeList().get(i);
				int xpoint = (int) (landuseNode.getLatitude() * 10000000);
				xpoints[i] = xpoint;
				int ypoint = (int) (landuseNode.getLongitude() * 10000000);
				ypoints[i] = ypoint;
			}

			polygons.add(new EnrichedPolygon(new Polygon(xpoints, ypoints, npoints), landuse.getLanduse()));
		}

		for (Way way : ways) {
			for (Node node : way.getNodeList()) {
				for (EnrichedPolygon p : polygons) {
					if (p.getPolygon().contains((int) (node.getLatitude() * 10000000),
							(int) (node.getLongitude() * 10000000))) {
						String landuse = p.getLanduse();
						node.setLanduse(landuse);

						org.graphstream.graph.Node graphNode = graph.getNode(node.getId());

						if (graphNode != null) {
							if (landuse.equals("residential")) {
								this.homeNodes.add(graphNode);
							}
							if (landuse.equals("commercial")) {
								this.workNodes.add(graphNode);
							}
							if (landuse.equals("industrial")) {
								this.workNodes.add(graphNode);
							}
						}
					}
				}
			}
		}
		log.info("Found #HomeNodes: " + this.homeNodes.size());
		log.info("Found #WorkNodes: " + this.workNodes.size());
	}

	public void setHomeNodes(List<org.graphstream.graph.Node> homeNodes) {
		this.homeNodes = homeNodes;
	}

	public void setWorkNodes(List<org.graphstream.graph.Node> workNodes) {
		this.workNodes = workNodes;
	}
}
