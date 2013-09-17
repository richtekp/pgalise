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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * A generic base class that can be used to implement an EventHandlerManager.
 * All functions and methods of the interface are already implemented.
 * However the implementer has to implement an abstract method
 * ( {@link AbstractEventHandlerManager#responsibleFor(EventHandler, Object)} )
 * to determine which handler has to be called to process an incoming evnet.   
 * @author mustafa
 *
 * @param <H>
 * @param <E>
 * @param <T>
 */
public abstract class AbstractEventHandlerManager<H extends EventHandler<E, T>, E, T> implements
		EventHandlerManager<H, E, T> {
	private List<H> handlers = new ArrayList<>();

	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	@Override
	public void init(InputStream config, Class clazz) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException {

		Properties prop = new Properties();
		prop.load(config);
		for (Object key : prop.keySet()) {
			String eventHanlderClazz = (String) key;
			Class handlerClass;
			if (clazz != null) {
				handlerClass = clazz.forName(eventHanlderClazz);
			}
			else {
				handlerClass = Class.forName(eventHanlderClazz);
			}
			H handler = (H) handlerClass.newInstance();
			getHandlers().add(handler);
		}
	}

	@Override
	public H getEventHandler(T type) {
		for (H handler : getHandlers()) {
			if (handler.getType().equals(type)) {
				return handler;
			}
		}
		return null;
	}

	@Override
	public void handleEvent(E event) {
		for (H handler : getHandlers()) {
			if (responsibleFor(handler, event)) {
				handler.handleEvent(event);
				break;
			}
		}
	}

	@Override
	public void addHandler(H handler) {
		remoteHandler(handler.getType());
		getHandlers().add(handler);
	}

	@Override
	public void remoteHandler(T type) {
		for (H handler : getHandlers()) {
			if (handler.getType().equals(type)) {
				getHandlers().remove(handler);
				break;
			}
		}
	}

	@Override
	public Iterator<H> iterator() {
		return getHandlers().iterator();
	}

	@Override
	public void clear() {
		this.getHandlers().clear();
	}

	public abstract boolean responsibleFor(H handler, E event);

	/**
	 * @return the handlers
	 */
	public List<H> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers the handlers to set
	 */
	public void setHandlers(
		List<H> handlers) {
		this.handlers = handlers;
	}
}
