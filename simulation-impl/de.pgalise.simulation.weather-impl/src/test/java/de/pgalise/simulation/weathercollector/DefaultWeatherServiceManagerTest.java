/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.weathercollector;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.simulation.weathercollector.weatherservice.WeatherServiceManager;
import de.pgalise.simulation.weathercollector.weatherservice.strategy.YahooWeather;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the weather service manager. Doesn't inject DatabaseManager in because
 * the injected test EntityManager factory can be passed as parameter in the
 * constructor.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalClient
@ManagedBean
public class DefaultWeatherServiceManagerTest {

  @PersistenceContext(unitName = "pgalise-weather")
  private EntityManager entityManager;
  @Resource
  private UserTransaction userTransaction;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private WeatherServiceManager instance;
  @EJB
  private DatabaseManager databaseManager;

  public DefaultWeatherServiceManagerTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  @Test
  public void testSaveInformations() throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, NamingException {
    userTransaction.begin();
    try {
      Set<ServiceStrategy> serviceStrategys = new HashSet<ServiceStrategy>(
        Arrays.asList(new YahooWeather(idGenerator,
            databaseManager)));

      City city = TestUtils.createDefaultTestCityInstance(idGenerator);
      entityManager.merge(city);
      instance.saveInformations();
      Query queryCurrent = entityManager.createQuery(
        "SELECT x FROM ServiceDataCurrent x");
      Query queryForecast = entityManager.createQuery(
        "SELECT y FROM ServiceDataForecast y");
      assertFalse(queryCurrent.getResultList().isEmpty());
      assertFalse(queryForecast.getResultList().isEmpty());
    } finally {
      userTransaction.commit();
    }
  }

}
