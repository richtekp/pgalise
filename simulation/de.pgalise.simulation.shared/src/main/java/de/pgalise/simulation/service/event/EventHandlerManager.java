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
package de.pgalise.simulation.service.event;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.event.Event;
import java.io.IOException;
import java.io.InputStream;

/**
 * The EventHandlerManager manages several EventHandler loaded on
 * initialization. Each EventHandler listens for a particular type of event.
 * When the EventHandlerManager is told to process an event it looks for the
 * appropriate EventHandler and informs it.
 *
 * @see EventHandler
 *
 * @param <H> type of the EventHandler that are managed by this
 * EventHandlerManager
 * @param <E> super class of the events to be processed
 * @param <T> type of event E (usually an enum class)
 * @author mustafa
 *
 */
public interface EventHandlerManager<H extends EventHandler<E>, E extends Event>
  extends Iterable<H> {

  /**
   * Registers all listed EventHandlers on this EventHandlerManager. All
   * previously registered handler will be deleted.
   *
   * @param <J>
   * @param config list of EventHandler (class names) to be registered
   * @param clazz if specified the paramater's class loader will be used to load
   * the event handlers
   * @throws ClassNotFoundException if a handler could not be found
   * @throws InstantiationException if a handler could not be instantiated
   * @throws IllegalAccessException if a handler could not be accessed
   * @throws IOException if an error occurred processing the inputstream
   */
  public <J extends H> void init(InputStream config,
    ClassLoader clazz) throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, IOException;

  /**
   * Registers the passed EventHandler on this EventHandlerManager.
   *
   * @param handler EventHandler to be registered on this EventHandlerManager
   */
  public void addHandler(H handler);

  /**
   * Removes a particular handler specified by the event type
   *
   * @param type specifies the handler to be removed
   */
  public void remoteHandler(EventType type);

  /**
   * Delegates the passed event to the appreciate handler.
   *
   * @param event the SimulationEvent to be handled
   */
  public void handleEvent(E event);

  /**
   * @param type the event type the handler is responsible for
   * @return EventHandler for the passed event type
   */
  public H getEventHandler(EventType type);

  /**
   * Clear all event handlers
   */
  public void clear();

  public void init(InitParameter initParameter);
}
