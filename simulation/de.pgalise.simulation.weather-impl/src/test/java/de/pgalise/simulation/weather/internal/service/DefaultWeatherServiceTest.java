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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.shared.exception.NoWeatherDataFoundException;
import de.pgalise.simulation.weather.dataloader.ServiceWeather;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;
import de.pgalise.simulation.weather.internal.modifier.DatabaseTestUtils;
import de.pgalise.simulation.weather.internal.modifier.DatabaseTests;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.simulation.weather.util.WeatherStrategyHelper;
import java.io.IOException;
import java.net.Inet4Address;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.vecmath.Vector2d;
import static org.easymock.EasyMock.*;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;

/**
 * JUnit Tests for WeatherService
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 27, 2012)
 */
public class DefaultWeatherServiceTest  {	
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

	private WeatherLoader weatherLoaderMock = createStrictMock(WeatherLoader.class);
	private City city;

	public DefaultWeatherServiceTest() throws NamingException {
		Coordinate referencePoint = new Coordinate(20, 20);
		Polygon referenceArea = DatabaseTestUtils.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		city = new City("test_city", 200000, 100, true, true, referenceArea);
		
		Context ctx = DatabaseTestUtils.getCONTAINER().getContext();
		// Load EJB for Weather loader
		ctx.unbind("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");
		ctx.bind("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader", weatherLoaderMock);
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
	public void testAddNextWeather() throws NoWeatherDataFoundException {
		/*
		 * Test cases
		 */

		// Test no weather data loaded
		try {
			service.addNextWeather();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		/*
		 * Test preparations
		 */

		// Get weather
		service.addNewWeather(startTimestamp, endTimestamp,
				true, null);

		/*
		 * Test cases
		 */

		// Test next day (normal)
		reset(weatherLoaderMock);
		expect(weatherLoaderMock.checkStationDataForDay(anyLong())).andReturn(true);
		expect(weatherLoaderMock.loadStationData(anyLong())).andReturn(null);
		expect(weatherLoaderMock.loadStationData(anyLong())).andReturn(null);
		replay(weatherLoaderMock);
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
		reset(weatherLoaderMock);
		ServiceWeather serviceWeather = new ServiceWeather(System.currentTimeMillis(), city, 0.2f, 0.2f, 0.3f, 1.0f, 20.0f);
		weatherLoaderMock.setLoadOption(true);
		expectLastCall();
		WeatherMap weatherMap = new StationDataMap() ;
		expect(weatherLoaderMock.loadStationData(anyLong())).andReturn(weatherMap);
		expect(weatherLoaderMock.loadForecastServiceWeatherData(
					anyLong(), anyObject(City.class))).andReturn(serviceWeather);
		ServiceWeather serviceWeatherForecast = new ServiceWeather(System.currentTimeMillis(), city, 0.3f, 0.3f, 1.1f, 20.0f, 2.9f);
		expect(weatherLoaderMock.loadCurrentServiceWeatherData(anyLong(), anyObject(City.class))).andReturn(serviceWeatherForecast);
		long weatherTimestamp = System.currentTimeMillis();
		Weather weather = new Weather(weatherTimestamp, 1, 1, 1.0f, 1.0f, 1, 1.0f, 1.0f, 1, 1.0f);
		weatherMap.put(weatherTimestamp, weather);
		replay(weatherLoaderMock);
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
		try {
			service.checkDate(startTimestamp);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}

		// Test false Date
		try {
			service.checkDate(0);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		// Test false Date (future)
		reset(weatherLoaderMock);
		expect(weatherLoaderMock.checkStationDataForDay(anyLong())).andReturn(false).times(2);
		replay(weatherLoaderMock);
		Assert.assertTrue(!(service.checkDate(System.currentTimeMillis())));

		// Test false Date (no data available)
		Assert.assertTrue(!(service.checkDate(testFalseDate)));

	}

	@Test
	public void testGetValueWeatherParameterEnumLong() throws NoWeatherDataFoundException, ParseException {
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
		reset(weatherLoaderMock);
		WeatherMap weatherMap = new StationDataMap();
		Weather weather = new Weather(timestamp, 1008, 1, 1.0f, 1.0f, 1, 1.0f, 1.2f, 1, 1.3f);
		weatherMap.put(timestamp, weather);
		expect(weatherLoaderMock.checkStationDataForDay(anyLong())).andReturn(true);
		expect(weatherLoaderMock.loadStationData(anyLong())).andReturn(weatherMap);
		replay(weatherLoaderMock);
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
	public void testGetValueWeatherParameterEnumLongVector2d() throws NoWeatherDataFoundException, ParseException {
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
		reset(weatherLoaderMock);
		WeatherMap weatherMap = new StationDataMap();
		Weather weather = new Weather(timestamp, 1, 11000, 1.0f, 1.0f, 1, 1.0f, 1.2f, 1, 1.3f);
		weatherMap.put(timestamp, weather);
		expect(weatherLoaderMock.checkStationDataForDay(anyLong())).andReturn(true);
		expect(weatherLoaderMock.loadStationData(anyLong())).andReturn(weatherMap);
		replay(weatherLoaderMock);
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
		reset(weatherLoaderMock);
		weatherMap = new StationDataMap();
		weather = new Weather(timestamp2, 1, 11000, 1.0f, 1.0f, 1, 1.0f, 1.2f, 1, 1.3f);
		weatherMap.put(timestamp2, weather);
		expect(weatherLoaderMock.checkStationDataForDay(anyLong())).andReturn(true);
		expect(weatherLoaderMock.loadStationData(anyLong())).andReturn(weatherMap);
		replay(weatherLoaderMock);
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
