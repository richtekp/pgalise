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
 
package de.pgalise.simulation.operationCenter.internal.strategy;

import de.pgalise.simulation.sensorFramework.Sensor;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
/**
 * Default implementation of a gate message strategy.
 * It will transmit the needed sensor ids to InfoSphere via TCP/IP.
 * The InfoSphere IP and it's ports can be changed in 'properties.props'.
 * @author Timo
 */
public class DefaultGPSGateStrategy extends AbstractController<Event,InfrastructureStartParameter, TrafficInitParameter> implements GPSGateStrategy {

	private static final Logger log = LoggerFactory.getLogger(DefaultGPSGateStrategy.class);
	private static final long serialVersionUID = 1L;
	private String socketIP;
	private int socketPortRemoveKeys, socketPortAddKeys;
	/**
	 * Contains all sensors
	 * Map<Integer = sensor type, Set<Integer = Sensor id>>
	 */
	private Map<Integer, Set<Sensor>> allSensorTypeSensorIDMap;
	/**
	 * Contains only sensors which are known by InfoSphere
	 * Map<Integer = sensor type, Set<Integer = Sensor id>>
	 */
	private Map<Integer, Set<Sensor>> activeSensorTypeSensorIDMap;
	/**
	 * Contains only sensors which are not known by InfoSphere
	 * Map<Integer = sensor type, Set<Integer = Sensor id>>
	 */
	private Map<Integer, Set<Sensor>> notActiveSensorTypeSensorIDMap;
	/**
	 * Map<Integer = sensor id, Double = percentage (1.0 == 100%).
	 */
	private Map<Integer, Double> sensorTypePercentageMap;
	
	
	/**
	 * Default
	 */
	public DefaultGPSGateStrategy() {}
	
	@Override
	public void handleGateMessage(Map<Integer, Double> gateInformationMap) throws UnknownHostException, IOException {
		log.debug("Handle gate message");
		for(Entry<Integer, Double> entry : gateInformationMap.entrySet()) {
			if(SensorTypeEnum.getForSensorTypeId(entry.getKey()).equals(TrafficSensorTypeEnum.GPS)) {
				int activeSensorsForType = this.activeSensorTypeSensorIDMap.get(entry.getKey()).size();
				int notActiveSensorsForType = this.notActiveSensorTypeSensorIDMap.get(entry.getKey()).size();
				int allSensors = activeSensorsForType + notActiveSensorsForType;
				int newActiveValue = (int)((allSensors) * entry.getValue());
				
				log.debug("New Value: " +entry.getValue() +" newActiveValue: " +newActiveValue +" allSensors: " +allSensors +" notActiveSensors: " +notActiveSensorsForType +" active sensors: " +activeSensorsForType);
				
				if(newActiveValue > activeSensorsForType) {
					this.addAmountOfSensorsToInfoSphere(newActiveValue, entry.getKey());
				} else if(newActiveValue < activeSensorsForType) {
					this.removeAmountOfSensorsFromInfoSphere(newActiveValue, entry.getKey());
				}
				
				/* Update our map: */
				this.sensorTypePercentageMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	protected void onInit(TrafficInitParameter param) throws InitializationException {
		log.debug("init");
		this.resetAllAttributes();
	}

	@Override
	protected void onReset() {
		this.resetAllAttributes();
	}

	@Override
	protected void onStart(InfrastructureStartParameter param) {}

	@Override
	protected void onStop() {}

	@Override
	protected void onResume() {}

	@Override
	protected void onUpdate(EventList<Event> simulationEventList) {}

	@Override
	public void createSensor(Sensor<?,?> sensor) throws SensorException {
		if(sensor.getSensorType().equals(TrafficSensorTypeEnum.GPS)) {
			log.debug("create gps sensor. ID: " +sensor.getId() +" type: " +sensor.getSensorType());
			Set<Sensor> sensorCollection = this.allSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId());
			if(sensorCollection == null) {
				sensorCollection = new HashSet<>();
				this.allSensorTypeSensorIDMap.put(sensor.getSensorType().getSensorTypeId(), sensorCollection);
			}
			sensorCollection.add(sensor);

			/* Send to InfoSphere, if we have no percentage limit: */
			if(this.sensorTypePercentageMap.get(sensor.getSensorType().getSensorTypeId()) == 1.0) {
				List<Sensor> tmpSensorIDList = new LinkedList<>();
				tmpSensorIDList.add(sensor);
				this.addSensorIDsToInfoSphere(tmpSensorIDList);
				this.activeSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).add(sensor);
			} else {
				this.notActiveSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).add(sensor);
			}
		}
	}

