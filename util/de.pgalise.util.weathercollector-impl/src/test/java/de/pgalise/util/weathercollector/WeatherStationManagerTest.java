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

import de.pgalise.util.weathercollector.model.City;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import org.junit.Test;

import de.pgalise.util.weathercollector.weatherstation.WeatherStationManager;
import de.pgalise.util.weathercollector.model.ServiceDataCurrent;
import de.pgalise.util.weathercollector.util.NonJTADatabaseManager;
import de.pgalise.util.weathercollector.weatherstation.StationStrategy;
import de.pgalise.util.weathercollector.weatherstation.strategy.StationOldenburg;
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
import org.apache.openejb.api.LocalClient;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Tests the weather station manager
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalClient
@ManagedBean
public class WeatherStationManagerTest {
	private final static EJBContainer CONTAINER = TestUtils.createContainer();
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("weather_data_test");

	public WeatherStationManagerTest() {
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
	public void testSaveInformations() {
		BaseDatabaseManager baseDatabaseManager = NonJTADatabaseManager.getInstance(
			entityManagerFactory);
		Set<StationStrategy> serviceStrategys = new HashSet<StationStrategy>(Arrays.asList(new StationOldenburg()));
		WeatherStationManager instance = new WeatherStationManager(
			baseDatabaseManager);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		City city = new City("Berlin",
			3375222,
			34,
			true,
			false);
		entityManager.getTransaction().begin();
		entityManager.persist(city);
		entityManager.getTransaction().commit();
		instance.saveInformations();
		Query query = entityManager.createQuery(String.format("SELECT x FROM %s x", ServiceDataCurrent.class.getSimpleName()));
		assertFalse(query.getResultList().isEmpty());
		entityManager.close();
	}
}
