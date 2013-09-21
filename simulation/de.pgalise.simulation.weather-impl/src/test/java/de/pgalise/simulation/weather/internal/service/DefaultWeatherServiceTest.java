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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.it.TestUtils;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.embeddable.EJBContainer;
import static org.junit.Assert.*;
import org.junit.Test;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.DatabaseWeatherLoader;
import de.pgalise.simulation.weather.model.StationDataMap;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import java.sql.Date;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.BeforeClass;

/**
 * JUnit Tests for WeatherService
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
@LocalClient
@ManagedBean
public class DefaultWeatherServiceTest  {	
	private static EJBContainer CONTAINER;
	@PersistenceUnit(unitName = "weather_test")
	private EntityManagerFactory ENTITY_MANAGER_FACTORY;
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
	private WeatherLoader<DefaultWeatherCondition> loader;

	private City city;
	
	@Resource
	private UserTransaction utx;

	@SuppressWarnings("LeakingThisInConstructor")
	public DefaultWeatherServiceTest() throws NamingException {
		CONTAINER.getContext().bind("inject",
			this);
		
		InitialContext initialContext = new InitialContext();
//		utx = (UserTransaction) initialContext.lookup(
//			"java:comp/UserTransaction");
		
		
		Coordinate referencePoint = new Coordinate(52.516667, 13.4);
		Polygon referenceArea = GeotoolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			referenceArea);
		
		// Load EJB for Weather loader
		loader = new DatabaseWeatherLoader(ENTITY_MANAGER_FACTORY.createEntityManager());

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 11, 1, 20, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 11, 2, 20, 14, 0);
		endTimestamp = cal.getTimeInMillis();
	}
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}

	@Test
	public void testAddNewNextDayWeather() throws NamingException, NotSupportedException, 
		SystemException, 
	HeuristicMixedException, HeuristicRollbackException, IllegalStateException, 
	RollbackException {
		DefaultWeatherService service = new DefaultWeatherService(city, loader);

		// Test no prior call to Service.addNewWeather
		try {
			service.addNewNextDayWeather();
			fail();
		} catch (IllegalStateException expected) {
		}
		
		Map<Date, StationDataNormal> entities = TestUtils.setUpWeatherStationData(
			startTimestamp,
			endTimestamp,
			utx,
			ENTITY_MANAGER_FACTORY);
		
		service.addNewWeather(startTimestamp, endTimestamp,
				true, null);
		//the acutal test
		service.addNewNextDayWeather();
		
		TestUtils.tearDownWeatherData(entities, StationDataNormal.class,
			utx,
			ENTITY_MANAGER_FACTORY);
	}

	@Test
	public void testAddNewWeather() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		List<WeatherStrategyHelper> strategyList = new ArrayList<>(1);
		strategyList.add(new WeatherStrategyHelper(new ReferenceCityModifier(startTimestamp,
				loader), startTimestamp));
		DefaultWeatherService service = new DefaultWeatherService(city,
			loader);
		
		// Test false Date
		try {
			service.addNewWeather(0, 0, true, null);
			fail();
		} catch (IllegalArgumentException excepted) {
		}

		// Test (normal)
		Map<Date, DefaultServiceDataCurrent> prequisites = TestUtils.setUpWeatherServiceDataCurrent(
			startTimestamp,
			endTimestamp,
			city,
			utx,
			ENTITY_MANAGER_FACTORY);
		Map<Date, StationDataNormal> prequisites0 = TestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			utx,
			ENTITY_MANAGER_FACTORY);
		Map<Date, DefaultServiceDataForecast> prequisites1 = TestUtils.setUpWeatherServiceDataForecast(startTimestamp,
			endTimestamp,
			city,
			utx,
			ENTITY_MANAGER_FACTORY);
		
		service.addNewWeather(startTimestamp,
					endTimestamp, true, strategyList);
		
		TestUtils.tearDownWeatherData(prequisites,DefaultServiceDataCurrent.class,
			utx,
			ENTITY_MANAGER_FACTORY);
		TestUtils.tearDownWeatherData(prequisites0,
			StationDataNormal.class,
			utx,
			ENTITY_MANAGER_FACTORY);
		TestUtils.tearDownWeatherData(prequisites1,
			DefaultServiceDataForecast.class,
			utx,
			ENTITY_MANAGER_FACTORY);
	}

	@Test
	public void testCheckDate() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long testFalseDate = DateConverter.convertDate("12.03.2002", "dd.mm.yyyy").getTime(); //before simulation
		DefaultWeatherService service = new DefaultWeatherService(city, loader);

		//test negative result
		boolean expResult = false;
		boolean result = service.checkDate(startTimestamp);
		assertEquals(expResult,
			result);
		
		//test positive result (need to set up for the exact date, the preceeding and the following date at the same hour of the day)
		MutableStationData weather = new StationDataNormal(new Date(startTimestamp),
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
		MutableStationData weatherPreceeding = new StationDataNormal(new Date(startTimestamp-DateConverter.ONE_DAY_IN_MILLIS),
			new Time(startTimestamp-DateConverter.ONE_DAY_IN_MILLIS),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		MutableStationData weatherFollowing = new StationDataNormal(new Date(startTimestamp+DateConverter.ONE_DAY_IN_MILLIS),
			new Time(startTimestamp+DateConverter.ONE_DAY_IN_MILLIS),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		utx.begin();
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		em.joinTransaction();
		em.persist(weather);
		em.persist(weatherFollowing);
		em.persist(weatherPreceeding);
		utx.commit();
		expResult = true;
		result = service.checkDate(startTimestamp);
		assertEquals(expResult, result);
		utx.begin();
		em.remove(weather);
		em.remove(weatherFollowing);
		em.remove(weatherPreceeding);
		utx.commit();
		em.close();

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

	}

	@Test
	public void testGetValueWeatherParameterEnumLong() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		DefaultWeatherService service = new DefaultWeatherService(city, loader);
		
		/*
		 * Test preparations
		 */
		Map<Date, StationDataNormal> prequisites = TestUtils.setUpWeatherStationData(
			startTimestamp,
			endTimestamp,
			utx,
			ENTITY_MANAGER_FACTORY);

		// Get weather
		service.addNewWeather(startTimestamp, endTimestamp,
				true, null);

		/*
		 * Test variables
		 */

		// Test Parameter
		WeatherParameterEnum testParameter = WeatherParameterEnum.AIR_PRESSURE;
		Time time = DateConverter.convertTime("18:00:00", "hh:mm:ss");
		long timestamp = startTimestamp + time.getTime();

		/*
		 * Test cases
		 */

		Number value;

		// Test (normal)
		value = service.getValue(testParameter, timestamp);
		assertEquals(1008.0, value.doubleValue(), 10.0); // Aggregate 1008
		
		// Test false timestamp
		try {
			service.getValue(testParameter, 0);
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

		// Test false key
		try {
			service.getValue(null, 0);
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}
		
		TestUtils.tearDownWeatherData(prequisites,StationDataNormal.class,
			utx,
			ENTITY_MANAGER_FACTORY);
	}

	@Test
	public void testGetValueWeatherParameterEnumLongVector2d() throws ParseException, NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		WeatherParameterEnum testParameter = WeatherParameterEnum.LIGHT_INTENSITY;

		Time time = DateConverter.convertTime("18:00:00", "hh:mm:ss");
		long timestamp = startTimestamp + time.getTime();
		long timestamp2 = startTimestamp + DateConverter.ONE_DAY_IN_MILLIS + time.getTime();

		Coordinate position = new Coordinate(2, 3);
		DefaultWeatherService service = new DefaultWeatherService(city, loader);

		/*
		 * Test preparations
		 */
		Map<Date, StationDataNormal> entities = TestUtils.setUpWeatherStationData(
			startTimestamp,
			endTimestamp,
			utx,
			ENTITY_MANAGER_FACTORY);
		// Get weather
		service.addNewWeather(startTimestamp, endTimestamp,
				true, null);

		/*
		 * Test cases
		 */

		Number value;

		// Test (normal)
		WeatherMap weatherMap = new StationDataMap();
		MutableStationData weather = new StationDataNormal(new Date(startTimestamp),
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
		weatherMap.put(timestamp, weather);
		value = service.getValue(testParameter, timestamp, position);
		assertEquals(11000.000, value.doubleValue(), 5000);

		// Test false key
		try {
			service.getValue(null, 0, position);
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

		// Test false timestamp
		try {
			service.getValue(testParameter, 0, position);
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

		// Test false position
		try {
			service.getValue(testParameter, timestamp, null);
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

		// Test false position
		try {
			service.getValue(testParameter, timestamp, new Coordinate(-1, -1));
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}
		
		//keep entities in database until here
		TestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			utx,
			ENTITY_MANAGER_FACTORY);

		// Test (normal) other date
		weatherMap = new StationDataMap();
		weather = new StationDataNormal(new Date(startTimestamp),
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
		weatherMap.put(timestamp2, weather);
		value = service.getValue(testParameter, timestamp2, position);
		assertEquals(11000.000, value.doubleValue(), 6000);

		// Test false timestamp
		try {
			service.getValue(testParameter, endTimestamp
					+ DateConverter.ONE_DAY_IN_MILLIS + time.getTime(), position);
			fail();
		} catch (IllegalArgumentException e) {
			//expected
		}
	}

}
