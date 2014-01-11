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

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.BusStop;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficTrip;
import de.pgalise.simulation.traffic.governor.TrafficGovernor;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultTrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.model.vehicle.XMLVehicleFactory;
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
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficSensorController;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.VehicleAmountManager;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.event.DeleteVehiclesEvent;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.internal.server.eventhandler.GenericVehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEventHandlerManager;
import de.pgalise.simulation.traffic.internal.server.eventhandler.vehicle.VehicleEventTypeEnum;
import de.pgalise.simulation.traffic.internal.server.rules.TrafficLightIntersectionSensor;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandlerManager;
import de.pgalise.simulation.traffic.server.route.RouteConstructor;
import de.pgalise.simulation.traffic.internal.server.scheduler.DefaultScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.internal.server.scheduler.SchedulerComposite;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;

/**
 * Default implementation of the traffic server. Possible extension points:<br/>
 * Event handler in server.eventhandler are used to handle incoming simulation
 * evnets.<br/><br/>
 *
 * Event handler in server.eventhandler.vehicle are used to handler vehicle
 * events referring a single vehicle (and its changed status).
 *
 * @author Mustafa
 * @author Lena
 * @version 1.0 (Feb 17, 2013)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.server.TrafficServer")
@Local(TrafficServerLocal.class)
public class DefaultTrafficServer extends AbstractController<
	VehicleEvent, TrafficStartParameter, TrafficInitParameter>
	implements
	TrafficServerLocal<VehicleEvent>,
	ScheduleHandler,
	VehicleEventHandler<VehicleEvent> {

	private static final long serialVersionUID = 1L;

	@Override
	public TrafficServerLocal<VehicleEvent> getResponsibleServer() {
		return this;
	}

	@Override
	public EventType getTargetEventType() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void handleEvent(VehicleEvent event) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void init(TrafficServerLocal<VehicleEvent> server) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * If a vehicle drove out of the boundaries of a server it will be passed to
	 * the server which is responsible for the city zone in which the vehicle
	 * wants to drive. For scheduling the vehicle (on the new server) in the right
	 * order the ReceivedVehicle contains information about the vehicle itself and
	 * the previous server.
	 *
	 * @author Mustafa
	 * @version 1.0 (Feb 17, 2013)
	 */
	private static class ReceivedVehicle implements Comparable<ReceivedVehicle> {

		/**
		 * Vehicle
		 */
		private final Vehicle<?> vehicle;

		private TrafficServerLocal<VehicleEvent> server;

		/**
		 * Constructor
		 *
		 * @param vehicle Vehicle
		 * @param server ID of the server
		 */
		ReceivedVehicle(Vehicle<?> vehicle,
			TrafficServerLocal<VehicleEvent> server) {
			this.vehicle = vehicle;
			this.server = server;
		}

		@Override
		public int compareTo(ReceivedVehicle o) {
			return server.getId().compareTo(o.getServer().getId());
		}

		public TrafficServerLocal<VehicleEvent> getServer() {
			return this.server;
		}

		public Vehicle<?> getVehicle() {
			return this.vehicle;
		}
	}

	/**
	 * Log
	 */
	private static final Logger log = LoggerFactory.getLogger(
		DefaultTrafficServer.class);
	private static final String NAME = "TrafficServer";

	/*
	 * API Dependencies
	 */
	/**
	 * GPS mapper (as EJB)
	 */
	private JaxRSCoordinate coordinate;

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
//	@EJB
//	private TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>, VehicleEvent> trafficEventHandlerManager;

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
	private TrafficSensorFactory sensorFactory;

	/**
	 * Server configurations
	 */
