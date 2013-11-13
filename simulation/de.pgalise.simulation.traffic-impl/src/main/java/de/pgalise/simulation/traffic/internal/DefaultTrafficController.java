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

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.traffic.InfrastructureInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.TrafficServiceDictionary;
import de.pgalise.simulation.traffic.event.AbstractTrafficEvent;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.util.generic.async.AsyncHandler;
import de.pgalise.util.generic.async.impl.ThreadPoolHandler;
import de.pgalise.util.generic.function.Function;
import de.pgalise.util.graph.disassembler.Disassembler;
import de.pgalise.util.graph.internal.QuadrantDisassembler;
import java.util.logging.Level;

/**
 * Implementation of the traffic controller
 * 
 * @author Mustafa
 * @version 1.0 (Feb 11, 2013)
 */
@Lock(LockType.READ)
@Singleton(name = "de.pgalise.simulation.traffic.TrafficController")
@Local(TrafficControllerLocal.class)
public class DefaultTrafficController<
	D extends VehicleData
> extends AbstractController<VehicleEvent, 
	InfrastructureStartParameter, 
	InfrastructureInitParameter
> implements TrafficControllerLocal<VehicleEvent> {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(DefaultTrafficController.class);
	private static final String NAME = "TrafficController";
	private static final long serialVersionUID = 1L;

	/**
	 * Server configuration
	 */
	@EJB
	private ServerConfigurationReader<TrafficServerLocal<VehicleEvent>> serverConfigReader;

	/**
	 * List with all traffic servers
	 */
	private List<TrafficServerLocal<VehicleEvent>> serverList;

	/**
	 * List with all city zones
	 */
	private List<Geometry> cityZones;

	/**
	 * Current disassembler
	 */
	private Disassembler disassembler;

	private AsyncHandler asyncHandler;

	private Geometry area;
	
	public DefaultTrafficController() {
		
	}

	/**
	 * Default constructor
	 * 
	 * @param area 
	 */
	public DefaultTrafficController(Geometry area) {
		disassembler = new QuadrantDisassembler();
		asyncHandler = new ThreadPoolHandler();
		this.area = area;
	}

	/**
	 * Constructor for JUnit tests
	 * 
	 * @param area 
	 * @param trafficServer
	 *            List with traffic servers
	 */
	public DefaultTrafficController(Geometry area, List<TrafficServerLocal<VehicleEvent>> trafficServer) {
		this.serverList = trafficServer;
		asyncHandler = new ThreadPoolHandler();

		disassembler = new QuadrantDisassembler();
		this.area = area;
	}

	@Override
	protected void onInit(final InfrastructureInitParameter param) throws InitializationException {
		serverList = getTrafficServer(param.getServerConfiguration());

		// stadt in gleichgroße teile aufteilen
		cityZones = createCityZones();

		// server graphdaten übergeben
		final List<Exception> exceptions = new ArrayList<>(1);
		for (int i = 0; i < serverList.size(); i++) {
			final int serverId = i;
			asyncHandler.addDelegateFunction(new Function() {

				@Override
				public void delegate() {
					TrafficServerLocal<VehicleEvent> server = serverList.get(serverId);
					try {
						server.init(param);
					} catch (InitializationException ex) {
						throw new RuntimeException(ex);
					} catch (IllegalStateException ex) {
						throw new RuntimeException(ex);
					}
					server.setCityZone(cityZones.get(serverId));
					log.debug("TrafficServer " + serverId + " initalized");
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
		for (TrafficServer<?> server : serverList) {
			server.reset();
		}
		serverList = null;
		cityZones = null;
	}

	@Override
	protected void onStart(InfrastructureStartParameter param) {
		for (TrafficServer<?> server : serverList) {
			server.start(param);
		}
	}

	@Override
	protected void onStop() {
		for (TrafficServer<?> server : serverList) {
			server.stop();
		}
	}

	@Override
	protected void onResume() {
		for (TrafficServer<?> server : serverList) {
			server.start(null);
		}
	}

	@Override
	protected void onUpdate(EventList<VehicleEvent> simulationEventList) {
		if(serverList.size()>1) {
			updateAsynchronous(simulationEventList);
		}
		serverList.get(0).update(simulationEventList);
	}
	
	private void updateAsynchronous(EventList<VehicleEvent> simulationEventList) {
		List<VehicleEvent> eventList = new ArrayList<>(16);
		for (VehicleEvent e : simulationEventList.getEventList()) {
			if (!e.getType().equals(TrafficEventTypeEnum.ROAD_BARRIER_TRAFFIC_EVENT)) {
				log.info(String.format(
						"Received event (%s) will be splitted into multiple events to distribute the load equally",
						e.getId()));
				eventList.addAll(divideEvent(e));
			} else {
				eventList.add(e);
			}
		}

		final EventList<VehicleEvent> newList = new EventList<>(eventList, simulationEventList.getTimestamp(),
				simulationEventList.getId());

		for (final TrafficServer<VehicleEvent> server : serverList) {
			asyncHandler.addDelegateFunction(new Function() {
				@Override
				public void delegate() {
					server.update(newList);
				}
			});
		}
		asyncHandler.start();
		asyncHandler.waitToFinish();

		for (final TrafficServer<?> server : serverList) {
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
	private List<? extends VehicleEvent> divideEvent(VehicleEvent e) {
		if (e.getType().equals(TrafficEventTypeEnum.CREATE_VEHICLES_EVENT)) {
			List<CreateVehiclesEvent<D>> eventList = new ArrayList<>(16);
			CreateVehiclesEvent<?> originalEvent = (CreateVehiclesEvent) e;

			// creat an event for each server
			List<CreateVehiclesEvent<D>> eventForEachServer = new ArrayList<>(this.serverList.size());
			for (int i = 0; i < eventForEachServer.size(); i++) {
				eventForEachServer.add(new CreateVehiclesEvent<D>(serverList.get(i),
					System.currentTimeMillis(), 0,
						new ArrayList<CreateRandomVehicleData>(16)));
			}

			int responsibleServer = 0;

			for (CreateRandomVehicleData data : originalEvent.getVehicles()) {
				NavigationNode startNode = data.getVehicleInformation().getTrip().getStartNode();
				NavigationNode targetNode = data.getVehicleInformation().getTrip().getTargetNode();

				// distribute the load equally for random routes
				if ((startNode == null ) && (targetNode == null)) {
					eventForEachServer.get(responsibleServer).getVehicles().add(data);
					responsibleServer = (responsibleServer + 1) % this.serverList.size();
				}
				// otherwise: let the server decide on their own who is
				// responsible
				else {
					for (CreateVehiclesEvent<D> tmpE : eventForEachServer) {
						tmpE.getVehicles().add(data);
					}
				}
			}
			eventList.addAll(eventForEachServer);
			return eventList;
		} else if (e.getType().equals(TrafficEventTypeEnum.CREATE_RANDOM_VEHICLES_EVENT)) {
			List<CreateRandomVehiclesEvent<D>> eventList = new ArrayList<>(16);
			CreateRandomVehiclesEvent<?> originalEvent = (CreateRandomVehiclesEvent) e;

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

				CreateRandomVehiclesEvent<D> newEvent = new CreateRandomVehiclesEvent<>(
					serverList.get(i), 
					System.currentTimeMillis(), 
					0, 
					new ArrayList<>( originalEvent.getCreateRandomVehicleDataList().subList(a, b)));
				eventList.add(newEvent);
			}
			return eventList;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void createSensor(SensorHelper<?> sensor) throws SensorException {
		for (TrafficServer<?> server : serverList) {
			server.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensor(SensorHelper<?> sensor) throws SensorException {
		for (TrafficServer<?> server : serverList) {
			server.deleteSensor(sensor);
		}
	}
	
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	@Override
	public boolean statusOfSensor(SensorHelper<?> sensor) throws SensorException {
		for (int i = 0; i < cityZones.size(); i++) {
			Coordinate pos = sensor.getPosition();
			if (cityZones.get(i).covers(GEOMETRY_FACTORY.createPoint(pos))) {
				return serverList.get(i).statusOfSensor(sensor);
			}
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
	private List<TrafficServerLocal<VehicleEvent>> getTrafficServer(ServerConfiguration serverConfig) {
		final List<TrafficServerLocal<VehicleEvent>> serverList0 = new ArrayList<>(16);
		serverConfigReader.read(serverConfig, new ServiceHandler<TrafficServerLocal<VehicleEvent>>() {

			@Override
			public String getName() {
				return TrafficServiceDictionary.TRAFFIC_SERVER;
			}

			@Override
			public void handle(String server, TrafficServerLocal<VehicleEvent> service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				serverList0.add(service);
			}

		});
		return serverList0;
	}

	/**
	 * Create city zones with the help of the disassembler
	 * 
	 * @return List with Geometry objects
	 */
	private List<Geometry> createCityZones() {
		return this.disassembler.disassemble(area,
				serverList.size());
	}

	@Override
	public void createSensors(Collection<SensorHelper<?>> sensors) throws SensorException {
		for (SensorHelper<?> sensor : sensors) {
			this.createSensor(sensor);
		}
	}

	@Override
	public void deleteSensors(Collection<SensorHelper<?>> sensors) throws SensorException {
		for (SensorHelper<?> sensor : sensors) {
			this.deleteSensor(sensor);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
