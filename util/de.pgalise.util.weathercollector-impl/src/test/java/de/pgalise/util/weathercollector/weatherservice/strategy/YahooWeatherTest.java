/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.testutils.TestUtils;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class YahooWeatherTest {

	private static EJBContainer CONTAINER;
	@PersistenceContext(unitName = "pgalise-weathercollector")
	private EntityManager entityManager;
	@EJB
	private DatabaseManager baseDatabaseManager;
	@Resource
	private UserTransaction userTransaction;

	public YahooWeatherTest() throws NamingException {
	}

	@Before
	public void setUp() throws NamingException {
		TestUtils.getContainer().getContext().bind("inject",
			this);
	}

	/**
	 * Test of getWeather method, of class YahooWeather. This connects to the
	 * original Yahoo service in order to let the test fail if something changes
	 * in API.
	 *
	 * @throws ReadServiceDataException
	 * @throws javax.transaction.NotSupportedException
	 * @throws javax.transaction.SystemException
	 * @throws javax.transaction.RollbackException
	 * @throws javax.transaction.HeuristicMixedException
	 * @throws javax.transaction.HeuristicRollbackException
	 */
	@Test
	public void testGetWeather() throws ReadServiceDataException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		userTransaction.begin();
		try {
			City city = TestUtils.createDefaultTestCityInstance();
			YahooWeather instance = new YahooWeather();
			ServiceDataHelper result = instance.getWeather(city,
				baseDatabaseManager);
			assertFalse(result.getForecastConditions().isEmpty());
			assertFalse(result.getCurrentCondition() == null);
		} finally {
			userTransaction.commit();
		}
	}

}
