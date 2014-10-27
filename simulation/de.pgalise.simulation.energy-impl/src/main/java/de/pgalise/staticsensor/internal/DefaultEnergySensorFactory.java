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
package de.pgalise.staticsensor.internal;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.energy.EnergySensorFactory;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import javax.ejb.Singleton;

/**
 * EJB-Wrapper around the AbstractEnergySensorFactory.
 *
 * @author mustafa
 * @version 1.0 (Apr 5, 2013)
 */
@Lock(LockType.READ)
@Singleton
public class DefaultEnergySensorFactory extends AbstractEnergySensorFactory
  implements EnergySensorFactory {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultEnergySensorFactory.class);

  /**
   * Server config reader
   */
  @EJB
  private ConfigReader configReader;

  public DefaultEnergySensorFactory() {
  }

  public DefaultEnergySensorFactory(RandomSeedService rss,
    IdGenerator idGenerator,
    WeatherControllerLocal wctrl,
    EnergyControllerLocal ectrl,
    int updateLimit) {
    super(rss,
      idGenerator,
      wctrl,
      ectrl,
      updateLimit);
  }

//	@PostConstruct
//	public void postConstruct() {
//		// output server auslesen
//		String output = configReader.getProperty(
//			ServerConfigurationIdentifier.SENSOR_OUTPUT_SERVER);
//
//		String[] serverAddress = output.split(":");
//
//		Output sensorOutput = null;
//
//		sensorOutput = new AbstractTcpIpOutput(serverAddress[0],
//			Integer.valueOf(serverAddress[1]),
//			new TcpIpKeepOpenStrategy());
//
//		final String outputDecorator = configReader.getProperty(
//			ServerConfigurationIdentifier.OUTPUT_DECORATOR);
//		if (outputDecorator != null && output.length() > 0 && outputDecorator.
//			length() != 0) {
//			try {
//				sensorOutput = (Output) Class.forName(outputDecorator).getConstructor(
//					Output.class)
//					.newInstance(sensorOutput);
//				log.info("Using output decorator: " + outputDecorator);
//			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				throw new RuntimeException(e);
//			}
//		} else {
//			log.info("Using output decorator: default (none)");
//		}
//
//		log.info("SensorFactory initialized. Set output server to " + output);
//	}
}
