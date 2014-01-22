/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.server.eventhandler;

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;

/**
 *
 * @author richter
 * @param <E>
 */
public interface TrafficEvent<E extends TrafficEvent<?>> extends Event {

  TrafficServerLocal<E> getResponsibleServer();

  long getSimulationTime();

  long getElapsedTime();
}
