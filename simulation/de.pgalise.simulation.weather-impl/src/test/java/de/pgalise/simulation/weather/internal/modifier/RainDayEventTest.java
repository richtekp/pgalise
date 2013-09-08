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
 
package de.pgalise.simulation.weather.internal.modifier;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.dataloader.DatabaseWeatherLoader;
import de.pgalise.simulation.weather.internal.dataloader.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.events.RainDayEvent;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.modifier.WeatherMapModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;

/**
 * JUnit test for RainDayEvent
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
public class RainDayEventTest {
	private final static EntityManager ENTITY_MANAGER = DatabaseTestUtils.getENTITY_MANAGER();

	/**
	 * End timestamp
	 */
	private final long endTimestamp;

	/**
	 * Start timestamp
	 */
	private final long startTimestamp;

	/**
	 * Test timestamp
	 */
	private final long testTimestamp;

	/**
	 * Test value
	 */
	public static float testValue = 20.0f;

	/**
	 * Test duration
	 */
	public static float testDuration = 4.0f;

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private static WeatherLoader loader;
	
	private	City city;

	public RainDayEventTest() throws NamingException {
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
		loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Start
		Calendar cal = new GregorianCalendar();
		cal.clear(); //important because otherwise setting 0 to hour, second and 
			//millisecond has no effect and current time is taken
		cal.set(2011, 5, 11, 0, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2011, 5, 12, 0, 0, 0);
		endTimestamp = cal.getTimeInMillis();

		// Test time
		cal.set(2011, 5, 11, 18, 0, 0);
		testTimestamp = cal.getTimeInMillis();

		// Create service
		service = new DefaultWeatherService(city, RainDayEventTest.loader);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ENTITY_MANAGER.close();
	}
	
	private Queue<Object> deletes = new LinkedList<>();

	@Before
	public void setUp() throws Exception {
		service = new DefaultWeatherService(city, loader);
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(startTimestamp);
		cal.add(Calendar.DATE, -1);
		long previousDayTimestamp = cal.getTimeInMillis();
		StationDataNormal stationDataNormal0 = new StationDataNormal(new Date(previousDayTimestamp), new Time(previousDayTimestamp), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f),
			stationDataNormal = new StationDataNormal(new Date(startTimestamp), new Time(startTimestamp), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f),
			stationDataNormal1 = new StationDataNormal(new Date(testTimestamp), new Time(testTimestamp), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f),
			stationDataNormal2 = new StationDataNormal(new Date(endTimestamp), new Time(endTimestamp), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f);
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		ENTITY_MANAGER.joinTransaction();
		ENTITY_MANAGER.persist(stationDataNormal0);
		ENTITY_MANAGER.persist(stationDataNormal);
		ENTITY_MANAGER.persist(stationDataNormal1);
		ENTITY_MANAGER.persist(stationDataNormal2);
		transaction.commit();
		deletes.add(stationDataNormal0);
		deletes.add(stationDataNormal);
		deletes.add(stationDataNormal1);
		deletes.add(stationDataNormal2);
		service.addNewWeather(testTimestamp, endTimestamp, true,
				null); //adds new data for startTimestamp and endTimestamp
		service.getReferenceValues().put(testTimestamp, new Weather(testTimestamp, 1, 1, 1.0f, 1.0f, 1, 1.0f, 1.0f, 1, 1.0f)); //adds new data for testTimestamp
	}
	
	@After 
	public void tearDown() throws Exception {
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		ENTITY_MANAGER.joinTransaction();
		while(!deletes.isEmpty()) {
			Object delete = deletes.poll();
			ENTITY_MANAGER.remove(delete);
		}
		transaction.commit();
	}

	@Test
	public void testDeployChanges() throws Exception {
		// Get extrema of reference values
		StationDataNormal stationDataNormal0 = new StationDataNormal(new Date(testTimestamp+Weather.INTERPOLATE_INTERVAL), new Time(testTimestamp+Weather.INTERPOLATE_INTERVAL), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f),
			stationDataNormal1 = new StationDataNormal(new Date(testTimestamp-4*Weather.INTERPOLATE_INTERVAL), new Time(testTimestamp-4*Weather.INTERPOLATE_INTERVAL), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f);
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		ENTITY_MANAGER.joinTransaction();
		ENTITY_MANAGER.persist(stationDataNormal0);
		ENTITY_MANAGER.persist(stationDataNormal1);
		transaction.commit();
		deletes.add(stationDataNormal0);
		deletes.add(stationDataNormal1);
		
		float refvalue = service.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
				testTimestamp).floatValue();

		// Deploy strategy
		RainDayEvent event = new RainDayEvent(
				new DefaultRandomSeedService().getSeed(ColdDayEventTest.class.toString()),
				testTimestamp, null, RainDayEventTest.testValue, RainDayEventTest.testDuration,
				RainDayEventTest.loader);
		service.deployStrategy(event);

		// Get extrema of decorator values
		float decvalue = service.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
				testTimestamp).floatValue();

		/*
		 * Testcase 1
		 */

		// Test 1: extrema are not equals
		Assert.assertTrue(refvalue < decvalue);

		// Test 2: extrema are as high event
		Assert.assertEquals(WeatherMapModifier.round(event.getMaxValue(), 3), WeatherMapModifier.round(decvalue, 3), 1);

	}
}
