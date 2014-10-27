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
package de.pgalise.simulation.controlCenter.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the name for open street map and bus stop file. This is used, when the
 * client selects an openstreetmap and bus stop file to load them.
 *
 * @author Timo
 */
public class MapAndBusstopFileData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * This OSM-File will be parsed and used for the street-graph.
	 */
	private Set<String> osmFileNames = new HashSet<>();

	/**
	 * This busstop file will be used for the busstops on the street-graph.
	 */
	private Set<String> busStopFileNames = new HashSet<>();

	public MapAndBusstopFileData() {
	}

	/**
	 * Constructor
	 *
	 * @param osmFileName
	 * @param busStopFileName
	 */
	public MapAndBusstopFileData(Set<String> osmFileName,
		Set<String> busStopFileName) {
		this.osmFileNames = osmFileName;
		this.busStopFileNames = busStopFileName;
	}

	public Set<String> getOsmFileNames() {
		return osmFileNames;
	}

	public void setOsmFileNames(Set<String> osmFileNames) {
		this.osmFileNames = osmFileNames;
	}

	public Set<String> getBusStopFileNames() {
		return busStopFileNames;
	}

	public void setBusStopFileNames(Set<String> busStopFileNames) {
		this.busStopFileNames = busStopFileNames;
	}
}
