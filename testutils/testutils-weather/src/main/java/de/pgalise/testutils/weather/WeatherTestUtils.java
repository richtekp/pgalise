/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils.weather;

import de.pgalise.simulation.persistence.PersistenceUtil;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.entity.StationDataNormal;
import de.pgalise.simulation.weather.entity.WeatherCondition;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author richter
 */
public class WeatherTestUtils {

  /**
   * sets up entities of type <tt>ServiceDataForecast</tt> for startTimestamp at
   * midnight, the day before startTimestamp at midnight and endTimestamp at
   * midnight
   *
   * @param startTimestamp
   * @param endTimestamp
   * @param city
   * @param persistenceUtil
   * @param entityManager
   * @param idGenerator
   * @return
   * @throws NotSupportedException
   * @throws SystemException
   * @throws HeuristicMixedException
   * @throws HeuristicRollbackException
   * @throws IllegalStateException
   * @throws RollbackException
   */
  public static Map<Date, ServiceDataForecast> setUpWeatherServiceDataForecast(
    long startTimestamp,
    long endTimestamp,
    City city,
          PersistenceUtil persistenceUtil,
    EntityManager entityManager,
    IdGenerator idGenerator) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    long preceedingDayTimestampMidnight = DateConverter.
      convertTimestampToMidnight(
        startTimestamp) - DateConverter.ONE_DAY_IN_MILLIS;
    long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
      endTimestamp);
    Map<Date, ServiceDataForecast> retValue;
    persistenceUtil.saveOrUpdateCity(entityManager, city);
    long start = preceedingDayTimestampMidnight;
    retValue = new HashMap<>();
    while (start < endTimestampMidnight + DateConverter.ONE_DAY_IN_MILLIS) {
      ServiceDataForecast serviceDataForecast = new ServiceDataForecast(
        idGenerator.getNextId(),
        DateUtils.truncate(new Date(start),
          Calendar.DATE),
        new Time(start),
        city,
        Measure.valueOf(10.0f,
          SI.CELSIUS),
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1.0f,
        1.0f,
        20.0f,
        WeatherCondition.retrieveCondition(idGenerator,
          WeatherCondition.UNKNOWN_CONDITION_CODE)
      );
      persistenceUtil.saveOrUpdate(entityManager,
        serviceDataForecast,
        ServiceDataForecast.class,
        serviceDataForecast.getId());
      retValue.put(new Date(start),
        serviceDataForecast);
      start += DateConverter.ONE_DAY_IN_MILLIS;
    }
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), endMidnight=%d (%s)", ServiceDataForecast.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
    return retValue;
  }

  /**
   * sets up entities of type <tt>ServiceDataCurrent</tt> for startTimestamp at
   * midnight, the day before startTimestamp at midnight and endTimestamp at
   * midnight
   *
   * @param startTimestamp
   * @param endTimestamp
   * @param city
   * @param entityManager
   * @param idGenerator
   * @return
   * @throws NotSupportedException
   * @throws SystemException
   * @throws HeuristicMixedException
   * @throws HeuristicRollbackException
   * @throws IllegalStateException
   * @throws RollbackException
   */
  public static Map<Date, ServiceDataCurrent> setUpWeatherServiceDataCurrent(
    long startTimestamp,
    long endTimestamp,
    City city,
    PersistenceUtil persistenceUtil,
    EntityManager entityManager,
    IdGenerator idGenerator) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    long preceedingDayTimestampMidnight = DateConverter.
      convertTimestampToMidnight(
        startTimestamp) - DateConverter.ONE_DAY_IN_MILLIS;
    long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
      endTimestamp);
    Map<Date, ServiceDataCurrent> retValue;
    persistenceUtil.saveOrUpdateCity(entityManager, city);
    long start = preceedingDayTimestampMidnight;
    retValue = new HashMap<>();
    while (start < endTimestampMidnight + DateConverter.ONE_DAY_IN_MILLIS) {
      ServiceDataCurrent serviceDataCurrent = new ServiceDataCurrent(
        idGenerator.getNextId(),
        DateUtils.truncate(new Date(start),
          Calendar.DATE),
        new Time(start),
        city,
        1.0f,
        Measure.valueOf(1.0f,
          SI.CELSIUS),
        1.0f,
        1.0f,
        20.0f,
        WeatherCondition.retrieveCondition(idGenerator,
          WeatherCondition.UNKNOWN_CONDITION_CODE)
      );
      persistenceUtil.saveOrUpdate(entityManager,
        serviceDataCurrent,
        ServiceDataCurrent.class,
        serviceDataCurrent.getId());
      retValue.put(new Date(start),
        serviceDataCurrent);
      start += DateConverter.ONE_DAY_IN_MILLIS;
    }
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), endMidnight=%d (%s)", ServiceDataCurrent.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
    return retValue;
  }

  /**
   * saves {@link StationDataNormal} for the day of timestamp at the actual tima
   * and at 00:00, the preceeding day at 00:00 and the following day at 00:00
   * and returns the persisted entities, which are supposed to be removed from
   * the database/entity manager by the user (possibly using {@link #tearDownWeatherData(java.util.Collection, javax.transaction.UserTransaction, javax.persistence.EntityManagerFactory)
   * }
   *
   * @param startTimestamp
   * @param endTimestamp
   * @param entityManager
   * @param idGenerator
   * @return
   * @throws NotSupportedException
   * @throws SystemException
   * @throws RollbackException
   * @throws HeuristicMixedException
   * @throws HeuristicRollbackException
   * @throws IllegalStateException
   * @see #tearDownWeatherData(java.util.Collection,
   * javax.transaction.UserTransaction, javax.persistence.EntityManagerFactory)
   */
  public static Map<Date, StationDataNormal> setUpWeatherStationData(
    long startTimestamp,
    long endTimestamp,
    PersistenceUtil persistenceUtil,
    EntityManager entityManager,
    IdGenerator idGenerator) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    long preceedingDayTimestampMidnight = DateConverter.
      convertTimestampToMidnight(
        startTimestamp) - DateConverter.ONE_DAY_IN_MILLIS;
    long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
      endTimestamp);
    Map<Date, StationDataNormal> retValue;
    long start = preceedingDayTimestampMidnight;
    retValue = new HashMap<>();
    while (start < endTimestampMidnight + DateConverter.ONE_DAY_IN_MILLIS) {
      StationDataNormal stationDataNormal = new StationDataNormal(idGenerator.
        getNextId(),
        DateUtils.truncate(new Date(start),
          Calendar.DATE),
        new Time(start),
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
      persistenceUtil.saveOrUpdate(entityManager,
        stationDataNormal,
        StationDataNormal.class,
        stationDataNormal.getId());
      retValue.put(new Date(start),
        stationDataNormal);
      start += DateConverter.ONE_DAY_IN_MILLIS;
    }
    StationDataNormal stationDataNormal = new StationDataNormal(idGenerator.
      getNextId(),
      new Date(
        startTimestamp),
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
    persistenceUtil.saveOrUpdate(entityManager,
      stationDataNormal,
      StationDataNormal.class,
      stationDataNormal.getId());
    retValue.put(new Date(startTimestamp),
      stationDataNormal);
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), start=%d (%s), endMidnight=%d (%s)", StationDataNormal.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
    return retValue;
  }

  public static <T extends Identifiable> void tearDownWeatherData(
    Map<?, T> mutableStationDatas,
    Class<T> clazz,
    EntityManager em) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    for (T mutableStationData : mutableStationDatas.values()) {
//			em.refresh(mutableStationData);
      T attached = em.find(clazz,
        mutableStationData.getId());
      em.remove(attached);
    }
  }

  private WeatherTestUtils() {
  }
}
