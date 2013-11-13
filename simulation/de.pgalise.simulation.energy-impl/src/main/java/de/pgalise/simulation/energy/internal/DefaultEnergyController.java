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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.energy.EnergyConsumptionManager;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.energy.EnergyEventStrategy;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.service.StatusEnum;
import de.pgalise.simulation.shared.city.Building;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.traffic.InfrastructureInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.weather.service.WeatherController;

/**
 * Default implementation of an energy controller.
 * It uses the specified {@link EnergyEventStrategy} and {@link EnergyConsumptionManager}
 * to calculate the energy consumption. The dependencies can be changed in 'META-INF/ejb-jar.xml'.
 * 
 * @author Timo
 * @version 1.0
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.energy.EnergyController")
@Local(EnergyControllerLocal.class)
public class DefaultEnergyController extends AbstractController<EnergyEvent, InfrastructureStartParameter, InfrastructureInitParameter> implements
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
		private Coordinate geoLocation;
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
		GeoRadiusWrapper(Coordinate geoLocation,
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
			if (measureRadiusInMeter != other.measureRadiusInMeter) {
				return false;
			}
			return true;
		}

		public Coordinate getGeoLocation() {
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

		public void setGeoLocation(Coordinate geoLocation) {
			this.geoLocation = geoLocation;
		}

		public void setMeasureRadiusInMeter(int measureRadiusInMeter) {
			this.measureRadiusInMeter = measureRadiusInMeter;
		}
	}

	private static final String NAME = "EnergyController";
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEnergyController.class);
	
	/**
	 * City of the current simulation
	 */
	private City city;

	/**
	 * city Infrastructure
	 */
	private CityInfrastructureData cityInfrastructure;

	/**
	 * Current timestamp
	 */
	private long currentTimestamp;

	/**
	 * EnergyConsumptionManager
	 */
	@EJB
	private EnergyConsumptionManager energyConsumptionManager;

	@EJB
	private ServiceDictionary serviceDictionary;

	@EJB
	private EnergyEventStrategy energyEventStrategy;

	private Map<GeoRadiusWrapper, Map<EnergyProfileEnum, List<Building>>> buildingsMap;
	
	private InfrastructureInitParameter initParameter;

	protected DefaultEnergyController() {
	}
	
	/**
	 * Default
	 */
	public DefaultEnergyController(CityInfrastructureData cityInfrastructureData) {
		this.cityInfrastructure = cityInfrastructureData;
		this.buildingsMap = new HashMap<>();
	}

	public City getCity() {
		return this.city;
	}

	public CityInfrastructureData getCityInfrastructure() {
		return this.cityInfrastructure;
	}

	public long getCurrentTimestamp() {
		return this.currentTimestamp;
	}

	@Override
	public double getEnergyConsumptionInKWh(long timestamp,
			Coordinate position, int measureRadiusInMeter) {
		if (this.getStatus() != StatusEnum.STARTED) {
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
			this.buildingsMap.put(geoRadiusWrapper, list);
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

	public EnergyConsumptionManager getEnergyConsumptionManager() {
		return this.energyConsumptionManager;
	}

	public EnergyEventStrategy getEnergyEventStrategy() {
		return energyEventStrategy;
	}

	public ServiceDictionary getServiceDictionary() {
		return serviceDictionary;
	}

	@Override
	protected void onInit(InfrastructureInitParameter param) throws InitializationException {
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
	protected void onStart(InfrastructureStartParameter param) {
		this.city = param.getCity();
		this.cityInfrastructure = this.initParameter.getCityInfrastructureData();
		this.energyConsumptionManager.init(this.initParameter.getStartTimestamp(),
				this.initParameter.getEndTimestamp(),
				this.serviceDictionary.getController(WeatherController.class));
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
		this.energyEventStrategy.update(this.currentTimestamp, energyEventList);
	}

	public void setCity(City city) {
		this.city = city;
	}

	public void setCityInfrastructure(CityInfrastructureData cityInfrastructure) {
		this.cityInfrastructure = cityInfrastructure;
	}

	public void setCurrentTimestamp(long currentTimestamp) {
		this.currentTimestamp = currentTimestamp;
	}

	public void setEnergyConsumptionManager(
			EnergyConsumptionManager energyConsumptionManager) {
		this.energyConsumptionManager = energyConsumptionManager;
	}

	public void setEnergyEventStrategy(EnergyEventStrategy energyEventStrategy) {
		this.energyEventStrategy = energyEventStrategy;
	}

	public void setServiceDictionary(ServiceDictionary serviceDictionary) {
		this.serviceDictionary = serviceDictionary;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
