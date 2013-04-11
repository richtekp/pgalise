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

import org.junit.After;
import org.junit.Test;

import de.pgalise.util.generic.MutableInteger;
import de.pgalise.util.generic.async.impl.ThreadPoolHandler;
import de.pgalise.util.generic.function.Function;

/**
 * Tests the async handler
 * 
 * @author Mustafa
 * @version 1.0 (Nov 21, 2012)
 */
public class ThreadPoolHandlerTest {

	/**
	 * Thrown exception
	 */
	private volatile Throwable throwable;

	@Test
	public void waitToFinishTest() {
		AsyncHandler h = new ThreadPoolHandler();

		final MutableInteger count = new MutableInteger();

		h.addDelegateFunction(new Function() {

			@Override
			public void delegate() {
				synchronized (count) {
					count.setValue(count.getValue() + 1);
				}
			}
		});

		h.addDelegateFunction(new Function() {

			@Override
			public void delegate() {
				synchronized (count) {
					count.setValue(count.getValue() + 1);
				}
			}
		});

		h.addDelegateFunction(new Function() {

			@Override
			public void delegate() {
				synchronized (count) {
					count.setValue(count.getValue() + 1);
				}
			}
		});

		h.start();
		h.waitToFinish();
		assertEquals(3, count.getValue());

		h.addDelegateFunction(new Function() {

			@Override
			public void delegate() {
				synchronized (count) {
					count.setValue(count.getValue() + 1);
				}
			}
		});
		h.start();
		h.waitToFinish();
		assertEquals(4, count.getValue());
	}

	@After
	public void tearDown() throws Throwable {
		// gab es einen fehler in einem der seperaten threads?
		if (this.throwable != null) {
			throw this.throwable;
		}
	}
}
