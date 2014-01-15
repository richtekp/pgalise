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

import de.pgalise.simulation.shared.entity.Way;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.DefaultFileBasedCityInfrastructureDataService;
import de.pgalise.testutils.TestUtils;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import org.junit.Before;

/**
 * Several JUnit test cases for the OSMParser.
 *
 * @author Timo
 */
@ManagedBean
@LocalBean
public class OSMCityInfrastructureDataTest {

	/**
	 * Test class
	 */
	@EJB
	private DefaultFileBasedCityInfrastructureDataService osmParser;

	public OSMCityInfrastructureDataTest() {
	}

	@Before
	public void setUp() throws Exception {
		TestUtils.getContainer().getContext().bind("inject",
			this);
		osmParser.parse(DefaultFileBasedCityInfrastructureDataService.class.getResourceAsStream("/oldenburg_pg.osm"),
			DefaultFileBasedCityInfrastructureDataService.class.getResourceAsStream("/stops.txt"));
	}

	/**
	 * Tests the get nearest node method.
	 */
	@Test
	public void getNearestNodeTest() {
		/* Take one node and test, if it will be returned: */
		NavigationNode givenNode = osmParser.createCityInfrastructureData().getNodes().get(
			(int) (Math.random() * osmParser.createCityInfrastructureData().getNodes().size()));
		NavigationNode returnedNode = osmParser.getNearestNode(givenNode.
			getGeoLocation().getX(),
			givenNode.getGeoLocation().getY());
		assertTrue(givenNode.getGeoLocation().getX() == returnedNode.
			getGeoLocation().getX()
			&& givenNode.getGeoLocation().getY() == returnedNode.getGeoLocation().
			getY());
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
		outerLoop:
		for (Way<?, ?> way : osmParser.createCityInfrastructureData().getMotorWaysWithBusStops()) {
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
		NavigationNode tmpNode = osmParser.createCityInfrastructureData().getNodes().get(
			(int) (Math.random() * osmParser.createCityInfrastructureData().getNodes().size()));
		JaxRSCoordinate centerPoint = new JaxRSCoordinate(tmpNode.getGeoLocation().
			getX(),
			tmpNode.getGeoLocation().getY());
		for (Building building : osmParser.getBuildingsInRadius(centerPoint,
			radiusInMeter)) {
			assertTrue(this.getDistanceInMeter(centerPoint,
				building.getPosition().getCenterPoint()) <= radiusInMeter);
		}
	}

	/**
	 * Tests the getNearestStreetNode method.
	 */
	@Test
	public void getNearestStreetNode() {
		boolean isDifferent = false;
		for (NavigationNode node : osmParser.createCityInfrastructureData().getNodes()) {
			if (!node.equals(osmParser.getNearestStreetNode(node.getGeoLocation().
				getX(),
				node.getGeoLocation().getY()))) {
				isDifferent = true;
				break;
			}
		}
		assertTrue(isDifferent);
	}

	/**
	 * Gives the distance in meter between start and target.
	 *
	 * @param start BaseGeoInfo
	 * @param target BaseGeoInfo
	 * @return distance
	 */
	private double getDistanceInMeter(JaxRSCoordinate start,
		JaxRSCoordinate target) {

		if ((start.getX() == target.getX())
			&& (start.getY() == target.getY())) {
			return 0.0;
		}

		double f = 1 / 298.257223563;
		double a = 6378.137;
		double F = ((start.getX() + target.getX()) / 2) * (Math.PI / 180);
		double G = ((start.getX() - target.getX()) / 2) * (Math.PI / 180);
		double l = ((start.getY() - target.getY()) / 2) * (Math.PI / 180);
		double S = Math.pow(Math.sin(G),
			2) * Math.pow(Math.cos(l),
				2) + Math.pow(Math.cos(F),
				2)
			* Math.pow(Math.sin(l),
				2);
		double C = Math.pow(Math.cos(G),
			2) * Math.pow(Math.cos(l),
				2) + Math.pow(Math.sin(F),
				2)
			* Math.pow(Math.sin(l),
				2);
		double w = Math.atan(Math.sqrt(S / C));
		double D = 2 * w * a;
		double R = Math.sqrt(S * C) / w;
		double H1 = (3 * R - 1) / (2 * C);
		double H2 = (3 * R + 1) / (2 * S);

		double distance = D
			* (1 + f * H1 * Math.pow(Math.sin(F),
				2) * Math.pow(Math.cos(G),
				2) - f * H2 * Math.pow(Math.cos(F),
				2)
			* Math.pow(Math.sin(G),
				2));

		return distance * 1000.0;
	}
}
