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

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.staticsensor.StaticSensorController;
import de.pgalise.simulation.staticsensor.StaticSensorControllerLocal;

/**
 * Controller for static sensors like weather or energy sensors.
 * 
 * @author Andreas Rehfeldt
 * @author Marina
 * @author Marcus
 * @auther Timo
 * @version 1.0 (Aug 21, 2012)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.staticsensor.StaticSensorController")
@Remote(StaticSensorController.class)
@Local(StaticSensorControllerLocal.class)
public class DefaultStaticSensorController extends AbstractController implements StaticSensorControllerLocal {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultStaticSensorController.class);

	/**
	 * Controller name
	 */
	private static final String NAME = "StaticSensorController";

	/**
	 * EnergyController
	 */
	private EnergyController energy;

	/**
	 * Sensor domain
	 */
	@EJB
	private SensorRegistry sensorRegistry;

	/**
	 * SensorFactory to create sensors;
	 */
	@EJB
	private SensorFactory sensorFactory;

	@EJB
	private ServiceDictionary serviceDictionary;

	@EJB
	private GPSMapper gpsMapper;

	/**
	 * Default
	 */
	public DefaultStaticSensorController() {
	}

	@Override
	public void createSensor(SensorHelper sensor) throws SensorException {
		Sensor newSensor = null;
		try {
			newSensor = this.sensorFactory.createSensor(sensor,
					StaticSensorControllerLocal.RESPONSIBLE_FOR_SENSOR_TYPES);
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		if (newSensor != null) {
			log.debug("Sensor " + newSensor.getSensorId() + "created");
			newSensor = this.sensorRegistry.addSensor(newSensor);
		}
	}

	@Override
	public void deleteSensor(SensorHelper sensor) throws SensorException {
		// Check sensor
		Sensor newSensor = this.sensorRegistry.getSensor(sensor.getSensorID());
		if (newSensor != null && this.sensorRegistry.removeSensor(newSensor) != null) {
			return;
		}
		throw new SensorException("Can't delete sensor: " + sensor.getSensorID());
	}

	public EnergyController getEnergy() {
		return this.energy;
	}

	/**
	 * Returns the sensor domain
	 * 
	 * @return sensorDomain
	 */
	public SensorRegistry getSensorDomain() {
		return this.sensorRegistry;
	}

	/**
	 * Returns the sensor factory
	 * 
	 * @return sensorFactory
	 */
	public SensorFactory getSensorFactory() {
		return this.sensorFactory;
	}

	public void setEnergy(EnergyController energy) {
		this.energy = energy;
	}

	@Override
	public boolean statusOfSensor(final SensorHelper sensor) throws SensorException {

		boolean state = false;
		// Check sensor
		Sensor newSensor = this.sensorRegistry.getSensor(sensor.getSensorID());
		if (newSensor != null) {
			state = newSensor.isActivated();
		}
		// Send state
		return state;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		/* Nothing to do here: */
	}

	@Override
	protected void onReset() {
		/* Nothing to do here: */
	}

	@Override
	protected void onStart(StartParameter param) {
		/* Nothing to do here: */
	}

	@Override
	protected void onStop() {
		/* Nothing to do here: */
	}

	@Override
	protected void onUpdate(SimulationEventList simulationEventList) {
		/* Nothing to do here: */
	}

	@Override
	protected void onResume() {
		/* Nothing to do here: */
	}

	@Override
	public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.deleteSensor(sensor);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