//	@EJB
//	private ServerConfigurationReader<TrafficServerLocal<VehicleEvent>> serverConfigReader;
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
	 * Route constructor
	 */
	@EJB
	private RouteConstructor routeConstructor;

	/**
	 * City zone of the server
	 */
	private Geometry cityZone;

	/**
	 * List of all known traffic servers
	 */
	private Set<TrafficServerLocal<VehicleEvent>> trafficServers;

	/**
	 * All known other city zones
	 */
	private List<Geometry> cityZones;

	/**
	 * List of received vehicles from other server since the last simulation
	 * update
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

	private Map<Long, VehicleEvent> eventForVehicle;

	@EJB
	private TrafficGovernor fuzzyTrafficGovernor;

	private VehicleAmountManager vehicleFuzzyManager;

	/**
	 * All listed road barriers
	 */
	private Set<RoadBarrier> listedRoadBarriers;

	@EJB
	private IdGenerator idGenerator;

	private Set<Vehicle<?>> managedVehicles = new HashSet<Vehicle<?>>();

	/**
	 * Default constructor
	 */
	public DefaultTrafficServer() {
	}

	/**
	 * Constructor (for tests)
	 *
	 * @param coordinate
	 * @param serviceDictionary Service dictionary
	 * @param sensorRegistry Sensor registry
	 * @param eventHandlerManager Event handler manager
	 * @param slist List with all known traffic servers
	 * @param sensorFactory
	 * @param graph
	 */
	public DefaultTrafficServer(JaxRSCoordinate coordinate,
		ServiceDictionary serviceDictionary,
		SensorRegistry sensorRegistry,
		TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>, VehicleEvent> eventHandlerManager,
		List<TrafficServerLocal<VehicleEvent>> slist,
		TrafficSensorFactory sensorFactory,
		TrafficGraph graph) {
		this.coordinate = coordinate;
		this.serviceDictionary = serviceDictionary;
		this.sensorRegistry = sensorRegistry;
		this.sensorFactory = sensorFactory;

		this.trafficGraphExtensions = new DefaultTrafficGraphExtensions(
			serviceDictionary.getRandomSeedService(),
			graph);

//		this.trafficEventHandlerManager = eventHandlerManager;
		this.vehicleEventHandlerManager = new DefaultVehicleEventHandlerManager();
		// this.vehicleFuzzyManager = new DefaultVehicleFuzzyManager(this, tolerance, checkSteps, timebuffer,
		// trafficGovernor);

		this.instanciateDependencies();
	}

	@Override
	public void createSensor(StaticSensor sensor) throws SensorException {
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
			this.sensorRegistry.addSensor(new TrafficLightSensor(this.idGenerator.
				getNextId(),
				this.sensorFactory.
				getSensorOutput(),
				null,
				trafficLightIntersection.getTrafficLight0()));
			this.sensorRegistry.addSensor(new TrafficLightSensor(this.idGenerator.
				getNextId(),
				this.sensorFactory.getSensorOutput(),
				null,
				trafficLightIntersection.getTrafficLight1()));
		}
	}

	@Override
	public void createSensors(Collection<StaticSensor> sensors) throws SensorException {
		for (StaticSensor sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public TrafficTrip createTimedTrip(Geometry cityZone,
		VehicleTypeEnum vehicleType,
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
		VehicleTypeEnum vehicleType) {
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
	public void deleteSensor(StaticSensor sensor) throws SensorException {
		this.sensorController.deleteSensor(sensor);
	}

	@Override
	public void deleteSensors(Collection<StaticSensor> sensors) throws SensorException {
		for (StaticSensor sensor : sensors) {
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
	public List<TrafficEdge> getBusRoute(List<BusStop> busStopIds) {
		return this.routeConstructor.getBusRoute(busStopIds);
	}

	@Override
	public Map<BusStop, TrafficNode> getBusStopNodes(List<BusStop> busStopIds) {
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
	public TrafficGraph getGraph() {
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

	@PostConstruct
	public void postConstruct() {
		this.instanciateDependencies();
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
		if (this.receivedVehicles.size() > 0) {
			log.info(
				"Processing previously received vehicles (" + +this.receivedVehicles.
				size()
				+ ") from other servers");
		}
		Collections.sort(this.receivedVehicles);
		String posBeforeUpdate;
		for (Iterator<ReceivedVehicle> i = this.receivedVehicles.iterator(); i.
			hasNext();) {
			ReceivedVehicle rv = i.next();
			posBeforeUpdate = rv.getVehicle().getPosition().toString();
			this.vehicleEventHandlerManager.handleEvent(
				new GenericVehicleEvent<>(this,
					this.currentTime,
					this.updateIntervall,
					rv.getVehicle(),
					VehicleEventTypeEnum.VEHICLE_UPDATE));

			Set<Vehicle<?>> vehicles = this.trafficGraphExtensions.getVehiclesOnNode(
				rv
				.getVehicle().getCurrentNode(),
				rv.getVehicle().getData().getType());
			if (vehicles.isEmpty()) {
				if (rv.getVehicle().getPosition().toString().equals(posBeforeUpdate)) {
					vehicles.add(rv.getVehicle());
					log.debug(
						"Vehicle " + rv.getVehicle().getName() + " registered on node "
						+ rv.getVehicle().getCurrentNode().getId());
				}
				rv.getVehicle().setVehicleState(VehicleStateEnum.NOT_STARTED);
				DefaultScheduleItem item = new DefaultScheduleItem(rv.getVehicle(),
					this.currentTime,
					this.updateIntervall);
				log.debug(String.format(
					"Scheduled moved vehicle %s to drive on next update: %s",
					item.getVehicle()
					.getName(),
					item.toString()));
				// item.setLastUpdate(this.currentTime + this.updateIntervall);
				this.scheduler.scheduleItem(item);
				i.remove();
			} else {
				log.debug("Could not process moved vehicle " + rv.getVehicle().getName()
					+ ". There is already a vehicle on the same position, amount: " + vehicles.
					size());
				this.trafficGraphExtensions.unregisterFromEdge(rv.getVehicle().
					getCurrentEdge(),
					rv.getVehicle()
					.getCurrentNode(),
					rv.getVehicle().getNextNode(),
					rv.getVehicle());
			}
		}
	}

	@Override
	public void setCityZone(Geometry cityZone) {
		this.cityZone = cityZone;
	}

	@Override
	public boolean statusOfSensor(StaticSensor sensor) throws SensorException {
		return this.sensorController.statusOfSensor(sensor);
	}

	@Override
	public void takeVehicle(Vehicle<?> vehicle,
		TrafficNode startNodeId,
		TrafficNode targetNodeId,
		TrafficServerLocal<VehicleEvent> origin) {
		log.debug(
			"Received vehicle " + vehicle.getName() + " from another server (" + origin + "), nodeId: "
			+ startNodeId);
		vehicle.setTrafficGraphExtensions(this.trafficGraphExtensions);
		vehicle.setPath(this.routeConstructor.getShortestPath(
			startNodeId,
			targetNodeId));
		vehicle.setVehicleState(VehicleStateEnum.PAUSED);
		this.receivedVehicles.add(new ReceivedVehicle(vehicle,
			this));
	}

//	/**
//	 * Returns all known traffic servers from the server configuration
//	 *
//	 * @param serverConfig server configuration
//	 * @return List of all traffic servers
//	 */
//	private List<TrafficServerLocal<VehicleEvent>> getTrafficServer(
//		ServerConfiguration serverConfig) {
//		this.serverConfigReader.read(
//			serverConfig,
//			new ServiceHandler<TrafficServerLocal<VehicleEvent>>() {
//
//				@Override
//				public String getName() {
//					return DefaultTrafficServiceDictionary.TRAFFIC_SERVER;
//				}
//
//				@Override
//				public void handle(String server,
//					TrafficServerLocal<VehicleEvent> service) {
//					if (!server.equals(DefaultTrafficServer.this.configReader.getProperty(
//							ServerConfigurationIdentifier.SERVER_HOST))) {
//						log.debug("Found another traffic server on host: " + server);
//						DefaultTrafficServer.this.trafficServers.add(service);
//					}
//				}
//
//			});
//		return this.trafficServers;
//	}
	/**
	 * Instanciate dependencies for the traffic server
	 */
	private void instanciateDependencies() {
		this.trafficGraphExtensions.setRandomSeedService(this.serviceDictionary.
			getRandomSeedService());
		this.cityZones = new ArrayList<>();
		this.trafficServers = new HashSet<>();
		this.receivedVehicles = new ArrayList<>();
		this.listedRoadBarriers = new HashSet<>();
		this.eventForVehicle = new HashMap<>();
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
		this.scheduler.addScheduler(VehicleTypeEnum.MOTORIZED_VEHICLES,
			s);
		this.scheduler.addScheduler(EnumSet.of(VehicleTypeEnum.BIKE),
			s2);
		this.scheduler.changeModus(ScheduleModus.WRITE);
		s.addScheduleHandler(this);
		s2.addScheduleHandler(this);
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
		try (InputStream stream = DefaultTrafficServer.class.getResourceAsStream(
			"/eventhandler.conf")) {
			this.vehicleEventHandlerManager.init(stream,
				DefaultTrafficServer.class);
			for (TrafficEventHandler<VehicleEvent> a : this.vehicleEventHandlerManager) {
				a.init(this);
			}
		}
		// Load update handlers
		try (InputStream stream = DefaultTrafficServer.class.getResourceAsStream(
			"/updatehandler.conf")) {
			this.vehicleEventHandlerManager.init(stream,
				DefaultTrafficServer.class);
		}
	}

	/**
	 * Load sensor dependencies
	 *
	 * @param param Init parameter
	 * @throws Exception
	 */
	private void loadSensorDependencies(InitParameter param) {
		this.sensorController = new DefaultSensorController(this,
			this.sensorRegistry,
			this.sensorFactory,
			this.coordinate,
			this.trafficGraphExtensions);
	}

	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

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
					if (!varNode.getId().equals(curNode.getId()) && (vehicle.
						getVehicleState() != VehicleStateEnum.REACHED_TARGET)) {
						log.debug(
							"Vehicle " + vehicle.getName() + " passed node " + curNode.getId());
						if (this.cityZone == null
							|| this.cityZone.covers(GEOMETRY_FACTORY.createPoint(
									this.trafficGraphExtensions.getPosition(curNode)))) {
							this.vehicleEventHandlerManager.handleEvent(
								new GenericVehicleEvent<>(
									this,
									currentTime,
									0,
									vehicle,
									VehicleEventTypeEnum.VEHICLE_PASSED_NODE));
						} else {
							// vehicle is driving out of boundaries
							List<TrafficServerLocal<VehicleEvent>> trafficServerList = new ArrayList<>(
								trafficServers);
							for (int j = 0; j < this.cityZones.size(); j++) {
								if (this.cityZones.get(j).covers(GEOMETRY_FACTORY.createPoint(
									this.trafficGraphExtensions.getPosition(curNode)))) {
									log.debug(String.format(
										"Vehicle %s drove out of the bounderies of server %s",
										vehicle.getName(),
										this.getId()));
									log.debug(String.format("Sending vehicle %s to server %s",
										vehicle.getName(),
										trafficServerList.get(j)));
									trafficServerList.get(j).takeVehicle(
										vehicle,
										curNode,
										vehicle.getNodePath()
										.get(vehicle.getNodePath().size() - 1),
										this);
									removeableVehicles.add(vehicle);
									break loop;
								}
							}
						}
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
	protected void onInit(TrafficInitParameter param) throws InitializationException {
		try {
//			this.trafficServers = this.getTrafficServer(param.getServerConfiguration());
			this.loadSensorDependencies(param);
			this.loadEventHandler();
			this.sensorController.init(param);

			this.updateIntervall = param.getInterval();

			this.vehicleFactory = new XMLVehicleFactory(this.serviceDictionary.
				getRandomSeedService(),
				this.serviceDictionary.getIdGenerator(),
				this.trafficGraphExtensions,
				DefaultTrafficServer.class.getResourceAsStream("/defaultvehicles.xml")
			);

			if (fuzzyTrafficGovernor != null) {
				this.vehicleFuzzyManager = new DefaultVehicleAmountManager(this,
					param.getTrafficFuzzyData()
					.getTolerance(),
					param.getTrafficFuzzyData().getUpdateSteps(),
					param.getTrafficFuzzyData()
					.getTimeBuffer(),
					this.fuzzyTrafficGovernor);
			} else {
				this.vehicleFuzzyManager = new VehicleAmountManagerMock();
			}

			// this.trafficGraphExtensions.setGraph(this.getGraph());
			// this.trafficGraphExtensions.setRouteConstructor(this.routeConstructor);
		} catch (Exception e) {
			throw new InitializationException(e);
		}
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
		this.cityZones.clear();
		this.trafficServers.clear();
//		this.trafficEventHandlerManager.clear();
		this.vehicleEventHandlerManager.clear();
		this.sensorController.reset();
	}

	@Override
	protected void onResume() {
		this.sensorController.start(null);
	}

	@Override
	protected void onStart(TrafficStartParameter param) {
		for (TrafficServerLocal<VehicleEvent> server : this.trafficServers) {
			this.cityZones.add(server.getCityZone());
		}
		// not working..
		// for(int i=0; i<this.serverListSize+1; i++) {
		// if(i==this.serverId) {
		// this.cityZones.add(this.cityZone);
		// }
		// else {
		// this.cityZones.add(this.trafficServers.get(i).getCityZone());
		// }
		// }
		this.sensorController.start(param);
	}

	@Override
	protected void onStop() {
		this.sensorController.stop();
	}

	@Override
	public void onUpdate(EventList<VehicleEvent> simulationEventList) {
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

//	@Override
//	public TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>, VehicleEvent> getTrafficEventHandlerManager() {
//		return vehicleEventHandlerManager;
//	}
	@Override
	public int getServerListSize() {
		return trafficServers.size();
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
	public String getName() {
		return NAME + "(" + this.getId() + ")";
	}

	@Override
	public Set<Vehicle<?>> getManagedVehicles() {
		return managedVehicles;
	}

	public void setManagedVehicles(
		Set<Vehicle<?>> managedVehicles) {
		this.managedVehicles = managedVehicles;
	}

	public void setTrafficServers(
		Set<TrafficServerLocal<VehicleEvent>> trafficServers) {
		this.trafficServers = trafficServers;
	}

	public Set<TrafficServerLocal<VehicleEvent>> getTrafficServers() {
		return trafficServers;
	}
}
