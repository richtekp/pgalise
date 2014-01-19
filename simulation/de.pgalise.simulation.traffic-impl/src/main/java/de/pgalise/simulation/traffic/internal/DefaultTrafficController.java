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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import de.pgalise.simulation.service.manager.ServerConfigurationReader;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.traffic.event.CreateRandomVehicleData;
import de.pgalise.simulation.traffic.event.CreateRandomVehiclesEvent;
import de.pgalise.simulation.traffic.event.CreateVehiclesEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.staticsensor.StaticSensor;
import de.pgalise.simulation.traffic.Iterables;
import de.pgalise.simulation.traffic.event.TrafficEventTypeEnum;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TrafficSensor;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.util.generic.async.AsyncHandler;
import de.pgalise.util.generic.async.impl.ThreadPoolHandler;
import de.pgalise.util.generic.function.Function;
import de.pgalise.util.graph.disassembler.Disassembler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.ejb.Stateful;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Stateful
@Local(TrafficControllerLocal.class)
public class DefaultTrafficController extends AbstractController<VehicleEvent, TrafficStartParameter, TrafficInitParameter>
  implements TrafficControllerLocal<VehicleEvent> {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultTrafficController.class);
  private static final String NAME = "TrafficController";
  private static final long serialVersionUID = 1L;

//	/**
//	 * Server configuration
//	 */
//	@EJB
//	private ServerConfigurationReader serverConfigReader;
  /**
   * List with all traffic servers
   */
//  private List<TrafficServerLocal<VehicleEvent>> serverList = new LinkedList<>();
  /**
   * List with all city zones (which are always subsets of area)
   */
  private Map<Geometry, TrafficServerLocal<VehicleEvent>> cityZones;

  /**
   * Current disassembler
   */
  @EJB
  private Disassembler disassembler;

  private transient AsyncHandler asyncHandler = new ThreadPoolHandler();

  /**
   * the complete area managed by this controller
   */
  private Geometry area;
  @EJB
  private IdGenerator idGenerator;

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

  /**
   * Constructor for JUnit tests
   *
   * @param area
   * @param trafficServer List with traffic servers
   */
  public DefaultTrafficController(Geometry area,
    Map<Geometry, TrafficServerLocal<VehicleEvent>> trafficServer) {
    this.cityZones = trafficServer;
    asyncHandler = new ThreadPoolHandler();

    this.area = area;
  }

