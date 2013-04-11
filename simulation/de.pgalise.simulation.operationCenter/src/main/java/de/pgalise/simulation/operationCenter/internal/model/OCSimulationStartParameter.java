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
 
package de.pgalise.simulation.operationCenter.internal.model;

import java.io.Serializable;

/**
 * SimulationStartParameter only for the operation center.
 * @author Timo
 */
public class OCSimulationStartParameter implements Serializable {
	private static final long serialVersionUID = -1212224094676807334L;
	private String cityName = "";
	private int population;
	
	/**
	 * Default
	 */
	public OCSimulationStartParameter() {}
	
	/**
	 * Constructor
	 * @param cityName
	 * @param population
	 * 			the population of the city
	 */
	public OCSimulationStartParameter(String cityName, int population) {
		this.cityName = cityName;
		this.population = population;

	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}
}
