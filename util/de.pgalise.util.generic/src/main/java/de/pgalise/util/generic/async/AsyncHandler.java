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

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.util.generic.function.Function;

/**
 * AsynHandler runs commands asynchronously and calls the callback function when all proceedings have been finished.
 * 
 * @author mustafa
 */
public abstract class AsyncHandler {
	private static final Logger log = LoggerFactory.getLogger(AsyncHandler.class);
	private Function callback;
	private int countTasks;
	private List<Function> tasks;

	public AsyncHandler() {
		this.tasks = new LinkedList<>();
	}

	public AsyncHandler(Function callback) {
		this.callback = callback;
		this.tasks = new LinkedList<>();
	}

	/**
	 * Sets the DelegateFunction. A DelegateFunction represents a specific task. For each task the AsyncHandler starts a
	 * asynchronous routine for the processing.
	 * Delegate function will be removed as soon as it has finished its operation.
	 * @param f
	 *            task to be done by the asynchronous routine
	 */
	public void addDelegateFunction(Function f) {
		this.incRunningWorker(1);
		this.tasks.add(f);
	}

	/**
	 * Sets the callback function. It is called when all asynchronous routines finished their job.
	 * 
	 * @param callback
	 *            callback function
	 */
	public void setCallback(Function callback) {
		this.callback = callback;
	}
	
	public Function getCallBack() {
		return this.callback;
	}

	public void setDelegateFunctions(List<Function> delegates) {
		this.tasks = delegates;
	}

	/**
	 * Starts a asynchronous routine for each specified task.
	 */
	public void start() {
		if ((this.tasks != null) && (this.tasks.size() > 0)) {
			for (Function d : this.tasks) {
				this.onStart(d);
			}
		} else {
			this.incRunningWorker(0);
		}
	}

	/**
	 * Has to be called when a asynchronous routine has finished his task.
	 * 
	 * @param n
	 *            n=-1 when finished the job
	 */
	protected synchronized void incRunningWorker(int n) {
		this.countTasks += n;
		if (n > 0) {
			log.debug("Task added. Total num: " + this.countTasks);
		} else {
			log.debug("Task removed. Total num: " + this.countTasks);
		}
		if (this.countTasks == 0) {
			if(this.callback!=null) {
				this.callback.delegate();
			}
			this.callback=null;
			this.tasks.clear();
		}
	}

	/**
	 * Creates an asynchronous routine and defines what it looks like in particular. A ThreadHandler for example could
	 * implement here a asynchronous routine as a separate thread. Do not forget to call incRunningWorker(-1) when the
	 * routine finished his task.
	 * 
	 * @param d
	 *            task to be done by the asynchronous routine
	 */
	protected abstract void onStart(final Function d);
	
	public abstract void waitToFinish();
}
