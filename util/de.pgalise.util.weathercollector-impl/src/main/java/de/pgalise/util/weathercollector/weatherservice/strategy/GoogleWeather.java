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

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.pgalise.util.weathercollector.model.City;
import de.pgalise.util.weathercollector.model.ServiceDataCurrent;
import de.pgalise.util.weathercollector.model.ServiceDataForecast;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.Converter;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategyLib;
import javax.persistence.EntityManagerFactory;

/**
 * Returns weather informations from Google. Uses the strategy pattern.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public final class GoogleWeather extends XMLAPIWeather {

	/**
	 * Constructor
	 */
	public GoogleWeather() {
		super("http://www.google.com/ig/api?h1=en&weather=", "Google", "F");
	}

	@Override
	protected ServiceDataHelper extractWeather(City city, Document doc, DatabaseManager entityManagerFactory) {
		ServiceDataHelper weather = new ServiceDataHelper(city, this.apiname);

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
			ServiceDataCurrent condition = new ServiceDataCurrent(new Date(weather.getMeasureTimestamp().getTime()),
					new Time(weather.getMeasureTimestamp().getTime()));

			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				// Temperature
				if (childnodes.item(j).getNodeName() == "temp_c") {
					condition.setTemperature(Float.parseFloat(dataString));
				} else if (childnodes.item(j).getNodeName() == "humidity") {
					// Relativ humidity
					String[] segs = dataString.split(Pattern.quote(":"));
					condition.setRelativHumidity(Float.parseFloat(segs[1].substring(0, (segs[1].length() - 1))));
				} else if (childnodes.item(j).getNodeName() == "condition") {
					// Condition
					condition.setCondition(ServiceStrategyLib.getConditionCode(dataString, entityManagerFactory));
				}
			}

			// Unit
			condition.setUnitTemperature("C");

			// City
			condition.setCity(city.getId());

			// Save current weather
			weather.setCurrentCondition(condition);
		}

		// ForecastCondition
		nodes = doc.getElementsByTagName("forecast_conditions");
		for (int i = 0; i < nodes.getLength(); i++) {
			NodeList childnodes = nodes.item(i).getChildNodes();

			ServiceDataForecast condition = new ServiceDataForecast(null);

			for (int j = 0; j < childnodes.getLength(); j++) {
				// Data
				String dataString = childnodes.item(j).getAttributes().getNamedItem("data").getTextContent();

				if (childnodes.item(j).getNodeName() == "day_of_week") {
					// Date
					try {
						condition.setDate(Converter.convertDateFromWeekday(dataString));
					} catch (ParseException e) {
						e.printStackTrace(); // Exception ausgeben
						break;
					}
				} else if (childnodes.item(j).getNodeName() == "low") {
					// Temperature low
					condition.setTemperatureLow(Float.parseFloat(dataString));
					// condition.setTemperatureLow(WeatherStrategyLib.convertToCelsius(temp));
				} else if (childnodes.item(j).getNodeName() == "high") {
					// Temperature high
					condition.setTemperatureHigh(Float.parseFloat(dataString));
				} else if (childnodes.item(j).getNodeName() == "condition") {
					// Condition
					condition.setCondition(ServiceStrategyLib.getConditionCode(dataString, entityManagerFactory));
				}
			}

			// City
			condition.setCity(city.getId());

			// Save weather informations
			if (condition.getDate() != null) {
				condition.setUnitTemperature(this.unitTemperature);
				weather.getForecastConditions().add(condition);
			}
		}

		// Returns the weather informations
		return weather;
	}

	@Override
	protected void setSearchCity(City city) {
		this.searchCity = city.getName();
	}
}
