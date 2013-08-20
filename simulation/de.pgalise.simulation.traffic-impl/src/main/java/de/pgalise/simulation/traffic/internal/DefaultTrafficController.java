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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.ServerConfiguration;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehicleData;
import de.pgalise.simulation.shared.event.traffic.CreateRandomVehiclesEvent;
import de.pgalise.simulation.shared.event.traffic.CreateVehiclesEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.shared.geometry.Geometry;
import de.pgalise.simulation.shared.geometry.Rectangle;
import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.util.generic.async.AsyncHandler;
import de.pgalise.util.generic.async.impl.ThreadPoolHandler;
import de.pgalise.util.generic.function.Function;
import de.pgalise.util.graph.disassembler.Disassembler;
import de.pgalise.util.graph.internal.QuadrantDisassembler;
import javax.vecmath.Vector2d;

/**
 * Implementation of the traffic controller
 * 
 * @author Mustafa
 * @version 1.0 (Feb 11, 2013)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.TrafficController")
@Local(TrafficControllerLocal.class)
@Remote(TrafficController.class)
public class DefaultTrafficController extends AbstractController implements TrafficControllerLocal {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultTrafficController.class);
	private static final String NAME = "TrafficController";

	/**
	 * GPS mapper
	 */
	@EJB
	private GPSMapper mapper;

	/**
	 * Server configuration
	 */
	@EJB
	private ServerConfigurationReader serverConfigReader;

	/**
	 * List with all traffic servers
	 */
	private List<TrafficServer> serverList;

	/**
	 * List with all city zones
	 */
	private List<Geometry> cityZones;

	/**
	 * Current disassembler
	 */
	private Disassembler disassembler;

	private AsyncHandler asyncHandler;

	/**
	 * help variable for JUnit tests
	 */
	private final boolean JUNIT_TEST;

	/**
	 * Default constructor
	 */
	public DefaultTrafficController() {
		JUNIT_TEST = false;
		disassembler = new QuadrantDisassembler();
		asyncHandler = new ThreadPoolHandler();
	}

	/**
	 * Constructor for JUnit tests
	 * 
	 * @param mapper
	 *            GPS mapper
	 * @param trafficServer
	 *            List with traffic servers
	 */
	public DefaultTrafficController(GPSMapper mapper, List<TrafficServer> trafficServer) {
		JUNIT_TEST = true;
		this.mapper = mapper;
		this.serverList = trafficServer;
		asyncHandler = new ThreadPoolHandler();

		disassembler = new QuadrantDisassembler();
	}

	@Override
	protected void onInit(final InitParameter param) throws InitializationException {
		if (!JUNIT_TEST) {
			serverList = getTrafficServer(param.getServerConfiguration());
		}

		// stadt in gleichgroße teile aufteilen
		cityZones = createCityZones();

		// server graphdaten übergeben
		final List<Exception> exceptions = new ArrayList<>();
		for (int i = 0; i < serverList.size(); i++) {
			final int serverId = i;
			asyncHandler.addDelegateFunction(new Function() {

				@Override
				public void delegate() {
					try {
						TrafficServer server = serverList.get(serverId);
						server.setServerId(serverId);
						server.init(param);
						server.setCityZone(cityZones.get(serverId));
						log.debug("TrafficServer " + serverId + " initalized");
					} catch (IllegalStateException | InitializationException e) {
						log.error("Failed to initialize TrafficServer " + serverId, e);
						exceptions.add(e);
					}
				}

			});
		}

		log.debug("Initializing all TrafficServer...");
		asyncHandler.start();
		asyncHandler.waitToFinish();
		log.debug("All TrafficServer have been initialized");

		if (exceptions.size() > 0) {
			log.error("Could not initialize TrafficController");
			throw new InitializationException("Could not initialize TrafficController");
		}

		log.info(String.format("%s TrafficServer registered", serverList.size()));
	}

	@Override
	protected void onReset() {
		for (TrafficServer server : serverList) {
			server.reset();
		}
		serverList = null;
		cityZones = null;
	}

	@Override
	protected void onStart(StartParameter param) {
		for (TrafficServer server : serverList) {
			server.start(param);
		}
	}

	@Override
	protected void onStop() {
		for (TrafficServer server : serverList) {
			server.stop();
		}
	}

	@Override
	protected void onResume() {
		for (TrafficServer server : serverList) {
			server.start(null);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onUpdate(SimulationEventList simulationEventList) {
		if(serverList.size()>1)
			updateAsynchronous(simulationEventList);
		
		serverList.get(0).update(simulationEventList);
	}
	
	private void updateAsynchronous(SimulationEventList simulationEventList) {
		List<SimulationEvent> eventList = (List<SimulationEvent>) (List<?>) simulationEventList.getEventList();
		eventList = new ArrayList<>();
		for (SimulationEvent e : simulationEventList.getEventList()) {
			if (!e.getEventType().equals(SimulationEventTypeEnum.ROAD_BARRIER_TRAFFIC_EVENT)) {
				log.info(String.format(
						"Received event (%s) will be splitted into multiple events to distribute the load equally",
						e.getId()));
				eventList.addAll(divideEvent(e));
			} else {
				eventList.add(e);
			}
		}

		final SimulationEventList newList = new SimulationEventList(eventList, simulationEventList.getTimestamp(),
				simulationEventList.getId());

		for (final TrafficServer server : serverList) {
			asyncHandler.addDelegateFunction(new Function() {
				@Override
				public void delegate() {
					server.update(newList);
				}
			});
		}
		asyncHandler.start();
		asyncHandler.waitToFinish();

		for (final TrafficServer server : serverList) {
			asyncHandler.addDelegateFunction(new Function() {
				@Override
				public void delegate() {
					server.processMovedVehicles();
				}
			});
		}
		asyncHandler.start();
		asyncHandler.waitToFinish();
	}

	/**
	 * Divides one event into multiple events assigning each one a responsible server.
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<SimulationEvent> divideEvent(SimulationEvent e) {
		List<SimulationEvent> eventList = new ArrayList<>();
		if (e.getEventType().equals(SimulationEventTypeEnum.CREATE_VEHICLES_EVENT)) {
			CreateVehiclesEvent originalEvent = (CreateVehiclesEvent) e;

			// creat an event for each server
			CreateVehiclesEvent eventForEachServer[] = new CreateVehiclesEvent[this.serverList.size()];
			for (int i = 0; i < eventForEachServer.length; i++) {
				eventForEachServer[i] = new CreateVehiclesEvent(UUID.randomUUID(),
						new ArrayList<CreateRandomVehicleData>());
				eventForEachServer[i].setResponsibleServer(i);
			}

			int responsibleServer = 0;

			for (CreateRandomVehicleData data : originalEvent.getVehicles()) {
				String startNode = data.getVehicleInformation().getTrip().getStartNode();
				String targetNode = data.getVehicleInformation().getTrip().getTargetNode();

				// distribute the load equally for random routes
				if ((startNode == null || startNode.equals("")) && (targetNode == null || targetNode.equals(""))) {
					eventForEachServer[responsibleServer].getVehicles().add(data);
					responsibleServer = (responsibleServer + 1) % this.serverList.size();
				}
				// otherwise: let the server decide on their own who is
				// responsible
				else {
					for (CreateVehiclesEvent tmpE : eventForEachServer) {
						tmpE.getVehicles().add(data);
					}
				}
			}
			eventList.addAll(Arrays.asList(eventForEachServer));
		} else if (e.getEventType().equals(SimulationEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT)) {
			CreateRandomVehiclesEvent originalEvent = (CreateRandomVehiclesEvent) e;

			int sizeForEachServer = originalEvent.getCreateRandomVehicleDataList().size() / serverList.size();
			int sizeForLastServer = originalEvent.getCreateRandomVehicleDataList().size()
					- (sizeForEachServer * (serverList.size() - 1));

			for (int i = 0; i < serverList.size(); i++) {
				int a = i * sizeForEachServer;
				int b = ((i + 1) * sizeForEachServer) + 1; // sublist
															// [beginning,
															// ending)
				if (i == serverList.size() - 1) {
					b = sizeForLastServer - 1;
					if (a == b) {
						continue;
					}
				}

				log.info(String.format("Creating new event from original [%s - %s)", a, b));

				CreateRandomVehiclesEvent newEvent = new CreateRandomVehiclesEvent(originalEvent.getId(),
						new ArrayList(originalEvent.getCreateRandomVehicleDataList().subList(a, b)));
				newEvent.setResponsibleServer(i);
				eventList.add(newEvent);
			}
		}
		return eventList;
	}

	@Override
	public void createSensor(SensorHelper sensor) throws SensorException {
		for (TrafficServer server : serverList) {
			server.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensor(SensorHelper sensor) throws SensorException {
		for (TrafficServer server : serverList) {
			server.deleteSensor(sensor);
		}
	}

	@Override
	public boolean statusOfSensor(SensorHelper sensor) throws SensorException {
		for (int i = 0; i < cityZones.size(); i++) {
			Vector2d pos = mapper.convertToVector(sensor.getPosition());
			if (cityZones.get(i).covers(pos))
				return serverList.get(i).statusOfSensor(sensor);
		}
		throw new SensorException("Could not find sensor.");
	}

	/**
	 * Returns the traffic servers. Uses the server configurations
	 * 
	 * @param serverConfig
	 *            server configurations
	 * @return List with traffic servers
	 */
	private List<TrafficServer> getTrafficServer(ServerConfiguration serverConfig) {
		final List<TrafficServer> serverList = new ArrayList<>();
		serverConfigReader.read(serverConfig, new ServiceHandler<TrafficServer>() {

			@Override
			public String getSearchedName() {
				return ServiceDictionary.TRAFFIC_SERVER;
			}

			@Override
			public void handle(String server, TrafficServer service) {
				log.info(String.format("Using %s on server %s", getSearchedName(), server));
				serverList.add(service);
			}

		});
		return serverList;
	}

	/**
	 * Create city zones with the help of the disassembler
	 * 
	 * @return List with Geometry objects
	 */
	private List<Geometry> createCityZones() {
		return this.disassembler.disassemble(new Rectangle(0, 0, this.mapper.getWidth(), this.mapper.getHeight()),
				serverList.size());
	}

	@Override
	public void createSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensors(Collection<SensorHelper> sensors) throws SensorException {
		for (SensorHelper sensor : sensors) {
			this.deleteSensor(sensor);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
