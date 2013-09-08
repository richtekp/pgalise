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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;

/**
 * JUnit test for ReferenceCitymodifier
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
public class ReferenceCityTest {
	private final static EntityManager ENTITY_MANAGER = DatabaseTestUtils.getENTITY_MANAGER();


	/**
	 * End timestamp
	 */
	public static long endTimestamp;

	/**
	 * Start timestamp
	 */
	public static long startTimestamp;

	/**
	 * Service Class
	 */
	private static DefaultWeatherService service;
	
	private	City city;

	/**
	 * Weather Loader
	 */
	private WeatherLoader loader;

	public ReferenceCityTest() throws NamingException {
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
		cal.set(2012, 8, 20, 0, 0, 0);
		ReferenceCityTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 8, 21, 0, 0, 0);
		ReferenceCityTest.endTimestamp = cal.getTimeInMillis();

		// Create service
		ReferenceCityTest.service = new DefaultWeatherService(city, loader);
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
//			stationDataNormal1 = new StationDataNormal(new Date(testTimestamp), new Time(testTimestamp), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f),
			stationDataNormal2 = new StationDataNormal(new Date(endTimestamp), new Time(endTimestamp), 1, 1, 1.0f, 1.0f, 1.0f, 1, 1.0f, 1, 1.0f);
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		ENTITY_MANAGER.joinTransaction();
		ENTITY_MANAGER.persist(stationDataNormal0);
		ENTITY_MANAGER.persist(stationDataNormal);
//		em.persist(stationDataNormal1);
		ENTITY_MANAGER.persist(stationDataNormal2);
		transaction.commit();
		deletes.add(stationDataNormal0);
		deletes.add(stationDataNormal);
//		deletes.add(stationDataNormal1);
		deletes.add(stationDataNormal2);
		ReferenceCityTest.service.addNewWeather(ReferenceCityTest.startTimestamp, ReferenceCityTest.endTimestamp, true,
				null);
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
		// Get reference values
		WeatherMap referenceValues = ReferenceCityTest.service.getReferenceValues();

		// Get max of reference values
		Weather refmax = Collections.max(referenceValues.values(), new TemperatureComparator());
		float refvalue1 = refmax.getTemperature();
		float refvalue2 = refmax.getPerceivedTemperature();
		float refvalue3 = refmax.getRelativHumidity();
		float refvalue4 = refmax.getWindVelocity();
		long reftime = refmax.getTimestamp();

		// Deploy strategy
		ReferenceCityModifier modifier = new ReferenceCityModifier(
				new DefaultRandomSeedService().getSeed(ReferenceCityTest.class.toString()), loader);
		ReferenceCityTest.service.deployStrategy(modifier);

		// Get modifier values
		WeatherMap modifierValues = ReferenceCityTest.service.getReferenceValues();

		// Get max of modifier values
		Weather decmax = modifierValues.get(reftime);

		/*
		 * Testcase 1 : Temperature
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue1 != decmax.getTemperature());

		/*
		 * Testcase 2 : Perceived Temperature
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue2 != decmax.getPerceivedTemperature());

		/*
		 * Testcase 3 : RelativHumidity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue3 != decmax.getRelativHumidity());

		/*
		 * Testcase 4 : WindVelocity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue4 != decmax.getWindVelocity());
	}
}
