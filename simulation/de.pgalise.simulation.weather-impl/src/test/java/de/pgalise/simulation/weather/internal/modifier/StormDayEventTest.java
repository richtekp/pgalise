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
import org.junit.Test;

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.events.StormDayEvent;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.modifier.AbstractWeatherMapModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import java.util.Collection;
import javax.annotation.ManagedBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.BeforeClass;

/**
 * JUnit test for StormDayEvent
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@LocalClient
@ManagedBean
public class StormDayEventTest {
	@PersistenceUnit(unitName = "weather_test", name="weather_test_StormDayEventTest")
	private EntityManagerFactory ENTITY_MANAGER_FACTORY;
	private static EJBContainer CONTAINER;

	/**
	 * End timestamp
	 */
	public long endTimestamp;

	/**
	 * Start timestamp
	 */
	public long startTimestamp;

	/**
	 * Test timestamp
	 */
	public long testTimestamp;

	/**
	 * Test value
	 */
	public float testValue = 10.0f;

	/**
	 * Test duration
	 */
	public float testDuration = 4.0f;

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private WeatherLoader loader;
	
	private	City city;

	@SuppressWarnings("LeakingThisInConstructor")
	public StormDayEventTest() throws NamingException {
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

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2010, 5, 9, 0, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2010, 5, 10, 0, 0, 0);
		endTimestamp = cal.getTimeInMillis();

		// Test time
		cal.set(2010, 5, 9, 18, 0, 0);
		testTimestamp = cal.getTimeInMillis();

		// Create service
		service = new DefaultWeatherService(city, loader);
	}
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}

	@Test
	public void testDeployChanges() throws Exception {
		service = new DefaultWeatherService(city, loader);
		
		//preparation
		UserTransaction transaction = (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
		Collection<StationDataNormal> entities = TestUtils.setUpWeatherStationData(startTimestamp,
			endTimestamp,
			transaction,
			ENTITY_MANAGER_FACTORY);
		service.addNewWeather(startTimestamp, endTimestamp, true,
				null);
		TestUtils.tearDownWeatherData(entities,StationDataNormal.class,
			transaction,
			ENTITY_MANAGER_FACTORY);
		
		// Get extrema of reference values
		float refvalue = service.getValue(WeatherParameterEnum.WIND_VELOCITY,
				testTimestamp).floatValue();

		// Deploy strategy
		StormDayEvent event = new StormDayEvent(new DefaultRandomSeedService().getSeed(ColdDayEventTest.class
				.toString()), testTimestamp, null, testValue,
				testDuration, loader);
		service.deployStrategy(event);

		// Get extrema of decorator values
		float decvalue = service.getValue(WeatherParameterEnum.WIND_VELOCITY,
				testTimestamp).floatValue();

		/*
		 * Testcase 1
		 */

		// Test 1: extrema are not equals
		Assert.assertTrue(refvalue < decvalue);

		// Test 2: extrema are as high event
		Assert.assertEquals(AbstractWeatherMapModifier.round(event.getMaxValue(), 3), AbstractWeatherMapModifier.round(decvalue, 3), 1);
	}

}
