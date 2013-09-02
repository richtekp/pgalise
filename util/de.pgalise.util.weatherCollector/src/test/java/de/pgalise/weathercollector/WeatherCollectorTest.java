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
 
package de.pgalise.weathercollector;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.pgalise.weathercollector.app.WeatherCollector;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.apache.openejb.api.LocalClient;

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

	public WeatherCollectorTest() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
		InitialContext initialContext = new InitialContext(p);
		initialContext.bind("inject", this);
	}

	@Test
	public void testCollectServiceData() {
		WeatherCollector weatherCollector = new WeatherCollector();
		weatherCollector.collectServiceData(entityManagerFactory);
	}

	@Test
	public void testCollectStationData() {
		WeatherCollector weatherCollector = new WeatherCollector();
		weatherCollector.collectStationData(entityManagerFactory);
	}

}
