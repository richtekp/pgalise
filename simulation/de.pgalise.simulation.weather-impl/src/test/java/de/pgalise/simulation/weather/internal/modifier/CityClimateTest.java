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

import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.testutils.TestUtils;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import javax.ejb.embeddable.EJBContainer;

import junit.framework.Assert;

import org.junit.Test;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.DatabaseWeatherLoader;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.simulationevents.CityClimateModifier;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.internal.util.comparator.TemperatureComparator;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.testutils.WeatherTestUtils;
import java.sql.Date;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.measure.unit.SI;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.BeforeClass;

/**
 * JUnit test for CityClimatemodifier
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
@LocalClient
@ManagedBean
public class CityClimateTest {
	@PersistenceUnit(unitName = "pgalise-weather")
	private EntityManagerFactory entityManagerFactory;
	private static EJBContainer container;
	
	/**
	 * End timestamp
	 */
	public final long endTimestamp;

	/**
	 * Start timestamp
	 */
	public final long startTimestamp;

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Weather Loader
	 */
	private static WeatherLoader loader;
	
	private	City city;
	
	@Resource
	private UserTransaction userTransaction;

	@SuppressWarnings("LeakingThisInConstructor")
	public CityClimateTest() throws NamingException {
		container.getContext().bind("inject",
			this);
		
		city = TestUtils.createDefaultTestCityInstance();
		
		// Load EJB for Weather loader
		loader = new DatabaseWeatherLoader(entityManagerFactory.createEntityManager());

		// Create service
		service = new DefaultWeatherService(city, loader);
		
		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 5, 10, 0, 0, 0);
		startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2011, 5, 14, 0, 0, 0);
		endTimestamp = cal.getTimeInMillis();
	}
	
	@BeforeClass
	public static void setUpClass() {
		container = TestUtils.getContainer();
	}

	@Test
	public void testDeployChanges() throws Exception {
		service = new DefaultWeatherService(city, loader);
		//preparations
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(startTimestamp);
		cal.add(Calendar.DATE, -1);
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

}
