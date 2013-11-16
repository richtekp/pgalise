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
 
package de.pgalise.simulation.service.internal;

import java.lang.reflect.Type;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default gson service with type adapters for {@link SensorHelper} and {@link SimulationEvent}.
 * @author Timo
 */
@Lock(LockType.READ)
@Local
@Singleton(name="de.pgalise.simulation.service.GsonService", mappedName = "de.pgalise.simulation.service.GsonService")
public class DefaultGsonService extends AbstractController implements GsonService {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultGsonService.class);
	private static final long serialVersionUID = 1L;

	@Override
	public Gson getGson() {
		return new GsonBuilder().
				registerTypeAdapter(SensorHelper.class, new SensorHelperTypeDeserializer()).
				registerTypeAdapter(AbstractEvent.class, new SimulationEventAdapter()).
				create();
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onReset() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onStart(StartParameter param) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onStop() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onResume() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onUpdate(EventList simulationEventList) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	/**
	 * Handles the deserialization of simulation events.
	 * @author Timo
	 */
	private static class SimulationEventAdapter implements JsonDeserializer<AbstractEvent>, JsonSerializer<AbstractEvent> {

		private Gson gson;
		
		/**
		 * Default
		 */
		private SimulationEventAdapter() {
			this.gson = new Gson();
		}
		
		@Override
		public AbstractEvent deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			AbstractEvent simulationEvent = this.gson.fromJson(json, AbstractEvent.class);			
			return (AbstractEvent) this.gson.fromJson(json, simulationEvent.getType().getImplementationClass());
		}

		@Override
		public JsonElement serialize(AbstractEvent src, Type typeOfSrc,
				JsonSerializationContext context) {
			return this.gson.toJsonTree(src, src.getType().getImplementationClass());
		}
	}
	
	/**
	 * Handles the deserialization of sensor helpers.
	 * @author Timo
	 */
	private static class SensorHelperTypeDeserializer implements JsonDeserializer<SensorHelper>, JsonSerializer<SensorHelper> {
		private Gson gson;

		/**
		 * Default
		 */
		private SensorHelperTypeDeserializer() {
			this.gson = new Gson();
		}

		@Override
		public SensorHelper deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {

			SensorHelper sensorHelper = this.gson.fromJson(json, SensorHelper.class);
			return (SensorHelper) this.gson.fromJson(json, sensorHelper.getSensorType().getSensorTypeClass());
		}

		@Override
		public JsonElement serialize(SensorHelper src, Type typeOfSrc,
				JsonSerializationContext context) {
			return this.gson.toJsonTree(src, src.getSensorType().getSensorTypeClass());
		}
	}	
	
	/**
	 * Handles every SensorInterferer subclasses by adding extra information into the JSON strings.
	 * @author tlottmann
	 */
	@SuppressWarnings("unused")
	private class GenericTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T>{

		private static final String CLASSNAME = "CLASSNAME";
		private static final String INSTANCE  = "INSTANCE";

		@Override
		public T deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject =  json.getAsJsonObject();
			JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
			String className = prim.getAsString();
			Class<?> klass = null;
			try {
				klass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				LOGGER.error("see nested exception", e);
				throw new JsonParseException(e.getMessage());
			}

			return context.deserialize(jsonObject.get(INSTANCE), klass);
		}

		@Override
		public JsonElement serialize(T src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject retValue = new JsonObject();
			String className = src.getClass().getCanonicalName();
			retValue.addProperty(CLASSNAME, className);
			JsonElement elem = context.serialize(src); 
			retValue.add(INSTANCE, elem);
			return retValue;
		}
	}
}
