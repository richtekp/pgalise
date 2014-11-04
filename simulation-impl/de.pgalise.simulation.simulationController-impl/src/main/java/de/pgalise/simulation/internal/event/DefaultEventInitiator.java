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
 *//* 
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
package de.pgalise.simulation.internal.event;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.event.EventInitiator;
import de.pgalise.simulation.internal.DefaultFrontController;
import de.pgalise.simulation.service.Controller;
import de.pgalise.simulation.service.ControllerStatusEnum;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.SimulationComponent;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.traffic.TrafficController;
import de.pgalise.simulation.traffic.TrafficControllerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import de.pgalise.simulation.weather.service.WeatherInitParameter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link EventInitiator}. It starts a thread
 * after start, which runs from {@link InitParameter#getStartTimestamp()} till
 * {@link InitParameter#getEndTimestamp()} and updates the current timestamp
 * after every iteration with {@link InitParameter#getInterval()}. The elapsed
 * real time between every interval steps is at least
 * {@link InitParameter#getClockGeneratorInterval()}. In every iteration the
 * update functions of the {@link SimulationComponent} controllers is called
 * with the {@link SimulationEventList} for the current interval. The update
 * order in every iteration is: {@link WeatherController}, {@link EnergyController}, {@link DefaultFrontController}, {@link ServerSideOperationCenterController},
 * {@link ControlCenterControllerLoader} and {@link TrafficController}.
 *
 * @author Timo
 */
@Lock(LockType.READ)
@Singleton(
  name = "de.pgalise.simulation.simulationController.event.EventInitiator")
@Local
public class DefaultEventInitiator extends AbstractController<Event, StartParameter, WeatherInitParameter>
  implements EventInitiator {

  private static final String NAME = "EventInitiator";
  private static SimpleDateFormat sdf;
  private static final long serialVersionUID = 1L;

  @EJB
  private IdGenerator idGenerator;
  @EJB
  private WeatherControllerLocal weatherController;
  @EJB
  private EnergyControllerLocal energyController;
  @EJB
  private TrafficControllerLocal trafficController;

  @Override
  public EventList<?> getEventList() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Event thread
   *
   * @author Timo
   * @version 1.0
   */
  private class EventThread extends Thread {

    private long lastLogInfo = -1;
    private long lastLogDebug = -1;

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
      while (DefaultEventInitiator.this.getStatus() == ControllerStatusEnum.STARTED) {
        if (DefaultEventInitiator.this.currentTimestamp <= DefaultEventInitiator.this.endTimestamp) {
          long startTimestamp;
          synchronized (DefaultEventInitiator.this.lockThreadLoop) {
            startTimestamp = new Date().getTime();

            // Log
            if (lastLogInfo == -1 || (currentTimestamp - lastLogInfo) >= 300000) {
              log.info("Current simulation time: "
                + sdf.format(new Date(
                    DefaultEventInitiator.this.currentTimestamp)));
              lastLogInfo = currentTimestamp;
            }

            if (lastLogDebug == -1 || (currentTimestamp - lastLogDebug) >= 60000) {
              log.debug("Current simulation time: "
                + sdf.format(new Date(
                    DefaultEventInitiator.this.currentTimestamp)));
              lastLogDebug = currentTimestamp;
            }

            /* WeatherService / Controller */
            List<WeatherEvent> weatherEventList;
            synchronized (weatherEventMap) {
              weatherEventList = DefaultEventInitiator.this.weatherEventMap
                .get(DefaultEventInitiator.this.currentTimestamp);
              DefaultEventInitiator.this.weatherEventMap
                .remove(DefaultEventInitiator.this.currentTimestamp);
            }
            if (weatherEventList == null) {
              weatherEventList = new ArrayList<>();
            }

            DefaultEventInitiator.this.weatherController.update(
              new EventList<>(idGenerator.getNextId(),
                weatherEventList,
                DefaultEventInitiator.this.currentTimestamp));

            /* Update EnergyController: */
            List<EnergyEvent> energyEventList;
            synchronized (DefaultEventInitiator.this.energyEventMap) {
              energyEventList = DefaultEventInitiator.this.energyEventMap
                .get(DefaultEventInitiator.this.currentTimestamp);
              DefaultEventInitiator.this.energyEventMap
                .remove(DefaultEventInitiator.this.currentTimestamp);
            }
            if (energyEventList == null) {
              energyEventList = new ArrayList<>();
            }

            DefaultEventInitiator.this.energyController.update(
              new EventList(idGenerator.getNextId(),
                energyEventList,
                DefaultEventInitiator.this
                .getCurrentTimestamp()));

            /*
             * FrontController...
             */
            for (Controller<?, ?, ?> c : DefaultEventInitiator.this.frontControllerList) {
              c.update(new EventList(idGenerator.getNextId(),
                new ArrayList<>(),
                DefaultEventInitiator.this.currentTimestamp));
            }

            /* Traffic */
            List<Event> trafficEventList;
            synchronized (trafficEventMap) {
              trafficEventList = DefaultEventInitiator.this.trafficEventMap
                .get(DefaultEventInitiator.this.currentTimestamp);
              DefaultEventInitiator.this.trafficEventMap
                .remove(DefaultEventInitiator.this.currentTimestamp);
            }
            if (trafficEventList == null) {
              trafficEventList = new ArrayList<>();
            }

            DefaultEventInitiator.this.trafficController.update(
              new EventList<>(idGenerator.getNextId(),
                new ArrayList<>(trafficEventList),
                DefaultEventInitiator.this.currentTimestamp));

            /* update time */
            synchronized (DefaultEventInitiator.this.lockTimestamp) {
              DefaultEventInitiator.this.currentTimestamp += DefaultEventInitiator.this.interval;
            }
          }
          /* Perform clock generator interval: */
          long remainTime = DefaultEventInitiator.this.clockGeneratorInterval - (new Date().
            getTime() - startTimestamp);
          if (remainTime >= 0 && remainTime < DefaultEventInitiator.this.clockGeneratorInterval) {
            try {
              Thread.sleep(remainTime);
            } catch (InterruptedException e) {
              log.warn(e.getLocalizedMessage());
            }
          }
        } else {
          DefaultEventInitiator.this.onStop();
          return;
        }
      }
    }
  }

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(
    DefaultEventInitiator.class);
  /**
   * Map with timestamp and future energy events
   */
  private final Map<Long, List<EnergyEvent>> energyEventMap = new HashMap<>();
  /**
   * Event thread
   */
  private Thread eventThread;
  /**
   * Lock object
   */
  private final Object lockThreadLoop = new Object(), lockTimestamp = new Object();
  ;
	private long currentTimestamp, endTimestamp, interval, clockGeneratorInterval;
  /**
   * Map with timestamp and traffic events
   */
  private final Map<Long, List<Event>> trafficEventMap = new HashMap<>();
  /**
   * Map with timestamp and weather events
   */
  private final Map<Long, List<WeatherEvent>> weatherEventMap = new HashMap<>();

  private List<Controller<?, ?, ?>> frontControllerList;

  /**
   * Default constructor
   */
  public DefaultEventInitiator() {
    sdf = new SimpleDateFormat();
    sdf.applyPattern("dd.MM.yy/HH:mm:ss");
  }

  public DefaultEventInitiator(IdGenerator idGenerator,
    List<Controller<?, ?, ?>> frontControllerList) {
    this.idGenerator = idGenerator;
    this.frontControllerList = frontControllerList;
  }

  public DefaultEventInitiator(IdGenerator idGenerator,
    long currentTimestamp,
    long endTimestamp,
    long interval,
    long clockGeneratorInterval,
    List<Controller<?, ?, ?>> frontControllerList) {
    this(idGenerator,
      frontControllerList);
    this.currentTimestamp = currentTimestamp;
    this.endTimestamp = endTimestamp;
    this.interval = interval;
    this.clockGeneratorInterval = clockGeneratorInterval;
  }

  @Override
  public Thread getEventThread() {
    return this.eventThread;
  }

  /**
   * Adds a new energy events to the energy event map.
   *
   * @param energyEvent Energy event
   * @param timestamp Timestamp
   */
  private void addEnergyEvent(EnergyEvent energyEvent,
    long timestamp) {
    synchronized (this.energyEventMap) {
      long bestTimestamp = this.findBestTimestamp(timestamp);
      List<EnergyEvent> energyEventList = this.energyEventMap.get(bestTimestamp);

      if (energyEventList == null) {
        energyEventList = new ArrayList<>();
        this.energyEventMap.put(bestTimestamp,
          energyEventList);
      }

      energyEventList.add(energyEvent);
    }
  }

  /**
   * Adds a new traffic event to the traffic event map.
   *
   * @param trafficEvent Traffic event
   * @param timestamp timestamp
   */
  private void addTrafficEvent(Event trafficEvent,
    long timestamp) {
    synchronized (this.trafficEventMap) {
      long bestTimestamp = this.findBestTimestamp(timestamp);
      List<Event> trafficEventList = this.trafficEventMap.get(bestTimestamp);

      if (trafficEventList == null) {
        trafficEventList = new ArrayList<>();
        this.trafficEventMap.put(bestTimestamp,
          trafficEventList);
      }

      trafficEventList.add(trafficEvent);
    }
  }

  /**
   * Adds a new weather event to the weather event map.
   *
   * @param weatherEvent Weather event
   * @param timestamp Timestamp
   */
  private void addWeatherEvent(WeatherEvent weatherEvent,
    long timestamp) {
    synchronized (this.weatherEventMap) {
      long bestTimestamp = this.findBestTimestamp(timestamp);
      List<WeatherEvent> weatherEventList = this.weatherEventMap.get(
        bestTimestamp);

      if (weatherEventList == null) {
        weatherEventList = new ArrayList<>();
        this.weatherEventMap.put(bestTimestamp,
          weatherEventList);
      }

      weatherEventList.add(weatherEvent);
    }
  }

  /**
   * Gives the best matching timestamp in the interval.
   *
   * @param timestamp Timestamp
   * @param currentTimestamp Actual timestamp
   * @param interval Interval
   * @return best matching timestamp
   */
  private long findBestTimestamp(long timestamp) {
    synchronized (this.lockTimestamp) {
      long bestTimestamp = this.currentTimestamp;
      for (; timestamp > bestTimestamp; bestTimestamp += this.interval) {

      }

      if (bestTimestamp == this.currentTimestamp) {
        return this.currentTimestamp + this.interval;
      }

      return bestTimestamp;
    }
  }

  /**
   * Resets the event thread and starts it
   */
  private void startEventThread() {
    this.eventThread = new EventThread();
    this.eventThread.start();
  }

  /**
   * Stops the event thread and the controller
   */
  private void stopEventThread() {
    if (this.eventThread != null && this.eventThread.isAlive()) {
      this.setStatus(ControllerStatusEnum.STOPPED);
    }
  }

  @Override
  public void addSimulationEventList(EventList<?> simulationEventList) {
    if ((simulationEventList.getEventList() != null) && !simulationEventList.
      getEventList().isEmpty()) {
      for (Event event : simulationEventList.getEventList()) {

        /*
         * Add new events here!
         */
        if (event instanceof TrafficEvent) {
          this.addTrafficEvent((TrafficEvent) event,
            simulationEventList.getTimestamp());
        } else if (event instanceof WeatherEvent) {
          this.addWeatherEvent((WeatherEvent) event,
            simulationEventList.getTimestamp());
        } else if (event instanceof EnergyEvent) {
          this.addEnergyEvent((EnergyEvent) event,
            simulationEventList.getTimestamp());
        }
      }
    }
  }

  @Override
  protected void onStart(StartParameter param) {
    if (energyController.getStatus() == ControllerStatusEnum.INITIALIZED) {
      this.energyController.start(param);
    }
    if (trafficController.getStatus() == ControllerStatusEnum.INITIALIZED) {
      this.trafficController.start(param);
    }
    if (weatherController.getStatus() == ControllerStatusEnum.INITIALIZED) {
      this.weatherController.start(param);
    }
    this.startEventThread();
  }

  @Override
  protected void onStop() {
    this.stopEventThread();
  }

  @Override
  protected void onUpdate(EventList<Event> simulationEventList) {
    this.addSimulationEventList(simulationEventList);
  }

  @Override
  protected void onInit(WeatherInitParameter param) throws InitializationException {
    this.endTimestamp = param.getEndTimestamp().getTime();
    this.interval = param.getInterval();
    this.clockGeneratorInterval = param.getClockGeneratorInterval();
    this.currentTimestamp = param.getStartTimestamp().getTime();

    if (energyController.getStatus() == ControllerStatusEnum.INIT) {
      this.energyController.init(param);
    }
    if (trafficController.getStatus() == ControllerStatusEnum.INIT) {
      this.trafficController.init(param);
    }
    if (weatherController.getStatus() == ControllerStatusEnum.INIT) {
      this.weatherController.init(param);
    }
  }

  @Override
  protected void onReset() {
  }

  @Override
  public long getCurrentTimestamp() {
    return this.currentTimestamp;
  }

  @Override
  protected void onResume() {
    // Nothing to do

  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void setFrontController(List<Controller<?, ?, ?>> controller) {
    this.frontControllerList = controller;
  }
}
