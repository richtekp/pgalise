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
import java.util.Properties;
import javax.annotation.ManagedBean;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.openejb.api.LocalClient;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class YahooWeatherTest {
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("weather_data_test");
	
	public YahooWeatherTest() {
	}

	@Before
	public void setUp() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		p.put("openejb.tempclassloader.skip", "annotations");
		CONTAINER.getContext().bind("inject",
			this);
	}

	/**
	 * Test of getWeather method, of class YahooWeather. This connects to the 
	 * original Yahoo service in order to let the test fail if something changes 
	 * in API.
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
		YahooWeather instance = new YahooWeather();
		ServiceDataHelper result = instance.getWeather(city,
			databaseManager);
		assertFalse(result.getForecastConditions().isEmpty());
	}
}