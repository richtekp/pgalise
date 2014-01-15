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
package de.pgalise.simulation.energy.internal;

import de.pgalise.simulation.energy.EnergyConsumptionManager;
import de.pgalise.simulation.energy.EnergyConsumptionManagerLocal;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.energy.EnergyEventStrategy;
import de.pgalise.simulation.energy.EnergyEventStrategyLocal;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.traffic.service.CityInfrastructureDataService;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.weather.service.WeatherController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of an energy controller. It uses the specified
 * {@link EnergyEventStrategy} and {@link EnergyConsumptionManager} to calculate
 * the energy consumption. The dependencies can be changed in
 * 'META-INF/ejb-jar.xml'.
 *
 * @author Timo
 * @version 1.0
 */
@Singleton(name = "de.pgalise.simulation.energy.EnergyController")
@Local(EnergyControllerLocal.class)
public class DefaultEnergyController extends AbstractController<EnergyEvent, TrafficStartParameter, TrafficInitParameter>
	implements
	EnergyControllerLocal {

	private static final long serialVersionUID = 1L;

	/**
	 * Wrapps geo location and measure radius in meter.
	 *
	 * @author Timo
	 *
	 */
	private static class GeoRadiusWrapper {

		/**
		 * Position
		 */
		private JaxRSCoordinate geoLocation;
		/**
		 * Radius in meter
		 */
		private int measureRadiusInMeter;

		/**
		 * Constructor
		 *
		 * @param geoLocation
		 * @param measureRadiusInMeter
		 */
		GeoRadiusWrapper(JaxRSCoordinate geoLocation,
			int measureRadiusInMeter) {
			this.geoLocation = geoLocation;
			this.measureRadiusInMeter = measureRadiusInMeter;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			GeoRadiusWrapper other = (GeoRadiusWrapper) obj;
			if (geoLocation == null) {
				if (other.geoLocation != null) {
					return false;
				}
			} else if (!geoLocation.equals(other.geoLocation)) {
				return false;
			}
			return measureRadiusInMeter == other.measureRadiusInMeter;
		}

		public JaxRSCoordinate getGeoLocation() {
			return geoLocation;
		}

		public int getMeasureRadiusInMeter() {
			return measureRadiusInMeter;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
				+ ((geoLocation == null) ? 0 : geoLocation.hashCode());
			result = prime * result + measureRadiusInMeter;
			return result;
		}

		public void setGeoLocation(JaxRSCoordinate geoLocation) {
			this.geoLocation = geoLocation;
		}

		public void setMeasureRadiusInMeter(int measureRadiusInMeter) {
			this.measureRadiusInMeter = measureRadiusInMeter;
		}
	}

	private static final String NAME = "EnergyController";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(
		DefaultEnergyController.class);

	/**
	 * City of the current simulation
	 */
	private City city;

	/**
	 * city Infrastructure
	 */
	@EJB
	private CityInfrastructureDataService cityInfrastructure;

	/**
	 * Current timestamp
	 */
	private long currentTimestamp;

	/**
	 * EnergyConsumptionManager
	 */
	@EJB
	private EnergyConsumptionManagerLocal energyConsumptionManager;

	@EJB
	private EnergyEventStrategyLocal energyEventStrategy;

	private Map<GeoRadiusWrapper, Map<EnergyProfileEnum, List<Building>>> buildingsMap = new HashMap<>();

	private TrafficInitParameter initParameter;

	@EJB
	private WeatherController weatherController;

	public DefaultEnergyController() {
	}

	/**
	 * Default
	 *
	 * @param cityInfrastructureData
	 */
	public DefaultEnergyController(
		CityInfrastructureDataService cityInfrastructureData) {
		this.cityInfrastructure = cityInfrastructureData;
		this.buildingsMap = new HashMap<>();
	}

	public City getCity() {
		return this.city;
	}

	public CityInfrastructureDataService getCityInfrastructure() {
		return this.cityInfrastructure;
	}

	public long getCurrentTimestamp() {
		return this.currentTimestamp;
	}

	@Override
	public double getEnergyConsumptionInKWh(long timestamp,
		JaxRSCoordinate position,
		int measureRadiusInMeter) {
		if (this.getStatus() != ControllerStatusEnum.STARTED) {
			throw new IllegalStateException(
				"Controller is not in running state!");
		}

		// Get all buildings
		GeoRadiusWrapper geoRadiusWrapper = new GeoRadiusWrapper(position,
			measureRadiusInMeter);
		Map<EnergyProfileEnum, List<Building>> list = this.buildingsMap
			.get(geoRadiusWrapper);
		if (list == null) {
			list = this.cityInfrastructure.getBuildings(position,
				measureRadiusInMeter);
			this.buildingsMap.put(geoRadiusWrapper,
				list);
		}

		double energyConsumption = 0.0;
		for (java.util.Map.Entry<EnergyProfileEnum, List<Building>> entry : list
			.entrySet()) {
			for (Building building : entry.getValue()) {
				energyConsumption += this.getEnergyConsumptionManager()
					.getEnergyConsumptionInKWh(
						timestamp,
						entry.getKey(),

						building.getPosition().getCenterPoint());
			}
		}

		return energyConsumption;
	}

	public EnergyConsumptionManagerLocal getEnergyConsumptionManager() {
		return this.energyConsumptionManager;
	}

	public EnergyEventStrategyLocal getEnergyEventStrategy() {
		return energyEventStrategy;
	}

	@Override
	protected void onInit(TrafficInitParameter param) throws InitializationException {
		this.buildingsMap.clear();
		this.initParameter = param;
	}

	@Override
	protected void onReset() {
		// Nothing to do
	}

	@Override
	protected void onResume() {
		// Nothing to do

	}

	@Override
	protected void onStart(TrafficStartParameter param) {
		this.city = param.getCity();
		this.energyConsumptionManager.init(this.initParameter.getStartTimestamp().
			getTime(),
			this.initParameter.getEndTimestamp().getTime(),
			weatherController);
	}

	@Override
	protected void onStop() {
		// Nothing to do
	}

	@Override
	protected void onUpdate(EventList<EnergyEvent> simulationEventList) {
		// Save timestamp
		this.setCurrentTimestamp(simulationEventList.getTimestamp());

		List<EnergyEvent> energyEventList = new ArrayList<>();
		for (EnergyEvent event : simulationEventList.getEventList()) {
			energyEventList.add(event);
		}
		this.energyEventStrategy.update(this.currentTimestamp,
			energyEventList);
	}

	public void setCity(City city) {
		this.city = city;
	}

	public void setCityInfrastructure(
		CityInfrastructureDataService cityInfrastructure) {
		this.cityInfrastructure = cityInfrastructure;
	}

	public void setCurrentTimestamp(long currentTimestamp) {
		this.currentTimestamp = currentTimestamp;
	}

	public void setEnergyConsumptionManager(
		EnergyConsumptionManagerLocal energyConsumptionManager) {
		this.energyConsumptionManager = energyConsumptionManager;
	}

	public void setEnergyEventStrategy(
		EnergyEventStrategyLocal energyEventStrategy) {
		this.energyEventStrategy = energyEventStrategy;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
