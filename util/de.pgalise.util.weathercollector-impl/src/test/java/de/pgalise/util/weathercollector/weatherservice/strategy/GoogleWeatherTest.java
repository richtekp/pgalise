/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author richter
 */
/*
 ignored because Google weather API no longer is accessible
 */
@LocalClient
@ManagedBean
@Ignore
public class GoogleWeatherTest {

	@PersistenceContext(unitName = "pgalise-weathercollector")
	private EntityManager entityManager;
	@EJB
	private DatabaseManager baseDatabaseManager;
	@Resource
	private UserTransaction userTransaction;

	@SuppressWarnings("LeakingThisInConstructor")
	public GoogleWeatherTest() throws NamingException {
	}

	@Before
	public void setUp() throws NamingException {
		TestUtils.getContainer().getContext().bind("inject",
			this);
	}

	/**
	 * Test of extractWeather method, of class GoogleWeather.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetWeather() throws Exception {
		userTransaction.begin();
		try {
			City city = TestUtils.createDefaultTestCityInstance();
			entityManager.merge(city.getPosition());
			entityManager.merge(city);
			GoogleWeather instance = new GoogleWeather();
			ServiceDataHelper result = instance.getWeather(city,
				baseDatabaseManager);
			assertFalse(result.getForecastConditions().isEmpty());
		} finally {
			userTransaction.commit();
		}
	}

}
