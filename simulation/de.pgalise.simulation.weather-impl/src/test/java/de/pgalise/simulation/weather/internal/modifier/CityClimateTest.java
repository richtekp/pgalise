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
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.it.TestUtils;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.CityClimateModifier;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;
import de.pgalise.simulation.weather.model.StationData;
import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.ManagedBean;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.After;

/**
 * JUnit test for CityClimatemodifier
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@LocalClient
@ManagedBean
public class CityClimateTest {
	private final static EntityManagerFactory ENTITY_MANAGER_FACTORY = TestUtils.createEntityManagerFactory("weather_data_test");
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	
	/**
	 * End timestamp
	 */
	public final static long endTimestamp;

	/**
	 * Start timestamp
	 */
	public final static long startTimestamp;
	static {
		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 5, 10, 0, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2011, 5, 14, 0, 0, 0);
		endTimestamp = cal.getTimeInMillis();
	}

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private static WeatherLoader loader;
	
	private	City city;

	public CityClimateTest() throws NamingException {
		service = new DefaultWeatherService(city, loader);
		 Properties p = new Properties();
    p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		p.put("openejb.tempclassloader.skip", "annotations");
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
		
		Context ctx =  CONTAINER.getContext();

		// Load EJB for Weather loader
		loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Create service
		service = new DefaultWeatherService(city, loader);
	}
	
	private Queue<Object> deletes = new LinkedList<>();

	@Before
	public void setUp() throws Exception {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(startTimestamp);
		cal.add(Calendar.DATE, -1);
		long previousDayTimestamp = cal.getTimeInMillis();
		StationDataNormal stationDataNormal0 = new StationDataNormal(new Date(previousDayTimestamp),
			new Time(previousDayTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f),
			stationDataNormal = new StationDataNormal(new Date(startTimestamp),
			new Time(startTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f),
			stationDataNormal2 = new StationDataNormal(new Date(endTimestamp),
			new Time(endTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		em.joinTransaction();
		em.persist(stationDataNormal0);
		em.persist(stationDataNormal);
//		em.persist(stationDataNormal1);
		em.persist(stationDataNormal2);
		transaction.commit();
		deletes.add(stationDataNormal0);
		deletes.add(stationDataNormal);
//		deletes.add(stationDataNormal1);
		deletes.add(stationDataNormal2);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null); //adds new data for startTimestamp and endTimestamp
//		service.getReferenceValues().put(testTimestamp, new Weather(testTimestamp, 1, 1, 1.0f, 1.0f, 1, 1.0f, 1.0f, 1, 1.0f)); //adds new data for testTimestamp
		em.close();
	}
	
	@After 
	public void tearDown() throws Exception {
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		em.joinTransaction();
		while(!deletes.isEmpty()) {
			Object delete = deletes.poll();
			em.remove(delete);
		}
		transaction.commit();
		em.close();
	}
	
//	@AfterClass
//	public static void tearDownClass() {
//		ENTITY_MANAGER.close();
//	}

	@Test
	public void testDeployChanges() throws Exception {
		// Get reference values
		WeatherMap referenceValues = service.getReferenceValues();

		// Get max of reference values
		StationData refmax = Collections.max(referenceValues.values(), new TemperatureComparator());
		float refvalue1 = refmax.getTemperature().floatValue(SI.CELSIUS);
		float refvalue2 = refmax.getPerceivedTemperature();
		float refvalue3 = refmax.getRadiation();
		float refvalue4 = refmax.getRelativHumidity();
		float refvalue5 = refmax.getWindVelocity();
		float refvalue6 = refmax.getPrecipitationAmount();
		long reftime = refmax.getMeasureTime().getTime();

		// Deploy strategy
		CityClimateModifier modifier = new CityClimateModifier(
			new DefaultRandomSeedService().getSeed(CityClimateTest.class.toString()), 
			loader
		);
		service.deployStrategy(modifier);

		// Get modifier values
		WeatherMap modifierValues = service.getReferenceValues();

		// Get max of modifier values
		StationData decmax = modifierValues.get(reftime);

		/*
		 * Testcase 1 : Temperature
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue1 != decmax.getTemperature().floatValue(SI.CELSIUS));

		/*
		 * Testcase 2 : Perceived Temperature
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue2 != decmax.getPerceivedTemperature());

		/*
		 * Testcase 3 : Radiation
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue3 != decmax.getRadiation());

		/*
		 * Testcase 4 : RelativHumidity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue4 != decmax.getRelativHumidity());

		/*
		 * Testcase 5 : WindVelocity
		 */

		// Test 1: Max are not equals
		Assert.assertTrue(refvalue5 != decmax.getWindVelocity());

		/*
		 * Testcase 6 : PrecipitationAmount
		 */

		// Test 1: Max are not equals
		if (modifier.isRainDay()) {
			Assert.assertTrue(refvalue6 != decmax.getPrecipitationAmount());
		} else {
			Assert.assertEquals(refvalue6, decmax.getPrecipitationAmount());
		}

	}

}
