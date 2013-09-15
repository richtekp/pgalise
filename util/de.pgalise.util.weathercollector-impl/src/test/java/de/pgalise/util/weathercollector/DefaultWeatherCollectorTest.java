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

import de.pgalise.it.TestUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
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
import javax.ejb.embeddable.EJBContainer;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.junit.BeforeClass;

/**
 * Tests the weather collector
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalClient
@ManagedBean
public class DefaultWeatherCollectorTest {
	private static EJBContainer CONTAINER;
	@PersistenceUnit(unitName = "weather_collector_test")
	private EntityManagerFactory entityManagerFactory;

	@SuppressWarnings("LeakingThisInConstructor")
	public DefaultWeatherCollectorTest() throws NamingException {
		CONTAINER.getContext().bind("inject",
			this);
	}
	
	@BeforeClass
	public static void setUpClass() {
		CONTAINER = TestUtils.getContainer();
	}
	
	@Test
	public void testCollectServiceData() {
		DefaultWeatherCollector weatherCollector = new DefaultWeatherCollector();
		BaseDatabaseManager<DefaultServiceDataHelper> baseDatabaseManager = NonJTADatabaseManager.getInstance(entityManagerFactory);
		Set<ServiceStrategy<DefaultServiceDataHelper>> serviceStrategys = new HashSet<ServiceStrategy<DefaultServiceDataHelper>>(Arrays.asList(new YahooWeather()));
		EntityManager entityManager = entityManagerFactory.createEntityManager();
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
		entityManager.persist(city);
		weatherCollector.collectServiceData(baseDatabaseManager, serviceStrategys);
		Query query = entityManager.createQuery("SELECT x FROM DefExtendedServiceDataCurrent x");
		assertFalse(query.getResultList().isEmpty());
		entityManager.close();
	}

	@Test
	public void testCollectStationData() throws NamingException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		DefaultWeatherCollector weatherCollector = new DefaultWeatherCollector();
		BaseDatabaseManager<DefaultServiceDataHelper> baseDatabaseManager = JTADatabaseManager.getInstance(entityManagerFactory);
		StationStrategy stationStrategy = createMock(StationStrategy.class);
		stationStrategy.saveWeather(anyObject(WeatherStationSaver.class));
		expectLastCall().atLeastOnce();
		InitialContext initialContext = new InitialContext();
		UserTransaction userTransaction = (UserTransaction) initialContext.lookup("java:comp/UserTransaction");
		userTransaction.begin();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
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
		entityManager.joinTransaction();
		entityManager.persist(city);
		userTransaction.commit();
		weatherCollector.collectStationData(baseDatabaseManager, new HashSet<>(Arrays.asList(stationStrategy)));
		//nothing to test (depends all on functionality of station)
		entityManager.close();
	}

}
