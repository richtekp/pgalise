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
 
package de.pgalise.util.generic.async.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.util.generic.async.AsyncQueueHandler;
import de.pgalise.util.generic.function.Function;

public class AsyncQueueHandlerExample {
	private static Logger log = LoggerFactory.getLogger(AsyncQueueHandlerExample.class);

	public static void main(String args[]) throws InterruptedException {
		AsyncQueueHandler handler = new AsyncQueueHandlerImpl();

		handler.getQueue().add(new Function() {
			@Override
			public void delegate() {
				try {
					log.debug("...");
					Thread.sleep(2000);
				} catch (InterruptedException e) {

				}
			}
		});

		handler.start();
		if (!handler.isRunning()) {
			log.error("Has to be true");
		}

		handler.getQueue().add(new Function() {

			@Override
			public void delegate() {
				log.debug("...");
			}

		});

		Thread.sleep(5000);
		// dürfte nichts kommen
		handler.start();

		handler.getQueue().add(new Function() {

			@Override
			public void delegate() {
				log.debug("...");
			}

		});
		handler.start();
	}
}
