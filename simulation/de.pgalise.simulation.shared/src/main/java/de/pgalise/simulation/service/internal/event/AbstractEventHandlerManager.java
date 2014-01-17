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
package de.pgalise.simulation.service.internal.event;

import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.service.event.EventHandler;
import de.pgalise.simulation.service.event.EventHandlerManager;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventType;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;

/**
 * A generic base class that can be used to implement an EventHandlerManager.
 * All functions and methods of the interface are already implemented. However
 * the implementer has to implement an abstract method (
 * {@link AbstractEventHandlerManager#responsibleFor(EventHandler, Object)} ) to
 * determine which handler has to be called to process an incoming evnet.
 *
 * @author mustafa
 *
 * @param <H>
 * @param <E>
 */
public abstract class AbstractEventHandlerManager<H extends EventHandler<E>, E extends Event>
  implements
  EventHandlerManager<H, E> {

  private List<H> handlers = new ArrayList<>();

  @EJB
  private RandomSeedService randomSeedService;

  @Override
  public <J extends H> void init(InputStream config,
    ClassLoader clazz) throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, IOException {
    Properties prop = new Properties();
    prop.load(config);
    for (Object key : prop.keySet()) {
      String eventHanlderClazz = (String) key;
      Class<H> handlerClass = null;
      if (clazz != null) {
        handlerClass = (Class<H>) clazz.
          loadClass(eventHanlderClazz);
      } else {
        handlerClass = (Class<H>) Thread.currentThread().getContextClassLoader().
          loadClass(eventHanlderClazz);
      }
      Constructor<H> handlerClassConstructor;
      try {
        handlerClassConstructor = handlerClass.getDeclaredConstructor();
      } catch (NoSuchMethodException ex) {
        throw new IllegalArgumentException(String.format(
          "clazz %s doesn't have a default constructor (can have any modifier, but has to be present)",
          handlerClass.getName()),
          ex);
      } catch (SecurityException ex) {
        throw new RuntimeException(
          "an unexpected SecurtiyException occured (see nested exception for details)",
          ex);
      }
      handlerClassConstructor.setAccessible(true);
      H handler = null;
      try {
        handler = handlerClassConstructor.newInstance();
      } catch (IllegalArgumentException | InvocationTargetException ex) {
        throw new RuntimeException(ex);
      }
      getHandlers().add(handler);
    }
  }

  @Override
  public H getEventHandler(EventType type) {
    for (H handler : getHandlers()) {
      if (handler.getTargetEventType().equals(type)) {
        return handler;
      }
    }
    return null;
  }

  @Override
  public void handleEvent(E event) {
    for (H handler : getHandlers()) {
      if (responsibleFor(handler,
        event)) {
        handler.handleEvent(event);
        break;
      }
    }
  }

  @Override
  public void addHandler(H handler) {
    remoteHandler(handler.getTargetEventType());
    getHandlers().add(handler);
  }

  @Override
  public void remoteHandler(EventType type) {
    for (H handler : getHandlers()) {
      if (handler.getTargetEventType().equals(type)) {
        getHandlers().remove(handler);
        break;
      }
    }
  }

  @Override
  public Iterator<H> iterator() {
    return getHandlers().iterator();
  }

  @Override
  public void clear() {
    this.getHandlers().clear();
  }

  public abstract boolean responsibleFor(H handler,
    E event);

  /**
   * @return the handlers
   */
  public List<H> getHandlers() {
    return handlers;
  }

  /**
   * @param handlers the handlers to set
   */
  public void setHandlers(
    List<H> handlers) {
    this.handlers = handlers;
  }
}
