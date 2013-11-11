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
 
package de.pgalise.cityinfrastructure;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.Way;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.Building;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.BusStop;
import de.pgalise.util.cityinfrastructure.impl.DefaultBuildingEnergyProfileStrategy;
import de.pgalise.simulation.traffic.OSMCityInfrastructureData;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;

/**
 * Several JUnit test cases for the OSMParser.
 * 
 * @author Timo
 */
public class OSMCityInfrastructureDataTest {

	/**
	 * Test class
	 */
	private static OSMCityInfrastructureData osmParser;
	private static TrafficGraph graph = new DefaultTrafficGraph();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			osmParser = new OSMCityInfrastructureData(
					OSMCityInfrastructureData.class.getResourceAsStream("/oldenburg_pg.osm"),
					OSMCityInfrastructureData.class.getResourceAsStream("/stops.txt"),
					new DefaultBuildingEnergyProfileStrategy(), graph);
		} catch (IOException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	/**
	 * Tests the get nearest node method.
	 */
	@Test
	public void getNearestNodeTest() {
		/* Take one node and test, if it will be returned: */
		NavigationNode givenNode = osmParser.getNodes().get((int) (Math.random() * osmParser.getNodes().size()));
		NavigationNode returnedNode = osmParser.getNearestNode(givenNode.getGeoLocation().x, givenNode.getGeoLocation().y);
		assertTrue(givenNode.getGeoLocation().x == returnedNode.getGeoLocation().x
				&& givenNode.getGeoLocation().y == returnedNode.getGeoLocation().y);
	}

	/**
	 * Tests if the northeast value is bigger, than the southwest value.
	 */
	@Test
	public void boundaryTest() {
		Boundary boundary = osmParser.getBoundary();
		assertTrue(boundary.getNorthEast().x > boundary.getSouthWest().x
				&& boundary.getNorthEast().y > boundary.getSouthWest().y);
	}

	/**
	 * Tests if there are bus stops as nodes.
	 */
	@Test
	public void busstoptest() {
		boolean foundBusstop = false;
		if (osmParser == null) {
			System.out.println("osmParser == null");
		}
		outerLoop: for (Way<?,?> way : osmParser.getMotorWaysWithBusstops()) {
			for (NavigationNode node : way.getNodeList()) {
				if (node instanceof BusStop) {
					foundBusstop = true;
					break outerLoop;
				}
			}
		}

		assertTrue(foundBusstop);
	}

	@Test
	public void getBuildingsInRadiusTest() {
		int radiusInMeter = 500;
		NavigationNode tmpNode = osmParser.getNodes().get((int) (Math.random() * osmParser.getNodes().size()));
		Coordinate centerPoint = new Coordinate(tmpNode.getGeoLocation().x, tmpNode.getGeoLocation().y);
		for (Building building : osmParser.getBuildingsInRadius(centerPoint, radiusInMeter)) {
			assertTrue(this.getDistanceInMeter(centerPoint, building.getPosition().getCenterPoint()) <= radiusInMeter);
		}
	}

	/**
	 * Tests the getNearestStreetNode method.
	 */
	@Test
	public void getNearestStreetNode() {
		boolean isDifferent = false;
		for (NavigationNode node : osmParser.getNodes()) {
			if (!node.equals(osmParser.getNearestStreetNode(node.getGeoLocation().x, node.getGeoLocation().y))) {
				isDifferent = true;
				break;
			}
		}
		assertTrue(isDifferent);
	}

	/**
	 * Gives the distance in meter between start and target.
	 * 
	 * @param start
	 *            Position
	 * @param target
	 *            Position
	 * @return distance
	 */
	private double getDistanceInMeter(Coordinate start, Coordinate target) {

		if ((start.x == target.x)
				&& (start.y == target.y)) {
			return 0.0;
		}

		double f = 1 / 298.257223563;
		double a = 6378.137;
		double F = ((start.x + target.x) / 2) * (Math.PI / 180);
		double G = ((start.x - target.x) / 2) * (Math.PI / 180);
		double l = ((start.y - target.y) / 2) * (Math.PI / 180);
		double S = Math.pow(Math.sin(G), 2) * Math.pow(Math.cos(l), 2) + Math.pow(Math.cos(F), 2)
				* Math.pow(Math.sin(l), 2);
		double C = Math.pow(Math.cos(G), 2) * Math.pow(Math.cos(l), 2) + Math.pow(Math.sin(F), 2)
				* Math.pow(Math.sin(l), 2);
		double w = Math.atan(Math.sqrt(S / C));
		double D = 2 * w * a;
		double R = Math.sqrt(S * C) / w;
		double H1 = (3 * R - 1) / (2 * C);
		double H2 = (3 * R + 1) / (2 * S);

		double distance = D
				* (1 + f * H1 * Math.pow(Math.sin(F), 2) * Math.pow(Math.cos(G), 2) - f * H2 * Math.pow(Math.cos(F), 2)
						* Math.pow(Math.sin(G), 2));

		return distance * 1000.0;
	}
}
