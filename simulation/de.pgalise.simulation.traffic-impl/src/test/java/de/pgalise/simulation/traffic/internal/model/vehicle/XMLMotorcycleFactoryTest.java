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
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.internal.model.factory.XMLMotorcycleFactory;
import de.pgalise.simulation.service.IdGenerator;
import java.awt.Color;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.entity.MotorcycleData;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import org.apache.openejb.api.LocalClient;

/**
 * Tests the {@link XMLMotorcycleFactoryTest}
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 27, 2012)
 */
@LocalClient
@ManagedBean
public class XMLMotorcycleFactoryTest {

  /**
   * Path to the XML file
   */
  public static final String FILEPATH = "/motorcycles.xml";
  @EJB
  private IdGenerator idGenerator;

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
    TrafficGraphExtensions trafficGraphExtensions = EasyMock.createNiceMock(
      TrafficGraphExtensions.class);
    MotorcycleFactory factory = new XMLMotorcycleFactory(idGenerator,
      trafficGraphExtensions,
      random,
      XMLBicycleFactoryTest.class.getResourceAsStream(FILEPATH));

    Vehicle<MotorcycleData> vehicle1 = factory.createRandomMotorcycle();
    Assert.assertNotNull(vehicle1);

    Vehicle<MotorcycleData> vehicle2 = factory.createMotorcycle();
    Assert.assertNotNull(vehicle2);
    Assert.assertEquals(Color.GRAY,
      vehicle2.getData().getColor());
  }

}
