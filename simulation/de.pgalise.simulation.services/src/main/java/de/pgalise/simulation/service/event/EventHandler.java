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
 
package de.pgalise.simulation.service.event;


/**
 * An EventHandler is responsible for processing a particular event.
 * An event itself is recognized by its event type.
 * 
 * @see EventHandlerManager
 * @author mustafa
 *
 * @param <E> event to be processed by this handler
 * @param <T> type of event E (usually an enum class)  
 */
public interface EventHandler<E, T> {
	/**
	 * @return type of the events this EventHandler is able to process 
	 */
	public T getType();
	
	/**
	 * Handles the incoming event.
	 * 
	 * @param event event to be handled
	 */
	public void handleEvent(E event);
}
