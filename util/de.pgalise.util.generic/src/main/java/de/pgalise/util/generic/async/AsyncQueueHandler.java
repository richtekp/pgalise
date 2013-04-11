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

import java.util.Queue;

import de.pgalise.util.generic.function.Function;

/**
 * Queue that holds commands to be executed by a separate thread.
 * 
 * @author mustafa
 */
public interface AsyncQueueHandler {
	/**
	 * Returns a queue of Function-Objects (commands). Function-Objects added to the queue will be called automatically
	 * if the processing thread is still running.
	 * 
	 * @return Queue of Function-Objects (commands)
	 */
	public Queue<Function> getQueue();

	/**
	 * @return Processing thread
	 */
	public Thread getThread();

	public Throwable getThrowable();

	/**
	 * @return true if commands processing thread is running otherwise false
	 */
	public boolean isRunning();

	/**
	 * Starts the commands processing thread which proceeds automatically the commands on the queue. If the thread is
	 * already running the method call will be ignored. When the queue runs empty the thread terminates and has to be
	 * started again.
	 */
	public void start();

	/**
	 * Interrupts the commands processing thread immediately
	 */
	public void stop();
}
