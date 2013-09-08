/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.dataloader.ServiceWeather;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.internal.dataloader.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationData;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataMap;
import de.pgalise.simulation.weather.internal.dataloader.entity.StationDataNormal;
import de.pgalise.simulation.weather.internal.modifier.DatabaseTestUtils;
import de.pgalise.simulation.weather.parameter.WindDirection;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author richter
 */
public class DatabaseWeatherLoaderTest {
	
	public DatabaseWeatherLoaderTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of checkStationDataForDay method, of class DatabaseWeatherLoader.
	 */
	@Test
	public void testCheckStationDataForDay() throws Exception {
		System.out.println("checkStationDataForDay");
		long timestamp = System.currentTimeMillis();
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
		City city = new City("test_city", 200000, 100, true, true, referenceArea);
		EntityManager entityManagerMock = createMock(EntityManager.class);
		TypedQuery queryMock = createStrictMock(TypedQuery.class);
		Date serviceDataCurrentDate = new Date(timestamp);
		int airPressure = 1, lightIntensity= 1, radiation = 2, windDirection = 12;
		float perceivedTemperature = 1.0f, temperature = 1.0f, precipitationAmout = 2.0f, relativeHumidity =2.0f, windVelocity=1.7f;
		StationData serviceDataCurrent = new StationDataNormal(new Date(timestamp), new Time(timestamp), airPressure, lightIntensity, perceivedTemperature, perceivedTemperature, precipitationAmout, radiation, relativeHumidity, windDirection, windVelocity);
		expect(entityManagerMock.createNamedQuery(
					StationDataNormal.class.getSimpleName() + ".findLastEntryByDate", StationDataNormal.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.setMaxResults(1)).andReturn(null);
		expect(queryMock.getSingleResult()).andReturn(serviceDataCurrent );
		
		expect(entityManagerMock.createNamedQuery("StationDataNormal.findByDate", StationDataNormal.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.getResultList()).andReturn(new ArrayList(Arrays.asList(serviceDataCurrent)));
		expect(entityManagerMock.createNamedQuery("StationDataNormal.findFirstEntryByDate", StationDataNormal.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.setMaxResults(1)).andReturn(null);
		expect(queryMock.getSingleResult()).andReturn(serviceDataCurrent);
		replay(entityManagerMock, queryMock);
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader();
		instance.setEntityManager(entityManagerMock);
		boolean expResult = true;
		boolean result = instance.checkStationDataForDay(timestamp);
		assertEquals(expResult, result);
	}

	/**
	 * Test of loadCurrentServiceWeatherData method, of class DatabaseWeatherLoader.
	 */
	@Test
	public void testLoadCurrentServiceWeatherData() throws Exception {
		System.out.println("loadCurrentServiceWeatherData");
		long timestamp = System.currentTimeMillis();
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
		City city = new City("test_city", 200000, 100, true, true, referenceArea);
		EntityManager entityManagerMock = createStrictMock(EntityManager.class);
		TypedQuery queryMock = createStrictMock(TypedQuery.class);
		Date serviceDataCurrentDate = new Date(timestamp);
		float relativeHumidity = 1.0f, windDirection = 1.0f, windVelocity=2.0f;
		ServiceDataCurrent serviceDataCurrent = new ServiceDataCurrent(relativeHumidity, 20.0f, windDirection, windVelocity, city, serviceDataCurrentDate);
		expect(entityManagerMock.createNamedQuery("ServiceDataCurrent.findByDate",
					ServiceDataCurrent.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.setParameter("city", city)).andReturn(null);
		expect(queryMock.getSingleResult()).andReturn(serviceDataCurrent );
		replay(entityManagerMock, queryMock);
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader();
		instance.setEntityManager(entityManagerMock);
		ServiceWeather expResult = new ServiceWeather(timestamp, city, relativeHumidity, 20.0f, 20.0f, windDirection, windVelocity);
		ServiceWeather result = instance.loadCurrentServiceWeatherData(timestamp, city);
		assertEquals(expResult, result);
	}

	/**
	 * Test of loadForecastServiceWeatherData method, of class DatabaseWeatherLoader.
	 */
	@Test
	public void testLoadForecastServiceWeatherData() throws Exception {
		System.out.println("loadForecastServiceWeatherData");
		long timestamp = System.currentTimeMillis();
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
		City city = new City("test_city", 200000, 100, true, true, referenceArea);
		EntityManager entityManagerMock = createMock(EntityManager.class);
		TypedQuery queryMock = createStrictMock(TypedQuery.class);
		Date serviceDataCurrentDate = new Date(timestamp);
		int airPressure = 1, lightIntensity= 1, radiation = 2;
		float perceivedTemperature = 1.0f, temperature = 1.0f, precipitationAmout = 2.0f, relativeHumidity =2.0f, windVelocity=1.7f, windDirection = 12;
		ServiceDataForecast serviceDataCurrent = new ServiceDataForecast(20.f, 20.0f, relativeHumidity, windDirection, windVelocity, city, new Date(timestamp));
		expect(entityManagerMock.createNamedQuery("ServiceDataForecast.findByDate",
					ServiceDataForecast.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.setParameter("city", city)).andReturn(null);
		expect(queryMock.getSingleResult()).andReturn(serviceDataCurrent );
		replay(entityManagerMock, queryMock);
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader();
		instance.setEntityManager(entityManagerMock);
		ServiceWeather expResult = new ServiceWeather(timestamp, city, relativeHumidity, 20.0f, 20.0f, windDirection, windVelocity);
		ServiceWeather result = instance.loadForecastServiceWeatherData(timestamp, city);
		assertEquals(expResult, result);
	}

	/**
	 * Test of loadStationData method, of class DatabaseWeatherLoader.
	 */
	@Test
	public void testLoadStationData() throws Exception {
		System.out.println("loadStationData");
		long timestamp = System.currentTimeMillis();
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
		City city = new City("test_city", 200000, 100, true, true, referenceArea);
		EntityManager entityManagerMock = createMock(EntityManager.class);
		DatabaseWeatherLoader instance = new DatabaseWeatherLoader();
		instance.setEntityManager(entityManagerMock);
		TypedQuery queryMock = createStrictMock(TypedQuery.class);
		Date serviceDataCurrentDate = new Date(timestamp);
		int airPressure = 1, lightIntensity= 1, radiation = 2, windDirection = 12;
		float perceivedTemperature = 1.0f, temperature = 1.0f, precipitationAmout = 2.0f, relativeHumidity =2.0f, windVelocity=1.7f;
		StationData serviceDataCurrent = new StationDataNormal(new Date(timestamp), new Time(timestamp), airPressure, lightIntensity, perceivedTemperature, temperature, precipitationAmout, radiation, relativeHumidity, windDirection, windVelocity);
		expect(entityManagerMock.createNamedQuery("StationDataNormal.findByDate",
					StationDataNormal.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.getResultList()).andReturn(new ArrayList(Arrays.asList(serviceDataCurrent)));
		expect(entityManagerMock.createNamedQuery("StationDataNormal.findLastEntryByDate",
					StationDataNormal.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.setMaxResults(1)).andReturn(null);
		expect(queryMock.getSingleResult()).andReturn(serviceDataCurrent );
		expect(entityManagerMock.createNamedQuery("StationDataNormal.findFirstEntryByDate",
					StationDataNormal.class)).andReturn(queryMock);
		expect(queryMock.setParameter(eq("date"), anyObject(Date.class))).andReturn(null);
		expect(queryMock.setMaxResults(1)).andReturn(null);
		expect(queryMock.getSingleResult()).andReturn(serviceDataCurrent );
		replay(entityManagerMock, queryMock);
		WeatherMap expResult = new StationDataMap();
		expResult.put(timestamp, new Weather(timestamp, airPressure, lightIntensity, perceivedTemperature, precipitationAmout, radiation, relativeHumidity, temperature, windDirection, windVelocity));
		WeatherMap result = instance.loadStationData(timestamp);
		assertEquals(expResult.keySet().size(), result.keySet().size());
		outer:
		for(Long key : result.keySet()) {
			for(Long key0 : expResult.keySet()) {
				if(Math.abs(key-key0) < 3600000) {
					continue outer;
				}
			}
//			fail("key "+key+" is not contained in result");
		}
		outer:
		for(Weather weather : result.values()) {
			Collection<Weather> candidates = new LinkedList<>();
			for(Weather weather0 : expResult.values()) {
				if(Math.abs(weather.getTimestamp()-weather0.getTimestamp()) < 3600000) {
					candidates.add(weather0);
				}
			}
			boolean candidatePassed = false;
			for(Weather candidate : candidates) {
				if(candidate.equalsIgnoreTimestamp(weather)) {
					continue outer;
				}
			}
//			fail("..");
		}
	}
}