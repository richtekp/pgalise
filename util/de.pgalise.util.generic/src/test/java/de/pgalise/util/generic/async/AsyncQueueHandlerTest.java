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
 
package de.pgalise.util.generic.async;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import de.pgalise.util.generic.MutableBoolean;
import de.pgalise.util.generic.async.impl.AsyncQueueHandlerImpl;
import de.pgalise.util.generic.function.Function;

/**
 * Tests the async queue handle
 * 
 * @author Mustafa
 * @version 1.0 (Nov 21, 2012)
 */
public class AsyncQueueHandlerTest {
	/**
	 * Thrown exception
	 */
	private volatile Throwable throwable;

	@After
	public void tearDown() throws Throwable {
		// gab es einen fehler in einem der seperaten threads?
		if (this.throwable != null) {
			throw this.throwable;
		}
	}

	@Test
	public void test() throws InterruptedException {
		AsyncQueueHandler handler = new AsyncQueueHandlerImpl();
		assertTrue(!handler.isRunning());
		assertEquals(0, handler.getQueue().size());

		final MutableBoolean b = new MutableBoolean(false);

		handler.getQueue().add(new Function() {
			@Override
			public void delegate() {
				try {
					Thread.sleep(200);
					b.setValue(true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		handler.getQueue().add(new Function() {
			@Override
			public void delegate() {
				try {
					Thread.sleep(200);
					try {
						// kann nur true, wenn die andere Anweisung vorher aufgerufen wurde (s.o)
						assertTrue(b.getValue());
					} catch (Throwable t) {
						AsyncQueueHandlerTest.this.throwable = t;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		// Asynchandler starten
		handler.start();

		// müsste aufgerufen bevor der Asynchandler den boolean wert ändert
		// (seperater thread wird bei der Ausführung des ersten Befehls 2 Sekunden angehalten)
		assertFalse(b.getValue());

		// auf den Asynhandler warten
		((AsyncQueueHandlerImpl) handler).getThread().join();
	}
}
