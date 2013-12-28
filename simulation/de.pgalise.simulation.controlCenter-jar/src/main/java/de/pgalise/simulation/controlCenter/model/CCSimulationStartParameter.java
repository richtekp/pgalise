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

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.traffic.AbstractInfrastructureStartParameter;
import de.pgalise.simulation.traffic.BusRoute;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 * All parameters to start the simulation. This is an extra version only for the
 * control center,
 * because not everything can be done on the client side and is not needed by
 * the client.
 *
 * @author Timo
 */
@ManagedBean(name = "startParameter")
@RequestScoped
public class CCSimulationStartParameter extends AbstractInfrastructureStartParameter
{

	private static final long serialVersionUID = 1L;
	/**
	 * Option to start with sensor interferes.
	 * If true, all sensor interferes will be activated.
	 */

	private boolean withSensorInterferes = false;

	/**
	 * Simulation time
	 */
	private long startTimestamp, endTimestamp, interval, clockGeneratorInterval;

	/**
	 * Sensor update steps
	 */
	private long sensorUpdateSteps;

	/**
	 * IPs for all the controllers.
	 */
	@ManagedProperty(value = "127.0.0.1")
	private String ipSimulationController;
	private String ipTrafficController;
	private String ipWeatherController;
	private String ipStaticSensorController;
	private String ipEnergyController;

	/**
	 * IPs for all the traffic servers.
	 */
	private List<String> trafficServerIPList;

	/**
	 * Sensorhelper list.
	 */
	private List<Sensor<?, ?>> sensorHelperList;

	/**
	 * list of simulation event lists
	 */
	private List<EventList<?>> simulationEventLists;

	private OSMAndBusstopFileData oSMAndBusstopFileData = new OSMAndBusstopFileData();

	/**
	 * To create random vehicles.
	 */
	private RandomVehicleBundle randomDynamicSensorBundle;

	/**
	 * List of bus routes
	 */
	private List<BusRoute> busRouteList;

	/**
	 * URL of the operation center
	 */
	private String operationCenterAddress;

	/**
	 * URL of the control center.
	 */
	private String controlCenterAddress;

	/**
	 * Traffic Fuzzy Data
	 */
	private TrafficFuzzyData trafficFuzzyData;

	/**
	 * Attractions during the simulation.
	 */
	private Collection<AttractionData> attractionCollection;
	/**
	 * A name of the simulation (defaults to the name of the associated city)
	 */
	private String name;

	private StartParameterOriginEnum startParameterOriginEnum = null;

	/**
	 * a placeholder for uploaded serialized instances used by {@link #overwriteWithImportedInstanceProperties()
	 * }
	 */
	private String importedInstanceFileContent = null;
	
	public CCSimulationStartParameter() {
	}

