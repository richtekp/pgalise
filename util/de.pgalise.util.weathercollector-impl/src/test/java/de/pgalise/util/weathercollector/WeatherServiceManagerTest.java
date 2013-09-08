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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeotoolsBootstrapping;
import de.pgalise.util.weathercollector.model.ServiceDataCurrent;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.util.weathercollector.util.NonJTADatabaseManager;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import org.junit.Test;

import de.pgalise.util.weathercollector.weatherservice.WeatherServiceManager;
import de.pgalise.util.weathercollector.weatherservice.strategy.YahooWeather;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.assertFalse;
import org.junit.Before;

/**
 * Tests the weather service manager
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalClient
@ManagedBean
public class WeatherServiceManagerTest {
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	private EntityManagerFactory entityManagerFactory = TestUtils.createEntityManagerFactory("weather_data_test");

	public WeatherServiceManagerTest() {
	}
	
	@Before
	public void setUp() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		p.put("openejb.tempclassloader.skip", "annotations");
		CONTAINER.getContext().bind("inject",
			this);
	}

	@Test
	public void testSaveInformations() throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, NamingException {
		BaseDatabaseManager baseDatabaseManager = NonJTADatabaseManager.getInstance(
			entityManagerFactory);
		Set<ServiceStrategy> serviceStrategys = new HashSet<ServiceStrategy>(Arrays.asList(new YahooWeather()));
		WeatherServiceManager instance = new WeatherServiceManager(baseDatabaseManager, serviceStrategys);
		
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
		entityManager.getTransaction().begin();
		entityManager.persist(city);
		entityManager.getTransaction().commit();
		instance.saveInformations(baseDatabaseManager);
		Query query = entityManager.createQuery(String.format("SELECT x FROM %s x", ServiceDataCurrent.class.getSimpleName()));
		assertFalse(query.getResultList().isEmpty());
		entityManager.close();
	}

}
