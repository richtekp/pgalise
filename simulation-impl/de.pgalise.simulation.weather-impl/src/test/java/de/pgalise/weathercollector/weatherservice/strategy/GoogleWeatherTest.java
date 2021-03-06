/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.weatherservice.strategy;

import de.pgalise.simulation.weathercollector.weatherservice.strategy.GoogleWeather;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManager;
  @EJB
  private DatabaseManager baseDatabaseManager;
  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;

  @SuppressWarnings("LeakingThisInConstructor")
  public GoogleWeatherTest() throws NamingException {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
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
      City city = TestUtils.createDefaultTestCityInstance(idGenerator);
      entityManager.merge(city);
      GoogleWeather instance = new GoogleWeather(idGenerator,
        baseDatabaseManager);
      ServiceDataHelper result = instance.getWeather(city);
      assertFalse(result.getForecastConditions().isEmpty());
    } finally {
      userTransaction.commit();
    }
  }

}
