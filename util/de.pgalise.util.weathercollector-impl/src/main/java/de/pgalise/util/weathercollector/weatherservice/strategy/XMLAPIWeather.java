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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.pgalise.util.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.util.weathercollector.model.ServiceDataHelper;
import de.pgalise.util.weathercollector.util.DatabaseManager;
import de.pgalise.util.weathercollector.weatherservice.ServiceStrategy;
import javax.persistence.EntityManagerFactory;

/**
 * Abstract superclass for weather service strategies which uses an URL
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public abstract class XMLAPIWeather implements ServiceStrategy {

	/**
	 * Name of the strategy
	 */
	protected final String apiname;

	/**
	 * City
	 */
	protected City city;

	/**
	 * Search city of the weather service. Yahoo uses these string
	 */
	protected String searchCity;

	/**
	 * Temperature unit
	 */
	protected String unitTemperature;

	/**
	 * URL
	 */
	protected final String URL;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            URL of the weather service
	 * @param name
	 *            API name
	 * @param unit
	 *            Temperature unit
	 */
	public XMLAPIWeather(String url, String name, String unit) {
		this.URL = url;
		this.apiname = name;
		this.unitTemperature = unit;
	}

	@Override
	public ServiceDataHelper getWeather(City city, DatabaseManager databaseManager) throws ReadServiceDataException {
		// Remember city
		this.city = city;
		this.setSearchCity(this.city);

		// No City can be found
		if (this.searchCity.equals("")) {
			throw new ReadServiceDataException("Stadt fuer die Api kann nicht gefunden werden.");
		}

		// Extract data
		return this.extractWeather(city, this.fetchWeatherData(this.searchCity), databaseManager);
	}

	/**
	 * Extract weather informations to the xml root
	 * 
	 * @param city
	 *            City
	 * @param doc
	 *            Document root
	 * @return ServiceData object
	 */
	protected abstract ServiceDataHelper extractWeather(City city, Document doc, DatabaseManager databaseManager);

	/**
	 * Returns the weather service xml to the given city
	 * 
	 * @param city
	 *            City
	 * @return Document root of the xml file
	 * @throws ReadServiceDataException
	 *             Data can not be read
	 */
	protected Document fetchWeatherData(String city) throws ReadServiceDataException {
		try {
			URL url = new URL(this.URL + city);
			InputStream inputStream = url.openStream();

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(inputStream);

			inputStream.close();

			return doc;
		} catch (javax.xml.parsers.ParserConfigurationException pce) {
			throw new ReadServiceDataException("Baum kann nicht geparst werden.");
		} catch (org.xml.sax.SAXException sa) {
			throw new ReadServiceDataException("Fehler beim Parsen.");
		} catch (IOException ioe) {
			throw new ReadServiceDataException("Inputstream kann nicht geschlossen werden.");
		}
	}

	/**
	 * Set the city for the API of the weather services
	 * 
	 * @param city
	 *            City
	 */
	protected abstract void setSearchCity(City city);
}