//  @EJB
//  private TrafficServerLocal onlyServer;
  @Override
  protected void onInit(final TrafficInitParameter param) throws InitializationException {
    int trafficServerCount = param.getTrafficServerCount();
    cityZones = new HashMap<>();

    // stadt in gleichgroße teile aufteilen
    area = param.getCity().getPosition().getBoundaries();
    List<Geometry> generatedCityZones = createCityZones(trafficServerCount);
    for (Geometry generatedCityZone : generatedCityZones) {
      try {
        Context localContext = new InitialContext();
        TrafficServerLocal<VehicleEvent> trafficServer = (TrafficServerLocal) localContext.
          lookup(
            "DefaultTrafficServerLocal");
        trafficServer.init(param);
        cityZones.put(generatedCityZone,
          trafficServer);
      } catch (NamingException ex) {
        throw new RuntimeException(ex);
      }
    }
    //@TODO: doesn't make sense that TrafficServers know other instances of 
    //TrafficServers and manage boundary crossing if TrafficController can do this
//    for (TrafficServerLocal trafficServer : serverList) {
//      Set<TrafficServerLocal<VehicleEvent>> trafficServersClone = new HashSet<>(
//        serverList);
//      trafficServersClone.remove(trafficServer);
//      trafficServer.setTrafficServers(trafficServersClone);
//    }

    for (final Geometry cityZone : cityZones.keySet()) {
      asyncHandler.addDelegateFunction(new Function() {

        @Override
        public void delegate() {
          TrafficServerLocal<VehicleEvent> server = cityZones.get(cityZones);
          try {
            server.init(param);
          } catch (IllegalStateException ex) {
            throw new RuntimeException(ex);
          }
          server.setCityZone(cityZone);
          log.debug("TrafficServer " + server + " initalized");
        }

      });
    }

    log.debug("Initializing all TrafficServer...");
    asyncHandler.start();
    asyncHandler.waitToFinish();
    log.debug("All TrafficServer have been initialized");

    log.info(String.format("%s TrafficServer registered",
      cityZones.size()));
  }

  @Override
  protected void onReset() {
    for (TrafficServer<?> server : cityZones.values()) {
      server.reset();
    }
    cityZones.clear();
  }

  @Override
  protected void onStart(TrafficStartParameter param) {
    //serers in serverList are initialized in onInit with TrafficInitParameter
    for (TrafficServer<?> server : cityZones.values()) {
      server.start(param);
    }
  }

  @Override
  protected void onStop() {
    for (TrafficServer<?> server : cityZones.values()) {
      server.stop();
    }
  }

  @Override
  protected void onResume() {
    for (TrafficServer<?> server : cityZones.values()) {
      server.start(null);
    }
  }

  @Override
  protected void onUpdate(EventList<VehicleEvent> simulationEventList) {
    if (cityZones.size() > 1) {
      updateAsynchronous(simulationEventList);
    }
    cityZones.values().iterator().next().update(simulationEventList);
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

    final EventList<VehicleEvent> newList = new EventList(idGenerator.
      getNextId(),
      eventList,
      simulationEventList.getTimestamp());

    for (final TrafficServer<VehicleEvent> server : cityZones.values()) {
      asyncHandler.addDelegateFunction(new Function() {
        @Override
        public void delegate() {
          server.update(newList);
        }
      });
    }
    asyncHandler.start();
    asyncHandler.waitToFinish();

    for (final TrafficServer<?> server : cityZones.values()) {
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
   * Divides one event into multiple events assigning each one a responsible
   * server. The number of divided events is equal the number of traffic servers
   *
   * @param e
   * @return
   */
  private List<? extends VehicleEvent> divideEvent(VehicleEvent e) {
    List<CreateVehiclesEvent<?>> eventList = new LinkedList<>();
    if (e instanceof CreateVehiclesEvent) {
      CreateVehiclesEvent<?> originalEvent = (CreateVehiclesEvent) e;

      // create an event for each server
      Map<TrafficServerLocal, List<CreateVehiclesEvent<?>>> eventForEachServer = new HashMap<>(
        this.cityZones.size());

      //separate random routes from specific routes (equal non-specific 
      //load balancing for random route; let server decide for specific routes
      //for themselfs
      List<CreateRandomVehicleData> randomRoutes = new LinkedList<>();
      Queue<CreateRandomVehicleData> specificRoutes = new LinkedList<>();
      for (CreateRandomVehicleData randomVehicleData : originalEvent.
        getVehicles()) {
        if (randomVehicleData.getVehicleInformation().getTrip().getStartNode() == null
          && randomVehicleData.getVehicleInformation().getTrip().getTargetNode() == null) {
          randomRoutes.add(randomVehicleData);
        } else {
          specificRoutes.add(randomVehicleData);
        }
      }

      int offset = 0;
      for (TrafficServerLocal<VehicleEvent> trafficServer : cityZones.values()) {
        int size = randomRoutes.size() / cityZones.size();
        CreateVehiclesEvent<?> dividedEvent = new CreateVehiclesEvent<>(
          trafficServer,
          e.getSimulationTime(),
          e.getElapsedTime(),
          new LinkedList<>(originalEvent.getVehicles().
            subList(0,
              size)));
        eventList.add(dividedEvent);
        offset += size;
      }
      Iterator<CreateRandomVehicleData> remaining = originalEvent.getVehicles().
        listIterator(offset);
      Iterator<TrafficServerLocal<VehicleEvent>> trafficServers = cityZones.
        values().iterator();
      while (remaining.hasNext()) {
        CreateVehiclesEvent<?> dividedEvent = new CreateVehiclesEvent<>(
          trafficServers.next(),
          e.getSimulationTime(),
          e.getElapsedTime(),
          new LinkedList<>(Arrays.asList(remaining.next())));
        eventList.add(dividedEvent);
      }

      Iterator<TrafficServerLocal<VehicleEvent>> trafficServerCycle = Iterables.
        cycle(
          cityZones.values()).iterator();
      while (!specificRoutes.isEmpty()) {
        CreateRandomVehicleData createRandomVehicleData = specificRoutes.poll();
//        for (TrafficServerLocal trafficServerLocal : cityZones.
//          values()) {
//          if (trafficServerLocal.isResponsible...) {
//            
//          }
//        }
        CreateVehiclesEvent<?> dividedEvent = new CreateVehiclesEvent<>(
          trafficServerCycle.next(),
          e.getSimulationTime(),
          e.getElapsedTime(),
          new LinkedList<>(Arrays.asList(createRandomVehicleData)));
        eventList.add(dividedEvent);
      }
      return eventList;
    } else if (e instanceof CreateRandomVehiclesEvent) {
      CreateRandomVehiclesEvent<?> originalEvent = (CreateRandomVehiclesEvent) e;
      int offset = 0;
      for (TrafficServerLocal<VehicleEvent> trafficServer : cityZones.values()) {
        int size = originalEvent.getCreateRandomVehicleDataList().size() / cityZones.
          size();
        CreateVehiclesEvent<?> dividedEvent = new CreateVehiclesEvent<>(
          trafficServer,
          e.getSimulationTime(),
          e.getElapsedTime(),
          new LinkedList<>(originalEvent.getCreateRandomVehicleDataList().
            subList(0,
              size)));
        eventList.add(dividedEvent);
        offset += size;
      }
      Iterator<CreateRandomVehicleData> remaining = originalEvent.
        getCreateRandomVehicleDataList().
        listIterator(offset);
      Iterator<TrafficServerLocal<VehicleEvent>> trafficServers = cityZones.
        values().iterator();
      while (remaining.hasNext()) {
        CreateVehiclesEvent<?> dividedEvent = new CreateVehiclesEvent<>(
          trafficServers.next(),
          e.getSimulationTime(),
          e.getElapsedTime(),
          new LinkedList<>(Arrays.asList(remaining.next())));
        eventList.add(dividedEvent);
      }
      return eventList;
    }

    throw new IllegalArgumentException();
  }

  @Override
  public void createSensor(TrafficSensor sensor) {
    for (TrafficServer<?> server : cityZones.values()) {
      server.createSensor(sensor);
    }
  }

  @Override
  public void deleteSensor(TrafficSensor sensor) {
    for (TrafficServer<?> server : cityZones.values()) {
      server.deleteSensor(sensor);
    }
  }

  /**
   * {@link InfraredSensor}s are always actived in this implementation
   *
   * @param sensor
   * @return
   * @throws SensorException
   */
  @Override
  public boolean isActivated(TrafficSensor sensor) {
    for (Geometry cityZone : cityZones.keySet()) {
      JaxRSCoordinate pos;
      if (sensor instanceof StaticSensor) {
        pos = ((StaticSensor) sensor).getPosition();
      } else if (sensor instanceof GpsSensor) {
        pos = ((GpsSensor) sensor).getSensorData().getPosition();
      } else if (sensor instanceof InfraredSensor) {
        return true;
      } else {
        throw new IllegalArgumentException();
      }
      if (cityZone.covers(GeoToolsBootstrapping.getGEOMETRY_FACTORY().
        createPoint(pos))) {
        return cityZones.get(cityZone).isActivated(sensor);
      }
    }
    throw new IllegalArgumentException(
      "sensor is not in any city zone of any server");
  }

//	/**
//	 * Returns the traffic servers. Uses the server configurations
//	 *
//	 * @param serverConfig server configurations
//	 * @return List with traffic servers
//	 */
//	private List<TrafficServerLocal<VehicleEvent>> getTrafficServer(
//		ServerConfiguration serverConfig) {
//		final List<TrafficServerLocal<VehicleEvent>> serverList0 = new ArrayList<>(
//			16);
//		serverConfigReader.read(serverConfig,
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
//					log.info(String.format("Using %s on server %s",
//							getName(),
//							server));
//					serverList0.add(service);
//				}
//
//			});
//		return serverList0;
//	}
  /**
   * Create city zones with the help of the disassembler
   *
   * @return List with Geometry objects
   */
  private List<Geometry> createCityZones(int count) {
    return this.disassembler.disassemble(area,
      count);
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

  @Override
  public Set<TrafficSensor> getAllManagedSensors() {
    Set<TrafficSensor> retValue = new HashSet<>();
    for (TrafficServer<?> trafficServer : cityZones.values()) {
      for (TrafficSensor sensor : trafficServer.getAllManagedSensors()) {
        if (!retValue.contains(sensor)) {
          retValue.add(sensor);
        }
      }
    }
    return retValue;
  }

  @Override
  public Map<Geometry, TrafficServerLocal<VehicleEvent>> getTrafficServers() {
    return cityZones;
  }
}
