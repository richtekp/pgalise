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

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.model.factory.XMLCarFactory;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.testutils.TestUtils;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

import org.junit.Test;

/**
 * Tests the {@link XMLCarFactoryTest}
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 27, 2012)
 */
@LocalClient
@ManagedBean
public class XMLCarFactoryTest {

  /**
   * Path to the XML file
   */
  public static final String FILEPATH = "/cars.xml";

  /**
   * Test file
   */
  public static final String TEST_FILE = "t.tmp";
  @EJB
  private IdGenerator idGenerator;

  public XMLCarFactoryTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().bind("inject",
      this);
  }

  @Test
  public void testCreateRandomVehicle() throws FileNotFoundException, IOException {
    /*
     * Test preparations
     */
    RandomSeedService random = EasyMock.createMock(RandomSeedService.class);
    EasyMock.expect(random.getSeed("XMLAbstractFactory")).andReturn(1000L);

    /*
     * Test case
     */
    TrafficGraph graph = new DefaultTrafficGraph();
    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(30,
        30));
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(60,
        60));
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addEdge(a,
      b);
    TrafficGraphExtensions trafficGraphExtensions = EasyMock.createNiceMock(
      TrafficGraphExtensions.class);
    CarFactory factory = new XMLCarFactory(idGenerator,
      trafficGraphExtensions,
      random,
      XMLBicycleFactoryTest.class.getResourceAsStream(FILEPATH));

    Vehicle<CarData> vehicle1 = factory.createRandomCar(graph.edgeSet());
    Assert.assertNotNull(vehicle1);

    Vehicle<CarData> vehicle2 = factory.createCar(graph.edgeSet());
    Assert.assertNotNull(vehicle2);
    Assert.assertEquals(Color.GRAY,
      vehicle2.getData().getColor());

    /*
     * Test to write down the vehicle
     */
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream(TEST_FILE);
      oos = new ObjectOutputStream(fos);

      oos.writeObject(vehicle2);

      oos.close();
      fos.close();
    } finally {
      if (fos != null) {
        fos.close();
      }
      if (oos != null) {
        oos.close();
      }
    }

    /*
     * Delete file
     */
    File file = new File(TEST_FILE);
    if (file.exists()) {
      file.delete();
      assertTrue(true);
    } else {
      assertTrue(false);
    }
  }

}
