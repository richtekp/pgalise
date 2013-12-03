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

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 * Holds the name for open street map and bus stop file.
 * This is used, when the client selects an openstreetmap and bus stop file to load them.
 * @author Timo
 */
@ManagedBean
@ApplicationScoped
public class OSMAndBusstopFileData {
	/**
	 * This OSM-File will be parsed and used for the street-graph.
	 */
	@ManagedProperty(value = "/oldenburg_pg.osm")
	private String osmFileName;
	
	/**
	 * This busstop file will be used for the busstops on the street-graph.
	 */
	@ManagedProperty(value = "/stops.gtfs")
	private String busStopFileName;

	/**
	 * Constructor
	 * @param osmFileName
	 * @param busStopFileName
	 */
	public OSMAndBusstopFileData(String osmFileName, String busStopFileName) {
		this.osmFileName = osmFileName;
		this.busStopFileName = busStopFileName;
	}

	public String getOsmFileName() {
		return osmFileName;
	}

	public void setOsmFileName(String osmFileName) {
		this.osmFileName = osmFileName;
	}

	public String getBusStopFileName() {
		return busStopFileName;
	}

	public void setBusstopFileName(String busstopFileName) {
		this.busStopFileName = busstopFileName;
	}
}
