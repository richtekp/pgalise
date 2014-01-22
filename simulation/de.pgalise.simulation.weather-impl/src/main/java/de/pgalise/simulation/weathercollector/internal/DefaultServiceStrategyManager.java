/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weathercollector.internal;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.weathercollector.ServiceStrategyManager;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import de.pgalise.simulation.weathercollector.weatherservice.ServiceStrategy;
import de.pgalise.simulation.weathercollector.weatherservice.strategy.YahooWeather;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Stateful
public class DefaultServiceStrategyManager implements ServiceStrategyManager {

  private final Set<ServiceStrategy> SERVICE_STRATEGIES = new HashSet<>();
  private ServiceStrategy primaryService;
  @EJB
  private IdGenerator idGenerator;
  @EJB
  private DatabaseManager databaseManager;

  public DefaultServiceStrategyManager() {
  }

  @Override
  public Set<ServiceStrategy> getServiceStrategies() {
    return Collections.unmodifiableSet(SERVICE_STRATEGIES);
  }

  @Override
  public ServiceStrategy getPrimaryServiceStrategy() {
    return primaryService;
  }

  @PostConstruct
  public void initialize() {
    primaryService = new YahooWeather(idGenerator,
      databaseManager);
    SERVICE_STRATEGIES.add(primaryService);
  }

}
