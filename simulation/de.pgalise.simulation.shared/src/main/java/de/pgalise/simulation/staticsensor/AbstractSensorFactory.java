/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
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

	private int updateLimit = 1;

	public AbstractSensorFactory() {
	}

	public AbstractSensorFactory(Output output) {
		this.sensorOutput = output;
	}

	public AbstractSensorFactory(Output sensorOutput,
		RandomSeedService rss,
		int updateLimit) {
		if(sensorOutput == null) {
			throw new IllegalArgumentException("output null");
		}
		this.sensorOutput = sensorOutput;
		if(randomSeedService == null) {
			throw new IllegalArgumentException("randomSeedService null");
		}
		this.randomSeedService = rss;
		this.updateLimit = updateLimit;
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
	
	public Coordinate createRandomPositionEnergySensor() {
		throw new UnsupportedOperationException();
	}
	
	public Coordinate createRandomPositionWeatherSensor() {
		throw new UnsupportedOperationException();
	}
	
	public Coordinate createRandomPositionInfraredSensor() {
		throw new UnsupportedOperationException();
	}
	
	public Coordinate createRandomPositionInductionLoopSensor() {
		throw new UnsupportedOperationException();
	}
	
	public Coordinate createRandomPositionTopoRadarSensor() {
		throw new UnsupportedOperationException();
	}
}
