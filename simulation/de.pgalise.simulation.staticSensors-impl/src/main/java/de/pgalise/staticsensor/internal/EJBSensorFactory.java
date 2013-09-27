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

import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.ServerConfigurationIdentifier;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.weather.service.WeatherController;
import java.lang.reflect.InvocationTargetException;

/**
 * EJB-Wrapper around the DefaultSensorFactory.
 * 
 * @author mustafa
 * @version 1.0 (Apr 5, 2013)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.sensorFramework.SensorFactory")
@Local(SensorFactory.class)
public class EJBSensorFactory implements SensorFactory {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(EJBSensorFactory.class);

	/**
	 * Sensor factory
	 */
	private DefaultSensorFactory factory;

	/**
	 * Service dictionary
	 */
	@EJB
	private ServiceDictionary dic;

	/**
	 * Server config reader
	 */
	@EJB
	private ConfigReader configReader;

	@PostConstruct
	public void postConstruct() {
		// output server auslesen
		String output = configReader.getProperty(ServerConfigurationIdentifier.SENSOR_OUTPUT_SERVER);

		String[] serverAddress = output.split(":");

		Output sensorOutput = null;

		sensorOutput = new TcpIpOutput(serverAddress[0], Integer.valueOf(serverAddress[1]), new TcpIpKeepOpenStrategy());

		final String outputDecorator = configReader.getProperty(ServerConfigurationIdentifier.OUTPUT_DECORATOR);
		if (outputDecorator != null && output.length() > 0 && outputDecorator.length() != 0) {
			try {
				sensorOutput = (Output) Class.forName(outputDecorator).getConstructor(Output.class)
						.newInstance(sensorOutput);
				log.info("Using output decorator: "+outputDecorator);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			log.info("Using output decorator: default (none)");
		}

		factory = new DefaultSensorFactory(dic.getRandomSeedService(), dic.getController(WeatherController.class),
				dic.getController(EnergyController.class), sensorOutput);
		log.info("SensorFactory initialized. Set output server to " + output);
	}

	@Override
	public Sensor<?> createSensor(SensorHelper<?> sensorHelper, EnumSet<SensorTypeEnum> allowedTypes)
			throws InterruptedException, ExecutionException {
		return factory.createSensor(sensorHelper, allowedTypes);
	}

	@Override
	public Output getOutput() {
		return this.factory.getOutput();
	}
}
