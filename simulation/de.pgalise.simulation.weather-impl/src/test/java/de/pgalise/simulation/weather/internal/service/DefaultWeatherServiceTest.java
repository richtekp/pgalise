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
import javax.naming.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.AbstractStationData;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import java.sql.Date;
import java.util.Properties;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
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

/**
 * JUnit Tests for WeatherService
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
@LocalClient
@ManagedBean
//@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
public class DefaultWeatherServiceTest  {	
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	@PersistenceUnit(unitName = "weather_data")
	private EntityManagerFactory ENTITY_MANAGER_FACTORY;// = TestUtils.createEntityManagerFactory("weather_data_test");
	/**
	 * End timestamp
	 */
	private long endTimestamp;

	/**
	 * Start timestamp
	 */
	private long startTimestamp;

	/**
	 * Test class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather loader
	 */
	private WeatherLoader loader;

	private City city;
	
//	@Resource
//	private UserTransaction utx;

	public DefaultWeatherServiceTest() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
//		p.put("openejb.tempclassloader.skip", "annotations");
		CONTAINER.getContext().bind("inject",
			this);
		
		Coordinate referencePoint = new Coordinate(20, 20);
		Polygon referenceArea = GeotoolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		city = new City("test_city", 200000, 100, true, true, referenceArea);
		
		Context ctx = CONTAINER.getContext();
		// Load EJB for Weather loader
		loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");
		service = new DefaultWeatherService(city, loader);

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 11, 1, 20, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 11, 2, 20, 14, 0);
		endTimestamp = cal.getTimeInMillis();
	}

	@After
	public void tearDown() throws Exception {
		// Delete old data
		if ((service.getReferenceValues() == null)
				|| !service.getReferenceValues().isEmpty()) {
			service._clearValues();
		}
	}

	@Test
	public void testAddNextWeather() throws NamingException, NotSupportedException, 
		SystemException, 
	HeuristicMixedException, HeuristicRollbackException, IllegalStateException, 
	RollbackException {
		/*
		 * Test cases
		 */

		// Test no weather data loaded
		try {
			service.addNextWeather();
			Assert.fail();
		} catch (Exception e) {
		}

		/*
		 * Test preparations
		 */

		// Get weather
		InitialContext initialContext = new InitialContext();
		UserTransaction utx = (UserTransaction) initialContext.lookup(
			"java:comp/UserTransaction");
//		UserTransaction userTransaction = new  
//org.apache.openejb.core.CoreUserTransaction(null);
		utx.begin();
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		em.joinTransaction();
		AbstractStationData stationData = new StationDataNormal(new Date(startTimestamp),
			new Time(DateConverter.convertTimestampToMidnight(startTimestamp)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		em.merge(stationData);
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
		em.merge(city);
		utx.commit();
		em.close();
		service.addNewWeather(startTimestamp, endTimestamp,
				true, null);

		/*
		 * Test cases
		 */

		// Test next day (normal)
		service.addNextWeather();
	}

	@Test
	public void testAddWeatherDate() throws ParseException {
		/*
		 * Test cases
		 */

		List<WeatherStrategyHelper> strategyList = new ArrayList<>(1);
		strategyList.add(new WeatherStrategyHelper(new ReferenceCityModifier(startTimestamp,
				loader), startTimestamp));

		// Test (normal)
		MutableStationData serviceWeather = new StationDataNormal(new Date(startTimestamp),
			new Time(DateConverter.convertTimestampToMidnight(startTimestamp)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		WeatherMap weatherMap = new StationDataMap() ;
		MutableStationData serviceWeatherForecast = new StationDataNormal(new Date(startTimestamp),
			new Time(DateConverter.convertTimestampToMidnight(startTimestamp)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		long weatherTimestamp = System.currentTimeMillis();
		MutableStationData weather = new StationDataNormal(new Date(startTimestamp),
			new Time(DateConverter.convertTimestampToMidnight(startTimestamp)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		weatherMap.put(weatherTimestamp, weather);
		service.addNewWeather(startTimestamp,
					endTimestamp, true, strategyList);

		// Test false Date
		try {
			service.addNewWeather(0, 0, true, null);
			Assert.fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testCheckDate() throws ParseException {
		long testFalseDate = DateConverter.convertDate("12.03.2002", "dd.mm.yyyy").getTime();

		/*
		 * Test cases
		 */

		// Test check Date (normal)
		service.checkDate(startTimestamp);

		// Test false Date
		try {
			service.checkDate(0);
			Assert.fail();
		} catch (Exception e) {
		}

		// Test false Date (future)
		Assert.assertTrue(!(service.checkDate(System.currentTimeMillis())));

		// Test false Date (no data available)
		Assert.assertTrue(!(service.checkDate(testFalseDate)));

	}

	@Test
	public void testGetValueWeatherParameterEnumLong() throws ParseException {
		/*
		 * Test preparations
		 */

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
		value = service.getValue(testParameter, timestamp);
		Assert.assertEquals(1008.0, value.doubleValue(), 10.0); // Aggregate 1008

		// Test false timestamp
		try {
			value = service.getValue(testParameter, 0);
			Assert.fail();
		} catch (RuntimeException e) {
			//expected
		}

		// Test false key
		try {
			value = service.getValue(null, 0);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//expected
		}
	}

	@Test
	public void testGetValueWeatherParameterEnumLongVector2d() throws ParseException {
		/*
		 * Test variables
		 */

		// Test Parameter
		WeatherParameterEnum testParameter = WeatherParameterEnum.LIGHT_INTENSITY;

		// Test times
		Time time = DateConverter.convertTime("18:00:00", "hh:mm:ss");
		long timestamp = startTimestamp + time.getTime();
		long timestamp2 = startTimestamp + DateConverter.NEXT_DAY_IN_MILLIS + time.getTime();

		// Test Position
		Coordinate position = new Coordinate(2, 3);

		/*
		 * Test preparations
		 */

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
		Assert.assertEquals(11000.000, value.doubleValue(), 5000);

		// Test false key
		try {
			value = service.getValue(null, 0, position);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

		// Test false timestamp
		try {
			value = service.getValue(testParameter, 0, position);
			Assert.fail();
		} catch (RuntimeException e) {
			//expected
		}

		// Test false position
		try {
			value = service.getValue(testParameter, timestamp, null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

		// Test false position
		try {
			value = service.getValue(testParameter, timestamp, new Coordinate(-1, -1));
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//expected
		}

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
		Assert.assertEquals(11000.000, value.doubleValue(), 6000);

		// Test false timestamp
		try {
			value = service.getValue(testParameter, endTimestamp
					+ DateConverter.NEXT_DAY_IN_MILLIS + time.getTime(), position);
			Assert.fail();
		} catch (RuntimeException e) {
			//expected
		}
	}

}
