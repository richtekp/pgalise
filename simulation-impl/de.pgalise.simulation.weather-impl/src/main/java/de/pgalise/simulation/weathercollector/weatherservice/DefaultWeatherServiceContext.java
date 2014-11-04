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
package de.pgalise.simulation.weathercollector.weatherservice;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weathercollector.exceptions.ReadServiceDataException;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import de.pgalise.simulation.weathercollector.weatherservice.strategy.YahooWeather;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses strategies to read informations from weather services
 *
 * @author Andreas Rehfeldt
 * @version 1.2 (Apr 15, 2012)
 */
@Stateful
public class DefaultWeatherServiceContext implements WeatherServiceContext {

  private final static Logger LOGGER = LoggerFactory.getLogger(
    DefaultWeatherServiceContext.class);

  /**
   * Path to the file with strategies
   */
  public static final String FILEPATH = "weatherservices.xml";

  /**
   * All available strategies
   */
  private Set<ServiceStrategy> strategies;

  /**
   * Current strategy
   */
  private ServiceStrategy currentStrategy;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private DatabaseManager databaseManager;

  /**
   * Constructor
   */
  public DefaultWeatherServiceContext() {
  }

  public DefaultWeatherServiceContext(Set<ServiceStrategy> serviceStrategys) {
    this.strategies = serviceStrategys;
  }

  @PostConstruct
  public void initialize() {
    this.strategies = loadStrategiesFromFile(idGenerator,
      databaseManager);
  }

  /**
   * Returns the best ServiceData
   *
   * @param city City
   * @param databaseManager
   * @return Best ServiceData
   */
  @Override
  public ServiceDataHelper getBestWeather(City city,
    DatabaseManager databaseManager) {

    // ServiceData objects
    ServiceDataHelper bestWeather = null;
    ServiceDataHelper tempWeather;

    // Deploy all strategies
    for (ServiceStrategy strategy0 : this.strategies) {
      try {
        // Set current strategy
        this.currentStrategy = strategy0;

        // --- Get informations ---
        tempWeather = this.currentStrategy.getWeather(city);

        // --- Complete informations ---
        bestWeather = (bestWeather == null) ? tempWeather : ServiceStrategyLib.
          completeWeather(bestWeather,
            tempWeather,
            idGenerator);

      } catch (ReadServiceDataException e) {
        LOGGER.warn("see nested exception",
          e);
        continue;
      }
    }

    return bestWeather;
  }

  /**
   * Returns the weather informations with the help of a random strategy for the
   * given city
   *
   * @param city City
   * @param databaseManager
   * @return ServiceData object
   * @throws ReadServiceDataException Data can not be read by strategy
   */
  @Override
  public ServiceDataHelper getSingleWeather(City city,
    DatabaseManager databaseManager) throws ReadServiceDataException {
    this.currentStrategy = this.getRandomStrategy();

    // Return informations
    return this.currentStrategy.getWeather(city);
  }

  public Set<ServiceStrategy> getStrategies() {
    return this.strategies;
  }

  /**
   * Returns a random strategy
   *
   * @return random ServiceStrategy
   */
  private ServiceStrategy getRandomStrategy() {

    if (this.strategies.size() > 0) {
      return new LinkedList<>(this.strategies).get(new Random().nextInt(
        this.strategies.size()));
    } else {
      return null;
    }
  }

  /**
   * Loads the strategies for the weather stations
   *
   * @return list with available strategies
   */
  @SuppressWarnings("unchecked")
  private static Set<ServiceStrategy> loadStrategiesFromFile(
    IdGenerator idGenerator,
    DatabaseManager databaseManager) {
    Set<ServiceStrategy> retValue = new HashSet<ServiceStrategy>(Arrays.asList(
      new YahooWeather(idGenerator,
        databaseManager)));

//    try (InputStream propInFile = Thread.currentThread().getContextClassLoader().
//      getResourceAsStream(FILEPATH)) {
//      // Read file
//      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//      Document doc = dBuilder.parse(propInFile);
//      doc.getDocumentElement().normalize();
//
//      // Get strategies node
//      NodeList nList = doc.getElementsByTagName("strategy");
//
//      // Get all strategies
//      for (int temp = 0; temp < nList.getLength(); temp++) {
//        Node nNode = nList.item(temp);
//        String classname = nNode.getTextContent();
//
//        // Add strategy
//        Object strategyRaw = Class.forName(classname).newInstance();
//        if (!(strategyRaw instanceof ServiceStrategy)) {
//          throw new IllegalArgumentException(String.format(
//            "file %s contains illegal class name %s",
//            FILEPATH,
//            classname));
//        }
//        ServiceStrategy strategy = (ServiceStrategy) strategyRaw;
//        list.add(strategy);
//      }
//    } catch (ParserConfigurationException | SAXException | IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//      throw new RuntimeException(
//        "Could not load the XML file for weather stations");
//    }
    return retValue;
  }
}
