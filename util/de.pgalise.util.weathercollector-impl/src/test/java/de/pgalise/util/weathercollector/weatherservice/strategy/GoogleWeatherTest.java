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
import de.pgalise.util.weathercollector.util.JTADatabaseManager;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import javax.annotation.ManagedBean;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.apache.openejb.api.LocalClient;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
@Ignore
public class GoogleWeatherTest {
	private static EJBContainer CONTAINER;
	@PersistenceUnit(unitName = "weather_collector_test")
	private EntityManagerFactory entityManager;
	private BaseDatabaseManager<DefaultServiceDataHelper> baseDatabaseManager;
	
	@SuppressWarnings("LeakingThisInConstructor")
	public GoogleWeatherTest() throws NamingException {
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
	 * Test of extractWeather method, of class GoogleWeather.
	 * 
	 * @throws ReadServiceDataException 
	 */
	@Test
	public void testGetWeather() throws ReadServiceDataException {
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
		GoogleWeather instance = new GoogleWeather();
		ServiceDataHelper<?,?> result = instance.getWeather(city,
			baseDatabaseManager);
		assertFalse(result.getForecastConditions().isEmpty());
	}

}