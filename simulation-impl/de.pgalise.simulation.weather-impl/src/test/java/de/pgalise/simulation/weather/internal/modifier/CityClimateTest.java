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
package de.pgalise.simulation.weather.internal.modifier;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.CityClimateModifier;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;
import de.pgalise.simulation.weather.persistence.WeatherPersistenceHelper;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.weather.WeatherTestUtils;
import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.measure.unit.SI;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for CityClimatemodifier
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@ManagedBean
@LocalClient
public class CityClimateTest {
  /**
   * Weather Loader
   */
  @EJB
  private WeatherLoader loader;

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManagerFactory;

  /**
   * End timestamp
   */
  public final long endTimestamp;

  /**
   * Start timestamp
   */
  public final long startTimestamp;

  /**
   * Service Class
   */
  @EJB
  private WeatherService service;

  @EJB
  private IdGenerator idGenerator;

  private City city;

  @Resource
  private UserTransaction userTransaction;
  @EJB
  private WeatherPersistenceHelper persistenceUtil;
  @EJB
  private RandomSeedService randomSeedService;

  public CityClimateTest() throws NamingException {
    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2011,
      5,
      10,
      0,
      0,
      0);
    startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2011,
      5,
      14,
      0,
      0,
      0);
    endTimestamp = cal.getTimeInMillis();
  }

  @Before
  public void setUp() throws Exception {
    TestUtils.getContext().bind("inject",
      this);
    TestUtils.getContainer().getContext().bind("inject",
      this);
    userTransaction.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);
    } finally {
      userTransaction.commit();
    }
  }

  @Test
  public void testDeployChanges() throws Exception {
    userTransaction.begin();
    try {
      //preparations
      Calendar cal = new GregorianCalendar();
      cal.setTimeInMillis(startTimestamp);
      cal.add(Calendar.DATE,
        -1);
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          persistenceUtil,
          entityManagerFactory,
          idGenerator);
      Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          persistenceUtil,
          entityManagerFactory,
          idGenerator);
      Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          persistenceUtil,
          entityManagerFactory,
          idGenerator);
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      // Get reference values
      WeatherMap referenceValues = service.getReferenceValues();

      // Get max of reference values
      AbstractStationData refmax = Collections.max(referenceValues.values(),
        new TemperatureComparator());
      float refvalue1 = refmax.getTemperature().floatValue(SI.CELSIUS);
      float refvalue2 = refmax.getPerceivedTemperature();
      float refvalue3 = refmax.getRadiation();
      float refvalue4 = refmax.getRelativHumidity();
      float refvalue5 = refmax.getWindVelocity();
      float refvalue6 = refmax.getPrecipitationAmount();
      long reftime = refmax.getMeasureTime().getTime();

      // Deploy strategy
      CityClimateModifier modifier = new CityClimateModifier(city,
        randomSeedService.getSeed(CityClimateTest.class.toString()),
        loader
      );
      service.deployStrategy(modifier,
        city);

      // Get modifier values
      WeatherMap modifierValues = service.getReferenceValues();

      // Get max of modifier values
      AbstractStationData decmax = modifierValues.get(reftime);

      /*
       * Testcase 1 : Temperature
       */
      // Test 1: Max are not equals
      Assert.assertTrue(refvalue1 != decmax.getTemperature().
        floatValue(SI.CELSIUS));

      /*
       * Testcase 2 : Perceived Temperature
       */
      // Test 1: Max are not equals
      Assert.assertTrue(refvalue2 != decmax.getPerceivedTemperature());

      /*
       * Testcase 3 : Radiation
       */
      // Test 1: Max are not equals
      Assert.assertTrue(refvalue3 != decmax.getRadiation());

      /*
       * Testcase 4 : RelativHumidity
       */
      // Test 1: Max are not equals
      Assert.assertTrue(refvalue4 != decmax.getRelativHumidity());

      /*
       * Testcase 5 : WindVelocity
       */
      // Test 1: Max are not equals
      Assert.assertTrue(refvalue5 != decmax.getWindVelocity());

      /*
       * Testcase 6 : PrecipitationAmount
       */
      // Test 1: Max are not equals
      if (modifier.isRainDay()) {
        Assert.assertTrue(refvalue6 != decmax.getPrecipitationAmount());
      } else {
        Assert.assertEquals(refvalue6,
          decmax.getPrecipitationAmount(),
          0.001);
      }

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        entityManagerFactory);
      WeatherTestUtils.tearDownWeatherData(entities0,
        ServiceDataCurrent.class,
        entityManagerFactory);
      WeatherTestUtils.tearDownWeatherData(entities1,
        ServiceDataForecast.class,
        entityManagerFactory);
    } finally {
      userTransaction.commit();
    }
  }

}
