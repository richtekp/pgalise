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
 
package de.pgalise.simulation.controlCenter.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.controlCenter.internal.message.AccessDeniedMessage;
import de.pgalise.simulation.controlCenter.internal.message.AskForValidNodeMessage;
import de.pgalise.simulation.controlCenter.internal.message.CCWebSocketMessage;
import de.pgalise.simulation.controlCenter.internal.message.CreateAttractionEventsMessage;
import de.pgalise.simulation.controlCenter.internal.message.CreateRandomVehiclesMessage;
import de.pgalise.simulation.controlCenter.internal.message.CreateSensorsMessage;
import de.pgalise.simulation.controlCenter.internal.message.DeleteSensorsMessage;
import de.pgalise.simulation.controlCenter.internal.message.ErrorMessage;
import de.pgalise.simulation.controlCenter.internal.message.GenericNotificationMessage;
import de.pgalise.simulation.controlCenter.internal.message.ImportXMLStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.LoadSimulationStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.OSMAndBusstopFileMessage;
import de.pgalise.simulation.controlCenter.internal.message.OSMParsedMessage;
import de.pgalise.simulation.controlCenter.internal.message.OnConnectMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationEventListMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationExportStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationExportedMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationStartedMessage;
import de.pgalise.simulation.controlCenter.internal.message.UsedIDsMessage;
import de.pgalise.simulation.controlCenter.internal.message.ValidNodeMessage;
import de.pgalise.simulation.controlCenter.internal.model.AttractionData;
import de.pgalise.simulation.controlCenter.internal.model.CCSimulationStartParameter;
import de.pgalise.simulation.controlCenter.internal.model.ErrorMessageData;
import de.pgalise.simulation.controlCenter.internal.model.IDWrapper;
import de.pgalise.simulation.controlCenter.internal.model.RandomVehicleBundle;
import de.pgalise.simulation.controlCenter.internal.util.service.ControlCenterModule;
import de.pgalise.simulation.controlCenter.internal.util.service.CreateAttractionEventService;
import de.pgalise.simulation.controlCenter.internal.util.service.CreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.OSMCityInfrastructureDataService;
import de.pgalise.simulation.controlCenter.internal.util.service.SensorInterfererService;
import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.Node;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.ServerConfiguration.Entity;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.traffic.AttractionTrafficEvent;
import de.pgalise.simulation.shared.event.traffic.CreateBussesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehiclesEvent;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.shared.sensor.SensorType;
import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.shared.traffic.VehicleInformation;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.util.GTFS.service.BusService;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;

/**
 * The control center user. An object will be created if a new user joins the control center. It's only possible
 * to have one user in the control center. Other users will receive an {@link AccessDeniedMessage} and can not
 * join the control center. The client can send and receive several messages ({@link CCWebSocketMessage}).
 * The server can use {@link CCWebSocketUser#sendMessage(CCWebSocketMessage)} to send messages to the client.
 * They will be serialized to JSON with the GSON API. The server can receive messages from the user via {@link CCWebSocketUser#onMessage(String)}.
 * This messages will be deserialized with the GSON API and can be processed than.
 * This class uses {@link ControlCenterModule} for dependency injection.
 * @author Dennis
 * @author Timo
 */
