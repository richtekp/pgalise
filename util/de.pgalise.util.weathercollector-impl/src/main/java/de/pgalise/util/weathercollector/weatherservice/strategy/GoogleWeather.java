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
import de.pgalise.simulation.weather.internal.dataloader.entity.DefaultCondition;
import de.pgalise.simulation.weather.model.Condition;
import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.pgalise.util.weathercollector.model.ExtendedServiceDataCurrent;
import de.pgalise.util.weathercollector.model.ExtendedServiceDataForecast;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.Converter;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import javax.measure.Measure;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

/**
 * Returns weather informations from Google. Uses the strategy pattern.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public class GoogleWeather extends XMLAPIWeather {

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
	protected ServiceDataHelper extractWeather(City city, Document doc, DatabaseManager entityManagerFactory) {
		ServiceDataHelper weather = new ServiceDataHelper(city, this.getApiname());

		// Read general informations
		NodeList nodes = doc.getElementsByTagName("forecast_information");
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childnodes = nodes.item(i).getChildNodes();
			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				// City
				if (childnodes.item(j).getNodeName() == "city") {
					weather.setApicity(dataString);
				} else if (childnodes.item(j).getNodeName() == "forecast_date") {
					try {
						// Date
						weather.setMeasureTimestamp(Converter.convertTimestamp(dataString, "yyyy-MM-dd"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

		}

		// CurrentCondition
		nodes = doc.getElementsByTagName("current_conditions");
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childnodes = nodes.item(i).getChildNodes();

			// Date
			ExtendedServiceDataCurrent condition = new ExtendedServiceDataCurrent(new Date(weather.getMeasureTimestamp().getTime()),
					new Time(weather.getMeasureTimestamp().getTime()), city,
				null, 1.0f,
				Float.NaN,
				Float.NaN,
				Float.MAX_VALUE,
				null, new Time(1), new Time(2));

			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				// Temperature
				if (childnodes.item(j).getNodeName() == "temp_c") {
					condition.setTemperature(Measure.valueOf(Float.parseFloat(dataString), SI.CELSIUS));
				} else if (childnodes.item(j).getNodeName() == "humidity") {
					// Relativ humidity
					String[] segs = dataString.split(Pattern.quote(":"));
					condition.setRelativHumidity(Float.parseFloat(segs[1].substring(0, (segs[1].length() - 1))));
				} else if (childnodes.item(j).getNodeName() == "condition") {
					// Condition
					condition.setCondition(DefaultCondition.retrieveCondition(Integer.parseInt(dataString)));
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

			ExtendedServiceDataForecast condition = new ExtendedServiceDataForecast(
							new Date(System.currentTimeMillis()), 
							new Time(System.currentTimeMillis()), 
							city, 
							Measure.valueOf(10.0f, SI.CELSIUS),  
							Measure.valueOf(10.0f, SI.CELSIUS),
							1.0f, 1.0f, 10.0f, DefaultCondition.retrieveCondition(Condition.UNKNOWN_CONDITION_CODE));

			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				if (childnodes.item(j).getNodeName() == "day_of_week") {
					// Date
					try {
						condition.setMeasureDate(Converter.convertDateFromWeekday(dataString));
					} catch (ParseException e) {
						e.printStackTrace(); // Exception ausgeben
						break;
					}
				} else if (childnodes.item(j).getNodeName() == "low") {
					// Temperature low
					condition.setTemperatureLow(Measure.valueOf(Float.parseFloat(dataString), SI.CELSIUS));
					// condition.setTemperatureLow(WeatherStrategyLib.convertToCelsius(temp));
				} else if (childnodes.item(j).getNodeName() == "high") {
					// Temperature high
					condition.setTemperatureHigh(Measure.valueOf(Float.parseFloat(dataString), SI.CELSIUS));
				} else if (childnodes.item(j).getNodeName() == "condition") {
					// Condition
					condition.setCondition(DefaultCondition.retrieveCondition(Integer.parseInt(dataString)));
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
