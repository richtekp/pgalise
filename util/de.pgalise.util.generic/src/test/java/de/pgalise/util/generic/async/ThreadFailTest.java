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

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the thread concept
 * 
 * @author Mustafa
 * @version 1.0 (Nov 21, 2012)
 */
@Ignore
public class ThreadFailTest {

	@Test
	public void sparateThread() {
		// 1. Problem: müsste fehlschlagen, tut es aber nicht
		// 2. Problem: Thread wird sofort interrupted sobald der Test-Runner-Thread fertig ist
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// durch diese Anweisung läuft der Thread länger
					// als der Test-Runner-Thread
					Thread.sleep(5000);

					// Es gibt keine Ausgabe in der Konsole (s. 1. Problem)
					System.out.println("blubb");

					// Angenommen Thread würde korrekt ausgeführt werden:

					// junit-test sollte hier eigentlich fehlschlagen...
					// asserts, die nicht im test-runner thread ausgeführt werden,
					// haben keine auswirkungen auf den junit-test.
					assertTrue(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}).start();
	}
}
