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

import de.pgalise.simulation.energy.internal.FuzzyEnergyConsumptionManager;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import net.sourceforge.jFuzzyLogic.FIS;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test for {@link FuzzyEnergyConsumptionManager}. Uses different
 * timestamps and weather settings to test the fuzzy rules.
 *
 * @author Timo
 */
public class FuzzyEnergyConsumptionManagerTest {

  private BaseCoordinate testLocation;
  private static final double coldDayTemperature = -5.0;
  private static final double hotDayTemperature = 35.0;
  private static final double mediumTemperature = 15.0;
  private static WeatherControllerLocal weatherController;
  private static long startTimestamp;
  private static long endTimestamp;
  private static long coldEarlyMorningWeekDayTimestamp;
  private static long coldAfternoonWeekDayTimestamp;
  private static long hotAfternoonWeekDayTimestamp;
  private static long hotEarlyMorningWeekDayTimestamp;
  private static long mediumAfternoonWeekDayTimestamp;
  private static long mediumEarlyMorningWeekDayTimestamp;
  private static long coldAfternoonWeekEndTimestamp;
  private static long coldEarlyMorningWeekEndTimestamp;
  private static long hotAfterNoonWeekEndTimestamp;
  private static long hotEarlyMorningWeekEndTimestamp;
  private static long mediumAfterNoonWeekEndTimestamp;
  private static long mediumEarlyMorningWeekEndTimestamp;
  private static long coldNightWeekDayTimestamp;
  private static long hotNightWeekDayTimestamp;
  private static long mediumNightWeekDayTimestamp;
  private static long coldNightWeekEndTimestamp;
  private static long hotNightWeekEndTimestamp;
  private static long mediumNightWeekEndTimestamp;
  private static long mediumWinterDayTimestamp;
  private static long mediumSummerTimestamp;
  private static long mediumSpringTimestamp;
  private static long mediumAutumnTimestamp;
  private static long coldSummerTimestamp;
  private static long hotSummerTimestamp;
  private static long coldSpringTimestamp;
  private static long hotSpringTimestamp;
  private static long coldAutumnTimestamp;
  private static long hotAutumnTimestamp;
  private static long coldWinterTimestamp;
  private static long hotWinterTimestamp;
  private static long mediumEarlySummerTimestamp;
  private static long mediumMiddaySummerTimestamp;
  private static long mediumLateSummerTimestamp;
  private static long mediumNightTimestamp;
  @EJB
  private IdGenerator idGenerator;

  /**
   * Test class
   */
  public static FuzzyEnergyConsumptionManager testclass;
  
  @Before
  public void setUp() {
    testLocation = new BaseCoordinate(idGenerator.getNextId(),0.0, 0.0);

    /* Mock the weathercontroller: */
    FuzzyEnergyConsumptionManagerTest.weatherController = EasyMock.createNiceMock(WeatherControllerLocal.class);

    /* Add timestamps and set weathercontroller: */
    // Start
    Calendar cal = new GregorianCalendar();
    cal.set(2011, 0, 0, 0, 0, 0);
    FuzzyEnergyConsumptionManagerTest.startTimestamp = cal.getTimeInMillis();

    // End
    cal.set(2011, 11, 31, 23, 59, 59);
    FuzzyEnergyConsumptionManagerTest.endTimestamp = cal.getTimeInMillis();

    // Cold afternoon weekday:
    cal.set(2011, 0, 3, 15, 0);
    FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp);

