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
 
package de.pgalise.simulation.controlCenter.internal.util.service;

import java.io.InputStream;

import com.google.gson.Gson;

import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;

/**
 * Interface for all start parameter serializer services.
 * @author Timo
 */
public interface StartParameterSerializerService {
	/**
	 * Deserialize the ControlCenterStartParameter.
	 * @param content
	 * 			e.g. a XML string.
	 * @return
	 */
	public ControlCenterStartParameter deserialize(String content);
	
	/**
	 * Deserialize the ControlCenterStartParameter.
	 * @param inputStream
	 * @return
	 */
	public ControlCenterStartParameter deserialize(InputStream inputStream);
	
	/**
	 * Serializes the ControlCenterStartParameter into a file.
	 * @param cCSimulationStartParameter
	 * @param fileName
	 * @return
	 * 		the file name.
	 */
	public String serialize(ControlCenterStartParameter cCSimulationStartParameter, String fileName);
	
	/**
	 * Inits the start parameter serializer service.
	 * @param gson
	 */
	public void init(Gson gson);
}
