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
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.model.StationDataMap;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
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
import javax.measure.Measure;
import javax.measure.unit.SI;
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
import org.junit.Test;

/**
 * JUnit Tests for WeatherService
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
@LocalClient
@ManagedBean
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
  private WeatherService service;

  private City city;

  @Resource
  private UserTransaction utx;

  @EJB
  private IdGenerator idGenerator;

  public DefaultWeatherServiceTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
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
        service.addNewNextDayWeather();
        fail();
      } catch (Exception expected) {
        //throws ejb related exception
        assertTrue(!(expected.getCause() instanceof IllegalArgumentException));
      }

      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(
          startTimestamp,
          endTimestamp,
          em,
          idGenerator);

      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);
      //the acutal test
      service.addNewNextDayWeather();

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
    strategyList.add(new WeatherStrategyHelper(new ReferenceCityModifier(
      startTimestamp,
      loader),
      startTimestamp));
    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      // Test false Date
      try {
        service.addNewWeather(0,
          0,
          true,
          null);
        fail();
      } catch (IllegalArgumentException excepted) {
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

      service.addNewWeather(startTimestamp,
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
    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      //test negative result
      boolean expResult = false;
      boolean result = service.checkDate(startTimestamp);
      assertEquals(expResult,
        result);

      //test positive result (need to set up for the exact date, the preceeding and the following date at the same hour of the day)
      AbstractStationData weather = new StationDataNormal(idGenerator.
        getNextId(),
        new Date(startTimestamp),
        new Time(startTimestamp),
        1,
        1,
        1.0f,
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1,
        1.0f,
        1.0f,
        1.0f);
      AbstractStationData weatherPreceeding = new StationDataNormal(idGenerator.
        getNextId(),
        new Date(
          startTimestamp - DateConverter.ONE_DAY_IN_MILLIS),
        new Time(startTimestamp - DateConverter.ONE_DAY_IN_MILLIS),
        1,
        1,
        1.0f,
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1,
        1.0f,
        1.0f,
        1.0f);
      AbstractStationData weatherFollowing = new StationDataNormal(idGenerator.
        getNextId(),
        new Date(
          startTimestamp + DateConverter.ONE_DAY_IN_MILLIS),
        new Time(startTimestamp + DateConverter.ONE_DAY_IN_MILLIS),
        1,
        1,
        1.0f,
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1,
        1.0f,
        1.0f,
        1.0f);
      em.persist(weather);
      em.persist(weatherFollowing);
      em.persist(weatherPreceeding);
      expResult = true;
      result = service.checkDate(startTimestamp);
      assertEquals(expResult,
        result);
      em.remove(weather);
      em.remove(weatherFollowing);
      em.remove(weatherPreceeding);

      // Test false Date
      try {
        service.checkDate(0);
        fail();
      } catch (IllegalArgumentException expected) {
      }

      // Test false Date (future)
      assertTrue(!(service.checkDate(System.currentTimeMillis())));

      // Test false Date (no data available)
      assertTrue(!(service.checkDate(testFalseDate)));
    } finally {
      utx.commit();
    }
  }

  @Test
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
      service.addNewWeather(startTimestamp,
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
      value = service.getValue(testParameter,
        timestamp);
      assertEquals(1008.0,
        value.doubleValue(),
        10.0); // Aggregate 1008

      // Test false timestamp
      try {
        service.getValue(testParameter,
          0);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      // Test false key
      try {
        service.getValue(null,
          0);
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

  @Test
  public void testGetValueWeatherParameterEnumLongVector2d() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    WeatherParameterEnum testParameter = WeatherParameterEnum.LIGHT_INTENSITY;

    Time time = DateConverter.convertTime("18:00:00",
      "hh:mm:ss");
    long timestamp = startTimestamp + time.getTime();
    long timestamp2 = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS + time.
      getTime();

    JaxRSCoordinate position = new JaxRSCoordinate(2,
      3);

    utx.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);

      /*
       * Test preparations
       */
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(
          startTimestamp,
          endTimestamp,
          em,
          idGenerator);
      // Get weather
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      /*
       * Test cases
       */
      Number value;

      // Test (normal)
      WeatherMap weatherMap = new StationDataMap();
      AbstractStationData weather = new StationDataNormal(idGenerator.
        getNextId(),
        new Date(startTimestamp),
        new Time(startTimestamp),
        1,
        1,
        1.0f,
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1,
        1.0f,
        1.0f,
        1.0f);
      weatherMap.put(timestamp,
        weather);
      value = service.getValue(testParameter,
        timestamp,
        position);
      assertEquals(11000.000,
        value.doubleValue(),
        5000);

      // Test false key
      try {
        service.getValue(null,
          0,
          position);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      // Test false timestamp
      try {
        service.getValue(testParameter,
          0,
          position);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      // Test false position
      try {
        service.getValue(testParameter,
          timestamp,
          null);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      // Test false position
      try {
        service.getValue(testParameter,
          timestamp,
          new JaxRSCoordinate(-1,
            -1));
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

      //keep entities in database until here
      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        em);

      // Test (normal) other date
      weatherMap = new StationDataMap();
      weather = new StationDataNormal(idGenerator.getNextId(),
        new Date(startTimestamp),
        new Time(startTimestamp),
        1,
        1,
        1.0f,
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1,
        1.0f,
        1.0f,
        1.0f);
      weatherMap.put(timestamp2,
        weather);
      value = service.getValue(testParameter,
        timestamp2,
        position);
      assertEquals(11000.000,
        value.doubleValue(),
        6000);

      // Test false timestamp
      try {
        service.getValue(testParameter,
          endTimestamp
          + DateConverter.ONE_DAY_IN_MILLIS + time.getTime(),
          position);
        fail();
      } catch (IllegalArgumentException e) {
        //expected
      }

    } finally {
      utx.commit();
    }
  }
}
