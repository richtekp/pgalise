/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.model.StationDataMap;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.entity.WeatherCondition;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.testutils.TestUtils;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.lang.time.DateUtils;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class DatabaseWeatherLoaderTest {

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManager;

  private City city;
  @EJB
  private WeatherLoader instance;

  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;

  public DatabaseWeatherLoaderTest() {
  }

  @Before
  public void setUp() throws NamingException, NotSupportedException, SystemException {
    TestUtils.getContext().bind("inject",
      this);
    TestUtils.getContainer().getContext().bind("inject",
      this);
    city = TestUtils.createDefaultTestCityInstance(idGenerator);
  }

  /**
   * Test of checkStationDataForDay method, of class DatabaseWeatherLoader.
   *
   * @throws Exception
   */
  @Test
  public void testCheckStationDataForDay() throws Exception {
    long timestamp = System.currentTimeMillis();
    long startTimestamp = DateConverter.convertTimestampToMidnight(timestamp);
    long endTimestamp = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS;
    boolean expResult = false;
    userTransaction.begin();
    try {
      city = TestUtils.createDefaultTestCityInstance(idGenerator);
      boolean result = instance.checkStationDataForDay(timestamp);
      assertEquals(expResult,
        result);

      WeatherService service = new DefaultWeatherService(city,
        instance);
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      expResult = true;
      result = instance.checkStationDataForDay(timestamp);
      assertEquals(expResult,
        result);

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities0,
        ServiceDataCurrent.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities1,
        ServiceDataForecast.class,
        entityManager);
    } finally {
      userTransaction.commit();
    }
  }

  /**
   * Test of loadCurrentServiceWeatherData method, of class
   * DatabaseWeatherLoader.
   *
   * @throws Exception
   */
  @Test
  public void testLoadCurrentServiceWeatherData() throws Exception {
    long timestamp = System.currentTimeMillis();
    long startTimestamp = DateConverter.convertTimestampToMidnight(timestamp);
    long endTimestamp = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS;
    userTransaction.begin();
    try {
      try {
        instance.loadCurrentServiceWeatherData(timestamp,
          city);
        fail();
      } catch (NoWeatherDataFoundException expected) {
      }

      WeatherService service = new DefaultWeatherService(city,
        instance);
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      ServiceDataCurrent expResult = null;
      ServiceDataCurrent result = instance.
        loadCurrentServiceWeatherData(timestamp,
          city);
      assertNotNull(result);
      assertEquals(DateUtils.truncate(new Date(timestamp),
        Calendar.DATE),
        result.getMeasureDate()
      );

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities0,
        ServiceDataCurrent.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities1,
        ServiceDataForecast.class,
        entityManager);
    } finally {
      userTransaction.commit();
    }
  }

  /**
   * Test of loadForecastServiceWeatherData method, of class
   * DatabaseWeatherLoader.
   *
   * @throws Exception
   */
  @Test
  @Ignore //@TODO: who to test values -> mock retrieval service ??
  public void testLoadForecastServiceWeatherData() throws Exception {
    long timestamp = System.currentTimeMillis();
    long startTimestamp = DateConverter.convertTimestampToMidnight(timestamp);
    long endTimestamp = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS;
    userTransaction.begin();
    try {
      try {
        instance.loadForecastServiceWeatherData(timestamp,
          city);
      } catch (NoWeatherDataFoundException expected) {
      }

      WeatherService service = new DefaultWeatherService(city,
        instance);
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);
      ServiceDataForecast expResult = new ServiceDataForecast(idGenerator.
        getNextId(),
        DateUtils.truncate(new Date(timestamp),
          Calendar.DATE),
        new Time(timestamp),
        city,
        Measure.valueOf(20f,
          SI.CELSIUS),
        Measure.valueOf(20f,
          SI.CELSIUS),
        1.0f,
        2.0f,
        2.0f,
        20.0f,
        WeatherCondition.retrieveCondition(idGenerator,
          2));
      ServiceDataForecast result = instance.loadForecastServiceWeatherData(
        timestamp,
        city);
      assertEquals(expResult.getMeasureDate(),
        result.getMeasureDate());
      assertEquals(expResult.getCity(),
        result.getCity());
      assertEquals(expResult.getCondition(),
        result.getCondition());
      assertEquals(expResult.getRelativHumidity(),
        result.getRelativHumidity());
      assertEquals(expResult.getTemperatureHigh(),
        result.getTemperatureHigh());
      assertEquals(expResult.getTemperatureLow(),
        result.getTemperatureLow());
      assertEquals(expResult.getWindDirection(),
        result.getWindDirection());
      assertEquals(expResult.getWindVelocity(),
        result.getWindVelocity());

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities0,
        ServiceDataCurrent.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities1,
        ServiceDataForecast.class,
        entityManager);
    } finally {
      userTransaction.commit();
    }
  }

  /**
   * Test of loadStationData method, of class DatabaseWeatherLoader.
   *
   * @throws Exception
   */
  @Test
  public void testLoadStationData() throws Exception {
    long timestamp = System.currentTimeMillis();
    long startTimestamp = DateConverter.convertTimestampToMidnight(timestamp);
    long endTimestamp = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS;
    userTransaction.begin();
    try {
      try {
        instance.loadForecastServiceWeatherData(timestamp,
          city);
      } catch (NoWeatherDataFoundException expected) {
      }

      WeatherService service = new DefaultWeatherService(city,
        instance);
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      service.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);
      WeatherMap expResult = new StationDataMap();
      expResult.put(timestamp,
        null);
      WeatherMap result = instance.loadStationData(timestamp);
      assertFalse(result.isEmpty());

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities0,
        ServiceDataCurrent.class,
        entityManager);
      WeatherTestUtils.tearDownWeatherData(entities1,
        ServiceDataForecast.class,
        entityManager);
    } finally {
      userTransaction.commit();
    }
  }

  private boolean stationDataEqualsIgnoreTimestamp(AbstractStationData o1,
    AbstractStationData o2) {
    return true;
  }
}
