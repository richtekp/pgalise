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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.pgalise.util.weathercollector.model.City;
import de.pgalise.util.weathercollector.model.ServiceDataCurrent;
import de.pgalise.util.weathercollector.model.ServiceDataForecast;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.Converter;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import javax.persistence.EntityManagerFactory;

/**
 * Returns weather informations from MSN. Uses the strategy pattern.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Apr 21, 2012)
 */
public final class MSNWeather extends XMLAPIWeather {

	/**
	 * Constructor
	 */
	public MSNWeather() {
		super("http://weather.service.msn.com/data.aspx?src=vista&weadegreetype=C&culture=en-US&weasearchstr=", "MSN",
				"C");
	}

	@Override
	protected ServiceDataHelper extractWeather(City city, Document doc, DatabaseManager databaseManager) {
		ServiceDataHelper weather = new ServiceDataHelper(city, this.apiname);

		// Read global data
		NodeList nodes = doc.getElementsByTagName("weather");
		Node node = nodes.item(0);
		if (node != null) {
			// City
			weather.setApicity(node.getAttributes().getNamedItem("weatherlocationname").getTextContent());

			// Temperature unit
			String temp = node.getAttributes().getNamedItem("degreetype").getTextContent();
			if (temp != null && !temp.isEmpty()) {
				this.unitTemperature = temp;
			}

			NodeList childnodes = node.getChildNodes();
			for (int j = 0; j < childnodes.getLength(); j++) {
				Node childnode = childnodes.item(j);

				// CurrentCondition
				if (childnode.getNodeName().equals("current")) {
					NamedNodeMap attributes = childnode.getAttributes();

					ServiceDataCurrent condition;
					try {
						// Date
						String dateString = attributes.getNamedItem("date").getTextContent();
						dateString = dateString + " " + attributes.getNamedItem("observationtime").getTextContent();

						// Current date
						Time time = Converter.convertTime(dateString, "yyyy-MM-dd h:mm:ss");
						Date date = Converter.convertDate(dateString, "yyyy-MM-dd h:mm:ss");
						condition = new ServiceDataCurrent(date, time);

						// Date
						weather.setMeasureTimestamp(Converter.convertTimestamp(dateString, "yyyy-MM-dd h:mm:ss"));
					} catch (ParseException e) {
						e.printStackTrace();
						return null;
					}

					String dataString = "";

					// Temperature
					dataString = attributes.getNamedItem("temperature").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setTemperature(Float.parseFloat(dataString));
					}

					// Condition
					dataString = attributes.getNamedItem("skycode").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setCondition(Integer.parseInt(dataString));
					}

					// Relativ humidity
					dataString = attributes.getNamedItem("humidity").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setRelativHumidity(Float.parseFloat(dataString));
					}

					// Temperature
					dataString = attributes.getNamedItem("windspeed").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setWindVelocity(Float.parseFloat(dataString));
					}

					// Temperature unit
					condition.setUnitTemperature(this.unitTemperature);

					// City
					condition.setCity(city);

					// Save informations
					weather.setCurrentCondition(condition);
				} else if (childnode.getNodeName().equals("forecast")) {
					// Forecast
					NamedNodeMap attributes = childnode.getAttributes();

					ServiceDataForecast condition;
					try {
						// Date
						String dateString = attributes.getNamedItem("date").getTextContent();

						// Current date
						condition = new ServiceDataForecast(Converter.convertDate(dateString, "yyyy-MM-dd"));
					} catch (ParseException e) {
						continue;
					}

					String dataString = "";

					// Temperature (low)
					dataString = attributes.getNamedItem("low").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setTemperatureLow(Float.parseFloat(dataString));
					}

					// Temperature (high)
					dataString = attributes.getNamedItem("high").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setTemperatureHigh(Float.parseFloat(dataString));
					}

					// Condition
					dataString = attributes.getNamedItem("skycodeday").getTextContent();
					if ((dataString != null) && !dataString.equals("")) {
						condition.setCondition(Integer.parseInt(dataString));
					}

					// City
					condition.setCity(city);

					// Save informations
					if (condition.getDate() != null) {
						condition.setUnitTemperature(this.unitTemperature);
						weather.getForecastConditions().add(condition);
					}
				}
			}
		}

		// Return informations
		return weather;
	}

	@Override
	protected void setSearchCity(City city) {
		this.searchCity = city.getName();
	}
}
