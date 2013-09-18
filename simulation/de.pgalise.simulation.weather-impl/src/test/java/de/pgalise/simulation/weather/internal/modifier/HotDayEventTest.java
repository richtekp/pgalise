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
import java.util.GregorianCalendar;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.events.HotDayEvent;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.modifier.AbstractWeatherMapModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
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
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.After;
import org.junit.BeforeClass;

/**
 * JUnit test for HotDayEvent
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@LocalClient
@ManagedBean
public class HotDayEventTest {
	@PersistenceUnit(unitName = "weather_test")
	private EntityManagerFactory ENTITY_MANAGER_FACTORY;
	private static EJBContainer CONTAINER;

	/**
	 * End timestamp
	 */
	private static long endTimestamp;

	/**
	 * Start timestamp
	 */
	private static long startTimestamp;

	/**
	 * Test timestamp
	 */
	private static long testTimestamp;

	/**
	 * Test value
	 */
	private static float testValue = 30.0f;

	/**
	 * Test duration
	 */
	private static float testDuration = 4.0f;

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private WeatherLoader<?> loader;
	
	private	City city;

	@SuppressWarnings("LeakingThisInConstructor")
	public HotDayEventTest() throws NamingException {
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
		loader = (WeatherLoader<?>) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2010, 5, 12, 0, 0, 0);
		HotDayEventTest.startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2010, 5, 13, 0, 0, 0);
		HotDayEventTest.endTimestamp = cal.getTimeInMillis();

		// Test time
		cal.set(2010, 5, 12, 18, 0, 0);
		HotDayEventTest.testTimestamp = cal.getTimeInMillis();

		// Create service
		service = new DefaultWeatherService(city, loader);
	}
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}
	
	private Queue<Object> deletes = new LinkedList<>();

	@Before
	public void setUp() throws Exception {
		service = new DefaultWeatherService(city, loader);
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
			stationDataNormal1 = new StationDataNormal(new Date(testTimestamp),
			new Time(testTimestamp),
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
		em.persist(stationDataNormal1);
		em.persist(stationDataNormal2);
		transaction.commit();
		deletes.add(stationDataNormal0);
		deletes.add(stationDataNormal);
		deletes.add(stationDataNormal1);
		deletes.add(stationDataNormal2);
		service.addNewWeather(testTimestamp, endTimestamp, true,
				null); //adds new data for startTimestamp and endTimestamp
		service.getReferenceValues().put(testTimestamp, new StationDataNormal(new Date(testTimestamp),
			new Time(testTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f)); //adds new data for testTimestamp
	}
	
	@After 
	public void tearDown() throws Exception {
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
		em.joinTransaction();
		while(!deletes.isEmpty()) {
			Object delete = deletes.poll();
			em.remove(delete);
		}
		transaction.commit();
		em.close();
	}

	@Test
	public void testDeployChanges() throws Exception {
		// Get extrema of reference values
		float refvalue = service.getValue(WeatherParameterEnum.TEMPERATURE,
				HotDayEventTest.testTimestamp).floatValue();

		// Deploy strategy
		HotDayEvent event = new HotDayEvent(new DefaultRandomSeedService().getSeed(ColdDayEventTest.class.toString()),
				HotDayEventTest.testTimestamp, null, HotDayEventTest.testValue, HotDayEventTest.testDuration,
				loader);
		service.deployStrategy(event);

		// Get extrema of decorator values
		float decvalue = service.getValue(WeatherParameterEnum.TEMPERATURE,
				HotDayEventTest.testTimestamp).floatValue();

		/*
		 * Testcase 1
		 */

		// Test 1: Max are not equals - Temperature
		Assert.assertTrue(refvalue < decvalue);

		// Test 2: Max are as high event - Temperature
		Assert.assertEquals(AbstractWeatherMapModifier.round(event.getMaxValue(), 3), AbstractWeatherMapModifier.round(decvalue, 3), 1);
	}

}
