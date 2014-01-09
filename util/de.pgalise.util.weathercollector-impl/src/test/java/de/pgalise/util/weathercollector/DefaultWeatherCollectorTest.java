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
import de.pgalise.simulation.shared.city.City;
import org.junit.Test;

import de.pgalise.util.weathercollector.app.DefaultWeatherCollector;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.util.weathercollector.util.JTADatabaseManager;
import de.pgalise.util.weathercollector.weatherstation.WeatherStationSaver;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.util.weathercollector.weatherservice.strategy.YahooWeather;
import de.pgalise.util.weathercollector.weatherstation.StationStrategy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.LocalBean;
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
import org.easymock.EasyMock;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.junit.Before;

/**
 * Tests the weather collector. Doesn't inject BaseDatabaseManager in because
 * the injected test EntityManager factory can be passed as parameter in the
 * constructor.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalBean
@ManagedBean
public class DefaultWeatherCollectorTest {

	private EJBContainer CONTAINER;
	@PersistenceContext(unitName = "pgalise")
	private EntityManager entityManager;
	private BaseDatabaseManager<DefaultServiceDataHelper> baseDatabaseManager;

	public DefaultWeatherCollectorTest() {
		this.baseDatabaseManager = new JTADatabaseManager(entityManager
		);
	}

	@Before
	public void setUpClass() throws NamingException {
		CONTAINER = TestUtils.getContainer();
		CONTAINER.getContext().bind("inject",
			this);
	}

	@Test
	public void testCollectServiceData() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		DefaultWeatherCollector weatherCollector = new DefaultWeatherCollector();
		Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys = new HashSet<ServiceStrategy<DefaultServiceDataHelper>>(
			Arrays.asList(new YahooWeather()));
		InitialContext initialContext = new InitialContext();
		UserTransaction userTransaction = (UserTransaction) initialContext.lookup(
			"java:comp/UserTransaction");
		userTransaction.begin();

		City city = TestUtils.createDefaultTestCityInstance();
		entityManager.persist(city);
//		CONTAINER.getContext().unbind("inject");
//		baseDatabaseManager = lookupJTADatabaseManagerBean();
		weatherCollector.collectServiceData(baseDatabaseManager,
			serviceStrategys);
		Query query = entityManager.createQuery(
			"SELECT x FROM DefaultServiceDataCurrent x");
		assertFalse(query.getResultList().isEmpty());
		userTransaction.commit();
	}

	@Test
	public void testCollectStationData() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		DefaultWeatherCollector weatherCollector = new DefaultWeatherCollector();
		StationStrategy stationStrategy = createMock(StationStrategy.class);
		stationStrategy.saveWeather(anyObject(WeatherStationSaver.class));
		expectLastCall().once();
		EasyMock.replay(stationStrategy);
		InitialContext initialContext = new InitialContext();
		UserTransaction userTransaction = (UserTransaction) initialContext.lookup(
			"java:comp/UserTransaction");
		userTransaction.begin();
		City city = TestUtils.createDefaultTestCityInstance();
		entityManager.persist(city);
		userTransaction.commit();
		weatherCollector.collectStationData(baseDatabaseManager,
			new HashSet<>(Arrays.asList(stationStrategy)));
		//only test that StationStrategy.saveWeather is invoked
	}

}