	/**
	 * Constructor
	 *
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param interval
	 * @param clockGeneratorInterval
	 *                                     the clock generator interval is an amount of time to wait after every
	 *                                     update.
	 * @param ipSimulationController
	 * @param ipTrafficController
	 * @param ipWeatherController
	 * @param ipStaticSensorController
	 * @param ipEnergyController
	 * @param trafficServerIPList
	 * @param city
	 * @param sensorHelperList
	 * @param simulationEventLists
	 * @param weatherEventList
	 * @param osmAndBusstopFileDatas
	 * @param randomDynamicSensorBundle
	 *                                     contains all vehicles which will be created on start
	 * @param withSensorInterferes
	 *                                     option to start with sensor interferes. If true, all sensor interferes will
	 *                                     be activated.
	 * @param sensorUpdateSteps
	 * @param aggregatedWeatherDataEnabled
	 *                                     shall the weather controller use aggregated data or real measured values
	 * @param busRoutes
	 *                                     all bus routes which shall be used
	 * @param operationCenterAddress
	 *                                     url of the operation center
	 * @param attractionCollection
	 * @param trafficFuzzyData
	 * @param controlCenterAddress
	 *                                     url of the control center
	 */
	public CCSimulationStartParameter(long startTimestamp, long endTimestamp,
					long interval, long clockGeneratorInterval,
					String ipSimulationController, String ipTrafficController,
					String ipWeatherController, String ipStaticSensorController,
					String ipEnergyController,
					List<String> trafficServerIPList, City city,
					List<Sensor<?, ?>> sensorHelperList,
					List<EventList<?>> simulationEventLists,
					List<WeatherEvent> weatherEventList,
					//					OSMAndBusstopFileData osmAndBusstopFileDatas,
					OSMAndBusstopFileData oSMAndBusstopFileData,
					RandomVehicleBundle randomDynamicSensorBundle,
					boolean withSensorInterferes,
					long sensorUpdateSteps,
					boolean aggregatedWeatherDataEnabled,
					List<BusRoute> busRoutes,
					String operationCenterAddress,
					TrafficFuzzyData trafficFuzzyData,
					Collection<AttractionData> attractionCollection,
					String controlCenterAddress) {
		super(city,
						aggregatedWeatherDataEnabled,
						weatherEventList);
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.interval = interval;
		this.ipSimulationController = ipSimulationController;
		this.ipTrafficController = ipTrafficController;
		this.ipWeatherController = ipWeatherController;
		this.ipStaticSensorController = ipStaticSensorController;
		this.ipEnergyController = ipEnergyController;
		this.trafficServerIPList = trafficServerIPList;
		this.sensorHelperList = sensorHelperList;
		this.simulationEventLists = simulationEventLists;
		this.randomDynamicSensorBundle = randomDynamicSensorBundle;
		this.withSensorInterferes = withSensorInterferes;
		this.busRouteList = busRoutes;
		this.operationCenterAddress = operationCenterAddress;
		this.trafficFuzzyData = trafficFuzzyData;
		this.attractionCollection = attractionCollection;
		this.controlCenterAddress = controlCenterAddress;
		this.name = city.getName();
		this.oSMAndBusstopFileData = oSMAndBusstopFileData;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public TrafficFuzzyData getTrafficFuzzyData() {
		return trafficFuzzyData;
	}

	public long getSensorUpdateSteps() {
		return sensorUpdateSteps;
	}

	public void setSensorUpdateSteps(long sensorUpdateSteps) {
		this.sensorUpdateSteps = sensorUpdateSteps;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public String getIpSimulationController() {
		return ipSimulationController;
	}

	public void setIpSimulationController(String ipSimulationController) {
		this.ipSimulationController = ipSimulationController;
	}

	public String getIpTrafficController() {
		return ipTrafficController;
	}

	public void setIpTrafficController(String ipTrafficController) {
		this.ipTrafficController = ipTrafficController;
	}

	public String getIpWeatherController() {
		return ipWeatherController;
	}

	public void setIpWeatherController(String ipWeatherController) {
		this.ipWeatherController = ipWeatherController;
	}

	public List<String> getTrafficServerIPList() {
		return trafficServerIPList;
	}

	public void setTrafficServerIPList(List<String> trafficServerIPList) {
		this.trafficServerIPList = trafficServerIPList;
	}

	public List<Sensor<?, ?>> getSensorHelperList() {
		return sensorHelperList;
	}

	public void setSensorHelperList(List<Sensor<?, ?>> sensorHelperList) {
		this.sensorHelperList = sensorHelperList;
	}

	public List<EventList<?>> getSimulationEventLists() {
		return simulationEventLists;
	}

	public void setSimulationEventLists(
					List<EventList<?>> simulationEventLists) {
		this.simulationEventLists = simulationEventLists;
	}

	public RandomVehicleBundle getRandomDynamicSensorBundle() {
		return randomDynamicSensorBundle;
	}

	public void setRandomDynamicSensorBundle(
					RandomVehicleBundle randomDynamicSensorBundle) {
		this.randomDynamicSensorBundle = randomDynamicSensorBundle;
	}

	public boolean isWithSensorInterferes() {
		return withSensorInterferes;
	}

	public void setWithSensorInterferes(boolean withSensorInterferes) {
		this.withSensorInterferes = withSensorInterferes;
	}

	public long getClockGeneratorInterval() {
		return clockGeneratorInterval;
	}

	public void setClockGeneratorInterval(long clockGeneratorInterval) {
		this.clockGeneratorInterval = clockGeneratorInterval;
	}

	public List<BusRoute> getBusRoutes() {
		return busRouteList;
	}

	public void setBusRoutes(List<BusRoute> busRoutes) {
		this.busRouteList = busRoutes;
	}

	public String getOperationCenterAddress() {
		return operationCenterAddress;
	}

	public void setOperationCenterAddress(String operationCenterAddress) {
		this.operationCenterAddress = operationCenterAddress;
	}

	public String getIpStaticSensorController() {
		return ipStaticSensorController;
	}

	public void setIpStaticSensorController(String ipStaticSensorController) {
		this.ipStaticSensorController = ipStaticSensorController;
	}

	public String getIpEnergyController() {
		return ipEnergyController;
	}

	public void setIpEnergyController(String ipEnergyController) {
		this.ipEnergyController = ipEnergyController;
	}

	public Collection<AttractionData> getAttractionCollection() {
		return attractionCollection;
	}

	public void setAttractionCollection(
					Collection<AttractionData> attractionCollection) {
		this.attractionCollection = attractionCollection;
	}

	public void setTrafficFuzzyData(TrafficFuzzyData trafficFuzzyData) {
		this.trafficFuzzyData = trafficFuzzyData;
	}

	public String getControlCenterAddress() {
		return controlCenterAddress;
	}

	public void setControlCenterAddress(String controlCenterAddress) {
		this.controlCenterAddress = controlCenterAddress;
	}

	public void setoSMAndBusstopFileData(
					OSMAndBusstopFileData oSMAndBusstopFileData) {
		this.oSMAndBusstopFileData = oSMAndBusstopFileData;
	}

	public OSMAndBusstopFileData getoSMAndBusstopFileData() {
		return oSMAndBusstopFileData;
	}

	/**
	 * @return the startParameterOriginEnum
	 */
	public StartParameterOriginEnum getStartParameterOriginEnum() {
		return startParameterOriginEnum;
	}

	/**
	 * @param startParameterOriginEnum the startParameterOriginEnum to set
	 */
	public void setStartParameterOriginEnum(
					StartParameterOriginEnum startParameterOriginEnum) {
		this.startParameterOriginEnum = startParameterOriginEnum;
	}

	/**
	 * handles the import of a serialized {@link CCSimulationStartParameter} using
	 * the serialized instance passed to <tt>importedInstanceFileContent</tt>
	 */
	/*
	 this allows to avoid the a handler for the selected start parameter because 
	 the values can be written into 
	 */
	public void overwriteWithImportedInstanceProperties() {
		throw new UnsupportedOperationException("implement");
	}

	/**
	 * @return the importedInstanceFileContent
	 */
	public String getImportedInstanceFileContent() {
		return importedInstanceFileContent;
	}

	/**
	 * @param importedInstanceFileContent the importedInstanceFileContent to set
	 */
	public void setImportedInstanceFileContent(
					String importedInstanceFileContent) {
		this.importedInstanceFileContent = importedInstanceFileContent;
	}
}
