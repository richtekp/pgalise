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

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.util.generic.async.AsyncQueueHandler;
import de.pgalise.util.generic.function.Function;

public class AsyncQueueHandlerImpl implements AsyncQueueHandler {
	private static Logger log = LoggerFactory.getLogger(AsyncQueueHandlerImpl.class);
	private Queue<Function> queue;
	private boolean running;
	private Thread thread;
	private volatile Throwable throwable;

	public AsyncQueueHandlerImpl() {
		this.queue = new LinkedList<>();
	}

	@Override
	public Queue<Function> getQueue() {
		return this.queue;
	}

	@Override
	public Thread getThread() {
		return this.thread;
	}

	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void start() {
		if (!this.running && !this.queue.isEmpty()) {
			this.running = true;
			this.throwable = null;
			this.thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while (!AsyncQueueHandlerImpl.this.queue.isEmpty()) {
							Function f = AsyncQueueHandlerImpl.this.queue.poll();
							f.delegate();
						}
					} catch (Throwable t) {
						t.printStackTrace();
						AsyncQueueHandlerImpl.this.throwable = t;
					} finally {
						log.debug("Thread ended");
						AsyncQueueHandlerImpl.this.running = false;
					}
				}

			});
			log.debug("Thread started");
			this.thread.start();
		} else {
			log.debug("Nothing to do. Start aborted");
		}
	}

	@Override
	public void stop() {
		this.running = false;
		this.thread.interrupt();
		this.thread = null;
		log.debug("Thread interrupted");
	}
}
