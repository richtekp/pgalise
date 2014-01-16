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

import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherInitParameter;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.testutils.TestUtils;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Testcases for WeatherController
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
@LocalClient
@ManagedBean
public class DefaultWeatherControllerTest {

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManagerFactory;
  /**
   * Logger
   */
  private final static Logger log = LoggerFactory.getLogger(
    DefaultWeatherControllerTest.class);

  /**
   * Test City
   */
  private City city;

  /**
   * Timestamp (end)
   */
  private long endTimestamp;

  /**
   * Timestamp (start)
   */
  private long startTimestamp;

  /**
   * Test timestamp of the events
   */
  private long eventTimestamp;

  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private WeatherController ctrl;

  public DefaultWeatherControllerTest() {
  }

  @Before
  public void setUp() throws Exception {
    TestUtils.getContainer().getContext().bind("inject",
      this);

    System.setProperty("simulation.configuration.filepath",
      "src/test/resources/simulation.conf");

    userTransaction.begin();
    try {
      // Create city
      city = TestUtils.createDefaultTestCityInstance(idGenerator);
    } finally {
      userTransaction.commit();
    }

    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2010,
      1,
      1,
      0,
      0,
      0);
    startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2010,
      1,
      2,
      0,
      0,
      0);
    endTimestamp = cal.getTimeInMillis();

    // Event time
    cal.set(2010,
      1,
      1,
      18,
      0,
      0);
    eventTimestamp = cal.getTimeInMillis();
  }

  @Test
  public void controllerTest() throws InterruptedException, IllegalArgumentException,
    ExecutionException, IllegalStateException, InitializationException, NamingException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, RollbackException {
    userTransaction.begin();
    try {
      Number testNumber;

      // Local test variables
      long valueTime = startTimestamp + 360000;
      WeatherParameterEnum testParameter = WeatherParameterEnum.WIND_VELOCITY;
      JaxRSCoordinate testPosition = new JaxRSCoordinate(2,
        3);
      List<WeatherEvent> testEventList = new ArrayList<>(1);
      testEventList.add(new ChangeWeatherEvent(idGenerator.getNextId(),
        WeatherEventTypeEnum.HOTDAY,
        30.0f,
        eventTimestamp,
        6L));
      EventList<WeatherEvent> testEvent = new EventList<>(idGenerator.
        getNextId(),
        testEventList,
        valueTime);

      WeatherInitParameter initParameter = new WeatherInitParameter(city,
        startTimestamp,
        endTimestamp,
        valueTime,
        eventTimestamp,
        null,
        null,
        null);
      initParameter.setStartTimestamp(new Date(startTimestamp));
      initParameter.setEndTimestamp(new Date(endTimestamp));

      StartParameter parameter = new StartParameter(
        true,
        null,
        city);

      log.debug("Testmethod: init()");
      ctrl.init(initParameter);
      Assert.assertEquals(ControllerStatusEnum.INITIALIZED,
        ctrl.getStatus());

      // Test (normal) -> call onSuccess
      log.debug("Testmethod: start()");
      Map<Date, StationDataNormal> entities = WeatherTestUtils.
        setUpWeatherStationData(startTimestamp,
          endTimestamp,
          entityManagerFactory,
          idGenerator);
      ctrl.start(parameter);
      Assert.assertEquals(ControllerStatusEnum.STARTED,
        ctrl.getStatus());

      // Test second start: can not started twice -> call onFailure
      try {
        ctrl.start(parameter);
        Assert.fail();
      } catch (IllegalStateException expected) {
      }
      Assert.assertEquals(ControllerStatusEnum.STARTED,
        ctrl.getStatus());

      // Test (normal) -> call onSuccess
      log.debug("Testmethod: getValue()");
      try {
        testNumber = ctrl.getValue(testParameter,
          valueTime,
          testPosition);
        Assert.assertEquals(3.457,
          testNumber.doubleValue(),
          0.5);
      } catch (Exception expected) {
      }

      // Test false parameters: null as key -> call onFailure
      try {
        testNumber = ctrl.getValue(null,
          valueTime,
          testPosition);
        Assert.assertNull(testNumber);
      } catch (Exception expected) {
      }

      // Test false parameters: wrong timestamp -> call onFailure
      try {
        testNumber = ctrl.getValue(testParameter,
          0,
          testPosition);
        Assert.assertNull(testNumber);
      } catch (Exception expected) {
      }

      // Test false parameters: wrong position -> call onFailure
      try {
        testNumber = ctrl.getValue(testParameter,
          0,
          new JaxRSCoordinate(-1,
            -2));
        Assert.assertNull(testNumber);
      } catch (Exception expected) {
      }

      // Test (normal) -> call onSuccess
      log.debug("Testmethod: update()");
      try {
        ctrl.update(testEvent);
      } catch (IllegalStateException expected) {
      }
      Assert.assertEquals(ControllerStatusEnum.STARTED,
        ctrl.getStatus());

      // Test false parameter: no event -> call onFailure
      try {
        ctrl.update(null);
        Assert.fail();
      } catch (IllegalArgumentException expected) {
      }
      Assert.assertEquals(ControllerStatusEnum.STARTED,
        ctrl.getStatus());

      /*
       * Log
       */
      log.debug("Testmethod: stop()");

      // Stops the controller. Validates the state enum to check, if the
      // controller has the stopped state.
      ctrl.stop();
      Assert.assertEquals(ControllerStatusEnum.STOPPED,
        ctrl.getStatus());

      /*
       * Log
       */
      log.debug("Testmethod: reset()");

      // Init the controller. Validates the state enum to check, if the
      // controller has the init state.
      ctrl.reset();
      Assert.assertEquals(ControllerStatusEnum.INIT,
        ctrl.getStatus());

      WeatherTestUtils.tearDownWeatherData(entities,
        StationDataNormal.class,
        entityManagerFactory);
    } finally {
      userTransaction.commit();
    }
  }

}