public class CCWebSocketUser extends MessageInbound {
	/**
	 * Not wanted reg exp in file names.
	 */
	private static final String removeFromFileNameRegExp = "\\W";
	/**
	 * Date format for exported start parameter file names.
	 */
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-H-m-s");
	private static final Charset charset = Charset.forName("UTF-8");
	private static final CharsetDecoder decoder = charset.newDecoder();
	private static final Logger log = LoggerFactory.getLogger(CCWebSocketUser.class);
	/**
	 * Google guice injector with the used dependencies.
	 */
	private static final Injector injector = Guice.createInjector(new ControlCenterModule());
	/**
	 * Used to serialize and deserialize the start parameter.
	 */
	private static final StartParameterSerializerService startParameterSerializerService = injector
			.getInstance(StartParameterSerializerService.class);
	/**
	 * Creates random vehicles, if the user ask for it.
	 */
	private static final CreateRandomVehicleService randomDynamicSensorService = injector
			.getInstance(CreateRandomVehicleService.class);
	/**
	 * Used for openstreetmap parsing. To avoid long waiting, this service will serialize the file binary and reload it, if it's already parsed.
	 */
	private static final OSMCityInfrastructureDataService osmCityInfrastructureDataService = injector
			.getInstance(OSMCityInfrastructureDataService.class);
	/**
	 * This service knows about all sensor interferers.
	 */
	private static final SensorInterfererService sensorInterfererService = injector
			.getInstance(SensorInterfererService.class);
	/**
	 * This service gives the energy profiles for all buildings in an area.
	 */
	private static final BuildingEnergyProfileStrategy buildingEnergyProfileStrategy = injector.getInstance(BuildingEnergyProfileStrategy.class);
	/**
	 * This service can create an attraction event {@link AttractionTrafficEvent}
	 */
	private static final CreateAttractionEventService createAttractionEventService = injector.getInstance(CreateAttractionEventService.class);
	private SimulationController simulationController;
	private CCWebSocketServlet ccWebSocketServlet;
	/**
	 * Lock to get sure that city infrastructure is parsed before called.
	 */
	private Object cityInfrastructureDataLock;
	private CityInfrastructureData cityInfrastructureData;
	private Properties properties;
	private CCSimulationStartParameter simulationStartParameter;

	/**
	 * For outgoing messages.
	 */
	private WsOutbound outbound;
	/**
	 * To serialize the messages.
	 */
	private Gson gson;
	
	private ServiceDictionary serviceDictionary;
	/**
	 * This service can query bus information.
	 */
	private BusService busService = injector.getInstance(BusService.class);
	
