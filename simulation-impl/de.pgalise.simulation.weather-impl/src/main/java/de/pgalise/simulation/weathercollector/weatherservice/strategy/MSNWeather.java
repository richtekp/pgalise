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
import de.pgalise.simulation.weather.entity.WeatherCondition;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.pgalise.simulation.weather.entity.ExtendedServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ExtendedServiceDataForecast;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import java.sql.Timestamp;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns weather informations from MSN. Uses the strategy pattern.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Apr 21, 2012)
 */
public class MSNWeather extends XMLAPIWeather {

  private final static Logger LOGGER = LoggerFactory.getLogger(MSNWeather.class);

  /**
   * Constructor
   *
   * @param idGenerator
   * @param databaseManager
   */
  public MSNWeather(IdGenerator idGenerator,
    DatabaseManager databaseManager) {
    super(
      "http://weather.service.msn.com/data.aspx?src=vista&weadegreetype=C&culture=en-US&weasearchstr=",
      "MSN",
      SI.CELSIUS,
      idGenerator,
      databaseManager);
  }

  @Override
  protected ServiceDataHelper extractWeather(City city,
    Document doc,
    DatabaseManager databaseManager) {
    ServiceDataHelper weather = new ServiceDataHelper(city,
      this.getApiname());
    Unit<Temperature> unit = SI.CELSIUS;

    // Read global data
    NodeList nodes = doc.getElementsByTagName("weather");
    Node node = nodes.item(0);
    if (node != null) {
      // City
      weather.setApicity(node.getAttributes().
        getNamedItem("weatherlocationname").getTextContent());

      // Temperature unit
      String temp = node.getAttributes().getNamedItem("degreetype").
        getTextContent();
      if (temp != null && !temp.isEmpty()) {
        if (temp.equals("C")) {
          unit = SI.CELSIUS;
        } else {
          throw new IllegalArgumentException("degreetype is not C");
        }
      }

      NodeList childnodes = node.getChildNodes();
      for (int j = 0; j < childnodes.getLength(); j++) {
        Node childnode = childnodes.item(j);

        // CurrentCondition
        if (childnode.getNodeName().equals("current")) {
          NamedNodeMap attributes = childnode.getAttributes();

          ExtendedServiceDataCurrent condition;
          try {
            // Date
            String dateString = attributes.getNamedItem("date").getTextContent();
            dateString = dateString + " " + attributes.getNamedItem(
              "observationtime").getTextContent();

            // Current date
            Time time = DateConverter.convertTime(dateString,
              "yyyy-MM-dd h:mm:ss");
            Date date = DateConverter.convertDate(dateString,
              "yyyy-MM-dd h:mm:ss");
            condition = new ExtendedServiceDataCurrent(getIdGenerator().
              getNextId(),
              date,
              time,
              city,
              Measure.valueOf(10.0f,
                SI.CELSIUS),
              1.0f,
              1.0f,
              10.0f,
              10.0f,
              20.0f,
              WeatherCondition.retrieveCondition(getIdGenerator(),
                WeatherCondition.UNKNOWN_CONDITION_CODE),
              new Time(1),
              new Time(2));

            // Date
            Timestamp convertedTimestamp = DateConverter.convertTimestamp(
              dateString,
              "yyyy-MM-dd h:mm:ss");
            weather.setMeasureTime(new Time(convertedTimestamp.getTime()));
            weather.setMeasureDate(new Date(convertedTimestamp.getTime()));
          } catch (ParseException e) {
            LOGGER.warn("see nested exception",
              e);
            return null;
          }

          String dataString;
          // Temperature
          dataString = attributes.getNamedItem("temperature").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setTemperature(Measure.valueOf(Float.
              parseFloat(dataString),
              unit));
          }

          // Condition
          dataString = attributes.getNamedItem("skycode").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setCondition(WeatherCondition.retrieveCondition(
              getIdGenerator(),
              Integer.
              parseInt(dataString)));
          }

          // Relativ humidity
          dataString = attributes.getNamedItem("humidity").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setRelativHumidity(Float.parseFloat(dataString));
          }

          // Temperature
          dataString = attributes.getNamedItem("windspeed").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setWindVelocity(Float.parseFloat(dataString));
          }

          // City
          condition.setCity(city);

          // Save informations
          weather.setCurrentCondition(condition);
        } else if (childnode.getNodeName().equals("forecast")) {
          // Forecast
          NamedNodeMap attributes = childnode.getAttributes();

          ExtendedServiceDataForecast condition;
          try {
            // Date
            String dateString = attributes.getNamedItem("date").getTextContent();

            // Current date
            condition = new ExtendedServiceDataForecast(getIdGenerator().
              getNextId(),
              DateConverter.convertDate(dateString,
                "yyyy-MM-dd"),
              new Time(System.currentTimeMillis()),
              city,
              Measure.valueOf(10.0f,
                SI.CELSIUS),
              Measure.valueOf(10.0f,
                SI.CELSIUS),
              1.0f,
              1.0f,
              10.0f,
              20.0f,
              WeatherCondition.retrieveCondition(getIdGenerator(),
                WeatherCondition.UNKNOWN_CONDITION_CODE));
          } catch (ParseException e) {
            continue;
          }

          String dataString;

          // Temperature (low)
          dataString = attributes.getNamedItem("low").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setTemperatureLow(Measure.valueOf(Float.parseFloat(
              dataString),
              SI.CELSIUS));
          }

          // Temperature (high)
          dataString = attributes.getNamedItem("high").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setTemperatureHigh(Measure.valueOf(Float.parseFloat(
              dataString),
              SI.CELSIUS));
          }

          // Condition
          dataString = attributes.getNamedItem("skycodeday").getTextContent();
          if ((dataString != null) && !dataString.isEmpty()) {
            condition.setCondition(WeatherCondition.retrieveCondition(
              getIdGenerator(),
              Integer.
              parseInt(dataString)));
          }

          // City
          condition.setCity(city);

          // Save informations
          if (condition.getMeasureDate() != null) {
            weather.getForecastConditions().add(condition);
          }
        }
      }
    }

    // Return informations
    return weather;
  }
}
