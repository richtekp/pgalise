/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.weatherservice.strategy;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.testutils.TestUtils;
import de.pgalise.util.weathercollector.util.DatabaseManager;
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
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class MSNWeatherTest {

  @PersistenceContext(unitName = "pgalise-weathercollector")
  private EntityManager entityManager;
  @EJB
  private DatabaseManager baseDatabaseManager;
  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;

  public MSNWeatherTest() throws NamingException {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
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
      entityManager.merge(city.getPosition());
      entityManager.merge(city);
      MSNWeather instance = new MSNWeather();
      ServiceDataHelper result = instance.getWeather(city,
        baseDatabaseManager);
      assertFalse(result.getForecastConditions().isEmpty());
    } finally {
      userTransaction.commit();
    }
  }

}
