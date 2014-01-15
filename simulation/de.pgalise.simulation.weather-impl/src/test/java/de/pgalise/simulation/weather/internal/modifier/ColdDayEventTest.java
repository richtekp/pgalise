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
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.modifier.events.ColdDayEvent;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.modifier.AbstractWeatherMapModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.testutils.TestUtils;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for ColdDayEvent
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@LocalBean
@ManagedBean
@LocalClient
public class ColdDayEventTest {

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManagerFactory;

  /**
   * End timestamp
   */
  private long endTimestamp;

  /**
   * Start timestamp
   */
  private long startTimestamp;

  /**
   * Test timestamp
   */
  private long testTimestamp;

  /**
   * Test value
   */
  private float testValue = -10.0f;

  /**
   * Test duration
   */
  private long testDuration = 4;

  /**
   * Service Class
   */
  @EJB
  private WeatherService service;

  /**
   * Weather Loader
   */
  @EJB
  private WeatherLoader loader;

  private City city;

  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;

  public ColdDayEventTest() throws NamingException {
    Calendar cal = new GregorianCalendar();
    cal.set(2010,
      6,
      12,
      0,
      0,
      0);
    startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2010,
      6,
      13,
      0,
      0,
      0);
    endTimestamp = cal.getTimeInMillis();

    // Test time
    cal.set(2010,
      6,
      12,
      18,
      0,
      0);
    testTimestamp = cal.getTimeInMillis();
  }

  @Before
  public void setUp() throws Exception {
    TestUtils.getContainer().getContext().bind("inject",
      this);
    userTransaction.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);
      service.setCity(city);
    } finally {
      userTransaction.commit();
    }
  }

  @Test
  public void testDeployChanges() throws Exception {
    userTransaction.begin();
    try {
      //preparation
      service = new DefaultWeatherService(city,
        loader);
      Calendar cal = new GregorianCalendar();
      cal.setTimeInMillis(startTimestamp);
      cal.add(Calendar.DATE,
        -1);
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManagerFactory,
          idGenerator);
      Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          entityManagerFactory,
          idGenerator);
      Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          entityManagerFactory,
          idGenerator);
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      // Get extrema of reference values
      float refvalue = service.getValue(WeatherParameterEnum.TEMPERATURE,
        testTimestamp).floatValue();

      // Deploy strategy
      ColdDayEvent event = new ColdDayEvent(
        new DefaultRandomSeedService().
        getSeed(ColdDayEventTest.class.toString()),
        testTimestamp,
        null,
        testValue,
        testDuration,
        loader);
      service.deployStrategy(event);

      // Get extrema of decorator values
      float decvalue = service.getValue(WeatherParameterEnum.TEMPERATURE,
        testTimestamp).floatValue();

      /*
       * Testcase 1
       */
      // Test 1: Min are not equals - Temperature
      Assert.assertTrue(refvalue > decvalue);

      // Test 2: Min are as high event - Temperature
      Assert.assertEquals(AbstractWeatherMapModifier.round(event.getMinValue(),
        3),
        AbstractWeatherMapModifier.round(decvalue,
          3),
        1);

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
