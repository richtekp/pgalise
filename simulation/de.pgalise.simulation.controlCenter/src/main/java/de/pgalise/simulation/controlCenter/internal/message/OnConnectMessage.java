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
 
package de.pgalise.simulation.controlCenter.internal.message;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.controlCenter.internal.model.OnConnectParameter;
import de.pgalise.simulation.controlCenter.internal.model.SavedStartParameterData;
import de.pgalise.simulation.shared.event.EventTypeEnum;
import de.pgalise.simulation.shared.event.weather.WeatherEventEnum;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;

/**
 * Message send on connect. It will Send the {@link OnConnectParameter} to the user.
 * 
 * @author Timo
 */
public class OnConnectMessage extends CCWebSocketMessage<OnConnectParameter> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OnConnectMessage.class);

	/**
	 * Constructor
	 * 
	 * @param busRouteList
	 * 			bus routes that are available for the city.
	 */
	public OnConnectMessage(List<BusRoute> busRouteList) {
		super(CCWebSocketMessage.MessageType.ON_CONNECT, new OnConnectParameter(getOSMResources(),
				getBusstopResources(), getSensorTypesMap(), getSavedStartParameterResource(),
				getWeatherEventEnumTypeMap(), getSimulationEventEnumTypeMap(), getVehicleTypeEnumTypeMap(),
				getVehicleModelEnumTypeMap(), getVehicleTypeRandomModelMap(), busRouteList));
	}

	/**
	 * Returns all saved start parameter.
	 * 
	 * @return
	 */
	private static List<SavedStartParameterData> getSavedStartParameterResource() {
		List<SavedStartParameterData> savedStartParameterList = new ArrayList<>();
		final Properties properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/properties.props");
		try {
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.warn(e.getMessage());
			}
		}

		String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath()
				.replaceAll("WEB-INF/classes", "");
		File[] files = new File(path).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(properties.getProperty("suffixStartParameterXML"));
			}
		});

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			savedStartParameterList.add(new SavedStartParameterData(file.lastModified(), file.getName().replaceAll(
					"." + properties.getProperty("suffixStartParameterXML"), ""), file.getName()));
		}

		return savedStartParameterList;
	}

	/**
	 * Returns names of the possible busstop resources.
	 * 
	 * @return
	 */
	private static List<String> getBusstopResources() {
		List<String> busstopResourcesList = new ArrayList<>();

		String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
		String[] files = new File(path).list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".gtfs");
			}
		});
		for (int i = 0; i < files.length; i++) {
			busstopResourcesList.add(files[i]);
		}

		return busstopResourcesList;
	}

	/**
	 * Returns names of the possible OSM resources.
	 * 
	 * @return
	 */
	private static List<String> getOSMResources() {
		List<String> osmResources = new ArrayList<>();
		String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
		String[] files = new File(path).list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".osm");
			}
		});
		for (int i = 0; i < files.length; i++) {
			osmResources.add(files[i]);
		}

		return osmResources;
	}

	/**
	 * Returns a map with sensor types.
	 * 
	 * @return
	 */
	private static Map<Integer, String> getSensorTypesMap() {
		Map<Integer, String> map = new HashMap<>();
		Map<Integer, SensorType> sensorTypeMap = SensorType.getSensorTypeIdsAsUnmodifiable();
		for (Entry<Integer, SensorType> entry : sensorTypeMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue().toString());
		}

		return map;
	}

	/**
	 * Returns a map with weather event enum name as value and id as key.
	 * 
	 * @return
	 */
	private static Map<Integer, String> getWeatherEventEnumTypeMap() {
		Map<Integer, String> weatherEventEnumTypeMap = new HashMap<>();
		Map<Integer, WeatherEventEnum> weatherEventEnumMap = WeatherEventEnum.getWeatherEventEnumIdsAsUnmodifiable();
		for (Entry<Integer, WeatherEventEnum> entry : weatherEventEnumMap.entrySet()) {
			weatherEventEnumTypeMap.put(entry.getKey(), entry.getValue().toString());
		}
		return weatherEventEnumTypeMap;
	}

	/**
	 * Returns a map with simulation event enum name as value and id as key.
	 * 
	 * @return
	 */
	private static Map<Integer, String> getSimulationEventEnumTypeMap() {
		Map<Integer, String> simulationEventTypeMap = new HashMap<>();
		Map<Integer, EventTypeEnum> simulationEventEnumMap = EventTypeEnum
				.getSimulationEventTypeMapAsUnmodifiable();
		for (Entry<Integer, EventTypeEnum> entry : simulationEventEnumMap.entrySet()) {
			simulationEventTypeMap.put(entry.getKey(), entry.getValue().toString());
		}
		return simulationEventTypeMap;
	}

	/**
	 * Returns a map with vehicle type enum name as value and id as key.
	 * 
	 * @return
	 */
	private static Map<Integer, String> getVehicleTypeEnumTypeMap() {
		Map<Integer, String> vehicleTypeMap = new HashMap<>();
		Map<Integer, VehicleTypeEnum> vehicleTypeEnumMap = VehicleTypeEnum.getVehicleTypeIdsAsUnmodifiable();
		for (Entry<Integer, VehicleTypeEnum> entry : vehicleTypeEnumMap.entrySet()) {
			vehicleTypeMap.put(entry.getKey(), entry.getValue().toString());
		}
		return vehicleTypeMap;
	}

	/**
	 * Returns a map with vehicle model enum name as value and id as key.
	 * 
	 * @return
	 */
	private static Map<Integer, String> getVehicleModelEnumTypeMap() {
		Map<Integer, String> vehicleModelMap = new HashMap<>();
		Map<Integer, VehicleModelEnum> vehicleModelEnumMap = VehicleModelEnum.getVehicleModelIdsAsUnmodifiable();
		for (Entry<Integer, VehicleModelEnum> entry : vehicleModelEnumMap.entrySet()) {
			vehicleModelMap.put(entry.getKey(), entry.getValue().toString());
		}
		return vehicleModelMap;
	}

	/**
	 * Returns a map with the vehicle type as key and a possible random model as value.
	 * 
	 * @return
	 */
	private static Map<String, String> getVehicleTypeRandomModelMap() {
		Map<String, String> vehicleTypeRandomModelMap = new HashMap<>();
		vehicleTypeRandomModelMap.put(VehicleTypeEnum.BIKE.toString(), VehicleModelEnum.BIKE_RANDOM.toString());
		vehicleTypeRandomModelMap.put(VehicleTypeEnum.BUS.toString(), VehicleModelEnum.BUS_RANDOM.toString());
		vehicleTypeRandomModelMap.put(VehicleTypeEnum.CAR.toString(), VehicleModelEnum.CAR_RANDOM.toString());
		vehicleTypeRandomModelMap.put(VehicleTypeEnum.MOTORCYCLE.toString(),
				VehicleModelEnum.MOTORCYCLE_RANDOM.toString());
		vehicleTypeRandomModelMap.put(VehicleTypeEnum.TRUCK.toString(), VehicleModelEnum.TRUCK_RANDOM.toString());

		return vehicleTypeRandomModelMap;
	}
}
