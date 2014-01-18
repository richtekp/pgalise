/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.model.vehicle;

import de.pgalise.simulation.traffic.internal.model.factory.XMLVehicleFactory;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.Motorcycle;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.testutils.TestUtils;
import java.awt.Color;
import java.io.InputStream;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class XMLVehicleFactoryTest {

  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private TrafficGraphExtensions trafficGraphExtensions;

  public XMLVehicleFactoryTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  /**
   * Test of createBicycle method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateBicycle() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Bicycle result = instance.createBicycle();
    assertNotNull(result);
  }

  /**
   * Test of createBus method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateBus() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Bus result = instance.createBus();
    assertNotNull(result);
  }

  /**
   * Test of createCar method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateCar() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Car result = instance.createCar();
    assertNotNull(result);
  }

  /**
   * Test of createMotorcycle method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateMotorcycle() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Motorcycle result = instance.createMotorcycle();
    assertNotNull(result);
  }

  /**
   * Test of createRandomBicycle method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateRandomBicycle() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Bicycle result = instance.createRandomBicycle();
    assertNotNull(result);
  }

  /**
   * Test of createRandomBus method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateRandomBus() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Bus result = instance.createRandomBus();
    assertNotNull(result);
  }

  /**
   * Test of createRandomCar method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateRandomCar() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Car result = instance.createRandomCar();
    assertNotNull(result);
  }

  /**
   * Test of createRandomMotorcycle method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateRandomMotorcycle() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Motorcycle result = instance.createRandomMotorcycle();
    assertNotNull(result);
  }

  /**
   * Test of createRandomTruck method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateRandomTruck() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Truck result = instance.createRandomTruck();
    assertNotNull(result);
  }

  /**
   * Test of createTruck method, of class XMLVehicleFactory.
   */
  @Test
  public void testCreateTruck() {
    Output output = EasyMock.createNiceMock(Output.class);
    InputStream inputStream = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("defaultvehicles.xml");
    XMLVehicleFactory instance = new XMLVehicleFactory(randomSeedService,
      idGenerator,
      trafficGraphExtensions,
      inputStream);
    Color color = Color.BLACK;
    int trailerCount = 2;
    Truck result = instance.createTruck(color,
      trailerCount);
    assertEquals(color,
      result.getData().getColor());
    assertEquals(trailerCount,
      result.getData().getTruckTrailerDatas().size());
  }
}
