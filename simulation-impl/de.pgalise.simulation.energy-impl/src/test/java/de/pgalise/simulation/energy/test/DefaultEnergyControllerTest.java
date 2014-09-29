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
package de.pgalise.simulation.energy.test;

import de.pgalise.simulation.energy.EnergyConsumptionManagerLocal;
import de.pgalise.simulation.energy.EnergyEventStrategyLocal;
import de.pgalise.simulation.energy.internal.DefaultEnergyController;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BasePolygon;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.service.FileBasedCityDataService;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit tests for {@link DefaultEnergyController}
 *
 * @author Timo
 * @author Andreas
 */
@Ignore //@TODO: usage of Mapping between coordinate and building is not clear (coordinate can be arbitraryly precise and be on the buildings surface or not
@LocalClient
@ManagedBean
public class DefaultEnergyControllerTest {

  /**
   * Test City
   */
  private static City city;

  /**
   * End of the simulation
   */
  private static long endTime;

  /**
   * Start of the simulation
   */
  private static long startTime;

  /**
   * Test class
   */
  private static DefaultEnergyController testClass;

  /**
   * Weather Controller
   */
  private static WeatherController weather;

  /**
   * Geolocation (should be inside building)
   */
  private BaseCoordinate testLocation;

  /**
   * Init parameters
   */
  private static TrafficInitParameter initParameter;

  /**
   * Start parameters
   */
  private static TrafficStartParameter startParameter;

  /**
   * Traffic information
   */
  /*
   Mock in order to get building list more easy
   */
  private static FileBasedCityDataService information;

  /**
   * The used energy consumption manager
   */
  @EJB
  private EnergyConsumptionManagerLocal energyConsumptionManager;

  /**
   * The used energy event strategy
   */
  @EJB
  private static EnergyEventStrategyLocal energyEventStrategy;
  @EJB
  private IdGenerator idGenerator;

  /**
   * Test time
   */
  private static long testTime;

  @Before
  public void setUp() throws Exception {
    TestUtils.getContext().bind("inject",
      this);

    testLocation = new BaseCoordinate( 1.5,
    1.5);
    Calendar cal = new GregorianCalendar();

    // city
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);

    // City information
    Map<EnergyProfileEnum, List<Building>> map = new HashMap<>();
    List<Building> buildingList = new ArrayList<>();
    map.put(EnergyProfileEnum.HOUSEHOLD,
      buildingList);
    for (int i = 0; i < 100; i++) {
      buildingList.add(
        new Building(idGenerator.getNextId(),
          new BaseBoundary(idGenerator.getNextId(), new BaseCoordinate( 53.136765,
            8.216524),
          new BasePolygon(idGenerator.getNextId(),
            GeoToolsBootstrapping.getGeometryFactory().createPolygon(
              new LinkedList<>(Arrays.asList(
                new BaseCoordinate( 1,
                  1),
                new BaseCoordinate( 1,
                  2),
                new BaseCoordinate( 2,
                  2),
                new BaseCoordinate( 2,
                  1),
                new BaseCoordinate( 1,
                  1)
              )).toArray(new BaseCoordinate[5])
            )
          )
        )      
      ));
    }

    DefaultEnergyControllerTest.information = EasyMock.createNiceMock(
      FileBasedCityDataService.class);
    EasyMock.expect(
      DefaultEnergyControllerTest.information.getBuildingEnergyProfileMap(
        testLocation,
        3))
      .andStubReturn(map);
    EasyMock.replay(DefaultEnergyControllerTest.information);

    // Start
    cal.set(2012,
      1,
      1,
      0,
      0,
      0);
    DefaultEnergyControllerTest.startTime = cal.getTimeInMillis();

    // End
    cal.set(2012,
      1,
      5,
      0,
      0,
      0);
    DefaultEnergyControllerTest.endTime = cal.getTimeInMillis();

    // Interval
    long interval = 1000;

    // Test time
    cal.set(2012,
      1,
      2,
      11,
      35,
      0);
    testTime = cal.getTimeInMillis();

    // Weather Controller:
    DefaultEnergyControllerTest.weather = EasyMock.createNiceMock(
      WeatherController.class);
    EasyMock.replay(DefaultEnergyControllerTest.weather);
    Output output = EasyMock.createNiceMock(Output.class);

    DefaultEnergyControllerTest.initParameter = new TrafficInitParameter(
      null,
      DefaultEnergyControllerTest.startTime,
      DefaultEnergyControllerTest.endTime,
      interval,
      interval,
      new URL("http://localhost:8080/operationCenter"),
      new URL(""),
      null,
      output);

    city = TrafficTestUtils.createDefaultTestCityInstance(idGenerator);
    DefaultEnergyControllerTest.startParameter = new TrafficStartParameter(
      city,
      true,
      new ArrayList<WeatherEvent>());

    // EnergyConsumptionManager
    energyConsumptionManager = EasyMock.createNiceMock(
      EnergyConsumptionManagerLocal.class);
    EasyMock.expect(energyConsumptionManager.getEnergyConsumptionInKWh(testTime,
      EnergyProfileEnum.HOUSEHOLD,
      testLocation)).andStubReturn(1.0);
    EasyMock.replay(energyConsumptionManager);

    // Create class
    DefaultEnergyControllerTest.testClass = new DefaultEnergyController(
      information);
    DefaultEnergyControllerTest.testClass.setEnergyConsumptionManager(
      energyConsumptionManager);
    DefaultEnergyControllerTest.testClass.setEnergyEventStrategy(
      energyEventStrategy);
  }

  @Test
  public void testController() throws IllegalStateException, InitializationException {

    /*
     * Init the controller
     */
    DefaultEnergyControllerTest.testClass.init(
      DefaultEnergyControllerTest.initParameter);
    Assert.assertEquals(ControllerStatusEnum.INITIALIZED,
      testClass.getStatus());

    /*
     * Start the controller
     */
    DefaultEnergyControllerTest.testClass.start(
      DefaultEnergyControllerTest.startParameter);
    Assert.assertEquals(ControllerStatusEnum.STARTED,
      testClass.getStatus());


    /*
     * RUN TEST
     */
    // Get value
    double value = DefaultEnergyControllerTest.testClass.
      getEnergyConsumptionInKWh(testTime,
        testLocation,
        3);

    Assert.assertEquals(100.0,
      value,
      0.5);

    /*
     * Stop the controller
     */
    DefaultEnergyControllerTest.testClass.stop();
    Assert.assertEquals(ControllerStatusEnum.STOPPED,
      testClass.getStatus());

    /*
     * Reset the controller
     */
    DefaultEnergyControllerTest.testClass.reset();
    Assert.assertEquals(ControllerStatusEnum.INIT,
      testClass.getStatus());
  }
}