	/**
	 * Constructor
	 * @param ccWebSocketServlet
	 * 			to lookup the simulation controller
	 * @param gson
	 * 			to serialize and deserialize the messages
	 * @param serviceDictionary
	 * 			to set and get the controllers.
	 */
	public CCWebSocketUser(CCWebSocketServlet ccWebSocketServlet, 
			Gson gson, 
			ServiceDictionary serviceDictionary) {
		this.ccWebSocketServlet = ccWebSocketServlet;
		this.gson = gson;
		this.cityInfrastructureDataLock = new Object();
		this.serviceDictionary = serviceDictionary;
		CCWebSocketUser.startParameterSerializerService.init(gson);
		this.properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/properties.props");
		try {
			this.properties.load(is);
		} catch(IOException e) {
			log.error("Exception", e);
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				log.error("Exception", e);
			}
		}
	}
	
	/**
	 * Invoked when message received.
	 * All the messages needs to be a subclass of {@link CCWebSocketMessage}.
	 * If a new message will be added, the behavior needs to be added here.
	 * @param message
	 */
	public void onMessage(String message) {
		log.debug("Incoming message: " +message);
		CCWebSocketMessage<?> ccWebSocketMessage = this.gson.fromJson(message, CCWebSocketMessage.class);
		
		try {
			switch(ccWebSocketMessage.getMessageType()) {
			case CCWebSocketMessage.MessageType.OSM_AND_BUSSTOP_FILE_MESSAGE:
				synchronized (cityInfrastructureDataLock) {
					OSMAndBusstopFileMessage osmAndBusstopFileMessage = this.gson.fromJson(message, OSMAndBusstopFileMessage.class);
					String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
					this.cityInfrastructureData = 
							osmCityInfrastructureDataService.createCityInfrastructureData(
									new File(path +osmAndBusstopFileMessage.getContent().getOsmFileName()), 
									new File(path +osmAndBusstopFileMessage.getContent().getBusStopFileName()), 
									buildingEnergyProfileStrategy);
					this.sendMessage(new OSMParsedMessage(ccWebSocketMessage.getMessageID(), this.cityInfrastructureData.getBoundary()));
					return;
				}
				
			case CCWebSocketMessage.MessageType.ASK_FOR_VALID_NODE:
				synchronized (cityInfrastructureDataLock) {
					if(this.cityInfrastructureData != null) {
						AskForValidNodeMessage askForValidNodeMessage = this.gson.fromJson(message, AskForValidNodeMessage.class);
						de.pgalise.simulation.controlCenter.internal.model.Node node = null;
						if(askForValidNodeMessage.getContent().isOnJunction()) {
							Node tmpNode = this.cityInfrastructureData.getNearestJunctionNode(
									askForValidNodeMessage.getContent().getPosition().x, 
									askForValidNodeMessage.getContent().getPosition().y);
							node = new de.pgalise.simulation.controlCenter.internal.model.Node(true, 
									true, 
									tmpNode.getId(), 
									new Coordinate(tmpNode.getLatitude(), tmpNode.getLongitude()));
						} else if(askForValidNodeMessage.getContent().isOnStreet()) {
							Node tmpNode = this.cityInfrastructureData.getNearestStreetNode(
									askForValidNodeMessage.getContent().getPosition().x, 
									askForValidNodeMessage.getContent().getPosition().y);
							node = new de.pgalise.simulation.controlCenter.internal.model.Node(false, 
									true, 
									tmpNode.getId(), 
									new Coordinate(tmpNode.getLatitude(), tmpNode.getLongitude()));
						} else {
							Node tmpNode = this.cityInfrastructureData.getNearestNode(
									askForValidNodeMessage.getContent().getPosition().x, 
									askForValidNodeMessage.getContent().getPosition().y);
							node = new de.pgalise.simulation.controlCenter.internal.model.Node(false, 
									false, 
									tmpNode.getId(), 
									new Coordinate(tmpNode.getLatitude(), tmpNode.getLongitude()));
						}
						
						this.sendMessage(new ValidNodeMessage(ccWebSocketMessage.getMessageID(), node));
						return;
					} else {
						throw new RuntimeException("OSM not parsed yet!");
					}
				}
				
			case CCWebSocketMessage.MessageType.CREATE_SENSORS_MESSAGE:
				List<SensorHelper> sensorHelperList = new LinkedList<>(this.gson.fromJson(message, CreateSensorsMessage.class).getContent());
				for(SensorHelper sensorHelper : sensorHelperList) {
					sensorHelper.setSensorInterfererType(sensorInterfererService.getSensorInterferes(sensorHelper.getSensorType(), 
							this.simulationStartParameter.isWithSensorInterferes()));
				}
				this.simulationController.createSensors(sensorHelperList);
				break;
				
			case CCWebSocketMessage.MessageType.DELETE_SENSORS_MESSAGE:
				this.simulationController.deleteSensors(this.gson.fromJson(message, DeleteSensorsMessage.class).getContent());
				break;
				
			case CCWebSocketMessage.MessageType.SIMULATION_START_PARAMETER:
			{
				log.debug("Start simulation");
				
				CCSimulationStartParameter ccSimulationStartParameter = this.gson.fromJson(message, SimulationStartParameterMessage.class).getContent();
				this.simulationStartParameter = ccSimulationStartParameter;
				
				/* Create server configuration: */
				ServerConfiguration serverConfiguration = new ServerConfiguration();
				Map<String, List<Entity>> serverConfiguationMap = new HashMap<>();
				
				log.debug("Create server configuration");
				
				/* Add traffic servers: */
				for(String address : ccSimulationStartParameter.getTrafficServerIPList()) {
					List<Entity> tmpEntityList = serverConfiguationMap.get(address);
					if(tmpEntityList == null) {
						tmpEntityList = new LinkedList<>();
						serverConfiguationMap.put(address, tmpEntityList);
					}
					tmpEntityList.add(new Entity(ServiceDictionary.TRAFFIC_SERVER));
				}
				
				/* Add traffic controller: */
				List<Entity> tmpEntityList = serverConfiguationMap.get(ccSimulationStartParameter.getIpTrafficController());
				if(tmpEntityList == null) {
					tmpEntityList = new LinkedList<>();
					serverConfiguationMap.put(ccSimulationStartParameter.getIpTrafficController(), tmpEntityList);
				}
				tmpEntityList.add(new Entity(ServiceDictionary.TRAFFIC_CONTROLLER));
				
				/* Add static sensor controller: */
				tmpEntityList = serverConfiguationMap.get(ccSimulationStartParameter.getIpStaticSensorController());
				if(tmpEntityList == null) {
					tmpEntityList = new LinkedList<>();
					serverConfiguationMap.put(ccSimulationStartParameter.getIpStaticSensorController(), tmpEntityList);
				}
				tmpEntityList.add(new Entity(ServiceDictionary.STATIC_SENSOR_CONTROLLER));
				
				/* Weather controller: */
				tmpEntityList = serverConfiguationMap.get(ccSimulationStartParameter.getIpWeatherController());
				if(tmpEntityList == null) {
					tmpEntityList = new LinkedList<>();
					serverConfiguationMap.put(ccSimulationStartParameter.getIpWeatherController(), tmpEntityList);
				}
				tmpEntityList.add(new Entity(ServiceDictionary.WEATHER_CONTROLLER));
				
				/* Energy controller: */
				tmpEntityList = serverConfiguationMap.get(ccSimulationStartParameter.getIpEnergyController());
				if(tmpEntityList == null) {
					tmpEntityList = new LinkedList<>();
					serverConfiguationMap.put(ccSimulationStartParameter.getIpEnergyController(), tmpEntityList);
				}
				tmpEntityList.add(new Entity(ServiceDictionary.ENERGY_CONTROLLER));
				
				/* Front controller (on every used ip): */
				Set<String> frontControllerAddressSet = new HashSet<>();
				for(String address : ccSimulationStartParameter.getTrafficServerIPList()) {
					if(!frontControllerAddressSet.contains(address)) {
						serverConfiguationMap.get(address).add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
						frontControllerAddressSet.add(address);
					}
				}
				if(!frontControllerAddressSet.contains(ccSimulationStartParameter.getIpEnergyController())) {
					serverConfiguationMap.get(ccSimulationStartParameter.getIpEnergyController()).add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
					frontControllerAddressSet.add(ccSimulationStartParameter.getIpEnergyController());
				}
				if(!frontControllerAddressSet.contains(ccSimulationStartParameter.getIpWeatherController())) {
					serverConfiguationMap.get(ccSimulationStartParameter.getIpWeatherController()).add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
					frontControllerAddressSet.add(ccSimulationStartParameter.getIpWeatherController());
				}
				if(!frontControllerAddressSet.contains(ccSimulationStartParameter.getIpStaticSensorController())) {
					serverConfiguationMap.get(ccSimulationStartParameter.getIpStaticSensorController()).add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
					frontControllerAddressSet.add(ccSimulationStartParameter.getIpSimulationController());
				}
				if(!frontControllerAddressSet.contains(ccSimulationStartParameter.getIpTrafficController())) {
					serverConfiguationMap.get(ccSimulationStartParameter.getIpTrafficController()).add(new Entity(ServiceDictionary.FRONT_CONTROLLER));
					frontControllerAddressSet.add(ccSimulationStartParameter.getIpTrafficController());
				}
				
				/* Random seed service */
				tmpEntityList.add(new Entity(ServiceDictionary.RANDOM_SEED_SERVICE));
				
				/* get simulation controller */
				log.debug("Look up simulationcontroller at: " +ccSimulationStartParameter.getIpSimulationController());
				this.simulationController = this.ccWebSocketServlet.getSimulationController(ccSimulationStartParameter.getIpSimulationController());
				
				serverConfiguration.setConfiguration(serverConfiguationMap);
								
				/* Created server configuration */
				
				/* Create init parameters: */
				InitParameter initParameter = new InitParameter(this.cityInfrastructureData, 
						serverConfiguration, 
						ccSimulationStartParameter.getStartTimestamp(), 
						ccSimulationStartParameter.getEndTimestamp(), 
						ccSimulationStartParameter.getInterval(), 
						ccSimulationStartParameter.getClockGeneratorInterval(), 
						ccSimulationStartParameter.getOperationCenterAddress(), 
						ccSimulationStartParameter.getControlCenterAddress(),
						ccSimulationStartParameter.getTrafficFuzzyData(),
						this.cityInfrastructureData.getBoundary());
				
				/* Init: */				
				this.simulationController.init(initParameter);
				
				/* Init random seed service: */
				this.serviceDictionary.getRandomSeedService().init(ccSimulationStartParameter.getStartTimestamp());
				Random random = new Random(this.serviceDictionary.getRandomSeedService().getSeed(CCWebSocketUser.class.getName()));
				
				/* To produce new sensor ids, we have to save all the used ones */
				Set<Integer> usedIntegerIDs = new HashSet<>();
				Set<UUID> usedUUIDs = new HashSet<>();
				
				/* Create sensors: */
				for(SensorHelper sensorHelper : ccSimulationStartParameter.getSensorHelperList()) {
					sensorHelper.setSensorInterfererType(sensorInterfererService.getSensorInterferes(sensorHelper.getSensorType(), 
							ccSimulationStartParameter.isWithSensorInterferes()));
					usedIntegerIDs.add(sensorHelper.getSensorID());
				}
				this.simulationController.createSensors(ccSimulationStartParameter.getSensorHelperList());
				
				/* Create start parameters: */
				StartParameter startParameter = new StartParameter(ccSimulationStartParameter.getCity(),
						ccSimulationStartParameter.isAggregatedWeatherDataEnabled(), 
						ccSimulationStartParameter.getWeatherEventList(), 
						ccSimulationStartParameter.getBusRoutes());
				
				List<Integer> newIntegerIDs = new LinkedList<>();
				List<UUID> newUUIDs = new LinkedList<>();
				
				List<SimulationEvent> simulationEventList = new LinkedList<>();
				
				/* Create random vehicles: */
				CreateRandomVehiclesEvent createRandomVehiclesEvent = (CreateRandomVehiclesEvent)randomDynamicSensorService.createRandomDynamicSensors(ccSimulationStartParameter.getRandomDynamicSensorBundle(), 
						this.serviceDictionary.getRandomSeedService(), ccSimulationStartParameter.isWithSensorInterferes());
				simulationEventList.add(createRandomVehiclesEvent);
				
				/* Save the new and old IDs from create random vehicles: */
				usedUUIDs.addAll(ccSimulationStartParameter.getRandomDynamicSensorBundle().getUsedUUIDs());
				usedIntegerIDs.addAll(ccSimulationStartParameter.getRandomDynamicSensorBundle().getUsedSensorIDs());
				for(CreateRandomVehicleData data : createRandomVehiclesEvent.getCreateRandomVehicleDataList()) {
					newUUIDs.add(data.getVehicleInformation().getVehicleID());
					usedUUIDs.add(data.getVehicleInformation().getVehicleID());
					for(SensorHelper sensor : data.getSensorHelpers()) {
						newIntegerIDs.add(sensor.getSensorID());
						usedIntegerIDs.add(sensor.getSensorID());
					}
				}
				
				/* Create attractions: */				
				for(AttractionData attractionData : ccSimulationStartParameter.getAttractionCollection()) {
					/* Before creating them, update the used IDs: */
					attractionData.getRandomVehicleBundle().getUsedSensorIDs().addAll(newIntegerIDs);
					attractionData.getRandomVehicleBundle().getUsedUUIDs().addAll(newUUIDs);
					AttractionTrafficEvent attractionTrafficEvent = createAttractionEventService.createAttractionTrafficEvent(
							attractionData.getRandomVehicleBundle(), 
							this.serviceDictionary.getRandomSeedService(), 
							this.simulationStartParameter.isWithSensorInterferes(), 
							attractionData.getNodeID(), 
							attractionData.getAttractionPoint(),
							attractionData.getAttractionStartTimestamp(),
							attractionData.getAttractionEndTimestamp());
					simulationEventList.add(attractionTrafficEvent);
					
					/* Update the new used IDs: */
					for(CreateRandomVehicleData data : attractionTrafficEvent.getCreateRandomVehicleDataList()) {
						newUUIDs.add(data.getVehicleInformation().getVehicleID());
						usedUUIDs.add(data.getVehicleInformation().getVehicleID());
						for(SensorHelper sensor : data.getSensorHelpers()) {
							newIntegerIDs.add(sensor.getSensorID());
							usedIntegerIDs.add(sensor.getSensorID());
						}
					}
				}
				
				
				/* Create busses: */
				log.debug("Selected bus routes in cc: " +ccSimulationStartParameter.getBusRoutes().size());
				
				List<CreateRandomVehicleData> busDataList = new LinkedList<>();
				List<BusRoute> busRouteList = new LinkedList<>();
				for(BusRoute busRoute : ccSimulationStartParameter.getBusRoutes()) {
					if(busRoute.isUsed()) {
						log.debug("Selected bus route: " +busRoute.getRouteId());
						busRouteList.add(busRoute);
					} else {
						log.debug("Not selected bus route: " +busRoute.getRouteId());
					}
				}
				
				log.debug("Selected bus routes: " +busRouteList.size());
				
				int totalNumberOfBusTrips = this.busService.getTotalNumberOfBusTrips(busRouteList, 
						ccSimulationStartParameter.getStartTimestamp());
				
				log.debug("Create " +totalNumberOfBusTrips +" busses!");
				
				for(int i = 0; i < totalNumberOfBusTrips; i++) {
					UUID id = this.getUniqueRandomUUID(usedUUIDs, random);
					List<SensorHelper> sensorLists = new ArrayList<>();
					sensorLists.add(new SensorHelper(this.getUniqueRandomIntID(usedIntegerIDs, random), 
							new Coordinate(), 
							SensorType.GPS_BUS, 
							new ArrayList<SensorInterfererType>(),
							""));
					sensorLists.add(new SensorHelper(this.getUniqueRandomIntID(usedIntegerIDs, random), 
							new Coordinate(), 
							SensorType.INFRARED, 
							new ArrayList<SensorInterfererType>(),
							""));
					busDataList.add(new CreateRandomVehicleData(sensorLists, new VehicleInformation(id, true,
							VehicleTypeEnum.BUS, VehicleModelEnum.BUS_CITARO, null, id.toString())));
				}
				simulationEventList.add(new CreateBussesEvent(busDataList, ccSimulationStartParameter.getStartTimestamp(), busRouteList));
				
				/* Add events to simulation: */
				this.simulationController.addSimulationEventList(new SimulationEventList(simulationEventList, 0, UUID.randomUUID()));
				
				/* Add event lists from startparameter: */
				for(SimulationEventList tmpList : ccSimulationStartParameter.getSimulationEventLists()) {
					this.simulationController.addSimulationEventList(tmpList);
				}
				
				/* Start the controllers: */
				this.simulationController.start(startParameter);
				
				/* Save start parameter: */
				startParameterSerializerService.serialize(ccSimulationStartParameter, 
						ccSimulationStartParameter.getCity().getName().replaceAll(removeFromFileNameRegExp, "") +"_"
						+dateFormat.format(new Date())
								+"." +this.properties.getProperty("suffixStartParameterXML"));
				
				/* Send message with new used ids: */
				this.sendMessage(new SimulationStartedMessage(ccWebSocketMessage.getMessageID(), new IDWrapper(newIntegerIDs, newUUIDs)));
				return;
			}				
			case CCWebSocketMessage.MessageType.SIMULATION_STOP:
				this.simulationController.stop();
				break;
				
			case CCWebSocketMessage.MessageType.SIMULATION_EVENT_LIST:
				SimulationEventList sel = this.gson.fromJson(message, SimulationEventListMessage.class).getContent();
				this.simulationController.update(sel);
				break;
			
			case CCWebSocketMessage.MessageType.CREATE_RANDOM_VEHICLES: {
				List<SimulationEvent> simulationEventList = new LinkedList<>();
				RandomVehicleBundle rvb = this.gson.fromJson(message, CreateRandomVehiclesMessage.class).getContent();
				simulationEventList.add(randomDynamicSensorService.createRandomDynamicSensors(rvb, 
								this.serviceDictionary.getRandomSeedService(), this.simulationStartParameter.isWithSensorInterferes()));
				this.simulationController.update(new SimulationEventList(simulationEventList, 0, UUID.randomUUID()));
				
				/* Send message with new used ids: */
				List<Integer> newIntegerIDs = new LinkedList<>();
				List<UUID> newUUIDs = new LinkedList<>();
				for(SimulationEvent event : simulationEventList) {
					for(CreateRandomVehicleData data : ((CreateRandomVehiclesEvent)event).getCreateRandomVehicleDataList()) {
						newUUIDs.add(data.getVehicleInformation().getVehicleID());
						for(SensorHelper sensor : data.getSensorHelpers()) {
							newIntegerIDs.add(sensor.getSensorID());
						}
					}
				}
				this.sendMessage(new UsedIDsMessage(ccWebSocketMessage.getMessageID(), new IDWrapper(newIntegerIDs, newUUIDs)));
				return;	
			}
				
			case CCWebSocketMessage.MessageType.CREATE_ATTRACTION_EVENTS_MESSAGE: {
				List<SimulationEvent> simulationEventList = new LinkedList<>();
				Collection<AttractionData> attractionDataCollection = this.gson.fromJson(message, CreateAttractionEventsMessage.class).getContent();
				for(AttractionData attractionData : attractionDataCollection) {
					simulationEventList.add(createAttractionEventService.createAttractionTrafficEvent(
							attractionData.getRandomVehicleBundle(), 
							this.serviceDictionary.getRandomSeedService(), 
							this.simulationStartParameter.isWithSensorInterferes(), 
							attractionData.getNodeID(), 
							attractionData.getAttractionPoint(),
							attractionData.getAttractionStartTimestamp(),
							attractionData.getAttractionEndTimestamp()));
				}
				this.simulationController.update(new SimulationEventList(simulationEventList, 0, UUID.randomUUID()));
				
				/* Send message with new used ids: */
				List<Integer> newIntegerIDs = new LinkedList<>();
				List<UUID> newUUIDs = new LinkedList<>();
				for(SimulationEvent event : simulationEventList) {
					for(CreateRandomVehicleData data : ((CreateRandomVehiclesEvent)event).getCreateRandomVehicleDataList()) {
						newUUIDs.add(data.getVehicleInformation().getVehicleID());
						for(SensorHelper sensor : data.getSensorHelpers()) {
							newIntegerIDs.add(sensor.getSensorID());
						}
					}
				}
				this.sendMessage(new UsedIDsMessage(ccWebSocketMessage.getMessageID(), new IDWrapper(newIntegerIDs, newUUIDs)));
				return;
			}	
			case CCWebSocketMessage.MessageType.SIMULATION_EXPORT_PARAMETER:
				SimulationExportStartParameterMessage simulationExportParameterMessage = this.gson.fromJson(message, SimulationExportStartParameterMessage.class);
				this.sendMessage(new SimulationExportedMessage(simulationExportParameterMessage.getMessageID(), startParameterSerializerService.serialize(simulationExportParameterMessage.getContent().getCcSimulationStartParameter(), 
						simulationExportParameterMessage.getContent().getFileName())));
				
				log.debug("Startparameter: " +message);
				
				return;	

			case CCWebSocketMessage.MessageType.LOAD_SIMULATION_START_PARAMETER:
				LoadSimulationStartParameterMessage loadSimulationStartParameterMessage = this.gson.fromJson(message, LoadSimulationStartParameterMessage.class);
				
				String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath().replaceAll("WEB-INF/classes", "");

				this.sendMessage(new SimulationStartParameterMessage(loadSimulationStartParameterMessage.getMessageID(), 
						startParameterSerializerService.deserialize(
								new FileInputStream(
										new File(
												path 
												+loadSimulationStartParameterMessage.getContent())))));
				return;
				
			case CCWebSocketMessage.MessageType.IMPORT_XML_START_PARAMETER:
				ImportXMLStartParameterMessage importXMLStartParameterMessage = this.gson.fromJson(message, ImportXMLStartParameterMessage.class);
				
				log.debug("XML:" +importXMLStartParameterMessage.getContent());
				
				this.sendMessage(new SimulationStartParameterMessage(importXMLStartParameterMessage.getMessageID(), 
						startParameterSerializerService.deserialize(importXMLStartParameterMessage.getContent())));
				return;
				
			default:
				break;
			}
			
			/* Everything was okay: */
			this.sendMessage(new GenericNotificationMessage(ccWebSocketMessage.getMessageID(), ""));
		} catch(Exception e) {
			log.error("Exception", e);
			try {
				this.sendMessage(new ErrorMessage(ccWebSocketMessage.getMessageID(), new ErrorMessageData(e.getMessage(), ccWebSocketMessage.getMessageType())));
			} catch (IOException e1) {
				log.error("Exception", e1);
			}
		}
	}
	
	/**
	 * Send a message to the client.
	 */
	public void sendMessage(CCWebSocketMessage<?> message) throws IOException {
		log.debug("Sending: " + message.toJson(this.gson));
		this.outbound.writeTextMessage(CharBuffer.wrap(message.toJson(this.gson)));
	}

	/**
	 * Message form client
	 */
	@Override
	protected void onBinaryMessage(ByteBuffer message) throws IOException {
		this.onMessage(decoder.decode(message).toString());
	}

	/**
	 * Message from client
	 */
	@Override
	protected void onTextMessage(CharBuffer message) throws IOException {
		this.onMessage(message.toString());
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		super.onOpen(outbound);
		this.outbound = outbound;
		if(CCWebSocketServlet.getUser() == this) {
			try {
				this.sendMessage(new OnConnectMessage(this.busService.getAllBusRoutes()));
			} catch (IOException | ClassNotFoundException | SQLException e) {
				log.error("Exception", e);
			}
		} else {
			try {
				this.sendMessage(new AccessDeniedMessage("Only one user allowed!"));
			} catch (IOException e) {
				log.error("Exception", e);
			}
		}

	}

	@Override
	protected void onClose(int status) {
		super.onClose(status);
		if(CCWebSocketServlet.getUser() == this) {
			CCWebSocketServlet.removeUser(this);
		}
	}
	
	/**
	 * Returns a random unused integer id.
	 * @param usedIDs
	 * @param random
	 * @return
	 */
	private int getUniqueRandomIntID(Set<Integer> usedIDs, Random random) {
		int randomValue = random.nextInt(Integer.MAX_VALUE);
		while(usedIDs.contains(randomValue)) {
			randomValue = random.nextInt(Integer.MAX_VALUE);
		}
		return randomValue;
	}
	
	/**
	 * Returns a random unused UUID.
	 * @param usedIDs
	 * @param random
	 * @return
	 */
	private UUID getUniqueRandomUUID(Set<UUID> usedIDs, Random random) {
		UUID id = new UUID(random.nextLong(), random.nextLong());
		while(usedIDs.contains(id)) {
			id = UUID.randomUUID();
		}
		return id;
	}
}
