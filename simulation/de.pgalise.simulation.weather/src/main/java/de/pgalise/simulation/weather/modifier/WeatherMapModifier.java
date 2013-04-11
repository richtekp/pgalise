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
 
package de.pgalise.simulation.weather.modifier;

import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.Random;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;

/**
 * Abstract super class for weather modifiers. (Decorator pattern (http://de.wikipedia.org/wiki/Decorator) and strategy
 * pattern).
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (02.07.2012)
 */
public abstract class WeatherMapModifier extends WeatherMap implements WeatherStrategy {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -5783884911469378482L;

	/**
	 * Round the value
	 * 
	 * @param value
	 *            Value
	 * @param digits
	 *            Number of decimal places
	 * @return rounded value
	 */
	public static double round(double value, int digits) {
		double rValue = Math.round(value * Math.pow(10d, digits));
		return rValue / Math.pow(10d, digits);
	}

	/**
	 * Round the value
	 * 
	 * @param value
	 *            Value
	 * @param digits
	 *            Number of decimal places
	 * @return rounded value
	 */
	public static float round(float value, int digits) {
		float rValue = Math.round(value * Math.pow(10d, digits));
		return (float) (rValue / Math.pow(10d, digits));
	}

	/**
	 * City
	 */
	protected City city;

	/**
	 * Current simulation timestamp
	 */
	protected long simulationTimestamp;

	/**
	 * WeatherMap
	 */
	protected WeatherMap map;

	/**
	 * Order id
	 */
	protected int orderID;

	/**
	 * Properties
	 */
	protected Properties props = null;

	/**
	 * Random generator
	 */
	protected Random randomGen;

	/**
	 * Weather loader
	 */
	protected WeatherLoader weatherLoader;

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 * @param props
	 *            Properties
	 */
	public WeatherMapModifier(long seed, Properties props, WeatherLoader weatherLoader) {
		this.randomGen = new Random(seed);
		this.props = props;
		this.weatherLoader = weatherLoader;

		// Init
		this.initDecorator();
	}

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherMapModifier(long seed, WeatherLoader weatherLoader) {
		this.randomGen = new Random(seed);
		this.weatherLoader = weatherLoader;
		// Init
		this.initDecorator();
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 *            WeatherMap
	 * @param seed
	 *            Seed for random generators
	 */
	public WeatherMapModifier(WeatherMap map, long seed, WeatherLoader weatherLoader) {
		this.map = map;
		this.randomGen = new Random(seed);
		this.weatherLoader = weatherLoader;

		// Init
		this.initDecorator();
	}

	/*
	 * (non-Javadoc)
	 * @see de.pgalise.simulation.weather.database.Weather#deployChanges()
	 */
	@Override
	public void deployChanges() {
		this.map.deployChanges();
	}

	public WeatherMap getMap() {
		return this.map;
	}

	@Override
	public int getOrderID() {
		return this.orderID;
	}

	public long getSimulationTimestamp() {
		return this.simulationTimestamp;
	}

	public WeatherLoader getWeatherLoader() {
		return this.weatherLoader;
	}

	@Override
	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public void setMap(WeatherMap map) {
		this.map = map;
	}

	@Override
	public void setSimulationTimestamp(long timestamp) {
		this.simulationTimestamp = timestamp;
	}

	public void setWeatherLoader(WeatherLoader weatherLoader) {
		this.weatherLoader = weatherLoader;
	}

	/**
	 * Returns the max of the map
	 * 
	 * @param comparator
	 *            Comparator
	 * @return Max value
	 */
	protected Weather getMaxValue(Comparator<Weather> comparator) {
		return Collections.max(this.map.values(), comparator);
	}

	/**
	 * Returns the min of the map
	 * 
	 * @param comparator
	 *            Comparator
	 * @return Min value
	 */
	protected Weather getMinValue(Comparator<Weather> comparator) {
		return Collections.min(this.map.values(), comparator);
	}

	/**
	 * Returns a random float value between 0 and max
	 * 
	 * @param max
	 *            Max value
	 * @return random float value
	 */
	protected float getRandomDouble(int max) {
		Random r = this.randomGen;
		int v1 = r.nextInt(max);
		double v2 = Math.round(r.nextDouble() * 1000.) / 1000.;

		return v1 + (float) v2;
	}

	/**
	 * Returns a random int value between 0 and max
	 * 
	 * @param max
	 *            Max value
	 * @return Random value
	 */
	protected int getRandomInt(int max) {
		return this.randomGen.nextInt(max);
	}

	/**
	 * Initiate the decorator
	 */
	protected abstract void initDecorator();
}
