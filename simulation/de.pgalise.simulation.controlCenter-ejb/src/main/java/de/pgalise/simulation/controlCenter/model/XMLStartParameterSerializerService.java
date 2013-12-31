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

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;

/**
 * A XML control center startParameter serializer service.
 * 
 * @author Timo
 */
public class XMLStartParameterSerializerService implements StartParameterSerializerService {
	private static final Logger log = LoggerFactory.getLogger(XMLStartParameterSerializerService.class);
	private static String filePath;
	
	static {
		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			filePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath().substring(1).replaceAll("WEB-INF/classes", "");
		} else {
			filePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath().replaceAll("WEB-INF/classes", "");
		}
	}
	
	private Gson gson;
	private XMLSerializer xmlSerializer;

	/**
	 * Default
	 */
	public XMLStartParameterSerializerService() {
		this.xmlSerializer = new XMLSerializer();
	}

	@Override
	public ControlCenterStartParameter deserialize(InputStream inputStream) {
		if (this.gson == null) {
			throw new IllegalStateException("Gson is null. Service must be initialized!");
		}
		return this.gson.fromJson(this.xmlSerializer.readFromStream(inputStream).toString(),
				ControlCenterStartParameter.class);
	}

	@Override
	public String serialize(ControlCenterStartParameter cCSimulationStartParameter, String fileName) {
		if (this.gson == null) {
			throw new IllegalStateException("Gson is null. Service must be initialized!");
		}
		String filePath = XMLStartParameterSerializerService.filePath + fileName;
		PrintWriter pw = null;

		try {
			if (new File(filePath).exists()) {
				throw new RuntimeException("file name: " + fileName + " already in use!");
			}
			pw = new PrintWriter(filePath, "UTF-8");
			pw.append(xmlSerializer.write(JSONSerializer.toJSON(this.gson.toJson(cCSimulationStartParameter))));
		} catch (Exception e) {
			log.error("Exception", e);
			throw new RuntimeException(e);
		} finally {
			try {
				pw.flush();
			} catch (Exception e) {
				log.error("Exception", e);
			}
			try {
				pw.close();
			} catch (Exception e) {
				log.error("Exception", e);
			}
		}

		return filePath.replaceAll(XMLStartParameterSerializerService.filePath, "");
	}

	@Override
	public ControlCenterStartParameter deserialize(String content) {
		return this.gson.fromJson(this.xmlSerializer.read(content).toString(), ControlCenterStartParameter.class);
	}

	@Override
	public void init(Gson gson) {
		this.gson = gson;
	}
}
