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
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersectionSensor;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import de.pgalise.staticsensor.internal.sensor.energy.SmartMeterSensor;
import de.pgalise.staticsensor.internal.sensor.energy.WindPowerSensor;
import de.pgalise.staticsensor.internal.sensor.weather.Anemometer;
import de.pgalise.staticsensor.internal.sensor.weather.Barometer;
import de.pgalise.staticsensor.internal.sensor.weather.Hygrometer;
import de.pgalise.staticsensor.internal.sensor.weather.Luxmeter;
import de.pgalise.staticsensor.internal.sensor.weather.Pyranometer;
import de.pgalise.staticsensor.internal.sensor.weather.RainSensor;
import de.pgalise.staticsensor.internal.sensor.weather.Thermometer;
import de.pgalise.staticsensor.internal.sensor.weather.WeatherStation;
import de.pgalise.staticsensor.internal.sensor.weather.WindFlagSensor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlTransient;
import javolution.xml.XMLSerializable;

/**
 * All parameters to start the simulation. This is an extra version only for the
 * control center, because not everything can be done on the client side and is
 * not needed by the client.
 *
 * @author Timo
 */
@ManagedBean
@SessionScoped
/*
 make this class a managed bean in order to specify validation constraints only once and to not be obliged to duplicate all fields in a start parameter controller
 */