	@Override
	public void createSensors(Collection<Sensor<?,?>> sensors)
			throws SensorException {		
		Set<Sensor> sensorIDSet = new HashSet<>();

		/* Add sensors to map and find out, if we have to send them: */
		for(Sensor sensor : sensors) {
			if(sensor.getSensorType().equals(TrafficSensorTypeEnum.GPS)) {
				log.debug("create gps sensor. ID: " +sensor.getId() +" type: " +sensor.getSensorType());
				Set<Sensor> sensorCollection = this.allSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId());
				if(sensorCollection == null) {
					sensorCollection = new HashSet<>();
					this.allSensorTypeSensorIDMap.put(sensor.getSensorType().getSensorTypeId(), sensorCollection);
				}
				sensorCollection.add(sensor);
				
				/* Send only to server if we have no percentage limit: */
				if(this.sensorTypePercentageMap.get(sensor.getSensorType().getSensorTypeId()) == 1.0) {
					sensorIDSet.add(sensor);
					this.activeSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).add(sensor);
				} else {					
					this.notActiveSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).add(sensor);
				}
			}
		}
		
		if(!sensorIDSet.isEmpty()) {
			this.addSensorIDsToInfoSphere(sensorIDSet);
		}
	}

	@Override
	public void deleteSensor(Sensor sensor) throws SensorException {
		/*
		 * Remove from all maps and find out, if we have to add a new sensor to InfoSphere:
		 */
		if(sensor.getSensorType().equals(TrafficSensorTypeEnum.GPS)) {
			log.debug("delete gps sensor. ID: " +sensor.getId() +" type: " +sensor.getSensorType());
			this.allSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getId());
			if(this.activeSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getId())) {
				if(this.sensorTypePercentageMap.get(sensor.getSensorType()) < 1.0) {
					this.addAmountOfSensorsToInfoSphere(1, sensor.getSensorType().getSensorTypeId());
				}
			} else {
				this.notActiveSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getId());	
			}	
		}
	}

	@Override
	public void deleteSensors(Collection<Sensor<?,?>> sensors)
			throws SensorException {
		/*
		 * Remove from all maps and find out, if we have to add new sensors to InfoSphere:
		 */
		Map<Integer, Integer> sensorTypeAddNewMap = new HashMap<>();
		for(Sensor sensor : sensors) {
			if(sensor.getSensorType().equals(TrafficSensorTypeEnum.GPS)) {
				log.debug("delete gps sensor. ID: " +sensor.getId() +" type: " +sensor.getSensorType());
				this.allSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getId());
				if(this.activeSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getId())) {
					if(this.sensorTypePercentageMap.get(sensor.getSensorType().getSensorTypeId()) < 1.0) {
						Integer amount = sensorTypeAddNewMap.get(sensor.getSensorType().getSensorTypeId());
						if(amount == null) {
							amount = 0;
						}
						sensorTypeAddNewMap.put(sensor.getSensorType().getSensorTypeId(), amount + 1);
					}
				} else {
					this.notActiveSensorTypeSensorIDMap.get(sensor.getSensorType().getSensorTypeId()).remove(sensor.getId());
				}
			}
		}
		
		for(Entry<Integer, Integer> entry : sensorTypeAddNewMap.entrySet()) {
			this.addAmountOfSensorsToInfoSphere(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public boolean statusOfSensor(Sensor sensor) throws SensorException {
		return false;
	}
	
	/**
	 * Resets all the attributes
	 */
	private void resetAllAttributes() {
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("/properties.props"));
		} catch (Exception e) {
			try {
				properties.load(DefaultGPSGateStrategy.class
						.getResourceAsStream("/properties.props"));
			} catch (IOException e1) {
				throw new RuntimeException("Can not load properties!");
			}
		}
		
		this.socketIP = properties.getProperty("InfoSphereGateMessageIP");
		this.socketPortRemoveKeys = Integer.valueOf(properties.getProperty("InfoSphereGateMessageRemoveKeysPort"));
		this.socketPortAddKeys = Integer.valueOf(properties.getProperty("InfoSphereGateMessageAddKeysPort"));
		this.allSensorTypeSensorIDMap = new HashMap<>();
		this.activeSensorTypeSensorIDMap = new HashMap<>();
		this.notActiveSensorTypeSensorIDMap = new HashMap<>();
		this.sensorTypePercentageMap = new HashMap<>();
		this.allSensorTypeSensorIDMap.put(TrafficSensorTypeEnum.GPS.getSensorTypeId(), new HashSet<Sensor>());
		this.activeSensorTypeSensorIDMap.put(TrafficSensorTypeEnum.GPS.getSensorTypeId(), new HashSet<Sensor>());
		this.notActiveSensorTypeSensorIDMap.put(TrafficSensorTypeEnum.GPS.getSensorTypeId(), new HashSet<Sensor>());
		this.sensorTypePercentageMap.put(TrafficSensorTypeEnum.GPS.getSensorTypeId(), 1.0);
	}
	
	/**
	 * Adds new sensor ids to InfoSphere
	 * @param sensorIDCollection
	 */
	private void addSensorIDsToInfoSphere(Collection<Sensor> sensorIDCollection) {
		Socket socket = null;
		DataOutputStream outStream = null;
		try {
			socket = new Socket(this.socketIP, this.socketPortAddKeys);
			outStream = new DataOutputStream(socket.getOutputStream());
			
			for(Sensor sensorID : sensorIDCollection) {
				log.debug("Send sensor with id: " +sensorID +" to InfoSphere.");
				outStream.writeLong(sensorID.getId());
				outStream.flush();
			}
			
		} catch(IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				outStream.close();
			} catch(Exception e) {}
			try {
				socket.close();
			} catch(Exception e) {}
		}
	}
	
	/**
	 * Removes sensor ids from InfoSphere
	 * @param sensorIDCollection
	 * @throws IOException 
	 */
	private void removeSensorIDsFromInfoSphere(Collection<Sensor> sensorIDCollection) throws IOException {
		Socket socket = null;
		DataOutputStream outStream = null;
		try {
			socket = new Socket(this.socketIP, this.socketPortRemoveKeys);
			outStream = new DataOutputStream(socket.getOutputStream());
			
			for(Sensor sensorID : sensorIDCollection) {
				outStream.writeLong(sensorID.getId());
				outStream.flush();
			}
		} catch(IOException e) {
			throw e;
		} finally {
			try {
				outStream.close();
			} catch(IOException e) {}
			try {
				socket.close();
			} catch(IOException e) {}
		}
	}
	
	/**
	 * Adds the specified amount of sensors to InfoSphere.
	 * Removes them also from not active map and adds them to active map.
	 * @param amount
	 * @param sensorType
	 */
	private void addAmountOfSensorsToInfoSphere(int amount, int sensorType) {
		Set<Sensor> sensorIDs = new HashSet<>();
		Set<Sensor> activeSensorSet = this.activeSensorTypeSensorIDMap.get(sensorType);
		Set<Sensor> notActiveSensorSet = this.notActiveSensorTypeSensorIDMap.get(sensorType);
		for(Sensor sensorID : new HashSet<>(notActiveSensorSet)) {
			sensorIDs.add(sensorID);
			activeSensorSet.add(sensorID);
			notActiveSensorSet.remove(sensorID);
			if(sensorIDs.size() == amount) {
				break;
			}
		}
		
		this.addSensorIDsToInfoSphere(sensorIDs);
	}
	
	/**
	 * Removes the given amount of sensors from InfoSphere.
	 * Removes them also from active map and adds them to not active map.
	 * @param amount
	 * @param sensorType
	 * @throws IOException 
	 */
	private void removeAmountOfSensorsFromInfoSphere(int amount, int sensorType) throws IOException {

		log.debug("Remove amount of sensors from infosphere");
		
		Set<Sensor> sensorIDs = new HashSet<>();
		Set<Sensor> activeSensorSet = this.activeSensorTypeSensorIDMap.get(sensorType);
		Set<Sensor> notActiveSensorSet = this.notActiveSensorTypeSensorIDMap.get(sensorType);
		for(Sensor sensorID : new HashSet<>(activeSensorSet)) {
			sensorIDs.add(sensorID);
			activeSensorSet.remove(sensorID);
			notActiveSensorSet.add(sensorID);
			if(sensorIDs.size() == amount) {
				break;
			}
		}

		log.debug("remove amount: " +amount +" sensorIDs: " +sensorIDs.size());
		
		this.removeSensorIDsFromInfoSphere(sensorIDs);
	}

	@Override
	public String getName() {
		return DefaultGPSGateStrategy.class.getName();
	}
}
