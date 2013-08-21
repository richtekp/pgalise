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
 
package de.pgalise.simulation.traffic.internal.server;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.configReader.Identifier;
import de.pgalise.simulation.service.event.SimulationEventHandler;
import de.pgalise.simulation.service.event.SimulationEventHandlerManager;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.traffic.TrafficEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorHelperTrafficLightIntersection;
import de.pgalise.simulation.shared.traffic.TrafficTrip;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.governor.TrafficGovernor;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.XMLVehicleFactory;
import de.pgalise.simulation.traffic.internal.server.route.DefaultRouteConstructor;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersection;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightSensor;
import de.pgalise.simulation.traffic.internal.server.scheduler.SortedListScheduler;
import de.pgalise.simulation.traffic.internal.server.sensor.DefaultSensorController;
import de.pgalise.simulation.traffic.model.RoadBarrier;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.MotorcycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle.State;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.VehicleAmountManager;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandlerManager;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventType;
import de.pgalise.simulation.traffic.server.route.RouteConstructor;
import de.pgalise.simulation.traffic.server.scheduler.Item;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler.Modus;
import de.pgalise.simulation.traffic.server.scheduler.SchedulerComposite;

/**
 * Default implementation of the traffic server.
 * Possible extension points:<br/>
 * Event handler in server.eventhandler are used to
 * handle incoming simulation evnets.<br/><br/>
 * 
 * Event handler in server.eventhandler.vehicle are used to
 * handler vehicle events referring a single vehicle (and its changed status).
 * 
 * @author Mustafa
 * @author Lena
 * @version 1.0 (Feb 17, 2013)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.server.TrafficServer")
@Local(TrafficServerLocal.class)
@Remote(TrafficServer.class)
public class DefaultTrafficServer extends AbstractController implements TrafficServerLocal, ScheduleHandler {
	/**
	 * If a vehicle drove out of the boundaries of a server it will be passed to the 
	 * server which is responsible for the city zone in which the vehicle wants to drive. 
	 * For scheduling the vehicle (on the new server) in the right order the ReceivedVehicle 
	 * contains information about the vehicle itself and the previous server.
	 * 
	 * @author Mustafa
	 * @version 1.0 (Feb 17, 2013)
	 */
	public static class ReceivedVehicle implements Comparable<ReceivedVehicle> {

		/**
		 * Vehicle
		 */
		private final Vehicle<? extends VehicleData> vehicle;

		/**
		 * ID of the server
		 */
		private final int serverId;

		/**
		 * Constructor
		 * 
		 * @param vehicle
		 *            Vehicle
		 * @param serverId
		 *            ID of the server
		 */
		public ReceivedVehicle(Vehicle<? extends VehicleData> vehicle, int serverId) {
			this.vehicle = vehicle;
			this.serverId = serverId;
		}

		@Override
		public int compareTo(ReceivedVehicle o) {
			if (o.serverId > this.serverId) {
				return -1;
			} else if (o.serverId < this.serverId) {
				return 1;
			} else {
				return 0;
			}
		}

		public int getServerId() {
			return this.serverId;
		}

		public Vehicle<? extends VehicleData> getVehicle() {
			return this.vehicle;
		}
	}

	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultTrafficServer.class);
	private static final String NAME = "TrafficServer";

	/*
	 * API Dependencies
	 */

	/**
	 * Option for JUnit tests
	 */
	private static boolean JUNIT_TEST = false;

	/**
	 * GPS mapper (as EJB)
	 */
	private Coordinate mapper;

	/**
	 * Service dictionary (as EJB)
	 */
	@EJB
	private ServiceDictionary serviceDictionary;

	/**
	 * Traffic graph extension (as EJB)
	 */
	@EJB
	private TrafficGraphExtensions trafficGraphExtensions;

	// does not need to be injected
	/**
	 * Vehicle factory to create new cars
	 */
	private XMLVehicleFactory vehicleFactory;
	/*
	 * Internal Dependencies
	 */
	/**
	 * Event handler manager
	 */
	@EJB
	private SimulationEventHandlerManager eventHandlerManager;

	/**
	 * Vehicle event handler manager
	 */
	@EJB
	private VehicleEventHandlerManager vehicleEventHandlerManager;

	/**
	 * Sensor registry
	 */
	@EJB
	private SensorRegistry sensorRegistry;

	/**
	 * Sensor factory
	 */
	@EJB
	private SensorFactory sensorFactory;

	/**
	 * Server configurations
	 */
	@EJB
	private ServerConfigurationReader serverConfigReader;

	/**
	 * Config reader
	 */
	@EJB
	private ConfigReader configReader;

	// does not need to be injected
	/**
	 * Composite of schedulers
	 */
	private SchedulerComposite scheduler;

	/**
	 * List with items which should be scheduled after an update. The list contains vehicles which drove to an
	 * attraction and will drive back.
	 */
	private List<Item> itemsToScheduleAfterAttractionReached;

	private List<Item> itemsToScheduleAfterFuzzy;

	private List<Vehicle<? extends VehicleData>> itemsToRemoveAfterFuzzy;

	/**
	 * Traffic sensor controller
	 */
	private TrafficSensorController sensorController;

	/**
	 * Route constructor
	 */
	private RouteConstructor routeConstructor;

	/*
	 * internal fields and data structures
	 */
	/**
	 * ID of the server
	 */
	private int serverId;

	/**
	 * City zone of the server
	 */
	private Geometry cityZone;

	/**
	 * List of all known traffic servers
	 */
	private List<TrafficServer> serverList;

	private List<Integer> serverIds;

	/**
	 * All known other city zones
	 */
	private List<Geometry> cityZones;

	/**
	 * List of received vehicles from other server since the last simulation update
	 */
	private List<ReceivedVehicle> receivedVehicles;

	/**
	 * Current timestamp of the simulation
	 */
	private long currentTime;

	/**
	 * Update interval of the simulation for the traffic servers
	 */
	private long updateIntervall;

	private Map<UUID, TrafficEvent> eventForVehicle;

	@EJB
	private TrafficGovernor fuzzyTrafficGovernor;

	private VehicleAmountManager vehicleFuzzyManager;

	/**
	 * All listed road barriers
	 */
	private Set<RoadBarrier> listedRoadBarriers;

	/**
	 * Default constructor
	 */
	public DefaultTrafficServer() {
	}

	/**
	 * Constructor (for tests)
	 * 
	 * @param mapper
	 *            GPS mapper
	 * @param serviceDictionary
	 *            Service dictionary
	 * @param sensorRegistry
	 *            Sensor registry
	 * @param eventHandlerManager
	 *            Event handler manager
	 * @param slist
	 *            List with all known traffic servers
	 */
	@SuppressWarnings("unchecked")
	public DefaultTrafficServer(ServiceDictionary serviceDictionary, SensorRegistry sensorRegistry,
			SimulationEventHandlerManager eventHandlerManager, List<TrafficServerLocal> slist,
			SensorFactory sensorFactory, Graph graph) {
		DefaultTrafficServer.JUNIT_TEST = true;

		this.mapper = mapper;
		this.serviceDictionary = serviceDictionary;
		this.sensorRegistry = sensorRegistry;
		this.sensorFactory = sensorFactory;

		this.trafficGraphExtensions = new DefaultTrafficGraphExtensions(serviceDictionary.getRandomSeedService());

		this.eventHandlerManager = eventHandlerManager;
		this.vehicleEventHandlerManager = new DefaultVehicleEventHandlerManager();
		// this.vehicleFuzzyManager = new DefaultVehicleFuzzyManager(this, tolerance, checkSteps, timebuffer,
		// trafficGovernor);

		this.instanciateDependencies();

		if (slist != null) {
			this.serverList = (List<TrafficServer>) (List<?>) slist;
			for (TrafficServer s : slist) {
				this.serverIds.add(s.getServerId());
			}
		}
	}

	@Override
	public void createSensor(SensorHelper sensor) throws SensorException {
		this.sensorController.createSensor(sensor);
		if (sensor instanceof SensorHelperTrafficLightIntersection) {
			final SensorHelperTrafficLightIntersection helper = (SensorHelperTrafficLightIntersection) sensor;
			final Node node = this.getGraph().getNode(helper.getNodeId());
			final TrafficLightIntersection trafficLightIntersection = new TrafficLightIntersection(
					helper.getSensorID(), node, this.trafficGraphExtensions);
			this.trafficGraphExtensions.setTrafficRule(node, trafficLightIntersection);
			this.sensorRegistry.addSensor(new TrafficLightSensor(this.sensorFactory.getOutput(), helper
					.getTrafficLightIds().get(0), null, trafficLightIntersection.getTrafficLight0()));
			this.sensorRegistry.addSensor(new TrafficLightSensor(this.sensorFactory.getOutput(), helper
					.getTrafficLightIds().get(1), null, trafficLightIntersection.getTrafficLight1()));
		}
	}

	@Override
	public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public TrafficTrip createTimedTrip(Geometry cityZone, VehicleTypeEnum vehicleType, Date date, int buffer) {
		return this.routeConstructor.createTimedTrip(serverId, cityZone, vehicleType, date, buffer);
	}

	@Override
	public TrafficTrip createTrip(Geometry cityZone, String nodeID, long startTimestamp, boolean isStartNode) {
		return this.routeConstructor.createTrip(serverId, cityZone, nodeID, startTimestamp, isStartNode);
	}

	@Override
	public TrafficTrip createTrip(Geometry cityZone, VehicleTypeEnum vehicleType) {
		return this.routeConstructor.createTrip(serverId, cityZone, vehicleType);
	}

	@Override
	public TrafficTrip createTrip(String startNodeID, String targetNodeID, long startTimestamp) {
		return this.routeConstructor.createTrip(serverId, startNodeID, targetNodeID, startTimestamp);
	}

	@Override
	public void deleteSensor(SensorHelper sensor) throws SensorException {
		this.sensorController.deleteSensor(sensor);
	}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.deleteSensor(sensor);
		}
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
	public Path getBusRoute(List<String> busStopIds) {
		return this.routeConstructor.getBusRoute(busStopIds);
	}

	@Override
	public Map<String, Node> getBusStopNodes(List<String> busStopIds) {
		return this.routeConstructor.getBusStopNodes(busStopIds);
	}

	@Override
	public CarFactory getCarFactory() {
		return this.vehicleFactory;
	}

	@Override
	public Geometry getCityZone() {
		return this.cityZone;
	}

	@Override
	public Graph getGraph() {
		return this.routeConstructor.getGraph();
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
	public ServiceDictionary getServiceDictionary() {
		return this.serviceDictionary;
	}

	@Override
	public Path getShortestPath(Node start, Node dest) {
		return this.routeConstructor.getShortestPath(start, dest);
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
	public void onRemove(Item v) {
		this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(VehicleEventType.VEHICLE_REMOVED, this, v
				.getVehicle(), 0, 0));
		this.sensorController.onRemove(v.getVehicle());
	}

	@Override
	public void onSchedule(Item v) {
		this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(VehicleEventType.VEHICLE_ADDED, this, v
				.getVehicle(), 0, 0));
		this.sensorController.onSchedule(v.getVehicle());
	}

	@PostConstruct
	public void postConstruct() {
		this.serverId = -1;
		this.instanciateDependencies();
	}

	/**
	 * - Die Warteschlangen werden vor ihrer Bearbeitung sortiert - Beim Abarbeiten werden neue Autos erstellt (weil das
	 * Auto selbst nicht übergeben werden konnte) - Beim Bearbeiten wird jedes Auto in eine Hashmap eingetragen (Key:
	 * Position als String) - Nachdem ein Auto geupdatet wurde und ein entsprechender Eintrag in der HashMap fehlt: -
	 * Auto darf sich das Auto an der Kante anmelden - Auto wird der ExpiredItems-List hinzugefügt - Nachdem ein Auto
	 * geupdatet wurde und ein entsprechender Eintrag in der HashMap existiert: - Auto wird nicht an der Kante
	 * angemeldet und auch nicht der ExpiredItems-Liste hinzugefügt - Nach Bearbeitung HashMap wieder leeren
	 */
	@Override
	public void processMovedVehicles() {
		if (this.receivedVehicles.size() > 0) {
			log.info("Processing previously received vehicles (" + +this.receivedVehicles.size()
					+ ") from other servers");
		}
		Collections.sort(this.receivedVehicles);
		String posBeforeUpdate;
		for (Iterator<ReceivedVehicle> i = this.receivedVehicles.iterator(); i.hasNext();) {
			ReceivedVehicle rv = i.next();
			posBeforeUpdate = rv.getVehicle().getPosition().toString();
			this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(VehicleEventType.VEHICLE_UPDATE, this, rv
					.getVehicle(), this.currentTime, this.updateIntervall));

			List<Vehicle<? extends VehicleData>> vehicles = this.trafficGraphExtensions.getVehiclesOnNode(rv
					.getVehicle().getCurrentNode(), rv.getVehicle().getData().getType());
			if (vehicles.size() == 0) {
				if (rv.getVehicle().getPosition().toString().equals(posBeforeUpdate)) {
					vehicles.add(rv.getVehicle());
					log.debug("Vehicle " + rv.getVehicle().getName() + " registered on node "
							+ rv.getVehicle().getCurrentNode().getId());
				}
				try {
					;
					rv.getVehicle().setState(State.NOT_STARTED);
					Item item = new Item(rv.getVehicle(), this.currentTime, this.updateIntervall);
					log.debug(String.format("Scheduled moved vehicle %s to drive on next update: %s", item.getVehicle()
							.getName(), item.toString()));
					// item.setLastUpdate(this.currentTime + this.updateIntervall);
					this.scheduler.scheduleItem(item);
					i.remove();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				log.debug("Could not process moved vehicle " + rv.getVehicle().getName()
						+ ". There is already a vehicle on the same position, amount: " + vehicles.size());
				this.trafficGraphExtensions.unregisterFromEdge(rv.getVehicle().getCurrentEdge(), rv.getVehicle()
						.getCurrentNode(), rv.getVehicle().getNextNode(), rv.getVehicle());
			}
		}
	}

	@Override
	public void setCityZone(Geometry cityZone) {
		this.cityZone = cityZone;
	}

	@Override
	public void setServerId(int serverId) {
		log.debug("Set ServerId to " + serverId);
		this.serverId = serverId;
	}

	@Override
	public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
		return this.sensorController.statusOfSensor(sensor);
	}

	@Override
	public void takeVehicle(Vehicle<? extends VehicleData> vehicle, String startNodeId, String targetNodeId,
			int serverId) {
		log.debug("Received vehicle " + vehicle.getName() + " from another server (" + serverId + "), nodeId: "
				+ startNodeId);
		vehicle.setTrafficGraphExtensions(this.trafficGraphExtensions);
		vehicle.setPath(this.routeConstructor.getShortestPath(this.routeConstructor.getGraph().getNode(startNodeId),
				this.routeConstructor.getGraph().getNode(targetNodeId)));
		vehicle.setState(Vehicle.State.PAUSED);
		this.receivedVehicles.add(new ReceivedVehicle(vehicle, serverId));
	}

	/**
	 * Returns all known traffic servers from the server configuration
	 * 
	 * @param serverConfig
	 *            server configuration
	 * @return List of all traffic servers
	 */
	private List<TrafficServer> getTrafficServer(ServerConfiguration serverConfig) {
		this.serverConfigReader.read(serverConfig, new ServiceHandler<TrafficServer>() {

			@Override
			public String getSearchedName() {
				return ServiceDictionary.TRAFFIC_SERVER;
			}

			@Override
			public void handle(String server, TrafficServer service) {
				if (!server.equals(DefaultTrafficServer.this.configReader.getProperty(Identifier.SERVER_HOST))) {
					log.debug("Found another traffic server on host: " + server);
					DefaultTrafficServer.this.serverList.add(service);
				}
			}

		});
		return this.serverList;
	}

	/**
	 * Instanciate dependencies for the traffic server
	 */
	private void instanciateDependencies() {
		this.trafficGraphExtensions.setRandomSeedService(this.serviceDictionary.getRandomSeedService());
		this.cityZones = new ArrayList<>();
		this.serverIds = new ArrayList<>();
		this.serverList = new ArrayList<>();
		this.receivedVehicles = new ArrayList<>();
		this.listedRoadBarriers = new HashSet<>();
		this.eventForVehicle = new HashMap<UUID, TrafficEvent>();
		this.itemsToScheduleAfterAttractionReached = new ArrayList<>();
		this.itemsToScheduleAfterFuzzy = new ArrayList<>();
		this.itemsToRemoveAfterFuzzy = new ArrayList<>();

		/*
		 * Create all schedulers
		 */

		// Scheduler s = ListScheduler.createInstance().getScheduler();
		// Scheduler s2 = ListScheduler.createInstance().getScheduler();
		Scheduler s = SortedListScheduler.createInstance().getScheduler();
		Scheduler s2 = SortedListScheduler.createInstance().getScheduler();

		this.scheduler = new SchedulerComposite();
		this.scheduler.addScheduler(VehicleTypeEnum.MOTORIZED_VEHICLES, s);
		this.scheduler.addScheduler(EnumSet.of(VehicleTypeEnum.BIKE), s2);
		this.scheduler.changeModus(Modus.WRITE);
		try {
			s.addScheduleHandler(this);
			s2.addScheduleHandler(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all event handlers from the files "eventhandler.conf" and "updatehandler.conf".
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	private void loadEventHandler() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IOException {
		// Load event handlers
		try (InputStream stream = DefaultTrafficServer.class.getResourceAsStream("/eventhandler.conf")) {
			this.eventHandlerManager.init(stream, this.getClass());
			for (SimulationEventHandler handler : this.eventHandlerManager) {
				((TrafficEventHandler) handler).init(this);
			}
		}
		// Load update handlers
		try (InputStream stream = DefaultTrafficServer.class.getResourceAsStream("/updatehandler.conf")) {
			this.vehicleEventHandlerManager.init(stream, this.getClass());
		}
	}

	/**
	 * Load sensor dependencies
	 * 
	 * @param param
	 *            Init parameter
	 * @throws Exception
	 */
	private void loadSensorDependencies(InitParameter param) throws Exception {
		this.sensorController = new DefaultSensorController(this, this.sensorRegistry, this.sensorFactory, this.mapper,
				this.trafficGraphExtensions);
	}
	
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	/**
	 * Updates all vehicles of the traffic server
	 * 
	 * @param currentTime
	 *            Current simulation timestamp
	 * @param simulationEventList
	 *            List with simulation events
	 * @return List with vehicles that have to remove from the server
	 */
	private List<Vehicle<? extends VehicleData>> updateVehicles(long currentTime,
			SimulationEventList simulationEventList) {
		List<Vehicle<? extends VehicleData>> removeableVehicles = new ArrayList<>();
		this.currentTime = currentTime;

		List<Item> expiredItems = this.scheduler.getExpiredItems(currentTime);
		// log.debug("Update command received (time="+currentTime+"), vehicles to be updated: "+expiredItems.size());
		for (Iterator<Item> i = expiredItems.iterator(); i.hasNext();) {
			Item item = i.next();
			Vehicle<? extends VehicleData> vehicle = item.getVehicle();

			if (State.UPDATEABLE_VEHICLES.contains(vehicle.getState())) {
				long elapsedTime = currentTime - item.getLastUpdate();
				item.setLastUpdate(currentTime);
				// log.debug("Elapsed time since last vehicle update: "+elapsedTime);/
				Node varNode = vehicle.getCurrentNode();

				this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(VehicleEventType.VEHICLE_UPDATE, this,
						vehicle, currentTime, elapsedTime));

				// if (!varNode.getId().equals(vehicle.getCurrentNode().getId())) {
				int startNode = vehicle.getIndex(varNode);
				int actualNode = vehicle.getIndex(vehicle.getCurrentNode());
				// log.debug("Vehicle "+vehicle.getName()+" passed Node from index #"+startNode
				// +" to #"+actualNode);
				loop: for (int k = startNode; k < actualNode; k++) {
					// log.debug("Loop: Vehicle "+vehicle.getName()+" passed Node from index #"+k +" to #"+(k+1));
					varNode = vehicle.getPath().getNodePath().get(k);// 0
					Node curNode = vehicle.getPath().getNodePath().get(k + 1);// 1
					if (!varNode.getId().equals(curNode.getId()) && (vehicle.getState() != State.REACHED_TARGET)) {
						log.debug("Vehicle " + vehicle.getName() + " passed node " + curNode.getId());
						if (this.cityZone == null
								|| this.cityZone.covers(GEOMETRY_FACTORY.createPoint(this.trafficGraphExtensions.getPosition(curNode)))) {
							this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(
									VehicleEventType.VEHICLE_PASSED_NODE, this, vehicle, currentTime, 0));
						} else {
							// vehicle is driving out of boundaries
							for (int j = 0; j < this.cityZones.size(); j++) {
								if (this.cityZones.get(j).covers(GEOMETRY_FACTORY.createPoint(this.trafficGraphExtensions.getPosition(curNode)))) {
									log.debug(String.format("Vehicle %s drove out of the bounderies of server %s",
											vehicle.getName(), this.serverId));
									log.debug(String.format("Sending vehicle %s to server %s", vehicle.getName(),
											serverIds.get(j)));
									this.serverList.get(j).takeVehicle(
											vehicle,
											curNode.getId(),
											vehicle.getPath().getNodePath()
													.get(vehicle.getPath().getNodePath().size() - 1).getId(),
											this.serverId);
									removeableVehicles.add(vehicle);
									break loop;
								}
							}
						}
					} else if (!varNode.getId().equals(curNode.getId()) && (vehicle.getState() == State.REACHED_TARGET)) {
						removeableVehicles.add(vehicle);
						this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(
								VehicleEventType.VEHICLE_REACHED_TARGET, this, vehicle, currentTime, 0));
					}
				}

				if (varNode.getId().equals(vehicle.getPath().getNodePath().get(0).getId())
						&& (item.getScheduleTime() == currentTime)) {
					log.debug("Vehicle " + vehicle.getName() + " passed startNode " + varNode.getId());
					this.vehicleEventHandlerManager.handleEvent(new VehicleEvent(VehicleEventType.VEHICLE_PASSED_NODE,
							this, vehicle, currentTime, 0));
				}
				// }
			} else if (vehicle.getState() == State.IN_TRAFFIC_RULE)
				item.setLastUpdate(currentTime);
			else if (vehicle.getState() == State.IN_TRAFFIC_RULE)
				item.setLastUpdate(currentTime);
			// log.debug(String.format("Vehicle %s changed position to %s", vehicle.getName(), vehicle.getPosition()));
		}

		return removeableVehicles;
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		try {
			if (!DefaultTrafficServer.JUNIT_TEST) {
				this.serverList = this.getTrafficServer(param.getServerConfiguration());
			}
			this.loadSensorDependencies(param);
			this.loadEventHandler();
			this.sensorController.init(param);

			this.routeConstructor = new DefaultRouteConstructor(this.mapper, param.getCityInfrastructureData(),
					param.getStartTimestamp(), this.serviceDictionary.getRandomSeedService(),
					this.trafficGraphExtensions);
			this.updateIntervall = param.getInterval();

			this.vehicleFactory = new XMLVehicleFactory(this.serviceDictionary.getRandomSeedService(),
					DefaultTrafficServer.class.getResourceAsStream("/defaultvehicles.xml"), this.trafficGraphExtensions);

			if (fuzzyTrafficGovernor != null) {
				this.vehicleFuzzyManager = new DefaultVehicleAmountManager(this, param.getTrafficFuzzyData()
						.getTolerance(), param.getTrafficFuzzyData().getUpdateSteps(), param.getTrafficFuzzyData()
						.getBuffer(), this.fuzzyTrafficGovernor);
			} else {
				this.vehicleFuzzyManager = new VehicleAmountManagerMock();
			}

			// this.trafficGraphExtensions.setGraph(this.getGraph());
			// this.trafficGraphExtensions.setRouteConstructor(this.routeConstructor);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InitializationException(e.getMessage());
		}
	}

	@Override
	protected void onReset() {
		try {
			this.scheduler.changeModus(Modus.WRITE);
			this.scheduler.clearExpiredItems();
			this.scheduler.clearScheduledItems();
			// log.debug("Scheduler resetted, expired #items: "
			// + this.scheduler.getExpiredItems(this.currentTime).size());
			// log.debug("Scheduler resetted, scheduled #items: "
			// + this.scheduler.getScheduledItems().size());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		this.listedRoadBarriers.clear();
		this.trafficGraphExtensions.reset();
		this.cityZones.clear();
		this.serverList.clear();
		this.eventHandlerManager.clear();
		this.vehicleEventHandlerManager.clear();
		this.sensorController.reset();
	}

	@Override
	protected void onResume() {
		this.sensorController.start(null);
	}

	@Override
	protected void onStart(StartParameter param) {
		for (TrafficServer server : this.serverList) {
			this.cityZones.add(server.getCityZone());
			this.serverIds.add(server.getServerId());
		}
		// not working..
		// for(int i=0; i<this.serverListSize+1; i++) {
		// if(i==this.serverId) {
		// this.cityZones.add(this.cityZone);
		// }
		// else {
		// this.cityZones.add(this.serverList.get(i).getCityZone());
		// }
		// }
		this.sensorController.start(param);
	}

	@Override
	protected void onStop() {
		this.sensorController.stop();
	}

	@Override
	protected void onUpdate(SimulationEventList simulationEventList) {
		/*
		 * Handle incoming events
		 */
		for (SimulationEvent event : simulationEventList.getEventList()) {
			this.eventHandlerManager.handleEvent(event);
		}

		this.scheduler.changeModus(Modus.READ);
		/*
		 * update vehicles and update handlers
		 */
		List<Vehicle<? extends VehicleData>> removeableVehicles = this.updateVehicles(
				simulationEventList.getTimestamp(), simulationEventList);
		this.scheduler.changeModus(Modus.WRITE);

		try {
			this.getScheduler().removeExpiredItems(removeableVehicles);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		for (Item item : itemsToScheduleAfterAttractionReached) {
			try {
				this.getScheduler().scheduleItem(item);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		itemsToScheduleAfterAttractionReached.clear();

		if (this.fuzzyTrafficGovernor != null)
			this.vehicleFuzzyManager.checkVehicleAmount(simulationEventList.getTimestamp());

		try {
			this.scheduler.removeScheduledItems(itemsToRemoveAfterFuzzy);
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		itemsToRemoveAfterFuzzy.clear();

		for (Item item : itemsToScheduleAfterFuzzy) {
			try {
				this.getScheduler().scheduleItem(item);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
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
	public SimulationEventHandlerManager getEventHandlerManager() {
		return eventHandlerManager;
	}

	public int getServerListSize() {
		return serverList.size();
	}

	public void updateRoadBarriers(long timestamp) {
		List<RoadBarrier> removeItems = new ArrayList<>();

		// Check end timestamp of the road barriers
		for (RoadBarrier roadBarrier : this.listedRoadBarriers) {
			if (roadBarrier.getEnd() <= timestamp) {

				// Add edges to graph
				for (Edge edge : roadBarrier.getEdges()) {
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
	 * @param edge
	 *            Edge to add
	 */
	private void addEdgeToGraph(final Edge edge) {
		// Add edge to the graph
		this.getGraph().addEdge(edge.getId(), edge.getNode0(), edge.getNode1());
		Edge graphEdge = this.getGraph().getEdge(edge.getId());

		// Add attributes to the edge
		for (String attributeString : edge.getAttributeKeySet()) {
			graphEdge.setAttribute(attributeString, edge.getAttribute(attributeString));
		}
	}

	@Override
	public void addNewRoadBarrier(RoadBarrier barrier) {
		// Remember the barrier
		this.listedRoadBarriers.add(barrier);
	}

	@Override
	public Set<Edge> getBlockedRoads(long timestamp) {
		Set<Edge> blockNodes = new HashSet<>();

		// Adds the node IDs to the set
		for (RoadBarrier roadBarrier : this.listedRoadBarriers) {
			if (timestamp >= roadBarrier.getStart() && timestamp < roadBarrier.getEnd()) {
				blockNodes.addAll(roadBarrier.getEdges());
			}
		}

		return blockNodes;
	}

	@Override
	public Map<UUID, TrafficEvent> getEventForVehicle() {
		return eventForVehicle;
	}

	@Override
	public List<Item> getItemsToScheduleAfterAttractionReached() {
		return this.itemsToScheduleAfterAttractionReached;
	}

	@Override
	public List<Item> getItemsToScheduleAfterFuzzy() {
		return this.itemsToScheduleAfterFuzzy;
	}

	@Override
	public List<Vehicle<? extends VehicleData>> getItemsToRemoveAfterFuzzy() {
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
	public String getName() {
		return NAME + "(" + this.serverId + ")";
	}

	@Override
	public int getServerId() {
		return this.serverId;
	}

	@Override
	public void setCityZone(Geometry cityZone, int serverId) {
		setCityZone(cityZone);
		setServerId(serverId);
	}
}
