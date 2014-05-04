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

import de.pgalise.simulation.energy.internal.CSVEnergyConsumptionManager;
import de.pgalise.simulation.energy.internal.profile.CSVProfileLoader;
import de.pgalise.simulation.energy.profile.EnergyProfileLoader;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.traffic.service.CityDataService;
import de.pgalise.testutils.TestUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the public methods of the CSVEnergyConsumptionManager.
 *
 * @author Andreas Rehfeldt
 * @author Timo
 * @version 1.0 (Oct 28, 2012)
 */
@LocalClient
@ManagedBean
public class CSVEnergyConsumptionManagerTest {

  /**
   * End timestamp
   */
  private static long endTimestamp;

  /**
   * Start timestamp
   */
  private static long startTimestamp;

  /**
   * Test location as GeoLocation
   */
  private BaseCoordinate testLocationAsGL;

  /**
   * The used energy profile loader.
   */
  private static final EnergyProfileLoader energyProfileLoader = new CSVProfileLoader();

  @EJB
  private IdGenerator idGenerator;

  public CSVEnergyConsumptionManagerTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
    testLocationAsGL = new BaseCoordinate(
    53.136765,
    8.216524);
    
    Map<EnergyProfileEnum, List<Building>> map = new HashMap<>();
    List<Building> buildingList = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      buildingList.add(new Building(idGenerator.getNextId(),
        new BaseBoundary(idGenerator.getNextId(), testLocationAsGL, null)));
    }
    map.put(EnergyProfileEnum.HOUSEHOLD,
      buildingList);
    CityDataService citydata = EasyMock.createNiceMock(
      CityDataService.class);
    EasyMock.expect(citydata.getBuildingEnergyProfileMap(
      testLocationAsGL,
      5)).andStubReturn(map);
    EasyMock.replay(citydata);

    CSVEnergyConsumptionManagerTest.testclass = new CSVEnergyConsumptionManager();
    testclass.setLoader(energyProfileLoader);

    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2012,
      1,
      1,
      20,
      0,
      0);
    CSVEnergyConsumptionManagerTest.startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2012,
      1,
      3,
      20,
      14,
      0);
    CSVEnergyConsumptionManagerTest.endTimestamp = cal.getTimeInMillis();
  }

  /**
   * Test class
   */
  public static CSVEnergyConsumptionManager testclass;

  @Test
  public void testGetEnergyConsumptionInKWhTimestamp() {
    /*
     * Test preparations
     */
    if (CSVEnergyConsumptionManagerTest.testclass.
      getProfiles().
      get(EnergyProfileEnum.HOUSEHOLD) == null) {
      CSVEnergyConsumptionManagerTest.testclass.init(
        CSVEnergyConsumptionManagerTest.startTimestamp,
        CSVEnergyConsumptionManagerTest.endTimestamp,
        null);
    }

    Calendar cal = new GregorianCalendar();
    cal.set(2012,
      1,
      1,
      20,
      14,
      0);
    long testTime = cal.getTimeInMillis();

    /*
     * TEST RUN
     */
    double actValue = CSVEnergyConsumptionManagerTest.testclass.
      getEnergyConsumptionInKWh(testTime,
        EnergyProfileEnum.HOUSEHOLD,
        testLocationAsGL);

    /*
     * Test 1 : Test the testTime
     */
    Assert.assertEquals(0.05381885854844445,
      actValue,
      0.001);
  }

  @Test
  public void testLoadEnergyProfiles() {
    /*
     * TEST RUN
     */
    CSVEnergyConsumptionManagerTest.testclass.init(
      CSVEnergyConsumptionManagerTest.startTimestamp,
      CSVEnergyConsumptionManagerTest.endTimestamp,
      null);

    /*
     * Test 1 : load all profiles correctly
     */
    for (EnergyProfileEnum profileEnum : EnergyProfileEnum.values()) {
      Assert.assertNotNull(CSVEnergyConsumptionManagerTest.testclass.
        getProfiles().get(profileEnum));
    }
  }

}
