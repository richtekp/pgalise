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
package de.pgalise.simulation.weathercollector.weatherservice.strategy;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import de.pgalise.simulation.weathercollector.weatherservice.ServiceStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.measure.quantity.Temperature;
import javax.measure.unit.Unit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

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
  private String apiname;

  /**
   * Temperature unit
   */
  private Unit<Temperature> unitTemperature;

  /**
   * URL
   */
  protected final String URL;
  private DatabaseManager databaseManager;
  private IdGenerator idGenerator;

  /**
   * Constructor
   *
   * @param url URL of the weather service
   * @param apiname
   * @param unit Temperature unit
   * @param idGenerator
   * @param databaseManager
   */
  public XMLAPIWeather(String url,
    String apiname,
    Unit<Temperature> unit,
    IdGenerator idGenerator,
    DatabaseManager databaseManager) {
    this(url,
      apiname,
      idGenerator,
      databaseManager);
    this.unitTemperature = unit;
  }

  public XMLAPIWeather(String url,
    String apiname,
    IdGenerator idGenerator,
    DatabaseManager databaseManager) {
    this.URL = url;
    this.apiname = apiname;
    this.idGenerator = idGenerator;
    this.databaseManager = databaseManager;
  }

  /**
   *
   * @param city (name has to be set and will be used to determine the area)
   * @return
   * @throws ReadServiceDataException
   */
  @Override
  public ServiceDataHelper getWeather(City city) throws ReadServiceDataException {
    // No City can be found
    if (city.getName() == null || city.getName().isEmpty()) {
      throw new ReadServiceDataException(
        "Stadt fuer die Api kann nicht gefunden werden.");
    }

    // Extract data
    return this.extractWeather(city,
      this.fetchWeatherData(city.getName()),
      databaseManager);
  }

  /**
   * Extract weather informations to the xml root
   *
   * @param city City
   * @param doc Document root
   * @param databaseManager
   * @return ServiceData object
   */
  protected abstract ServiceDataHelper extractWeather(City city,
    Document doc,
    DatabaseManager databaseManager);

  /**
   * Returns the weather service xml to the given city
   *
   * @param city City
   * @return Document root of the xml file
   * @throws ReadServiceDataException Data can not be read
   */
  protected Document fetchWeatherData(String city) throws ReadServiceDataException {
    try {
      URL url = new URL(this.URL + city);
      Document doc;
      try (InputStream inputStream = url.openStream()) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.
          newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        doc = docBuilder.parse(inputStream);
      }

      return doc;
    } catch (javax.xml.parsers.ParserConfigurationException pce) {
      throw new ReadServiceDataException("Baum kann nicht geparst werden.");
    } catch (org.xml.sax.SAXException sa) {
      throw new ReadServiceDataException("Fehler beim Parsen.");
    } catch (IOException ioe) {
      throw new ReadServiceDataException(
        "Inputstream kann nicht geschlossen werden.");
    }
  }

  /**
   * @return the apiname
   */
  public String getApiname() {
    return apiname;
  }

  /**
   * @param apiname the apiname to set
   */
  public void setApiname(String apiname) {
    this.apiname = apiname;
  }

  /**
   * @return the unitTemperature
   */
  public Unit<Temperature> getUnitTemperature() {
    return unitTemperature;
  }

  /**
   * @param unitTemperature the unitTemperature to set
   */
  public void setUnitTemperature(
    Unit<Temperature> unitTemperature) {
    this.unitTemperature = unitTemperature;
  }

  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }
}
