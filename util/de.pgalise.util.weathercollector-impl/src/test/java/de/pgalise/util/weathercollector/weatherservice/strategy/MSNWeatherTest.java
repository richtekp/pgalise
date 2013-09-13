/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.it.TestUtils;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
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
public class MSNWeatherTest {
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	private EntityManagerFactory entityManagerFactory = TestUtils.createEntityManagerFactory("weather_data_test");
	
	public MSNWeatherTest() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		p.put("openejb.tempclassloader.skip", "annotations");
		CONTAINER.getContext().bind("inject",
			this);
	}

	@Before
	public void setUp() throws NamingException {
	}

	/**
	 * Test of extractWeather method, of class MSNWeather.
	 * 
	 * @throws ReadServiceDataException 
	 */
	@Test
	public void testgetWeather() throws ReadServiceDataException {
		Polygon referenceArea = GeotoolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(new Coordinate[] {
			new Coordinate(1,
			1),
			new Coordinate(1,
			2),
			new Coordinate(2,
			2),
			new Coordinate(2,
			1),
			new Coordinate(1,
			1)
		});
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			referenceArea);
		DatabaseManager databaseManager = JTADatabaseManager.getInstance(entityManagerFactory);
		MSNWeather instance = new MSNWeather();
		DefaultServiceDataHelper result = instance.getWeather(city,
			databaseManager);
		assertFalse(result.getForecastConditions().isEmpty());
	}
}