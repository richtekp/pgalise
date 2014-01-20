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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.energy.EnergyConsumptionManager;
import de.pgalise.simulation.energy.internal.CSVEnergyConsumptionManager;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.service.CityInfrastructureDataService;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.shared.entity.Building;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.junit.Before;
import org.junit.Ignore;

/**
 * Tests the synchronization of the {@link EnergyConsumptionManager}
 *
 * @author Andreas Rehfeldt
 * @author Timo
 * @version 1.0 (Oct 29, 2012)
 */
@LocalClient
@ManagedBean
@Ignore //@loads EnergyProfileLocal as EJB which doesn't have an implementation which is annotated as EJB (how did this work before?)
public class EnergyConsumptionManagerSyncTest {

  /**
   * End timestamp
   */
  private static long endTime = 0;

  /**
   * Start timestamp
   */
  private static long startTime = 0;

  /**
   * Test class
   */
  private static EnergyConsumptionManager testclass;

  /**
   * Test location as Geolocation
   */
  private static final JaxRSCoordinate testLocation = new JaxRSCoordinate(
    53.136765,
    8.216524);

  /**
   * Test location as Vector2d
   */
  private static final JaxRSCoordinate testLocationAsV2d = EnergyConsumptionManagerSyncTest.testLocation;
  /**
   * Number of test threads
   */
  private static final int testNumberOfThreads = 100;
  @EJB
  private IdGenerator idGenerator;

  public EnergyConsumptionManagerSyncTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);

    Map<EnergyProfileEnum, List<Building>> map = new HashMap<>();
    List<Building> buildingList = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      buildingList.add(new Building(idGenerator.getNextId(),
        new JaxRSCoordinate(53.136765,
          8.216524),
        null));
    }

    CityInfrastructureDataService citydata = EasyMock.createNiceMock(
      CityInfrastructureDataService.class);
    EasyMock.expect(citydata.getBuildings(
      EnergyConsumptionManagerSyncTest.testLocation,
      5)).andStubReturn(map);
    EasyMock.replay(citydata);

    EnergyConsumptionManagerSyncTest.testclass = new CSVEnergyConsumptionManager();

    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2012,
      1,
      1,
      0,
      0,
      0);
    EnergyConsumptionManagerSyncTest.startTime = cal.getTimeInMillis();

    // End
    cal.set(2012,
      1,
      4,
      0,
      0,
      0);
    EnergyConsumptionManagerSyncTest.endTime = cal.getTimeInMillis();

    // Load new data
    EnergyConsumptionManagerSyncTest.testclass.init(
      EnergyConsumptionManagerSyncTest.startTime,
      EnergyConsumptionManagerSyncTest.endTime,
      null);
  }

  /**
   * Exception
   */
  private volatile Throwable throwable;

  @After
  public void tearDown() throws Throwable {
    if (this.throwable != null) {
      throw this.throwable;
    }
  }

  @Test
  public void testGetValue() throws InterruptedException {
    // List with all threads
    final List<Thread> threads = new ArrayList<>();

    // Creates 50 Threads
    for (int i = 0; i < EnergyConsumptionManagerSyncTest.testNumberOfThreads; i++) {
      final int y = i;
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {

          // Sleep with random value
          try {
            Thread.sleep((long) (Math.random() * 1000L));
          } catch (InterruptedException e) {
            EnergyConsumptionManagerSyncTest.this.throwable = e;
            e.printStackTrace();
          }

          if ((y % 50) == 0) {
            // Every 20 thread add new weather
            EnergyConsumptionManagerSyncTest.testclass.init(
              EnergyConsumptionManagerSyncTest.startTime,
              EnergyConsumptionManagerSyncTest.endTime,
              null);
          }

          Assert.assertTrue(EnergyConsumptionManagerSyncTest.testclass.
            getEnergyConsumptionInKWh(
              EnergyConsumptionManagerSyncTest.startTime,
              EnergyProfileEnum.HOUSEHOLD,
              EnergyConsumptionManagerSyncTest.testLocationAsV2d) > 0);
        }
      });

      // Save thread
      threads.add(thread);

      // Start thread
      thread.start();
    }

    // Wait for threads
    for (Thread thread : threads) {
      thread.join();
    }
  }
}
