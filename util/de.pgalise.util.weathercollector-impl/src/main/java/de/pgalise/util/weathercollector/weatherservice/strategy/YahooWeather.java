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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.pgalise.util.weathercollector.model.City;
import de.pgalise.util.weathercollector.model.ServiceDataCurrent;
import de.pgalise.util.weathercollector.model.ServiceDataForecast;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.Converter;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import java.sql.Date;
import java.sql.Time;

/**
 * Returns weather informations from Yahoo. Uses the strategy pattern. More: http://developer.yahoo.com/weather/#terms
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public class YahooWeather extends XMLAPIWeather {

	/**
	 * Constructor
	 */
	public YahooWeather() {
		super("http://weather.yahooapis.com/forecastrss?u=c&w=", "Yahoo", "C");
	}

	@Override
	protected ServiceDataHelper extractWeather(City city, Document doc, DatabaseManager databaseManager) {
		ServiceDataHelper weather = new ServiceDataHelper(city, this.apiname);

		// City (<yweather:location city="Oldenburg" region="NI" country="Germany"/>)
		NodeList nodes = doc.getElementsByTagName("yweather:location");
		for (int i = 0; i < nodes.getLength(); i++) {
			weather.setApicity(nodes.item(i).getAttributes().getNamedItem("city").getTextContent());
		}

		// Date
		nodes = doc.getElementsByTagName("pubDate");
		for (int i = 0; i < nodes.getLength(); i++) {
			try {
				String dateString = nodes.item(i).getTextContent();
				weather.setMeasureTimestamp(Converter.convertTimestamp(dateString, "E, dd MMM yyyy h:mm a z"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// Unit (<yweather:units temperature="C" distance="km" pressure="mb" speed="km/h"/>)
		nodes = doc.getElementsByTagName("yweather:units");
		for (int i = 0; i < nodes.getLength(); i++) {
			String temp = nodes.item(i).getAttributes().getNamedItem("temperature").getTextContent();
			if (temp != null && !temp.isEmpty()) {
				this.unitTemperature = temp;
				break;
			}
		}

		// CurrentCondition (<yweather:condition text="Mist" code="20" temp="6" date="Fri, 16 Mar 2012 7:58 am CET" />)
		nodes = doc.getElementsByTagName("yweather:condition");
		for (int i = 0; i < nodes.getLength(); i++) {
			NamedNodeMap attributes = nodes.item(i).getAttributes();

			ServiceDataCurrent condition;
			try {
				// Date
				String dateString = attributes.getNamedItem("date").getTextContent();
				Date date = Converter.convertDate(dateString, "E, dd MMM yyyy h:mm a z");
				Time time = Converter.convertTime(dateString, "E, dd MMM yyyy h:mm a z");
				condition = new ServiceDataCurrent(date, time	);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}

			String dataString = "";

			// Temperature
			dataString = attributes.getNamedItem("temp").getTextContent();
			if ((dataString != null) && !dataString.equals("")) {
				condition.setTemperature(Float.parseFloat(dataString));
			}

			// Condition
			dataString = attributes.getNamedItem("code").getTextContent();
			if ((dataString != null) && !dataString.equals("")) {
				condition.setCondition(Integer.parseInt(dataString));
			}

			// Temperature unit
			condition.setUnitTemperature(this.unitTemperature);

			// City
			condition.setCity(city);

			// Save informations
			weather.setCurrentCondition(condition);
		}

		// Forecast (<yweather:forecast day="Fri" date="16 Mar 2012" low="7" high="12" text="Mostly Sunny" code="34" />)
		nodes = doc.getElementsByTagName("yweather:forecast");
		for (int i = 0; i < nodes.getLength(); i++) {
			NamedNodeMap attributes = nodes.item(i).getAttributes();

			ServiceDataForecast condition;
			try {
				// Date
				String dateString = attributes.getNamedItem("date").getTextContent();
				condition = new ServiceDataForecast(Converter.convertDate(dateString, "dd MMM yyyy"));
			} catch (ParseException e) {
				e.printStackTrace();
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
			dataString = attributes.getNamedItem("code").getTextContent();
			if ((dataString != null) && !dataString.equals("")) {
				condition.setCondition(Integer.parseInt(dataString));
			}

			// Temperature unit
			condition.setUnitTemperature(this.unitTemperature);

			// City
			condition.setCity(city);

			// Save informations
			weather.getForecastConditions().add(condition);
		}

		if (weather.getCurrentCondition() != null) {
			// (<yweather:atmosphere humidity="86" visibility="4.7" pressure="1019.6"
			// rising="2" />)
			nodes = doc.getElementsByTagName("yweather:atmosphere");
			for (int i = 0; i < nodes.getLength(); i++) {
				NamedNodeMap attributes = nodes.item(i).getAttributes();
				ServiceDataCurrent condition = weather.getCurrentCondition();

				String humidity = attributes.getNamedItem("humidity").getTextContent();
				if ((humidity != null) && !humidity.equals("")) {
					condition.setRelativHumidity(Float.parseFloat(humidity));
				}

				String visibiliy = attributes.getNamedItem("visibility").getTextContent();
				if ((visibiliy != null) && !visibiliy.equals("")) {
					condition.setVisibility(Float.parseFloat(visibiliy));
				}
			}

			// Sunset & sunrise
			nodes = doc.getElementsByTagName("yweather:astronomy");
			for (int i = 0; i < nodes.getLength(); i++) {
				NamedNodeMap attributes = nodes.item(i).getAttributes();
				ServiceDataCurrent condition = weather.getCurrentCondition();

				try {
					// Sunset
					String timeString = attributes.getNamedItem("sunrise").getTextContent();
					if ((timeString != null) && !timeString.equals("")) {
						condition.setSunrise(Converter.convertTime(timeString, "h:mm a"));
					}

					// Sunrise
					timeString = attributes.getNamedItem("sunset").getTextContent();
					if ((timeString != null) && !timeString.equals("")) {
						condition.setSunset(Converter.convertTime(timeString, "h:mm a"));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			// Wind
			nodes = doc.getElementsByTagName("yweather:wind");
			for (int i = 0; i < nodes.getLength(); i++) {
				NamedNodeMap attributes = nodes.item(i).getAttributes();
				ServiceDataCurrent condition = weather.getCurrentCondition();

				String direction = attributes.getNamedItem("direction").getTextContent();
				if ((direction != null) && !direction.equals("")) {
					condition.setWindDirection(Integer.parseInt(direction));
				}

				String speed = attributes.getNamedItem("speed").getTextContent();
				if ((speed != null) && !speed.equals("")) {
					condition.setWindVelocity(Float.parseFloat(speed));
				}

			}
		}

		// Return informations
		return weather;
	}

	@Override
	protected void setSearchCity(City city) {
		try {
			this.searchCity = "" + this.getWoeid(city.getName());
		} catch (IOException | ParserConfigurationException | SAXException e) {
			this.searchCity = "";
		}
	}

	/**
	 * Returns the city for the API
	 * 
	 * @param city
	 *            City
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private String getWoeid(String city) throws IOException, ParserConfigurationException, SAXException {
		// Search city
		URL url = new URL(
				"http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text=%27" + city
						+ "%27&format=xml");

		Document doc = null;
		try (InputStream inputStream = url.openStream()) {
			// Get xml document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(inputStream);
		}

		// Read WOEID
		if (doc != null) {
			NodeList nodes = doc.getElementsByTagName("woeid");
			if (nodes.item(0) != null) {
				return nodes.item(0).getTextContent();
			}
		}

		return "";
	}

}