    // Cold morning weekday:
    cal.set(2011, 0, 3, 5, 0);
    FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp);

    // Hot afternoon weekday:
    cal.set(2011, 6, 12, 15, 0);
    FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp);

    // Hot morning weekday:
    cal.set(2011, 6, 12, 5, 0);
    FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp);

    // Medium afternoon weekday:
    cal.set(2011, 7, 16, 15, 0);
    FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp);

    // Medium morning weekday:
    cal.set(2011, 7, 16, 5, 0);
    FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp);

    // Cold Afternoon weekend:
    cal.set(2011, 0, 1, 15, 0);
    FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp);

    // Cold morning weekday:
    cal.set(2011, 0, 2, 5, 0);
    FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp);

    // Hot Afternoon weekend:
    cal.set(2011, 6, 16, 15, 0);
    FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp);

    // Hot morning weekday:
    cal.set(2011, 6, 16, 5, 0);
    FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp);

    // Medium Afternoon weekend:
    cal.set(2011, 7, 20, 15, 0);
    FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp);

    // Medium morning weekday:
    cal.set(2011, 7, 20, 5, 0);
    FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp);

    // Cold Night weekday:
    cal.set(2011, 0, 3, 1, 0);
    FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp);

    // Hot Night weekday:
    cal.set(2011, 6, 12, 1, 0);
    FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp);

    // Medium Night weekday:
    cal.set(2011, 7, 16, 1, 0);
    FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp);

    // Cold Night weekend:
    cal.set(2011, 0, 1, 1, 0);
    FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp);

    // Hot Night weekend:
    cal.set(2011, 6, 16, 1, 0);
    FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp);

    // Medium Night weekend:
    cal.set(2011, 7, 20, 1, 0);
    FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp);

    // Medium winter day:
    cal.set(2011, 0, 0, 11, 0);
    FuzzyEnergyConsumptionManagerTest.mediumWinterDayTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumWinterDayTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumWinterDayTimestamp);

    // Hot winter day:
    cal.set(2011, 0, 0, 12, 0);
    FuzzyEnergyConsumptionManagerTest.hotWinterTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotWinterTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotWinterTimestamp);

    // Cold winter day:
    cal.set(2011, 0, 0, 13, 0);
    FuzzyEnergyConsumptionManagerTest.coldWinterTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldWinterTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotWinterTimestamp);

    // Medium spring day:
    cal.set(2011, 3, 22, 11, 0);
    FuzzyEnergyConsumptionManagerTest.mediumSpringTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumSpringTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumSpringTimestamp);

    // Cold spring day:
    cal.set(2011, 3, 22, 12, 0);
    FuzzyEnergyConsumptionManagerTest.coldSpringTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldSpringTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldSpringTimestamp);

    // Hot spring day:
    cal.set(2011, 3, 22, 13, 0);
    FuzzyEnergyConsumptionManagerTest.hotSpringTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotSpringTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotSpringTimestamp);

    // Medium autumn day:
    cal.set(2011, 9, 26, 11, 0);
    FuzzyEnergyConsumptionManagerTest.mediumAutumnTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumAutumnTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumAutumnTimestamp);

    // Cold autumn day:
    cal.set(2011, 9, 26, 12, 0);
    FuzzyEnergyConsumptionManagerTest.coldAutumnTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldAutumnTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldAutumnTimestamp);

    // Hot autumn day:
    cal.set(2011, 9, 26, 13, 0);
    FuzzyEnergyConsumptionManagerTest.hotAutumnTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotAutumnTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotAutumnTimestamp);

    // Medium summer day:
    cal.set(2011, 8, 27, 11, 0);
    FuzzyEnergyConsumptionManagerTest.mediumSummerTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumSummerTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumSummerTimestamp);

    // Hot summer day:
    cal.set(2011, 8, 21, 12, 0);
    FuzzyEnergyConsumptionManagerTest.hotSummerTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.hotSummerTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.hotDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.hotSummerTimestamp);

    // Cold summer day:
    cal.set(2011, 8, 21, 13, 0);
    FuzzyEnergyConsumptionManagerTest.coldSummerTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.coldSummerTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.coldDayTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.coldSummerTimestamp);

    // Medium early summer day:
    cal.set(2011, 7, 1, 6, 0);
    FuzzyEnergyConsumptionManagerTest.mediumEarlySummerTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumEarlySummerTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumEarlySummerTimestamp);

    // Medium midday summer day:
    cal.set(2011, 7, 1, 12, 0);
    FuzzyEnergyConsumptionManagerTest.mediumMiddaySummerTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumMiddaySummerTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumMiddaySummerTimestamp);

    // Medium late summer day:
    cal.set(2011, 7, 1, 12, 0);
    FuzzyEnergyConsumptionManagerTest.mediumLateSummerTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumLateSummerTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumLateSummerTimestamp);

    // Medium summer night:
    cal.set(2011, 7, 1, 1, 0);
    FuzzyEnergyConsumptionManagerTest.mediumNightTimestamp = cal.getTimeInMillis();
    EasyMock.expect(
      FuzzyEnergyConsumptionManagerTest.weatherController.getValue(WeatherParameterEnum.TEMPERATURE,
        FuzzyEnergyConsumptionManagerTest.mediumNightTimestamp,
        testLocation)).andStubReturn(
        FuzzyEnergyConsumptionManagerTest.mediumTemperature);
    setWeatherControllerExpectsTo0(FuzzyEnergyConsumptionManagerTest.mediumNightTimestamp);

    EasyMock.replay(FuzzyEnergyConsumptionManagerTest.weatherController);

    /* Init the testclass: */
    FuzzyEnergyConsumptionManagerTest.testclass = new FuzzyEnergyConsumptionManager();
    FuzzyEnergyConsumptionManagerTest.testclass.init(FuzzyEnergyConsumptionManagerTest.startTimestamp,
      FuzzyEnergyConsumptionManagerTest.endTimestamp, FuzzyEnergyConsumptionManagerTest.weatherController);
    FuzzyEnergyConsumptionManagerTest.testclass
      .setWeatherController(FuzzyEnergyConsumptionManagerTest.weatherController);
  }

  /**
   * Sets the easy mock expect returns to 0.0 for all values in @see
   * {@link WeatherParameterEnum} Excludes WeatherParameterEnum.TEMPERATURE
   *
   * @param timestamp
   */
  private void setWeatherControllerExpectsTo0(long timestamp) {
    for (WeatherParameterEnum parameter : WeatherParameterEnum.values()) {
      EasyMock.expect(
        FuzzyEnergyConsumptionManagerTest.weatherController.getValue(parameter, timestamp,
          testLocation)).andStubReturn(0.0);
    }
  }

  /**
   * Tests the fuzzy rules for bakery.
   */
  @Test
  public void testGetEnergyConsumptionInKWhBakery() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.BAKERY,
      testLocation);

    /* highest energy consumption is always in the morning */
    Assert.assertTrue(coldMorningWeekDay > coldAfterNoonWeekDay);
    Assert.assertTrue(coldMorningWeekDay > coldNightWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > coldAfterNoonWeekEnd);
    Assert.assertTrue(coldMorningWeekEnd > coldNightWeekEnd);
    Assert.assertTrue(hotMorningWeekDay > hotAfterNoonWeekDay);
    Assert.assertTrue(hotMorningWeekDay > hotNightWeekDay);
    Assert.assertTrue(hotMorningWeekEnd > hotAfterNoonWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > hotNightWeekEnd);
    Assert.assertTrue(mediumMorningWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(mediumMorningWeekDay > mediumNightWeekDay);
    Assert.assertTrue(mediumMorningWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(mediumMorningWeekEnd > mediumNightWeekEnd);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);
  }

  /**
   * Tests the fuzzy rules for business on weekend.
   */
  @Test
  public void testGetEnergyConsumptionInKWhBusinessOnWeekEnd() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp,
      EnergyProfileEnum.BUSINESS_ON_WEEKEND, testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.BUSINESS_ON_WEEKEND,
      testLocation);

    /* highest energy consumption is always on weekend */
    Assert.assertTrue(coldMorningWeekDay < coldMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay < coldAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay < coldNightWeekEnd);
    Assert.assertTrue(mediumMorningWeekDay < mediumMorningWeekEnd);
    Assert.assertTrue(mediumAfterNoonWeekDay < mediumAfterNoonWeekEnd);
    Assert.assertTrue(mediumNightWeekDay < mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotMorningWeekDay < hotMorningWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekDay < hotAfterNoonWeekEnd);
    Assert.assertTrue(hotNightWeekDay < hotNightWeekEnd);
    Assert.assertTrue(mediumMorningWeekDay < mediumMorningWeekEnd);
    Assert.assertTrue(mediumAfterNoonWeekDay < mediumAfterNoonWeekEnd);
    Assert.assertTrue(mediumNightWeekDay < mediumNightWeekEnd);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);
  }

  /**
   * Tests the fuzzy rules for farm building.
   */
  @Test
  public void testGetEnergyConsumptionInKWhFarmBuilding() {
    double mediumSpringDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumSpringTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumSummerDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumSummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumAutumnDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAutumnTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumWinterDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumWinterDayTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumEarlySummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlySummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumMiddaySummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumMiddaySummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumLateSummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumLateSummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumNightSummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);

    /* More in summer, spring and autumn than in winter: */
    Assert.assertTrue(mediumWinterDay < mediumSummerDay);
    Assert.assertTrue(mediumWinterDay < mediumSpringDay);
    Assert.assertTrue(mediumWinterDay < mediumAutumnDay);

    /* More in the morning, midday and lateday than in the night: */
    Assert.assertTrue(mediumEarlySummer > mediumNightSummer);
    Assert.assertTrue(mediumMiddaySummer > mediumNightSummer);
    Assert.assertTrue(mediumLateSummer > mediumNightSummer);
  }

  /**
   * Tests the fuzzy rules for farm building other.
   */
  @Test
  public void testGetEnergyConsumptionInKWhFarmBuildingOther() {
    double mediumSpringDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumSpringTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumSummerDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumSummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumAutumnDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAutumnTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumWinterDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumWinterDayTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumEarlySummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlySummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumMiddaySummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumMiddaySummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumLateSummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumLateSummerTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);
    double mediumNightSummer = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightTimestamp, EnergyProfileEnum.FARM_BUILDING,
      testLocation);

    /* More in summer, spring and autumn than in winter: */
    Assert.assertTrue(mediumWinterDay < mediumSummerDay);
    Assert.assertTrue(mediumWinterDay < mediumSpringDay);
    Assert.assertTrue(mediumWinterDay < mediumAutumnDay);

    /* More in the morning, midday and lateday than in the night: */
    Assert.assertTrue(mediumEarlySummer > mediumNightSummer);
    Assert.assertTrue(mediumMiddaySummer > mediumNightSummer);
    Assert.assertTrue(mediumLateSummer > mediumNightSummer);
  }

  /**
   * Tests the fuzzy rules for farm building with animal breeding.
   */
  @Test
  public void testGetEnergyConsumptionInKWhFarmBuildingWithAnimalBreeding() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumEarlySummer = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumEarlySummerTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumMiddaySummer = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumMiddaySummerTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumLateSummer = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumLateSummerTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);
    double mediumNightSummer = FuzzyEnergyConsumptionManagerTest.testclass
      .getEnergyConsumptionInKWh(FuzzyEnergyConsumptionManagerTest.mediumNightTimestamp,
        EnergyProfileEnum.FARM_BUILDING_WITH_ANIMAL_BREEDING,
        testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* less in night: */
    Assert.assertTrue(mediumEarlySummer > mediumNightSummer);
    Assert.assertTrue(mediumMiddaySummer > mediumNightSummer);
    Assert.assertTrue(mediumLateSummer > mediumNightSummer);
  }

  /**
   * Tests the fuzzy rules for farm building with animal breeding.
   */
  @Test
  public void testGetEnergyConsumptionInKWhHouseHold() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.HOUSEHOLD,
      testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* late more than in night: */
    Assert.assertTrue(coldAfterNoonWeekDay > coldNightWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > coldAfterNoonWeekDay);
  }

  /**
   * Tests the fuzzy rules for industry general
   */
  @Test
  public void testGetEnergyConsumptionInKWhIndustryGeneral() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_GENERAL, testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_GENERAL, testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_GENERAL,
      testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* more in week than on weekend: */
    Assert.assertTrue(coldMorningWeekDay > coldMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekDay > hotMorningWeekEnd);
    Assert.assertTrue(mediumMorningWeekDay > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > coldAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekDay > hotAfterNoonWeekEnd);
    Assert.assertTrue(mediumAfterNoonWeekDay > mediumAfterNoonWeekEnd);
  }

  /**
   * Tests the fuzzy rules for industry on workdays
   */
  @Test
  public void testGetEnergyConsumptionInKWhIndustryOnWorkdays() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_ON_WORKDAYS, testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_ON_WORKDAYS,
      testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* more in week than on weekend: */
    Assert.assertTrue(coldMorningWeekDay > coldMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekDay > hotMorningWeekEnd);
    Assert.assertTrue(mediumMorningWeekDay > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > coldAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekDay > hotAfterNoonWeekEnd);
    Assert.assertTrue(mediumAfterNoonWeekDay > mediumAfterNoonWeekEnd);
  }

  /**
   * Tests the fuzzy rules for industry with consumption night
   */
  @Test
  public void testGetEnergyConsumptionInKWhIndustryWithConsumptionNight() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WITH_CONSUMPTION_NIGHT, testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* more in night than on day: */
    Assert.assertTrue(coldNightWeekDay > coldAfterNoonWeekDay);
    Assert.assertTrue(coldNightWeekEnd > coldAfterNoonWeekEnd);
    Assert.assertTrue(hotNightWeekDay > hotAfterNoonWeekDay);
    Assert.assertTrue(hotNightWeekEnd > hotAfterNoonWeekEnd);
    Assert.assertTrue(mediumNightWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(mediumNightWeekEnd > mediumAfterNoonWeekEnd);
  }

  /**
   * Tests the fuzzy rules for industry working
   */
  @Test
  public void testGetEnergyConsumptionInKWhIndustryWorking() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp,
      EnergyProfileEnum.INDUSTRY_WORKING, testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp,
      EnergyProfileEnum.INDUSTRY_WORKING, testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.INDUSTRY_WORKING,
      testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* more in week than on weekend: */
    Assert.assertTrue(coldMorningWeekDay > coldMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekDay > hotMorningWeekEnd);
    Assert.assertTrue(mediumMorningWeekDay > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > coldAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekDay > hotAfterNoonWeekEnd);
    Assert.assertTrue(mediumAfterNoonWeekDay > mediumAfterNoonWeekEnd);
  }

  /**
   * Tests the fuzzy rules for shops.
   */
  @Test
  public void testGetEnergyConsumptionInKWhShop() {
    double coldMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double coldMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldEarlyMorningWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double coldAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double coldAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldAfternoonWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double coldNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double coldNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.coldNightWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double hotMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double hotMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotEarlyMorningWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double hotAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfternoonWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double hotAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotAfterNoonWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double hotNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double hotNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.hotNightWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double mediumMorningWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double mediumMorningWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumEarlyMorningWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double mediumAfterNoonWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfternoonWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double mediumAfterNoonWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumAfterNoonWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double mediumNightWeekDay = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekDayTimestamp, EnergyProfileEnum.SHOP,
      testLocation);
    double mediumNightWeekEnd = FuzzyEnergyConsumptionManagerTest.testclass.getEnergyConsumptionInKWh(
      FuzzyEnergyConsumptionManagerTest.mediumNightWeekEndTimestamp, EnergyProfileEnum.SHOP,
      testLocation);

    /* more on cold and hot days than on medium days: */
    Assert.assertTrue(coldMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(hotMorningWeekDay > mediumMorningWeekDay);
    Assert.assertTrue(coldMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(hotMorningWeekEnd > mediumMorningWeekEnd);
    Assert.assertTrue(coldAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(hotAfterNoonWeekDay > mediumAfterNoonWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekEnd > mediumAfterNoonWeekEnd);
    Assert.assertTrue(coldNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(hotNightWeekDay > mediumNightWeekDay);
    Assert.assertTrue(coldNightWeekEnd > mediumNightWeekEnd);
    Assert.assertTrue(hotNightWeekEnd > mediumNightWeekEnd);

    /* more on afternoon than in night */
    Assert.assertTrue(coldAfterNoonWeekDay > coldNightWeekDay);
    Assert.assertTrue(coldAfterNoonWeekEnd > coldNightWeekEnd);
    Assert.assertTrue(hotAfterNoonWeekDay > hotNightWeekDay);
    Assert.assertTrue(hotAfterNoonWeekEnd > hotNightWeekEnd);
    Assert.assertTrue(mediumAfterNoonWeekDay > mediumNightWeekDay);
    Assert.assertTrue(mediumAfterNoonWeekEnd > mediumNightWeekEnd);
  }
}
