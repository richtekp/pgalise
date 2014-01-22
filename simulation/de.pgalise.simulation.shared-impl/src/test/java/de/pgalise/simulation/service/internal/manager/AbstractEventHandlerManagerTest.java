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
package de.pgalise.simulation.service.internal.manager;

import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.service.event.EventHandler;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.junit.Test;

import de.pgalise.simulation.service.internal.event.AbstractEventHandler;
import de.pgalise.simulation.service.internal.event.AbstractEventHandlerManager;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventTypeEnum;

/**
 * Tests the {@link DefaultSimulationEventHandlerManager}
 *
 * @author Mustafa
 * @version 1.0 (Feb 15, 2013)
 */
public class AbstractEventHandlerManagerTest {

  @Test
  public void initializationTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
    IOException {
    AbstractEventHandlerManager<EventHandler<Event>, Event> manager = new AbstractEventHandlerManagerImpl();
    String handlerName = DummyEventHandler.class.getName();
    InputStream in = new ByteArrayInputStream(handlerName.getBytes());
    manager.init(in,
      DummyEventHandler.class.getClassLoader());

    EventHandler<Event> handler = manager.getEventHandler(
      EventTypeEnum.CHANGE_WEATHER_EVENT);
    assertNotNull(handler);
    assertTrue(handler instanceof DummyEventHandler);
  }

  @Test
  public void handleEventTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
    IOException {

    AbstractEvent event = EasyMock.createNiceMock(AbstractEvent.class);
    EasyMock.expect(event.getType()).andReturn(
      EventTypeEnum.CHANGE_WEATHER_EVENT);
    EasyMock.replay(event);

    AbstractEventHandler<Event> handler = EasyMock.createMock(
      AbstractEventHandler.class);
    EasyMock.expect(handler.getTargetEventType()).andStubReturn(
      EventTypeEnum.CHANGE_WEATHER_EVENT);
    handler.handleEvent(event);
    EasyMock.replay(handler);

    AbstractEventHandlerManager<EventHandler<Event>, Event> manager = new AbstractEventHandlerManagerImpl();
    manager.addHandler(handler);
    manager.handleEvent(event);

    EasyMock.verify(handler);
  }

  /**
   * Mock of the SimulationEventHandler
   *
   * @author Andreas Rehfeldt
   * @version 1.0 (Mar 20, 2013)
   */
  private static class DummyEventHandler extends AbstractEventHandler<Event> {

    DummyEventHandler() {
      super(EventTypeEnum.CHANGE_WEATHER_EVENT);
    }

    @Override
    public EventTypeEnum getTargetEventType() {
      return EventTypeEnum.CHANGE_WEATHER_EVENT;
    }

    @Override
    public void handleEvent(Event event) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(InitParameter initParameter) {

    }
  }

  private class AbstractEventHandlerManagerImpl extends AbstractEventHandlerManager<EventHandler<Event>, Event> {

    @Override
    public boolean responsibleFor(EventHandler<Event> handler,
      Event event) {
      return true;
    }
  }

}
