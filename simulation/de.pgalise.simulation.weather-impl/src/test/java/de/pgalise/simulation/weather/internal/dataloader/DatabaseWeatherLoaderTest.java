/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.model.ServiceDataCurrent;
import de.pgalise.simulation.weather.model.ServiceDataForecast;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.model.StationDataMap;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.service.WeatherService;
import de.pgalise.testutils.weather.WeatherTestUtils;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.testutils.TestUtils;
import java.sql.Date;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@LocalBean
@ManagedBean
public class DatabaseWeatherLoaderTest {

	@PersistenceContext(unitName = "pgalise-weather")
	private EntityManager entityManager;

	private City city;
	@EJB
	private WeatherLoader instance;

	@Resource
	private UserTransaction userTransaction;

	public DatabaseWeatherLoaderTest() {
	}

	@Before
	public void setUp() throws NamingException, NotSupportedException, SystemException {
		TestUtils.getContainer().getContext().bind("inject",
			this);
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
			city = TestUtils.createDefaultTestCityInstance();
			boolean result = instance.checkStationDataForDay(timestamp);
			assertEquals(expResult,
				result);

			WeatherService service = new DefaultWeatherService(city,
				instance);
			Map<Date, StationDataNormal> entities = WeatherTestUtils.
				setUpWeatherStationData(startTimestamp,
					endTimestamp,
					entityManager);
			Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
				setUpWeatherServiceDataCurrent(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
				setUpWeatherServiceDataForecast(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			service.addNewWeather(startTimestamp,
				endTimestamp,
				true,
				null);

			expResult = true;
			result = instance.checkStationDataForDay(timestamp);
			assertEquals(expResult,
				result);
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
					entityManager);
			Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
				setUpWeatherServiceDataCurrent(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
				setUpWeatherServiceDataForecast(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			service.addNewWeather(startTimestamp,
				endTimestamp,
				true,
				null);

			ServiceDataCurrent expResult = null;
			ServiceDataCurrent result = instance.
				loadCurrentServiceWeatherData(timestamp,
					city);
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
	 * Test of loadForecastServiceWeatherData method, of class
	 * DatabaseWeatherLoader.
	 *
	 * @throws Exception
	 */
	@Test
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
					entityManager);
			Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
				setUpWeatherServiceDataCurrent(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
				setUpWeatherServiceDataForecast(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			service.addNewWeather(startTimestamp,
				endTimestamp,
				true,
				null);
			ServiceDataForecast expResult = null;
			ServiceDataForecast result = instance.loadForecastServiceWeatherData(
				timestamp,
				city);
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
					entityManager);
			Map<Date, ServiceDataCurrent> entities0 = WeatherTestUtils.
				setUpWeatherServiceDataCurrent(startTimestamp,
					endTimestamp,
					city,
					entityManager);
			Map<Date, ServiceDataForecast> entities1 = WeatherTestUtils.
				setUpWeatherServiceDataForecast(startTimestamp,
					endTimestamp,
					city,
					entityManager);
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

	private boolean stationDataEqualsIgnoreTimestamp(StationData o1,
		StationData o2) {
		return true;
	}
}
