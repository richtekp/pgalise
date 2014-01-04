/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import javax.ejb.EJB;

/**
 *
 * @param <S>
 * @author richter
 */
public abstract class AbstractSensorFactory<S extends Sensor<?,?>> implements
	SensorFactory {

	/**
	 * Sensor output
	 */
	@EJB
	private Output sensorOutput;

	/**
	 * Random seed service
	 */
	@EJB
	private RandomSeedService randomSeedService;
	@EJB
	private IdGenerator idGenerator;

	private int updateLimit = 1;

	public AbstractSensorFactory() {
	}

	public AbstractSensorFactory(Output output) {
		this.sensorOutput = output;
	}

	public AbstractSensorFactory(Output sensorOutput,IdGenerator idGenerator,
		RandomSeedService rss,
		int updateLimit) {
		if(sensorOutput == null) {
			throw new IllegalArgumentException("output null");
		}
		if(idGenerator == null) {
			throw new IllegalArgumentException("idGenerator null");
		}
		this.sensorOutput = sensorOutput;
		if(randomSeedService == null) {
			throw new IllegalArgumentException("randomSeedService null");
		}
		this.randomSeedService = rss;
		this.updateLimit = updateLimit;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setUpdateLimit(int updateLimit) {
		this.updateLimit = updateLimit;
	}

	public int getUpdateLimit() {
		return updateLimit;
	}

	protected void setSensorOutput(Output output) {
		this.sensorOutput = output;
	}

	@Override
	public Output getSensorOutput() {
		return sensorOutput;
	}

	public void setRandomSeedService(RandomSeedService randomSeedService) {
		this.randomSeedService = randomSeedService;
	}

	public RandomSeedService getRandomSeedService() {
		return randomSeedService;
	}
	
	public JaxRSCoordinate createRandomPositionEnergySensor() {
		throw new UnsupportedOperationException();
	}
	
	public JaxRSCoordinate createRandomPositionWeatherSensor() {
		throw new UnsupportedOperationException();
	}
	
	public JaxRSCoordinate createRandomPositionInfraredSensor() {
		throw new UnsupportedOperationException();
	}
	
	public JaxRSCoordinate createRandomPositionInductionLoopSensor() {
		throw new UnsupportedOperationException();
	}
	
	public JaxRSCoordinate createRandomPositionTopoRadarSensor() {
		throw new UnsupportedOperationException();
	}
}
