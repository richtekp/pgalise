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
 
package de.pgalise.simulation.weather.internal.modifier.jfreechart;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.it.TestUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jfree.data.time.TimeSeries;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.internal.DefaultRandomSeedService;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import org.junit.BeforeClass;

/**
 * Abstract super class for chart tests. Theses tests are used to test the weather decorators.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
public abstract class AbstractChartTest {
	private static EJBContainer container;

	/**
	 * Title for decorator dataset
	 */
	public static final String DECORATOR_TITLE = "Decorator";

	/**
	 * Title for reference dataset
	 */
	public static final String REFERENCE_TITLE = "Reference";

	/**
	 * File path for property file
	 */
	private static final String PROPERTIES_FILE_PATH = "/weather_decorators.properties";

	/**
	 * End timestamp
	 */
	private long endTimestamp;

	/**
	 * Parameter
	 */
	private WeatherParameterEnum parameter;

	/**
	 * Properties
	 */
	private Properties props;

	/**
	 * RandomSeedService
	 */
	private RandomSeedService random;

	/**
	 * Service Class
	 */
	private DefaultWeatherService service;

	/**
	 * Start timestamp
	 */
	private long startTimestamp;

	/**
	 * Weather Loader
	 */
	private WeatherLoader<DefaultWeatherCondition> loader;

	/**
	 * Constructor
	 * 
	 * @param startdate
	 *            Date as timestamp
	 * @param enddate
	 *            Date as timestamp
	 * @param parameter
	 *            WeatherParameterEnum
	 * @throws IOException
	 * @throws NamingException
	 */
	public AbstractChartTest(long startdate, long enddate, WeatherParameterEnum parameter)
			throws IOException, NamingException {
		this.random = new DefaultRandomSeedService();
		this.startTimestamp = startdate;
		this.endTimestamp = enddate;
		this.parameter = parameter;

		// Read props
		try {
			InputStream propInFile = AbstractChartTest.class
					.getResourceAsStream(AbstractChartTest.PROPERTIES_FILE_PATH);
			this.props = new Properties();
			this.props.loadFromXML(propInFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Context ctx = container.getContext();

		// City
		City city = TestUtils.createDefaultTestCityInstance();

		// Load EJB for Weather loader
		this.loader = (WeatherLoader<DefaultWeatherCondition>) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Create service
		this.service = new DefaultWeatherService(city, this.loader);

		// Get reference weather informations
		this.service.addNewWeather(this.startTimestamp, this.endTimestamp, true, null);

	}
	
	@BeforeClass
	public static void setUpClass() {
		container = TestUtils.getContainer();
	}

	public WeatherLoader<DefaultWeatherCondition> getLoader() {
		return this.loader;
	}

	/**
	 * Get Timeserie for decorator values
	 * 
	 * @return Timeserie
	 * @throws Exception
	 */
	protected abstract TimeSeries getDecoratorTimeSerie() throws Exception;

	/**
	 * Get Timeserie for reference values
	 * 
	 * @return Timeserie
	 */
	protected abstract TimeSeries getReferenceTimeSerie();

	/**
	 * @return the random
	 */
	public RandomSeedService getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(RandomSeedService random) {
		this.random = random;
	}

	/**
	 * @return the service
	 */
	public DefaultWeatherService getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(
		DefaultWeatherService service) {
		this.service = service;
	}

	/**
	 * @return the startTimestamp
	 */
	public long getStartTimestamp() {
		return startTimestamp;
	}

	/**
	 * @param startTimestamp the startTimestamp to set
	 */
	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	/**
	 * @param loader the loader to set
	 */
	public void setLoader(
		WeatherLoader<DefaultWeatherCondition> loader) {
		this.loader = loader;
	}

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * @return the parameter
	 */
	public WeatherParameterEnum getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(
		WeatherParameterEnum parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the endTimestamp
	 */
	public long getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * @param endTimestamp the endTimestamp to set
	 */
	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
}
