/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.util.weathercollector.TestUtils;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.util.JTADatabaseManager;
import javax.annotation.ManagedBean;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.openejb.api.LocalClient;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
@Ignore
public class GoogleWeatherTest {
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("weather_data_test");
	
	public GoogleWeatherTest() {
	}

	/**
	 * Test of extractWeather method, of class GoogleWeather.
	 * 
	 * @throws ReadServiceDataException 
	 */
	@Test
	public void testGetWeather() throws ReadServiceDataException {
		City city = new City("Berlin",
			3400000,
			80,
			true,
			true,
			new Coordinate(52.516667, 13.4));
		DatabaseManager databaseManager = JTADatabaseManager.getInstance(entityManagerFactory);
		GoogleWeather instance = new GoogleWeather();
		ServiceDataHelper result = instance.getWeather(city,
			databaseManager);
		assertFalse(result.getForecastConditions().isEmpty());
	}
}