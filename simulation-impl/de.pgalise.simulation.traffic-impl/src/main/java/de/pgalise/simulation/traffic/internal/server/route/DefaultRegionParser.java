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

import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.entity.Way;
import de.pgalise.simulation.shared.tag.LanduseTagEnum;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.MotorWay;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficWay;
import de.pgalise.simulation.traffic.server.route.EnrichedPolygon;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lena
 */
@Stateful
public class DefaultRegionParser implements RegionParser {

	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(
		DefaultRegionParser.class);

	public static boolean isInsidePolygon(Polygon p,
		int x,
		int y) {
		return p.contains(x,
			y);
	}

	private List<TrafficNode> homeNodes = new ArrayList<>();

	private List<TrafficNode> workNodes = new ArrayList<>();

	/**
	 * Constructor
	 *
	 * @param trafficInformation
	 */
	public DefaultRegionParser() {
	}

	@Override
	public List<TrafficNode> getHomeNodes() {
		return this.homeNodes;
	}

	@Override
	public List<TrafficNode> getWorkNodes() {
		return this.workNodes;
	}

	@Override
	public void parseLanduse(TrafficGraph graph,
		CityInfrastructureData trafficInformation) {
		List<EnrichedPolygon> polygons = new ArrayList<>();
		Set<MotorWay> ways = trafficInformation.getMotorWays();
		Set<TrafficWay> landuses = trafficInformation.getLandUseWays();

		for (Way<?, ?> landuse : landuses) {
			int npoints = landuse.getNodeList().size();
			int[] xpoints = new int[npoints];
			int[] ypoints = new int[npoints];

			for (int i = 0; i < landuse.getEdgeList().size(); i++) {
				NavigationNode landuseNode = landuse.getNodeList().get(i);
				int xpoint = (int) (landuseNode.getX() * 10000000);
				xpoints[i] = xpoint;
				int ypoint = (int) (landuseNode.getY() * 10000000);
				ypoints[i] = ypoint;
			}

			polygons.add(new EnrichedPolygon(new Polygon(xpoints,
				ypoints,
				npoints),
				landuse.getLanduseTags()));
		}

		for (MotorWay way : ways) {
			for (TrafficNode node : way.getNodeList()) {
				for (EnrichedPolygon p : polygons) {
					if (p.getPolygon().contains(
						(int) (node.getX() * 10000000),
						(int) (node.getY() * 10000000))) {
						Set<String> landuse = p.getLanduse();
						node.getLanduseTags().addAll(landuse);

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

	public void setHomeNodes(List<TrafficNode> homeNodes) {
		this.homeNodes = homeNodes;
	}

	public void setWorkNodes(List<TrafficNode> workNodes) {
		this.workNodes = workNodes;
	}
}
