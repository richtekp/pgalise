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
 
package de.pgalise.util.weathercollector;

import de.pgalise.testutils.TestUtils;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.weather.model.WeatherCondition;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.util.weathercollector.util.JTADatabaseManager;
import org.junit.Test;

import de.pgalise.util.weathercollector.weatherservice.DefaultWeatherServiceManager;
import de.pgalise.util.weathercollector.weatherservice.strategy.YahooWeather;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.InitialContext;
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
import org.junit.BeforeClass;

/**
 * Tests the weather service manager. Doesn't inject BaseDatabaseManager in 
 * because the injected test EntityManager factory can be passed as parameter 
 * in the constructor.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalClient
@ManagedBean
public class DefaultWeatherServiceManagerTest {
	private static EJBContainer CONTAINER;
	@PersistenceContext(unitName = "pgalise")
	private EntityManager entityManager;
	private BaseDatabaseManager<DefaultServiceDataHelper> baseDatabaseManager;

	@SuppressWarnings("LeakingThisInConstructor")
	public DefaultWeatherServiceManagerTest() throws NamingException {
		CONTAINER.getContext().bind("inject",
			this);
		this.baseDatabaseManager = new JTADatabaseManager(
		entityManager);
	}
	
	@BeforeClass
	public static void setUpClass() throws NamingException {
		CONTAINER = TestUtils.getContainer();
	}

	@Test
	public void testSaveInformations() throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, NamingException {
		Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys = new HashSet<ServiceStrategy<DefaultServiceDataHelper>>(Arrays.asList(new YahooWeather()));
		DefaultWeatherServiceManager instance = new DefaultWeatherServiceManager(baseDatabaseManager, serviceStrategys);
		
		InitialContext initialContext = new InitialContext();
		UserTransaction userTransaction = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
		userTransaction.begin();
		entityManager.joinTransaction();
		
		City city = TestUtils.createDefaultTestCityInstance();
		entityManager.persist(city);
		instance.saveInformations(baseDatabaseManager);
		Query queryCurrent = entityManager.createQuery("SELECT x FROM DefaultServiceDataCurrent x");
		Query queryForecast = entityManager.createQuery("SELECT y FROM DefaultServiceDataForecast y");
		assertFalse(queryCurrent.getResultList().isEmpty());
		assertFalse(queryForecast.getResultList().isEmpty());
		userTransaction.commit();
	}

}