public class ControlCenterStartParameter extends TrafficStartParameter
	implements XMLSerializable {

	public final static Set<Class<? extends Sensor>> SUPPORTED_SENSOR_TYPES = new HashSet<Class<? extends Sensor>>(
		Arrays.asList(GpsSensor.class,
			InductionLoopSensor.class,
			TopoRadarSensor.class,
			TrafficLightSensor.class,
			PhotovoltaikSensor.class,
			SmartMeterSensor.class,
			WindPowerSensor.class,
			TrafficLightIntersectionSensor.class,
			Anemometer.class,
			Barometer.class,
			Hygrometer.class,
			Luxmeter.class,
			Pyranometer.class,
			RainSensor.class,
			Thermometer.class,
			WeatherStation.class,
			WindFlagSensor.class,
			InfraredSensor.class));

	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport mPcs
		= new PropertyChangeSupport(this);

	/**
	 * Option to start with sensor interferes. If true, all sensor interferes will
	 * be activated.
	 */
	private boolean withSensorInterferes = false;

	/**
	 * Sensor update steps
	 */
	private long sensorUpdateSteps;

	/**
	 * IPs for all the controllers.
	 */
	private String ipSimulationController = "127.0.0.1";
	private String ipTrafficController = "127.0.0.1";
	private String ipWeatherController = "127.0.0.1";
	private String ipStaticSensorController = "127.0.0.1";
	private String ipEnergyController = "127.0.0.1";

	private int trafficServerCount = 2;

	/**
	 * Sensorhelper list.
	 */
	@XmlTransient
	private Set<Sensor<?, ?>> sensors;

	/**
	 * list of simulation event lists
	 */
	private List<EventList<?>> simulationEventLists;

	private MapAndBusstopFileData mapAndBusstopFileData;

	/**
	 * To create random vehicles.
	 */
	private RandomVehicleBundle randomDynamicSensorBundle;

	/**
	 * List of bus routes
	 */
	private List<BusRoute> busRouteList;

	/**
	 * Attractions during the simulation.
	 */
	private Collection<AttractionData> attractionCollection;
	/**
	 * A name of the simulation (defaults to the name of the associated city)
	 */
	private String name = "Oldenburg";

	private StartParameterOriginEnum startParameterOriginEnum = StartParameterOriginEnum.CREATED;

	/**
	 * a placeholder for uploaded serialized instances used by {@link #overwriteWithImportedInstanceProperties()
	 * }
	 */
	private String importedInstanceFileContent = null;

	private Map<Class<? extends Sensor>, Long> specificUpdateSteps = new HashMap<>();
  @Min(1024) @Max(65536)
  private int outputPort = TcpIpOutput.PORT_DEFAULT;
        
        private TrafficFuzzyData trafficFuzzyData;

	public ControlCenterStartParameter() {
		for (Class<? extends Sensor> supportedSensorType : SUPPORTED_SENSOR_TYPES) {
			specificUpdateSteps.put(supportedSensorType,
				1L);
		}
	}

	/**
	 * Constructor
	 *
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param interval
	 * @param clockGeneratorInterval the clock generator interval is an amount of
	 * time to wait after every update.
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
	 * @param randomDynamicSensorBundle contains all vehicles which will be
	 * created on start
	 * @param withSensorInterferes option to start with sensor interferes. If
	 * true, all sensor interferes will be activated.
	 * @param sensorUpdateSteps
	 * @param aggregatedWeatherDataEnabled shall the weather controller use
	 * aggregated data or real measured values
	 * @param busRoutes all bus routes which shall be used
	 * @param operationCenterAddress url of the operation center
	 * @param attractionCollection
	 * @param trafficFuzzyData
	 * @param controlCenterAddress url of the control center
	 */
	public ControlCenterStartParameter(long startTimestamp,
		long endTimestamp,
		long interval,
		long clockGeneratorInterval,
		String ipSimulationController,
		String ipTrafficController,
		String ipWeatherController,
		String ipStaticSensorController,
		String ipEnergyController,
		int trafficServerCount,
		TrafficCity city,
		Set<Sensor<?, ?>> sensorHelperList,
		List<EventList<?>> simulationEventLists,
		List<WeatherEvent> weatherEventList,
		//					MapAndBusstopFileData osmAndBusstopFileDatas,
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
		this.ipSimulationController = ipSimulationController;
		this.ipTrafficController = ipTrafficController;
		this.ipWeatherController = ipWeatherController;
		this.ipStaticSensorController = ipStaticSensorController;
		this.ipEnergyController = ipEnergyController;
		this.sensors = sensorHelperList;
		this.simulationEventLists = simulationEventLists;
		this.randomDynamicSensorBundle = randomDynamicSensorBundle;
		this.withSensorInterferes = withSensorInterferes;
		this.busRouteList = busRoutes;
		this.attractionCollection = attractionCollection;
		this.name = city.getName();
		this.mapAndBusstopFileData = mapAndBusstopFileData;
		this.trafficServerCount = trafficServerCount;
                this.trafficFuzzyData = trafficFuzzyData;
	}

	/**
	 * handles the import of a serialized {@link ControlCenterStartParameter}
	 * using the serialized instance passed to
	 * <tt>importedInstanceFileContent</tt>
	 */
	/*
	 this allows to avoid the a handler for the selected start parameter because 
	 the values can be written into 
	 */
	public void overwriteWithImportedInstanceProperties() {
		throw new UnsupportedOperationException("implement");
	}

	public void
		addPropertyChangeListener(PropertyChangeListener listener) {
		mPcs.addPropertyChangeListener(listener);
	}

	public void
		removePropertyChangeListener(PropertyChangeListener listener) {
		mPcs.removePropertyChangeListener(listener);
	}

//	public static class XML extends XMLFormat<ControlCenterStartParameter> {
//
//		@Override
//		public void write(ControlCenterStartParameter g,
//			OutputElement xml) throws XMLStreamException {
//			xml.setAttribute("withSensorInterferes",
//				g.withSensorInterferes);
//			xml.setAttribute("startTimestamp",
//				g.startTimestamp);
//			xml.setAttribute("endTimestamp",
//				g.endTimestamp);
//			xml.setAttribute("interval",
//				g.interval);
//			xml.setAttribute("clockGeneratorInterval",
//				g.clockGeneratorInterval);
//			xml.setAttribute("sensorUpdateSteps",
//				g.sensorUpdateSteps);
//			xml.setAttribute("ipSimulationController",
//				g.ipSimulationController);
//			xml.setAttribute("ipTrafficController",
//				g.ipTrafficController);
//			xml.setAttribute("ipWeatherController",
//				g.ipWeatherController);
//			xml.setAttribute("ipStaticSensorController",
//				g.ipStaticSensorController);
//			xml.setAttribute("ipEnergyController",
//				g.ipEnergyController);
//			xml.setAttribute("operationCenterAddress",
//				g.operationCenterAddress);
//			xml.setAttribute("name",
//				g.name);
//			xml.add(g.trafficServerIPs);
//			xml.add(g.simulationEventLists);
//			xml.add(g.mapAndBusstopFileData);
//			xml.add(g.randomDynamicSensorBundle);
//			xml.add(g.busRouteList);
//			xml.add(g.trafficFuzzyData);
//			xml.add(g.attractionCollection);
//			xml.add(g.specificUpdateSteps);
//		}
//
//		@Override
//		public void read(InputElement xml,
//			ControlCenterStartParameter g) throws XMLStreamException {
//			throw new UnsupportedOperationException();
//		}
//	}
    
  //////////////////////////////////////////////////////////////////////////////
  // getter and setter
  //////////////////////////////////////////////////////////////////////////////

	public void setSpecificUpdateSteps(
		Map<Class<? extends Sensor>, Long> specificUpdateSteps) {
		Map<Class<? extends Sensor>, Long> oldSpecificUpdateSteps = specificUpdateSteps;
		this.specificUpdateSteps = specificUpdateSteps;
		mPcs.firePropertyChange("mouthWidth",
			oldSpecificUpdateSteps,
			specificUpdateSteps);
	}

	public Map<Class<? extends Sensor>, Long> getSpecificUpdateSteps() {
		return specificUpdateSteps;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setTrafficServerCount(int trafficServerCount) {
		this.trafficServerCount = trafficServerCount;
	}

	public int getTrafficServerCount() {
		return trafficServerCount;
	}

	public long getSensorUpdateSteps() {
		return sensorUpdateSteps;
	}

	public void setSensorUpdateSteps(long sensorUpdateSteps) {
		this.sensorUpdateSteps = sensorUpdateSteps;
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

	@XmlTransient
	public Set<Sensor<?, ?>> getSensors() {
		return sensors;
	}

	public void setSensors(Set<Sensor<?, ?>> sensors) {
		this.sensors = sensors;
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

	public List<BusRoute> getBusRoutes() {
		return busRouteList;
	}

	public void setBusRoutes(List<BusRoute> busRoutes) {
		this.busRouteList = busRoutes;
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

	public void setMapAndBusstopFileData(
		MapAndBusstopFileData mapAndBusstopFileData) {
		this.mapAndBusstopFileData = mapAndBusstopFileData;
	}

	public MapAndBusstopFileData getMapAndBusstopFileData() {
		return mapAndBusstopFileData;
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

    public void setTrafficFuzzyData(TrafficFuzzyData trafficFuzzyData) {
        this.trafficFuzzyData = trafficFuzzyData;
    }

    public TrafficFuzzyData getTrafficFuzzyData() {
        return trafficFuzzyData;
    }
}
