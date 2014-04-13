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
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

/**
 * Abstract super class for gps interferers
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Nov 14, 2012)
 */
public abstract class GpsBaseInterferer implements GpsInterferer {

  private static final long serialVersionUID = 1L;

  /**
   * Random Seed Service
   */
  @EJB
  private RandomSeedService randomSeedService;

  /**
   * Random Object
   */
  private Random random;

  /**
   * Change amplitude
   */
  private double changeAmplitude;

  /**
   * Probability to change the values
   */
  private double changeProbability;

  /**
   * Properties with limits
   */
  private Properties properties;

  public GpsBaseInterferer() {
  }

  public GpsBaseInterferer(RandomSeedService randomservice,
    double changeAmplitude,
    double changeProbability) {
    this.randomSeedService = randomservice;
    this.changeAmplitude = changeAmplitude;
    this.changeProbability = changeProbability;
  }

  public GpsBaseInterferer(RandomSeedService randomSeedService,
    String propertiesFilepath) {
    this(propertiesFilepath);
    this.randomSeedService = randomSeedService;
  }

  /**
   * propertiesFilepath
   *
   * @param filepath File path to the properties file
   */
  public GpsBaseInterferer(
    String propertiesFilepath) {
    if (propertiesFilepath == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "filepath"));
    }

    // Read property file
    try (InputStream propInFile = Thread.currentThread().getContextClassLoader().
      getResourceAsStream(
        propertiesFilepath)) {
        this.properties = new Properties();
        this.properties.loadFromXML(propInFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

		// Save default values

      /*
       * Note every property file should have these properties
       */
      this.changeAmplitude = Double.parseDouble(this.properties.getProperty(
        "change_amplitude"));
      this.changeProbability = Double.parseDouble(this.properties.getProperty(
        "change_probability"));
  }
  
  @PostConstruct
  public void initialize() {    
    this.random = new Random(randomSeedService.getSeed(GpsBaseInterferer.class.
      getName()));
  }

  public double getChangeAmplitude() {
    return this.changeAmplitude;
  }

  public double getChangeProbability() {
    return this.changeProbability;
  }

  public Random getRandom() {
    return this.random;
  }

  public void setChangeAmplitude(double changeAmplitude) {
    this.changeAmplitude = changeAmplitude;
  }

  public void setChangeProbability(double changeProbability) {
    this.changeProbability = changeProbability;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  /**
   * @return the randomSeedService
   */
  public RandomSeedService getRandomservice() {
    return randomSeedService;
  }

  /**
   * @param randomservice the randomSeedService to set
   */
  public void setRandomservice(
    RandomSeedService randomservice) {
    this.randomSeedService = randomservice;
  }

  /**
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  protected void setProperties(Properties properties) {
    this.properties = properties;
  }

}
