/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 *
 * @param <S> 
 * @author richter
 */
public abstract class AbstractSensorFactory<S extends Sensor<?>> implements SensorFactory {
	
	/**
	 * Sensor output
	 */
	private Output sensorOutput;

	/**
	 * Random seed service
	 */
	private RandomSeedService randomSeedService;

	/**
	 * Weather controller
	 */
	private WeatherController weatherController;

	/**
	 * Energy controller
	 */
	private EnergyController energyController;

	public AbstractSensorFactory(Output sensorOutput,
		RandomSeedService rss,
		WeatherController weatherCtrl,
		EnergyController energyCtrl) {
		this.sensorOutput = sensorOutput;
		this.randomSeedService = rss;
		this.weatherController = weatherCtrl;
		this.energyController = energyCtrl;
	}

	public AbstractSensorFactory(Output output) {
		this.sensorOutput = output;
	}

	protected void setOutput(Output output) {
		this.sensorOutput = output;
	}

	@Override
	public Output getSensorOutput() {
		return sensorOutput;
	}

	public void setWeatherController(WeatherController weatherController) {
		this.weatherController = weatherController;
	}

	public WeatherController getWeatherController() {
		return weatherController;
	}

	public void setRandomSeedService(RandomSeedService randomSeedService) {
		this.randomSeedService = randomSeedService;
	}

	public RandomSeedService getRandomSeedService() {
		return randomSeedService;
	}

	public void setEnergyController(EnergyController energyController) {
		this.energyController = energyController;
	}

	public EnergyController getEnergyController() {
		return energyController;
	}
}
