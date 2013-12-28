/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather.testutils;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author richter
 */
public class WeatherTestUtils {
	
	/**
	 * sets up entities of type <tt>DefaultServiceDataForecast</tt> for startTimestamp at midnight, the day before startTimestamp at midnight and endTimestamp at midnight
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param city
	 * @param utx
	 * @param entityManagerFactory
	 * @return
	 * @throws NotSupportedException
	 * @throws SystemException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws IllegalStateException
	 * @throws RollbackException 
	 */
	public static Map<Date, DefaultServiceDataForecast> setUpWeatherServiceDataForecast(long startTimestamp, long endTimestamp, City city, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long preceedingDayTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp)-DateConverter.ONE_DAY_IN_MILLIS;
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		em.merge(city);
		long start = preceedingDayTimestampMidnight;
		Map<Date, DefaultServiceDataForecast> retValue = new HashMap<>();
		while(start < endTimestampMidnight+DateConverter.ONE_DAY_IN_MILLIS) {
			DefaultServiceDataForecast serviceDataForecast = new DefaultServiceDataForecast(new Date(start),
				new Time(start),
				city,
				Measure.valueOf(10.0f,
				SI.CELSIUS),
				Measure.valueOf(1.0f, SI.CELSIUS),
				1.0f,
				1.0f,
				1.0f,
				WeatherCondition.UNKNOWN_CONDITION
			);
			em.merge(serviceDataForecast);
			retValue.put(new Date(start), serviceDataForecast);
			start += DateConverter.ONE_DAY_IN_MILLIS;
		}
		em.close();
		utx.commit();
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), endMidnight=%d (%s)", DefaultServiceDataForecast.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
		return retValue;
	}
	
	/**
	 * sets up entities of type <tt>DefaultServiceDataCurrent</tt> for startTimestamp at midnight, the day before startTimestamp at midnight and endTimestamp at midnight
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param city
	 * @param utx
	 * @param entityManagerFactory
	 * @return
	 * @throws NotSupportedException
	 * @throws SystemException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws IllegalStateException
	 * @throws RollbackException 
	 */
	public static Map<Date, DefaultServiceDataCurrent> setUpWeatherServiceDataCurrent(long startTimestamp, long endTimestamp, City city, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long preceedingDayTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp)-DateConverter.ONE_DAY_IN_MILLIS;
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		em.merge(city);
		long start = preceedingDayTimestampMidnight;
		Map<Date, DefaultServiceDataCurrent> retValue = new HashMap<>();
		while(start < endTimestampMidnight+DateConverter.ONE_DAY_IN_MILLIS) {
			DefaultServiceDataCurrent serviceDataCurrent = new DefaultServiceDataCurrent(new Date(start),
				new Time(start),
				city,
				1.0f,
				Measure.valueOf(1.0f, SI.CELSIUS),
				1.0f,
				1.0f,
				WeatherCondition.UNKNOWN_CONDITION
			);
			em.merge(serviceDataCurrent);
			retValue.put(new Date(start), serviceDataCurrent);
			start += DateConverter.ONE_DAY_IN_MILLIS;
		}
		em.close();
		utx.commit();
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), endMidnight=%d (%s)", DefaultServiceDataCurrent.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
		return retValue;
	}
	
	/**
	 * saves {@link StationDataNormal} for the day of timestamp at the actual tima and at 00:00, the 
	 * preceeding day at 00:00 and the following day at 00:00 and returns the 
	 * persisted entities, which are supposed to be removed from the 
	 * database/entity manager by the user (possibly using {@link #tearDownWeatherData(java.util.Collection, javax.transaction.UserTransaction, javax.persistence.EntityManagerFactory) }
	 * @param startTimestamp 
	 * @param endTimestamp 
	 * @param utx 
	 * @param entityManagerFactory 
	 * @return 
	 * @throws NotSupportedException 
	 * @throws SystemException 
	 * @throws RollbackException 
	 * @throws HeuristicMixedException 
	 * @throws HeuristicRollbackException 
	 * @throws IllegalStateException 
	 * @see #tearDownWeatherData(java.util.Collection, javax.transaction.UserTransaction, javax.persistence.EntityManagerFactory) 
	 */
	public static Map<Date, StationDataNormal> setUpWeatherStationData(long startTimestamp, long endTimestamp, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long preceedingDayTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp)-DateConverter.ONE_DAY_IN_MILLIS;
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		long start = preceedingDayTimestampMidnight;
		Map<Date, StationDataNormal> retValue = new HashMap<>();
		while(start < endTimestampMidnight+DateConverter.ONE_DAY_IN_MILLIS) {
			StationDataNormal stationDataNormal = new StationDataNormal(new Date(start),
			new Time(start),
				1,
				1,
				1.0f,
				Measure.valueOf(1.0f, SI.CELSIUS),
				1.0f,
				1,
				1.0f,
				1.0f,
				1.0f);
			em.merge(stationDataNormal);
			retValue.put(new Date(start), stationDataNormal);
			start += DateConverter.ONE_DAY_IN_MILLIS;
		}
		StationDataNormal stationDataNormal = new StationDataNormal(new Date(startTimestamp),
			new Time(startTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		em.merge(stationDataNormal);
		retValue.put(new Date(startTimestamp), stationDataNormal);
		em.close();
		utx.commit();
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), start=%d (%s), endMidnight=%d (%s)", StationDataNormal.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
		return retValue;
	}
	
	public static <T extends Identifiable> void tearDownWeatherData(Map<?, T> mutableStationDatas, Class<T> clazz, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		for(T mutableStationData : mutableStationDatas.values()) {
//			em.refresh(mutableStationData);
			T attached = em.find(clazz,
				mutableStationData.getId());
			em.remove(attached);
		}
		utx.commit();
		em.close();
	}
}
