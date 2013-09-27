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

import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import de.pgalise.simulation.shared.city.LanduseTagEnum;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.shared.city.Way;
import java.util.Set;

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

	private List<NavigationNode> homeNodes = new ArrayList<>();

	private CityInfrastructureData trafficInformation;

	private List<NavigationNode> workNodes = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param trafficInformation
	 */
	public RegionParser(CityInfrastructureData trafficInformation) {
		this.trafficInformation = trafficInformation;
	}

	public List<NavigationNode> getHomeNodes() {
		return this.homeNodes;
	}

	public List<NavigationNode> getWorkNodes() {
		return this.workNodes;
	}

	public void parseLanduse(TrafficGraph graph) {
		List<EnrichedPolygon> polygons = new ArrayList<>();
		List<Way<?, ?>> ways = this.trafficInformation.getMotorWays();
		List<Way<?,?>> landuses = this.trafficInformation.getLandUseWays();

		for (Way<?,?> landuse : landuses) {
			int npoints = landuse.getNodeList().size();
			int[] xpoints = new int[npoints];
			int[] ypoints = new int[npoints];

			for (int i = 0; i < landuse.getEdgeList().size(); i++) {
				NavigationNode landuseNode = landuse.getNodeList().get(i);
				int xpoint = (int) (landuseNode.getGeoLocation().x * 10000000);
				xpoints[i] = xpoint;
				int ypoint = (int) (landuseNode.getGeoLocation().y * 10000000);
				ypoints[i] = ypoint;
			}

			polygons.add(new EnrichedPolygon(new Polygon(xpoints, ypoints, npoints), landuse.getLanduseTags()));
		}

		for (Way<?,?> way : ways) {
			for (NavigationNode node : way.getNodeList()) {
				for (EnrichedPolygon p : polygons) {
					if (p.getPolygon().contains((int) (node.getGeoLocation().x* 10000000),
							(int) (node.getGeoLocation().y* 10000000))) {
						Set<LanduseTagEnum> landuse = p.getLanduse();
						node.getTags().put(CityNodeTagCategoryEnum.landuse, landuse);

						if (landuse.contains(LanduseTagEnum.RESIDENTIAL)) {
							this.homeNodes.add(node);
						}
						if (landuse.contains(LanduseTagEnum.COMMERCIAL)) {
							this.workNodes.add(node);
						}
						if (landuse.contains(LanduseTagEnum.INDUSTRY)) {
							this.workNodes.add(node);
						}
					}
				}
			}
		}
		log.info("Found #HomeNodes: " + this.homeNodes.size());
		log.info("Found #WorkNodes: " + this.workNodes.size());
	}

	public void setHomeNodes(List<NavigationNode> homeNodes) {
		this.homeNodes = homeNodes;
	}

	public void setWorkNodes(List<NavigationNode> workNodes) {
		this.workNodes = workNodes;
	}
}
