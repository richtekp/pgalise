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
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.traffic.VehicleType;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficTrip;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.event.DeleteVehiclesEvent;
import de.pgalise.simulation.traffic.governor.TrafficGovernor;
import de.pgalise.simulation.traffic.internal.server.DefaultVehicleEventHandlerManager;
import de.pgalise.simulation.traffic.internal.server.eventhandler.GenericVehicleEvent;
import de.pgalise.simulation.traffic.internal.server.eventhandler.vehicle.VehicleEventTypeEnum;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersection;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersectionSensor;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightSensor;
import de.pgalise.simulation.traffic.internal.server.scheduler.SchedulerComposite;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.model.RoadBarrier;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleFactory;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.VehicleAmountManager;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandlerManager;
import de.pgalise.simulation.traffic.server.route.RouteConstructor;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import de.pgalise.util.generic.async.AsyncHandler;
import de.pgalise.util.generic.async.impl.ThreadPoolHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateful
@Local(TrafficControllerLocal.class)
public class DefaultTrafficController extends AbstractController<VehicleEvent, TrafficStartParameter, TrafficInitParameter>
  implements TrafficControllerLocal<VehicleEvent>, ScheduleHandler {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultTrafficController.class);
  private static final String NAME = "TrafficController";
  private static final long serialVersionUID = 1L;

  private transient AsyncHandler asyncHandler = new ThreadPoolHandler();

  /**
   * the complete area managed by this controller
   */
  private Geometry area;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private TrafficGraphExtensions trafficGraphExtensions;
  @EJB
  private VehicleFactory vehicleFactory;
  private long trafficServerUpdateInterval = 10L;
  @EJB
  private TrafficSensorFactory trafficSensorFactory;
  @EJB
  private TrafficSensorController trafficSensorController;
  @EJB
  private RouteConstructor routeConstructor;
  @EJB
  private TrafficGovernor trafficGovernor;
  @EJB
  private VehicleAmountManager vehicleAmountManager;
  private Output output;

  @Override
  public Set<TrafficSensor> getAllManagedSensors() {
    Set<TrafficSensor> retValue = new HashSet<>();
    for (Vehicle vehicle : managedVehicles) {
      retValue.add(vehicle.getGpsSensor());
    }
    return retValue;
  }

  /**
   * Vehicle event handler manager
   */
  private VehicleEventHandlerManager vehicleEventHandlerManager;

  /**
   * Sensor registry
   */
  private Set<StaticTrafficSensor> sensorRegistry = new HashSet<>();

  /**
   * Sensor factory
   */
  private TrafficSensorFactory sensorFactory;

  private ConfigReader configReader;

  // does not need to be injected
  /**
   * Composite of schedulers
   */
  private SchedulerComposite scheduler;

  /**
   * List with items which should be scheduled after an update. The list
   * contains vehicles which drove to an attraction and will drive back.
   */
  private List<ScheduleItem> itemsToScheduleAfterAttractionReached;

  private List<ScheduleItem> itemsToScheduleAfterFuzzy;

  private List<Vehicle<?>> itemsToRemoveAfterFuzzy;

  /**
   * Traffic sensor controller
   */
  private TrafficSensorController sensorController;

  /**
   * City zone of the server
   */
  private Geometry cityZone;

  /**
   * Current timestamp of the simulation
   */
  private long currentTime;

  /**
   * Update interval of the simulation for the traffic servers
   */
  private long updateIntervall;
  private Map<Long, VehicleEvent> eventForVehicle;
  private TrafficGovernor fuzzyTrafficGovernor;
  private VehicleAmountManager vehicleFuzzyManager;
  /**
   * All listed road barriers
   */
  private Set<RoadBarrier> listedRoadBarriers = new HashSet<>();
  private Set<Vehicle<?>> managedVehicles = new HashSet<>();
  private TrafficGraph graph = new DefaultTrafficGraph();

  public DefaultTrafficController() {
  }

  /**
   * Default constructor
   *
   * @param area
   */
  public DefaultTrafficController(Geometry area) {
    asyncHandler = new ThreadPoolHandler();
    this.area = area;
  }

  @Override
  protected void onInit(final TrafficInitParameter param) throws InitializationException {
    this.output = param.getOutput();
    area = param.getCity().getGeoInfo().getBoundaries();
    init0(); //initializes vehicleEventHandlerManager
    this.vehicleEventHandlerManager.init(param);
    try {
      this.loadEventHandler();
      this.sensorController.init(param);

      this.updateIntervall = param.getInterval();
      this.fuzzyTrafficGovernor.init(param);

      // this.trafficGraphExtensions.setGraph(this.getGraph());
      // this.trafficGraphExtensions.setRouteConstructor(this.routeConstructor);
    } catch (IOException | ClassNotFoundException | IllegalAccessException | IllegalStateException | InstantiationException e) {
      throw new InitializationException(e);
    }
  }

  @Override
  protected void onUpdate(EventList<VehicleEvent> simulationEventList) {
    /*
     * Handle incoming events
     */
    for (VehicleEvent event : simulationEventList.getEventList()) {
      this.vehicleEventHandlerManager.handleEvent(event);
    }

    this.scheduler.changeModus(ScheduleModus.READ);
    /*
     * update vehicles and update handlers
     */
    List<Vehicle<?>> removeableVehicles = this.updateVehicles(
      simulationEventList.getTimestamp(),
      simulationEventList);
    this.scheduler.changeModus(ScheduleModus.WRITE);

    this.getScheduler().removeExpiredItems(removeableVehicles);

    for (ScheduleItem item : itemsToScheduleAfterAttractionReached) {
      this.getScheduler().scheduleItem(item);
    }
    itemsToScheduleAfterAttractionReached.clear();

    if (this.fuzzyTrafficGovernor != null) {
      this.vehicleFuzzyManager.checkVehicleAmount(simulationEventList.
        getTimestamp());
    }

    this.scheduler.removeScheduledItems(itemsToRemoveAfterFuzzy);
    itemsToRemoveAfterFuzzy.clear();

    for (ScheduleItem item : itemsToScheduleAfterFuzzy) {
      this.getScheduler().scheduleItem(item);
    }
    itemsToScheduleAfterFuzzy.clear();

    /*
     * Update traffic rules
     */
    this.trafficGraphExtensions.updateTrafficRules(simulationEventList);

    /*
     * Update road barriers
     */
    this.updateRoadBarriers(simulationEventList.getTimestamp());
  }

  @Override
  public boolean isActivated(TrafficSensor sensor) {
    return this.sensorController.isActivated(sensor);
  }

  @Override
  public void createSensors(Set<TrafficSensor> sensors) {
    for (TrafficSensor sensor : sensors) {
      this.createSensor(sensor);
    }
  }

  @Override
  public void deleteSensors(Set<TrafficSensor> sensors) {
    for (TrafficSensor sensor : sensors) {
      this.deleteSensor(sensor);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  public void initialize() {
    this.init0();
  }

  @Override
  public void createSensor(TrafficSensor sensor) {
    this.sensorController.createSensor(sensor);
    if (sensor instanceof TrafficLightIntersectionSensor) {
      final TrafficLightIntersectionSensor helper = (TrafficLightIntersectionSensor) sensor;
      final TrafficNode node = this.getGraph().getNodeClosestTo(helper.
        getPosition());
      final TrafficLightIntersection trafficLightIntersection = new TrafficLightIntersection(
        node,
        getGraph(),
        this.trafficGraphExtensions);
      this.trafficGraphExtensions.setTrafficRule(node,
        trafficLightIntersection);
      this.sensorRegistry.add(new TrafficLightSensor(this.idGenerator.
        getNextId(),
        output,
        null,
        trafficLightIntersection.getTrafficLight0()));
      this.sensorRegistry.add(new TrafficLightSensor(this.idGenerator.
        getNextId(),
        output,
        null,
        trafficLightIntersection.getTrafficLight1()));
    }
  }

  @Override
  public TrafficTrip createTimedTrip(Geometry cityZone,
    VehicleType vehicleType,
    Date date,
    int buffer) {
    return this.routeConstructor.createTimedTrip(this,
      cityZone,
      vehicleType,
      date,
      buffer);
  }

  @Override
  public TrafficTrip createTrip(Geometry cityZone,
    TrafficNode nodeID,
    long startTimestamp,
    boolean isStartNode) {
    return this.routeConstructor.createTrip(this,
      cityZone,
      nodeID,
      startTimestamp,
      isStartNode);
  }

  @Override
  public TrafficTrip createTrip(Geometry cityZone,
    VehicleType vehicleType) {
    return this.routeConstructor.createTrip(this,
      cityZone,
      vehicleType);
  }

  @Override
  public TrafficTrip createTrip(TrafficNode startNodeID,
    TrafficNode targetNodeID,
    long startTimestamp) {
    return this.routeConstructor.createTrip(this,
      startNodeID,
      targetNodeID,
      startTimestamp);
  }

  @Override
  public void deleteSensor(TrafficSensor sensor) {
    this.sensorController.deleteSensor(sensor);
  }

  @Override
  public BicycleFactory getBikeFactory() {
    return this.vehicleFactory;
  }

  @Override
  public BusFactory getBusFactory() {
    return this.vehicleFactory;
  }

  @Override
  public List<TrafficEdge> getBusRoute(List<BusStop> busStopIds) {
    return this.routeConstructor.getBusRoute(busStopIds);
  }

  @Override
  public CarFactory getCarFactory() {
    return this.vehicleFactory;
  }

  @Override
  public Geometry getCityZone() {
    return this.area;
  }

  @Override
  public TrafficGraph getGraph() {
    return this.graph;
  }

  @Override
  public MotorcycleFactory getMotorcycleFactory() {
    return this.vehicleFactory;
  }

  @Override
  public Scheduler getScheduler() {
    return this.scheduler;
  }

  @Override
  public List<TrafficEdge> getShortestPath(TrafficNode start,
    TrafficNode dest) {
    return this.routeConstructor.getShortestPath(start,
      dest);
  }

  @Override
  public TrafficGraphExtensions getTrafficGraphExtesions() {
    return this.trafficGraphExtensions;
  }

  @Override
  public TruckFactory getTruckFactory() {
    return this.vehicleFactory;
  }

  @Override
  public long getUpdateIntervall() {
    return this.updateIntervall;
  }

  @Override
  public void onRemove(ScheduleItem v) {
    this.vehicleEventHandlerManager.handleEvent(new DeleteVehiclesEvent<>(this,
      0,
      0,
      v
      .getVehicle()));
    this.sensorController.onRemove(v.getVehicle());
  }

  @Override
  public void onSchedule(ScheduleItem v) {
    this.vehicleEventHandlerManager.handleEvent(new GenericVehicleEvent<>(this,
      0,
      0,
      v
      .getVehicle(),
      VehicleEventTypeEnum.VEHICLE_ADDED));
    this.sensorController.onSchedule(v.getVehicle());
  }

  /**
   * - Die Warteschlangen werden vor ihrer Bearbeitung sortiert - Beim
   * Abarbeiten werden neue Autos erstellt (weil das Auto selbst nicht übergeben
   * werden konnte) - Beim Bearbeiten wird jedes Auto in eine Hashmap
   * eingetragen (Key: Position als String) - Nachdem ein Auto geupdatet wurde
   * und ein entsprechender Eintrag in der HashMap fehlt: - Auto darf sich das
   * Auto an der Kante anmelden - Auto wird der ExpiredItems-List hinzugefügt -
   * Nachdem ein Auto geupdatet wurde und ein entsprechender Eintrag in der
   * HashMap existiert: - Auto wird nicht an der Kante angemeldet und auch nicht
   * der ExpiredItems-Liste hinzugefügt - Nach Bearbeitung HashMap wieder leeren
   */
  @Override
  public void processMovedVehicles() {
    JaxRSCoordinate posBeforeUpdate;
    for (Iterator<Vehicle<?>> i = this.managedVehicles.iterator(); i.
      hasNext();) {
      Vehicle rv = i.next();
      posBeforeUpdate = rv.getPosition();
      this.vehicleEventHandlerManager.handleEvent(
        new GenericVehicleEvent<>(this,
          this.currentTime,
          this.updateIntervall,
          rv,
          VehicleEventTypeEnum.VEHICLE_UPDATE));

      Set<Vehicle<?>> vehicles = this.trafficGraphExtensions.getVehiclesOnNode(
        rv.getCurrentNode(),
        rv.getData().getType());
      if (vehicles.isEmpty()) {
        if (rv.getPosition().toString().equals(posBeforeUpdate)) {
          vehicles.add(rv);
          log.debug(
            "Vehicle " + rv.getName() + " registered on node "
            + rv.getCurrentNode().getId());
        }
        rv.setVehicleState(VehicleStateEnum.NOT_STARTED);
        ScheduleItem item = new ScheduleItem(rv,
          this.currentTime,
          this.updateIntervall);
        log.debug(String.format(
          "Scheduled moved vehicle %s to drive on next update",
          item));
        // item.setLastUpdate(this.currentTime + this.updateIntervall);
        this.scheduler.scheduleItem(item);
        i.remove();
      } else {
        log.debug("Could not process moved vehicle " + rv.getName()
          + ". There is already a vehicle on the same position, amount: " + vehicles.
          size());
        this.trafficGraphExtensions.unregisterFromEdge(rv.
          getCurrentEdge(),
          rv
          .getCurrentNode(),
          rv.getNextNode(),
          rv);
      }
    }
  }

  public void setCityZone(Geometry cityZone) {
    this.cityZone = cityZone;
  }

  @Override
  public void takeVehicle(Vehicle<?> vehicle,
    TrafficNode startNodeId,
    TrafficNode targetNodeId) {
    vehicle.setTrafficGraphExtensions(this.trafficGraphExtensions);
    vehicle.setPath(this.routeConstructor.getShortestPath(
      startNodeId,
      targetNodeId));
    vehicle.setVehicleState(VehicleStateEnum.PAUSED);
    this.managedVehicles.add(vehicle);
  }

  /**
   * Instanciate dependencies for the traffic server.
   * <tt>vehicleEventHandlerManager</tt> still needs to be initialized with {@link VehicleEventHandlerManager#init(de.pgalise.simulation.service.InitParameter)
   * }.
   */
  private void init0() {
    this.vehicleEventHandlerManager = new DefaultVehicleEventHandlerManager(
      randomSeedService,
      this,
      output);
  }

  /**
   * Loads all event handlers from the files "eventhandler.conf" and
   * "updatehandler.conf".
   *
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IOException
   */
  private void loadEventHandler() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
    IOException {
    // Load event handlers
    try (InputStream stream = DefaultTrafficController.class.
      getResourceAsStream(
        "/eventhandler.conf")) {
        this.vehicleEventHandlerManager.init(stream,
          Thread.currentThread().getContextClassLoader());
        for (TrafficEventHandler<VehicleEvent> a : this.vehicleEventHandlerManager) {
          a.init(this);
        }
      }
      // Load update handlers
      try (InputStream stream = DefaultTrafficController.class.
        getResourceAsStream(
          "/updatehandler.conf")) {
          this.vehicleEventHandlerManager.init(stream,
            Thread.currentThread().getContextClassLoader());
        }
  }

  /**
   * Updates all vehicles of the traffic server
   *
   * @param currentTime Current simulation timestamp
   * @param simulationEventList List with simulation events
   * @return List with vehicles that have to remove from the server
   */
  private List<Vehicle<? extends VehicleData>> updateVehicles(long currentTime,
    EventList<VehicleEvent> simulationEventList) {
    List<Vehicle<? extends VehicleData>> removeableVehicles = new ArrayList<>();
    this.currentTime = currentTime;

    List<ScheduleItem> expiredItems = this.scheduler.
      getExpiredItems(currentTime);
    for (ScheduleItem item : expiredItems) {
      Vehicle<? extends VehicleData> vehicle = item.getVehicle();

      if (VehicleStateEnum.UPDATEABLE_VEHICLES.contains(vehicle.
        getVehicleState())) {
        long elapsedTime = currentTime - item.getLastUpdate();
        item.setLastUpdate(currentTime);
        // log.debug("Elapsed time since last vehicle update: "+elapsedTime);/
        TrafficNode varNode = vehicle.getCurrentNode();

        this.vehicleEventHandlerManager.handleEvent(
          new GenericVehicleEvent<>(
            this,
            currentTime,
            elapsedTime,
            vehicle,
            VehicleEventTypeEnum.VEHICLE_UPDATE));

        // if (!varNode.getId().equals(vehicle.getCurrentNode().getId())) {
        int startNode = vehicle.getIndex(varNode);
        int actualNode = vehicle.getIndex(vehicle.getCurrentNode());
        // log.debug("Vehicle "+vehicle.getName()+" passed Node from index #"+startNode
        // +" to #"+actualNode);
        loop:
        for (int k = startNode; k < actualNode; k++) {
          // log.debug("Loop: Vehicle "+vehicle.getName()+" passed Node from index #"+k +" to #"+(k+1));
          varNode = vehicle.getNodePath().get(k);// 0
          TrafficNode curNode = vehicle.getNodePath().get(k + 1);// 1
          if (!varNode.equals(curNode) && (vehicle.
            getVehicleState() != VehicleStateEnum.REACHED_TARGET)) {
            log.debug(
              "Vehicle " + vehicle.getName() + " passed node " + curNode);
            this.vehicleEventHandlerManager.handleEvent(
              new GenericVehicleEvent<>(
                this,
                currentTime,
                0,
                vehicle,
                VehicleEventTypeEnum.VEHICLE_PASSED_NODE));
          } else if (!varNode.getId().equals(curNode.getId()) && (vehicle.
            getVehicleState() == VehicleStateEnum.REACHED_TARGET)) {
            removeableVehicles.add(vehicle);
            this.vehicleEventHandlerManager.handleEvent(
              new GenericVehicleEvent<>(this,
                currentTime,
                0,
                vehicle,
                VehicleEventTypeEnum.VEHICLE_REACHED_TARGET));
          }
        }

        if (varNode.getId().equals(vehicle.getNodePath().get(0).getId())
          && (item.getScheduleTime() == currentTime)) {
          log.debug(
            "Vehicle " + vehicle.getName() + " passed startNode " + varNode.
            getId());
          this.vehicleEventHandlerManager.handleEvent(new GenericVehicleEvent<>(
            this,
            currentTime,
            0,
            vehicle,
            VehicleEventTypeEnum.VEHICLE_PASSED_NODE));
        }
        // }
      } else if (vehicle.getVehicleState() == VehicleStateEnum.IN_TRAFFIC_RULE) {
        item.setLastUpdate(currentTime);
      } else if (vehicle.getVehicleState() == VehicleStateEnum.IN_TRAFFIC_RULE) {
        item.setLastUpdate(currentTime);
      }
      // log.debug(String.format("Vehicle %s changed position to %s", vehicle.getName(), vehicle.getPosition()));
    }

    return removeableVehicles;
  }

  @Override
  protected void onReset() {
    this.scheduler.changeModus(ScheduleModus.WRITE);
    this.scheduler.clearExpiredItems();
    this.scheduler.clearScheduledItems();
    // log.debug("Scheduler resetted, expired #items: "
    // + this.scheduler.getExpiredItems(this.currentTime).size());
    // log.debug("Scheduler resetted, scheduled #items: "
    // + this.scheduler.getScheduledItems().size());
    this.listedRoadBarriers.clear();
    this.trafficGraphExtensions.reset();
    this.vehicleEventHandlerManager.clear();
    this.sensorController.reset();
  }

  @Override
  protected void onResume() {
    this.sensorController.start(null);
  }

  @Override
  protected void onStart(TrafficStartParameter param) {
    this.sensorController.start(param);
  }

  @Override
  protected void onStop() {
    this.sensorController.stop();
  }

  public void updateRoadBarriers(long timestamp) {
    List<RoadBarrier> removeItems = new ArrayList<>();

    // Check end timestamp of the road barriers
    for (RoadBarrier roadBarrier : this.listedRoadBarriers) {
      if (roadBarrier.getEnd() <= timestamp) {

        // Add edges to graph
        for (TrafficEdge edge : roadBarrier.getEdges()) {
          this.addEdgeToGraph(edge);
        }

        // Remember the road barrier to remove
        removeItems.add(roadBarrier);
      }
    }

    // Delete expired road barriers
    if (!removeItems.isEmpty()) {
      this.listedRoadBarriers.removeAll(removeItems);
    }
  }

  /**
   * Add edge to graph
   *
   * @param edge Edge to add
   */
  private void addEdgeToGraph(final TrafficEdge edge) {
    // Add edge to the graph
    this.getGraph().addEdge(
      edge.getSource(),
      edge.getTarget(),
      edge);
  }

  @Override
  public void addNewRoadBarrier(RoadBarrier barrier) {
    // Remember the barrier
    this.listedRoadBarriers.add(barrier);
  }

  @Override
  public Set<TrafficEdge> getBlockedRoads(long timestamp) {
    Set<TrafficEdge> blockNodes = new HashSet<>();

    // Adds the node IDs to the set
    for (RoadBarrier roadBarrier : this.listedRoadBarriers) {
      if (timestamp >= roadBarrier.getStart() && timestamp < roadBarrier.
        getEnd()) {
        blockNodes.addAll(roadBarrier.getEdges());
      }
    }

    return blockNodes;
  }

  @Override
  public Map<Long, VehicleEvent> getEventForVehicle() {
    return eventForVehicle;
  }

  @Override
  public List<ScheduleItem> getItemsToScheduleAfterAttractionReached() {
    return this.itemsToScheduleAfterAttractionReached;
  }

  @Override
  public List<ScheduleItem> getItemsToScheduleAfterFuzzy() {
    return this.itemsToScheduleAfterFuzzy;
  }

  @Override
  public List<Vehicle<?>> getItemsToRemoveAfterFuzzy() {
    return this.itemsToRemoveAfterFuzzy;
  }

  @Override
  public VehicleAmountManager getVehicleFuzzyManager() {
    return vehicleFuzzyManager;
  }

  @Override
  public long getCurrentTime() {
    return this.currentTime;
  }

  @Override
  public Set<Vehicle<?>> getManagedVehicles() {
    return managedVehicles;
  }

  public void setManagedVehicles(
    Set<Vehicle<?>> managedVehicles) {
    this.managedVehicles = managedVehicles;
  }
}
