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
 
package de.pgalise.simulation.traffic.vehicle;

import java.awt.Color;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.XMLTruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.TruckData;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;

/**
 * Tests the {@link XMLTruckFactoryTest}
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 27, 2012)
 */
public class XMLTruckFactoryTest {

	/**
	 * Path to the XML file
	 */
	public static final String FILEPATH = "/defaultvehicles.xml";

	@Test
	public void test() {
		/*
		 * Test preparations
		 */
		RandomSeedService random = EasyMock.createMock(RandomSeedService.class);
		EasyMock.expect(random.getSeed("XMLAbstractFactory")).andReturn(1000L);

		/*
		 * Test case
		 */

		TrafficGraph graph = new DefaultTrafficGraph();
		TruckFactory factory = new XMLTruckFactory(random, XMLTruckFactoryTest.class.getResourceAsStream(FILEPATH),
				new DefaultTrafficGraphExtensions(random, graph));

		Vehicle<TruckData> vehicle1 = factory.createRandomTruck( null);
		Assert.assertNotNull(vehicle1);

		Vehicle<TruckData> vehicle2 = factory.createTruck(  Color.GRAY, 2, null);
		Assert.assertNotNull(vehicle2);
		Assert.assertEquals(Color.GRAY, vehicle2.getData().getColor());
		Assert.assertEquals(2, vehicle2.getData().getTrailerCount(), 0);
	}

}
