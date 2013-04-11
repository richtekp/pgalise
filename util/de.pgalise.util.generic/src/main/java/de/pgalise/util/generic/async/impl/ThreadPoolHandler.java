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

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.pgalise.util.generic.async.AsyncHandler;
import de.pgalise.util.generic.function.Function;

/**
 * Implementation of the AsyncHandler using Java built-in ThreadPool-Service.
 * 
 * @author Mustafa
 * @version 1.0 (Apr 5, 2013)
 */
public class ThreadPoolHandler extends AsyncHandler {
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Collection<Future<?>> futures;

	public ThreadPoolHandler() {
		super();
		futures = new LinkedList<Future<?>>();
	}

	public ThreadPoolHandler(Function callback) {
		super(callback);
		futures = new LinkedList<Future<?>>();
	}

	@Override
	protected void onStart(final Function d) {
		futures.add(executor.submit(new Runnable() {

			@Override
			public void run() {
				d.delegate();
				ThreadPoolHandler.this.incRunningWorker(-1);
			}
		}));
	}

	@Override
	public void waitToFinish() {
		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		futures.clear();
	}
}
