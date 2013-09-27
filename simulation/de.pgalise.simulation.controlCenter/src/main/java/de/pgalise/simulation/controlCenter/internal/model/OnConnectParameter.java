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
 
package de.pgalise.simulation.controlCenter.internal.model;

import de.pgalise.simulation.traffic.BusRoute;
import java.util.List;
import java.util.Map;

/**
 * Parameters that are needed on connect.
 * This is useful, if new sensors, vehicletypes or vehicle models are added to the simulation.
 * Furthermore it's also useful to inform the client about possible bus routes, saved start parameters,
 * available bus stops and openstreetmap resource files.
 * @author Timo
 */
public class OnConnectParameter {
	
	private List<String> osmResourcesList, busstopResourcesList;
	/**
	 * Map<Integer = enum ordinate, String enum name>
	 */
	private Map<Integer, String> sensorTypeMap;
	private Map<Integer, String> weatherEventTypeMap;
	private Map<Integer, String> simulationEventTypeMap;
	private Map<Integer, String> vehicleTypeMap;
	private Map<Integer, String> vehicleModelMap;
	private Map<String, String> vehicleTypeRandomModelMap;
	
	private List<SavedStartParameterData> savedStartParameterList;
	private List<BusRoute> busRouteList;
	
	/**
	 * Constructor
	 * @param osmResourcesList
	 * @param busstopResourcesList
	 * @param sensorTypeMap
	 * @param savedStartParameterList
	 * @param weatherEventTypeMap
	 * @param simulationEventTypeMap
	 * @param vehicleTypeMap
	 * @param vehicleModelMap
	 * @param vehicleTypeRandomModelMap
	 * @param busRoutesList
	 */
	public OnConnectParameter(List<String> osmResourcesList,
			List<String> busstopResourcesList,
			Map<Integer, String> sensorTypeMap,
			List<SavedStartParameterData> savedStartParameterList, 
			Map<Integer, String> weatherEventTypeMap,
			Map<Integer, String> simulationEventTypeMap,
			Map<Integer, String> vehicleTypeMap,
			Map<Integer, String> vehicleModelMap,
			Map<String, String> vehicleTypeRandomModelMap,
			List<BusRoute> busRoutesList) {
		this.osmResourcesList = osmResourcesList;
		this.busstopResourcesList = busstopResourcesList;
		this.sensorTypeMap = sensorTypeMap;
		this.savedStartParameterList = savedStartParameterList;
		this.weatherEventTypeMap = weatherEventTypeMap;
		this.simulationEventTypeMap = simulationEventTypeMap;
		this.vehicleTypeMap = vehicleTypeMap;
		this.vehicleModelMap = vehicleModelMap;
		this.vehicleTypeRandomModelMap = vehicleTypeRandomModelMap;
		this.busRouteList = busRoutesList;
	}

	public List<SavedStartParameterData> getSavedStartParameterList() {
		return savedStartParameterList;
	}

	public void setSavedStartParameterList(
			List<SavedStartParameterData> savedStartParameterList) {
		this.savedStartParameterList = savedStartParameterList;
	}

	public List<String> getOsmResourcesList() {
		return osmResourcesList;
	}
	
	public void setOsmResourcesList(List<String> osmResourcesList) {
		this.osmResourcesList = osmResourcesList;
	}
	
	public List<String> getBusstopResourcesList() {
		return busstopResourcesList;
	}
	
	public void setBusstopResourcesList(List<String> busstopResourcesList) {
		this.busstopResourcesList = busstopResourcesList;
	}
	
	public Map<Integer, String> getSensorTypeMap() {
		return sensorTypeMap;
	}
	
	public void setSensorTypeMap(Map<Integer, String> sensorTypeMap) {
		this.sensorTypeMap = sensorTypeMap;
	}

	public Map<Integer, String> getWeatherEventTypeMap() {
		return weatherEventTypeMap;
	}

	public void setWeatherEventTypeMap(Map<Integer, String> weatherEventTypeMap) {
		this.weatherEventTypeMap = weatherEventTypeMap;
	}

	public Map<Integer, String> getSimulationEventTypeMap() {
		return simulationEventTypeMap;
	}

	public void setSimulationEventTypeMap(
			Map<Integer, String> simulationEventTypeMap) {
		this.simulationEventTypeMap = simulationEventTypeMap;
	}

	public Map<Integer, String> getVehicleTypeMap() {
		return vehicleTypeMap;
	}

	public void setVehicleTypeMap(Map<Integer, String> vehicleTypeMap) {
		this.vehicleTypeMap = vehicleTypeMap;
	}

	public Map<Integer, String> getVehicleModelMap() {
		return vehicleModelMap;
	}

	public void setVehicleModelMap(Map<Integer, String> vehicleModelMap) {
		this.vehicleModelMap = vehicleModelMap;
	}

	public Map<String, String> getVehicleTypeRandomModelMap() {
		return vehicleTypeRandomModelMap;
	}

	public void setVehicleTypeRandomModelMap(
			Map<String, String> vehicleTypeRandomModelMap) {
		this.vehicleTypeRandomModelMap = vehicleTypeRandomModelMap;
	}

	public List<BusRoute> getBusRouteList() {
		return busRouteList;
	}

	public void setBusRouteList(List<BusRoute> busRouteList) {
		this.busRouteList = busRouteList;
	}
}
