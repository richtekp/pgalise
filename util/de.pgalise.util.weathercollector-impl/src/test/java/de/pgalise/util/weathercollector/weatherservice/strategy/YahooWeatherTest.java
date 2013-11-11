/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.it.TestUtils;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import de.pgalise.util.weathercollector.util.JTADatabaseManager;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import javax.annotation.ManagedBean;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.openejb.api.LocalClient;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class YahooWeatherTest {
	private static EJBContainer CONTAINER;
	@PersistenceContext(unitName = "weather_test")
	private EntityManager entityManager;
	private BaseDatabaseManager<DefaultServiceDataHelper,DefaultWeatherCondition> baseDatabaseManager;
	
	@SuppressWarnings("LeakingThisInConstructor")
	public YahooWeatherTest() throws NamingException {
		CONTAINER.getContext().bind("inject",
			this);
		this.baseDatabaseManager = new JTADatabaseManager(
		entityManager);
	}
	
	@BeforeClass
	public static void setUpClass() throws NamingException {
		CONTAINER = TestUtils.getContainer();
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
		
		City city = TestUtils.createDefaultTestCityInstance();
		YahooWeather instance = new YahooWeather();
		ServiceDataHelper<?,?,?> result = instance.getWeather(city,
			baseDatabaseManager);
		assertFalse(result.getForecastConditions().isEmpty());
		assertFalse(result.getCurrentCondition() == null);
	}

}