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
package de.pgalise.simulation.weather.internal.service;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.testutils.TestUtils;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit Tests for WeatherService
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
@LocalClient
@ManagedBean
@LocalBean
public class DefaultWeatherServiceTest {

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager em;
  /**
   * End timestamp
   */
  private long endTimestamp;

  /**
   * Start timestamp
   */
  private long startTimestamp;

  /**
   * Weather loader
   */
  @EJB
  private WeatherLoader loader;

  @EJB
  private WeatherService instance;

  private City city;

  @Resource
  private UserTransaction utx;

  @EJB
  private IdGenerator idGenerator;

  public DefaultWeatherServiceTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().bind("inject",
      this);

    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2012,
      11,
      1,
      20,
      0,
      0);
    startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2012,
      11,
      2,
      20,
      14,
      0);
    endTimestamp = cal.getTimeInMillis();
  }

  @Test
  public void testAddNewNextDayWeather() throws NamingException, NotSupportedException,
    SystemException,
    HeuristicMixedException, HeuristicRollbackException, IllegalStateException,
    RollbackException {
    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      // Test no prior call to Service.addNewWeather
      try {
        instance.addNewNextDayWeather();
        fail();
      } catch (IllegalStateException expected) {
      }

      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(
          startTimestamp,
          endTimestamp,
          em,
          idGenerator);

      instance.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);
      //the acutal test
      instance.addNewNextDayWeather();

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        em);
    } finally {
      utx.commit();
    }
  }

  @Test
  public void testAddNewWeather() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    List<WeatherStrategyHelper> strategyList = new ArrayList<>(1);
    strategyList.add(new WeatherStrategyHelper(new ReferenceCityModifier(city,
      startTimestamp,
      loader),
      startTimestamp));
    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      // Test false Date
      try {
        instance.addNewWeather(0,
          0,
          true,
          null);
        fail();
      } catch (IllegalArgumentException expected) {
      }

      // Test (normal)
      Map<Date, ServiceDataCurrent> prequisites = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(
          startTimestamp,
          endTimestamp,
          city,
          em,
          idGenerator);
      Map<Date, StationDataNormal> prequisites0 = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          em,
          idGenerator);
      Map<Date, ServiceDataForecast> prequisites1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          em,
          idGenerator);

      instance.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        strategyList);

      WeatherTestUtils.tearDownWeatherData(prequisites,
        ServiceDataCurrent.class,
        em);
      WeatherTestUtils.tearDownWeatherData(prequisites0,
        StationDataNormal.class,
        em);
      WeatherTestUtils.tearDownWeatherData(prequisites1,
        ServiceDataForecast.class,
        em);

    } finally {
      utx.commit();
    }
  }

  @Test
  public void testCheckDate() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    long testFalseDate = DateConverter.convertDate("12.03.2002",
      "dd.mm.yyyy").getTime(); //before simulation
    Map<Date, StationDataNormal> entities;
    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      //test negative result
      boolean expResult = false;
      boolean result = instance.checkDate(startTimestamp);
      assertEquals(expResult,
        result);

      //test positive result (need to set up for the exact date, the preceeding and the following date at the same hour of the day)
      entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          em,
          idGenerator);
      expResult = true;
      result = instance.checkDate(startTimestamp);
      assertEquals(expResult,
        result);
      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        em);

      // Test false Date
      try {
        instance.checkDate(0);
        fail();
      } catch (IllegalArgumentException expected) {
      }

      // Test false Date (future)
      assertTrue(!(instance.checkDate(System.currentTimeMillis())));

      // Test false Date (no data available)
      assertTrue(!(instance.checkDate(testFalseDate)));
    } finally {
      utx.commit();
    }
  }

  @Test
  @Ignore //why is the result 1008? -> docs!!
  public void testGetValueWeatherParameterEnumLong() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      /*
       * Test preparations
       */
      Map<Date, StationDataNormal> prequisites = WeatherTestUtils.
        setUpWeatherStationData(
          startTimestamp,
          endTimestamp,
          em,
          idGenerator);

      // Get weather
      instance.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      /*
       * Test variables
       */
      // Test Parameter
      WeatherParameterEnum testParameter = WeatherParameterEnum.AIR_PRESSURE;
      Time time = DateConverter.convertTime("18:00:00",
        "hh:mm:ss");
      long timestamp = startTimestamp + time.getTime();

      /*
       * Test cases
       */
      Number value;

      // Test (normal)
      value = instance.getValue(testParameter,
        timestamp,
        city);
      assertEquals(1008.0,
        value.doubleValue(),
        10.0); // Aggregate 1008

      // Test false timestamp
      try {
        instance.getValue(testParameter,
          0,
          city);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      // Test false key
      try {
        instance.getValue(null,
          0,
          city);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      WeatherTestUtils.tearDownWeatherData(prequisites,
        StationDataNormal.class,
        em);
    } finally {
      utx.commit();
    }
  }

