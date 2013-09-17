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
 
package de.pgalise.util.weathercollector.weatherservice.strategy;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.MyExtendedServiceDataCurrent;
import de.pgalise.util.weathercollector.model.MyExtendedServiceDataForecast;
import de.pgalise.util.weathercollector.model.DefaultServiceDataHelper;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.pgalise.util.weathercollector.util.DatabaseManager;
import java.sql.Timestamp;
import javax.measure.Measure;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns weather informations from Google. Uses the strategy pattern.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public class GoogleWeather extends XMLAPIWeather {
	private final static Logger LOGGER = LoggerFactory.getLogger(GoogleWeather.class);

	/**
	 * Constructor
	 */
	public GoogleWeather() {
		super("http://www.google.com/ig/api?h1=en&weather=", "Google", NonSI.FAHRENHEIT);
	}

	@Override
	protected Document fetchWeatherData(String city) throws ReadServiceDataException {
		throw new UnsupportedOperationException("Google Weather API is shut down!");
	}

	@Override
	protected DefaultServiceDataHelper extractWeather(City city, Document doc, DatabaseManager entityManagerFactory) {
		DefaultServiceDataHelper weather = new DefaultServiceDataHelper(city, this.getApiname());

		// Read general informations
		NodeList nodes = doc.getElementsByTagName("forecast_information");
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childnodes = nodes.item(i).getChildNodes();
			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				// City
				if ("city".equals(childnodes.item(j).getNodeName())) {
					weather.setApicity(dataString);
				} else if ("forecast_date".equals(childnodes.item(j).getNodeName())) {
					try {
						// Date
						Timestamp convertedTimestamp = DateConverter.convertTimestamp(dataString, "yyyy-MM-dd");
						weather.setMeasureTime(new Time(convertedTimestamp.getTime()));
						weather.setMeasureDate(new Date(convertedTimestamp.getTime()));
					} catch (ParseException e) {
						LOGGER.warn("see nested exception",
							e);
					}
				}
			}

		}

		// CurrentCondition
		nodes = doc.getElementsByTagName("current_conditions");
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childnodes = nodes.item(i).getChildNodes();

			// Date
			MyExtendedServiceDataCurrent condition = new MyExtendedServiceDataCurrent(new Date(weather.getMeasureTime().getTime()),
					new Time(weather.getMeasureTime().getTime()), city,
				null, 1.0f,
				Float.NaN,
				Float.NaN,
				Float.MAX_VALUE,
				null, new Time(1), new Time(2));

			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				// Temperature
				if ("temp_c".equals(childnodes.item(j).getNodeName())) {
					condition.setTemperature(Measure.valueOf(Float.parseFloat(dataString), SI.CELSIUS));
				} else if ("humidity".equals(childnodes.item(j).getNodeName())) {
					// Relativ humidity
					String[] segs = dataString.split(Pattern.quote(":"));
					condition.setRelativHumidity(Float.parseFloat(segs[1].substring(0, (segs[1].length() - 1))));
				} else if ("condition".equals(childnodes.item(j).getNodeName())) {
					// Condition
					condition.setCondition(DefaultWeatherCondition.retrieveCondition(Integer.parseInt(dataString)));
				}
			}

			// City
			condition.setCity(city);

			// Save current weather
			weather.setCurrentCondition(condition);
		}

		// ForecastCondition
		nodes = doc.getElementsByTagName("forecast_conditions");
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childnodes = nodes.item(i).getChildNodes();

			MyExtendedServiceDataForecast condition = new MyExtendedServiceDataForecast(
							new Date(System.currentTimeMillis()), 
							new Time(System.currentTimeMillis()), 
							city, 
							Measure.valueOf(10.0f, SI.CELSIUS),  
							Measure.valueOf(10.0f, SI.CELSIUS),
							1.0f, 1.0f, 10.0f, DefaultWeatherCondition.UNKNOWN_CONDITION);

			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				if ("day_of_week".equals(childnodes.item(j).getNodeName())) {
					// Date
					try {
						condition.setMeasureDate(DateConverter.convertDateFromWeekday(dataString));
					} catch (ParseException e) {
						LOGGER.warn("see nested exception",
							e); // Exception ausgeben
						break;
					}
				} else if ("low".equals(childnodes.item(j).getNodeName())) {
					// Temperature low
					condition.setTemperatureLow(Measure.valueOf(Float.parseFloat(dataString), SI.CELSIUS));
					// condition.setTemperatureLow(WeatherStrategyLib.convertToCelsius(temp));
				} else if ("high".equals(childnodes.item(j).getNodeName())) {
					// Temperature high
					condition.setTemperatureHigh(Measure.valueOf(Float.parseFloat(dataString), SI.CELSIUS));
				} else if ("condition".equals(childnodes.item(j).getNodeName())) {
					// Condition
					condition.setCondition(DefaultWeatherCondition.retrieveCondition(Integer.parseInt(dataString)));
				}
			}

			// City
			condition.setCity(city);

			// Save weather informations
			if (condition.getMeasureDate() != null) {
				weather.getForecastConditions().add(condition);
			}
		}

		// Returns the weather informations
		return weather;
	}
}
