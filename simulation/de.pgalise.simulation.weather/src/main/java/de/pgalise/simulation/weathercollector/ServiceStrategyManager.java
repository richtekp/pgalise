/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weathercollector;

import de.pgalise.simulation.weathercollector.weatherservice.ServiceStrategy;
import java.io.Serializable;
import java.util.Set;

/**
 * Manages different implementations of {@link ServiceStrategy} which are
 * difficult to manage with EJBs
 *
 * @author richter
 */
public interface ServiceStrategyManager extends Serializable {

  Set<ServiceStrategy> getServiceStrategies();

  ServiceStrategy getPrimaryServiceStrategy();
}
