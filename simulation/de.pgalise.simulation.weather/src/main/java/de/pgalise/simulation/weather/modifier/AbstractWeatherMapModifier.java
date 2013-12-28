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
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.model.StationData;
import de.pgalise.simulation.weather.model.WeatherCondition;

/**
 * Abstract super class for weather modifiers. (Decorator pattern (http://de.wikipedia.org/wiki/Decorator) and strategy
 * pattern).
 * 
 * @param <C> 
 * @author Andreas Rehfeldt
 * @version 1.0 (02.07.2012)
 */
public abstract class AbstractWeatherMapModifier extends WeatherMap implements WeatherMapModifier {

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
	private City city;

	/**
	 * Current simulation timestamp
	 */
	private long simulationTimestamp;

	/**
	 * WeatherMap
	 */
	private WeatherMap map;

	/**
	 * Order id
	 */
	private int orderID;

	/**
	 * Properties
	 */
	private Properties props = null;

	/**
	 * Random generator
	 */
	private Random randomGen;

	/**
	 * Weather loader
	 */
	private WeatherLoader weatherLoader;

	/**
	 * Constructor
	 * 
	 * @param seed
	 *            Seed for random generators
	 * @param props
	 *            Properties
	 * @param weatherLoader  
	 */
	public AbstractWeatherMapModifier(long seed, Properties props, WeatherLoader weatherLoader) {
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
	 * @param weatherLoader  
	 */
	public AbstractWeatherMapModifier(long seed, WeatherLoader weatherLoader) {
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
	 * @param weatherLoader  
	 */
	public AbstractWeatherMapModifier(WeatherMap map, long seed, WeatherLoader weatherLoader) {
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
		this.getMap().deployChanges();
	}

	@Override
	public WeatherMap getMap() {
		return this.map;
	}

	@Override
	public int getOrderID() {
		return this.orderID;
	}

	@Override
	public long getSimulationTimestamp() {
		return this.simulationTimestamp;
	}

	@Override
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
	protected StationData getMaxValue(Comparator<StationData> comparator) {
		return Collections.max(this.getMap().values(), comparator);
	}

	/**
	 * Returns the min of the map
	 * 
	 * @param comparator
	 *            Comparator
	 * @return Min value
	 */
	protected StationData getMinValue(Comparator<StationData> comparator) {
		return Collections.min(this.getMap().values(), comparator);
	}

	/**
	 * Returns a random float value between 0 and max
	 * 
	 * @param max
	 *            Max value
	 * @return random float value
	 */
	protected float getRandomDouble(int max) {
		Random r = this.getRandomGen();
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
		return this.getRandomGen().nextInt(max);
	}

	/**
	 * Initiate the decorator
	 */
	protected abstract void initDecorator();

	/**
	 * @return the city
	 */
	public City getCity() {
		return city;
	}

	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	/**
	 * @return the props
	 */
	public Properties getProps() {
		return props;
	}

	/**
	 * @param props the props to set
	 */
	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * @return the randomGen
	 */
	public Random getRandomGen() {
		return randomGen;
	}

	/**
	 * @param randomGen the randomGen to set
	 */
	public void setRandomGen(Random randomGen) {
		this.randomGen = randomGen;
	}
}
