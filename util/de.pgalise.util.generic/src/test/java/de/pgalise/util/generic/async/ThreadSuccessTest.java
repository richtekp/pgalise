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

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the thread concept
 * 
 * @author Mustafa
 * @version 1.0 (Nov 21, 2012)
 */
@Ignore
public class ThreadSuccessTest {
	/**
	 * Thrown exception
	 */
	private volatile Throwable throwable;

	@Test
	public void sparateThread() throws InterruptedException {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);

					try {
						// schlägt ein assert fehlt,
						// gibt es eine exception, die den junit-test
						// fehlschlagen lässt.
						// funktioniert aber nur im main-thread!
						assertTrue(false);
					} catch (Throwable t) {
						// deshalb: merke dir den fehler
						ThreadSuccessTest.this.throwable = t;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});
		thread.start();
		// test-runnter thread zwingen auf den seperaten thread zu warten
		thread.join();
	}

	// wird nach jedem test aufgerufen
	@After
	public void tearDown() throws Throwable {
		// gab es einen fehler in einem der seperaten threads?
		if (this.throwable != null) {
			throw this.throwable;
		}
	}
}