//  @Test
//  public void testGetValueWeatherParameterEnumLongVector2d() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
//    WeatherParameterEnum testParameter = WeatherParameterEnum.LIGHT_INTENSITY;
//
//    Time time = DateConverter.convertTime("18:00:00",
//      "hh:mm:ss");
//    long timestamp = startTimestamp + time.getTime();
//    long timestamp2 = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS + time.
//      getTime();
//
//    JaxRSCoordinate position = new JaxRSCoordinate(2,
//      3);
//
//    utx.begin();
//    try {
//      city = TestUtils.createDefaultTestCityInstance(idGenerator);
//
//      /*
//       * Test preparations
//       */
//      Map<Date, StationDataNormal> entities = WeatherTestUtils.
//        setUpWeatherStationData(
//          startTimestamp,
//          endTimestamp,
//          em,
//          idGenerator);
//      // Get weather
//      instance.addNewWeather(startTimestamp,
//        endTimestamp,
//        true,
//        null);
//
//      /*
//       * Test cases
//       */
//      Number value;
//
//      // Test (normal)
//      WeatherMap weatherMap = new StationDataMap();
//      AbstractStationData weather = new StationDataNormal(idGenerator.
//        getNextId(),
//        new Date(startTimestamp),
//        new Time(startTimestamp),
//        1,
//        1,
//        1.0f,
//        Measure.valueOf(1.0f,
//          SI.CELSIUS),
//        1.0f,
//        1,
//        1.0f,
//        1.0f,
//        1.0f);
//      weatherMap.put(timestamp,
//        weather);
//      value = instance.getValue(testParameter,
//        timestamp,
//        position);
//      assertEquals(11000.000,
//        value.doubleValue(),
//        5000);
//
//      // Test false key
//      try {
//        instance.getValue(null,
//          0,
//          position);
//        fail();
//      } catch (IllegalArgumentException e) {
//        //expected
//      }
//
//      // Test false timestamp
//      try {
//        instance.getValue(testParameter,
//          0,
//          position);
//        fail();
//      } catch (IllegalArgumentException e) {
//        //expected
//      }
//
//      // Test false position
//      try {
//        instance.getValue(testParameter,
//          timestamp,
//          null);
//        fail();
//      } catch (IllegalArgumentException e) {
//        //expected
//      }
//
//      // Test false position
//      try {
//        instance.getValue(testParameter,
//          timestamp,
//          new JaxRSCoordinate(-1,
//            -1));
//        fail();
//
//      } catch (IllegalArgumentException e) {
//        //expected
//      }
//
//      //keep entities in database until here
//      WeatherTestUtils.tearDownWeatherData(entities,
//        StationDataNormal.class,
//        em);
//
//      // Test (normal) other date
//      weatherMap = new StationDataMap();
//      weather = new StationDataNormal(idGenerator.getNextId(),
//        new Date(startTimestamp),
//        new Time(startTimestamp),
//        1,
//        1,
//        1.0f,
//        Measure.valueOf(1.0f,
//          SI.CELSIUS),
//        1.0f,
//        1,
//        1.0f,
//        1.0f,
//        1.0f);
//
//      weatherMap.put(timestamp2,
//        weather);
//      value = instance.getValue(testParameter,
//        timestamp2,
//        position);
//
//      assertEquals(
//        11000.000,
//        value.doubleValue(),
//        6000);
//
//      // Test false timestamp
//      try {
//        instance.getValue(testParameter,
//          endTimestamp
//          + DateConverter.ONE_DAY_IN_MILLIS + time.getTime(),
//          position);
//        fail();
//      } catch (IllegalArgumentException e) {
//        //expected
//      }
//
//    } finally {
//      utx.commit();
//    }
//  }
}
