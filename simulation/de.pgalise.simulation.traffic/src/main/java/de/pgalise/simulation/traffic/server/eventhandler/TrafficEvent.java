/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.TrafficControllerLocal;

/**
 *
 * @author richter
 * @param <E>
 */
public interface TrafficEvent<E extends TrafficEvent<?>> extends Event {

  TrafficControllerLocal<E> getResponsibleServer();

  long getSimulationTime();

  long getElapsedTime();
}
