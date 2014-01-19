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
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.testutils.TestUtils;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the synchronization of the weather service
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 29, 2012)
 */
@LocalClient
@LocalBean
@ManagedBean
public class DefaultWeatherServiceSyncTest {

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManager;

  /**
   * End timestamp
   */
  private long endTimestamp;

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultWeatherServiceSyncTest.class);

  /**
   * Start timestamp
   */
  private long startTimestamp;

  /**
   * Test class
   */
  @EJB
  private WeatherService testclass;

  /**
   * Number of test threads
   */
  private final static int NUMBER_OF_THREADS = 20;
  private final static long THREAD_WAIT_MILLIS = 0;

  /**
   * Weather loader
   */
  @EJB
  private WeatherLoader loader;

  private City city;

  @Resource
  private UserTransaction userTransaction;

  @EJB
  private IdGenerator idGenerator;

  @Before
  public void setUp() throws Exception {
    TestUtils.getContainer().bind("inject",
      this);

    userTransaction.begin();
    try {
      city = TestUtils.
        createDefaultTestCityInstance(idGenerator);
    } finally {
      userTransaction.commit();
    }

    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2011,
      1,
      1,
      0,
      0,
      0);
    startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2011,
      1,
      2,
      0,
      0,
      0);
    endTimestamp = cal.getTimeInMillis();
  }

  @Test
  public void testGetValue() throws Exception {
    // Test time
    final long testTime = startTimestamp + (1000 * 60 * 60 * 5);
    // All threads
    final List<Thread> threads = new ArrayList<>();
    userTransaction.begin();
    try {
      Map<Date, StationDataNormal> entities;
      Map<Date, ServiceDataCurrent> entities0;
      Map<Date, ServiceDataForecast> entities1;
      entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManager,
          idGenerator);
      entities0 = WeatherTestUtils.
        setUpWeatherServiceDataCurrent(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      entities1 = WeatherTestUtils.
        setUpWeatherServiceDataForecast(startTimestamp,
          endTimestamp,
          city,
          entityManager,
          idGenerator);
      testclass.addNewWeather(startTimestamp,
        endTimestamp,
        true,
        null);

      // Creates 50 Threads
      for (int i = 0; i < NUMBER_OF_THREADS; i++) {
        final int y = i;
        Thread thread = new Thread(new Runnable() {
          @Override
          public void run() {
            Number value;

            // Sleep with random value
            try {
              Thread.sleep((long) (Math.random() * THREAD_WAIT_MILLIS));
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }

            if ((y % 20) == 0) {
              // Every 20 thread add new weather
              testclass.addNewWeather(startTimestamp,
                endTimestamp,
                true,
                null);
              log.debug("New weather added!");

            } else {
              // Get test value
              value = testclass.getValue(WeatherParameterEnum.TEMPERATURE,
                testTime,
                city);
              Assert.assertTrue(value != null);
              log.debug("Thread (" + y + ") value: " + value.floatValue());
            }
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
}
