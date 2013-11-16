/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.StationDataMap;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.model.ServiceDataCurrent;
import de.pgalise.simulation.weather.model.ServiceDataForecast;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.simulation.weather.testutils.WeatherTestUtils;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Date;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class DatabaseWeatherLoaderTest {
	@PersistenceUnit(unitName = "pgalise")
	private EntityManagerFactory entityManagerFactory;
	private static EJBContainer container;
	
	private final City city;
	
	@Resource
	private UserTransaction userTransaction;
	
	@SuppressWarnings("LeakingThisInConstructor")
	public DatabaseWeatherLoaderTest() throws NamingException {
		container.getContext().bind("inject",
			this);
		
		city = TestUtils.createDefaultTestCityInstance();
	}
	
	@BeforeClass
	public static void setUpClass() {
		container = TestUtils.getContainer();
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
		long endTimestamp = startTimestamp+DateConverter.ONE_DAY_IN_MILLIS;
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());
		boolean expResult = false;
		boolean result = instance.checkStationDataForDay(timestamp);
		assertEquals(expResult,
			result);
		
		WeatherService service = new DefaultWeatherService(city,
			instance);
		Map<Date, StationDataNormal> entities = WeatherTestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataCurrent> entities0 = WeatherTestUtils.setUpWeatherServiceDataCurrent(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataForecast> entities1 = WeatherTestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null);
		
		expResult = true;
		result = instance.checkStationDataForDay(timestamp);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of loadCurrentServiceWeatherData method, of class DatabaseWeatherLoader.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testLoadCurrentServiceWeatherData() throws Exception {
		long timestamp = System.currentTimeMillis();
		long startTimestamp = DateConverter.convertTimestampToMidnight(timestamp);
		long endTimestamp = startTimestamp+DateConverter.ONE_DAY_IN_MILLIS;
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());
		try {
			instance.loadCurrentServiceWeatherData(timestamp, city);
			fail();
		}catch(NoWeatherDataFoundException expected) {
		}
		
		WeatherService service = new DefaultWeatherService(city,
			instance);
		Map<Date, StationDataNormal> entities = WeatherTestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataCurrent> entities0 = WeatherTestUtils.setUpWeatherServiceDataCurrent(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataForecast> entities1 = WeatherTestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null);
		
		instance = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());
		ServiceDataCurrent<DefaultWeatherCondition> expResult = null;
		ServiceDataCurrent<DefaultWeatherCondition> result = instance.loadCurrentServiceWeatherData(timestamp, city);
		assertEquals(expResult, result);
		
		WeatherTestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities0,
			DefaultServiceDataCurrent.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities1,
			DefaultServiceDataForecast.class,
			userTransaction,
			entityManagerFactory);
	}

	/**
	 * Test of loadForecastServiceWeatherData method, of class DatabaseWeatherLoader.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testLoadForecastServiceWeatherData() throws Exception {
		long timestamp = System.currentTimeMillis();
		long startTimestamp = DateConverter.convertTimestampToMidnight(timestamp);
		long endTimestamp = startTimestamp+DateConverter.ONE_DAY_IN_MILLIS;
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());
		try {
			instance.loadForecastServiceWeatherData(timestamp, city);
		}catch(NoWeatherDataFoundException expected) {
		}
		
		WeatherService service = new DefaultWeatherService(city,
			instance);
		Map<Date, StationDataNormal> entities = WeatherTestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataCurrent> entities0 = WeatherTestUtils.setUpWeatherServiceDataCurrent(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataForecast> entities1 = WeatherTestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null);
		ServiceDataForecast<DefaultWeatherCondition> expResult =  null;
		ServiceDataForecast<DefaultWeatherCondition> result = instance.loadForecastServiceWeatherData(timestamp, city);
		assertEquals(expResult, result);
		
		WeatherTestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities0,
			DefaultServiceDataCurrent.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities1,
			DefaultServiceDataForecast.class,
			userTransaction,
			entityManagerFactory);
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
		long endTimestamp = startTimestamp+DateConverter.ONE_DAY_IN_MILLIS;
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());
		try {
			instance.loadForecastServiceWeatherData(timestamp, city);
		}catch(NoWeatherDataFoundException expected) {
		}
	
		WeatherService service = new DefaultWeatherService(city,
			instance);
		Map<Date, StationDataNormal> entities = WeatherTestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataCurrent> entities0 = WeatherTestUtils.setUpWeatherServiceDataCurrent(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		Map<Date, DefaultServiceDataForecast> entities1 = WeatherTestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			userTransaction,
			entityManagerFactory);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null);
		instance = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());
		WeatherMap expResult = new StationDataMap();
		expResult.put(timestamp, null);
		WeatherMap result = instance.loadStationData(timestamp);
		assertFalse(result.isEmpty());
		
		WeatherTestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities0,
			DefaultServiceDataCurrent.class,
			userTransaction,
			entityManagerFactory);
		WeatherTestUtils.tearDownWeatherData(entities1,
			DefaultServiceDataForecast.class,
			userTransaction,
			entityManagerFactory);
	}
	
	private boolean stationDataEqualsIgnoreTimestamp(StationData o1, StationData o2) {
		return true;
	}
}