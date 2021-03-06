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
package de.pgalise.simulation.controlCenter.internal.ejb;

import com.google.gson.Gson;
import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.controlCenter.ControlCenterUser;
import de.pgalise.simulation.controlCenter.internal.message.AccessDeniedMessage;
import de.pgalise.simulation.controlCenter.internal.message.AskForValidNodeMessage;
import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import de.pgalise.simulation.controlCenter.internal.message.CreateAttractionEventsMessage;
import de.pgalise.simulation.controlCenter.internal.message.CreateRandomVehiclesMessage;
import de.pgalise.simulation.controlCenter.internal.message.CreateSensorsMessage;
import de.pgalise.simulation.controlCenter.internal.message.DeleteSensorsMessage;
import de.pgalise.simulation.controlCenter.internal.message.ErrorMessage;
import de.pgalise.simulation.controlCenter.internal.message.GenericNotificationMessage;
import de.pgalise.simulation.controlCenter.internal.message.IdentifiableControlCenterMessage;
import de.pgalise.simulation.controlCenter.internal.message.ImportXMLStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.LoadSimulationStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.MapAndBusstopFileMessage;
import de.pgalise.simulation.controlCenter.internal.message.OSMParsedMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationEventListMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationExportStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationExportedMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationInitParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationStartParameterMessage;
import de.pgalise.simulation.controlCenter.internal.message.SimulationStartedMessage;
import de.pgalise.simulation.controlCenter.internal.message.ValidNodeMessage;
import de.pgalise.simulation.controlCenter.internal.util.service.CreateAttractionEventService;
import de.pgalise.simulation.controlCenter.internal.util.service.CreateRandomVehicleService;
import de.pgalise.simulation.controlCenter.internal.util.service.SensorInterfererService;
import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;
import de.pgalise.simulation.controlCenter.model.AttractionData;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.controlCenter.model.ErrorMessageData;
import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.ServerConfigurationEntity;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.traffic.VehicleModelEnum;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.service.FileBasedCityDataService;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.VehicleInformation;
import de.pgalise.simulation.traffic.event.AttractionTrafficEvent;
import de.pgalise.simulation.traffic.event.CreateBussesEvent;
import de.pgalise.simulation.traffic.event.CreateRandomBusData;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.traffic.service.BusService;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import javax.ejb.EJB;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server side representation for control center user. An object will be
 * created if a new user joins the control center. It's only possible to have
 * one user in the control center. Other users will receive an
 * {@link AccessDeniedMessage} and can not join the control center. The client
 * can send and receive several messages ({@link ControlCenterMessage}). The
 * server can use {@link CCWebSocketUser#sendMessage(CCWebSocketMessage)} to
 * send messages to the client. They will be serialized to JSON with the GSON
 * API. The server can receive messages from the user via
 * {@link CCWebSocketUser#onMessage(String)}. This messages will be deserialized
 * with the GSON API and can be processed then.
 *
 * @author Dennis
 * @author Timo
 */
//@ServerEndpoint(value = "/echo")
public class DefaultControlCenterUser extends Endpoint implements
  ControlCenterUser {

  /**
   * Not wanted reg exp in file names.
   */
  private static final String removeFromFileNameRegExp = "\\W";
  /**
   * Date format for exported start parameter file names.
   */
  private static final DateFormat dateFormat = new SimpleDateFormat(
    "yyyy-MM-dd-H-m-s");
  private static final Charset charset = Charset.forName("UTF-8");
  private static final CharsetDecoder decoder = charset.newDecoder();
  private static final Logger log = LoggerFactory.getLogger(
    ControlCenterUser.class);
  /**
   * Used to serialize and deserialize the start parameter.
   */
  @EJB
  private StartParameterSerializerService startParameterSerializerService;
  /**
   * Creates random vehicles, if the user ask for it.
   */
  @EJB
  private CreateRandomVehicleService randomDynamicSensorService;
  /**
   * Used for openstreetmap parsing. To avoid long waiting, this service will
   * serialize the file binary and reload it, if it's already parsed.
   */
  @EJB
  private FileBasedCityDataService osmCityInfrastructureDataService;
  /**
   * This service knows about all sensor interferers.
   */
  @EJB
  private SensorInterfererService sensorInterfererService;
  /**
   * This service gives the energy profiles for all buildings in an area.
   */
  @EJB
  private BuildingEnergyProfileStrategy buildingEnergyProfileStrategy;
  /**
   * This service can create an attraction event {@link AttractionTrafficEvent}
   */
  @EJB
  private CreateAttractionEventService createAttractionEventService;
  @EJB
  private SimulationController simulationController;
  /**
   * Lock to get sure that city infrastructure is parsed before called.
   */
  private final Object cityInfrastructureDataLock = new Object();
  @EJB
  private FileBasedCityDataService cityInfrastructureManager;
  private Properties properties;
  @EJB
  private ControlCenterStartParameter simulationStartParameter;

  /**
   * To serialize the messages.
   */
  private Gson gson;
  /**
   * This service can query bus information.
   */
  @EJB
  private BusService busService;
  /**
   * the websocket session (of Java EE 7's websocket API)
   */
  private Session session;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private TrafficControllerLocal trafficController;
  @EJB
  private WeatherController weatherController;
  @EJB
  private EnergyControllerLocal energyController;

  public DefaultControlCenterUser() {
  }

  /**
   * Constructor
   *
   * @param gson to serialize and deserialize the messages
   * @param serviceDictionary to set and get the controllers.
   */
  public DefaultControlCenterUser(
    Gson gson) {
    this.gson = gson;
    this.properties = new Properties();
    InputStream is = Thread.currentThread().getContextClassLoader().
      getResourceAsStream("/properties.props");
    try {
      this.properties.load(is);
    } catch (IOException e) {
      log.error("Exception",
        e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        log.error("Exception",
          e);
      }
    }
  }

  /**
   * Invoked when message received. All the messages needs to be a subclass of
   * {@link ControlCenterMessage}. If a new message will be added, the behavior
   * needs to be added here.
   *
   * @param message
   */
  @Override
  public void onMessage(String message) {
    log.debug("Incoming message: " + message);
    IdentifiableControlCenterMessage<?> ccWebSocketMessage = this.gson.fromJson(
      message,
      IdentifiableControlCenterMessage.class);

    try {
      switch (ccWebSocketMessage.getMessageType()) {
        case OSM_AND_BUSSTOP_FILE_MESSAGE:
          synchronized (cityInfrastructureDataLock) {
            MapAndBusstopFileMessage osmAndBusstopFileMessage = this.gson.
              fromJson(message,
                MapAndBusstopFileMessage.class);
            String path = Thread.currentThread().getContextClassLoader().
              getResource("/").getPath();
            this.cityInfrastructureManager.parseFile(
              new File(path + osmAndBusstopFileMessage.
                getContent().getOsmFileNames()));
            this.sendMessage(new OSMParsedMessage(ccWebSocketMessage.getId(),
              this.cityInfrastructureManager.createCity().getBoundary().getBoundary().getEnvelopeInternal()));
            return;
          }

        case ASK_FOR_VALID_NODE:
          synchronized (cityInfrastructureDataLock) {
            if (this.cityInfrastructureManager != null) {
              AskForValidNodeMessage askForValidNodeMessage = this.gson.
                fromJson(message,
                  AskForValidNodeMessage.class);
              NavigationNode node;
              if (askForValidNodeMessage.getContent().isOnJunction()) {
                node = this.cityInfrastructureManager.getNearestJunctionNode(
                  askForValidNodeMessage.getContent().getX(),
                  askForValidNodeMessage.getContent().getY());
              } else if (askForValidNodeMessage.getContent().isOnStreet()) {
                node = this.cityInfrastructureManager.getNearestStreetNode(
                  askForValidNodeMessage.getContent().getX(),
                  askForValidNodeMessage.getContent().getY());
              } else {
                node = this.cityInfrastructureManager.getNearestNode(
                  askForValidNodeMessage.getContent().getX(),
                  askForValidNodeMessage.getContent().getY());
              }

              this.sendMessage(new ValidNodeMessage(ccWebSocketMessage.getId(),
                node));
              return;
            } else {
              throw new RuntimeException("OSM not parsed yet!");
            }
          }

        case CREATE_SENSORS_MESSAGE:
          Set<Sensor<?, ?>> sensorHelperList = new HashSet<>(this.gson.
            fromJson(message,
              CreateSensorsMessage.class).getContent());
          for (Sensor<?, ?> sensorHelper : sensorHelperList) {
            sensorHelper.setSensorInterfererTypes(sensorInterfererService.
              getSensorInterfererTypes(sensorHelper.getSensorType(),
                this.simulationStartParameter.
                isWithSensorInterferes()));
          }
          this.simulationController.createSensors(sensorHelperList);
          break;

        case DELETE_SENSORS_MESSAGE:
          this.simulationController.deleteSensors(this.gson.fromJson(message,
            DeleteSensorsMessage.class).getContent());
          break;

        case SIMULATION_START_PARAMETER:
          onStartParameterMessage(message,
            ccWebSocketMessage
          );
          break;

        case SIMULATION_INIT_PARAMETER:
          onInitParameterMessage(message,
            ccWebSocketMessage);
          break;
        case SIMULATION_STOP:
          this.simulationController.stop();
          break;

        case SIMULATION_EVENT_LIST:
          EventList sel = this.gson.fromJson(message,
            SimulationEventListMessage.class).getContent();
          this.simulationController.update(sel);
          break;

        case CREATE_RANDOM_VEHICLES: {
          List<TrafficEvent> simulationEventList = new LinkedList<>();
          RandomVehicleBundle rvb = this.gson.fromJson(message,
            CreateRandomVehiclesMessage.class).getContent();
          simulationEventList.add(randomDynamicSensorService.
            createRandomDynamicSensors(rvb,
              randomSeedService,
              this.simulationStartParameter.isWithSensorInterferes()));
          this.simulationController.update(
            new EventList(idGenerator.getNextId(),
              simulationEventList,
              0));

          /* Send message with new used ids: */
          List<Sensor<?, ?>> newIntegerIDs = new LinkedList<>();
          List<UUID> newUUIDs = new LinkedList<>();
//					this.sendMessage(new UsedIDsMessage(ccWebSocketMessage.getId(),
//						new IDWrapper(newIntegerIDs,
//							newUUIDs)));
          return;
        }

        case CREATE_ATTRACTION_EVENTS_MESSAGE: {
          List<AbstractEvent> simulationEventList = new LinkedList<>();
          Collection<AttractionData> attractionDataCollection = this.gson.
            fromJson(message,
              CreateAttractionEventsMessage.class).
            getContent();
          for (AttractionData attractionData : attractionDataCollection) {
            simulationEventList.add(createAttractionEventService.
              createAttractionTrafficEvent(
                attractionData.getRandomVehicleBundle(),
                randomSeedService,
                this.simulationStartParameter.
                isWithSensorInterferes(),
                attractionData.getNodeID(),
                attractionData.getAttractionPoint(),
                attractionData.getAttractionStartTimestamp(),
                attractionData.getAttractionEndTimestamp()));
          }
          this.simulationController.update(
            new EventList(idGenerator.getNextId(),
              simulationEventList,
              0));

          /* Send message with new used ids: */
          List<Sensor<?, ?>> newIntegerIDs = new LinkedList<>();
          List<UUID> newUUIDs = new LinkedList<>();
//					this.sendMessage(new UsedIDsMessage(ccWebSocketMessage.getId(),
//						new IDWrapper(newIntegerIDs,
//							newUUIDs)));
          return;
        }
        case SIMULATION_EXPORT_PARAMETER:
          SimulationExportStartParameterMessage simulationExportParameterMessage = this.gson.
            fromJson(message,
              SimulationExportStartParameterMessage.class);
          this.sendMessage(new SimulationExportedMessage(
            simulationExportParameterMessage.getId(),
            startParameterSerializerService.serialize(
              simulationExportParameterMessage.getContent().
              getCcSimulationStartParameter(),
              simulationExportParameterMessage.getContent().
              getFileName())));

          log.debug("Startparameter: " + message);

          return;

        case LOAD_SIMULATION_START_PARAMETER:
          LoadSimulationStartParameterMessage loadSimulationStartParameterMessage = this.gson.
            fromJson(message,
              LoadSimulationStartParameterMessage.class);

          String path = Thread.currentThread().getContextClassLoader().
            getResource("/").getPath().replaceAll("WEB-INF/classes",
              "");

          this.sendMessage(new SimulationStartParameterMessage(
            loadSimulationStartParameterMessage.getId(),
            startParameterSerializerService.deserialize(
              new FileInputStream(
                new File(
                  path
                  + loadSimulationStartParameterMessage.
                  getContent())))));
          return;

        case IMPORT_XML_START_PARAMETER:
          ImportXMLStartParameterMessage importXMLStartParameterMessage = this.gson.
            fromJson(message,
              ImportXMLStartParameterMessage.class);

          log.debug("XML:" + importXMLStartParameterMessage.getContent());

          this.sendMessage(new SimulationStartParameterMessage(
            importXMLStartParameterMessage.getId(),
            startParameterSerializerService.deserialize(
              importXMLStartParameterMessage.getContent())));
          return;

        default:
          break;
      }

      /* Everything was okay: */
      this.sendMessage(
        new GenericNotificationMessage(ccWebSocketMessage.getId(),
          ""));
    } catch (Exception e) {
      log.error("Exception",
        e);
      try {
        this.sendMessage(new ErrorMessage(ccWebSocketMessage.getId(),
          new ErrorMessageData(e.getMessage(),
            ccWebSocketMessage.
            getMessageType())));
      } catch (IOException e1) {
        log.error("Exception",
          e1);
      }
    }
  }

  /**
   * Send a message to the client.
   *
   * @param message
   * @throws IOException
   */
  @Override
  public void sendMessage(ControlCenterMessage<?> message) throws IOException {
    log.debug("Sending: " + message.toJson(this.gson));
    this.session.getBasicRemote().sendText(message.toJson(this.gson));
  }

  /**
   * Message from client
   *
   * @param message
   * @param session
   * @return
   */
  @OnMessage
  @Override
  public String onMessage(String message,
    Session session) {
    return message;
  }

  @Override
  public void onOpen(Session session,
    EndpointConfig config) {
    this.session = session;
    throw new UnsupportedOperationException(
      "handle login mechanism (user mustn't have servlet reference)");
//		if (ccWebSocketServlet.getUser() == this) {
//			try {
//				this.
//					sendMessage(new OnConnectMessage(this.busService.
//							getAllBusRoutes()));
//			} catch (IOException e) {
//				log.error("Exception",
//					e);
//			}
//		} else {
//			try {
//				this.sendMessage(new AccessDeniedMessage("Only one user allowed!"));
//			} catch (IOException e) {
//				log.error("Exception",
//					e);
//			}
//		}
  }

  /**
   * Returns a random unused integer id.
   *
   * @param usedIDs
   * @param random
   * @return
   */
  private int getUniqueRandomIntID(Set<Integer> usedIDs,
    Random random) {
    int randomValue = random.nextInt(Integer.MAX_VALUE);
    while (usedIDs.contains(randomValue)) {
      randomValue = random.nextInt(Integer.MAX_VALUE);
    }
    return randomValue;
  }

  private void onStartParameterMessage(String message,
    IdentifiableControlCenterMessage<?> ccWebSocketMessage) throws IOException, SensorException, InitializationException {
    log.debug("Start simulation");

    ControlCenterStartParameter ccSimulationStartParameter = this.gson.
      fromJson(message,
        SimulationStartParameterMessage.class).
      getContent();
    this.simulationStartParameter = ccSimulationStartParameter;

    /* Create server configuration: */
    Map<String, List<ServerConfigurationEntity>> serverConfiguationMap = new HashMap<>();

    log.debug("Create server configuration");

    /* Created server configuration */
    /* Create init parameters: */

    /* Create sensors: */
    for (Sensor<?, ?> sensorHelper : ccSimulationStartParameter.getSensors()) {
      sensorHelper.setSensorInterfererTypes(sensorInterfererService.
        getSensorInterfererTypes(sensorHelper.getSensorType(),
          ccSimulationStartParameter.isWithSensorInterferes()));
    }
    this.simulationController.createSensors(ccSimulationStartParameter.
      getSensors());

    /* Create start parameters: */
    TrafficStartParameter startParameter = new TrafficStartParameter();
    startParameter.setCity(ccSimulationStartParameter.getCity());
    startParameter.setAggregatedWeatherDataEnabled(
      ccSimulationStartParameter.isAggregatedWeatherDataEnabled());
    startParameter.setWeatherEvents(ccSimulationStartParameter.
      getWeatherEvents());

    List<Event> simulationEventList = new LinkedList<>();

    /* Create random vehicles: */
    CreateRandomVehiclesEvent<?> createRandomVehiclesEvent = (CreateRandomVehiclesEvent<?>) randomDynamicSensorService.
      createRandomDynamicSensors(ccSimulationStartParameter.
        getRandomDynamicSensorBundle(),
        randomSeedService,
        ccSimulationStartParameter.isWithSensorInterferes());
    simulationEventList.add(createRandomVehiclesEvent);

    /* @TODO?: Save the new and old IDs from create random vehicles: */

    /* Create attractions: */
    for (AttractionData attractionData : ccSimulationStartParameter.
      getAttractionCollection()) {
      /* Before creating them, update the used IDs: */
      AttractionTrafficEvent<?> attractionTrafficEvent = createAttractionEventService.
        createAttractionTrafficEvent(
          attractionData.getRandomVehicleBundle(),
          randomSeedService,
          this.simulationStartParameter.
          isWithSensorInterferes(),
          attractionData.getNodeID(),
          attractionData.getAttractionPoint(),
          attractionData.getAttractionStartTimestamp(),
          attractionData.getAttractionEndTimestamp());
      simulationEventList.add(attractionTrafficEvent);

      /* @TODO?: Update the new used IDs: */
    }

    /* Create busses: */
    log.debug("Selected bus routes in cc: " + ccSimulationStartParameter.
      getBusRoutes().size());

    List<CreateRandomVehicleData> busDataList = new LinkedList<>();
    List<BusRoute> busRouteList = new LinkedList<>();
    for (BusRoute busRoute : ccSimulationStartParameter.getBusRoutes()) {
      if (busRoute.getUsed()) {
        log.debug("Selected bus route: " + busRoute.getId());
        busRouteList.add(busRoute);
      } else {
        log.debug("Not selected bus route: " + busRoute.getId());
      }
    }

    log.debug("Selected bus routes: " + busRouteList.size());

    int totalNumberOfBusTrips = this.busService.getTotalNumberOfBusTrips(
      busRouteList,
      ccSimulationStartParameter.getStartTimestamp().getTime());

    log.debug("Create " + totalNumberOfBusTrips + " busses!");

    for (int i = 0; i < totalNumberOfBusTrips; i++) {
      UUID id = UUID.randomUUID();
      busDataList.add(new CreateRandomBusData(null,
        null,
        new VehicleInformation(true,
          VehicleTypeEnum.BUS,
          VehicleModelEnum.BUS_CITARO,
          null,
          id.toString())));
    }
    simulationEventList.add(new CreateBussesEvent(trafficController,
      ccSimulationStartParameter.getStartTimestamp().getTime(),
      0L,
      busDataList,
      busRouteList));

    /* Add events to simulation: */
    this.simulationController.addSimulationEventList(new EventList<>(
      idGenerator.getNextId(),
      simulationEventList,
      0));

    /* Add event lists from startparameter: */
    for (EventList<?> tmpList : ccSimulationStartParameter.
      getSimulationEventLists()) {
      this.simulationController.addSimulationEventList(tmpList);
    }

    /* Start the controllers: */
    this.simulationController.start(startParameter);

    /* Save start parameter: */
    startParameterSerializerService.serialize(ccSimulationStartParameter,
      ccSimulationStartParameter.getCity().getName().replaceAll(
        removeFromFileNameRegExp,
        "") + "_"
      + dateFormat.format(new Date())
      + "." + this.properties.getProperty(
        "suffixStartParameterXML"));

    /* Send message with new used ids: */
    this.sendMessage(new SimulationStartedMessage(ccWebSocketMessage.
      getId()));
    return;
  }

  public void onInitParameterMessage(String message,
    IdentifiableControlCenterMessage<?> ccWebSocketMessage) throws IOException, SensorException, InitializationException {

    TrafficInitParameter initParameter = this.gson.
      fromJson(message,
        SimulationInitParameterMessage.class).
      getContent();

    /* Init: */
    this.simulationController.init(initParameter);
  }
}
