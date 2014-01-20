/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import de.pgalise.simulation.weathercollector.weatherservice.strategy.MSNWeather;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
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
@LocalBean
@LocalClient
@ManagedBean
@Ignore
public class MSNWeatherTest {

  @PersistenceContext(unitName = "pgalise-weathercollector")
  private EntityManager entityManager;
  @EJB
  private DatabaseManager baseDatabaseManager;
  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;

  public MSNWeatherTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().bind("inject",
      this);
  }

  /**
   * Test of extractWeather method, of class MSNWeather.
   *
   * @throws Exception
   */
  @Test
  public void testGetWeather() throws Exception {
    userTransaction.begin();
    try {
      City city = TestUtils.createDefaultTestCityInstance(idGenerator);
      entityManager.merge(city.getGeoInfo());
      entityManager.merge(city);
      MSNWeather instance = new MSNWeather(idGenerator,
        baseDatabaseManager);
      ServiceDataHelper result = instance.getWeather(city);
      assertFalse(result.getForecastConditions().isEmpty());
    } finally {
      userTransaction.commit();
    }
  }

}
