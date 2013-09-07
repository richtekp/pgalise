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

import org.junit.Test;

import de.pgalise.util.weathercollector.app.DefaultWeatherCollector;
import de.pgalise.util.weathercollector.util.BaseDatabaseManager;
import de.pgalise.util.weathercollector.util.JTADatabaseManager;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.apache.openejb.api.LocalClient;
import org.junit.AfterClass;
import org.junit.Before;

/**
 * Tests the weather collector
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 14, 2012)
 */
@LocalClient
public class WeatherCollectorTest {
	@PersistenceUnit(unitName = "weather_data_test")
	private EntityManagerFactory entityManagerFactory;
	private final static EJBContainer CONTAINER = TestUtils.createContainer();

	public WeatherCollectorTest() {
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
	public void testCollectServiceData() {
		DefaultWeatherCollector weatherCollector = new DefaultWeatherCollector();
		BaseDatabaseManager baseDatabaseManager = JTADatabaseManager.getInstance(entityManagerFactory);
		weatherCollector.collectServiceData(baseDatabaseManager);
	}

	@Test
	public void testCollectStationData() {
		DefaultWeatherCollector weatherCollector = new DefaultWeatherCollector();
		BaseDatabaseManager baseDatabaseManager = JTADatabaseManager.getInstance(entityManagerFactory);
		weatherCollector.collectStationData(baseDatabaseManager);
	}
	
	@AfterClass
	public static void tearDownClass() {
		CONTAINER.close();
	}

}
