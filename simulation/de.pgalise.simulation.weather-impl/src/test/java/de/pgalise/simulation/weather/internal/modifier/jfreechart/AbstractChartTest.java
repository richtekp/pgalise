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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
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
import de.pgalise.simulation.shared.controller.Controller;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.internal.service.DefaultWeatherService;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import org.junit.AfterClass;

/**
 * Abstract super class for chart tests. Theses tests are used to test the weather decorators.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
public abstract class AbstractChartTest {
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
	private final static EJBContainer container;
	static {
		try {
			// Load EJB properties
			Properties prop = new Properties();
			prop.load(Controller.class.getResourceAsStream("/META-INF/jndi.properties"));
			container = EJBContainer.createEJBContainer(prop);
		} catch (IOException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

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
	protected long endTimestamp;

	/**
	 * Parameter
	 */
	protected WeatherParameterEnum parameter;

	/**
	 * Properties
	 */
	protected Properties props;

	/**
	 * RandomSeedService
	 */
	protected RandomSeedService random;

	/**
	 * Service Class
	 */
	protected DefaultWeatherService service;

	/**
	 * Start timestamp
	 */
	protected long startTimestamp;

	/**
	 * Weather Loader
	 */
	private WeatherLoader loader;

	/**
	 * Constructor
	 * 
	 * @param startdate
	 *            Date as timestamp
	 * @param enddate
	 *            Date as timestamp
	 * @param parameter
	 *            WeatherParameterEnum
	 * @throws NoWeatherDataFoundException
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
		Coordinate referencePoint = new Coordinate(20, 20);
		Polygon referenceArea = GEOMETRY_FACTORY.createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1)
			}
		);
		City city = new City("test_city", 200000, 100, true, true, referenceArea);

		// Load EJB for Weather loader
		this.loader = (WeatherLoader) ctx
				.lookup("java:global/de.pgalise.simulation.weather-impl/de.pgalise.simulation.weather.dataloader.WeatherLoader");

		// Create service
		this.service = new DefaultWeatherService(city, this.loader);

		// Get reference weather informations
		this.service.addNewWeather(this.startTimestamp, this.endTimestamp, true, null);

	}
	
	@AfterClass
	public static void tearDownClass() {
		container.close();
	}

	public WeatherLoader getLoader() {
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
}
