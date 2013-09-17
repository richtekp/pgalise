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
 
package de.pgalise.simulation.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.junit.Test;

import de.pgalise.simulation.service.event.SimulationEventHandler;
import de.pgalise.simulation.service.event.SimulationEventHandlerManager;
import de.pgalise.simulation.service.internal.DefaultSimulationEventHandlerManager;
import de.pgalise.simulation.shared.event.SimulationEvent;
import de.pgalise.simulation.shared.event.SimulationEventTypeEnum;

/**
 * Tests the {@link DefaultSimulationEventHandlerManager}
 * 
 * @author Mustafa
 * @version 1.0 (Feb 15, 2013)
 */
public class EventHandlerManagerTest {
	@Test
	public void initializationTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IOException {
		SimulationEventHandlerManager manager = new DefaultSimulationEventHandlerManager();
		String handlerName = DummyEventHandler.class.getName();
		InputStream in = new ByteArrayInputStream(handlerName.getBytes());
		manager.init(in, EventHandlerManagerTest.class);

		SimulationEventHandler handler = manager.getEventHandler(SimulationEventTypeEnum.CREATE_VEHICLES_EVENT);
		assertNotNull(handler);
		assertTrue(handler instanceof DummyEventHandler);
	}

	@Test
	public void handleEventTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IOException {

		SimulationEvent event = EasyMock.createNiceMock(SimulationEvent.class);
		EasyMock.expect(event.getEventType()).andReturn(SimulationEventTypeEnum.CREATE_VEHICLES_EVENT);
		EasyMock.replay(event);

		SimulationEventHandler handler = EasyMock.createMock(SimulationEventHandler.class);
		EasyMock.expect(handler.getType()).andStubReturn(SimulationEventTypeEnum.CREATE_VEHICLES_EVENT);
		handler.handleEvent(event);
		EasyMock.replay(handler);

		SimulationEventHandlerManager manager = new DefaultSimulationEventHandlerManager();
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
	private static class DummyEventHandler implements SimulationEventHandler {

		@Override
		public SimulationEventTypeEnum getType() {
			return SimulationEventTypeEnum.CREATE_VEHICLES_EVENT;
}

		@Override
		public void handleEvent(SimulationEvent event) {
		}
	}
}
